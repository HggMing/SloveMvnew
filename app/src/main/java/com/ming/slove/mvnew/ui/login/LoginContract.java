package com.ming.slove.mvnew.ui.login;

import com.ming.slove.mvnew.common.base.BasePresenter;
import com.ming.slove.mvnew.common.base.BaseView;
import com.ming.slove.mvnew.model.bean.Login;

public interface LoginContract {
    interface View extends BaseView<Presenter> {
        void showRemember(String loginName, String loginPwd);//记住密码，界面显示

        void showNotRemember(String loginName);//不记住密码，界面显示

        void showIsTest(boolean isTest);//是否测试服务器，复选框状态显示

        void showLoginning();//登录中

        void showCanLogin();//可登录状态

        void loginSuccess();//登录成功,进入主页

        void loginFailure(String msg);//登录失败，提示

        void loginFailureToReg(String msg, String loginname);//未注册账号登录，前往注册页
    }

    interface Presenter extends BasePresenter {

        void setTest();//设为测试服务器

        void setNotTest();//设为正式服务器

        void onClickLogin(String loginName,String loginPwd);//点击登录

        void onClickJzmm();//点击记住密码

        void showView();//由参数决定的界面显示初始化
    }
    interface Model{

        void saveTest( boolean isTest,String url);//储存是否为测试服务器,及baseurl

        void saveShopAddress(Login login);//存储店长所在村id及地址

        void saveUserInfo(String auth, String uid, int is_shopowner);//储存登录用户信息

        void saveLoginInfo(String loginName, String loginPwd);//存储登录账号信息

        void saveIsRememberPwd(boolean isRemember);//储存是否记住密码
    }
}
