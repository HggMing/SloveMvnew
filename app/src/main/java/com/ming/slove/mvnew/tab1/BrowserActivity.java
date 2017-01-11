package com.ming.slove.mvnew.tab1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ming.slove.mvnew.api.MyServiceClient;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.common.widgets.dialog.Dialog_ShareBottom;
import com.ming.slove.mvnew.model.bean.ShoppingAddress;
import com.ming.slove.mvnew.tab3.product.ChooseAddressActivity;
import com.orhanobut.hawk.Hawk;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.APP;
import com.ming.slove.mvnew.common.base.BaseActivity;
import com.ming.slove.mvnew.common.widgets.alipay.PayUtils;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.model.bean.OrderInfo;
import com.ming.slove.mvnew.tab1.webutils.X5WebView;
import com.ming.slove.mvnew.tab2.chat.ChatActivity;
import com.ming.slove.mvnew.tab3.product.ProductListActivity;
import com.tencent.smtt.export.external.interfaces.JsResult;
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

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.ming.slove.mvnew.tab3.product.ProductPayActivity.KEY_USER_ADDR_INFO;


public class BrowserActivity extends BaseActivity {
    private TextView toolbarTitle;
    private Toolbar toolbar;
    private X5WebView mWebView;
    private FrameLayout mViewParent;
    private static final int MAX_LENGTH = 14;

    private ProgressBar mPageLoadingProgressBar = null;
    WebSettings webSetting;
    private static final int REQUEST_ADD = 123;


    public static String KEY_URL = "key_url";
    private String mIntentUrl;

    public static String WEB_TITLE = "the_title";
    private String mIntentTitle;


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

        this.webViewTransportTest();
    }

    private void webViewTransportTest() {
        X5WebView.setSmallWebViewEnabled(false);
    }

    private void initProgressBar() {
        mPageLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBar1);// new
        mPageLoadingProgressBar.setMax(100);
        mPageLoadingProgressBar.setProgressDrawable(this.getResources().getDrawable(R.drawable.color_progressbar));
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
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mIntentTitle == null) {
                    String title=view.getTitle();
                    if (title != null && title.length() > MAX_LENGTH) {
                        title = title.subSequence(0, MAX_LENGTH) + "...";
                    }
                    toolbarTitle.setText(title);
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView webView, String title) {
                super.onReceivedTitle(webView, title);
//                if (mIntentTitle == null) {
//                    if (title != null && title.length() > MAX_LENGTH) {
//                        title = title.subSequence(0, MAX_LENGTH) + "...";
//                    }
//                    toolbarTitle.setText(title);
//                }
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
                MyDialog.Builder builder = new MyDialog.Builder(BrowserActivity.this);
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
                MyDialog.Builder builder = new MyDialog.Builder(BrowserActivity.this);
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
                new AlertDialog.Builder(BrowserActivity.this)
                        .setTitle("下载")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Toast.makeText(BrowserActivity.this, "fake message: i'll download...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Toast.makeText(BrowserActivity.this, "fake message: refuse download...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setOnCancelListener(
                                new DialogInterface.OnCancelListener() {

                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        Toast.makeText(BrowserActivity.this, "fake message: refuse download...", Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
            }
        });

        mWebView.addJavascriptInterface(new WebAppInterface(this), "village");

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

    class WebAppInterface {
        Context mContext;

        WebAppInterface(Context context) {
            mContext = context;
        }

        String auth = Hawk.get(APPS.USER_AUTH);

        @JavascriptInterface
        public String setTitle(String title, String isHomepage) {
            //修改标题
            return "{\"err\":\"0\"}";
        }

        @JavascriptInterface
        public String getAuth() {
            return "{\"err\":\"0\",\"auth\":\"" + auth + "\"}";
        }

        @JavascriptInterface
        public String Alipay(final String order_sn) {
//            Toast.makeText(BrowserActivity.this, "order_sn:" + order_sn, Toast.LENGTH_SHORT).show();
            MyServiceClient.getService()
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
                            PayUtils payUtils = new PayUtils(BrowserActivity.this, -1);
                            payUtils.pay(data.getOrder_title(), "村特产订单",
                                    String.valueOf(data.getMoney()), data.getOrder_sn(), data.getUrl());
                        }
                    });
            return "{\"err\":\"0\"}";
        }

        @JavascriptInterface
        public String goInVill(String village_id, String village_name) {
//            Toast.makeText(BrowserActivity.this, "vid:" + village_id, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, ProductListActivity.class);
            intent.putExtra(ProductListActivity.VILLAGE_ID, village_id);
            intent.putExtra(ProductListActivity.VILLAGE_NAME, village_name);
            startActivity(intent);
            return "{\"err\":\"0\"}";
        }

        @JavascriptInterface
        public String talk2Shopper(String shopper_id, String shopper_name, String shopper_head_url) {
//            Toast.makeText(BrowserActivity.this, "s_id:"+shopper_id, Toast.LENGTH_SHORT).show();
            if (!StringUtils.isBlank(shopper_id)) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra(ChatActivity.UID, shopper_id);
                intent.putExtra(ChatActivity.USER_NAME, shopper_name);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "本村暂无客服。", Toast.LENGTH_SHORT).show();
            }
            return "{\"err\":\"0\"}";
        }

        @JavascriptInterface
        public String ChooseAddress() {
//            Toast.makeText(mContext, "点击选择收货地址", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, ChooseAddressActivity.class);
            startActivityForResult(intent, REQUEST_ADD);
            return "{\"err\":\"0\"}";
        }

        @JavascriptInterface
        public String getShareParameter(String shareUrl, String title, String content, String pic_surl_1) {
//            Toast.makeText(mContext, "点击分享", Toast.LENGTH_SHORT).show();
            Dialog_ShareBottom dialog = new Dialog_ShareBottom();
            dialog.setShareContent(title, content, shareUrl, pic_surl_1);
            dialog.show(getSupportFragmentManager());
            return "{\"err\":\"0\"}";
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
//            webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null || mWebView == null || intent.getData() == null)
            return;
        mWebView.loadUrl(intent.getData().toString());
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null)
            mWebView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADD://选择收货地址后，返回数据
                    ShoppingAddress.DataBean userAddrInfo = data.getParcelableExtra(KEY_USER_ADDR_INFO);
                    ;//用户地址信息
                    String addrJson = "{\"err\":0,\n" +
                            "\"type\":1,\n" +
                            "\"phone\":" + userAddrInfo.getTel() + ",\n" +
                            "\"name\":" + userAddrInfo.getUname() + ",\n" +
                            "\"addr\":" + userAddrInfo.getAddr() + "}";
                    try {
                        JSONObject addJSONObject = new JSONObject(addrJson);
                        mWebView.loadUrl("javascript:RetChooseAddress(" + addJSONObject + ")");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
