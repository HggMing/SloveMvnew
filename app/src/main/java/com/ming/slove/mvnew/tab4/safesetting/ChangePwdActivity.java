package com.ming.slove.mvnew.tab4.safesetting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.model.bean.Result;
import com.orhanobut.hawk.Hawk;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePwdActivity extends BackActivity {

    @Bind(R.id.et_oldpwd)
    EditText etOldpwd;
    @Bind(R.id.et_newpwd1)
    EditText etNewpwd1;
    @Bind(R.id.et_newpwd2)
    EditText etNewpwd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_change_pwd);
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
            if (newpwd1.length() < 6 || newpwd1.length() > 16) {
                Toast.makeText(this, "密码必须在6-16位", Toast.LENGTH_SHORT).show();
                return true;
            }
            String auth = Hawk.get(APPS.USER_AUTH);
            OtherApi.getService().getCall_ChangePwd(auth, oldpwd, newpwd1)
                    .enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            if (response.isSuccessful()) {
                                final Result changePwdResult = response.body();
                                if (changePwdResult != null) {
                                    MyDialog.Builder builder2 = new MyDialog.Builder(ChangePwdActivity.this);
                                    builder2.setTitle("提示")
                                            .setCannel(false)
                                            .setMessage(changePwdResult.getMsg())
                                            .setPositiveButton("确定",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            dialog.dismiss();
                                                            if (changePwdResult.getErr() == 0) {
                                                                ChangePwdActivity.this.finish();
                                                            }
                                                        }

                                                    });
                                    if (!isFinishing()) {
                                        builder2.create().show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            Toast.makeText(ChangePwdActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
