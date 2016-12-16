package com.ming.slove.mvnew.ui.splash;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.ming.slove.mvnew.common.base.BasePresenter;
import com.ming.slove.mvnew.common.base.BaseView;
import com.tbruyelle.rxpermissions.RxPermissions;

public interface SplashContract {
    interface View extends BaseView<Presenter> {

        void goHome();

        void goLogin();

        Context getContext();

        FragmentActivity getActivity();
    }

    interface Presenter extends BasePresenter {

        void requestPermission(RxPermissions rxPermissions);//获取运行时权限（储存）
    }
}


