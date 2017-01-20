package com.ming.slove.mvnew.ui.login;

import android.support.annotation.NonNull;

import com.ming.slove.mvnew.api.MyService;
import com.ming.slove.mvnew.api.MyServiceClient;
import com.ming.slove.mvnew.api.login.LoginApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.model.bean.Login;
import com.orhanobut.hawk.Hawk;

import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

import static com.ming.slove.mvnew.common.utils.BaseTools.checkNotNull;


/**
 * Presenter
 */
public class LoginPresenter implements LoginContract.Presenter {

    private boolean isTest;//是否为测试服务器

    private String loginName;
    private String loginPwd;
    private boolean isRememberPwd;// 是否记住密码

    private String point;

    @NonNull
    private final LoginContract.View mView;
    @NonNull
    private final LoginContract.Model mModel;
    @NonNull
    private CompositeSubscription mSubscriptions;

    public LoginPresenter(@NonNull LoginContract.View loginView) {
        mView = checkNotNull(loginView);

        mModel = new LoginModel();

        mSubscriptions = new CompositeSubscription();
        mView.setPresenter(this);

        isRememberPwd = Hawk.get(APPS.IS_REMEMBER_PASSWORD, true);//初始默认记住密码
        loginName = Hawk.get(APPS.KEY_LOGIN_NAME, "");
        loginPwd = Hawk.get(APPS.KEY_LOGIN_PASSWORD, "");
        isTest = Hawk.get(APPS.KEY_IS_TEST, true);//默认为测试
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void showView() {
        if (isRememberPwd) {
            mView.showRemember(loginName, loginPwd);
        } else {
            mView.showNotRemember(loginName);
        }
        mView.showIsTest(isTest);
    }

    @Override
    public void setTest() {
        String baseUrl_Test = "http://product1.yibanke.com/";
        MyServiceClient.setIsApiChange(baseUrl_Test);//修改api后，重建retrofit实例
        mModel.saveTest(true, baseUrl_Test);
    }

    @Override
    public void setNotTest() {
        String baseUrl = "http://product.yibanke.com/";
        MyServiceClient.setIsApiChange(baseUrl);//修改api后，重建retrofit实例
        mModel.saveTest(false, baseUrl);
    }

    @Override
    public void onClickLogin(String loginName, String loginPwd) {
        mView.showLoginning();

        if (loginName.equals("")) {
            mView.showCanLogin();
            mView.toast("登录名不能为空");
            return;
        }
        if (!BaseTools.checkPhone(loginName)) {
            mView.showCanLogin();
            mView.toast("登录名不是标准的手机号码格式");
            return;
        }
        if (loginPwd.equals("")) {
            mView.showCanLogin();
            mView.toast("登录密码不能为空");
            return;
        }
        loginByRx(loginName, loginPwd);
    }

    @Override
    public void onClickJzmm() {
        if (isRememberPwd) {
            isRememberPwd = false;
            mModel.saveIsRememberPwd(false);
            mView.showNotRemember(null);
        } else {
            isRememberPwd = true;
            mModel.saveIsRememberPwd(true);
            mView.showRemember(null, null);
        }
    }

    private void loginByRx(final String loginName, final String loginPwd) {
        LoginApi.login(loginName, loginPwd)
                .subscribe(new Subscriber<Login>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        point = "亲，网络不给力啊,请检查网络";
                        mView.loginFailure(point);
                    }

                    @Override
                    public void onNext(Login login) {
                        if (login.getErr() == 0) {
                            //储存店长管理村地址
                            if (login.getShopowner().getIs_shopowner() == 1) {
                                mModel.saveShopAddress(login);
                            }
                            //储存登录用户信息
                            mModel.saveUserInfo(login.getAuth(),
                                    login.getInfo().getUid(),
                                    login.getShopowner().getIs_shopowner(),
                                    login.getIs_show_yingshan());

                            if (isRememberPwd) {
                                mModel.saveLoginInfo(loginName, loginPwd);
                            } else {
                                mModel.saveLoginInfo("", "");
                            }
                            mView.loginSuccess();
                            return;
                        }
                        if (login.getErr() == 2003) {
                            point = login.getMsg();
                            mView.loginFailureToReg(point, loginName);
                            return;
                        }
                        point = login.getMsg();
                        mView.loginFailure(point);
                    }
                });
    }
}
