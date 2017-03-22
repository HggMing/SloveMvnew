package com.ming.slove.mvnew.tab4;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.google.gson.Gson;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BaseActivity;
import com.ming.slove.mvnew.common.widgets.alipay.PayUtils;
import com.ming.slove.mvnew.common.widgets.dialog.Dialog_ShareBottom;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.model.bean.OrderInfo;
import com.ming.slove.mvnew.model.bean.ShoppingAddress;
import com.ming.slove.mvnew.model.databean.WebAppUserInfo;
import com.ming.slove.mvnew.tab1.BrowserActivity;
import com.ming.slove.mvnew.tab1.webutils.X5WebView;
import com.orhanobut.hawk.Hawk;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.ming.slove.mvnew.tab3.product.ProductPayActivity.KEY_USER_ADDR_INFO;


public class SettingBrowserActivity extends BaseActivity {
    private TextView toolbarTitle;
    private Toolbar toolbar;
    private X5WebView mWebView;
    private FrameLayout contentEmpty;
    private ProgressBar mPageLoadingProgressBar = null;
    private FrameLayout mViewParent;

    WebSettings webSetting;

    public static String KEY_URL = "key_url";
    private String mIntentUrl;

    public static String WEB_TITLE = "the_title";
    private String mIntentTitle;
    private boolean isLoadError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_browser);

        initView();
        init();
    }

    private void initView() {
        mViewParent = (FrameLayout) findViewById(R.id.webView1);
        toolbar = (Toolbar) findViewById(R.id.toolbar_activity_base);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        contentEmpty = (FrameLayout) findViewById(R.id.content_empty);

        mIntentUrl = getIntent().getStringExtra(KEY_URL);
        mIntentTitle = getIntent().getStringExtra(WEB_TITLE);
        if (mIntentTitle != null)
            toolbarTitle.setText(mIntentTitle);

        try {
            if (Build.VERSION.SDK_INT >= 11) {
                //对该window进行硬件加速.
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert toolbar != null;
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            //设置toolbar后,开启返回图标
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //设备返回图标样式
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_toolbar_back);
        }

        contentEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.reload();//刷新网页
                mWebView.setVisibility(View.GONE);
                contentEmpty.setVisibility(View.GONE);
            }
        });

        this.webViewTransportTest();
    }

    private void webViewTransportTest() {
        X5WebView.setSmallWebViewEnabled(false);
    }

    private void initProgressBar() {
        mPageLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBar1);// new
        mPageLoadingProgressBar.setMax(100);
        mPageLoadingProgressBar.setProgressDrawable(this.getResources().getDrawable(R.drawable.list_color_progressbar));
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        mWebView = new X5WebView(this);
        mViewParent.addView(mWebView);

        initProgressBar();
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                //如果不需要其他对点击链接事件的处理返回true，否则返回false
                return false;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                isLoadError = false;
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
                if (mIntentTitle == null) {
                    String title = webView.getTitle();
                    toolbarTitle.setText(title);
                }
                addJsFunction();
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
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView webView, String title) {
                super.onReceivedTitle(webView, title);
                if (mIntentTitle == null) {
                    toolbarTitle.setText(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mPageLoadingProgressBar.setProgress(newProgress);
                if (mPageLoadingProgressBar != null && newProgress != 100) {
                    mPageLoadingProgressBar.setVisibility(View.VISIBLE);
                } else if (mPageLoadingProgressBar != null) {
                    mPageLoadingProgressBar.setVisibility(View.GONE);
                }
            }

            //处理javascript中的alert
            @Override
            public boolean onJsAlert(WebView webView, String s, String s1, final JsResult jsResult) {
                MyDialog.Builder builder = new MyDialog.Builder(SettingBrowserActivity.this);
                builder.setTitle("提示")
                        .setCannel(false)
                        .setMessage(s1)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //点击确定按钮之后，继续执行网页中的操作
                                        jsResult.confirm();
                                        dialog.dismiss();
                                    }
                                });
                if (!isFinishing()) {
                    builder.create().show();
                }
                return true;
            }

            //处理javascript中的confirm
            @Override
            public boolean onJsConfirm(WebView webView, String s, String s1, final JsResult jsResult) {
                MyDialog.Builder builder = new MyDialog.Builder(SettingBrowserActivity.this);
                builder.setTitle("提示")
                        .setMessage(s1)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        jsResult.confirm();
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                jsResult.cancel();
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                return true;
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String arg0, String arg1, String arg2,
                                        String arg3, long arg4) {
                new AlertDialog.Builder(SettingBrowserActivity.this)
                        .setTitle("下载")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Toast.makeText(SettingBrowserActivity.this, "fake message: i'll download...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Toast.makeText(SettingBrowserActivity.this, "fake message: refuse download...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setOnCancelListener(
                                new DialogInterface.OnCancelListener() {

                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        Toast.makeText(SettingBrowserActivity.this, "fake message: refuse download...", Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
            }
        });

        mWebView.addJavascriptInterface(new WebAppJsInterface(this), "mimiObj");

        webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
//        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        //webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        //webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0).getPath());

        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        //webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        long time = System.currentTimeMillis();
        if (mIntentUrl != null) {
            mWebView.loadUrl(mIntentUrl);
        }
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    class WebAppJsInterface {
        Context mContext;

        WebAppJsInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public Boolean isInAPP() {
            //isInAPP返回true
            return true;
        }

        @JavascriptInterface
        public String getMyID() {
            //返回用户uid！
            return Hawk.get(APPS.ME_UID);
        }

        @JavascriptInterface
        public String getUserInfo() {
//            Toast.makeText(mContext, "获取用户信息，需要去实现", Toast.LENGTH_SHORT).show();
            //返回UserInfo！
            WebAppUserInfo webAppUserInfo = Hawk.get(APPS.WEB_APP_USER_INFO);
            String jsonStr = new Gson().toJson(webAppUserInfo);
            return jsonStr;
        }

        @JavascriptInterface
        public int saveSetting(String key, String value) {
            //成功0，失败1
            Hawk.put(key, value);
            return 0;
        }

        @JavascriptInterface
        public String loadSetting(String key) {
            //loadSetting
            return Hawk.get(key);
        }

        @JavascriptInterface
        public int saveFile(String filename, String value) {
            //成功0，失败1
            //saveFile//// FIXME: 2017/2/23
            Toast.makeText(mContext, "保存文件，暂未实现，文件名：" + filename + ",文件值：" + value, Toast.LENGTH_SHORT).show();
            return 0;
        }

        @JavascriptInterface
        public String loadFile(String filename) {
            String rtn = "";
            //loadFile！//// FIXME: 2017/2/23
            Toast.makeText(mContext, "读取本地文件，暂未实现，文件名：" + filename, Toast.LENGTH_SHORT).show();
            return rtn;
        }

        @JavascriptInterface
        public void showTitle(boolean show) {
            //是否显示本地Toolbar
        }

        @JavascriptInterface
        public int Share(String shareUrl, String title, String pic, String content) {
            //返回0、分享成功1、分享失败2、用户取消分享
            Dialog_ShareBottom dialog = new Dialog_ShareBottom();
            dialog.setShareContent(title, content, shareUrl, pic);
            dialog.show(getSupportFragmentManager());
            return dialog.getShareResult();
        }

        @JavascriptInterface
        public int createBtn(String btn, String img) {
            //0、成功1、失败
            Toast.makeText(mContext, "创建按钮，暂未实现，按键：" + btn + ",图片：" + img, Toast.LENGTH_SHORT).show();
            return 0;
        }

        @JavascriptInterface
        public int scanCode() {
            Toast.makeText(mContext, "浏览代码，暂未实现，", Toast.LENGTH_SHORT).show();
            return 0;
        }

        @JavascriptInterface
        public void goURL(String url, boolean isNewWin, String a) {
            Toast.makeText(mContext, "进入链接，暂未实现，URL：" + url + ",是否新窗口：" + isNewWin + ",未知参数：" + a, Toast.LENGTH_SHORT).show();
            if (isNewWin) {
            }
        }

        @JavascriptInterface
        public void Close() {
            Toast.makeText(mContext, "关闭页面，此处一般不会调用", Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void goModal(String modalID, String parms) {
            int imodalID = -1;
            try {
                imodalID = Integer.parseInt(modalID);
            } catch (Exception e) {

            }
            switch (imodalID) {
                default:
                    Toast.makeText(mContext, "执行本地模块功能，模块id：" + imodalID, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @JavascriptInterface
        public void Logout() {
            Toast.makeText(mContext, "退出登录，此处一般不会调用", Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public String Alipay(final String order_sn) {
//            Toast.makeText(BrowserActivity.this, "order_sn:" + order_sn, Toast.LENGTH_SHORT).show();
            String auth = Hawk.get(APPS.USER_AUTH);
            OtherApi.getService()
                    .get_OrderInfo(order_sn, auth)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<OrderInfo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(OrderInfo orderInfo) {
                            OrderInfo.DataBean data = orderInfo.getData();
                            PayUtils payUtils = new PayUtils(SettingBrowserActivity.this, 6);
                            payUtils.pay(data.getOrder_title(), "订单支付",
                                    String.valueOf(data.getMoney()), data.getOrder_sn(), data.getUrl());
                        }
                    });
            return "{\"err\":\"0\"}";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:
                if (isLoadError) {
                    mWebView.setVisibility(View.GONE);
                }
                mWebView.reload();
                contentEmpty.setVisibility(View.GONE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
//            webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            if (isLoadError) {
                mWebView.setVisibility(View.GONE);
            }
            mWebView.goBack();
            contentEmpty.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mWebView == null)
            return;
        if (intent == null || intent.getData() == null) {
            mWebView.reload();
        } else {
            mWebView.loadUrl(intent.getData().toString());
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null)
            mWebView.destroy();
        super.onDestroy();
    }

    // 注入js函数监听
    private void addJsFunction() {
        int themeColor = ThemeUtils.getColorById(this, R.color.theme_color_primary);
        String themeJsColor = "#" + Integer.toHexString(themeColor).substring(2);
        // 这段js函数的功能就是，改变web界面的主题颜色
        mWebView.loadUrl("javascript:(function() {" +
                "    var a,b,c,d ;" +
                "    for (b = $('.btn-primary'),a = 0; a < b.length; a++) b[a].style.background = '" + themeJsColor + "', b[a].style.borderColor = '" + themeJsColor + "';" +
                "    for (c = $('.statusMsg'), a = 0; a < c.length; a++) c[a].style.background = '"+themeJsColor+"';" +
                "    for (d = $('div [style*=#36c]'), a = 0; a < d.length; a++) d[a].style.background = '"+themeJsColor+"';" +
                "})();");
    }
}
