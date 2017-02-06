package com.ming.slove.mvnew.common.widgets.video;

import android.content.Context;
import android.content.DialogInterface;
import android.media.session.*;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by MingN on 2017/1/13.
 */

public class MyVideoPlayer extends JCVideoPlayerStandard {
    public MyVideoPlayer(Context context) {
        super(context);
    }

    public MyVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_my_video_player;
    }

    @Override
    public void showWifiDialog() {
        MyDialog.Builder builder = new MyDialog.Builder(getContext());
        builder.setMessage("您当前正在使用移动网络，继续播放将消耗流量")
                .setPositiveButton("继续播放",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startPlayLogic();
                                WIFI_TIP_DIALOG_SHOWED = true;
                            }
                        })
                .setNegativeButton("停止播放", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        backPress();
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}
