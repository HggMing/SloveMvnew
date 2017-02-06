package com.ming.slove.mvnew.tab4.mysetting.mypurse;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.base.LazyLoadFragment;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.common.widgets.CustomItem;
import com.ming.slove.mvnew.model.bean.MoneyDetail;
import com.ming.slove.mvnew.model.bean.ResultOther;
import com.ming.slove.mvnew.tab4.mysetting.mypurse.tab1.TakeMoneyActivity;
import com.ming.slove.mvnew.tab4.safesetting.SetPursePwdActivity;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyPurseTab1Fragment extends LazyLoadFragment {

    @Bind(R.id.money)
    TextView money;
    @Bind(R.id.m_x_recyclerview)
    RecyclerView mXRecyclerView;
    @Bind(R.id.content_empty)
    TextView contentEmpty;
    @Bind(R.id.take_money)
    CustomItem takeMoney;


    private MyPurseAdapter mAdapter;
    List<MoneyDetail.DataBean.ListBean> mList = new ArrayList<>();
    private String auth;

    @Override
    public int getLayout() {
        return R.layout.fragment_my_purse;
    }

    @Override
    public void initViews(View view) {
        configXRecyclerView();//XRecyclerView配置
    }

    @Override
    public void loadData() {
        //显示账号余额
        auth = Hawk.get(APPS.USER_AUTH);
        OtherApi.getService()
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
                        money.setText(resultOther.getMoney());
                    }
                });
        //显示收支明细
        OtherApi.getService()
                .get_MoneyDetail(auth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MoneyDetail>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MoneyDetail moneyDetail) {
                        mList.addAll(moneyDetail.getData().getList());
                        if (mList.isEmpty() || mList == null) {
                            contentEmpty.setVisibility(View.VISIBLE);
                        } else {
                            contentEmpty.setVisibility(View.GONE);
                        }
                        mAdapter.setItem(mList);
                    }
                });
    }

    //配置RecyclerView
    private void configXRecyclerView() {
        //设置布局和adapter
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MyPurseAdapter();
        mXRecyclerView.setAdapter(mAdapter);

//        mXRecyclerView.addItemDecoration(new MyItemDecoration(getContext()));//添加分割线
        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
    }

    //点击提现
    @OnClick(R.id.take_money)
    public void onClick() {
        //检测是否设置钱包密码
        OtherApi.getService()
                .get_IsSetPWD(auth)
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
                        if (resultOther.getIs_pwd() == 1) {//已设置密码
//                            Toast.makeText(MyPurseActivity.this, "提现", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), TakeMoneyActivity.class);
                            startActivity(intent);
                        } else {
                            //设置钱包密码
                            Intent intent = new Intent(getContext(), SetPursePwdActivity.class);
                            intent.putExtra(SetPursePwdActivity.TYPE, 1);
                            startActivity(intent);
                        }
                    }
                });
    }

    static class MyPurseAdapter extends BaseRecyclerViewAdapter<MoneyDetail.DataBean.ListBean, MyPurseAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab4_my_purse, parent, false);
            return new ViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Context mContext = holder.itemView.getContext();
            MoneyDetail.DataBean.ListBean data = mList.get(position);

            //显示奖励类型
            int type = Integer.valueOf(data.getTypes());
            String[] titleName = mContext.getResources().getStringArray(R.array.money_details);
            if (type < titleName.length) {
                holder.title.setText(titleName[type]);
            } else {
                holder.title.setText("其它");
            }

            //显示时间
            String date = data.getCtime();
            String timeFormat = BaseTools.getTimeFormat(date);
            holder.time.setText(timeFormat);

            //显示获得方式备注
            holder.content.setText(data.getMemo());
            //显示收支（type245为负）
            String money = data.getMoney();
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


