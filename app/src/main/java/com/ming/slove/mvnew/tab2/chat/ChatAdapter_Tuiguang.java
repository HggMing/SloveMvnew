package com.ming.slove.mvnew.tab2.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.model.bean.JsonMsg_Tuiguang;
import com.ming.slove.mvnew.model.bean.SalesOrderList;
import com.ming.slove.mvnew.shop.shoptab2.SalesOrderActivity;
import com.ming.slove.mvnew.tab1.BrowserActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MingN on 2017/5/9.
 */
class ChatAdapter_Tuiguang extends BaseRecyclerViewAdapter<JsonMsg_Tuiguang.ListBean, ChatAdapter_Tuiguang.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_tab2_sales_2, parent, false);
        mView.setBackgroundColor(0X000000);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Context mContext = holder.itemView.getContext();
        final JsonMsg_Tuiguang.ListBean data = mList.get(position);

        //商品图片
        String imageUrl =  data.getImg_url();
        Glide.with(mContext).load(imageUrl)
                .placeholder(R.drawable.shape_picture_background)
                .centerCrop()
                .into(holder.img);
        //购买商品简略信息
        holder.tvContent.setText(data.getTitle());
        //购买数量
        holder.tvNumber.setText("￥" + data.getPrice());
        //价格显示
        holder.tvPrice.setVisibility(View.INVISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BrowserActivity.class);
                intent.putExtra(BrowserActivity.KEY_URL, data.getProduct_url());
                mContext.startActivity(intent);
            }
        });
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