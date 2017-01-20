package com.ming.slove.mvnew.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.APP;
import com.yalantis.ucrop.compress.CompressConfig;
import com.yalantis.ucrop.compress.CompressImageOptions;
import com.yalantis.ucrop.compress.CompressInterface;
import com.yalantis.ucrop.dialog.SweetAlertDialog;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.ui.AlbumDirectoryActivity;
import com.yalantis.ucrop.ui.ImageGridActivity;
import com.yalantis.ucrop.util.LocalMediaLoader;
import com.yalantis.ucrop.util.PictureConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by MingN on 2017/1/16.
 */

public class MyPictureSelector {
    //获取当前app主题的颜色
    private Context mContext = APP.getInstance().getApplicationContext();
    private int themeColor = ThemeUtils.getColorById(mContext, R.color.theme_color_primary);
    private int themeColorDark = ThemeUtils.getColorById(mContext, R.color.theme_color_primary_dark);
    private int themeColorTrans = ThemeUtils.getColorById(mContext, R.color.theme_color_primary_trans);

    private Activity mActivity;


    public MyPictureSelector(Activity mActivity) {
        this.mActivity = mActivity;
    }

    private void help(List<LocalMedia> selectMedia) {
        //注意-->type为2时 设置isPreview or isCrop 无效
        //注意：Options可以为空，默认标准模式
        PictureConfig options = new PictureConfig();
        options.setType(LocalMediaLoader.TYPE_IMAGE); //1图片 or 2视频 LocalMediaLoader.TYPE_IMAGE,TYPE_VIDEO

        options.setEnableCrop(true); //是否裁剪
        options.setCopyMode(PictureConfig.COPY_MODEL_DEFAULT);// 裁剪比例 默认 1:1 3:4 3:2 16:9 可参考 Constants.COPY_MODEL_1_1
        options.setCompressQuality(100);// 图片裁剪质量,默认无损
        options.setCropW(0); //裁剪宽 如果值大于图片原始宽高 将返回原图大小
        options.setCropH(0); //裁剪高 如果值大于图片原始宽高 将返回原图大小

        options.setSelectMode(PictureConfig.MODE_MULTIPLE);// 2单选 or 1多选 MODE_MULTIPLE MODE_SINGLE
        options.setMaxSelectNum(9); //最大可选数量
        options.setSelectMedia(selectMedia);//已选择的图片集合

        options.setCompress(true); //是否压缩
        options.setShowCamera(true); //是否显示相机
        options.setEnablePreview(true); //是否预览,多选才生效

        options.setPreviewVideo(true);// 是否预览视频(播放)，多选才生效
        options.setRecordVideoDefinition(PictureConfig.HIGH);// 视频清晰度 PictureConfig.HIGH 清晰 PictureConfig.ORDINARY 普通 低质量
        options.setRecordVideoSecond(60);// 视频秒数

        setTheme(options);//主题相关设置

        AlbumDirectoryActivity.startPhoto(mActivity, options);//调用
    }

    private void setTheme(PictureConfig options) {
        options.setImageSpanCount(3);// 列表每行显示个数

        options.setThemeStyle(themeColor);//// 标题栏背景色;
        // 可以自定义底部 预览 完成 文字的颜色和背景色
        options.setPreviewColor(Color.WHITE);//预览文字颜色
        options.setCompleteColor(Color.WHITE);//完成文字颜色
        options.setPreviewBottomBgColor(themeColorTrans);//预览界面底部背景色
        options.setBottomBgColor(themeColorTrans);//选择图片页面底部背景色

        options.setCheckNumMode(false);// 是否显示QQ选择风格(带数字效果)
        options.setCheckedBoxDrawable(com.yalantis.ucrop.R.drawable.checkbox_selector);// 图片选择默认样式
    }

    /**
     * 选择一张图片，仅压缩
     */
    public void selectorSinglePicture() {
        PictureConfig options = new PictureConfig();
        options.setType(LocalMediaLoader.TYPE_IMAGE); //1图片 or 2视频 LocalMediaLoader.TYPE_IMAGE,TYPE_VIDEO
        options.setSelectMode(PictureConfig.MODE_SINGLE);// 2单选 or 1多选 MODE_MULTIPLE MODE_SINGLE

        options.setEnableCrop(false); //是否裁剪

        options.setCompress(true); //是否压缩
        options.setShowCamera(true); //是否显示相机
        options.setEnablePreview(false); //是否预览

        setTheme(options);//主题相关设置
        AlbumDirectoryActivity.startPhoto(mActivity, options);//调用
    }

    /**
     * 选择一张图片，裁剪为方形，压缩作为头像
     */
    public void selectorSquarePicture() {
        PictureConfig options = new PictureConfig();
        options.setType(LocalMediaLoader.TYPE_IMAGE); //1图片 or 2视频 LocalMediaLoader.TYPE_IMAGE,TYPE_VIDEO
        options.setSelectMode(PictureConfig.MODE_SINGLE);// 2单选 or 1多选 MODE_MULTIPLE MODE_SINGLE

        options.setEnableCrop(true); //是否裁剪
        options.setCopyMode(PictureConfig.COPY_MODEL_1_1);// 裁剪比例 默认 1:1 3:4 3:2 16:9 可参考 Constants.COPY_MODEL_1_1

        options.setCompress(true); //是否压缩
        options.setShowCamera(true); //是否显示相机
        options.setEnablePreview(false); //是否预览

        setTheme(options);//主题相关设置
        AlbumDirectoryActivity.startPhoto(mActivity, options);//调用
    }

    /**
     * 选择多张图片
     */
    public void selectorMultiplePicture( List<LocalMedia> selectMedia) {
        PictureConfig options = new PictureConfig();
        options.setType(LocalMediaLoader.TYPE_IMAGE); //1图片 or 2视频 LocalMediaLoader.TYPE_IMAGE,TYPE_VIDEO

        options.setEnableCrop(false); //是否裁剪

        options.setSelectMode(PictureConfig.MODE_MULTIPLE);// 2单选 or 1多选 MODE_MULTIPLE MODE_SINGLE
        options.setMaxSelectNum(9); //最大可选数量
        options.setSelectMedia(selectMedia);//已选择的图片集合

        options.setCompress(false); //是否压缩
        options.setShowCamera(true); //是否显示相机
        options.setEnablePreview(false); //是否预览

        setTheme(options);//主题相关设置
        AlbumDirectoryActivity.startPhoto(mActivity, options);//调用
    }

    /**
     * 选择视频
     */
    public void selectorVideo() {
        //注意-->type为2时 设置isPreview or isCrop 无效
        PictureConfig options = new PictureConfig();
        options.setType(LocalMediaLoader.TYPE_VIDEO); //1图片 or 2视频 LocalMediaLoader.TYPE_IMAGE,TYPE_VIDEO

        options.setCompress(false); //是否压缩
        options.setShowCamera(true); //是否显示相机

        options.setSelectMode(PictureConfig.MODE_MULTIPLE);// 2单选 or 1多选 MODE_MULTIPLE MODE_SINGLE
        options.setMaxSelectNum(1); //最大可选数量

        options.setPreviewVideo(false);// 是否预览视频(播放)，多选才生效
        options.setRecordVideoDefinition(PictureConfig.ORDINARY);// 视频清晰度 PictureConfig.HIGH 清晰 PictureConfig.ORDINARY 普通 低质量
        options.setRecordVideoSecond(60);// 视频秒数

        setTheme(options);//主题相关设置

        AlbumDirectoryActivity.startPhoto(mActivity, options);//调用
    }
}
