package com.ming.slove.mvnew.api;

import com.ming.slove.mvnew.model.bean.A1Provice;
import com.ming.slove.mvnew.model.bean.A2City;
import com.ming.slove.mvnew.model.bean.A3County;
import com.ming.slove.mvnew.model.bean.A4Town;
import com.ming.slove.mvnew.model.bean.A5Village;
import com.ming.slove.mvnew.model.bean.ApplyInfo;
import com.ming.slove.mvnew.model.bean.BBSDetail;
import com.ming.slove.mvnew.model.bean.BBSList;
import com.ming.slove.mvnew.model.bean.BbsCommentList;
import com.ming.slove.mvnew.model.bean.Book2List;
import com.ming.slove.mvnew.model.bean.BookList;
import com.ming.slove.mvnew.model.bean.CardList;
import com.ming.slove.mvnew.model.bean.CheckPhone;
import com.ming.slove.mvnew.model.bean.EbankWifiConnect;
import com.ming.slove.mvnew.model.bean.ExpressFirm;
import com.ming.slove.mvnew.model.bean.ExpressList;
import com.ming.slove.mvnew.model.bean.FollowVillageList;
import com.ming.slove.mvnew.model.bean.FriendDetail;
import com.ming.slove.mvnew.model.bean.FriendList;
import com.ming.slove.mvnew.model.bean.IncomeBase;
import com.ming.slove.mvnew.model.bean.IncomeHistory;
import com.ming.slove.mvnew.model.bean.IncomeMonth;
import com.ming.slove.mvnew.model.bean.IncomeReward;
import com.ming.slove.mvnew.model.bean.InsuranceOrderList;
import com.ming.slove.mvnew.model.bean.IpPort;
import com.ming.slove.mvnew.model.bean.Login;
import com.ming.slove.mvnew.model.bean.MessageList;
import com.ming.slove.mvnew.model.bean.MoneyDetail;
import com.ming.slove.mvnew.model.bean.MyOrderList;
import com.ming.slove.mvnew.model.bean.MyVillUsers;
import com.ming.slove.mvnew.model.bean.NewsList;
import com.ming.slove.mvnew.model.bean.OrderInfo;
import com.ming.slove.mvnew.model.bean.ProductList;
import com.ming.slove.mvnew.model.bean.ProductNewOrder;
import com.ming.slove.mvnew.model.bean.QueryVillageList;
import com.ming.slove.mvnew.model.bean.RechargeOrderList;
import com.ming.slove.mvnew.model.bean.RecommendList;
import com.ming.slove.mvnew.model.bean.RecommendVillage;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.ResultOther;
import com.ming.slove.mvnew.model.bean.SalesOrderList;
import com.ming.slove.mvnew.model.bean.ShoppingAddress;
import com.ming.slove.mvnew.model.bean.TravelOrderList;
import com.ming.slove.mvnew.model.bean.UpdateAppBack;
import com.ming.slove.mvnew.model.bean.UploadFiles;
import com.ming.slove.mvnew.model.bean.UserInfo;
import com.ming.slove.mvnew.model.bean.UserInfoByPhone;
import com.ming.slove.mvnew.model.bean.VillageInfo;
import com.ming.slove.mvnew.model.bean.VillageMaster;
import com.ming.slove.mvnew.model.bean.ZanList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 网络相关服务器接口
 */
public interface MyService {
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

    /**
     * 连接ebank网络时，获取ip和端口
     *
     * @return IpPort
     */
    @GET("http://1.2.3.4/getip")
    Observable<IpPort> get_IpPort();

    /**
     * 若连接ebank网络，则认证上网
     *
     * @param ip   get_IpPort 获取到的ip
     * @param port get_IpPort 获取到的port
     * @param mac  get_IpPort 获取到的mac
     * @param auth 认证信息
     * @return EbankWifiConnect
     */
    @GET("http://{ip}:{port}/check")
    Observable<EbankWifiConnect> get_EbankWifiConnect(
            @Path("ip") String ip,
            @Path("port") String port,
            @Query("mac") String mac,
            @Query("auth") String auth);

    /**
     * 进行实名认证，注册接口
     *
     * @param data    加密后的参数
     * @param facepic 正面免冠图片
     * @param pic1    身份证正面照片
     * @return 先base64解密，得到json
     */
    @Multipart
    @POST("http://capi.nids.com.cn/iras/reg")
    Observable<ResponseBody> post_FaceRealBinding(
            @Part("data") RequestBody data,
            @Part("facepic\"; filename=\"jpg") RequestBody facepic,
            @Part("pic1\"; filename=\"jpg") RequestBody pic1);

    /**
     * 验证是否已实名认证
     *
     * @param data 加密后的参数
     * @return 先base64解密，得到json
     */
    @Multipart
    @POST("http://capi.nids.com.cn/iras/userinfo")
    Observable<ResponseBody> post_IsRealBinding(
            @Part("data") RequestBody data);

    /**
     * 人脸登录：登录接口需要提供正面人脸照片和用户电话号码，系统在得到照片之后将和用户注册照片进行比对，返回结果。
     *
     * @param data    加密后的参数
     * @param facepic 人脸图片
     * @return 先base64解密，得到json
     */
    @Multipart
    @POST("http://capi.nids.com.cn/iras/ver")
    Observable<ResponseBody> post_FaceLogin(
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
    Observable<Login> post_FaceLogin2(
            @Field("sign") String sign);

    /**
     * 验证手机号码是否注册接口
     *
     * @param tel 手机号
     * @return 注册参数sign
     */
    @GET("user/telcheck")
    Observable<CheckPhone> get_CheckPhone(
            @Query("tel") String tel);

    /**
     * 获取注册验证码接口
     *
     * @param sign 验证手机号时，返回的签名 sign
     * @param type 类型，1注册，2找回密码,3、重新绑定手机
     * @param tel  手机号
     * @return 结果msg
     */
    @GET("user/rcode")
    Observable<Result> get_RCode(
            @Query("sign") String sign,
            @Query("type") int type,
            @Query("tel") String tel);

    /**
     * 注册接口
     *
     * @param tel    手机号码
     * @param code   验证码
     * @param pwd    密码（6-16位）
     * @param sign   签名
     * @param rphone 推荐人手机号
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("user/register")
    Observable<Result> post_Register(
            @Field("tel") String tel,
            @Field("code") String code,
            @Field("pwd") String pwd,
            @Field("sign") String sign,
            @Field("rphone") String rphone);

    /**
     * 忘记密码，账号检查接口
     *
     * @param tel 手机号码
     * @return 注册参数sign
     */
    @GET("password/telcheck")
    Observable<CheckPhone> get_CheckPhonePSW(
            @Query("tel") String tel);

    /**
     * 重置密码接口:该接口主要用户，忘记密码，通过短信验证码重置密码密码的接口
     *
     * @param tel  登录账号
     * @param pwd  密码（6-16位）
     * @param code 验证码
     * @param sign 找回密码签名
     * @return 结果msg（返回有info，暂时不用）
     */
    @FormUrlEncoded
    @POST("password/findpwd")
    Observable<Result> post_ResetPassword(
            @Field("tel") String tel,
            @Field("pwd") String pwd,
            @Field("code") String code,
            @Field("sign") String sign);

    /**
     * 意见反馈接口
     *
     * @param auth    认证信息
     * @param content 反馈内容
     * @param contact 联系方式
     * @return 是否成功
     */
    @GET("feedback/add")
    Observable<Result> get_Advice(
            @Query("auth") String auth,
            @Query("content") String content,
            @Query("contact") String contact);

    /**
     * 获取用户信息接口
     *
     * @param auth 认证信息
     * @return 用户信息
     */
    @GET("user/ginfo")
    Observable<UserInfo> get_UserInfo(
            @Query("auth") String auth);

    /**
     * 修改密码接口
     *
     * @param auth   认证信息
     * @param oldPwd 原始密码
     * @param pwd    新密码 新密码（6-16位）
     * @return 结果msg
     */
    @GET("password/upwd")
    Call<Result> getCall_ChangePwd(
            @Query("auth") String auth,
            @Query("Old_pwd") String oldPwd,
            @Query("pwd") String pwd);

    /**
     * 用户已经登录系统，修改头像
     *
     * @param auth 认证信息
     * @param head 头像：内容采用base64后的字符串，再加上扩展名
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("user/uhead")
    Observable<Result> post_UpdateHead(
            @Field("auth") String auth,
            @Field("head") String head);

    /**
     * 修改用户信息接口
     *
     * @param auth  认证信息
     * @param uName 姓名
     * @param sex   性别
     * @param cid   身份证号码
     * @param vid   村圈id→我的地址
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("user/uinfo")
    Call<Result> postCall_UpdateInfo(
            @Field("auth") String auth,
            @Field("uname") String uName,
            @Field("sex") String sex,
            @Field("cid") String cid,
            @Field("vid") String vid);

    /**
     * 五级详细地址列表接口
     *
     * @param auth 认证信息
     * @return 各级地址信息
     */
    @GET("vill/gprovicelist")
    Call<A1Provice> getCall_Add1(
            @Query("auth") String auth);

    @GET("vill/gcitylist")
    Call<A2City> getCall_Add2(
            @Query("auth") String auth,
            @Query("province_id") String province_id);

    @GET("vill/gcountylist")
    Call<A3County> getCall_Add3(
            @Query("auth") String auth,
            @Query("city_id") String city_id);

    @GET("vill/gtownlist")
    Call<A4Town> getCall_Add4(
            @Query("auth") String auth,
            @Query("county_id") String county_id);

    @GET("vill/gvilllist")
    Call<A5Village> getCall_Add5(
            @Query("auth") String auth,
            @Query("town_id") String town_id);

    /**
     * 分页获取村圈列表信息，同时该接口会返回，关注总数cnt
     *
     * @param auth     认证信息
     * @param page     当前页码，默认为：1页
     * @param pagesize 每页条数，默认20条
     * @return 村圈列表信息
     */
    @GET("vill/followlist")
    Call<FollowVillageList> getCall_FollowList(
            @Query("auth") String auth,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 查询村庄详细地址接口
     *
     * @param village_name 村名 关键字
     * @return 详细村地址名
     */
    @GET("vill/qlist")
    Observable<QueryVillageList> get_QueryVillage(
            @Query("village_name") String village_name);

    /**
     * 取消关注村圈接口
     *
     * @param auth 认证信息
     * @param vid  村id
     * @return 结果msg
     */
    @GET("vill/del")
    Call<Result> getCall_DelFollowList(
            @Query("auth") String auth,
            @Query("vid") String vid);

    /**
     * 关注村圈接口
     *
     * @param auth 认证信息
     * @param vid  村id
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("vill/follow")
    Call<Result> postCall_FollowVillage(
            @Field("auth") String auth,
            @Field("vid") String vid);

    /**
     * 获取推荐村圈接口
     *
     * @param auth 认证信息
     * @return 推荐的村
     */
    @GET("vill/recommend")
    Observable<RecommendVillage> get_RecommendVillage(
            @Query("auth") String auth);

    /**
     * 获取政务里，消息列表
     *
     * @param vid      村id
     * @param type     1：新闻，2：政策，3：服务，4：资讯
     * @param page     当前页码，默认为：1页
     * @param pagesize 每页条数，默认20条
     * @return 详细列表清单
     */
    @GET("news/list")
    Observable<NewsList> get_NewsList(
            @Query("vid") String vid,
            @Query("type") int type,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 村况里，荣誉室，活动，美食
     *
     * @param vid      村id
     * @param type     1、荣誉室2、活动3、村委（Item不同，单独写）4、美食
     * @param page     当前页码，默认为：1页
     * @param pagesize 每页条数，默认20条
     * @return 详细列表清单
     */
    @GET("tool/list")
    Observable<VillageInfo> get_VillageInfoList(
            @Query("vid") String vid,
            @Query("type") int type,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 村况里，荣誉室，活动，美食的添加
     *
     * @param type 1、荣誉室2、活动3、村委（Item不同，单独写）4、美食
     */
    @Multipart
    @POST("tool/add")
    Observable<Result> post_AddVillageInfo(
            @Part("auth") RequestBody auth,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content,
            @Part("type") RequestBody type,
            @Part("files\"; filename=\"jpg") RequestBody file);

    /**
     * 村况里，荣誉室，活动，美食的删除
     *
     * @param id item id
     */
    @FormUrlEncoded
    @POST("tool/del")
    Observable<Result> post_DelVillageInfo(
            @Field("auth") String auth,
            @Field("id") String id);

    /**
     * 村况里，村委列表
     *
     * @param auth 验证参数
     * @param vid  村id
     * @return 村委列表
     */
    @GET("cg/list")
    Observable<VillageMaster> get_VillageMasterList(
            @Query("auth") String auth,
            @Query("vid") String vid);

    /**
     * 新增村委信息
     *
     * @param contact 电话
     * @param job     职务
     * @param sex     0男1女
     * @param uname   姓名
     * @param vid     村id
     * @param zzmm    政治面貌
     * @param file    头像
     * @return data-id
     */
    @Multipart
    @POST("cg/add")
    Observable<Result> post_AddVillageMaster(
            @Part("auth") RequestBody auth,
            @Part("contact") RequestBody contact,
            @Part("job") RequestBody job,
            @Part("sex") RequestBody sex,
            @Part("uname") RequestBody uname,
            @Part("vid") RequestBody vid,
            @Part("zzmm") RequestBody zzmm,
            @Part("head\"; filename=\"jpg") RequestBody file);

    /**
     * 删除村委信息
     *
     * @param id item id
     */
    @FormUrlEncoded
    @POST("cg/del")
    Observable<Result> post_DelVillageMaster(
            @Field("auth") String auth,
            @Field("id") String id);

    /**
     * 该接口用户帖子的附件上传，包括图片其他压缩包等
     *
     * @param auth 验证参数
     * @param file 附件上传
     * @return insert_id和url
     */
    @Multipart
    @POST("bbs/ufiles")
    Observable<UploadFiles> post_UploadImage(
            @Part("auth") RequestBody auth,
            @Part("files\"; filename=\"jpg") RequestBody file
            // @PartMap Map<String, RequestBody> params,
    );

    /**
     * 该接口用户帖子的附件上传，上传视频
     * @param auth 验证参数
     * @param type 0图片2视频（图片可以不传，默认0）
     * @param file 附件上传
     * @return
     */
    @Multipart
    @POST("bbs/ufiles")
    Observable<UploadFiles> post_UploadVideo(
            @Part("auth") RequestBody auth,
            @Part("type") RequestBody type,
            @Part("files\"; filename=\"mp4") RequestBody file);

    /**
     * 发布新帖子
     *
     * @param auth    认证信息
     * @param vid     村id
     * @param title   标题，最长64个字
     * @param conts   内容，最长500个字
     * @param pimg    图片
     * @param file_id 附件的id，没有附件就不传(多个附件请用“逗号”隔开)
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("bbs/add")
    Observable<Result> post_BBSPost(
            @Field("auth") String auth,
            @Field("vid") String vid,
            @Field("title") String title,
            @Field("conts") String conts,
            @Field("pimg") String pimg,
            @Field("file_id") String file_id);

    /**
     * 评论帖子
     *
     * @param auth  认证信息
     * @param pid   帖子id
     * @param conts 评论内容，最长255个字
     * @return 结果msg+insert_id
     */
    @FormUrlEncoded
    @POST("bbs/addcom")
    Observable<Result> post_AddComment(
            @Field("auth") String auth,
            @Field("pid") String pid,
            @Field("conts") String conts);

    /**
     * 删除帖子接口
     *
     * @param auth 认证信息
     * @param id   帖子id
     * @return 结果meg
     */
    @FormUrlEncoded
    @POST("bbs/del")
    Observable<Result> post_DeleteBbs(
            @Field("auth") String auth,
            @Field("id") String id);

    /**
     * 删除帖子评论接口
     *
     * @param auth 认证信息
     * @param id   评论id
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("bbs/delcom")
    Observable<Result> post_DeleteComment(
            @Field("auth") String auth,
            @Field("id") String id);

    /**
     * 获取帖子列表接口
     *
     * @param auth     认证信息
     * @param vid      村id
     * @param page     当前页码，默认为：1页
     * @param pagesize 每页条数，默认20条
     * @return 帖子列表信息
     */
    @GET("bbs/list")
    Observable<BBSList> get_BBSList(
            @Query("auth") String auth,
            @Query("vid") String vid,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 获取帖子详细内容
     */
    @GET("bbs/getinfo")
    Observable<BBSDetail> get_BBSDetail(
            @Query("auth") String auth,
            @Query("id") String id);

    /**
     * 获取评论列表接口
     *
     * @param auth     认证信息
     * @param pid      帖子id
     * @param page     当前页码，默认为：1页
     * @param pagesize 每页条数，默认20条
     * @return 评论列表
     */
    @GET("bbs/comlist")
    Observable<BbsCommentList> get_BbsCommentList(
            @Query("auth") String auth,
            @Query("pid") String pid,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 获取村圈中帖子点赞的列表
     *
     * @param auth     认证信息
     * @param pid      帖子id
     * @param page     当前页码，默认为：1页
     * @param pagesize 每页条数，默认20条
     * @return 点赞时间，用户头像和名字
     */
    @GET("bbs/zanlist")
    Observable<ZanList> get_ZanList(
            @Query("auth") String auth,
            @Query("pid") String pid,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 对文档点赞
     *
     * @param auth 认证信息
     * @param pid  文档id
     * @return 结果msg
     */
    @GET("bbs/zans")
    Call<Result> getCall_ClickLike(
            @Query("auth") String auth,
            @Query("pid") String pid);

    /**
     * 举报帖子接口
     *
     * @param auth  认证信息
     * @param bid   帖子id,必填
     * @param conts 举报原因，选填
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("bbs/report")
    Observable<Result> post_Report(
            @Field("auth") String auth,
            @Field("bid") String bid,
            @Field("conts") String conts);

    /**
     * 分页获取好友列表信息，同时该接口会返回，好友总数cnt，其中uid:10000:小包谷【语音助手】;uid:10001:我们村【客服】
     *
     * @param auth     认证信息
     * @param page     当前页码，默认为：1页
     * @param pagesize 每页条数，默认20条
     * @return "aname":"备注名",
     * "uid":"13123",
     * "name":"昵称",
     * "phone":"手机号",
     * "sex":"0",
     * "head":"头像"
     */
    @GET("friend/list")
    Observable<FriendList> get_FriendList(
            @Query("auth") String auth,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 通过手机号获取用户的档案信息，用于查询以便添加好友
     *
     * @param auth 认证信息
     * @param tel  手机号
     * @return 用户信息
     */
    @GET("friend/getuserinfo")
    Observable<UserInfoByPhone> get_UserInfoByPhone(
            @Query("auth") String auth,
            @Query("tel") String tel);

    /**
     * 发送添加好友的请求
     *
     * @param auth 认证信息
     * @param tel  手机号
     * @return 结果msg
     */
    @GET("friend/adde")
    Observable<Result> get_AddFriendRequest(
            @Query("auth") String auth,
            @Query("tel") String tel);

    /**
     * 同意和拒绝添加好友接口
     *
     * @param auth 认证信息
     * @param uid  用户id
     * @return String
     */
    @Multipart
    @POST("friend/agree")
    Observable<ResponseBody> post_AddAgree(
            @Part("auth") RequestBody auth,
            @Part("uid") RequestBody uid);

    @Multipart
    @POST("friend/unagree")
    Observable<ResponseBody> post_AddUnagree(
            @Part("auth") RequestBody auth,
            @Part("uid") RequestBody uid);

    /**
     * 获取好友的档案信息
     *
     * @param auth 认证信息
     * @param uid  好友id
     * @return "userinfo":用户信息
     * "photoinfo":用户相册
     * "videoinfo":用户视频
     * "voiceinfo":语音留言（废除）
     * "scoreinfo":学习成绩
     * "healthinfo":健康状况
     * "bbs_top_pic4":帖子最新4图
     */
    @GET("arch/info_e")
    Observable<FriendDetail> get_FriendDetail(
            @Query("auth") String auth,
            @Query("uid") String uid);

    /**
     * 设置好友别名或者称呼
     *
     * @param auth  认证信息
     * @param uid   好友id
     * @param aname 好友别名或者称呼
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("friend/rname")
    Observable<Result> post_RemarkName(
            @Field("auth") String auth,
            @Field("uid") String uid,
            @Field("aname") String aname);

    /**
     * 删除好友接口
     *
     * @param auth 认证信息
     * @param uid  好友id
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("friend/del")
    Observable<Result> post_DelFriend(
            @Field("auth") String auth,
            @Field("uid") String uid);

    /**
     * 获取用户（好友）帖子列表接口
     *
     * @param auth     认证信息
     * @param uid      用户id
     * @param page     当前页码，默认为：1页
     * @param pagesize 每页条数，默认20条
     * @return 帖子列表信息
     */
    @GET("bbs/ulist")
    Observable<BBSList> get_FriendBbsList(
            @Query("auth") String auth,
            @Query("uid") String uid,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 聊天机器人助手，小苞谷接口
     *
     * @param data 特定方式加密后字符串
     * @return 先base64解密，得到json
     */
    @Multipart
    @POST("http://capi.nids.com.cn/znzd/voice")
    Observable<ResponseBody> post_ChatRobot(
            @Part("data") RequestBody data);

    /**
     * 用户之间发送消息
     *
     * @param from   发送人id
     * @param to     接收人id
     * @param ct     消息类型，0文字，1图片，2声音，3html，4内部消息json格式，5交互消息 6应用透传消息json格式,7朋友系统消息json，100分享
     * @param app    发送消息的app
     * @param txt    消息内容
     * @param source 发送的资源，默认空数组，如果有资源则是数据流base64后的数据+’.’+资源的扩展名
     * @param ex     扩展字段， 根据不同应用定义不同的意义
     * @param mt     发送方式:1即时消息，2异步消息
     * @param xt     发送人类型 0系统，2用户与用户，1公众号与用户
     * @return 返回
     */
    @FormUrlEncoded
    @POST("http://push.traimo.com/msg/user_sent")
    Observable<Result> post_sendMessage(
            @Field("from") String from,
            @Field("to") String to,
            @Field("ct") String ct,
            @Field("app") String app,
            @Field("txt") String txt,
            @Field("source") String source,
            @Field("ex") String ex,
            @Field("mt") int mt,
            @Field("xt") String xt);

    /**
     * 用户获取消息接口
     *
     * @param me  接收人id
     * @param app 需要什么app的消息
     * @param os  1安卓，2苹果，3winphone，4  web
     * @return MessageList
     */
    @GET("http://push.traimo.com/msg/lists")
    Observable<MessageList> get_MessageList(
            @Query("me") String me,
            @Query("app") String app,
            @Query("os") int os);

    /**
     * 注册用户（当客户端与推送服务器建立连接后调用）
     *
     * @param me  当前的eid
     * @param os  1安卓，2苹果，3winphone，4  web
     * @param app 当前应用，yxj
     * @param cid 第三方连接id
     * @return 结果msg
     */
    @GET("http://push.traimo.com/client/register")
    Observable<Result> getObservable_RegisterChat(
            @Query("me") String me,
            @Query("os") int os,
            @Query("app") String app,
            @Query("cid") String cid);

    /**
     * 申请站长接口
     *
     * @param auth     认证信息
     * @param vid      村id
     * @param uname    姓名
     * @param contact  手机号码
     * @param conts    申请理由
     * @param sex      性别 0:男 1:女
     * @param edu      教育程度
     * @param cid_img1 身份证正面照ID :先通过上传接口将图片上传，这里的参数传返回的ID
     * @param cid_img2 身份证背面照ID: 先通过上传接口将图片上传，这里的参数传返回的ID
     * @param q_img    其他材料:注意，这里将所有其他材料上传后组合成字符串，使用”,”分割，如：878,879,880
     * @param brithday 生日:字符串格式
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("vill/applymaster")
    Observable<Result> post_ApplyMaster(
            @Field("auth") String auth,
            @Field("vid") String vid,
            @Field("uname") String uname,
            @Field("contact") String contact,
            @Field("conts") String conts,
            @Field("sex") int sex,
            @Field("edu") String edu,
            @Field("cid_img1") int cid_img1,
            @Field("cid_img2") int cid_img2,
            @Field("q_img") int q_img,
            @Field("brithday") String brithday);

    /**
     * 查询申请站长状态
     *
     * @param auth 认证信息
     * @param vid  村id
     * @return 申请人的信息
     */
    @GET("vill/applystatus")
    Observable<ApplyInfo> get_IsApply(
            @Query("auth") String auth,
            @Query("vid") String vid);

    /**
     * 是否设置了钱包密码
     *
     * @param auth 认证信息
     * @return is_pwd
     */
    @GET("amount/is_set_pwd")
    Observable<ResultOther> get_IsSetPWD(
            @Query("auth") String auth);

    /**
     * 设置钱包密码
     *
     * @param auth 认证信息
     * @param pwd  钱包密码
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("amount/set_pwd")
    Observable<Result> post_SetPursePWD(
            @Field("auth") String auth,
            @Field("pwd") String pwd);

    /**
     * 修改钱包密码
     *
     * @param auth    认证信息
     * @param old_pwd 原始钱包密码
     * @param new_pwd 新钱包密码
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("amount/reset_pwd")
    Observable<Result> post_EditPursePWD(
            @Field("auth") String auth,
            @Field("old_pwd") String old_pwd,
            @Field("new_pwd") String new_pwd);

    /**
     * 获取重置钱包密码的验证码
     *
     * @param auth 认证信息
     * @return 结果msg
     */
    @GET("user/get_trade_code")
    Observable<Result> get_TradeCode(
            @Query("auth") String auth);

    /**
     * 重置钱包密码接口
     *
     * @param auth 认证信息
     * @param code 验证码
     * @param pwd  密码
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("user/edit_trade_pwd")
    Observable<Result> post_ResetPursePwd(
            @Field("auth") String auth,
            @Field("code") String code,
            @Field("pwd") String pwd);

    /**
     * 查询余额，及是否绑定银行卡
     *
     * @param auth 认证信息
     * @return money，is_bind
     */
    @GET("amount/balance")
    Observable<ResultOther> get_Money(
            @Query("auth") String auth);

    /**
     * 查询收支明细
     *
     * @param auth 认证信息
     * @return 明细
     */
    @GET("amount/money_detail")
    Observable<MoneyDetail> get_MoneyDetail(
            @Query("auth") String auth);

    //本月收入（总）
    @GET("account/income_currentmonth")
    Observable<ResultOther> get_Money_Month(
            @Query("auth") String auth);

    //本月：基础、返点、提成（简）
    @GET("account/get_threekindmony")
    Observable<IncomeMonth> get_Income_Month(
            @Query("auth") String auth);

    //本月：基础收入（详）
    @GET("account/get_currentmmoneylist")
    Observable<IncomeBase> get_Income_Base(
            @Query("auth") String auth);

    //本月：业务提成（详）
    @GET("account/get_currentMyewuList")
    Observable<IncomeBase> get_Income_Business(
            @Query("auth") String auth);

    //本月：返点奖励（详）
    @GET("account/get_currentmprofitlist")
    Observable<IncomeReward> get_Income_Reward(
            @Query("auth") String auth);

    //历史收入（简）
    @GET("account/get_lastsixmmony")
    Observable<IncomeHistory> get_Income_History(
            @Query("auth") String auth);

    /**
     * 获取绑定的银行卡列表
     *
     * @param auth 认证信息
     * @return 列表
     */
    @GET("amount/cardlist")
    Observable<CardList> get_CardList(
            @Query("auth") String auth);

    /**
     * 添加绑定银行卡接口
     *
     * @param auth           认证信息
     * @param bank_name      开户行
     * @param bank_no        银行卡号
     * @param bank_true_name 开卡人姓名
     * @return insert_id
     */
    @FormUrlEncoded
    @POST("amount/bind")
    Observable<ResultOther> post_AddCard(
            @Field("auth") String auth,
            @Field("bank_name") String bank_name,
            @Field("bank_no") String bank_no,
            @Field("bank_true_name") String bank_true_name);

    /**
     * 取消绑定银行卡接口
     *
     * @param auth 认证信息
     * @param id   编号
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("amount/del")
    Observable<Result> post_DelCard(
            @Field("auth") String auth,
            @Field("id") String id);

    /**
     * 提现接口
     *
     * @param auth           认证信息
     * @param money          提现金额
     * @param pwd            钱包密码
     * @param bank_name      开户行
     * @param bank_no        银行卡号
     * @param bank_true_name 开卡人姓名
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("amount/gmoney")
    Observable<Result> post_TakeMoney(
            @Field("auth") String auth,
            @Field("money") String money,
            @Field("pwd") String pwd,
            @Field("bank_name") String bank_name,
            @Field("bank_no") String bank_no,
            @Field("bank_true_name") String bank_true_name);


    /**
     * 获取我的订单列表（包含特产和村实惠）
     *
     * @param auth     认证信息
     * @param page     页
     * @param pagesize 每页数
     * @return MyOrderList
     */
    @GET("orders/orderlist")
    Observable<MyOrderList> get_OrderList(
            @Query("auth") String auth,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 取消未支付订单
     *
     * @param auth     认证信息
     * @param order_no 订单号
     * @return Result
     */
    @FormUrlEncoded
    @POST("orders/cancelorder")
    Observable<Result> post_OrderCancel(
            @Field("auth") String auth,
            @Field("order_no") String order_no);

    /**
     * 订单确认收货
     *
     * @param auth     认证信息
     * @param order_no 订单号
     * @return Result
     */
    @FormUrlEncoded
    @POST("orders/confirm")
    Observable<Result> post_OrderConfirm(
            @Field("auth") String auth,
            @Field("order_no") String order_no);

    /**
     * 获取收货地址列表
     *
     * @param auth 认证信息
     * @return 列表
     */
    @GET("orders/addrlist")
    Observable<ShoppingAddress> get_ShoppingAddress(
            @Query("auth") String auth);

    /**
     * 添加收货地址列表
     *
     * @param auth    认证信息
     * @param uname   收货人姓名
     * @param addr    收货地址
     * @param tel     手机
     * @param is_def  是否默认
     * @param zipcode 邮政编码
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("orders/newaddr")
    Observable<Result> post_AddShoppingAddress(
            @Field("auth") String auth,
            @Field("uname") String uname,
            @Field("addr") String addr,
            @Field("tel") String tel,
            @Field("is_def") String is_def,
            @Field("zipcode") String zipcode);

    //修改收货地址
    @FormUrlEncoded
    @POST("orders/editaddr")
    Observable<Result> post_EditShoppingAddress(
            @Field("auth") String auth,
            @Field("id") String id,
            @Field("uname") String uname,
            @Field("addr") String addr,
            @Field("tel") String tel,
            @Field("is_def") String is_def,
            @Field("zipcode") String zipcode);

    //删除地址
    @FormUrlEncoded
    @POST("orders/deladdr")
    Observable<Result> post_DelShoppingAddress(
            @Field("auth") String auth,
            @Field("id") String id);

    /**
     * 根据订单号获取订单详情
     *
     * @param order_sn 订单号
     * @param auth     认证信息
     * @return OrderInfo
     */
    @GET("orders/orderinfo")
    Observable<OrderInfo> get_OrderInfo(
            @Query("order_sn") String order_sn,
            @Query("auth") String auth);


    /**
     * 生成特产订单
     *
     * @param auth          认证信息
     * @param buy_uname     购买人姓名
     * @param product       商品列表。格式如下id1,num1|id2,num2|id3,num3|...id为商品ID，num为数量，不同商品之间用"|"分割，同一商品字段用","分割
     * @param province      省份
     * @param city          市
     * @param district      区
     * @param address       详细地址
     * @param zipcode       邮编
     * @param tel           电话
     * @param postscript    备注消息
     * @param shipping_id   用户选择的配送方式id
     * @param shipping_name 用户选择的配送方式名称
     * @param vid           村id
     * @return ProductNewOrder
     */
    @FormUrlEncoded
    @POST("orders/neworder")
    Observable<ProductNewOrder> post_NewOrder(
            @Field("auth") String auth,
            @Field("buy_uname") String buy_uname,
            @Field("product") String product,
            @Field("province") String province,
            @Field("city") String city,
            @Field("district") String district,
            @Field("address") String address,
            @Field("zipcode") String zipcode,
            @Field("tel") String tel,
            @Field("postscript") String postscript,
            @Field("shipping_id") String shipping_id,
            @Field("shipping_name") String shipping_name,
            @Field("vid") String vid);


    /**
     * 采用余额支付
     *
     * @param auth     认证信息
     * @param order_sn 订单号
     * @param pwd      支付密码
     * @param money    支付金额
     * @return Result
     */
    @FormUrlEncoded
    @POST("orders/yuer_pay")
    Observable<Result> post_PayByYuer(
            @Field("auth") String auth,
            @Field("order_sn") String order_sn,
            @Field("pwd") String pwd,
            @Field("money") String money);

    /**
     * 获取快递列表接口
     *
     * @param vid    村id
     * @param status 2：收件列表；5：寄件列表
     * @return 快递列表
     */
    @GET("express/list")
    Observable<ExpressList> get_ExpressList(
            @Query("vid") String vid,
            @Query("status") String status);

    //获取可选快递公司列表
    @GET("express/ship")
    Observable<ExpressFirm> get_ExpressFirm(
            @Query("auth") String auth);

    /**
     * 添加可收取快递
     *
     * @param auth      认证信息
     * @param vid       店长村id
     * @param number    单号
     * @param status    2：取件
     * @param ship      快递公司
     * @param qujianren 取件人
     * @param qphone    取件人号码
     * @return insert_id
     */
    @FormUrlEncoded
    @POST("express/add")
    Observable<ResultOther> post_EditExpressTake(
            @Field("auth") String auth,
            @Field("vid") String vid,
            @Field("number") String number,
            @Field("status") String status,
            @Field("ship") String ship,
            @Field("qujianren") String qujianren,
            @Field("qphone") String qphone);

    /**
     * 添加新的寄件及编辑
     *
     * @param id        寄件身份信息id
     * @param number    寄件单号
     * @param ship      快递公司
     * @param ship_fee  快递费
     * @param jijianren 寄件人
     * @param jphone    寄件人手机
     * @param addr      寄件地址
     * @return 结果msg
     */
    @FormUrlEncoded
    @POST("express/addinfo")
    Observable<Result> post_EditExpressSend(
            @Field("id") String id,
            @Field("number") String number,
            @Field("ship") String ship,
            @Field("ship_fee") String ship_fee,
            @Field("jijianren") String jijianren,
            @Field("jphone") String jphone,
            @Field("addr") String addr);

    /**
     * 未借图书列表
     */
    @GET("book/list")
    Observable<BookList> get_BookListTab1(
            @Query("vid") String vid,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 已借图书列表
     */
    @GET("book/booklogs")
    Observable<Book2List> get_BookListTab2(
            @Query("vid") String vid,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 添加图书
     *
     * @return data-id
     */
    @Multipart
    @POST("book/addbook")
    Observable<Result> post_AddBook(
            @Part("auth") RequestBody auth,
            @Part("vid") RequestBody vid,
            @Part("bname") RequestBody bname,
            @Part("bnum") RequestBody bnum,
            @Part("files\"; filename=\"jpg") RequestBody file);

    /**
     * 获取本村用户列表
     *
     * @param auth     认证信息
     * @param page     页数
     * @param pagesize 每页条数
     * @return 用户信息
     */
    @GET("vill/get_my_vill_user")
    Observable<MyVillUsers> get_MyVillUsers(
            @Query("auth") String auth,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 动态页面，获取推荐村特产
     *
     * @return RecommendList
     */
    @GET("product/tlist")
    Observable<RecommendList> get_RecommendList(
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 获取村特产列表
     *
     * @param auth     认证信息
     * @param vid      村id
     * @param page     页
     * @param pagesize 每页条数
     * @return ProductList
     */
    @GET("product/list")
    Observable<ProductList> get_ProductList(
            @Query("auth") String auth,
            @Query("vid") String vid,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 获取话费充值订单列表
     *
     * @param vid    村id
     * @param status 状态 0：未付款 1：已付款 2：充值成功 3：充值失败 7:删除 9:全部(不含删除)
     * @param page   页数
     * @return RechargeOrderList
     */
    @GET("recharge/list")
    Observable<RechargeOrderList> get_RechargeList(
            @Query("vid") String vid,
            @Query("status") int status,
            @Query("page") int page);

    /**
     * 获取村实惠订单列表
     *
     * @return SalesOrderList
     */
    @GET("sale/list")
    Observable<SalesOrderList> get_SalesOrderList(
            @Query("auth") String auth,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 获取汽车保险订单列表
     *
     * @return InsuranceOrderList
     */
    @GET("baoxian/list")
    Observable<InsuranceOrderList> get_InsuranceOrderList(
            @Query("auth") String auth,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 获取旅游业务订单列表
     *
     * @return TravelOrderList
     */
    @GET("travel/list")
    Observable<TravelOrderList> get_TravelOrderList(
            @Query("auth") String auth,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 获取App更新
     *
     * @param id        应用ID
     * @param api_token 长度为 32, 用户在 fir 的 api_token
     * @return UpdateAppBack
     */
    @GET("http://api.fir.im/apps/latest/{id}")
    Observable<UpdateAppBack> get_UpdateApp(
            @Path("id") String id,
            @Query("api_token") String api_token);

    /**
     * 通过Head获取App更新的真实下载地址
     *
     * @return ResponseBody
     */
    @HEAD
    Observable<Response<Void>> get_UpdateAppUrl(
            @Url String url);

}
