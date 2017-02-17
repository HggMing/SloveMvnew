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

import com.bilibili.magicasakura.widgets.TintTextView;
import com.bumptech.glide.Glide;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.model.bean.InRoomPeopleList;
import com.ming.slove.mvnew.model.bean.SocketData;
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
public class InRoomMsgListAdapter extends BaseRecyclerViewAdapter<SocketData.DataBean, InRoomMsgListAdapter.ViewHolder> {

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_msg, parent, false);
        return new ViewHolder(mView);
    }

    /**
     * 绑定ViewHoler，给item中的控件设置数据
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SocketData.DataBean data = mList.get(position);

        //显示数据编辑
        //聊天人员姓名
        if (data.getFrom() != null) {
            String uname = data.getFrom().getName();
            if (StringUtils.isEmpty(uname)) {
                //若用户名为空，显示手机号，中间四位为*
                String iphone = data.getFrom().getAccount();
                if (!StringUtils.isEmpty(iphone)) {
                    uname = iphone.substring(0, 3) + "****" + iphone.substring(7, 11);
                } else {
                    uname = "匿名";
                }
            }
            holder.liveUname.setText(uname);
            //聊天消息内容
            String content = data.getContent();
            holder.liveContent.setText(content);
        }else{
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
            holder.liveUname.setText(uname);
            //聊天消息内容
            String content = "进入了房间";
            holder.liveContent.setText(content);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.live_uname)
        TintTextView liveUname;
        @Bind(R.id.live_content)
        TextView liveContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
