package com.ming.slove.mvnew.shop.shoptab2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintButton;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.MyServiceClient;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.common.widgets.alipay.PayUtils;
import com.ming.slove.mvnew.model.bean.InsuranceOrderList;
import com.ming.slove.mvnew.shop.shoptab1.books.NoDecoration;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 汽车保险订单
 */
public class InsuranceOrderActivity extends BackActivity {
    private List<InsuranceOrderList.ListBean> mList = new ArrayList<>();
    private InsuranceOrderAdapter mAdapter;

    private int page = 1;
    final private static int PAGE_SIZE = 10;

    private String notify_url;

    private String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle(R.string.title_activity_insurance_order);

        initView();
        initData(page);
    }

    private void initView() {
        auth = Hawk.get(APPS.USER_AUTH);
        showLoading(true);
        //设置adapter
        mAdapter = new InsuranceOrderAdapter();
        addXRecycleView(mAdapter, new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                initData(++page);
                mXRecyclerView.loadMoreComplete();
            }
        });
        mXRecyclerView.addItemDecoration(new NoDecoration(this));//添加空白分割线

        //点击事件，支付
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                InsuranceOrderList.ListBean data = mList.get(position);
                PayUtils payUtils = new PayUtils(InsuranceOrderActivity.this, 2);
                payUtils.pay("汽车保险", "汽车保险",
                        data.getPrice(), data.getOrder_sn(), notify_url);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        //下拉刷新
        showRefresh(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.setItem(null);
                mList.clear();
                page = 1;
                initData(page);
            }
        });
    }

    //支付宝调用后刷新订单界面
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mAdapter.setItem(null);
        mList.clear();
        page = 1;
        initData(page);
    }

    private void initData(final int page) {
        MyServiceClient.getService()
                .get_InsuranceOrderList(auth, page, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InsuranceOrderList>() {
                    @Override
                    public void onCompleted() {
                        hideRefresh();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(InsuranceOrderList insuranceOrderList) {
                        notify_url = insuranceOrderList.getUrl();
                        mList.addAll(insuranceOrderList.getList());
                        if (mList.isEmpty()) {
                            showEmpty(R.string.empty_orders);
                        } else {
                            hideEmpty();
                        }
                        mAdapter.setItem(mList);
                    }
                });
    }

    static class InsuranceOrderAdapter extends BaseRecyclerViewAdapter<InsuranceOrderList.ListBean, InsuranceOrderAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_tab2_insurance, parent, false);
            return new ViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Context mContext = holder.itemView.getContext();
            final InsuranceOrderList.ListBean data = mList.get(position);
            //购买人
            holder.tvBuyer.setText(data.getC_no());
            //付款状态 1：未付款 2：已付款 3：退款申请中4：退款成功
            String payStatus = "未付款";
            switch (data.getPay_status()) {
                case "1":
                    payStatus = "未付款";
                    holder.layoutButton.setVisibility(View.VISIBLE);
                    break;
                case "2":
                    payStatus = "已付款";
                    holder.layoutButton.setVisibility(View.GONE);
                    break;
                case "3":
                    payStatus = "退款申请中";
                    holder.layoutButton.setVisibility(View.GONE);
                    break;
                case "4":
                    payStatus = "退款成功";
                    holder.layoutButton.setVisibility(View.GONE);
                    break;
            }
            holder.tvStatus.setText(payStatus);
            //购买人信息
            holder.tvName.setText("车主姓名：" + data.getC_name());
            holder.tvNumber.setText("车牌号码：" + data.getC_number());
            holder.tvPrice.setText("保险费用：￥" + data.getPrice());
            //投保时间
            final String date = data.getCtime();
            String timeFormat = BaseTools.formatDateByFormat(date, "yyyy-MM-dd");
            holder.tvTime.setText("投保时间：" + timeFormat);
            //支付点击
            holder.btnOrderSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.btnOrderSend, position);
                }
            });
        }


        static class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tv_buyer)
            TextView tvBuyer;
            @Bind(R.id.tv_status)
            TextView tvStatus;
            @Bind(R.id.tv_name)
            TextView tvName;
            @Bind(R.id.tv_number)
            TextView tvNumber;
            @Bind(R.id.tv_price)
            TextView tvPrice;
            @Bind(R.id.tv_time)
            TextView tvTime;
            @Bind(R.id.btn_order_send)
            TintButton btnOrderSend;
            @Bind(R.id.layout_button)
            RelativeLayout layoutButton;
            @Bind(R.id.m_item)
            RelativeLayout mItem;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
