package com.ming.slove.mvnew.tab4.mysetting.myorder;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintProgressBar;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.app.ThemeHelper;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.base.LazyLoadFragment;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.model.bean.MyOrderList;
import com.ming.slove.mvnew.model.bean.ProductNewOrder;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.ShoppingAddress;
import com.ming.slove.mvnew.model.event.ProductBuyEvent;
import com.ming.slove.mvnew.model.event.RefreshMyOrderListEvent;
import com.ming.slove.mvnew.shop.shoptab1.books.NoDecoration;
import com.ming.slove.mvnew.tab1.BrowserActivity;
import com.ming.slove.mvnew.tab3.product.ProductPayActivity;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Ming on 2016/11/15.
 */
public class MyOrderListFragment extends LazyLoadFragment {

    @Bind(R.id.m_x_recyclerview)
    XRecyclerView mXRecyclerView;
    @Bind(R.id.m_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.content_empty)
    TextView contentEmpty;
    @Bind(R.id.progressBar)
    TintProgressBar mProgressBar;

    private List<MyOrderList.DataBean.ListBean> mList = new ArrayList<>();
    private MyOrderAdapter mAdapter;

    private int page = 1;
    final private static int PAGE_SIZE = 10;
    private String notify_url;
    private String auth;

    private int fromWhere;
    private String payStatus;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_orders;
    }

    @Override
    public void initViews(View view) {
        auth = Hawk.get(APPS.USER_AUTH);
        EventBus.getDefault().register(this);

        //不同tab展示
        fromWhere = getArguments().getInt(MyOrderListActivity.ORDER_TYPE, 0);
        switch (fromWhere) {
            case 1://待付款
                payStatus = "1";
                break;
            case 2://待发货
                payStatus = "2";
                break;
            case 3://待收货
                payStatus = "4";
                break;
            case 4://待评价
                payStatus = "5";
                break;
        }

        // 刷新时，指示器旋转后变化的颜色
        String theme = ThemeHelper.getThemeColorName(getContext());
        int themeColorRes = getResources().getIdentifier(theme, "color", getContext().getPackageName());
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
        //设置recyclerview布局
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mXRecyclerView.addItemDecoration(new NoDecoration(getContext()));//添加空白分割线
//        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画

        //设置adapter
        mAdapter = new MyOrderAdapter();
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

        //确认收货or取消订单后，刷新列表
        final Subscriber<Result> mResultSubscriber = new Subscriber<Result>() {
            @Override
            public void onCompleted() {
                EventBus.getDefault().post(new RefreshMyOrderListEvent());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Result result) {

            }
        };

        //点击事件，支付详情页面
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                switch (view.getId()) {
                    case R.id.btn_01://去支付
                        MyOrderList.DataBean.ListBean data = mList.get(position);
                        //传入订单支付信息
                        double price = Double.parseDouble(data.getOrder_amount());
                        ProductNewOrder.DataBean dataFromOrder = new ProductNewOrder.DataBean(data.getOrder_sn(), price, notify_url);
                        //传入购买列表信息
                        EventBus.getDefault().postSticky(new ProductBuyEvent(new BigDecimal(data.getOrder_amount()),
                                mAdapter.buyProductList,
                                dataFromOrder));

                        //传入收货地址信息
                        ShoppingAddress.DataBean userAddrInfo = new ShoppingAddress.DataBean(data.getBuy_uname(), data.getTel(), data.getAddress(), data.getZipcode());
                        Intent intent = new Intent(getContext(), ProductPayActivity.class);
                        intent.putExtra(ProductPayActivity.KEY_USER_ADDR_INFO, userAddrInfo);
                        intent.putExtra(ProductPayActivity.KEY_FROM_WHERE, 1);
                        startActivity(intent);
                        break;
                    case R.id.btn_02://确认收货or去评价
                        String a = mList.get(position).getPay_status();
                        switch (a) {
                            case "4"://确认收货
                                MyDialog.Builder builder = new MyDialog.Builder(getContext());
                                builder.setTitle("提示")
                                        .setMessage("确认收货？")
                                        .setPositiveButton("确定",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        OtherApi.getService().post_OrderConfirm(auth, mList.get(position).getOrder_sn())
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(mResultSubscriber);
                                                        dialog.dismiss();
                                                    }
                                                })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                if (!getActivity().isFinishing()) {
                                    builder.create().show();
                                }
                                break;
                            case "5"://去评价//TODO: 2017/1/6
                                Toast.makeText(getContext(), "去评价", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        break;
                    case R.id.btn_03://查看物流
                        String url = APPS.BASE_URL + "/express/kd_order?number=" + mList.get(position).getExpress_sn();
                        Intent intent3 = new Intent(getContext(), BrowserActivity.class);
                        intent3.putExtra(BrowserActivity.KEY_URL, url);
                        intent3.putExtra(BrowserActivity.WEB_TITLE, "物流详情");
                        startActivity(intent3);
                        break;
                    case R.id.btn_04://取消
                        MyDialog.Builder builder = new MyDialog.Builder(getContext());
                        builder.setTitle("提示")
                                .setMessage("确认要取消这个订单？")
                                .setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                OtherApi.getService().post_OrderCancel(auth, mList.get(position).getOrder_sn())
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(mResultSubscriber);
                                                dialog.dismiss();
                                            }
                                        })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        if (!getActivity().isFinishing()) {
                            builder.create().show();
                        }
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    public void loadData() {
        //加载中动画
        mProgressBar.setVisibility(View.VISIBLE);
        initData(page);
    }

    private void initData(final int page) {
        Subscriber<MyOrderList.DataBean.ListBean> myOrderListSubscriber = new Subscriber<MyOrderList.DataBean.ListBean>() {
            @Override
            public void onCompleted() {
                mProgressBar.setVisibility(View.GONE);
                if (mRefreshLayout != null)
                    mRefreshLayout.setRefreshing(false);
                if (mList.isEmpty()) {
                    contentEmpty.setVisibility(View.VISIBLE);
                    contentEmpty.setText(R.string.empty_orders2);
                } else {
                    contentEmpty.setVisibility(View.GONE);
                }
                mAdapter.setItem(mList);
            }

            @Override
            public void onError(Throwable e) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onNext(MyOrderList.DataBean.ListBean listBean) {
                mList.add(listBean);
            }
        };

        if (fromWhere == 0) {//全部
            OtherApi.getService()
                    .get_OrderList(auth, page, PAGE_SIZE)
                    .subscribeOn(Schedulers.io())
                    .flatMap(new Func1<MyOrderList, Observable<MyOrderList.DataBean.ListBean>>() {
                        @Override
                        public Observable<MyOrderList.DataBean.ListBean> call(MyOrderList myOrderList) {
                            notify_url = myOrderList.getData().getUrl();
                            return Observable.from(myOrderList.getData().getList());
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(myOrderListSubscriber);
        } else {
            OtherApi.getService()
                    .get_OrderList(auth, page, PAGE_SIZE)
                    .subscribeOn(Schedulers.io())
                    .flatMap(new Func1<MyOrderList, Observable<MyOrderList.DataBean.ListBean>>() {
                        @Override
                        public Observable<MyOrderList.DataBean.ListBean> call(MyOrderList myOrderList) {
                            notify_url = myOrderList.getData().getUrl();
                            return Observable.from(myOrderList.getData().getList());
                        }
                    })
                    .filter(new Func1<MyOrderList.DataBean.ListBean, Boolean>() {
                        @Override
                        public Boolean call(MyOrderList.DataBean.ListBean listBean) {
                            return payStatus.equals(listBean.getPay_status());
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(myOrderListSubscriber);
        }
    }

    /**
     * 非全部订单页面，确认收货or取消订单or评价后，刷新全部页面
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMyOrderTab1(RefreshMyOrderListEvent event) {
        mAdapter.setItem(null);
        mList.clear();
        page = 1;
        initData(page);
    }
}
