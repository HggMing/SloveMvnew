package com.ming.slove.mvnew.tab4.mysetting.mypurse.tab2;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.model.bean.IncomeBase;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IncomeBaseActivity extends BackActivity {

    private IncomeBaseAdapter mAdapter;
    List<IncomeBase.ListBean> mList = new ArrayList<>();

    public static final String TYPE = "key_type";
    private int type;//1、基础收入2、业务提成

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getIntent().getIntExtra(TYPE, 1);
        if (type == 1) {
            setToolbarTitle(R.string.title_activity_income_base);
        } else {
            setToolbarTitle(R.string.title_activity_income_business);
        }

        initView();
        loadData();
    }
    private void initView() {
        showLoading(true);
        //设置布局和adapter
        mAdapter = new IncomeBaseAdapter(type);
        addRecycleView(mAdapter);
    }

    private void loadData() {
        Subscriber<IncomeBase> subscriber = new Subscriber<IncomeBase>() {
            @Override
            public void onCompleted() {
                showLoading(false);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(IncomeBase incomeBase) {
                mList.addAll(incomeBase.getList());
                if (mList.isEmpty() || mList == null) {
                    if(type==1){
                        showEmpty(R.string.empty_income_base);
                    }else{
                        showEmpty(R.string.empty_income_business);
                    }
                } else {
                    hideEmpty();
                }
                mAdapter.setItem(mList);
            }
        };

        String auth = Hawk.get(APPS.USER_AUTH);
        if (type == 1) {
            OtherApi.getService()
                    .get_Income_Base(auth)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        } else {
            OtherApi.getService()
                    .get_Income_Business(auth)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }
    }



    static class IncomeBaseAdapter extends BaseRecyclerViewAdapter<IncomeBase.ListBean, IncomeBaseAdapter.ViewHolder> {
        int type;

        public IncomeBaseAdapter(int type) {
            this.type = type;
        }

        @Override
        public IncomeBaseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab4_my_purse, parent, false);
            return new IncomeBaseAdapter.ViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(IncomeBaseAdapter.ViewHolder holder, int position) {
            Context mContext = holder.itemView.getContext();
            IncomeBase.ListBean data = mList.get(position);

            //显示奖励类型
            if(type==1){
                holder.title.setText("基础收支");
            }else{
                holder.title.setText("业务提成");
            }

            //显示时间
            String date = data.getAtime();
            holder.time.setText(date);

            //显示获得方式备注
            holder.content.setText(data.getMemo());
            //显示收支
            String money = data.getRealmoney();
            if (money.contains("-")) {
                holder.pointsChange.setText(money);
                holder.pointsChange.setTextColor(mContext.getResources().getColor(R.color.color08));
            } else if (money.contains("+")) {
                holder.pointsChange.setText(money);
                holder.pointsChange.setTextColor(mContext.getResources().getColor(R.color.font_green));
            } else {
                holder.pointsChange.setText("+" + money);
                holder.pointsChange.setTextColor(mContext.getResources().getColor(R.color.font_green));
            }
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.title)
            TextView title;
            @Bind(R.id.time)
            TextView time;
            @Bind(R.id.content)
            TextView content;
            @Bind(R.id.pointsChange)
            TextView pointsChange;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
