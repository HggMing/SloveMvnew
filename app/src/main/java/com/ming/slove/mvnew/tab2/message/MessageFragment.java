package com.ming.slove.mvnew.tab2.message;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.LazyLoadFragment;
import com.ming.slove.mvnew.common.utils.MyItemDecoration2;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.model.bean.FriendList;
import com.ming.slove.mvnew.model.bean.RecommendList;
import com.ming.slove.mvnew.model.database.ChatMsgModel;
import com.ming.slove.mvnew.model.database.FriendsModel;
import com.ming.slove.mvnew.model.database.InstantMsgModel;
import com.ming.slove.mvnew.model.database.MyDB;
import com.ming.slove.mvnew.model.event.InstantMsgEvent;
import com.ming.slove.mvnew.tab2.chat.ChatActivity;
import com.ming.slove.mvnew.tab2.newfriend.NewFriendActivity;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 动态
 */
public class MessageFragment extends LazyLoadFragment implements MessageAdapter.OnItemClickListener {

    final private static int PAGE_SIZE = 10;
    @Bind(R.id.m_x_recyclerview)
    RecyclerView mXRecyclerView;
    @Bind(R.id.content_empty)
    TextView contentEmpty;
    @Bind(R.id.tv_recommend)
    TextView tvRecommend;
    @Bind(R.id.m_x_recyclerview2)
    XRecyclerView mXRecyclerView2;
    private MessageAdapter mAdapter;
    private List<InstantMsgModel> mList;
    private RecommendAdapter mAdapter2;
    private List<RecommendList.DataBean.ListBean> mList2 = new ArrayList<>();//特产推荐列表
    private int page = 1;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initFriendInfo() {
        page = 1;
        String auth = Hawk.get(APPS.USER_AUTH);

        OtherApi.getService().get_FriendList(auth, page, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FriendList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(FriendList friendList) {
                        if (friendList != null && friendList.getErr() == 0) {
                            List<FriendList.DataBean.ListBean> mFList = friendList.getData().getList();

                            //存储好友列表到本地数据库
                            for (FriendList.DataBean.ListBean user : mFList) {
                                String save_uid = user.getUid();
                                String save_uicon = APPS.BASE_URL + user.getHead();
                                String save_uname = user.getName();
                                FriendsModel friendsModel = new FriendsModel(save_uid, save_uname, save_uicon, true);
                                MyDB.insert(friendsModel);
                            }

                            //生成欢迎语动态
                            FriendList.DataBean.ListBean user2 = mFList.get(1);//我们村客服
                            InstantMsgModel user2_msg = MyDB.getInstance().queryById(user2.getUid(), InstantMsgModel.class);
                            if (user2_msg == null) {//动态不存在才添加
                                String user2_icon = APPS.BASE_URL + user2.getHead();
                                String time = String.valueOf(System.currentTimeMillis()).substring(0, 10);//13位时间戳，截取前10位，主要统一
                                String content = "你好！" + mFList.get(2).getName() + ",欢迎来到我们村！";
                                InstantMsgModel msgModel = new InstantMsgModel(user2.getUid(), user2_icon, user2.getName(), time, content, 1);
                                MyDB.insert(msgModel);
                                EventBus.getDefault().post(new InstantMsgEvent());

                                //欢迎语消息保存到数据库
                                ChatMsgModel chatMsg = new ChatMsgModel();
                                chatMsg.setType(ChatMsgModel.ITEM_TYPE_LEFT);//接收消息
                                chatMsg.setFrom(user2.getUid());//消息来源用户id
                                String me_uid = Hawk.get(APPS.ME_UID, "");
                                chatMsg.setTo(me_uid);
                                chatMsg.setSt(time);//消息时间
                                chatMsg.setCt("0");//消息类型
                                chatMsg.setTxt(content);//类型：文字
                                MyDB.insert(chatMsg);//保存到数据库
                            }

                            //储存好友信息
                            List<String> friendUids = new ArrayList<>();
                            for (int i = 0; i < mList.size(); i++) {
                                String uid = mList.get(i).getUid();
                                if (uid != null) {
                                    friendUids.add(uid);
                                }
                            }
                            Hawk.put(APPS.FRIEND_LIST_UID, friendUids);
                        }
                    }
                });
    }


    //动态消息数据
    private void initDatas() {
        mList = MyDB.getQueryAll(InstantMsgModel.class);
//        if (mList.isEmpty() || mList == null) {
//            contentEmpty.setVisibility(View.VISIBLE);
//        } else {
//            contentEmpty.setVisibility(View.GONE);
//        }
        mAdapter.setItem(mList);
    }

    //产品推荐数据
    private void initData2(int page) {
        OtherApi.getService()
                .get_RecommendList(page, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RecommendList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(RecommendList recommendList) {
                        mList2.addAll(recommendList.getData().getList());
                        if (mList2.isEmpty()) {
                            tvRecommend.setVisibility(View.GONE);
                            mXRecyclerView2.setVisibility(View.GONE);
                        } else {
                            tvRecommend.setVisibility(View.VISIBLE);
                            mXRecyclerView2.setVisibility(View.VISIBLE);
                        }
                        mAdapter2.setItem(mList2);
                    }
                });
    }


    /**
     * 更新动态列表
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showInstantMsg(InstantMsgEvent event) {
        if (mList != null) {
            mList.clear();
        }
        mAdapter.clear();
        mList = MyDB.getQueryAll(InstantMsgModel.class);
//        if (mList.isEmpty() || mList == null) {
//            contentEmpty.setVisibility(View.VISIBLE);
//        } else {
//            contentEmpty.setVisibility(View.GONE);
//        }
        mAdapter.setItem(mList);
    }


    //配置RecyclerView
    private void configXRecyclerView() {
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));//设置布局管理器
        mXRecyclerView.addItemDecoration(new MyItemDecoration2(getContext()));//添加分割线
        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画

        mAdapter = new MessageAdapter();
        mXRecyclerView.setAdapter(mAdapter);//设置adapter
        mAdapter.setOnItemClickListener(this);

        //推荐页配置
       /* mXRecyclerView2.setLayoutManager(new GridLayoutManager(mActivity,2, GridLayoutManager.VERTICAL, false));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space);
        mXRecyclerView2.addItemDecoration(new SpaceItemDecoration(spacingInPixels));//添加间距
        mXRecyclerView2.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView2.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画

        mAdapter2 = new RecommendAdapter();
        mXRecyclerView2.setAdapter(mAdapter2);

        mXRecyclerView2.setPullRefreshEnabled(false);
        mXRecyclerView2.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mXRecyclerView2.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                initData2(++page);
                mXRecyclerView2.loadMoreComplete();
            }
        });*/

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_message;
    }

    @Override
    public void initViews(View view) {
        configXRecyclerView();//XRecyclerView配置
        EventBus.getDefault().register(this);
        initFriendInfo();//加载用户好友信息
    }

    @Override
    public void loadData() {
        initDatas();//加载动态列表
//        initData2(page);//加载产品推荐列表
    }


    @Override
    public void onItemClick(View view, int position) {
        if (APPS.NEW_FRIEND.equals(view.getTag())) {
            Intent intent0 = new Intent(getContext(), NewFriendActivity.class);
            startActivity(intent0);
        } else {
            //点击动态，进入聊天界面
            Intent intent2 = new Intent(getContext(), ChatActivity.class);
            intent2.putExtra(ChatActivity.UID, mList.get(position).getUid());
            intent2.putExtra(ChatActivity.USER_NAME, mList.get(position).getUname());
            startActivity(intent2);
        }
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        //长按删除此条动态
        MyDialog.Builder builder = new MyDialog.Builder(getContext());
        builder.setTitle("提示")
                .setMessage("删除该聊天？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //数据库中删除此条动态
                        InstantMsgModel iMsg = new InstantMsgModel();
                        iMsg.setUid(mList.get(position).getUid());
                        MyDB.delete(iMsg);
                        //刷新动态
                        EventBus.getDefault().post(new InstantMsgEvent());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if (!getActivity().isFinishing()) {
            builder.create().show();
        }
    }
}
