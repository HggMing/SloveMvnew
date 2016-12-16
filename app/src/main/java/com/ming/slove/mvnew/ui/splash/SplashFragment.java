package com.ming.slove.mvnew.ui.splash;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.common.base.BaseFragment;
import com.ming.slove.mvnew.ui.login.LoginActivity;
import com.ming.slove.mvnew.ui.main.MainActivity;
import com.tbruyelle.rxpermissions.RxPermissions;

import static com.ming.slove.mvnew.common.utils.BaseTools.checkNotNull;


/**
 * View
 */
public class SplashFragment extends BaseFragment implements SplashContract.View {
    private SplashContract.Presenter mPresenter;

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(SplashContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        //设置动画
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);//透明度从0.3到不透明变化
        aa.setDuration(100);//动画持续时长
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                //android6.0 获取运行时权限
                mPresenter.requestPermission(new RxPermissions(getActivity()));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    @Override
    public void goHome() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void goLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
