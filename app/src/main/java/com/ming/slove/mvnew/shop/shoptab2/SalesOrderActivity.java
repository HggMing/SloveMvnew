package com.ming.slove.mvnew.shop.shoptab2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintButton;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.MyServiceClient;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.app.ThemeHelper;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.utils.MyItemDecoration;
import com.ming.slove.mvnew.common.utils.MyItemDecoration2;
import com.ming.slove.mvnew.common.widgets.alipay.PayUtils;
import com.ming.slove.mvnew.model.bean.SalesOrderList;
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
 * 村实惠订单
 */
public class SalesOrderActivity extends BackActivity {

    @Bind(R.id.m_x_recyclerview)
    XRecyclerView mXRecyclerView;
    @Bind(R.id.m_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.content_empty)
    TextView contentEmpty;

    private List<SalesOrderList.ListBean> mList = new ArrayList<>();
    private SalesOrderAdapter mAdapter;

    private int page = 1;
    final private static int PAGE_SIZE = 10;

    private String notify_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_sales_order);
        config();
        initData(page);

        // 刷新时，指示器旋转后变化的颜色
        String theme = ThemeHelper.getThemeColorName(this);
        int themeColorRes = getResources().getIdentifier(theme, "color", getPackageName());
        mRefreshLayout.setColorSchemeResources(themeColorRes);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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

    private void config() {
        //设置recyclerview布局
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mXRecyclerView.addItemDecoration(new NoDecoration(this));//添加空白分割线
//        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画

        //设置adapter
        mAdapter = new SalesOrderAdapter();
        mXRecyclerView.setAdapter(mAdapter);

        mXRecyclerView.setPullRefreshEnabled(false);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                initData(++page);
                mXRecyclerView.loadMoreComplete();
            }
        });

        //点击事件，支付
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SalesOrderList.ListBean data = mList.get(position);
                PayUtils payUtils = new PayUtils(SalesOrderActivity.this, 3);
                payUtils.pay(data.getProducts(), "村实惠订单",
                        data.getOrder_amount(), data.getOrder_sn(), notify_url);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void initData(final int page) {
        String auth = Hawk.get(APPS.USER_AUTH);
        MyServiceClient.getService()
                .get_SalesOrderList(auth, page, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SalesOrderList>() {
                    @Override
                    public void onCompleted() {
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SalesOrderList salesOrderList) {
                        notify_url = salesOrderList.getUrl();
                        mList.addAll(salesOrderList.getList());
                        if (mList.isEmpty()) {
                            contentEmpty.setVisibility(View.VISIBLE);
                            contentEmpty.setText(R.string.empty_orders);
                        } else {
                            contentEmpty.setVisibility(View.GONE);
                        }
                        mAdapter.setItem(mList);
                    }
                });
    }

    static class SalesOrderAdapter extends BaseRecyclerViewAdapter<SalesOrderList.ListBean, SalesOrderAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_tab2_sales, parent, false);
            return new ViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            Context mContext = holder.itemView.getContext();
            SalesOrderList.ListBean data = mList.get(position);

            //购买人
            holder.tvBuyer.setText(data.getBuy_uname());
            //付款状态：1，未付款；2，已付款  3、退款申请中，4、退款成功'
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
                case "100":
                    payStatus = "取消订单";
                    holder.layoutButton.setVisibility(View.GONE);
                    break;
            }
            holder.tvStatus.setText(payStatus);

            //价格显示
            int nums = 0;
            for (SalesOrderList.ListBean.ProductListBean product : data.getProduct_list()) {
                nums += Integer.parseInt(product.getGoods_number());
            }
            holder.tvTotalNumCost.setText("共计" + nums + "件商品，合计:￥" + data.getOrder_amount() + "(含运费￥" + data.getShipping_fee() + ")");
            //支付点击
            holder.btnOrderSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.btnOrderSend, position);
                }
            });

            //内置recycleView配置
            holder.mAdapter.setItem(data.getProduct_list());
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tv_buyer)
            TextView tvBuyer;
            @Bind(R.id.tv_status)
            TextView tvStatus;
            @Bind(R.id.tv_total_num_cost)
            TextView tvTotalNumCost;
            @Bind(R.id.btn_order_send)
            TintButton btnOrderSend;
            @Bind(R.id.layout_button)
            RelativeLayout layoutButton;
            @Bind(R.id.m_item)
            RelativeLayout mItem;
            @Bind(R.id.m_x_recyclerview)
            RecyclerView mXRecyclerView2;

            private SalesOrder2Adapter mAdapter;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                configXRecyclerView(itemView.getContext());
            }

            //配置RecyclerView
            private void configXRecyclerView(Context context) {
                //设置布局和adapter
                mXRecyclerView2.setLayoutManager(new LinearLayoutManager(context));
                mAdapter = new SalesOrder2Adapter();
                mXRecyclerView2.setAdapter(mAdapter);

                mXRecyclerView2.addItemDecoration(new MyItemDecoration2(context));//添加分割线
                mXRecyclerView2.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
                mXRecyclerView2.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
            }
        }
    }

    static class SalesOrder2Adapter extends BaseRecyclerViewAdapter<SalesOrderList.ListBean.ProductListBean, SalesOrder2Adapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_tab2_sales_2, parent, false);
            return new ViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            Context mContext = holder.itemView.getContext();
            SalesOrderList.ListBean.ProductListBean data = mList.get(position);

            //商品图片
            String imageUrl = APPS.BASE_URL + data.getPicurl();
            Glide.with(mContext).load(imageUrl)
                    .centerCrop()
                    .into(holder.img);
            //购买商品简略信息
            holder.tvContent.setText(data.getGoods_name());
            //购买数量
            holder.tvNumber.setText("x" + data.getGoods_number());
            //价格显示
            holder.tvPrice.setText("￥" + data.getGoods_price());

        }


        static class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.img)
            ImageView img;
            @Bind(R.id.tv_content)
            TextView tvContent;
            @Bind(R.id.tv_price)
            TextView tvPrice;
            @Bind(R.id.tv_number)
            TextView tvNumber;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
