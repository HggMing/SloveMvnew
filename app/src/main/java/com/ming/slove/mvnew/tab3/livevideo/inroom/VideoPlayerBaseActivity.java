package com.ming.slove.mvnew.tab3.livevideo.inroom;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.orhanobut.logger.Logger;
import com.sina.weibo.sdk.api.share.Base;

/**
 * Auto hide and show navigation bar and status bar for API >= 19.
 * Keep screen on.
 */

public class VideoPlayerBaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持唤醒状态,让屏幕保持不暗
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            hideSystemUI();
//        }
    }

    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION//防止导航栏隐藏时内容区域大小发生变化
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN//防止状态栏隐藏时内容区域大小发生变化
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION//隐藏导航栏
                            | View.SYSTEM_UI_FLAG_FULLSCREEN//隐藏状态栏
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);//点击屏幕不弹出虚拟机，只有边缘滑动才响应
        }
    }
}
