package com.ming.slove.mvnew.app;

import android.app.Application;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.orhanobut.hawk.Hawk;

import me.shaohui.shareutil.ShareConfig;
import me.shaohui.shareutil.ShareManager;

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
        // 分享功能配置 //// TODO: 2017/1/4 填写微信开发id
        ShareConfig config = ShareConfig.instance()
                .qqId("1105819027")
                //.wxId(WX_ID)
                //.weiboId(WEIBO_ID)
                // 下面两个，如果不需要登录功能，可不填写
                //.weiboRedirectUrl(REDIRECT_URL)
                //.wxSecret(WX_ID)
                ;
        ShareManager.init(config);
    }
}
