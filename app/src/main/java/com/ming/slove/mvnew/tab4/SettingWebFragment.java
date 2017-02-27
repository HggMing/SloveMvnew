package com.ming.slove.mvnew.tab4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.bilibili.magicasakura.widgets.TintProgressBar;
import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.app.ThemeHelper;
import com.ming.slove.mvnew.common.base.LazyLoadFragment;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.common.widgets.dialog.CardPickerDialog;
import com.ming.slove.mvnew.common.widgets.dialog.Dialog_ShareBottom;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.model.bean.ApplyInfo;
import com.ming.slove.mvnew.model.bean.CheckPhone;
import com.ming.slove.mvnew.model.bean.UserInfo;
import com.ming.slove.mvnew.model.database.MyDB;
import com.ming.slove.mvnew.model.databean.WebAppUserInfo;
import com.ming.slove.mvnew.model.event.ChangeThemeColorEvent;
import com.ming.slove.mvnew.model.event.ShopApplyPassEvent;
import com.ming.slove.mvnew.model.event.UpdataShopOwnerHeadEvent;
import com.ming.slove.mvnew.tab1.BrowserActivity;
import com.ming.slove.mvnew.tab4.applyshoper.ApplyShopOwnerActivity;
import com.ming.slove.mvnew.tab4.applyshoper.ShowApplyingActivity;
import com.ming.slove.mvnew.tab4.safesetting.RealNameBindingActivity;
import com.ming.slove.mvnew.tab4.safesetting.SafeSettingActivity;
import com.ming.slove.mvnew.tab4.scommon.AboutActivity;
import com.ming.slove.mvnew.tab4.scommon.AdviceActivity;
import com.ming.slove.mvnew.tab4.scommon.DataCleanManager;
import com.ming.slove.mvnew.tab4.selfinfo.UserDetailActivity;
import com.ming.slove.mvnew.ui.login.LoginActivity;
import com.ming.slove.mvnew.ui.main.MainActivity;
import com.ming.slove.mvnew.ui.update.UpdateApp;
import com.orhanobut.hawk.Hawk;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 设置页面
 */
public class SettingWebFragment extends LazyLoadFragment implements CardPickerDialog.ClickListener {
    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.progressBar)
    TintProgressBar progressBar;
    @Bind(R.id.content_empty)
    FrameLayout contentEmpty;

    private String url;
    private boolean isLoadError;

    private UserInfo.DataEntity dataEntity;
    WebAppUserInfo webAppUserInfo = new WebAppUserInfo();
    private String auth;
    private int isShopOwner;//是否是店长,1是0不是
    private boolean isUpdataMyInfo;//是否更新完个人信息

    private final int REQUEST_USER_INFO = 122;
    private final int REQUEST_APPLY_PASSED = 123;

    @Override
    public int getLayout() {
        return R.layout.fragment_web;
    }

    @Override
    public void initViews(View view) {
        auth = Hawk.get(APPS.USER_AUTH);
        isShopOwner = Hawk.get(APPS.IS_SHOP_OWNER);

        url = "http://118.178.232.77:8090/view/set/index.html";
        setHasOptionsMenu(true);
    }

    @Override
    public void loadData() {
        initData();
        getUserInfoDetail();//在线获取用户信息
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_theme:
                //更换主题
                CardPickerDialog dialog = new CardPickerDialog();
                dialog.setClickListener(this);
                dialog.show(getActivity().getSupportFragmentManager(), CardPickerDialog.TAG);
                return true;
            case R.id.action_refresh:
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

        webView.addJavascriptInterface(new WebAppJsInterface(getContext(), (MainActivity) getActivity()), "mimiObj");

        // 打开链接。
        webView.loadUrl(url);
    }

    class WebAppJsInterface {
        Context mContext;
        MainActivity mActivity;

        WebAppJsInterface(Context c, MainActivity a) {
            mContext = c;
            mActivity = a;
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
            //返回UserInfo！
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
            Toast.makeText(mContext, "保存文件，暂未实现，文件名："+filename+",文件值："+value, Toast.LENGTH_SHORT).show();
            return 0;
        }

        @JavascriptInterface
        public String loadFile(String filename) {
            String rtn = "";
            //loadFile！//// FIXME: 2017/2/23
            Toast.makeText(mContext, "读取本地文件，暂未实现，文件名："+filename, Toast.LENGTH_SHORT).show();
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
            dialog.show(mActivity.getSupportFragmentManager());
            return dialog.getShareResult();
        }

        @JavascriptInterface
        public int createBtn(String btn, String img) {
            //0、成功1、失败
            Toast.makeText(mContext, "创建按钮，暂未实现，按键："+btn+",图片："+img, Toast.LENGTH_SHORT).show();
            return 0;
        }

        @JavascriptInterface
        public int scanCode() {
            Toast.makeText(mContext, "浏览代码，暂未实现，", Toast.LENGTH_SHORT).show();
            return 0;
        }

        @JavascriptInterface
        public void goURL(String url, boolean isNewWin, String a) {
            Toast.makeText(mContext, "进入链接，暂未实现，URL："+url+",是否新窗口："+isNewWin+",未知参数："+a, Toast.LENGTH_SHORT).show();
            if (isNewWin) {
            }
        }

        @JavascriptInterface
        public void Close() {
        }

        @JavascriptInterface
        public void goModal(String modalID, String parms) {
            //modalID
            int imodalID = -1;
            try {
                imodalID = Integer.parseInt(modalID);
            } catch (Exception e) {

            }
            switch (imodalID) {
                case 1://检查更新
                    UpdateApp.updateCheck(mContext);
                    break;
                case 2://清理缓存
                    MyDialog.Builder builder = new MyDialog.Builder(mContext);
                    builder.setTitle("提示")
                            .setMessage("清除程序数据和缓存（不会删除下载的图片）？")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String cacheSize = "5.6MB";
                                            try {
                                                cacheSize = DataCleanManager.getFormatSize(
                                                        DataCleanManager.getFolderSize(mActivity.getFilesDir())
                                                                + DataCleanManager.getFolderSize(mActivity.getCacheDir()));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            Toast.makeText(mContext, "清理缓存数据" + cacheSize, Toast.LENGTH_SHORT).show();
                                            DataCleanManager.cleanApplicationData(mContext);
                                            dialog.dismiss();
                                        }
                                    })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                    break;
                case 3://意见反馈
                    Intent intent3 = new Intent(mContext, AdviceActivity.class);
                    startActivity(intent3);
                    break;
                case 4://关于我们
                    Intent intent4 = new Intent(mContext, AboutActivity.class);
                    startActivity(intent4);
                    break;
                case 5://申请成为店长
                    //获取申请店长，当前状态
                    getApplyStatus();
                    break;
                case 6://账户安全
                    Intent intent6 = new Intent(getContext(), SafeSettingActivity.class);
                    startActivity(intent6);
                    break;
                case 7://修改自己的资料
                    if (dataEntity != null) {
                        Intent intent7 = new Intent(getContext(), UserDetailActivity.class);
                        intent7.putExtra(UserDetailActivity.USER_INFO, dataEntity);
                        startActivityForResult(intent7, REQUEST_USER_INFO);
                    } else {
                        getUserInfoDetail();
                    }
                    break;
                default:
                    break;
            }
        }

        @JavascriptInterface
        public void Logout() {
            logout();
        }
    }

    /**
     * 点击退出登录
     */
    private void logout() {
        MyDialog.Builder builder = new MyDialog.Builder(getContext());
        builder.setTitle("提示")
                .setMessage("确定退出登录？")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Hawk.put(APPS.KEY_LOGIN_PASSWORD, "");
                                Hawk.put(APPS.IS_UPDATA_MY_INFO, false);
                                Hawk.delete(APPS.ME_UID);
                                Hawk.delete(APPS.SELECTED_CARD);
                                Hawk.delete(APPS.MANAGER_ADDRESS);
                                Hawk.delete(APPS.MANAGER_VID);
                                //停止个推SDK服务
                                PushManager.getInstance().stopService(getContext());
                                //关闭数据库
                                MyDB.createDb(getContext()).close();
                                MyDB.setLiteOrm(null);

                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if (!getActivity().isFinishing()) {
            builder.create().show();
        }
    }

    // 注入js函数监听
    private void addJsFunction() {
        int themeColor = ThemeUtils.getColorById(getContext(), R.color.theme_color_primary);
        String themeJsColor = "#" + Integer.toHexString(themeColor).substring(2);
        // 这段js函数的功能就是，改变web界面的主题颜色
        webView.loadUrl("javascript:(function(){"
                + "var objs = $('.btn-primary'); " //用此css的全部容器，
                + "for(var i=0;i<objs.length;i++)  " + "{"
                + "    objs[i].style.background='" + themeJsColor + "'; "
                + "    objs[i].style.borderColor='" + themeJsColor + "'; "
                + "}"

                + "var objs = $('.fa'); " //全部的图标字体，图标颜色
                + "for(var i=0;i<objs.length;i++)  " + "{"
                + "    objs[i].style.color='" + themeJsColor + "'; "
                + "}"
                + "})()");
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
            Intent intent = new Intent(getContext(), SettingBrowserActivity.class);
            intent.putExtra(BrowserActivity.KEY_URL, url);
            startActivity(intent);
            //如果不需要其他对点击链接事件的处理返回true，否则返回false
            return true;
        }

        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            isLoadError = false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // html加载完成之后，添加js函数
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
    }

    /**
     * 获取用户信息
     */
    public void getUserInfoDetail() {
        OtherApi.getService().get_UserInfo(auth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(UserInfo userInfo) {
                        if (userInfo != null && userInfo.getErr() == 0) {
                            dataEntity = userInfo.getData();
                            String headUrl = APPS.BASE_URL + dataEntity.getHead();
                            Hawk.put(APPS.ME_HEAD, headUrl);

                            //如果是店长，更新首页，店长头像。解决无APP.ME_HEAD时，头像显示问题
                            if (isShopOwner == 1) {
                                EventBus.getDefault().post(new UpdataShopOwnerHeadEvent(headUrl));
                            }

                            String uName = dataEntity.getUname();
                            Hawk.put(APPS.ME_NAME, uName);

                            Hawk.put(APPS.IS_UPDATA_MY_INFO, true);

                            //设置要上次给web的数据
                            webAppUserInfo.setAuth(auth);
                            webAppUserInfo.setHeadpic(dataEntity.getHead());
                            webAppUserInfo.setUname(uName);
                            Hawk.put(APPS.WEB_APP_USER_INFO,webAppUserInfo);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_USER_INFO:
                if (resultCode == Activity.RESULT_OK) {
                    //修改数据后在线更新个人信息
                    isUpdataMyInfo = Hawk.get(APPS.IS_UPDATA_MY_INFO, false);
                    if (!isUpdataMyInfo) {
                        getUserInfoDetail();
                        webView.reload();//更新页面
                    }
                }
                break;
            case REQUEST_APPLY_PASSED:
                if (resultCode == Activity.RESULT_OK) {
                    isShopOwner = 1;
                    //申请店长成功后，主页tab由4变5。一些店长相关细节处理
                    EventBus.getDefault().post(new ShopApplyPassEvent());
                }
        }
    }

    @Override
    public void onConfirm(int currentTheme) {
        if (ThemeHelper.getTheme(getContext()) != currentTheme) {
            ThemeHelper.setTheme(getContext(), currentTheme);
            ThemeUtils.refreshUI(getContext(), new ThemeUtils.ExtraRefreshable() {
                @Override
                public void refreshGlobal(Activity activity) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        ActivityManager.TaskDescription taskDescription = new ActivityManager
                                .TaskDescription(null, null, ThemeUtils.getThemeAttrColor(getContext(), android.R.attr.colorPrimary));
                        getActivity().setTaskDescription(taskDescription);
                        getActivity().getWindow().setStatusBarColor(ThemeUtils.getColorById(getContext(), R.color.theme_color_primary));
                    }
                }

                @Override
                public void refreshSpecificView(View view) {
                }
            });
            //通知MainActivity更换主题
            EventBus.getDefault().post(new ChangeThemeColorEvent());
            webView.reload();//刷新本网页
        }
    }

    private void getApplyStatus() {
        String vid = Hawk.get(APPS.APPLY_INFO_VID + dataEntity.getUid());
        if (StringUtils.isEmpty(vid)) {//假如没有申请过
            getIsBinding();//获取是否实名认证
        } else {
            OtherApi.getService()
                    .get_IsApply(auth, vid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ApplyInfo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(ApplyInfo applyInfo) {
                            ApplyInfo.DataBean data = applyInfo.getData();
                            if (data != null) {//已申请过，进入查看申请状态页面
                                Intent intent = new Intent(getContext(), ShowApplyingActivity.class);
                                intent.putExtra(ShowApplyingActivity.STATUS_APPLY, data.getStats());
                                startActivityForResult(intent, REQUEST_APPLY_PASSED);
                            } else {//已经选择申请的村（已实名通过），但没有申请就退出。点击申请后直接进入申请界面
                                Intent intent4 = new Intent(getContext(), ApplyShopOwnerActivity.class);
                                intent4.putExtra(ApplyShopOwnerActivity.USER_INFO, dataEntity);
                                startActivity(intent4);
                            }
                        }
                    });
        }
    }

    private void getIsBinding() {
        //1)将除图片外的参数以及机构key组成一个字符串(注意顺序)
        String phone = Hawk.get(APPS.KEY_LOGIN_NAME);
        String other = "compid=9&phone=" + phone;
        String str = other + "&key=69939442285489888751746749876227";
        //2)使用MD5算法加密上述字符串
        String sign = BaseTools.md5(str);
        //3)最终得到参数字符串：（注意，KEY参数不带到参数列表,sign参数加入参数列表）
        String str2 = other + "&sign=" + sign;
        //4)把上述字符串做base64加密，最终得到请求:
        String paraString = Base64.encodeToString(str2.getBytes(), Base64.NO_WRAP);
        RequestBody data = RequestBody.create(MediaType.parse("text/plain"), paraString);

        OtherApi.getService()
                .post_IsRealBinding(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        String s = null;
                        try {
                            s = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        CheckPhone result = gson.fromJson(new String(Base64.decode(s, Base64.DEFAULT)), CheckPhone.class);
                        if (result.getErr() == 0) {//已经实名认证,进入申请界面
                            Intent intent4 = new Intent(getContext(), ApplyShopOwnerActivity.class);
                            intent4.putExtra(ApplyShopOwnerActivity.USER_INFO, dataEntity);
                            startActivity(intent4);
                        }
                        if (result.getErr() == 1002) {//还没有实名认证
                            MyDialog.Builder builder1 = new MyDialog.Builder(getContext());
                            builder1.setTitle("提示")
                                    .setMessage("你的账号尚未实名认证，请先进行实名认证。")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            Intent intent5 = new Intent(getContext(), RealNameBindingActivity.class);
                                            startActivity(intent5);
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            if (!getActivity().isFinishing()) {
                                builder1.create().show();
                            }
                        }
                    }
                });
    }
}
