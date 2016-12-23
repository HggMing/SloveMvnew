package com.ming.slove.mvnew.tab4.mysetting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.MyServiceClient;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.model.bean.ResultOther;
import com.ming.slove.mvnew.tab1.BrowserActivity;
import com.ming.slove.mvnew.tab4.mysetting.mypurse.MyPurseActivity;
import com.ming.slove.mvnew.tab4.mysetting.shoppingaddress.ShoppingAddressActivity;
import com.orhanobut.hawk.Hawk;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MySettingActivity extends BackActivity {

    @Bind(R.id.tv_credit_num)
    TextView tvCreditNum;

    private String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_mysetting);
        initView();
    }

    private void initView() {
        //显示账号余额
        auth = Hawk.get(APPS.USER_AUTH);
        MyServiceClient.getService()
                .get_Money(auth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultOther>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResultOther resultOther) {
                        tvCreditNum.setText(resultOther.getPingfen()+"分");
                    }
                });
    }

    @OnClick({R.id.click_my_order, R.id.click_shopping_address, R.id.click_my_purse, R.id.click_my_auction})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.click_my_purse:
//                Toast.makeText(MySettingActivity.this, "我的钱包", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MySettingActivity.this, MyPurseActivity.class);
                startActivity(intent);
                break;
            case R.id.click_my_order:
                Toast.makeText(MySettingActivity.this, "我的订单", Toast.LENGTH_SHORT).show();
                break;
            case R.id.click_shopping_address:
//                Toast.makeText(MySettingActivity.this, "我的收货地址", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(MySettingActivity.this, ShoppingAddressActivity.class);
                startActivity(intent2);
                break;
            case R.id.click_my_auction:
//                Toast.makeText(MySettingActivity.this, "我的拍卖", Toast.LENGTH_SHORT).show();
                String url= APPS.BASE_URL+"/auction/my_auction?auth="+auth;
                Intent intent3 = new Intent(this, BrowserActivity.class);
                intent3.putExtra(BrowserActivity.KEY_URL, url);
                startActivity(intent3);
                break;
        }
    }
}
