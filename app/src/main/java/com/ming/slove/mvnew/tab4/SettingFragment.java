package com.ming.slove.mvnew.tab4;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.app.ThemeHelper;
import com.ming.slove.mvnew.common.base.LazyLoadFragment;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.common.widgets.CustomItem;
import com.ming.slove.mvnew.common.widgets.dialog.CardPickerDialog;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.model.bean.ApplyInfo;
import com.ming.slove.mvnew.model.bean.CheckPhone;
import com.ming.slove.mvnew.model.bean.UserInfo;
import com.ming.slove.mvnew.model.database.MyDB;
import com.ming.slove.mvnew.model.event.ChangeThemeColorEvent;
import com.ming.slove.mvnew.model.event.ShopApplyPassEvent;
import com.ming.slove.mvnew.model.event.UpdataShopOwnerHeadEvent;
import com.ming.slove.mvnew.tab4.applyshoper.ApplyShopOwnerActivity;
import com.ming.slove.mvnew.tab4.applyshoper.MyShopActivity;
import com.ming.slove.mvnew.tab4.applyshoper.ShowApplyingActivity;
import com.ming.slove.mvnew.tab4.mysetting.MySettingActivity;
import com.ming.slove.mvnew.tab4.safesetting.RealNameBindingActivity;
import com.ming.slove.mvnew.tab4.safesetting.SafeSettingActivity;
import com.ming.slove.mvnew.tab4.scommon.SettingCommonActivity;
import com.ming.slove.mvnew.tab4.selfinfo.UserDetailActivity;
import com.ming.slove.mvnew.ui.login.LoginActivity;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SettingFragment extends LazyLoadFragment implements CardPickerDialog.ClickListener {

    @Bind(R.id.icon_head)
    ImageView iconHead;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.sex)
    ImageView sex;
    @Bind(R.id.store_manager)
    ImageView storeManager;
    @Bind(R.id.account_number)
    TextView accountNumber;
    @Bind(R.id.click_store_manager)
    CustomItem clickShop;


    private boolean isUpdataMyInfo;//是否更新完个人信息
    private UserInfo.DataEntity dataEntity;

    private String auth;
    private int isShopOwner;//是否是店长,1是0不是
    private final int REQUEST_USER_INFO = 122;
    private final int REQUEST_APPLY_PASSED = 123;

    @Override
    public int getLayout() {
        return R.layout.fragment_tab4;
    }

    @Override
    public void initViews(View view) {
        auth = Hawk.get(APPS.USER_AUTH);

        isShopOwner = Hawk.get(APPS.IS_SHOP_OWNER);

        setHasOptionsMenu(true);

        initView();//界面初始化
    }

    @Override
    public void loadData() {
        getUserInfoDetail();//在线获取用户信息
    }

    private void initView() {
        //是否为店长显示
        if (isShopOwner == 1) {
            clickShop.setText("我的店");
            clickShop.setIcon(R.mipmap.tab4_mystore);
            storeManager.setVisibility(View.VISIBLE);//店长图标
        } else {
            clickShop.setText("申请店长");
            clickShop.setIcon(R.mipmap.tab4_store_manager);
            storeManager.setVisibility(View.GONE);
        }
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_main, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_theme) {
            //更换主题
            CardPickerDialog dialog = new CardPickerDialog();
            dialog.setClickListener(this);
            dialog.show(getActivity().getSupportFragmentManager(), CardPickerDialog.TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_USER_INFO:
                if (resultCode == Activity.RESULT_OK) {
                    //修改数据后在线更新个人信息
                    isUpdataMyInfo = Hawk.get(APPS.IS_UPDATA_MY_INFO, false);
                    if (!isUpdataMyInfo) {
                        getUserInfoDetail();
                    }
                }
                break;
            case REQUEST_APPLY_PASSED:
                if (resultCode == Activity.RESULT_OK) {
                    isShopOwner = 1;
                    clickShop.setText("我的店");
                    clickShop.setIcon(R.mipmap.tab4_mystore);
                    storeManager.setVisibility(View.VISIBLE);//店长图标
                    //申请店长成功后，主页tab由4变5。一些店长相关细节处理
                    EventBus.getDefault().post(new ShopApplyPassEvent());
                }
        }
    }

    /**
     * 点击退出登录
     */
    private void logout() {
        MyDialog.Builder builder = new MyDialog.Builder(getContext());
        builder.setTitle("提示")
                .setMessage("确定退出登录？")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Hawk.put(APPS.KEY_LOGIN_PASSWORD, "");
                                Hawk.put(APPS.IS_UPDATA_MY_INFO, false);
                                Hawk.delete(APPS.ME_UID);
                                Hawk.delete(APPS.SELECTED_CARD);
                                Hawk.delete(APPS.MANAGER_ADDRESS);
                                Hawk.delete(APPS.MANAGER_VID);
                                //停止个推SDK服务
                                PushManager.getInstance().stopService(getContext());
                                //关闭数据库
                                MyDB.createDb(getContext()).close();
                                MyDB.setLiteOrm(null);

                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if (!getActivity().isFinishing()) {
            builder.create().show();
        }
    }

    /**
     * 获取用户信息
     */
    public void getUserInfoDetail() {
        OtherApi.getService().get_UserInfo(auth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(UserInfo userInfo) {
                        if (userInfo != null && userInfo.getErr() == 0) {
                            dataEntity = userInfo.getData();
                            String headUrl = APPS.BASE_URL + dataEntity.getHead();
                            Hawk.put(APPS.ME_HEAD, headUrl);

                            //如果是店长，更新首页，店长头像。解决无APP.ME_HEAD时，头像显示问题
                            if (isShopOwner == 1) {
                                EventBus.getDefault().post(new UpdataShopOwnerHeadEvent(headUrl));
                            }

                            String uName = dataEntity.getUname();
                            Hawk.put(APPS.ME_NAME,uName);
                            String sexNumber = dataEntity.getSex();
                            String accountNo = dataEntity.getLogname();
                            //头像
                            Glide.with(getContext())
                                    .load(headUrl)
                                    .bitmapTransform(new CropCircleTransformation(getContext()))
                                    .error(R.mipmap.defalt_user_circle)
                                    .into(iconHead);
                            //昵称
                            if (StringUtils.isEmpty(uName)) {
                                String iphone = dataEntity.getPhone();
                                String showName = iphone.substring(0, 3) + "****" + iphone.substring(7, 11);
                                name.setText(showName);
                            } else {
                                name.setText(uName);
                            }

                            //性别
                            if ("0".equals(sexNumber)) {
                                sex.setImageDrawable(getResources().getDrawable(R.mipmap.ic_sex_boy));
                            } else {
                                sex.setImageDrawable(getResources().getDrawable(R.mipmap.ic_sex_girl));
                            }
                            accountNumber.setText("账号：" + accountNo);

                            Hawk.put(APPS.IS_UPDATA_MY_INFO, true);
                        }
                    }
                });
    }

    @OnClick({R.id.test, R.id.click_user, R.id.click_safe_center, R.id.click_my_setting, R.id.click_setting_common, R.id.click_store_manager, R.id.click_loyout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test://测试按钮:

                break;
            case R.id.click_user://点击编辑用户
                if (dataEntity != null) {
                    Intent intent1 = new Intent(getContext(), UserDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(UserDetailActivity.USER_INFO, dataEntity);
                    intent1.putExtras(bundle);
                    startActivityForResult(intent1, REQUEST_USER_INFO);
                }else{
                    getUserInfoDetail();
                }
                break;
            case R.id.click_my_setting://我的
                Intent intent2 = new Intent(getContext(), MySettingActivity.class);
                startActivity(intent2);
                break;

            case R.id.click_store_manager:
                if (isShopOwner == 1) {//进入店长管理页面
                    Intent intent3 = new Intent(getContext(), MyShopActivity.class);
                    startActivity(intent3);
                } else {
                    //获取申请店长，当前状态
                    getApplyStatus();
                }
                break;
            case R.id.click_safe_center://账号安全
                Intent intent4 = new Intent(getContext(), SafeSettingActivity.class);
                startActivity(intent4);
                break;
            case R.id.click_setting_common://通用
                Intent intent5 = new Intent(getContext(), SettingCommonActivity.class);
                startActivity(intent5);
                break;
            case R.id.click_loyout://退出当前账号
                logout();
                break;
        }
    }

    private void getIsBinding() {
        //1)将除图片外的参数以及机构key组成一个字符串(注意顺序)
        String phone = Hawk.get(APPS.KEY_LOGIN_NAME);
        String other = "compid=9&phone=" + phone;
        String str = other + "&key=69939442285489888751746749876227";
        //2)使用MD5算法加密上述字符串
        String sign = BaseTools.md5(str);
        //3)最终得到参数字符串：（注意，KEY参数不带到参数列表,sign参数加入参数列表）
        String str2 = other + "&sign=" + sign;
        //4)把上述字符串做base64加密，最终得到请求:
        String paraString = Base64.encodeToString(str2.getBytes(), Base64.NO_WRAP);
        RequestBody data = RequestBody.create(MediaType.parse("text/plain"), paraString);

        OtherApi.getService()
                .post_IsRealBinding(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        String s = null;
                        try {
                            s = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        CheckPhone result = gson.fromJson(new String(Base64.decode(s, Base64.DEFAULT)), CheckPhone.class);
                        if (result.getErr() == 0) {//已经实名认证,进入申请界面
                            Intent intent4 = new Intent(getContext(), ApplyShopOwnerActivity.class);
                            intent4.putExtra(ApplyShopOwnerActivity.USER_INFO, dataEntity);
                            startActivity(intent4);
                        }
                        if (result.getErr() == 1002) {//还没有实名认证
                            MyDialog.Builder builder1 = new MyDialog.Builder(getContext());
                            builder1.setTitle("提示")
                                    .setMessage("你的账号尚未实名认证，请先进行实名认证。")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            Intent intent5 = new Intent(getContext(), RealNameBindingActivity.class);
                                            startActivity(intent5);
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            if (!getActivity().isFinishing()) {
                                builder1.create().show();
                            }
                        }
                    }
                });
    }

    private void getApplyStatus() {
        String vid = Hawk.get(APPS.APPLY_INFO_VID + dataEntity.getUid());
        if (StringUtils.isEmpty(vid)) {//假如没有申请过
            getIsBinding();//获取是否实名认证
        } else {
            OtherApi.getService()
                    .get_IsApply(auth, vid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ApplyInfo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(ApplyInfo applyInfo) {
                            ApplyInfo.DataBean data = applyInfo.getData();
                            if (data != null) {//已申请过，进入查看申请状态页面
                                Intent intent = new Intent(getContext(), ShowApplyingActivity.class);
                                intent.putExtra(ShowApplyingActivity.STATUS_APPLY, data.getStats());
                                startActivityForResult(intent, REQUEST_APPLY_PASSED);
                            } else {//已经选择申请的村（已实名通过），但没有申请就退出。点击申请后直接进入申请界面
                                Intent intent4 = new Intent(getContext(), ApplyShopOwnerActivity.class);
                                intent4.putExtra(ApplyShopOwnerActivity.USER_INFO, dataEntity);
                                startActivity(intent4);
                            }
                        }
                    });
        }
    }

    @Override
    public void onConfirm(int currentTheme) {
        if (ThemeHelper.getTheme(getContext()) != currentTheme) {
            ThemeHelper.setTheme(getContext(), currentTheme);
            ThemeUtils.refreshUI(getContext(), new ThemeUtils.ExtraRefreshable() {
                @Override
                public void refreshGlobal(Activity activity) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        ActivityManager.TaskDescription taskDescription = new ActivityManager
                                .TaskDescription(null, null, ThemeUtils.getThemeAttrColor(getContext(), android.R.attr.colorPrimary));
                        getActivity().setTaskDescription(taskDescription);
                        getActivity().getWindow().setStatusBarColor(ThemeUtils.getColorById(getContext(), R.color.theme_color_primary));
                    }
                }

                @Override
                public void refreshSpecificView(View view) {
                }
            });
            //通知MainActivity更换主题
            EventBus.getDefault().post(new ChangeThemeColorEvent());
        }
    }
}
