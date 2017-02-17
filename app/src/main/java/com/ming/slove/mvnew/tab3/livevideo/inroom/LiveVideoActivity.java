package com.ming.slove.mvnew.tab3.livevideo.inroom;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.video.VideoApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.utils.SocketTools;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.common.widgets.likeview.PeriscopeLayout;
import com.ming.slove.mvnew.common.widgets.video.Utils;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.RoomList;
import com.ming.slove.mvnew.model.bean.SocketData;
import com.ming.slove.mvnew.model.event.SocketDataEvent;
import com.ming.slove.mvnew.ui.main.MyViewPager;
import com.orhanobut.hawk.Hawk;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketResponsePacket;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 播放直播页面
 */
public class LiveVideoActivity extends VideoPlayerBaseActivity {
    @Bind(R.id.VideoView)
    PLVideoTextureView mVideoView;
    @Bind(R.id.CoverView)
    ImageView mCoverView;
    @Bind(R.id.LoadingView)
    LinearLayout mLoadingView;
    @Bind(R.id.view_pager_live)
    MyViewPager viewPager;

    @Bind(R.id.periscope)
    PeriscopeLayout periscope;
    @Bind(R.id.btn_like)
    ImageView btnLike;
    @Bind(R.id.play_end)
    TextView tvPlayEnd;


    private static final int MESSAGE_ID_RECONNECTING = 0x01;

    private Toast mToast = null;
    private String mVideoPath = null;
    private int mRotation = 0;
    private int mDisplayAspectRatio = PLVideoTextureView.ASPECT_RATIO_FIT_PARENT; //default
    private boolean mIsActivityPaused = true;
    private int mIsLiveStreaming = 1;

    private String roomId;//房间id
    private String auth;

    public static final String VIDEO_ROOM_INFO = "video_room_info";//直播房间信息
    public static final String VIDEO_ROOM_OWNER_HEAD = "video_room_owner_head";//主播头像地址
    private RoomList.DataBean.ListBean roomInfo;

    private List<Fragment> fragments = new ArrayList<>();
    private FragmentManager fragmentManager;

    private SocketClient mSocketclient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_video);
        ButterKnife.bind(this);

        initView();
        initVideoView();
        initData();
    }

    private void initView() {
        //数据初始化
        roomInfo = getIntent().getParcelableExtra(VIDEO_ROOM_INFO);

        auth = Hawk.get(APPS.USER_AUTH);
        roomId = roomInfo.getRoom_id();

        //封面图片
        String url = APPS.BASE_URL + roomInfo.getPic_1();
        Glide.with(this).load(url)
                .centerCrop()
                .into(mCoverView);

        //其他界面的fragment配置
        fragments.add(new LiveMsgFragment());
        fragments.add(new EmptyFragment());

        fragmentManager = getSupportFragmentManager();

        viewPager.setSlipping(true);//设置ViewPager是否可以滑动
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new MyPagerAdapter());

        //主播头像和名字
        String headUrl = getIntent().getStringExtra(VIDEO_ROOM_OWNER_HEAD);
        String name = roomInfo.getName();
        if (StringUtils.isEmpty(name)) {
            //若用户名为空，显示手机号，中间四位为*
            String iphone = roomInfo.getAccount();
            if (!StringUtils.isEmpty(iphone)) {
                name = iphone.substring(0, 3) + "****" + iphone.substring(7, 11);
            } else {
                name = "匿名";
            }
        }
        //向fragment_1传递数据
        Bundle bundle = new Bundle();
        bundle.putString(LiveMsgFragment.VIDEO_ROOM_ID, roomId);
        bundle.putString(LiveMsgFragment.VIDEO_ROOM_OWNER_HEAD, headUrl);
        bundle.putString(LiveMsgFragment.VIDEO_ROOM_OWNER_NAME, name);
        fragments.get(0).setArguments(bundle);
    }

    /**
     * 填充ViewPager的数据适配器
     */
    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(fragments.get(position).getView());
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = fragments.get(position);
            if (!fragment.isAdded()) { // 如果fragment还没有added
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.add(fragment, fragment.getClass().getSimpleName());
                ft.commit();
                fragmentManager.executePendingTransactions();
            }

            if (fragment.getView().getParent() == null) {
                container.addView(fragment.getView()); // 为viewpager增加布局
            }
            return fragment.getView();
        }
    }

    private void initVideoView() {
        mVideoView.setBufferingIndicator(mLoadingView);
        mLoadingView.setVisibility(View.VISIBLE);
        mVideoView.setCoverView(mCoverView);
        //播放视图显示设置
        mVideoView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);//设为拉伸，铺满屏幕

        //如果需要修复显示的方向
        // if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        //     mVideoView.setPreviewOrientation(0);
        // }
        // else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        //     mVideoView.setPreviewOrientation(270);
        // }

        mIsLiveStreaming = getIntent().getIntExtra("liveStreaming", 1);//默认 直播
        int codec = getIntent().getIntExtra("mediaCodec", AVOptions.MEDIA_CODEC_AUTO);//默认解码方式 自动
        //播放参数配置
        setOptions(codec);

        // 翻转显示（镜像显示）
        // mVideoView.setMirror(true);

        //播放结束后会自动重新开始,循环播放
        //mVideoView.setLooping(true);

        // You can also use a custom `MediaController` widget
//        mMediaController = new MediaController(this, false, mIsLiveStreaming == 1);
//        mVideoView.setMediaController(mMediaController);
        //播放完成回调
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        //播放错误回调
        mVideoView.setOnErrorListener(mOnErrorListener);
        //开始播放
        mVideoPath = roomInfo.getUrl();//播放地址
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.start();
    }

    private void setOptions(int codecType) {
        AVOptions options = new AVOptions();

       /* 解码方式:AVOptions.MEDIA_CODEC_HW_DECODE，硬解
       AVOptions.MEDIA_CODEC_SW_DECODE, 软解
        AVOptions.MEDIA_CODEC_AUTO, 硬解优先，失败后自动切换到软解*/
        options.setInteger(AVOptions.KEY_MEDIACODEC, codecType);

        // 准备超时时间，包括创建资源、建立连接、请求码流等，单位是 ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        // 读取视频流超时时间，单位是 ms
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);

        // 当前播放的是否为在线直播，如果是，则底层会有一些播放优化
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, mIsLiveStreaming);
        if (mIsLiveStreaming == 1) {
            // 是否开启"延时优化"，只在在线直播流中有效// 默认值是：0
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        }

        // 默认的缓存大小，单位是 ms  // 默认值是：2000
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 2000);
        // 最大的缓存大小，单位是 ms// 默认值是：4000
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 4000);

        // 播放前最大探测流的字节数，单位是 byte// 默认值是：128 * 1024
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);

        // 是否自动启动播放，如果设置为 1，则在调用 `prepareAsync` 或者 `setVideoPath` 之后自动启动播放，无需调用 `start()` // 默认值是：1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        // 在开始播放之前配置
        mVideoView.setAVOptions(options);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mToast = null;
        mVideoView.pause();
        mIsActivityPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsActivityPaused = false;
        mVideoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

    /**
     * 旋转屏幕
     */
    public void onClickRotate(View v) {
        mRotation = (mRotation + 1) % 3;
        switch (mRotation) {
            case 0:
                mVideoView.setDisplayOrientation(0);
                break;
            case 1:
                mVideoView.setDisplayOrientation(90);
                break;
            case 2:
                mVideoView.setDisplayOrientation(270);
                break;
        }
    }

    /**
     * 切换视图显示(适应屏幕、拉伸、16:9、4:3)
     */
    public void onClickSwitchScreen(View v) {
        mDisplayAspectRatio = (mDisplayAspectRatio + 1) % 5;
        mVideoView.setDisplayAspectRatio(mDisplayAspectRatio);
        switch (mVideoView.getDisplayAspectRatio()) {
            case PLVideoTextureView.ASPECT_RATIO_ORIGIN:
                showToastTips("原始");
                break;
            case PLVideoTextureView.ASPECT_RATIO_FIT_PARENT:
                showToastTips("适应屏幕");
                break;
            case PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT:
                showToastTips("拉伸");
                break;
            case PLVideoTextureView.ASPECT_RATIO_16_9:
                showToastTips("16:9");
                break;
            case PLVideoTextureView.ASPECT_RATIO_4_3:
                showToastTips("4:3");
                break;
            default:
                break;
        }
    }

    /**
     * 关闭页面
     */
    public void onClickClose(View v) {
        onBackPressed();
    }

    /**
     * 点赞
     */
    @OnClick(R.id.btn_like)
    public void onClick() {
        //发送点赞信息
        String msg = "{\"type\":\"room_zan_change\",\"room_id\":\"" + roomId + "\"}";
        if (mSocketclient != null)
            mSocketclient.sendString(msg);
    }

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            boolean isNeedReconnect = false;
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    showToastTips("无效的URL!");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    showToastTips("播放资源不存在!");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    showToastTips("服务器拒绝连接!");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    showToastTips("连接超时!");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    showToastTips("空的播放列表!");
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    showToastTips("与服务器连接断开!");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    showToastTips("网络异常!");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    showToastTips("未授权，播放一个禁播的流!");
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    showToastTips("播放器准备超时!");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    showToastTips("读取数据超时!");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_HW_DECODE_FAILURE:
//                    showToastTips("硬解码失败!");
                    setOptions(AVOptions.MEDIA_CODEC_SW_DECODE);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    showToastTips("未知错误!");
                    break;
                default:
                    showToastTips("未知错误!");
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
            if (isNeedReconnect) {
                sendReconnectMessage();
            } else {
                finish();
            }
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            showToastTips("已播放完成!");
            finish();
        }
    };

    private void showToastTips(final String tips) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(LiveVideoActivity.this, tips, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    private void sendReconnectMessage() {
        showToastTips("正在重连...");
        mLoadingView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 500);
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != MESSAGE_ID_RECONNECTING) {
                return;
            }
            if (mIsActivityPaused || !Utils.isLiveStreamingAvailable()) {
                finish();
                return;
            }
            if (!Utils.isNetworkAvailable(LiveVideoActivity.this)) {
                sendReconnectMessage();
                return;
            }
            mVideoView.setVideoPath(mVideoPath);
            mVideoView.start();
        }
    };

    private void initData() {
        createScoket(); //建立长连接
        setInRoom();//确认进入房间
    }

    private void createScoket() {
        new SocketTools().connect(new SocketClientDelegate() {
            @Override
            public void onConnected(SocketClient client) {
                //登录系统,发送登录系统请求
                String phone = Hawk.get(APPS.KEY_LOGIN_NAME);
                String msg = "{\"type\":\"login_chat\",\"phone\":\"" + phone + "\"}";
                client.sendString(msg);
            }

            @Override
            public void onDisconnected(final SocketClient client) {
                // 可在此实现自动重连
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(3 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        client.connect();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                    }
                }.execute();
            }

            @Override
            public void onResponse(SocketClient client, @NonNull SocketResponsePacket responsePacket) {
                if (responsePacket.isHeartBeat()) {
                    return;
                }
                String msg = responsePacket.getMessage();// 获取接收消息
                Gson gson = new Gson();
                SocketData socketData = gson.fromJson(msg, SocketData.class);

                switch (socketData.getOp_type()) {
                    case "new_connect"://建立socket
                        break;
                    case "login_chat"://登录成功
                        mSocketclient = client;
                        break;
                    case "room_zan_change"://房间点赞动画
                        periscope.addHeart();
                        break;
                    case "close_room"://直播结束
                        mVideoView.pause();
                        mVideoView.setOnErrorListener(null);//关闭错误监听
                        tvPlayEnd.setVisibility(View.VISIBLE);
                        break;
                    default:
                        EventBus.getDefault().post(new SocketDataEvent(socketData, client));
                        break;
                }
            }
        });
    }

    private void setInRoom() {
        VideoApi.getService()
                .post_ComeInRoom(auth, roomId)
                .subscribeOn(Schedulers.newThread())
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
                        if (result.getErr() != 0) {
                            Toast.makeText(LiveVideoActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        MyDialog.Builder builder = new MyDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("是否退出当前直播间?")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                leaveRoom();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    //离开房间
    private void leaveRoom() {
        VideoApi.getService()
                .post_LeaveRoom(auth, roomId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    @Override
                    public void onCompleted() {
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        finish();
                    }

                    @Override
                    public void onNext(Result result) {
                        if (result.getErr() == 0) {
                            setResult(RESULT_OK);
                            Toast.makeText(LiveVideoActivity.this, "你已成功退出直播间", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LiveVideoActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
