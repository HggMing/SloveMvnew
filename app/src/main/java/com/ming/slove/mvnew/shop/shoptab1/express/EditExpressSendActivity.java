package com.ming.slove.mvnew.shop.shoptab1.express;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintEditText;
import com.bilibili.magicasakura.widgets.TintImageView;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.common.widgets.MySpinner;
import com.ming.slove.mvnew.common.widgets.scanner.MyScannerActivity;
import com.ming.slove.mvnew.model.bean.ExpressFirm;
import com.ming.slove.mvnew.model.bean.ExpressList;
import com.ming.slove.mvnew.model.bean.Result;
import com.orhanobut.hawk.Hawk;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditExpressSendActivity extends BackActivity {

    @Bind(R.id.et_num)
    TintEditText etNum;
    @Bind(R.id.scan)
    TintImageView scan;
    @Bind(R.id.my_spinner)
    MySpinner mySp;
    @Bind(R.id.et_ship)
    TintEditText etShip;
    @Bind(R.id.et_money)
    TintEditText etMoney;
    @Bind(R.id.et_name)
    TintEditText etName;
    @Bind(R.id.et_phone)
    TintEditText etPhone;
    @Bind(R.id.et_address)
    TintEditText etAddress;
    @Bind(R.id.content)
    LinearLayout content;

    private ArrayAdapter<String> mAdapter;
    private String expressFirm;
    private String id_express;

    private final int REQUEST_CODE = 12556;
    public static String EXPRESS_SEND_DATA = "express_send_data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_express_send);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_edit_express_send);

        initData();
    }

    private void initData() {
        ExpressList.DataBean.ListBean data = getIntent().getParcelableExtra(EXPRESS_SEND_DATA);
        id_express = data.getId();
        if (!StringUtils.isEmpty(data.getNumber())) {
            etNum.setText(data.getNumber());//快递单号
            etMoney.setText(data.getShip_fee());//寄件费
            etShip.setVisibility(View.VISIBLE);
            mySp.setVisibility(View.GONE);
            etShip.setText(data.getShip());
            expressFirm = data.getShip();
        } else {
            etShip.setVisibility(View.GONE);
            mySp.setVisibility(View.VISIBLE);
            //选择设置快递公司
            settingExpressFrim();
        }

        etName.setText(data.getUname());//寄件人姓名
        etPhone.setText(data.getJphone());//寄件人电话
        etAddress.setText(data.getAddr());//寄件人地址
    }

    private void settingExpressFrim() {
        String auth = Hawk.get(APPS.USER_AUTH);
        mAdapter = mySp.getAdapter(this);
        //可选数据列表
        OtherApi.getService()
                .get_ExpressFirm(auth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExpressFirm>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ExpressFirm expressFirm) {
                        mAdapter.clear();
                        mAdapter.add("请选择快递公司");
                        for (ExpressFirm.DataBean firm : expressFirm.getData()) {
                            mAdapter.add(firm.getShipname());
                        }
                    }
                });
        //选择监听
        mySp.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    expressFirm = mAdapter.getItem(position);
                } else {
                    expressFirm = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
            String number = etNum.getEditableText().toString();
            String money = etMoney.getEditableText().toString();
            String name = etName.getEditableText().toString();
            String phone = etPhone.getEditableText().toString();
            String addr = etAddress.getEditableText().toString();

            if (StringUtils.isEmpty(number)) {
                Toast.makeText(this, "快递单号不能为空，可扫码快速添加！", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (StringUtils.isEmpty(money)) {
                Toast.makeText(this, "寄件金额不能为空，若无，请输入0！", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (mySp.getVisibility() == View.VISIBLE && StringUtils.isEmpty(expressFirm)) {
                Toast.makeText(this, "请选择快递公司！", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (!StringUtils.isEmpty(phone) && !BaseTools.checkPhone(phone)) {
                Toast.makeText(this, "输入的手机号码格式不正确！", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (StringUtils.isEmpty(phone)) {
                Toast.makeText(this, "请输入寄件人手机号码！", Toast.LENGTH_SHORT).show();
                return true;
            }
            String vid = Hawk.get(APPS.MANAGER_VID);
            OtherApi.getService()
                    .post_EditExpressSend(id_express, number, expressFirm, money, name, phone, addr)
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
                            if (result.getErr() == 0) {
                                setResult(RESULT_OK);
                                finish();
                            }
                            Toast.makeText(EditExpressSendActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.scan)
    public void onClick() {
        Intent intent = new Intent(this, MyScannerActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String numberScan = data.getStringExtra(MyScannerActivity.SCAN_RESULT);
                etNum.setText(numberScan);
            }
        }
    }
}
