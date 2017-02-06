package com.ming.slove.mvnew.tab4.scommon;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.model.bean.EbankWifiConnect;
import com.ming.slove.mvnew.model.bean.IpPort;
import com.ming.slove.mvnew.ui.update.UpdateApp;
import com.orhanobut.hawk.Hawk;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SettingCommonActivity extends BackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_common);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_setting_common);
    }

    @OnClick({R.id.click_connect_wifi, R.id.click_check_version, R.id.click_clear_cache, R.id.click_advice, R.id.click_about})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.click_connect_wifi:
//                Toast.makeText(SettingCommonActivity.this, "上网认证", Toast.LENGTH_SHORT).show();
                connectWifi();
                break;
            case R.id.click_check_version:
//                Toast.makeText(SettingCommonActivity.this, "检查新版本", Toast.LENGTH_SHORT).show();
                UpdateApp.updateCheck(this);
                break;
            case R.id.click_clear_cache:
                MyDialog.Builder builder = new MyDialog.Builder(this);
                builder.setTitle("提示")
                        .setMessage("清除程序数据和缓存（不会删除下载的图片）？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String cacheSize = "5.6MB";
                                        try {
                                            cacheSize = DataCleanManager.getFormatSize(
                                                    DataCleanManager.getFolderSize(getFilesDir())
                                                            + DataCleanManager.getFolderSize(getCacheDir()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(SettingCommonActivity.this, "清理缓存数据" + cacheSize, Toast.LENGTH_SHORT).show();
                                        DataCleanManager.cleanApplicationData(SettingCommonActivity.this);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                if (!isFinishing()) {
                    builder.create().show();
                }
                break;
            case R.id.click_advice:
//                Toast.makeText(SettingCommonActivity.this, "意见反馈", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(this, AdviceActivity.class);
                startActivity(intent4);
                break;
            case R.id.click_about:
//                Toast.makeText(SettingCommonActivity.this, "关于我们", Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(this, AboutActivity.class);
                startActivity(intent5);
                break;
        }
    }

    /**
     * 若连接e-bank，则认证上网。
     */
    private void connectWifi() {
        final String auth = Hawk.get(APPS.USER_AUTH);
        OtherApi.getService().get_IpPort()
                .flatMap(new Func1<IpPort, Observable<EbankWifiConnect>>() {
                    @Override
                    public Observable<EbankWifiConnect> call(IpPort ipPort) {
                        return OtherApi.getService().get_EbankWifiConnect(ipPort.getIp(), ipPort.getPort(), ipPort.getMac(), auth);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<EbankWifiConnect>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(SettingCommonActivity.this, "请连接兴农易站相关WiFi后，再点击此处认证上网。", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(EbankWifiConnect ebankWifiConnect) {
                        if ("1".equals(ebankWifiConnect.getStatus()))
                            Toast.makeText(SettingCommonActivity.this, "恭喜你上网认证通过,获得两小时上网时间!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
