package com.ming.slove.mvnew.shop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintProgressBar;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.LazyLoadFragment;
import com.ming.slove.mvnew.tab1.BrowserActivity;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 营山县长显示统计页面
 */
public class ShowYingShanFragment extends LazyLoadFragment {
    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.progressBar)
    TintProgressBar progressBar;
    @Bind(R.id.content_empty)
    FrameLayout contentEmpty;

    private String url;
    private boolean isLoadError;

    @Override
    public int getLayout() {
        return R.layout.fragment_web;
    }

    @Override
    public void initViews(View view) {
        url = APPS.BASE_URL + "/yingshan";
        setHasOptionsMenu(true);
    }

    @Override
    public void loadData() {
        initData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            if (isLoadError) {
                webView.setVisibility(View.GONE);
            }
            webView.reload();
            contentEmpty.setVisibility(View.GONE);
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initData() {
        //添加进度条
        webView.setWebChromeClient(new MyWebChromeClient());
        //为WebView设置WebViewClient处理某些操作
        webView.setWebViewClient(new MywWebViewClient());
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        File cachePath = new File(APPS.FILE_PATH_WEBCACHE);
        webSetting.setAppCachePath(cachePath.getPath());
        File databasesPath = new File(APPS.FILE_PATH_DBCACHE);
        webSetting.setDatabasePath(databasesPath.getPath());
        // 打开链接。
        webView.loadUrl(url);
    }

    @OnClick(R.id.content_empty)
    public void onClick() {
        webView.reload();//刷新网页
        webView.setVisibility(View.GONE);
        contentEmpty.setVisibility(View.GONE);
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView webView, int i) {
            super.onProgressChanged(webView, i);
            if (progressBar != null) {
                progressBar.setProgress(i);
                if (i == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                    AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
                    animation.setDuration(500);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    progressBar.startAnimation(animation);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    class MywWebViewClient extends WebViewClient {
        //重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
            Intent intent = new Intent(getContext(), BrowserActivity.class);
            intent.putExtra(BrowserActivity.KEY_URL, url);
            startActivity(intent);
            //如果不需要其他对点击链接事件的处理返回true，否则返回false
            return true;
        }

        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            isLoadError=false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // html加载完成之后
            webView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
            super.onReceivedError(webView, webResourceRequest, webResourceError);
            if (webResourceError.getErrorCode() != -6) {
                contentEmpty.setVisibility(View.VISIBLE);
                isLoadError = true;
            }
        }
    }
}
