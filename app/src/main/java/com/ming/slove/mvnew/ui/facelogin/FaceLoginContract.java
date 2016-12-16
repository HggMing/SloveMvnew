package com.ming.slove.mvnew.ui.facelogin;

import com.ming.slove.mvnew.common.base.BasePresenter;
import com.ming.slove.mvnew.common.base.BaseView;
import com.ming.slove.mvnew.model.bean.Login;
import com.tbruyelle.rxpermissions.RxPermissions;

public interface FaceLoginContract {
    interface View extends BaseView<Presenter> {

        void setBtnOk(boolean isCanClick, String s);//设置登录键(能否点击，文字显示）

        void loginSuccess();//登录成功
    }

    interface Presenter extends BasePresenter {

        void userLogin(String phone, String photoPath);//用户登录

        void setHasFacePhoto(boolean b);//设置是否已拍摄人脸照片
    }

    interface Model {

        void saveShopAddress(Login login);//存储店长所在村id及地址

        void saveUserInfo(String auth, String uid, int is_shopowner, String loginName);//储存登录用户信息
    }
}
