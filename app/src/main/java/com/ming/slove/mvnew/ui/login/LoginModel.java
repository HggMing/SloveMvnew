package com.ming.slove.mvnew.ui.login;

import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.model.bean.Login;
import com.orhanobut.hawk.Hawk;

/**
 * Created by Ming on 2016/12/9.
 */

public class LoginModel implements LoginContract.Model {
    @Override
    public void saveTest(boolean isTest, String url) {
        Hawk.put(APPS.KEY_IS_TEST, isTest);
        Hawk.put(APPS.KEY_BASE_URL, url);
    }

    @Override
    public void saveShopAddress(Login login) {
        String manager_vid = login.getShopowner().getManager_vid();
        if (manager_vid != null && manager_vid.length() >= 12) {
            String key_vid = manager_vid.substring(0, 12);//取出第一个店长vid
            Login.VidInfoBean vidInfoBean = login.getVid_info().get(key_vid);
            String vName = vidInfoBean.getProvince_name() +
                    vidInfoBean.getCity_name() +
                    vidInfoBean.getCounty_name() +
                    vidInfoBean.getTown_name() +
                    vidInfoBean.getVillage_name();//店长村详细地址

            Hawk.put(APPS.MANAGER_VID, key_vid);
            Hawk.put(APPS.MANAGER_ADDRESS, vName);
        }
    }

    @Override
    public void saveUserInfo(String auth, String uid, int is_shopowner,int is_show_yingshan) {
        Hawk.put(APPS.USER_AUTH, auth);//保存认证信息
        Hawk.put(APPS.ME_UID, uid);
        Hawk.put(APPS.IS_SHOP_OWNER, is_shopowner);
        Hawk.put(APPS.IS_SHOW_YINGSHAN, is_show_yingshan);
    }

    @Override
    public void saveLoginInfo(String loginName, String loginPwd) {
        Hawk.put(APPS.KEY_LOGIN_NAME, loginName);
        Hawk.put(APPS.KEY_LOGIN_PASSWORD, loginPwd);
    }

    @Override
    public void saveIsRememberPwd(boolean isRemember) {
        Hawk.put(APPS.IS_REMEMBER_PASSWORD, isRemember);
    }
}
