package com.ming.slove.mvnew.tab4.scommon;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.common.base.BackActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ming on 2016/3/3.
 */
public class AboutActivity extends BackActivity {
    @Bind(R.id.version)
    TextView version;
    @Bind(R.id.about_update)
    TextView aboutUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_about);

        initAboutActivity();
    }
    /**
     * 用于显示程序版本号
     */
    final void initAboutActivity() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pInfo.versionName;

            String versionString = String.format("版本：%s", versionName);
            version.setText(versionString);

            String changeLog= Hawk.get(versionName);
            aboutUpdate.setText(changeLog);

        } catch (Exception e) {
            errorLog(e);
        }
    }

    private static void errorLog(Exception e) {
        if (e == null) {
            return;
        }
        e.printStackTrace();
        Log.e("", "" + e);
    }
}

