package com.ming.slove.mvnew.tab3.livevideo.inroom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.model.bean.BbsCommentList;
import com.ming.slove.mvnew.model.bean.InRoomPeopleList;
import com.ming.slove.mvnew.model.bean.UserInfoByPhone;
import com.ming.slove.mvnew.tab2.frienddetail.FriendDetailActivity;
import com.orhanobut.hawk.Hawk;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ming on 2016/3/30.
 */
public class InRoomPeopleListAdapter extends RecyclerView.Adapter<InRoomPeopleListAdapter.ViewHolder> {

    private List<InRoomPeopleList.DataBean.ListBean> mList;

    public void setItem(List<InRoomPeopleList.DataBean.ListBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
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
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab3_bbs_detail, parent, false);
        return new ViewHolder(mView);
    }

    /**
     * 绑定ViewHoler，给item中的控件设置数据
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Context mContext = holder.itemView.getContext();
        InRoomPeopleList.DataBean.ListBean data=mList.get(position);

        //显示数据编辑
        //观看直播人员姓名
        String uname = data.getName();
        if (StringUtils.isEmpty(uname)) {
            //若用户名为空，显示手机号，中间四位为*
            String iphone = data.getAccount();
            if (!StringUtils.isEmpty(iphone)) {
                uname = iphone.substring(0, 3) + "****" + iphone.substring(7, 11);
            } else {
                uname = "匿名";
            }
        }
        holder.content.setText(uname);
        holder.name.setVisibility(View.INVISIBLE);
        //进入房间时间
        String date = data.getCtime();
        String showTime = BaseTools.getTimeFormat(date);
        holder.time.setText(showTime);
        //获取观看人员头像
        String auth= Hawk.get(APPS.USER_AUTH);
        OtherApi.getService()
                .get_UserInfoByPhone(auth, data.getAccount())
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
                    public void onNext(final UserInfoByPhone userInfoByPhone) {
                        if(userInfoByPhone!=null){
                            String head=APPS.BASE_URL + userInfoByPhone.getData().getHead();
                            Glide.with(holder.itemView.getContext()).load(head)
                                    .bitmapTransform(new CropCircleTransformation(holder.itemView.getContext()))
                                    .error(R.mipmap.defalt_user_circle)
                                    .into(holder.icon);
                            //点击查看观看人员信息
                            holder.icon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, FriendDetailActivity.class);
                                    String uid = userInfoByPhone.getData().getUid();
                                    intent.putExtra(FriendDetailActivity.FRIEND_UID, uid);
                                    mContext.startActivity(intent);
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.icon)
        ImageView icon;
        @Bind(R.id.content)
        TextView content;
        @Bind(R.id.layoutContent)
        LinearLayout layoutContent;
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.commentlayout)
        RelativeLayout item;
        @Bind(R.id.bbs_comment_del)
        ImageView bbsCommentDel;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
