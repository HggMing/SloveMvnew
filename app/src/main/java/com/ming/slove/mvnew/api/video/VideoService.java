package com.ming.slove.mvnew.api.video;

import com.ming.slove.mvnew.model.bean.InRoomPeopleList;
import com.ming.slove.mvnew.model.bean.NewRoomInfo;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.RoomList;

import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 直播相关服务器接口
 */
public interface VideoService {
    /**
     * 1、获取房间列表V
     *
     * @return 返回房间列表
     */
    @GET("live_video/list")
    Observable<RoomList> get_RoomList(
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 2、创建房间V
     *
     * @param auth  认证信息
     * @param title 房间名称
     * @return 推拉流地址等信息
     */
    @Multipart
    @POST("live_video/add_room")
    Observable<NewRoomInfo> post_AddRoom(
            @Part("auth") RequestBody auth,
            @Part("title") RequestBody title,
            @Part("files\"; filename=\"jpg") RequestBody pic);

    /**
     * 3、删除房间V
     *
     * @param room_id 房间id
     * @return Result
     */
    @FormUrlEncoded
    @POST("live_video/del_room")
    Observable<Result> post_DelRoom(
            @Field("auth") String auth,
            @Field("room_id") String room_id);

    /**
     * 4、进入房间V
     *
     * @param auth    认证信息
     * @param room_id room_id:房间号
     * @return Result
     */
    @FormUrlEncoded
    @POST("live_video/come_in_room")
    Observable<Result> post_ComeInRoom(
            @Field("auth") String auth,
            @Field("room_id") String room_id);

    /**
     * 5、离开房间V
     *
     * @param auth    认证信息
     * @param room_id room_id:房间号
     * @return Result
     */
    @FormUrlEncoded
    @POST("live_video/leave_room")
    Observable<Result> post_LeaveRoom(
            @Field("auth") String auth,
            @Field("room_id") String room_id);

    /**
     * 获取房间内人员信息V
     *
     * @param room_id 房间id
     * @return InRoomPeopleList
     */
    @GET("live_video/people_list")
    Observable<InRoomPeopleList> get_PeopleList(
            @Query("room_id") String room_id,
            @Query("page") int page,
            @Query("pagesize") int pagesize);

    /**
     * 直播点赞
     *
     * @param auth    认证信息
     * @param room_id 房间号
     * @return Result
     */
    @FormUrlEncoded
    @POST("live_video/zan")
    Observable<Result> post_LiveZan(
            @Field("auth") String auth,
            @Field("room_id") String room_id);
}
