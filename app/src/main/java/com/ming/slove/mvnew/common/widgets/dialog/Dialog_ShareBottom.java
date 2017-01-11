package com.ming.slove.mvnew.common.widgets.dialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Toast;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.model.bean.IncomeBase;
import com.ming.slove.mvnew.ui.share.ShareFriendListActivity;

import me.shaohui.bottomdialog.BaseBottomDialog;
import me.shaohui.shareutil.ShareLogger;
import me.shaohui.shareutil.ShareUtil;
import me.shaohui.shareutil.share.ShareListener;
import me.shaohui.shareutil.share.SharePlatform;

/**
 * 分享对话框
 */

public class Dialog_ShareBottom extends BaseBottomDialog implements View.OnClickListener {

    private ShareListener mShareListener;

    private String title;
    private String summary;
    private String targetUrl;
    private String thumb;


    public void setShareContent(String title, String summary, String targetUrl, String thumb) {
        this.title = title;
        this.summary = summary;
        this.targetUrl = targetUrl;
        this.thumb = thumb;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.layout_bottom_share;
    }

    @Override
    public void bindView(final View v) {
        v.findViewById(R.id.share_qq).setOnClickListener(this);
        v.findViewById(R.id.share_qzone).setOnClickListener(this);
        v.findViewById(R.id.share_weibo).setOnClickListener(this);
        v.findViewById(R.id.share_wx).setOnClickListener(this);
        v.findViewById(R.id.share_wx_timeline).setOnClickListener(this);

        mShareListener = new ShareListener() {
            @Override
            public void shareSuccess() {
                Toast.makeText(v.getContext(), "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void shareFailure(Exception e) {
                if (ShareLogger.INFO.NOT_INSTALL.equals(e.getMessage())) {
                    Toast.makeText(v.getContext(), "没有安装相关应用，无法分享。", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "分享失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void shareCancel() {
                Toast.makeText(v.getContext(), "取消分享", Toast.LENGTH_SHORT).show();

            }
        };
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(getContext(), "正在打开分享界面，请稍等", Toast.LENGTH_SHORT).show();
        switch (view.getId()) {
            case R.id.share_qq:
                ShareUtil.shareMedia(getContext(), SharePlatform.QQ, title, summary, targetUrl, thumb, mShareListener);
                break;
            case R.id.share_qzone:
                ShareUtil.shareMedia(getContext(), SharePlatform.QZONE, title, summary, targetUrl, thumb, mShareListener);
                break;

            case R.id.share_wx_timeline:
                ShareUtil.shareMedia(getContext(), SharePlatform.WX_TIMELINE, title, summary, targetUrl, thumb, mShareListener);
                break;
            case R.id.share_wx:
                ShareUtil.shareMedia(getContext(), SharePlatform.WX, title, summary, targetUrl, thumb, mShareListener);
                break;
            case R.id.share_weibo://分享到app内好友
                String msg = "{\"title\":\"" + title + "\",\"detail\":\"" + summary + "\",\"image\":\"" + thumb + "\",\"link\":\"" + targetUrl + "\"}";
                Intent intent = new Intent(getContext(), ShareFriendListActivity.class);
                intent.putExtra(ShareFriendListActivity.SHARE_MSG, msg);
                startActivity(intent);
                break;
        }
        dismiss();
    }
}
