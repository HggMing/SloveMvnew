package com.ming.slove.mvnew.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintCheckBox;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BaseFragment;
import com.ming.slove.mvnew.common.base.ClickDialog;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.ui.facelogin.FaceLoginActivity;
import com.ming.slove.mvnew.ui.main.MainActivity;
import com.ming.slove.mvnew.ui.register.TestPhoneNumberActivity;
import com.orhanobut.hawk.Hawk;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ming.slove.mvnew.common.utils.BaseTools.checkNotNull;


/**
 * View
 */
public class LoginFragment extends BaseFragment implements LoginContract.View {
    @Bind(R.id.check_test)
    TintCheckBox checkTest;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.img_jzmm)
    ImageView imgJzmm;
    @Bind(R.id.tv_reg)
    TextView tvReg;
    @Bind(R.id.layout_jzmm)
    RelativeLayout layoutJzmm;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.btn_facelogin)
    Button btnFacelogin;
    @Bind(R.id.tv_forgetpwd)
    TextView tvForgetpwd;
    private LoginContract.Presenter mPresenter;

    private static final int REGISTERED_PHONE = 123;//请求返回已注册手机号
    public static final String REGISTERED_PHONE_NUMBER = "registered_phone_number";//返回已注册手机号


    public static LoginFragment newInstance() {
        return new LoginFragment();
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
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    private void initView(View view) {
        //由参数决定的界面显示初始化
        mPresenter.showView();
        //设置是否为测试服务器的监听
        checkTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPresenter.setTest();
                } else {
                    mPresenter.setNotTest();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.img_jzmm, R.id.tv_reg, R.id.btn_login, R.id.btn_facelogin, R.id.tv_forgetpwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login://登录
                if (!BaseTools.checkNetWorkStatus(getContext())) {
                    String s = "很抱歉，您的网络已经中断，请检查是否连接。";
                    loginFailure(s);
                    return;
                }
                String loginName = etName.getEditableText().toString();
                String loginPwd = etPwd.getEditableText().toString();
                mPresenter.onClickLogin(loginName, loginPwd);
                break;
            case R.id.btn_facelogin://人脸登录
                Intent intent3 = new Intent(getContext(), FaceLoginActivity.class);
                startActivity(intent3);
                break;
            case R.id.img_jzmm://记住密码
                mPresenter.onClickJzmm();
                break;
            case R.id.tv_reg://注册
                Intent intent = new Intent(getContext(), TestPhoneNumberActivity.class);
                intent.putExtra(TestPhoneNumberActivity.LOGIN_NAME, "");
                intent.putExtra(TestPhoneNumberActivity.TYPE, 1);
                startActivityForResult(intent, REGISTERED_PHONE);
                break;
            case R.id.tv_forgetpwd://忘记密码
                Intent intent2 = new Intent(getContext(), TestPhoneNumberActivity.class);
                intent2.putExtra(TestPhoneNumberActivity.LOGIN_NAME, "");
                intent2.putExtra(TestPhoneNumberActivity.TYPE, 2);
                startActivity(intent2);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REGISTERED_PHONE:
                    //注册成功后，让用户输入密码登录
                    String result = data.getStringExtra(REGISTERED_PHONE_NUMBER);
                    etName.setText(result);
                    etPwd.setText("");
                    Hawk.put(APPS.KEY_LOGIN_NAME, result);
                    Hawk.put(APPS.KEY_LOGIN_PASSWORD, "");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void showRemember(String loginName, String loginPwd) {
        if (loginName != null)
            etName.setText(loginName);
        if (loginPwd != null)
            etPwd.setText(loginPwd);
        imgJzmm.setBackgroundResource(R.mipmap.agree);
    }

    @Override
    public void showNotRemember(String loginName) {
        if (loginName != null)
            etName.setText(loginName);
        imgJzmm.setBackgroundResource(R.mipmap.agree_no);
    }

    @Override
    public void showIsTest(boolean isTest) {
        checkTest.setChecked(isTest);
    }

    @Override
    public void showLoginning() {
        btnLogin.setClickable(false);
        btnLogin.setText("登录中...");
    }

    @Override
    public void showCanLogin() {
        etName.setEnabled(true);
        etName.setClickable(true);
        etPwd.setEnabled(true);
        etPwd.setClickable(true);
        btnLogin.setClickable(true);
        btnLogin.setText("登录");
    }

    @Override
    public void loginSuccess() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void loginFailure(String msg) {
        showCanLogin();
        toast(msg);
    }

    @Override
    public void loginFailureToReg(String msg, final String loginname) {
        showCanLogin();
        showDialogOk(msg, new ClickDialog.OnClickDialogOk() {
            @Override
            public void dialogOk() {
                Intent intent = new Intent(getContext(), TestPhoneNumberActivity.class);
                intent.putExtra(TestPhoneNumberActivity.LOGIN_NAME, loginname);
                intent.putExtra(TestPhoneNumberActivity.TYPE, 1);
                startActivity(intent);
            }
        });
    }
}
