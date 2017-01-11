package com.ming.slove.mvnew.tab4.mysetting.myorder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintButton;
import com.google.gson.Gson;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.utils.MyItemDecoration2;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.model.bean.MyOrderList;
import com.ming.slove.mvnew.model.bean.MyOrderProductsT;
import com.ming.slove.mvnew.model.bean.ProductList;
import com.ming.slove.mvnew.tab1.BrowserActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MingN on 2017/1/4.
 */

public class MyOrderAdapter extends BaseRecyclerViewAdapter<MyOrderList.DataBean.ListBean, MyOrderAdapter.ViewHolder> {

    public SparseArray<ProductList.DataBean.ListBean> buyProductList = new SparseArray<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_order, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Context mContext = holder.itemView.getContext();
        final MyOrderList.DataBean.ListBean data = mList.get(position);

        //订单来源：1、村实惠2、特产
        int orderFrom = data.getO_type();

        //左上角提示文字
        if (orderFrom == 1) {
            holder.tvBuyer.setText("村实惠订单");
        } else {
            holder.tvBuyer.setText("特产订单");
        }

        //付款状态：1，未付款；2，已付款  3、退款申请中，4、退款成功'
        String payStatus = "未付款";
        holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.color03_dark));
        switch (data.getPay_status()) {
            case "1":
                payStatus = "未付款";
                holder.tvStatus.setTextColor(0xff6600);
                holder.configBtn(1);//取消+去支付
                break;
            case "2":
                payStatus = "已付款";
                holder.layoutButton.setVisibility(View.GONE);
                holder.tvBuyer.setText("你的订单已经付款，等待发货");
                break;
            case "3":
                payStatus = "退款申请中";
                holder.layoutButton.setVisibility(View.GONE);
                break;
            case "4":
                payStatus = "已发货";
                if (orderFrom == 1) {
                    holder.configBtn(4);//查看物流
                } else {
                    holder.configBtn(2);//查看物流+确认收货
                }
                break;
            case "5":
                payStatus = "订单完成";
                if (orderFrom == 1) {
                    holder.configBtn(4);//查看物流
                } else {
                    holder.configBtn(3);//查看物流+去评价
                }
                break;
            case "9":
                payStatus = "订单完成";
                holder.configBtn(4);//查看物流
                break;
            case "100":
                payStatus = "取消订单";
                holder.layoutButton.setVisibility(View.GONE);
                break;
        }
        holder.tvStatus.setText(payStatus);

        //内置recycleView配置
        String products = "{\"products\":" + data.getProducts() + "}";
        Gson gson = new Gson();
        final MyOrderProductsT productList = gson.fromJson(products, MyOrderProductsT.class);
        holder.mAdapter.setItem(productList.getProducts());

        //价格显示
        int nums = 0;
        for (MyOrderProductsT.ProductsBean product : productList.getProducts()) {
            String num = product.getCount();
            if (StringUtils.isEmpty(num)) {
                num = product.getBuy_num();
            }
            nums += Integer.parseInt(num);
        }
        holder.tvTotalNumCost.setText("共计" + nums + "件商品，合计:￥" + data.getOrder_amount() + "(含运费￥" + data.getShipping_fee() + ")");
        //点击去支付
        holder.btn01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < productList.getProducts().size(); i++) {
                    MyOrderProductsT.ProductsBean data1 = productList.getProducts().get(i);
                    ProductList.DataBean.ListBean buyProduct = new ProductList.DataBean.ListBean();
                    String name = new String(Base64.decode(data1.getName(), Base64.DEFAULT));
                    String price = data1.getPrice();
                    String picurl_1 = data1.getPicurl_1();
                    int num;
                    if (StringUtils.isEmpty(data1.getCount())) {
                        num = Integer.parseInt(data1.getBuy_num());
                    } else {
                        num = Integer.parseInt(data1.getCount());
                    }
                    buyProduct.setTitle(name);
                    buyProduct.setPicurl_1(picurl_1);
                    buyProduct.setPrice(price);
                    buyProduct.setBuyNum(num);
                    buyProductList.put(i, buyProduct);
                }

                mOnItemClickListener.onItemClick(holder.btn01, position);
            }
        });
        //点击确认收货or评价
        holder.btn02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(holder.btn02, position);
            }
        });
        //点击查看物流
        holder.btn03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(holder.btn03, position);
            }
        });
        //点击取消
        holder.btn04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(holder.btn04, position);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_buyer)
        TextView tvBuyer;
        @Bind(R.id.tv_status)
        TextView tvStatus;
        @Bind(R.id.tv_total_num_cost)
        TextView tvTotalNumCost;
        @Bind(R.id.btn_01)
        TintButton btn01;
        @Bind(R.id.btn_02)
        TintButton btn02;
        @Bind(R.id.btn_03)
        TintButton btn03;
        @Bind(R.id.btn_04)
        TintButton btn04;
        @Bind(R.id.layout_button)
        LinearLayout layoutButton;
        @Bind(R.id.m_item)
        RelativeLayout mItem;
        @Bind(R.id.m_x_recyclerview)
        RecyclerView mXRecyclerView2;

        private MyOrder2Adapter mAdapter;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            configXRecyclerView(itemView.getContext());
        }

        private void configBtn(int status) {
            layoutButton.setVisibility(View.VISIBLE);
            switch (status) {
                case 1://取消+去支付
                    btn01.setVisibility(View.VISIBLE);//去支付
                    btn02.setVisibility(View.GONE);//确认收货or去评价
                    btn03.setVisibility(View.GONE);//查看物流
                    btn04.setVisibility(View.VISIBLE);//取消
                    break;
                case 2://查看物流+确认收货
                    btn01.setVisibility(View.GONE);//去支付
                    btn02.setVisibility(View.VISIBLE);//确认收货or去评价
                    btn03.setVisibility(View.VISIBLE);//查看物流
                    btn04.setVisibility(View.GONE);//取消
                    break;
                case 3://查看物流+去评价
                    btn01.setVisibility(View.GONE);//去支付
                    btn02.setVisibility(View.VISIBLE);//确认收货or去评价
                    btn02.setText("去评价");
                    btn03.setVisibility(View.VISIBLE);//查看物流
                    btn04.setVisibility(View.GONE);//取消
                    break;
                case 4://查看物流
                    btn01.setVisibility(View.GONE);//去支付
                    btn02.setVisibility(View.GONE);//确认收货or去评价
                    btn03.setVisibility(View.VISIBLE);//查看物流
                    btn04.setVisibility(View.GONE);//取消
                    break;
            }
        }

        //配置RecyclerView
        private void configXRecyclerView(Context context) {
            //设置布局和adapter
            mXRecyclerView2.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new MyOrder2Adapter();
            mXRecyclerView2.setAdapter(mAdapter);

            mXRecyclerView2.addItemDecoration(new MyItemDecoration2(context));//添加分割线
            mXRecyclerView2.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
            mXRecyclerView2.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
        }
    }
}
