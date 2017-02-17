package com.ming.slove.mvnew.ui.facelogin;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.google.gson.Gson;
import com.ming.slove.mvnew.api.login.LoginApi;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.model.bean.CheckPhone;
import com.ming.slove.mvnew.model.bean.Login;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

import static com.ming.slove.mvnew.common.utils.BaseTools.checkNotNull;


/**
 * Presenter
 */
public class FaceLoginPresenter implements FaceLoginContract.Presenter {
    @NonNull
    private final FaceLoginContract.View mView;
    @NonNull
    private final FaceLoginContract.Model mModel;
    @NonNull
    private CompositeSubscription mSubscriptions;

    private boolean hasFacePhoto = false;

    private String tPhone;


    public FaceLoginPresenter(@NonNull FaceLoginContract.View faceloginView) {
        mView = checkNotNull(faceloginView);

        mModel = new FaceLoginModel();

        mSubscriptions = new CompositeSubscription();
        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void setHasFacePhoto(boolean b) {
        this.hasFacePhoto = b;
    }

    @Override
    public void userLogin(String phone, String photoPath) {
        tPhone = phone;
        if (StringUtils.isEmpty(phone)) {
            mView.toast2("请输入认证的手机号码");
            return;
        }
        if (!hasFacePhoto) {
            mView.setBtnOk(true, "确定");
            mView.toast2("请拍摄正面免冠照片");
            return;
        }
        mView.setBtnOk(false, "认证中,请稍等");
        getFaceLoginSign(phone, photoPath);
    }

    private void getFaceLoginSign(String phone, String photoPath) {
        Observable<ResponseBody> observable = LoginApi.getFaceLoginSign(phone, photoPath);
        if (observable != null) {
            observable.subscribe(new Subscriber<ResponseBody>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    mView.toast2("验证超时，建议重新拍摄清晰正面免冠照片，再次尝试登陆。");
                    mView.setBtnOk(true, "确定");
                }

                @Override
                public void onNext(ResponseBody responseBody) {
                    String s = null;
                    try {
                        s = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //数据通过Base64解码后，转换为bean
                    Gson gson = new Gson();
                    CheckPhone result = gson.fromJson(new String(Base64.decode(s, Base64.DEFAULT)), CheckPhone.class);
                    if (result.getErr() == 0) {
                        faceLogin(result.getSign());
                    } else {
                        mView.toast2(result.getErr() + "：" + result.getMsg());
                        mView.setBtnOk(true, "确定");
                    }
                }
            });
        }
    }

    private void faceLogin(String sign) {
        mView.toast2("人脸认证成功！");
        LoginApi.faceLogin(sign)
                .subscribe(new Subscriber<Login>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Login login) {
                        //储存店长管理村地址
                        if (login.getShopowner().getIs_shopowner() == 1) {
                            mModel.saveShopAddress(login);
                        }

                        //储存登录用户信息
                        mModel.saveUserInfo(login.getAuth(),
                                login.getInfo().getUid(),
                                login.getShopowner().getIs_shopowner(),
                                tPhone,
                                login.getIs_show_yingshan());

                        mView.loginSuccess();
                    }
                });
    }
}
