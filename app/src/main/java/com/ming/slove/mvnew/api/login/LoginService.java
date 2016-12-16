package com.ming.slove.mvnew.api.login;

import com.ming.slove.mvnew.model.bean.Login;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * 网络相关服务器接口
 */
interface LoginService {

    /**
     * 登录接口
     *
     * @param logname 用户名
     * @param pwd     密码
     * @return 用户信息
     */
    @GET("user/login")
    Observable<Login> get_Login(
            @Query("logname") String logname,
            @Query("pwd") String pwd);

    @GET("user/login")
    Observable<Login> get_Login2(@QueryMap Map<String, String> map);

    /**
     * 人脸登录：登录接口需要提供正面人脸照片和用户电话号码，系统在得到照片之后将和用户注册照片进行比对，返回结果。
     * 通过此接口获取，人脸登录的参数sign
     *
     * @param data    加密后的参数
     * @param facepic 人脸图片
     * @return 先base64解密，得到json，转出bean获得sign
     */
    @Multipart
    @POST("http://capi.nids.com.cn/iras/ver")
    Observable<ResponseBody> post_FaceLoginSign(
            @Part("data") RequestBody data,
            @Part("facepic\"; filename=\"face.jpg") RequestBody facepic);

    /**
     * 人脸认证成功后登录接口：该接口在进行人脸认证成功后，直接调用该接口，进行登录。
     * 该接口中如果该用户，未绑定就会直接绑定认证的时候提交的账号，如果绑定了 ，就会用绑定的账号登录
     *
     * @param sign 人脸认证标识，在进行人脸认证成功后，认证接口返回的sign
     * @return Login
     */
    @FormUrlEncoded
    @POST("user/authlogin")
    Observable<Login> post_FaceLogin(
            @Field("sign") String sign);
}
