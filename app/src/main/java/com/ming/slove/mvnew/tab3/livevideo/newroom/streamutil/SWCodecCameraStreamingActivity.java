package com.ming.slove.mvnew.tab3.livevideo.newroom.streamutil;

import android.os.Bundle;

import com.ming.slove.mvnew.R;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.WatermarkSetting;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;

/**
 * 软编流传输
 */
public class SWCodecCameraStreamingActivity extends StreamingBaseActivity {
    private static final String TAG = "SWCodecCameraStreaming";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
        afl.setShowMode(AspectFrameLayout.SHOW_MODE.REAL);
        CameraPreviewFrameView cameraPreviewFrameView =
                (CameraPreviewFrameView) findViewById(R.id.cameraPreview_surfaceView);
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
}
