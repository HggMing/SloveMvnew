package com.ming.slove.mvnew.ui.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.common.utils.ActivityUtils;
import com.ming.slove.mvnew.common.utils.BaseTools;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseTools.setFullScreen(this);//隐藏状态栏
        setContentView(R.layout.activity_empty);

        LoginFragment loginFragment =
                (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), loginFragment, R.id.contentFrame);
        }

        new LoginPresenter(loginFragment);
    }
}
