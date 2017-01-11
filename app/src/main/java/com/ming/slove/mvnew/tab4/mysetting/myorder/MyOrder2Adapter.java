package com.ming.slove.mvnew.tab4.mysetting.myorder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.model.bean.MyOrderProductsT;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MingN on 2017/1/4.
 */

public class MyOrder2Adapter extends BaseRecyclerViewAdapter<MyOrderProductsT.ProductsBean, MyOrder2Adapter.ViewHolder> {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_tab2_sales_2, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Context mContext = holder.itemView.getContext();
        MyOrderProductsT.ProductsBean data = mList.get(position);

        //商品图片
        String imageUrl = APPS.BASE_URL + data.getPicurl_1();
        Glide.with(mContext).load(imageUrl)
                .placeholder(R.drawable.default_nine_picture)
                .centerCrop()
                .into(holder.img);
        //购买商品简略信息
        String name = new String(Base64.decode(data.getName(),Base64.DEFAULT));
        holder.tvContent.setText(name);
        //购买数量
        String num = data.getCount();
        if (StringUtils.isEmpty(num)) {
            num = data.getBuy_num();
        }
        holder.tvNumber.setText("x" + num);
        //价格显示
        holder.tvPrice.setText("￥" + data.getPrice());
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
