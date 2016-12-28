package com.ming.slove.mvnew.tab4.mysetting.mypurse.tab2;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.MyServiceClient;
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

    @Bind(R.id.m_x_recyclerview)
    RecyclerView mXRecyclerView;
    @Bind(R.id.content_empty)
    TextView contentEmpty;

    private IncomeBaseAdapter mAdapter;
    List<IncomeBase.ListBean> mList = new ArrayList<>();

    public static final String TYPE = "key_type";
    private int type;//1、基础收入2、业务提成

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_base);
        ButterKnife.bind(this);

        type = getIntent().getIntExtra(TYPE, 1);
        if (type == 1) {
            setToolbarTitle(R.string.title_activity_income_base);
        } else {
            setToolbarTitle(R.string.title_activity_income_business);
        }

        configXRecyclerView();
        loadData();
    }

    private void loadData() {
        Subscriber<IncomeBase> subscriber = new Subscriber<IncomeBase>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(IncomeBase incomeBase) {
                mList.addAll(incomeBase.getList());
                if (mList.isEmpty() || mList == null) {
                    contentEmpty.setVisibility(View.VISIBLE);
                    if(type==1){
                        contentEmpty.setText("本月暂无基础收支明细！");
                    }else{
                        contentEmpty.setText("本月暂无业务提成记录！");
                    }
                } else {
                    contentEmpty.setVisibility(View.GONE);
                }
                mAdapter.setItem(mList);
            }
        };

        String auth = Hawk.get(APPS.USER_AUTH);
        if (type == 1) {
            MyServiceClient.getService()
                    .get_Income_Base(auth)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        } else {
            MyServiceClient.getService()
                    .get_Income_Business(auth)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }
    }

    //配置RecyclerView
    private void configXRecyclerView() {
        //设置布局和adapter
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new IncomeBaseAdapter(type);
        mXRecyclerView.setAdapter(mAdapter);

//        mXRecyclerView.addItemDecoration(new MyItemDecoration(this));//添加分割线
        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
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
