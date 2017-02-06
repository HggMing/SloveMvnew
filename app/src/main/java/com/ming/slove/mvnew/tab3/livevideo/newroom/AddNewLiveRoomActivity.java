package com.ming.slove.mvnew.tab3.livevideo.newroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintEditText;
import com.bumptech.glide.Glide;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.api.video.VideoApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.MyPictureSelector;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.model.bean.NewRoomInfo;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.tab3.livevideo.newroom.streamutil.Config;
import com.ming.slove.mvnew.tab3.livevideo.newroom.streamutil.SWCodecCameraStreamingActivity;
import com.ming.slove.mvnew.tab3.newpost.NewPostActivity;
import com.orhanobut.hawk.Hawk;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.util.PictureConfig;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddNewLiveRoomActivity extends BackActivity {
    @Bind(R.id.et_title)
    TintEditText etTitle;
    @Bind(R.id.img_picture)
    ImageView imgPicture;
    @Bind(R.id.img_add)
    ImageView imgAdd;

    private String auth;

    private RequestBody imgPictureBody;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_live_room);
        ButterKnife.bind(this);

        setToolbarTitle(R.string.title_activity_add_new_live_room);
        auth = Hawk.get(APPS.USER_AUTH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 将菜单图标添加到toolbar
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_submit) {
            String title = etTitle.getEditableText().toString();
            if (StringUtils.isEmpty(title)) {
                Toast.makeText(this, "请输入直播标题！", Toast.LENGTH_SHORT).show();
                return true;
            }
            item.setEnabled(false);

            dialog = new ProgressDialog(this);
            dialog.setMessage("直播准备中，请稍后...");
            dialog.setCancelable(false);
            dialog.show();

            RequestBody authBody = RequestBody.create(MediaType.parse("text/plain"), auth);
            RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);

            VideoApi.getService()
                    .post_AddRoom(authBody,titleBody,imgPictureBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<NewRoomInfo>() {
                        @Override
                        public void onCompleted() {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            item.setEnabled(true);
                        }

                        @Override
                        public void onNext(NewRoomInfo newRoomInfo) {
                            if (newRoomInfo.getErr() == 0) {
                                //添加房间成功后转预览推流页面，并且关闭该页面
                                String pubUrl=Config.EXTRA_PUBLISH_URL_PREFIX+newRoomInfo.getData().getUrl_1();//推流地址
                                Intent intent = new Intent(AddNewLiveRoomActivity.this, SWCodecCameraStreamingActivity.class);
                                intent.putExtra(Config.EXTRA_KEY_PUB_URL, pubUrl);
                                intent.putExtra(Config.EXTRA_KEY_ROOM_ID, newRoomInfo.getData().getRoom_id());
                                startActivity(intent);
                                finish();
                            } else {
                                item.setEnabled(true);
                                Toast.makeText(AddNewLiveRoomActivity.this, "添加失败，请检查输入信息是否正确。", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.layout_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_add://封面图片
                MyPictureSelector pictureSelector = new MyPictureSelector(this);
                pictureSelector.selectorSinglePicture();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_IMAGE:
                    List<LocalMedia> mediaList = (List<LocalMedia>) data.getSerializableExtra(PictureConfig.REQUEST_OUTPUT);
                    if (mediaList != null) {
                        String imagPath = mediaList.get(0).getCompressPath();
                        imgAdd.setVisibility(View.GONE);
                        Glide.with(this)
                                .load(imagPath)
                                .placeholder(R.drawable.default_nine_picture)
                                .into(imgPicture);
                        File file = new File(imagPath);
                        imgPictureBody = RequestBody.create(MediaType.parse("image/*"), file);
                    }
                    break;
            }
        }
    }
}
