package com.ming.slove.mvnew.tab2.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.model.bean.BBSDetail;
import com.ming.slove.mvnew.model.bean.BBSList;
import com.ming.slove.mvnew.model.database.ChatMsgModel;
import com.ming.slove.mvnew.model.database.FriendsModel;
import com.ming.slove.mvnew.model.database.MyDB;
import com.ming.slove.mvnew.tab1.BrowserActivity;
import com.ming.slove.mvnew.tab3.villagebbs.bbsdetail.BbsDetailActivity;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Ming on 2016/5/5.
 */
public class ChatAdapter extends BaseRecyclerViewAdapter<ChatMsgModel, RecyclerView.ViewHolder> {

    public void addData(List<ChatMsgModel> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        if (mList == null) {
            mList = new ArrayList<>();
        }

        mList.addAll(data);
//        Collections.reverse(mList);//列表反向
        this.notifyDataSetChanged();
    }

    /**
     * 添加一条数据并刷新界面
     *
     * @param bean
     */
    public void addData(ChatMsgModel bean) {
        if (bean == null) {
            return;
        }
        if (mList == null) {
            mList = new ArrayList<>();
        }
//        Collections.reverse(mList);
        mList.add(bean);
//        Collections.reverse(mList);
        notifyItemInserted(mList.size());
    }

    /**
     * 根据不同的条目返回不同的类型的布局
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    /**
     * 根据不同的类型返回不同的holder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //加载Item View的时候根据不同TYPE加载不同的布局
        if (viewType == ChatMsgModel.ITEM_TYPE_LEFT) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab2_message_list_left, parent, false);
            return new LeftViewHolder(mView);
        } else {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab2_message_list_right, parent, false);
            return new RightViewHolder(mView);
        }
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Context mContext = holder.itemView.getContext();
        final ChatMsgModel data = mList.get(position);
        if (holder instanceof LeftViewHolder) {//接收消息布局

            //消息时间
            final String date = data.getSt();
            String timeFormat = BaseTools.getTimeFormat(date);
            ((LeftViewHolder) holder).time.setText(timeFormat);
            //头像
            FriendsModel friend = MyDB.createDb(mContext).queryById(data.getFrom(), FriendsModel.class);
            String headUrl = friend.getUicon();
            Glide.with(mContext)
                    .load(headUrl)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .error(R.mipmap.defalt_user_circle)
                    .into(((LeftViewHolder) holder).icon);
            //消息显示
            switch (data.getCt()) {
                case "0"://文字消息
                    ((LeftViewHolder) holder).resend.setVisibility(View.GONE);//其他消息，不显示红点
                    ((LeftViewHolder) holder).content.setVisibility(View.VISIBLE);
                    ((LeftViewHolder) holder).msgImage.setVisibility(View.GONE);
                    ((LeftViewHolder) holder).voicePlay.setVisibility(View.GONE);
                    ((LeftViewHolder) holder).shareLayout.setVisibility(View.GONE);
                    String content = data.getTxt();
                    ((LeftViewHolder) holder).content.setText(content);
                    break;
                case "1"://图片消息
                    ((LeftViewHolder) holder).resend.setVisibility(View.GONE);//其他消息，不显示红点
                    ((LeftViewHolder) holder).content.setVisibility(View.GONE);
                    ((LeftViewHolder) holder).msgImage.setVisibility(View.VISIBLE);
                    ((LeftViewHolder) holder).voicePlay.setVisibility(View.GONE);
                    ((LeftViewHolder) holder).shareLayout.setVisibility(View.GONE);
                    String imageUrl = APPS.CHAT_BASE_URL + data.getLink();
                    Glide.with(mContext)
                            .load(imageUrl)
                            .asBitmap()
                            .error(R.drawable.shape_picture_background)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    //限制显示图片imageview的大小
                                    ImageSize imageSize = ImageSizeUtil.getImageSize(resource);
                                    LayoutParams imageLP = ((LeftViewHolder) holder).msgImage.getLayoutParams();
                                    if (imageSize != null) {
                                        imageLP.width = imageSize.getWidth();
                                        imageLP.height = imageSize.getHeight();
                                    }
                                    ((LeftViewHolder) holder).msgImage.setLayoutParams(imageLP);
                                    //显示图片
                                    ((LeftViewHolder) holder).msgImage.setImageBitmap(resource);
                                }
                            });
                    break;
                case "2"://声音消息
                    ((LeftViewHolder) holder).resend.setVisibility(View.VISIBLE);//新语音消息，显示红点
                    ((LeftViewHolder) holder).content.setVisibility(View.GONE);
                    ((LeftViewHolder) holder).msgImage.setVisibility(View.GONE);
                    ((LeftViewHolder) holder).voicePlay.setVisibility(View.VISIBLE);
                    ((LeftViewHolder) holder).shareLayout.setVisibility(View.GONE);
                    break;
                case "100"://来自我们村的分享消息
                    ((LeftViewHolder) holder).resend.setVisibility(View.GONE);//其他消息，不显示红点
                    ((LeftViewHolder) holder).content.setVisibility(View.GONE);
                    ((LeftViewHolder) holder).msgImage.setVisibility(View.GONE);
                    ((LeftViewHolder) holder).voicePlay.setVisibility(View.GONE);
                    ((LeftViewHolder) holder).shareLayout.setVisibility(View.VISIBLE);

                    Glide.with(mContext)
                            .load(data.getShare_image())
                            .error(R.drawable.shape_picture_background)
                            .into(((LeftViewHolder) holder).shareImg);
                    ((LeftViewHolder) holder).shareTitle.setText(data.getShare_title());
                    ((LeftViewHolder) holder).shareDetail.setText(data.getShare_detail());
                    break;
                case "3"://html
                    break;
                case "4"://内部消息json格式
                    break;
                case "5"://交互消息
                    break;
                case "6"://应用透传消息json格式
                    break;
                case "7"://朋友系统消息json
                    break;
            }
            //点击事件
            ((LeftViewHolder) holder).item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (data.getCt()) {
                        case "0"://文字消息
                            break;
                        case "1"://图片消息
                            final ArrayList<String> strings = new ArrayList<>();
                            //当图片显示
                           /* strings.add(data.getLink());
                            Intent intent = new Intent(mContext, BigImageActivity.class);
                            intent.putStringArrayListExtra(BigImageActivity.IMAGE_LIST, strings);
                            intent.putExtra(BigImageActivity.IMAGE_INDEX, position);
                            mContext.startActivity(intent);*/
                            //显示所有聊天图片
                            final int[] position2 = new int[1];
                            Observable.from(mList)
                                    .filter(new Func1<ChatMsgModel, Boolean>() {
                                        @Override
                                        public Boolean call(ChatMsgModel chatMsgModel) {
                                            return "1".equals(chatMsgModel.getCt());
                                        }
                                    })
                                    .map(new Func1<ChatMsgModel, String>() {
                                        @Override
                                        public String call(ChatMsgModel chatMsgModel) {
                                            return chatMsgModel.getLink();
                                        }
                                    })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<String>() {
                                        @Override
                                        public void onCompleted() {
                                            Intent intent = new Intent(mContext, BigImageActivity.class);
                                            intent.putStringArrayListExtra(BigImageActivity.IMAGE_LIST, strings);
                                            intent.putExtra(BigImageActivity.IMAGE_INDEX, position2[0]);
                                            mContext.startActivity(intent);
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onNext(String s) {
                                            if (s.substring(0, 2).equals("20")) {
                                                strings.add(APPS.CHAT_BASE_URL + s);//接收的路径为网络+baseurl
                                            } else {
                                                strings.add(s);//发送的图片是全路径
                                            }

                                            if (data.getLink().equals(s)) {
                                                position2[0] = strings.size() - 1;
                                            }
                                        }
                                    });
                            break;
                        case "2"://声音消息

                            break;
                        case "100"://点击分享的消息
                            String s = data.getShare_link();
                            if (s != null) {
                                String auth = Hawk.get(APPS.USER_AUTH);
                                int isBbs = s.indexOf("bbs/bbsinfo");//分享我们村帖子

                                if (isBbs != -1) {
                                    int begin = s.indexOf("id=");
                                    int end = s.indexOf("&");
                                    String share_id;
                                    if (end == -1) {
                                        share_id = s.substring(begin + 3, s.length());
                                    } else {
                                        share_id = s.substring(begin + 3, end);
                                    }

                                    OtherApi.getService()
                                            .get_BBSDetail(auth, share_id)
                                            .map(new Func1<BBSDetail, BBSList.DataEntity.ListEntity>() {
                                                @Override
                                                public BBSList.DataEntity.ListEntity call(BBSDetail bbsDetail) {
                                                    return bbsDetail.getData();
                                                }
                                            })
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Subscriber<BBSList.DataEntity.ListEntity>() {
                                                @Override
                                                public void onCompleted() {

                                                }

                                                @Override
                                                public void onError(Throwable e) {

                                                }

                                                @Override
                                                public void onNext(BBSList.DataEntity.ListEntity listEntity) {
                                                    Intent intent = new Intent(mContext, BbsDetailActivity.class);
                                                    intent.putExtra(BbsDetailActivity.BBS_DETAIL, listEntity);
                                                    mContext.startActivity(intent);
                                                }
                                            });
                                } else {//分享村特产
                                    String url = s+"&auth="+auth+"&device_type=1";
                                    Intent intent3 = new Intent(mContext, BrowserActivity.class);
                                    intent3.putExtra(BrowserActivity.KEY_URL, url);
                                    mContext.startActivity(intent3);
                                }
                            }
                            break;
                    }
                }
            });

            ((LeftViewHolder) holder).item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //长按删除消息
                    mOnItemClickListener.onItemLongClick(((LeftViewHolder) holder).item, position);
                    return true;
                }
            });

        } else if (holder instanceof RightViewHolder) {//发送消息布局
            //发送消息时间
            String date = data.getSt();//13位数时间戳
            String timeFormat = BaseTools.getTimeFormat(date);
            ((RightViewHolder) holder).time.setText(timeFormat);

            //头像
            String headUrl = Hawk.get(APPS.ME_HEAD);
            Glide.with(mContext)
                    .load(headUrl)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .error(R.mipmap.defalt_user_circle)
                    .into(((RightViewHolder) holder).icon);
            //消息显示
            ((RightViewHolder) holder).resend.setVisibility(data.getIsShowPro() == 2 ? View.VISIBLE : View.GONE);//感叹号,发送失败时显示
            switch (data.getCt()) {
                case "0"://文字消息
                    ((RightViewHolder) holder).content.setVisibility(View.VISIBLE);
                    ((RightViewHolder) holder).msgImage.setVisibility(View.GONE);
                    ((RightViewHolder) holder).voicePlay.setVisibility(View.GONE);
                    String content = data.getTxt();
                    ((RightViewHolder) holder).content.setText(content);
                    break;
                case "1"://图片消息
                    ((RightViewHolder) holder).content.setVisibility(View.GONE);
                    ((RightViewHolder) holder).msgImage.setVisibility(View.VISIBLE);
                    ((RightViewHolder) holder).voicePlay.setVisibility(View.GONE);
                    String imageUrl = data.getLink();
                    Glide.with(mContext)
                            .load(imageUrl)
                            .asBitmap()
                            .error(R.drawable.shape_picture_background)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    //限制显示图片imageview的大小
                                    ImageSize imageSize = ImageSizeUtil.getImageSize(resource);
                                    LayoutParams imageLP = ((RightViewHolder) holder).msgImage.getLayoutParams();
                                    if (imageSize != null) {
                                        imageLP.width = imageSize.getWidth();
                                        imageLP.height = imageSize.getHeight();
                                    }
                                    ((RightViewHolder) holder).msgImage.setLayoutParams(imageLP);
                                    //显示图片
                                    ((RightViewHolder) holder).msgImage.setImageBitmap(resource);
                                }
                            });
                    break;
                case "2"://声音消息
                    ((RightViewHolder) holder).content.setVisibility(View.GONE);
                    ((RightViewHolder) holder).msgImage.setVisibility(View.GONE);
                    ((RightViewHolder) holder).voicePlay.setVisibility(View.VISIBLE);
                    break;
            }

            //点击事件
            ((RightViewHolder) holder).item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (data.getCt()) {
                        case "0"://文字消息
                            break;
                        case "1"://图片消息
                            final ArrayList<String> strings = new ArrayList<>();
                            //当图片显示
                           /* strings.add(data.getLink());
                            Intent intent = new Intent(mContext, BigImageActivity.class);
                            intent.putStringArrayListExtra(BigImageActivity.IMAGE_LIST, strings);
                            intent.putExtra(BigImageActivity.IMAGE_INDEX, position);
                            mContext.startActivity(intent);*/
                            //显示所有聊天图片
                            final int[] position2 = new int[1];
                            Observable.from(mList)
                                    .filter(new Func1<ChatMsgModel, Boolean>() {
                                        @Override
                                        public Boolean call(ChatMsgModel chatMsgModel) {
                                            return "1".equals(chatMsgModel.getCt());
                                        }
                                    })
                                    .map(new Func1<ChatMsgModel, String>() {
                                        @Override
                                        public String call(ChatMsgModel chatMsgModel) {
                                            return chatMsgModel.getLink();
                                        }
                                    })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<String>() {
                                        @Override
                                        public void onCompleted() {
                                            Intent intent = new Intent(mContext, BigImageActivity.class);
                                            intent.putStringArrayListExtra(BigImageActivity.IMAGE_LIST, strings);
                                            intent.putExtra(BigImageActivity.IMAGE_INDEX, position2[0]);
                                            mContext.startActivity(intent);
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onNext(String s) {
                                            if (s.substring(0, 2).equals("20")) {
                                                //接收的路径为网络+baseurl
                                                strings.add(APPS.CHAT_BASE_URL + s);
                                            } else {
                                                strings.add(s);//发送的图片是全路径
                                            }
                                            if (data.getLink().equals(s)) {
                                                position2[0] = strings.size() - 1;
                                            }
                                        }
                                    });
                            break;
                        case "2"://声音消息

                            break;
                    }
                }
            });

            ((RightViewHolder) holder).item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //长按删除消息
                    mOnItemClickListener.onItemLongClick(((RightViewHolder) holder).item, position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class LeftViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.icon)
        ImageView icon;
        @Bind(R.id.content)
        TextView content;
        @Bind(R.id.voice_play)
        ImageView voicePlay;
        @Bind(R.id.msg_image)
        ImageView msgImage;
        @Bind(R.id.resend)
        TextView resend;
        @Bind(R.id.linearLayout)
        LinearLayout item;
        @Bind(R.id.layout_share)
        RelativeLayout shareLayout;
        @Bind(R.id.share_img)
        ImageView shareImg;
        @Bind(R.id.share_title)
        TextView shareTitle;
        @Bind(R.id.share_detail)
        TextView shareDetail;


        LeftViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class RightViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.icon)
        ImageView icon;
        @Bind(R.id.content)
        TextView content;
        @Bind(R.id.voice_play)
        ImageView voicePlay;
        @Bind(R.id.msg_image)
        ImageView msgImage;
        @Bind(R.id.linearLayout)
        LinearLayout item;
        @Bind(R.id.resend)
        TextView resend;
        @Bind(R.id.message_list_list_item_right)
        RelativeLayout messageListListItemRight;

        RightViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
