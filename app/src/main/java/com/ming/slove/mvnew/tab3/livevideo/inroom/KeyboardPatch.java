package com.ming.slove.mvnew.tab3.livevideo.inroom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.orhanobut.logger.Logger;

import java.lang.reflect.Method;

/**
 * 手动处理键盘弹出后的界面问题
 * Created by MingN on 2017/2/15.
 */

public class KeyboardPatch {
    private Activity activity;
    private View decorView;
    private View contentView;
    private LiveMsgFragment fragment;

    /**
     * 构造函数
     *
     * @param act         需要解决bug的activity
     * @param contentView 界面容器，activity中一般是R.id.content，也可能是Fragment的容器，根据个人需要传递
     */
    public KeyboardPatch(Activity act, View contentView) {
        this.activity = act;
        this.decorView = act.getWindow().getDecorView();
        this.contentView = contentView;
    }

    /**
     * 为处理直播聊天界面，专用
     * @param act
     * @param contentView
     * @param fragment
     */
    public KeyboardPatch(Activity act, View contentView, LiveMsgFragment fragment) {
        this.activity = act;
        this.decorView = act.getWindow().getDecorView();
        this.contentView = contentView;
        this.fragment = fragment;
    }

    /**
     * 默认处理acitivity
     * @param act Activity
     */
    public KeyboardPatch(Activity act) {
        View contentViewActivity = ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
        this.activity = act;
        this.decorView = act.getWindow().getDecorView();
        this.contentView = contentViewActivity;
    }

    /**
     * 监听layout变化
     */
    public void enable() {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (Build.VERSION.SDK_INT >= 19) {
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }

    /**
     * 取消监听
     */
    public void disable() {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (Build.VERSION.SDK_INT >= 19) {
            decorView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();

            decorView.getWindowVisibleDisplayFrame(r);
            int height = decorView.getContext().getResources().getDisplayMetrics().heightPixels;
            int diff = height - r.bottom;

            if (diff > 0) {//弹出键盘
                if (contentView.getPaddingBottom() != diff) {
                    contentView.setPadding(0, 0, 0, diff);
                }
            } else {//关闭键盘
                if (contentView.getPaddingBottom() != 0) {
                    if (fragment != null) {
                        contentView.setPadding(0, 0, 0, 0);
//                        hideSystemUI();
                        fragment.hideSoftInput();
                    } else {
                        contentView.setPadding(0, 0, 0, getBottomStatusHeight(activity.getApplicationContext()));
                    }
                }
            }
        }
    };

    /**
     * 隐藏系统状态栏和虚拟键
     */
    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION//防止导航栏隐藏时内容区域大小发生变化
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN//防止状态栏隐藏时内容区域大小发生变化
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION//隐藏导航栏
                            | View.SYSTEM_UI_FLAG_FULLSCREEN//隐藏状态栏
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);//点击屏幕不弹出虚拟机，只有边缘滑动才响应
        }
    }

    /**
     * 获取 虚拟按键的高度
     *
     * @param context
     * @return
     */
    public static int getBottomStatusHeight(Context context) {
        int totalHeight = getDpi(context);
        int contentHeight = getScreenHeight(context);
        return totalHeight - contentHeight;
    }

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public static int getDpi(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}