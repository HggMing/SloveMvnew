package com.ming.slove.mvnew.tab3.livevideo.newroom;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.video.VideoApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.utils.SocketTools;
import com.ming.slove.mvnew.common.widgets.dialog.Dialog_ShareBottom;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.common.widgets.likeview.PeriscopeLayout;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.ShareInfo;
import com.ming.slove.mvnew.model.bean.SocketData;
import com.ming.slove.mvnew.model.event.SocketDataEvent;
import com.ming.slove.mvnew.tab3.livevideo.inroom.EmptyFragment;
import com.ming.slove.mvnew.tab3.livevideo.inroom.LiveMsgFragment;
import com.ming.slove.mvnew.tab3.livevideo.newroom.streamutil.CameraPreviewFrameView;
import com.ming.slove.mvnew.tab3.livevideo.newroom.streamutil.Config;
import com.ming.slove.mvnew.tab3.livevideo.newroom.streamutil.StreamingBaseActivity;
import com.ming.slove.mvnew.ui.main.MyViewPager;
import com.ming.slove.mvnew.ui.main.MyViewPagerAdapter;
import com.orhanobut.hawk.Hawk;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.WatermarkSetting;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketResponsePacket;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 软编流传输
 */
public class LiveCameraStreamingActivity extends StreamingBaseActivity {
    private String roomId;//房间号
    private SocketClient mSocketclient;

    private PeriscopeLayout periscope;//点赞动画显示区域


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVideoView();
        initView();
        initData();
    }

    private void initView() {
        //view初始化
        MyViewPager viewPager = (MyViewPager) findViewById(R.id.view_pager_live);
        periscope = (PeriscopeLayout) findViewById(R.id.periscope);
        final RelativeLayout lyControlUI = (RelativeLayout) findViewById(R.id.ly_control_ui);
        ImageView btnLike = (ImageView) findViewById(R.id.btn_like);
        final ImageView btnClose = (ImageView) findViewById(R.id.btn_close);
        ImageView btnShare = (ImageView) findViewById(R.id.btn_share);

        //数据初始化
        roomId = getIntent().getStringExtra(Config.EXTRA_KEY_ROOM_ID);

        //其他界面的fragment配置
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new LiveMsgFragment());
        fragments.add(new EmptyFragment());

        viewPager.setSlipping(true);//设置ViewPager是否可以滑动
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setAdapter(new MyViewPagerAdapter(fragments, getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        lyControlUI.setVisibility(View.GONE);
                        btnClose.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        lyControlUI.setVisibility(View.VISIBLE);
                        btnClose.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //向fragment_1传递数据
        String headUrl = Hawk.get(APPS.ME_HEAD);
        String name = Hawk.get(APPS.ME_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(LiveMsgFragment.VIDEO_ROOM_ID, roomId);
        bundle.putString(LiveMsgFragment.VIDEO_ROOM_OWNER_HEAD, headUrl);
        bundle.putString(LiveMsgFragment.VIDEO_ROOM_OWNER_NAME, name);
        fragments.get(0).setArguments(bundle);

        //点赞
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送点赞信息
                String msg = "{\"type\":\"room_zan_change\",\"room_id\":\"" + roomId + "\"}";
                if (mSocketclient != null)
                    mSocketclient.sendString(msg);
            }
        });
        //退出
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //分享
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //分享链接的网址
                ShareInfo shareInfo=getIntent().getParcelableExtra(Config.EXTRA_KEY_SHARE_INFO);

                Dialog_ShareBottom dialog = new Dialog_ShareBottom();
                dialog.setShareContent(shareInfo.getTitle(),shareInfo.getSummary(),shareInfo.getTargetUrl(),shareInfo.getThumb());
                dialog.show(getSupportFragmentManager());
            }
        });
    }

    private void initVideoView() {
        AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
        afl.setShowMode(AspectFrameLayout.SHOW_MODE.FULL);
        CameraPreviewFrameView cameraPreviewFrameView = (CameraPreviewFrameView) findViewById(R.id.cameraPreview_surfaceView);
        cameraPreviewFrameView.setListener(this);

        //水印添加
        WatermarkSetting watermarksetting = null;
//        WatermarkSetting watermarksetting = new WatermarkSetting(this);
//        watermarksetting.setResourceId(R.drawable.qiniu_logo)
//                .setAlpha(100)
//                .setSize(WatermarkSetting.WATERMARK_SIZE.MEDIUM)
//                .setCustomPosition(0.5f, 0.5f);

        //核心类，所有音视频推流相关的具体操作，都在 MediaStreamingManager 中进行。
        mMediaStreamingManager = new MediaStreamingManager(this, afl, cameraPreviewFrameView,
                AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC); // 编码方式修改（视频软编，音频软编）

//        mMediaStreamingManager.prepare(mCameraStreamingSetting, mMicrophoneStreamingSetting, watermarksetting, mProfile, new PreviewAppearance(0.0f, 0.0f, 0.5f, 0.5f, PreviewAppearance.ScaleType.FIT));
        mMediaStreamingManager.prepare(mCameraStreamingSetting, mMicrophoneStreamingSetting, watermarksetting, mProfile);

        mMediaStreamingManager.setStreamingStateListener(this);
        mMediaStreamingManager.setSurfaceTextureCallback(this);
        mMediaStreamingManager.setStreamingSessionListener(this);
//        mMediaStreamingManager.setNativeLoggingEnabled(false);
        mMediaStreamingManager.setStreamStatusCallback(this);
        mMediaStreamingManager.setAudioSourceCallback(this);
        // update the StreamingProfile
//        mProfile.setStream(new Stream(mJSONObject1));
//        mMediaStreamingManager.setStreamingProfile(mProfile);
        setFocusAreaIndicator();
    }

    @Override
    public void onBackPressed() {
        MyDialog.Builder builder = new MyDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("是否退出直播,并关闭此直播间?")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delRoom();
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

    private void delRoom() {
        String auth = Hawk.get(APPS.USER_AUTH);
        VideoApi.getService()
                .post_DelRoom(auth, roomId)
                .subscribeOn(Schedulers.io())
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
                            Toast.makeText(LiveCameraStreamingActivity.this, "直播结束", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LiveCameraStreamingActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void initData() {
        new SocketTools().connect(new SocketClientDelegate() {
            @Override
            public void onConnected(SocketClient client) {
                //登录系统,发送登录系统请求
                String phone = Hawk.get(APPS.KEY_LOGIN_NAME);
                String msg = "{\"type\":\"login_chat\",\"phone\":\"" + phone + "\"}";
                client.sendString(msg);
                mSocketclient = client;
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
                        break;
                    case "room_zan_change"://房间点赞数量
                        periscope.addHeart();
                        break;
                    case "close_room"://直播结束
                        break;
                    default:
                        EventBus.getDefault().post(new SocketDataEvent(socketData, client));
                        break;
                }
            }
        });
    }
}
