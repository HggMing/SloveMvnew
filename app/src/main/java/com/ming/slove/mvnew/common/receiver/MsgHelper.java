package com.ming.slove.mvnew.common.receiver;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.utils.NotifyUtil;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.model.bean.FriendDetail;
import com.ming.slove.mvnew.model.bean.JsonMsg_NewFriend;
import com.ming.slove.mvnew.model.bean.JsonMsg_Share;
import com.ming.slove.mvnew.model.bean.MessageList;
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
import com.ming.slove.mvnew.model.event.RefreshTab2Event;
import com.ming.slove.mvnew.tab2.chat.ChatActivity;
import com.ming.slove.mvnew.ui.splash.SplashActivity;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MsgHelper {
    private String me_uid;

    private MsgHelper() {
    }

    public static MsgHelper getInstance() {
        return Singleton.msgHelper;
    }

    //获取消息，并本地保存
    public void getMessageList(final Context context, final boolean isShowNotify) {
        //请求消息
        MyDB.createDb(context);
        me_uid = Hawk.get(APPS.ME_UID, "");
        OtherApi.getService()
                .get_MessageList(me_uid, "yxj", 1)
                .flatMap(new Func1<MessageList, Observable<MessageList.LBean>>() {
                    @Override
                    public Observable<MessageList.LBean> call(MessageList messageList) {
                        List<MessageList.LBean> lBeanList = messageList.getL();
                        Collections.reverse(lBeanList);//列表反向
                        return Observable.from(lBeanList);
                    }
                })
                .filter(new Func1<MessageList.LBean, Boolean>() {
                    @Override
                    public Boolean call(MessageList.LBean lBean) {
                        if ("21".equals(lBean.getCt())) {//添加好友请求消息
                            if ("1".equals(lBean.getFrom())) {
                                String jsonString = lBean.getTxt();
                                JsonMsg_NewFriend newFriendMsg = new Gson().fromJson(jsonString, JsonMsg_NewFriend.class);

                                //好友请求保存到数据库
                                JsonMsg_NewFriend.UinfoBean uinfo = newFriendMsg.getUinfo();
                                String uicon = APPS.BASE_URL + uinfo.getHead();
                                NewFriendModel newFriend = new NewFriendModel(uinfo.getUid(), uicon, uinfo.getUname(), uinfo.getSex(), uinfo.getPhone(), 1);
                                MyDB.insert(newFriend);
                                if (isNewFriendActivity(context)) {
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
                            return false;
                        } else {
                            return true;
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MessageList.LBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MessageList.LBean lBean) {
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

                        String msgString = lBean.getTxt();
                        switch (lBean.getCt()) {
                            case "1":
                                chatMsg.setTxt("[图片]");
                                break;
                            case "2":
                                chatMsg.setTxt("[语音]");
                                break;
                            case "9":
                                chatMsg.setTxt("[拍卖通知]");
                                chatMsg.setJsonString(msgString);
                                break;
                            case "100":
                                JsonMsg_Share shareMsg = new Gson().fromJson(msgString, JsonMsg_Share.class);
                                chatMsg.setTxt("[分享]:\"" + shareMsg.getTitle() + "\"");
                                chatMsg.setJsonString(msgString);
                                break;
                            case "1000"://推广消息
                                chatMsg.setTxt("[推广产品]");
                                String tuiguangListJsonString = "{\"list\":" + msgString + "}";
                                chatMsg.setJsonString(tuiguangListJsonString);
                                break;
                            default:
                                chatMsg.setTxt(msgString);//类型：文字
                                break;
                        }
                        chatMsg.setLink(lBean.getLink());//类型：图片or语音
                        MyDB.insert(chatMsg);//保存到数据库

                        if ("1".equals(lBean.getFrom()) & msgString.length() > 10) {
                            if ("已经同意添加您为好友".equals(msgString.substring(msgString.length() - 10, msgString.length()))) {
                                //对方同意添加好友后，刷新好友列表
                                EventBus.getDefault().post(new RefreshFriendList());
                            }
                        }

                        if (isChatting(context)) {
                            //在聊天界面，不显示通知,不处理徽章显示
                            EventBus.getDefault().post(new NewMsgEvent(chatMsg));
                        } else {
                            //显示消息通知
                            showNotify(chatMsg, context,isShowNotify);
                        }
                    }
                });
    }

    public void showNotify(final ChatMsgModel chatMsg, final Context context, final boolean isShowNotify) {
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
                            makeMsg(context, chatMsg, uid, friendsModel,isShowNotify);
                        }
                    });
        } else {
            makeMsg(context, chatMsg, uid, friend,isShowNotify);
        }
    }

    //处理消息
    private void makeMsg(Context context, ChatMsgModel chatMsg, String uid, FriendsModel friend, boolean isShowNotify) {
        //新消息条数，读取及更新
        int count = friend.getCount() + 1;
        friend.setCount(count);
        MyDB.update(friend);
        //保存动态并刷新
        InstantMsgModel msgModel = new InstantMsgModel(uid, friend.getUicon(), friend.getUname(), chatMsg.getSt(), chatMsg.getTxt(), count);
        MyDB.insert(msgModel);
        EventBus.getDefault().post(new InstantMsgEvent());
        //刷新主页tab2处徽章
        EventBus.getDefault().post(new RefreshTab2Event(count));

        if (isShowNotify) {//是否显示通知消息
            int requestCode = Integer.parseInt(uid);//唯一标识通知
            //点击通知后操作
            Intent intent = new Intent();
            if (isAppRunning(context)) {
                intent.setClass(context, ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(ChatActivity.UID, uid);
                intent.putExtra(ChatActivity.USER_NAME, friend.getUname());
            } else {
                intent.setClass(context, SplashActivity.class);
            }
            PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //构建通知
            int largeIcon = R.mipmap.ic_message_notification;
            int smallIcon = R.mipmap.tab2_btn1;
            String title = friend.getUname();
            String ticker = title + ": " + chatMsg.getTxt();

            String content = "[" + count + "条]" + ": " + chatMsg.getTxt();
            //实例化工具类，并且调用接口
            NotifyUtil notify3 = new NotifyUtil(context, requestCode);
            notify3.notify_msg(pIntent, smallIcon, largeIcon, ticker, title, content, true, true, false);
        }
    }

    //程序是否运行
    private boolean isAppRunning(Context context) {
        String packageName = "com.ming.slove.mvnew";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(100);

        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packageName) || info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    //是否在聊天界面
    private boolean isChatting(Context context) {
        String activityName = "com.ming.slove.mvnew.tab2.chat.ChatActivity";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivityName = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return activityName.equals(runningActivityName);
    }

    //是否在新的朋友页面
    private boolean isNewFriendActivity(Context context) {
        String activityName = "com.ming.slove.mvnew.tab2.newfriend.NewFriendActivity";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivityName = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return activityName.equals(runningActivityName);
    }

    private static class Singleton {
        private static MsgHelper msgHelper = new MsgHelper();
    }
}
