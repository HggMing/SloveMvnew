package com.ming.slove.mvnew.tab4.mysetting.mypurse;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.MyServiceClient;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.LazyLoadFragment;
import com.ming.slove.mvnew.common.widgets.CustomItem;
import com.ming.slove.mvnew.model.bean.IncomeMonth;
import com.ming.slove.mvnew.model.bean.ResultOther;
import com.ming.slove.mvnew.tab4.mysetting.mypurse.tab2.IncomeBaseActivity;
import com.ming.slove.mvnew.tab4.mysetting.mypurse.tab2.IncomeHistoryActivity;
import com.ming.slove.mvnew.tab4.mysetting.mypurse.tab2.IncomeRewardActivity;
import com.orhanobut.hawk.Hawk;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MyPurseTab2Fragment extends LazyLoadFragment {

    @Bind(R.id.money)
    TextView money;
    @Bind(R.id.income_base)
    CustomItem incomeBase;
    @Bind(R.id.income_business)
    CustomItem incomeBusiness;
    @Bind(R.id.income_reward)
    CustomItem incomeReward;
    @Bind(R.id.income_history)
    CustomItem incomeHistory;

    private String auth;

    @Override
    public int getLayout() {
        return R.layout.fragment_my_purse_month;
    }

    @Override
    public void initViews(View view) {
        auth = Hawk.get(APPS.USER_AUTH);
    }

    @Override
    public void loadData() {
        //显示账号本月收入
        MyServiceClient.getService()
                .get_Money_Month(auth)
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
                        money.setText(resultOther.getAccount());
                    }
                });
        //当月收入概览
        MyServiceClient.getService()
                .get_Income_Month(auth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<IncomeMonth, Observable<IncomeMonth.ListBean>>() {
                    @Override
                    public Observable<IncomeMonth.ListBean> call(IncomeMonth incomeMonth) {
                        return Observable.from(incomeMonth.getList());
                    }
                })
                .subscribe(new Subscriber<IncomeMonth.ListBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(IncomeMonth.ListBean listBean) {
                        String type = listBean.getType();
                        String money = listBean.getMoney() + "元";
                        if ("jichu".equals(type)) incomeBase.setContent(money);
                        if ("fandian".equals(type)) incomeReward.setContent(money);
                        if ("tichen".equals(type)) incomeBusiness.setContent(money);
                    }
                });
    }

    @OnClick({R.id.income_base, R.id.income_business, R.id.income_reward, R.id.income_history})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.income_base://基础收入
                Intent intent1 = new Intent(getContext(), IncomeBaseActivity.class);
                intent1.putExtra(IncomeBaseActivity.TYPE,1);
                startActivity(intent1);
                break;
            case R.id.income_business://业务提成 //// FIXME: 2016/12/28 返回不太确定
                Intent intent2 = new Intent(getContext(), IncomeBaseActivity.class);
                intent2.putExtra(IncomeBaseActivity.TYPE,2);
                startActivity(intent2);
                break;
            case R.id.income_reward://返点奖励
                Intent intent3 = new Intent(getContext(), IncomeRewardActivity.class);
                startActivity(intent3);
                break;
            case R.id.income_history://历史收入
                Intent intent4 = new Intent(getContext(), IncomeHistoryActivity.class);
                startActivity(intent4);
                break;
        }
    }
}


