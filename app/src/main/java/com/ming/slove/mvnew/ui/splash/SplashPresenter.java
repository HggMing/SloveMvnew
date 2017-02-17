package com.ming.slove.mvnew.ui.splash;

import android.Manifest;
import android.support.annotation.NonNull;

import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.ClickDialog;
import com.ming.slove.mvnew.ui.update.UpdateApp;
import com.orhanobut.hawk.Hawk;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

import static com.ming.slove.mvnew.common.utils.BaseTools.checkNotNull;


/**
 * Presenter
 */
public class SplashPresenter implements SplashContract.Presenter {
    @NonNull
    private final SplashContract.View mView;
    @NonNull
    private CompositeSubscription mSubscriptions;

    public SplashPresenter(@NonNull SplashContract.View splashView) {
        mView = checkNotNull(splashView);

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
    public void requestPermission(final RxPermissions rxPermissions) {
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            //已获得全部权限，检测更新
                            UpdateApp.updateAuto(mView.getContext(), new UpdateApp.DoOnActivity() {
                                @Override
                                public void notNeedUpdate() {
                                    //如果没有检测到更新，继续加载程序
                                    String loginName = Hawk.get(APPS.KEY_LOGIN_NAME, "");
                                    String loginPwd = Hawk.get(APPS.KEY_LOGIN_PASSWORD, "");

                                    if (!loginName.equals("") && !loginPwd.equals("")) {
                                        mView.goHome();
                                    } else {
                                        mView.goLogin();
                                    }
                                }
                            });
                        } else {
                            //用户没有勾选不再询问，返回true
                            rxPermissions.shouldShowRequestPermissionRationale(mView.getActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.RECORD_AUDIO)
                                    .subscribe(new Subscriber<Boolean>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onNext(Boolean aBoolean) {
                                            if (aBoolean) {
                                                String msg = "1、储存空间权限，用于储存用户信息;\n" +
                                                        "2、相机权限，用于发帖时，发布照片。\n"+
                                                        "3、麦克风权限，用于直播时录音。";
                                                mView.showDialog(msg, new ClickDialog.OnClickDialog() {
                                                    @Override
                                                    public void dialogOk() {
                                                        //点击ok，继续获取权限
                                                        requestPermission(rxPermissions);
                                                    }

                                                    @Override
                                                    public void dialogCannel() {
                                                        //点击cannel，退出程序
                                                        mView.closeActivity();
                                                    }
                                                });
                                            } else {
                                                String msg = "为正常体验软件，请在系统设置中，为本APP授权。";
                                                mView.showDialogOk(msg, new ClickDialog.OnClickDialogOk() {
                                                    @Override
                                                    public void dialogOk() {
                                                        //点击确定，退出程序
                                                        mView.closeActivity();
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
