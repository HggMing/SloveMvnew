package com.ming.slove.mvnew.tab4.mysetting.mypurse.tab2;

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
import com.ming.slove.mvnew.common.utils.MyItemDecoration2;
import com.ming.slove.mvnew.model.bean.IncomeHistory;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IncomeHistoryActivity extends BackActivity {

    @Bind(R.id.m_x_recyclerview)
    RecyclerView mXRecyclerView;
    @Bind(R.id.content_empty)
    TextView contentEmpty;

    private IncomeHistoryAdapter mAdapter;
    List<IncomeHistory.ListBean> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_history);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_income_history);

        configXRecyclerView();
        loadData();
    }

    private void loadData() {
        String auth = Hawk.get(APPS.USER_AUTH);
        MyServiceClient.getService()
                .get_Income_History(auth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<IncomeHistory>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(IncomeHistory incomeHistory) {
                        mList.addAll(incomeHistory.getList());
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
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new IncomeHistoryAdapter();
        mXRecyclerView.setAdapter(mAdapter);

        mXRecyclerView.addItemDecoration(new MyItemDecoration2(this));//添加分割线
        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
    }

    static class IncomeHistoryAdapter extends BaseRecyclerViewAdapter<IncomeHistory.ListBean, IncomeHistoryAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_common, parent, false);
            return new ViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            IncomeHistory.ListBean data = mList.get(position);

            holder.title.setText(data.getActime() + "月收入");
            holder.content.setText(data.getResult() + "元");
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.icon)
            View icon;
            @Bind(R.id.title)
            TextView title;
            @Bind(R.id.content)
            TextView content;
            @Bind(R.id.arrow)
            View arrow;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
