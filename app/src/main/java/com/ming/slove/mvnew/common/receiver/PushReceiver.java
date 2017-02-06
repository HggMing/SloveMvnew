package com.ming.slove.mvnew.common.receiver;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.igexin.sdk.PushConsts;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.utils.NotifyUtil;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.model.bean.AddFriendRequest;
import com.ming.slove.mvnew.model.bean.FriendDetail;
import com.ming.slove.mvnew.model.bean.MessageList;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.ShareMsg;
import com.ming.slove.mvnew.model.database.ChatMsgModel;
import com.ming.slove.mvnew.model.database.FriendsModel;
import com.ming.slove.mvnew.model.database.InstantMsgModel;
import com.ming.slove.mvnew.model.database.MyDB;
import com.ming.slove.mvnew.model.database.NewFriendModel;
import com.ming.slove.mvnew.model.event.InstantMsgEvent;
import com.ming.slove.mvnew.model.event.NewFriend2Event;
import com.ming.slove.mvnew.model.event.NewFriendEvent;
import com.ming.slove.mvnew.model.event.NewMsgEvent;
import com.ming.slove.mvnew.model.event.RefreshFriendList;
import com.ming.slove.mvnew.tab2.chat.ChatActivity;
import com.ming.slove.mvnew.ui.splash.SplashActivity;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PushReceiver extends BroadcastReceiver {

    //应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)

    public static StringBuilder payloadData = new StringBuilder();
    private boolean isAppRunning;//程序是否运行
    private boolean isChatting;//是否在聊天界面
    private boolean isNewFriendActivity;//是否在新的朋友页面
    private String me_uid;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    getMessageList(context);
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                String me = Hawk.get(APPS.ME_UID);
                OtherApi.getService().
                        getObservable_RegisterChat(me, 1, "yxj", cid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Result>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Result result) {

                            }
                        });
                break;
            default:
                break;
        }
    }

    //获取消息，并本地保存

    private void getMessageList(final Context context) {
        //请求消息
        MyDB.createDb(context);
        me_uid = Hawk.get(APPS.ME_UID, "");
        OtherApi.getService()
                .get_MessageList(me_uid, "yxj", 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MessageList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MessageList messageList) {
                        //接收到新消息！！
                        List<MessageList.LBean> lBeanList = messageList.getL();
                        Collections.reverse(lBeanList);//列表反向
                        showNotify(context, lBeanList);
                    }
                });
    }

    private void showNotify(final Context context, List<MessageList.LBean> lBeanList) {

        //程序运行状态
        getStatus(context);
        //消息通知
        for (MessageList.LBean lBean : lBeanList) {
            if ("21".equals(lBean.getCt())) {//添加好友请求消息
                if ("1".equals(lBean.getFrom())) {
                    String jsonString = lBean.getTxt();
                    Gson gson = new Gson();
                    AddFriendRequest addFriendRequest = gson.fromJson(jsonString, AddFriendRequest.class);

                    //好友请求保存到数据库
                    AddFriendRequest.UinfoBean uinfo = addFriendRequest.getUinfo();
                    String uicon = APPS.BASE_URL + uinfo.getHead();
                    NewFriendModel newFriend = new NewFriendModel(uinfo.getUid(), uicon, uinfo.getUname(), uinfo.getSex(), uinfo.getPhone(), 1);
                    MyDB.insert(newFriend);
                    if (isNewFriendActivity) {
                        EventBus.getDefault().post(new NewFriend2Event());
                    } else {
                        EventBus.getDefault().post(new NewFriendEvent());
                    }

                    //保存动态并刷新
                    InstantMsgModel msgModel = new InstantMsgModel("-9999",
                            APPS.NEW_FRIEND,
                            "新的朋友",
                            lBean.getSt(),
                            "你有新的好友请求。", 1);
                    MyDB.insert(msgModel);
                    EventBus.getDefault().post(new InstantMsgEvent());
                }
            } else {
                //获取消息保存到数据库
                final ChatMsgModel chatMsg = new ChatMsgModel();
                chatMsg.setType(ChatMsgModel.ITEM_TYPE_LEFT);//接收消息
                if ("1".equals(lBean.getFrom())) {
                    chatMsg.setFrom("10001");//系统消息由"我们村客服"发来
                } else {
                    chatMsg.setFrom(lBean.getFrom());//消息来源用户id
                }
                chatMsg.setTo(me_uid);
                chatMsg.setSt(lBean.getSt());//消息时间
                chatMsg.setCt(lBean.getCt());//消息类型
                switch (lBean.getCt()) {
                    case "1":
                        chatMsg.setTxt("[图片]");
                        break;
                    case "2":
                        chatMsg.setTxt("[语音]");
                        break;
                    case "100":
                        String jsonString = lBean.getTxt();
                        Gson gson = new Gson();
                        ShareMsg shareMsg = gson.fromJson(jsonString, ShareMsg.class);

                        chatMsg.setTxt("[分享]:\"" + shareMsg.getTitle() + "\"");

                        chatMsg.setShareMsg(shareMsg.getTitle(), shareMsg.getDetail(), shareMsg.getImage(), shareMsg.getLink());
                        break;
                    default:
                        chatMsg.setTxt(lBean.getTxt());//类型：文字
                        break;
                }
                chatMsg.setLink(lBean.getLink());//类型：图片or语音
                MyDB.insert(chatMsg);//保存到数据库


                String t = lBean.getTxt();
                if ("1".equals(lBean.getFrom()) & t.length() > 10) {
                    if ("已经同意添加您为好友".equals(t.substring(t.length() - 10, t.length()))) {
                        //对方同意添加好友后，刷新好友列表
                        EventBus.getDefault().post(new RefreshFriendList());
                    }
                }

                if (isChatting) {
                    //在聊天界面，不显示通知
                    EventBus.getDefault().post(new NewMsgEvent(chatMsg));
                } else {
                    final String uid = chatMsg.getFrom();
                    final FriendsModel friend = MyDB.createDb(context).queryById(uid, FriendsModel.class);
                    if (friend == null) {//如果消息来自非好友（新增：客户联系店长）
                        String auth = Hawk.get(APPS.USER_AUTH);
                        OtherApi.getService().get_FriendDetail(auth, uid)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<FriendDetail>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(FriendDetail friendDetail) {
                                        FriendDetail.DataBean.UserinfoBean userinfoBean = friendDetail.getData().getUserinfo();
                                        //存储陌生消息人信息到本地好友数据库，标识isFriend为false
                                        String save_uicon = APPS.BASE_URL + userinfoBean.getHead();
                                        String save_uname = userinfoBean.getUname();//昵称
                                        String iphone = userinfoBean.getPhone();
                                        if (StringUtils.isEmpty(save_uname)) {
                                            save_uname = iphone;//昵称为空，显示手机号
                                        }
                                        FriendsModel friendsModel = new FriendsModel(uid, save_uname, save_uicon, false);
                                        MyDB.insert(friendsModel);
                                        //处理后续消息
                                        makeMsg(context, chatMsg, uid, friendsModel);
                                    }
                                });
                    } else {
                        makeMsg(context, chatMsg, uid, friend);
                    }
                }
            }
        }
    }

    //处理消息
    private void makeMsg(Context context, ChatMsgModel chatMsg, String uid, FriendsModel friend) {
        int requestCode = Integer.parseInt(uid);//唯一标识通知
        //点击通知后操作
        Intent intent2 = new Intent();
        if (isAppRunning) {
            intent2.setClass(context, ChatActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent2.putExtra(ChatActivity.UID, uid);
            intent2.putExtra(ChatActivity.USER_NAME, friend.getUname());
        } else {
            intent2.setClass(context, SplashActivity.class);
        }
        PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        //构建通知
        int largeIcon = R.mipmap.ic_message_notification;
        int smallIcon = R.mipmap.tab2_btn1;
        String title = friend.getUname();
        String ticker = title + ": " + chatMsg.getTxt();
        //新消息条数，读取及更新
        int count = friend.getCount() + 1;
        friend.setCount(count);
        MyDB.update(friend);
        String content = "[" + count + "条]" + ": " + chatMsg.getTxt();
        //实例化工具类，并且调用接口
        NotifyUtil notify3 = new NotifyUtil(context, requestCode);
        notify3.notify_msg(pIntent, smallIcon, largeIcon, ticker, title, content, true, true, false);

        //保存动态并刷新
        InstantMsgModel msgModel = new InstantMsgModel(uid, friend.getUicon(), title, chatMsg.getSt(), chatMsg.getTxt(), count);
        MyDB.insert(msgModel);
        EventBus.getDefault().post(new InstantMsgEvent());
    }

    //获取程序运行状态

    private void getStatus(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //程序是否运行
        isAppRunning = false;
        String PackageName = "com.ming.slove.mvnew";
        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(PackageName) || info.baseActivity.getPackageName().equals(PackageName)) {
                isAppRunning = true;
                break;
            }
        }
        //是否在聊天界面
        isChatting = false;
        String activityName = "com.ming.slove.mvnew.tab2.chat.ChatActivity";
        String runningActivityName = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        if (activityName.equals(runningActivityName)) {
            isChatting = true;
        }
        //是否在新的朋友页面
        isNewFriendActivity = false;
        String activityName2 = "com.ming.slove.mvnew.tab2.newfriend.NewFriendActivity";
        if (activityName2.equals(runningActivityName)) {
            isNewFriendActivity = true;
        }
    }
}
