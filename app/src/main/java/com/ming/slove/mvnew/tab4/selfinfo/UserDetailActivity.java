package com.ming.slove.mvnew.tab4.selfinfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.common.utils.MyPictureSelector;
import com.ming.slove.mvnew.common.widgets.dialog.Dialog_UpdateSex;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.UserInfo;
import com.orhanobut.hawk.Hawk;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.util.PictureConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserDetailActivity extends BackActivity {

    public static final String USER_INFO = "userInfo";
    public static final String NEW_ADDRESS = "newAddress";
    public static final String NEW_NAME = "newName";
    public static final String NEW_IDCARD = "newIdcard";
    @Bind(R.id.icon_head2)
    ImageView iconHead2;
    @Bind(R.id.get_name)
    TextView getName;
    @Bind(R.id.get_sex)
    TextView getSex;
    @Bind(R.id.get_id_card)
    TextView getIdCard;
    @Bind(R.id.get_address)
    TextView getAddress;
    @Bind(R.id.get_phone)
    TextView getPhone;
    @Bind(R.id.get_login_time)
    TextView getLoginTime;
    UserInfo.DataEntity userInfo;

    private String auth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_user_detail);
        auth = Hawk.get(APPS.USER_AUTH);

        initView();
    }

    private void initView() {
        userInfo = getIntent().getParcelableExtra(USER_INFO);
        //头像
        String headUrl = APPS.BASE_URL + userInfo.getHead();
        Glide.with(this)
                .load(headUrl)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(iconHead2);
        //姓名
        getName.setText(userInfo.getUname());
        //性别
        String sex = userInfo.getSex();
        if ("0".equals(sex)) {
            getSex.setText("男");
        } else if ("1".equals(sex)) {
            getSex.setText("女");
        } else {
            getSex.setText("未知");
        }
        //身份证号
        getIdCard.setText(userInfo.getCid());
        //地址:"province_name":"四川省", "city_name":"遂宁市", "county_name":"蓬溪县", "town_name":"红江镇", "village_name":"永益村"
        String address = (userInfo.getProvince_name() +
                userInfo.getCity_name() +
                userInfo.getCounty_name() +
                userInfo.getTown_name() +
                userInfo.getVillage_name());
        getAddress.setText(address);
        //手机号
        getPhone.setText(userInfo.getPhone());
        // 最后登录时间
        String date = userInfo.getLastlog();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(new Date(Long.valueOf(date + "000")));
        getLoginTime.setText(time);
    }

    @OnClick({R.id.set_head, R.id.set_name, R.id.set_sex, R.id.set_id_card, R.id.set_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_head:
                updateHead();
                break;
            case R.id.set_name:
                Intent intent1 = new Intent(this, UpdateUnameActivity.class);
                intent1.putExtra(UpdateUnameActivity.OLD_NAME, userInfo.getUname());
                startActivityForResult(intent1, 11);
                break;
            case R.id.set_sex:
                updateSex();
                break;
            case R.id.set_id_card:
                Intent intent2 = new Intent(this, UpdateIdcardActivity.class);
                intent2.putExtra(UpdateIdcardActivity.OLD_CID, userInfo.getCid());
                startActivityForResult(intent2, 22);
                break;
            case R.id.set_address:
                Intent intent3 = new Intent(this, UpdateAdressActivity.class);
                startActivityForResult(intent3, 33);
                break;
        }
    }

    /**
     * 修改头像
     */
    private void updateHead() {
        MyPictureSelector pictureSelector = new MyPictureSelector(this);
        pictureSelector.selectorSquarePicture();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_IMAGE://拍照或选择图片后，更新头像
                    List<LocalMedia> mediaList = (List<LocalMedia>) data.getSerializableExtra(PictureConfig.REQUEST_OUTPUT);
                    if (mediaList != null) {
                        String imagPath = mediaList.get(0).getCompressPath();
                        //刷新头像显示
                        Glide.with(this)
                                .load(imagPath)
                                .bitmapTransform(new CropCircleTransformation(this))
                                .into(iconHead2);

                        Bitmap bitmap = BitmapFactory.decodeFile(imagPath);//图片文件转为Bitmap对象
                        final String newHead = BaseTools.bitmapToBase64(bitmap) + ".jpg";
                        OtherApi.getService()
                                .post_UpdateHead(auth, newHead)
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
                                        if (result != null) {
                                            Toast.makeText(UserDetailActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                                            if (result.getErr() == 0) {
                                                //上传头像成功
                                                Hawk.put(APPS.IS_UPDATA_MY_INFO, false);
                                            }
                                        }
                                    }
                                });
                    }
                    break;
                case 11:
                    String result = data.getStringExtra(NEW_NAME);
                    getName.setText(result);
                    Hawk.put(APPS.IS_UPDATA_MY_INFO, false);
                    break;
                case 22:
                    String result2 = data.getStringExtra(NEW_IDCARD);
                    getIdCard.setText(result2);
                    Hawk.put(APPS.IS_UPDATA_MY_INFO, false);
                    break;
                case 33:
                    String result3 = data.getStringExtra(NEW_ADDRESS);
                    getAddress.setText(result3);
                    Hawk.put(APPS.IS_UPDATA_MY_INFO, false);
                    break;
            }
        }
    }

    /**
     * 修改性别
     */
    private void updateSex() {
        final Dialog_UpdateSex.Builder sexDialog = new Dialog_UpdateSex.Builder(UserDetailActivity.this);
        userInfo = getIntent().getParcelableExtra(USER_INFO);
        final Dialog_UpdateSex dialog = sexDialog.setTitle("修改性别")
                .setMysex(userInfo.getSex())//显示当前性别
                .create();
        dialog.show();

        sexDialog.maleAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sexDialog.male.isChecked()) {
                    sexDialog.male.setChecked(true);
                    sexDialog.female.setChecked(false);
                    userInfo.setSex("0");
                    getSex.setText("男"); //修改本页性别显示
                    updateSexToServer("0", dialog);
                } else {
                    dialog.dismiss();
                }
            }
        });

        sexDialog.femaleAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sexDialog.female.isChecked()) {
                    sexDialog.female.setChecked(true);
                    sexDialog.male.setChecked(false);
                    userInfo.setSex("1");
                    getSex.setText("女"); //修改本页性别显示
                    updateSexToServer("1", dialog);
                } else {
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 更新性别到服务器
     *
     * @param sexNo 男：“0”，女：“1”
     */
    private void updateSexToServer(String sexNo, final Dialog_UpdateSex dialog) {
        OtherApi.getService().postCall_UpdateInfo(auth, null, sexNo, null, null)
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        if (response.isSuccessful()) {
                            Result result = response.body();
                            if (result != null) {
                                Hawk.put(APPS.IS_UPDATA_MY_INFO, false);
                                Toast.makeText(UserDetailActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                    }
                });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
