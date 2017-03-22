package com.ming.slove.mvnew.common.widgets.video;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.common.utils.BaseTools;

/**
 * Created by MingN on 2017/3/17.
 */

public class MyVideoPlayActivity extends AppCompatActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private String video_path;
    private ImageView left_back;
    private android.widget.MediaController mMediaController;
    private VideoView mVideoView;
    private ImageView iv_play;
    private int mPositionWhenPaused = -1;
    public static String VIDEO_PATH = "video_path";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseTools.transparentStatusBar(this);//透明状态栏
        setContentView(R.layout.activity_video_play);

        video_path = getIntent().getStringExtra(VIDEO_PATH);
        left_back = (ImageView) findViewById(R.id.left_back);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        iv_play = (ImageView) findViewById(R.id.iv_play);

        mMediaController = new android.widget.MediaController(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setMediaController(mMediaController);

        left_back.postDelayed(new Runnable() {
            @Override
            public void run() {
                left_back.setVisibility(View.VISIBLE);
            }
        }, 2000);

        left_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        iv_play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mVideoView.start();
                iv_play.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void onStart() {
        (new Thread(new Runnable() {
            public void run() {
                mVideoView.setVideoPath(video_path);
                mVideoView.start();
            }
        })).start();
        super.onStart();
    }

    public void onPause() {
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
        super.onPause();
    }

    public void onResume() {
        if (mPositionWhenPaused >= 0) {
            mVideoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }

        super.onResume();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        iv_play.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
}
