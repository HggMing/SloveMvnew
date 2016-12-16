package com.ming.slove.mvnew.tab3.villagelist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.model.bean.FollowVillageList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ming on 2016/3/30.
 */
public class VillageListAdapter extends BaseRecyclerViewAdapter<FollowVillageList.DataEntity.ListEntity, VillageListAdapter.ViewHolder> {

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab3, parent, false);
        return new ViewHolder(mView);
    }

    /**
     * 绑定ViewHoler，给item中的控件设置数据
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mOnItemClickListener != null) {
            final int finalPosition = position;
            holder.tab3Item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.tab3Item, finalPosition);
                }
            });
            holder.tab3Item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(holder.tab3Item, finalPosition);
                    return true;
                }
            });
        }
        //显示数据编辑
        String imageUrl = APPS.BASE_URL + mList.get(position).getPic();
        Glide.with(holder.itemView.getContext()).load(imageUrl)
                .placeholder(R.mipmap.default_village)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageViewVillage);//关注村圈图
        String villageName = mList.get(position).getVillage_name();
        holder.tvVillageName.setText(villageName);//关注村圈名称
        String newMessage = mList.get(position).getBbsInfo().getDesc();
        holder.tvNews.setText(newMessage);//村圈最新消息

        String date = mList.get(position).getBbsInfo().getCtime();
        String time = BaseTools.getTimeFormat(date);
        holder.tvTime.setText(time);//最新动态时间
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tab3_item)
        RelativeLayout tab3Item;
        @Bind(R.id.imageView_1)
        ImageView imageViewVillage;
        @Bind(R.id.tv_title)
        TextView tvVillageName;
        @Bind(R.id.tv_news)
        TextView tvNews;
        @Bind(R.id.tv_time)
        TextView tvTime;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
