package com.ming.slove.mvnew.tab3.newpost;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ming.slove.mvnew.R;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ming on 2016/4/20.
 */

public class NewPostAdapter extends RecyclerView.Adapter<NewPostAdapter.ViewHolder> {

    private List<LocalMedia> mList;
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;

    public void setItem(List<LocalMedia> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    /**
     * 点击事件接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab3_post_image_list, parent, false);
        return new ViewHolder(mView);
    }

    /**
     * 绑定ViewHoler，给item中的控件设置数据
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //少于8张，显示继续添加的图标
        if (getItemViewType(position) == TYPE_CAMERA) {

            holder.showPicture.setVisibility(View.GONE);
            holder.addPicture.setVisibility(View.VISIBLE);
            if (mOnItemClickListener != null) {
                holder.addPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(holder.addPicture, holder.getAdapterPosition());
                    }
                });
            }
        } else {
            holder.showPicture.setVisibility(View.VISIBLE);
            holder.addPicture.setVisibility(View.GONE);

            if (mOnItemClickListener != null) {
                holder.postPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(holder.postPicture, position);
                    }
                });
                holder.delPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(holder.delPicture, position);
                    }
                });
            }
            //显示数据编辑
            String imageUrl = mList.get(position).getPath();
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .asBitmap()
                    .centerCrop()
                    .into(holder.postPicture);
        }
    }

    @Override
    public int getItemCount() {
        int size = mList == null ? 0 : mList.size();
        if (size < 9) {
            return size + 1;
        } else {
            return size;
        }
    }

    private boolean isShowAddItem(int position) {
        int size = mList == null ? 0 : mList.size();
        return position == size;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.post_picture)
        ImageView postPicture;
        @Bind(R.id.del_picture)
        ImageView delPicture;
        @Bind(R.id.card_picture)
        CardView showPicture;
        @Bind(R.id.img_add)
        ImageView addPicture;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
