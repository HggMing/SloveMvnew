package com.ming.slove.mvnew.app;

import android.app.Application;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.orhanobut.hawk.Hawk;

import static com.ming.slove.mvnew.app.APPS.BASE_URL;
import static com.ming.slove.mvnew.app.APPS.KEY_BASE_URL;

public class APP extends Application {
    /**
     * 单例模式中获取唯一的Application实例
     */
    private static APP instance;

    public static APP getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //主题切换初始化
        ThemeUtils.setSwitchColor(ThemeHelper.getSwitchColor());
        //用于存储
        Hawk.init(this).build();
        //初始为测试服务器
        BASE_URL = Hawk.get(KEY_BASE_URL, "http://product1.yibanke.com/");
    }
}
