package com.ming.slove.mvnew.tab4.safesetting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintButton;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.MyServiceClient;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.model.bean.Result;
import com.orhanobut.hawk.Hawk;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChangePursePwdActivity extends BackActivity {

    @Bind(R.id.et_oldpwd)
    EditText etOldpwd;
    @Bind(R.id.et_newpwd1)
    EditText etNewpwd1;
    @Bind(R.id.et_newpwd2)
    EditText etNewpwd2;
    @Bind(R.id.btn_get_rcode)
    TintButton btnGetRcode;

    private String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_purse_pwd);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_change_purse_pwd);

        auth = Hawk.get(APPS.USER_AUTH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 将菜单图标添加到toolbar
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_submit) {

            String oldpwd = etOldpwd.getEditableText().toString();
            String newpwd1 = etNewpwd1.getEditableText().toString();
            String newpwd2 = etNewpwd2.getEditableText().toString();

            if (StringUtils.isEmpty(oldpwd)) {
                Toast.makeText(this, "旧密码不能为空", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (StringUtils.isEmpty(newpwd1)) {
                Toast.makeText(this, "新密码不能为空", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (StringUtils.isEmpty(newpwd2)) {
                Toast.makeText(this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (!newpwd1.equals(newpwd2)) {
                Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (newpwd1.length() < 6) {
                Toast.makeText(this, "密码必须在6位", Toast.LENGTH_SHORT).show();
                return true;
            }
            MyServiceClient.getService()
                    .post_EditPursePWD(auth, oldpwd, newpwd1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Result>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(final Result result) {
                            if (result.getErr() == 90003) {
                                result.setMsg("原始钱包密码错误，请重新输入");
                            }
                            MyDialog.Builder builder = new MyDialog.Builder(ChangePursePwdActivity.this);
                            builder.setTitle("提示")
                                    .setCannel(false)
                                    .setMessage(result.getMsg())
                                    .setPositiveButton("确定",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    dialog.dismiss();
                                                    if (result.getErr() == 0) {
                                                        ChangePursePwdActivity.this.finish();
                                                    }
                                                }

                                            });
                            if (!isFinishing()) {
                                builder.create().show();
                            }
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //点击忘记钱包密码，进入重置钱包密码页面
    @OnClick(R.id.btn_get_rcode)
    public void onClick() {
        Intent intent = new Intent(this, ResetPursePwdActivity.class);
        startActivity(intent);
        finish();
    }
}
