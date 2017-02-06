package com.ming.slove.mvnew.tab3.livevideo.inroom;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintTextView;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.video.VideoApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.MyItemDecoration;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.common.widgets.video.FullLiveVideoActivity;
import com.ming.slove.mvnew.common.widgets.video.MediaController;
import com.ming.slove.mvnew.common.widgets.video.Utils;
import com.ming.slove.mvnew.model.bean.InRoomPeopleList;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.RoomList;
import com.orhanobut.hawk.Hawk;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ming on 2016/3/3.
 */
public class VideoRoomDetailActivity extends BackActivity {
    @Bind(R.id.icon)
    ImageView imgHead;
    @Bind(R.id.content)
    TextView tvContent;
    @Bind(R.id.name)
    TintTextView tvName;
    @Bind(R.id.number)
    TextView tvNumber;
    @Bind(R.id.m_x_recyclerview)
    XRecyclerView mXRecyclerView;

    @Bind(R.id.VideoView)
    PLVideoTextureView mVideoView;
    @Bind(R.id.CoverView)
    ImageView mCoverView;
    @Bind(R.id.LoadingView)
    LinearLayout mLoadingView;


    private InRoomPeopleListAdapter mAdapter;
    List<InRoomPeopleList.DataBean.ListBean> mList = new ArrayList<>();

    final private static int PAGE_SIZE = 10;
    private int page = 1;
    private String roomId;//房间id

    private String auth;
    private int peopleNum;//房间内总人数

    public static final String VIDEO_ROOM_INFO = "video_room_info";//直播房间相关信息
    public static final String VIDEO_ROOM_OWNER = "video_room_owner";//主播头像地址
    private RoomList.DataBean.ListBean roomInfo;
    private String headUrl;

    //播放器相关
    private static final int MESSAGE_ID_RECONNECTING = 0x01;

    private MediaController mMediaController;
    private Toast mToast = null;
    private String mVideoPath = null;
    private boolean mIsActivityPaused = true;
    private int mIsLiveStreaming = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_room_detail);
        ButterKnife.bind(this);

        initView();
        initVideoView();//初始化播放器
        initData();
    }

    private void initView() {
        //关闭当前页，滑动返回
        SwipeBackHelper.getCurrentPage(this)//获取当前页面
                .setSwipeBackEnable(false);//设置是否可滑动
        //数据初始化
        auth = Hawk.get(APPS.USER_AUTH);
        roomInfo = getIntent().getParcelableExtra(VIDEO_ROOM_INFO);
        roomId = roomInfo.getRoom_id();
        headUrl = getIntent().getStringExtra(VIDEO_ROOM_OWNER);

        setToolbarTitle(roomInfo.getName() + "的直播间");

        //主播头像
        Glide.with(this).load(headUrl)
                .bitmapTransform(new CropCircleTransformation(this))
                .error(R.mipmap.defalt_user_circle)
                .into(imgHead);
        //主播名字
        String name = roomInfo.getName();
        tvName.setText(name);
        //标题内容显示
        String content = roomInfo.getTitle();
        tvContent.setText(content);
        //人数显示
        tvNumber.setText(peopleNum + "人正在看");
        //播放直播
        //封面图片
        String url = APPS.BASE_URL + roomInfo.getPic_1();
//        Glide.with(this).load(url)
//                .centerCrop()
//                .into(mCoverView);

        //配置显示房间人员
        mAdapter = new InRoomPeopleListAdapter();
        mXRecyclerView.setAdapter(mAdapter);//设置adapter

        mXRecyclerView.setLayoutManager(new LinearLayoutManager(this));//设置布局管理器
        mXRecyclerView.addItemDecoration(new MyItemDecoration(this));//添加分割线
//        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画

        mXRecyclerView.setPullRefreshEnabled(false);//关闭刷新功能
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);

        if (peopleNum <= PAGE_SIZE) {
            mXRecyclerView.setLoadingMoreEnabled(false);//评论数少于一页，不显示加载更多
        }

        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
//                mXRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                getDataList(++page);
                mXRecyclerView.loadMoreComplete();
            }
        });
    }

    private void initData() {
        setInRoom();//确认进入房间
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
                        if (result.getErr() == 0) {
                            getDataList(page);
                        } else {
                            Toast.makeText(VideoRoomDetailActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getDataList(int page) {
        VideoApi.getService()
                .get_PeopleList(roomId, page, PAGE_SIZE)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InRoomPeopleList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(InRoomPeopleList inRoomPeopleList) {
                        peopleNum = Integer.parseInt(inRoomPeopleList.getData().getCnt());
                        tvNumber.setText(peopleNum + "人正在看");

                        mList.addAll(inRoomPeopleList.getData().getList());
                        mAdapter.setItem(mList);
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
                                finish();
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

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Result result) {
                        if (result.getErr() == 0) {
                            setResult(RESULT_OK);
                            Toast.makeText(VideoRoomDetailActivity.this, "你已成功退出直播间", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VideoRoomDetailActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initVideoView() {
        mVideoView.setBufferingIndicator(mLoadingView);
        mLoadingView.setVisibility(View.VISIBLE);

        mVideoView.setCoverView(mCoverView);

        //如果需要修复显示的方向
        // if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        //     mVideoView.setPreviewOrientation(0);
        // }
        // else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        //     mVideoView.setPreviewOrientation(270);
        // }

//        mVideoPath = getIntent().getStringExtra("videoPath");//播放地址
//        mVideoPath = "rtmp://live.hkstv.hk.lxdns.com/live/hks";  //测试播放
        mVideoPath= roomInfo.getUrl();
        mIsLiveStreaming = getIntent().getIntExtra("liveStreaming", 1);//默认 直播
        int codec = getIntent().getIntExtra("mediaCodec", AVOptions.MEDIA_CODEC_AUTO);//默认解码方式 自动
        //播放参数配置
        setOptions(codec);

        // 翻转显示（镜像显示）
        // mVideoView.setMirror(true);

        //播放结束后会自动重新开始,循环播放
        //mVideoView.setLooping(true);

        // 媒体控制面板，可自定义，直播暂时屏蔽
//        mMediaController = new MediaController(this, false, mIsLiveStreaming == 1);
//        mVideoView.setMediaController(mMediaController);
        //播放完成回调
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        //播放错误回调
        mVideoView.setOnErrorListener(mOnErrorListener);
        //开始播放
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
                mToast = Toast.makeText(VideoRoomDetailActivity.this, tips, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
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
            if (!Utils.isNetworkAvailable(VideoRoomDetailActivity.this)) {
                sendReconnectMessage();
                return;
            }
            mVideoView.setVideoPath(mVideoPath);
            mVideoView.start();
        }
    };

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
     * 全屏播放屏幕
     */
    public void onClickFullScreen(View v) {
       Intent intent=new Intent(this, FullLiveVideoActivity.class);
        intent.putExtra(FullLiveVideoActivity.THE_VIDEO_PATH, mVideoPath);
        startActivity(intent);
    }
}

