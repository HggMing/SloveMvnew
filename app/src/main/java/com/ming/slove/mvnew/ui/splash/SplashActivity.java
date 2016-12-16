package com.ming.slove.mvnew.ui.splash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.common.utils.ActivityUtils;
import com.ming.slove.mvnew.common.utils.BaseTools;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseTools.setFullScreen(this);//隐藏状态栏
        setContentView(R.layout.activity_empty);

        SplashFragment splashFragment =
                (SplashFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (splashFragment == null) {
            splashFragment = SplashFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), splashFragment, R.id.contentFrame);
        }

        new SplashPresenter(splashFragment);
    }
}
