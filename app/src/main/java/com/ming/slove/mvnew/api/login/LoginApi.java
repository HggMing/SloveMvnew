package com.ming.slove.mvnew.api.login;

import android.util.Base64;

import com.ming.slove.mvnew.api.ApiUtils;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.common.utils.PhotoOperate;
import com.ming.slove.mvnew.model.bean.Login;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 登录相关api
 * Created by Ming on 2016/12/9.
 */

public class LoginApi {
    private static LoginService mService;
    public static boolean isApiChanged;

    private static LoginService getService() {
        if (mService == null||isApiChanged) {
            mService = ApiUtils.createService(LoginService.class);
            isApiChanged=false;
        }
        return mService;
    }

    public static Observable<Login> login(String loginName, String pwd) {
        return getService().get_Login(loginName, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 登录接口
     *
     * @param loginName 用户名
     * @param pwd       密码
     * @return Login
     */
    public static Observable<Login> login2(String loginName, String pwd) {
        Map<String, String> map = new HashMap<>();
        map.put("logname", loginName);
        map.put("pwd", pwd);
        return getService().get_Login2(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取人脸登录的参数Sign
     *
     * @param phone     用户手机号
     * @param photoPath 人脸照片地址
     * @return Login
     */
    public static Observable<ResponseBody> getFaceLoginSign(String phone, String photoPath) {
        /**
         *  compid	机构id
         did	设备id
         phone	用户电话号码
         type	类型	1001:人脸验证，1002：指纹验证， 1010：综合验证
         facepic	人脸图片
         fingerpic	指纹图片
         sign	参数和机构KEY组合字符串的加密串
         */
        //1)将除图片外的参数以及机构key组成一个字符串(注意顺序)
        String other = "compid=9&did=123456&phone=" + phone + "&type=1001";
        String str = other + "&key=69939442285489888751746749876227";
        //2)使用MD5算法加密上述字符串
        String sign = BaseTools.md5(str);
        //3)最终得到参数字符串：（注意，KEY参数不带到参数列表,sign参数加入参数列表）
        String str2 = other + "&sign=" + sign;
        //4)把上述字符串做base64加密，最终得到请求:
        String paraString = Base64.encodeToString(str2.getBytes(), Base64.NO_WRAP);
        RequestBody data = RequestBody.create(MediaType.parse("text/plain"), paraString);

        //对图片压缩处理
        File file = null;
        try {
            file = new PhotoOperate().scal(photoPath);//自定义相机方案
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (file != null) {
            RequestBody ficepic = RequestBody.create(MediaType.parse("image/*"), file);
            return getService()
                    .post_FaceLoginSign(data, ficepic)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } else {
            return null;
        }
    }

    /**
     * 人脸登录接口
     *
     * @param sign 人脸认证成功标识sign
     * @return Login
     */
    public static Observable<Login> faceLogin(String sign) {
        return getService()
                .post_FaceLogin(sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
