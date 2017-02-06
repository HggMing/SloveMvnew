package com.ming.slove.mvnew.tab4.mysetting.mypurse.tab2;

import android.os.Bundle;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.widgets.CustomItem;
import com.ming.slove.mvnew.model.bean.IncomeReward;
import com.orhanobut.hawk.Hawk;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IncomeRewardActivity extends BackActivity {

    @Bind(R.id.item_1)
    CustomItem item1;
    @Bind(R.id.item_2)
    CustomItem item2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_reward);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_income_reward);

        loadData();
    }

    private void loadData() {
        //显示账号本月返点奖励
        String auth = Hawk.get(APPS.USER_AUTH);
        OtherApi.getService()
                .get_Income_Reward(auth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<IncomeReward>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(IncomeReward incomeReward) {
                        item1.setText(incomeReward.getList().get(0).getMemo());
                        item1.setContent(incomeReward.getList().get(0).getRealmoney() + "元");

                        item2.setText(incomeReward.getList().get(1).getMemo());
                        item2.setContent(incomeReward.getList().get(1).getRealmoney() + "元");
                    }
                });
    }
}
