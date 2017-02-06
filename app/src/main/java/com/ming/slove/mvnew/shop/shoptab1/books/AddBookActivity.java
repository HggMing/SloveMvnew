package com.ming.slove.mvnew.shop.shoptab1.books;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintEditText;
import com.bumptech.glide.Glide;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.MyPictureSelector;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.common.widgets.scanner.MyScannerActivity;
import com.ming.slove.mvnew.model.bean.Result;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
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

public class AddBookActivity extends BackActivity {

    @Bind(R.id.et_num)
    TintEditText etNum;
    @Bind(R.id.et_name)
    TintEditText etName;
    @Bind(R.id.img_book)
    ImageView imgBook;
    @Bind(R.id.img_add)
    ImageView imgAdd;

    private final int REQUEST_CODE = 55423;
    private String auth;
    private RequestBody imgBookBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_add_book);

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

            String number = etNum.getEditableText().toString();
            String name = etName.getEditableText().toString();

            if (StringUtils.isEmpty(number)) {
                Toast.makeText(this, "图书书号不能为空，可扫码快速添加！", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (StringUtils.isEmpty(name)) {
                Toast.makeText(this, "请输入图书书名！", Toast.LENGTH_SHORT).show();
                return true;
            }

            String vid = Hawk.get(APPS.MANAGER_VID);
            RequestBody authBody = RequestBody.create(MediaType.parse("text/plain"), auth);
            RequestBody vidBody = RequestBody.create(MediaType.parse("text/plain"), vid);
            RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody numberBody = RequestBody.create(MediaType.parse("text/plain"), number);

            OtherApi.getService()
                    .post_AddBook(authBody, vidBody, nameBody, numberBody, imgBookBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Result>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.d(e.getMessage());
                        }

                        @Override
                        public void onNext(Result result) {
                            if (result.getErr() == 0) {
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(AddBookActivity.this, "添加失败，请检查输入信息是否正确。", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.scan, R.id.add_book})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan://扫描书号
                Intent intent = new Intent(this, MyScannerActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.add_book://图书封面图片
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
                case REQUEST_CODE:
                    String numberScan = data.getStringExtra(MyScannerActivity.SCAN_RESULT);
                    etNum.setText(numberScan);
                    break;
                case PictureConfig.REQUEST_IMAGE:
                    List<LocalMedia> mediaList = (List<LocalMedia>) data.getSerializableExtra(PictureConfig.REQUEST_OUTPUT);
                    if (mediaList != null) {
                        String imagPath = mediaList.get(0).getCompressPath();
                        imgAdd.setVisibility(View.GONE);
                        Glide.with(this)
                                .load(imagPath)
                                .placeholder(R.drawable.default_nine_picture)
                                .into(imgBook);
                        File file = new File(imagPath);
                        imgBookBody = RequestBody.create(MediaType.parse("image/*"), file);

                        //对图片压缩处理
                       /* File file = null;
                        try {
                            file = new PhotoOperate().scal(photoInfo.getPath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (file != null) {
                            imgBookBody = RequestBody.create(MediaType.parse("image*//*"), file);
                        }*/
                    }
                    break;
            }
        }
    }
}
