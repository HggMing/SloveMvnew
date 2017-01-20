package com.ming.slove.mvnew.shop.shoptab3;

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
import com.ming.slove.mvnew.api.MyServiceClient;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.MyPictureSelector;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.model.bean.Result;
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

public class AddVillageInfoActivity extends BackActivity {
    @Bind(R.id.et_title)
    TintEditText etTitle;
    @Bind(R.id.et_content)
    TintEditText etContent;
    @Bind(R.id.img_picture)
    ImageView imgPicture;
    @Bind(R.id.img_add)
    ImageView imgAdd;
    @Bind(R.id.layout_add)
    FrameLayout layoutAdd;

    private String auth;
    public static String TYPE = "the_title_name";
    private int type;//1、荣誉室2、活动3、村委（Item不同，单独写）4、美食

    private RequestBody imgPictureBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_village_info);
        ButterKnife.bind(this);

        type = getIntent().getIntExtra(TYPE, 0);
        switch (type) {
            case 1:
                setToolbarTitle("新增荣誉");
                break;
            case 2:
                setToolbarTitle("新增活动");
                break;
            case 4:
                setToolbarTitle("新增美食");
                break;
        }

        auth = Hawk.get(APPS.USER_AUTH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 将菜单图标添加到toolbar
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_submit) {

            String title = etTitle.getEditableText().toString();
            String content = etContent.getEditableText().toString();

            if (StringUtils.isEmpty(title)) {
                Toast.makeText(this, "标题不能为空！", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (StringUtils.isEmpty(content)) {
                Toast.makeText(this, "请输入详细内容！", Toast.LENGTH_SHORT).show();
                return true;
            }
            String typeS = String.valueOf(type);
            RequestBody authBody = RequestBody.create(MediaType.parse("text/plain"), auth);
            RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), content);
            RequestBody typeBody = RequestBody.create(MediaType.parse("text/plain"), typeS);

            MyServiceClient.getService()
                    .post_AddVillageInfo(authBody, titleBody, contentBody, typeBody, imgPictureBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Result>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Result result) {
                            if (result.getErr() == 0) {
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(AddVillageInfoActivity.this, "添加失败，请检查输入信息是否正确。", Toast.LENGTH_SHORT).show();
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
