package com.ming.slove.mvnew.tab4.mysetting.shoppingaddress;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintEditText;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.ShoppingAddress;
import com.ming.slove.mvnew.tab3.product.ProductPayActivity;
import com.orhanobut.hawk.Hawk;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditShoppingAdressActivity extends BackActivity {

    @Bind(R.id.et_name)
    TintEditText etName;
    @Bind(R.id.et_address)
    TintEditText etAddress;
    @Bind(R.id.et_phone)
    TintEditText etPhone;
    @Bind(R.id.et_code)
    TintEditText etCode;
    @Bind(R.id.check_default)
    CheckBox checkDefault;

    public static final String SHOPPIND_ADDRESS_DATA = "shopping_address_data";
    private ShoppingAddress.DataBean data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shopping_adress);
        ButterKnife.bind(this);

        data = getIntent().getParcelableExtra(SHOPPIND_ADDRESS_DATA);
        if (data == null) {
            setToolbarTitle("新增地址");
        } else {
            setToolbarTitle("编辑地址");
            //编辑时的原始数据
            etName.setText(data.getUname());
            etAddress.setText(data.getAddr());
            etPhone.setText(data.getTel());
            etCode.setText(data.getZipcode());
            if ("1".equals(data.getIs_def())) {
                checkDefault.setChecked(true);
            } else {
                checkDefault.setChecked(false);
            }
        }
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

            //提交的数据
            final String sd_name = etName.getEditableText().toString();
            final String sd_addr = etAddress.getEditableText().toString();
            final String sd_phone = etPhone.getEditableText().toString();
            final String sd_code = etCode.getEditableText().toString();
            String sd_is_def;
            if (checkDefault.isChecked()) {
                sd_is_def = "1";
            } else {
                sd_is_def = "0";
            }
            //数据的验证
            if (StringUtils.isEmpty(sd_name)) {
                Toast.makeText(this, "请输入收件人姓名!", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (StringUtils.isEmpty(sd_addr)) {
                Toast.makeText(this, "请输入收货地址！", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (StringUtils.isEmpty(sd_phone)) {
                Toast.makeText(this, "请输入收件人手机号码！", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (!BaseTools.checkPhone(sd_phone)) {
                Toast.makeText(this, "请输入正确的手机号码！", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (StringUtils.isEmpty(sd_code)) {
                Toast.makeText(this, "请输入邮政编码！", Toast.LENGTH_SHORT).show();
                return true;
            }
            //数据的提交
            String auth = Hawk.get(APPS.USER_AUTH);
            if (data != null) {
                String sd_id = data.getId();
                OtherApi.getService()
                        .post_EditShoppingAddress(auth, sd_id, sd_name, sd_addr, sd_phone, sd_is_def, sd_code)
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
                            public void onNext(Result result) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
            } else {
                OtherApi.getService()
                        .post_AddShoppingAddress(auth, sd_name, sd_addr, sd_phone, sd_is_def, sd_code)
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
                            public void onNext(Result result) {
                                ShoppingAddress.DataBean data2 = new ShoppingAddress.DataBean(sd_name, sd_phone, sd_addr, sd_code);
                                Intent intent = new Intent();
                                intent.putExtra(ProductPayActivity.KEY_USER_ADDR_INFO, data2);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
