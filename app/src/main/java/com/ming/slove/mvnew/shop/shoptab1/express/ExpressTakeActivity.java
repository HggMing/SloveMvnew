package com.ming.slove.mvnew.shop.shoptab1.express;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.common.utils.MyItemDecoration2;
import com.ming.slove.mvnew.model.bean.ExpressList;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ExpressTakeActivity extends BackActivity {

    private ExpressTakeAdapter mAdapter;
    private List<ExpressList.DataBean.ListBean> mList = new ArrayList<>();
    private final int REQUEST_CODE = 12308;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle(R.string.title_activity_express_take);

        initView();
        initData();
    }

    private void initView() {
        showLoading(true);
        //设置adapter
        mAdapter = new ExpressTakeAdapter();
        addRecycleView(mAdapter);
        mRecyclerView.addItemDecoration(new MyItemDecoration2(this));//添加分割线

        //设置添加按钮
        showFab(mRecyclerView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpressTakeActivity.this, EditExpressTakeActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private void initData() {
        String vid = Hawk.get(APPS.MANAGER_VID);
        OtherApi.getService()
                .get_ExpressList(vid, "2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExpressList>() {
                    @Override
                    public void onCompleted() {
                        showLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ExpressList expressList) {
                        mList.clear();
                        mList.addAll(expressList.getData().getList());
                        if (mList.isEmpty()) {
                            showEmpty(R.string.empty_express_take);
                        } else {
                            hideEmpty();
                        }
                        mAdapter.setItem(mList);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                initData();
            }
        }
    }

    static class ExpressTakeAdapter extends BaseRecyclerViewAdapter<ExpressList.DataBean.ListBean, ExpressTakeAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_tab1_express_take, parent, false);
            return new ViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ExpressList.DataBean.ListBean data = mList.get(position);
            //显示待取件快递单号(圆通  单号：123）
            holder.mTitle.setText(data.getShip() + "\t\t单号：" + data.getNumber());
            //显示代取件发布时间
            String date = data.getCtime();
            String timeFormat = BaseTools.formatDateByFormat(date, "yyyy-MM-dd HH:mm");
            holder.mTime.setText(timeFormat);
            //显示状态信息
            holder.eStatus.setText("未领取");
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.title)
            TextView mTitle;
            @Bind(R.id.time)
            TextView mTime;
            @Bind(R.id.item_layout)
            RelativeLayout mItem;
            @Bind(R.id.express_status)
            TextView eStatus;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
