package com.ming.slove.mvnew.tab2.friendlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.widgets.stickyrecyclerheaders.StickyRecyclerHeadersAdapter;
import com.ming.slove.mvnew.model.bean.FriendList;
import com.ming.slove.mvnew.model.database.MyDB;
import com.ming.slove.mvnew.model.database.NewFriendModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Ming on 2016/3/30.
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<FriendList.DataBean.ListBean> mList;

    public FriendListAdapter(Context mContext, List<FriendList.DataBean.ListBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    /**
     * 点击事件接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
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
        View mView = mLayoutInflater.inflate(R.layout.item_tab2, parent, false);
        return new ViewHolder(mView);
    }

    /**
     * 绑定ViewHoler，给item中的控件设置数据
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mOnItemClickListener != null) {
            holder.friends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.friends, position);
                }
            });
            holder.friends.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(holder.friends, position);
                    return true;
                }
            });
        }
        //显示数据编辑
        //好友名字的显示
        String showName = mList.get(position).getName();
        holder.userName.setText(showName);
        //好友头像的显示
        if (position == 0) {
            Glide.with(mContext).load(R.mipmap.tab2_new_friend)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .into(holder.userHead);
        } else {
            String imageUrl = APPS.BASE_URL + mList.get(position).getHead();
            Glide.with(mContext).load(imageUrl)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .error(R.mipmap.defalt_user_circle)
                    .into(holder.userHead);
        }
        //新的朋友，新消息小红徽章
        if (position == 0) {
            List<NewFriendModel> nFriends = MyDB.getQueryAll(NewFriendModel.class);
            int count = 0;
            for (NewFriendModel nFriend : nFriends) {
                count += nFriend.getCount();
            }
            if (count > 0) {
                holder.badge.setVisibility(View.VISIBLE);
                holder.badge.setText(String.valueOf(count));
            } else {
                holder.badge.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public long getHeaderId(int position) {
        return mList.get(position).getSortLetters().charAt(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tab2_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        String showValue = String.valueOf(mList.get(position).getSortLetters().charAt(0));
        if ("$".equals(showValue)) {
            textView.setText("系统");
        } else if ("%".equals(showValue)) {
            textView.setText("我");
        } else {
            textView.setText(showValue);
        }
    }

    public int getPositionForSection(char section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        if (section == '↑') {
            return 0;
        }
        return -1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.user_head)
        ImageView userHead;
        @Bind(R.id.user_name)
        TextView userName;
        @Bind(R.id.friends)
        RelativeLayout friends;
        @Bind(R.id.badge)
        TextView badge;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
