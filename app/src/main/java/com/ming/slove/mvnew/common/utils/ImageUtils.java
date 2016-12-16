package com.ming.slove.mvnew.common.utils;

import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Ming on 2016/12/12.
 */

public class ImageUtils {
    /**
     * 加载照片，无缓存
     * @param fragment content
     * @param string 图片源
     * @param view 图片控件
     */
    public static void showPictureNoChache(Fragment fragment, String string, ImageView view) {
        Glide.with(fragment)
                .load(string)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(view);
    }
}
