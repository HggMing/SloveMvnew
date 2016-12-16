package com.ming.slove.mvnew.ui.facelogin;

import android.os.Bundle;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.common.base.Back2Activity;
import com.ming.slove.mvnew.common.utils.ActivityUtils;

public class FaceLoginActivity extends Back2Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle(R.string.title_activity_facelogin);

        FaceLoginFragment faceloginFragment =
                (FaceLoginFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (faceloginFragment == null) {
            faceloginFragment = FaceLoginFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), faceloginFragment, R.id.contentFrame);
        }

        new FaceLoginPresenter(faceloginFragment);
    }

    @Override
    protected void setActivityContent() {
        setContentView(R.layout.activity_toolbar2);
    }
}
