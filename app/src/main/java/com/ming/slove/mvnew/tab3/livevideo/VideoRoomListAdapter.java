package com.ming.slove.mvnew.tab3.livevideo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintTextView;
import com.bumptech.glide.Glide;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.model.bean.RoomList;
import com.ming.slove.mvnew.model.bean.UserInfoByPhone;
import com.orhanobut.hawk.Hawk;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ming on 2016/7/13.
 */
public class VideoRoomListAdapter extends BaseRecyclerViewAdapter<RoomList.DataBean.ListBean, VideoRoomListAdapter.ViewHolder> {
    private String headUrl;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_room_list, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Context mContext = holder.itemView.getContext();
        //直播封面图片加载
        String imageUrl = APPS.BASE_URL + mList.get(position).getPic_1();
        Glide.with(mContext).load(imageUrl)
                .placeholder(R.drawable.default_nine_picture)
                .into(holder.thumbImageView);
        //主播名字
        String name = mList.get(position).getName();
        holder.name.setText(name);
        //标题内容显示
        String content = mList.get(position).getTitle();
        holder.content.setText(content);
        //人数显示
        String num = mList.get(position).getNum();
        holder.number.setText(num + "人正在看");
        //点击事件,进入直播间
        if (mOnItemClickListener != null) {
            holder.mPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mPlayer.setTag(headUrl);
                    mOnItemClickListener.onItemClick(holder.mPlayer, position);
                }
            });
            //关闭直播
            holder.mPlayer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(holder.mPlayer, position);
                    return true;
                }
            });
        }

        //获取主播头像
        String auth= Hawk.get(APPS.USER_AUTH);
        OtherApi.getService()
                .get_UserInfoByPhone(auth, mList.get(position).getAccount())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfoByPhone>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(UserInfoByPhone userInfoByPhone) {
                        if(userInfoByPhone!=null){
                            headUrl=APPS.BASE_URL + userInfoByPhone.getData().getHead();
                            Glide.with(holder.itemView.getContext()).load(headUrl)
                                    .bitmapTransform(new CropCircleTransformation(holder.itemView.getContext()))
                                    .error(R.mipmap.defalt_user_circle)
                                    .into(holder.icon);
                        }
                    }
                });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.thumb_image_view)
        ImageView thumbImageView;
        @Bind(R.id.m_player)
        FrameLayout mPlayer;
        @Bind(R.id.icon)
        ImageView icon;
        @Bind(R.id.content)
        TextView content;
        @Bind(R.id.name)
        TintTextView name;
        @Bind(R.id.number)
        TextView number;
        @Bind(R.id.live_video_item)
        LinearLayout liveVideoItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
