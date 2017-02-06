package com.ming.slove.mvnew.common.widgets.video;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ming.slove.mvnew.R;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

/**
 * 播放直播页面
 */
public class FullLiveVideoActivity extends VideoPlayerBaseActivity {

    private static final int MESSAGE_ID_RECONNECTING = 0x01;

    private MediaController mMediaController;
    private PLVideoTextureView mVideoView;
    private Toast mToast = null;
    private String mVideoPath = null;
    private int mRotation = 0;
    private int mDisplayAspectRatio = PLVideoTextureView.ASPECT_RATIO_FIT_PARENT; //default
    private View mLoadingView;
    private View mCoverView = null;
    private boolean mIsActivityPaused = true;
    private int mIsLiveStreaming = 1;

    public static String THE_VIDEO_PATH = "the_video_path";

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_video);
        mVideoView = (PLVideoTextureView) findViewById(R.id.VideoView);
        mLoadingView = findViewById(R.id.LoadingView);
        mVideoView.setBufferingIndicator(mLoadingView);
        mLoadingView.setVisibility(View.VISIBLE);
        mCoverView = (ImageView) findViewById(R.id.CoverView);
        mVideoView.setCoverView(mCoverView);

        //如果需要修复显示的方向
        // if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        //     mVideoView.setPreviewOrientation(0);
        // }
        // else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        //     mVideoView.setPreviewOrientation(270);
        // }

        mVideoPath = getIntent().getStringExtra(THE_VIDEO_PATH);//播放地址
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
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.start();
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
        mRotation = (mRotation + 90) % 360;
        mVideoView.setDisplayOrientation(mRotation);
    }

    /**
     * 切换识图显示(适应屏幕、拉伸、16:9、4:3)
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
     * 关闭全屏页面
     */
    public void onClickClose(View v) {
        finish();
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
                mToast = Toast.makeText(FullLiveVideoActivity.this, tips, Toast.LENGTH_SHORT);
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
            if (!Utils.isNetworkAvailable(FullLiveVideoActivity.this)) {
                sendReconnectMessage();
                return;
            }
            mVideoView.setVideoPath(mVideoPath);
            mVideoView.start();
        }
    };
}
