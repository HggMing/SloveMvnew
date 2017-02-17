package com.ming.slove.mvnew.tab3.livevideo.inroom;

import android.content.Context;
import android.os.IBinder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintButton;
import com.bilibili.magicasakura.widgets.TintEditText;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.video.VideoApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.LazyLoadFragment;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.model.bean.InRoomPeopleList;
import com.ming.slove.mvnew.model.bean.SocketData;
import com.ming.slove.mvnew.model.event.SocketDataEvent;
import com.ming.slove.mvnew.tab3.newpost.SquareFrameLayout;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.vilyever.socketclient.SocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 直播播放界面，显示信息
 */
public class LiveMsgFragment extends LazyLoadFragment {
    @Bind(R.id.head)
    ImageView imgHead;
    @Bind(R.id.name)
    TextView tvName;
    @Bind(R.id.people_num)
    TextView tvPeopleNum;
    @Bind(R.id.comment_edit)
    TintEditText commentEdit;
    @Bind(R.id.comment_post)
    TintButton commentPost;
    @Bind(R.id.post_msg)
    LinearLayout lyPostMsg;
    @Bind(R.id.tv_msg)
    TextView tvMsg;
    @Bind(R.id.ly_msg)
    LinearLayout lyMsg;
    @Bind(R.id.ly_people_list)
    SquareFrameLayout lyPeopleList;
    @Bind(R.id.m_x_recyclerview)
    XRecyclerView mXRecyclerView;
    @Bind(R.id.rv_show_msg)
    RecyclerView rvShowMsg;
    @Bind(R.id.tv_people_in)
    TextView tvPeopleIn;

    private ImageView btnLike;

    private InRoomPeopleListAdapter mAdapter;
    List<InRoomPeopleList.DataBean.ListBean> mList = new ArrayList<>();

    private InRoomMsgListAdapter msgAdapter;

    final private static int PAGE_SIZE = 10;
    private int page = 1;
    private String roomId;//房间id
    private int peopleNum = 0;

    private SocketClient mSocketclient;

    public static final String VIDEO_ROOM_ID = "video_room_id";//直播房间id
    public static final String VIDEO_ROOM_OWNER_HEAD = "video_room_owner_head";//主播头像地址
    public static final String VIDEO_ROOM_OWNER_NAME = "video_room_owner_name";//主播昵称

    private KeyboardPatch keyboardPatch;//处理键盘遮挡输入框问题
    private boolean isShowPeopleList = false;//是否显示用户列表

    //将socket相关数据传递到本fragment
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void socketChangeUI(SocketDataEvent event) {
        mSocketclient = event.getmSocketclient();
        SocketData socketData = event.getSocketData();

        switch (socketData.getOp_type()) {
            case "come_in_room"://进入房间
                String me_phone = Hawk.get(APPS.KEY_LOGIN_NAME);
                if (!socketData.getData().getAccount().equals(me_phone)) {//自己进入不增加人数
                    peopleNum += 1;
                    tvPeopleNum.setText(peopleNum + "人");
                }

                String uname = socketData.getData().getName();
                if (StringUtils.isEmpty(uname)) {
                    //若用户名为空，显示手机号，中间四位为*
                    String iphone = socketData.getData().getAccount();
                    if (!StringUtils.isEmpty(iphone)) {
                        uname = iphone.substring(0, 3) + "****" + iphone.substring(7, 11);
                    } else {
                        uname = "匿名";
                    }
                }
                //设置动画
                AlphaAnimation aa = new AlphaAnimation(1f, 0f);//透明度从不透明到透明的变化
                aa.setDuration(2000);//动画持续时长2s
                tvPeopleIn.startAnimation(aa);
                final String finalUname = uname;
                aa.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        tvPeopleIn.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationStart(Animation animation) {
                        tvPeopleIn.setText(finalUname + "进入了房间");
                        tvPeopleIn.setVisibility(View.VISIBLE);
                    }
                });

                break;
            case "come_out_room"://离开房间
                peopleNum -= 1;
                if (peopleNum < 0)
                    peopleNum = 0;
                tvPeopleNum.setText(peopleNum + "人");
                break;
            case "say_to_one"://房间内消息，对个人
                break;
            case "say_to_all"://房间内消息，对全体
                msgAdapter.addLastItem(socketData.getData());
                rvShowMsg.smoothScrollToPosition(msgAdapter.getItemCount());
                break;
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_live_msg;
    }

    @Override
    public void initViews(View view) {
        EventBus.getDefault().register(this);
        keyboardPatch = new KeyboardPatch(getActivity(), view, this);
        keyboardPatch.enable();

        btnLike = (ImageView) getActivity().findViewById(R.id.btn_like);

        roomId = getArguments().getString(VIDEO_ROOM_ID);
        String headUrl = getArguments().getString(VIDEO_ROOM_OWNER_HEAD);
        String name = getArguments().getString(VIDEO_ROOM_OWNER_NAME);

        //主播头像
        Glide.with(this).load(headUrl)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .error(R.mipmap.defalt_user_circle)
                .into(imgHead);
        //主播名字
        tvName.setText(name);

        //配置显示房间人员
        mAdapter = new InRoomPeopleListAdapter();
        mXRecyclerView.setAdapter(mAdapter);//设置adapter

        mXRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));//设置布局管理器
//        mXRecyclerView.addItemDecoration(new MyItemDecoration(getContext()));//添加分割线
        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画

        mXRecyclerView.setPullRefreshEnabled(false);//关闭刷新功能
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);

        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
//                mXRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                getDataList(++page);
                mXRecyclerView.loadMoreComplete();
            }
        });

        //配置显示房间内消息列表
        msgAdapter = new InRoomMsgListAdapter();
        rvShowMsg.setAdapter(msgAdapter);//设置adapter

        rvShowMsg.setLayoutManager(new LinearLayoutManager(getContext()));//设置布局管理器
        rvShowMsg.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
        //发布消息，输入框监听
        commentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    commentPost.setEnabled(false);
                } else {
                    commentPost.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        keyboardPatch.disable();
    }

    private void getDataList(int page) {
        VideoApi.getService()
                .get_PeopleList(roomId, page, PAGE_SIZE)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InRoomPeopleList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(InRoomPeopleList inRoomPeopleList) {
                        peopleNum = Integer.parseInt(inRoomPeopleList.getData().getCnt());
                        tvPeopleNum.setText(peopleNum + "人");

                        if (peopleNum <= PAGE_SIZE) {
                            mXRecyclerView.setLoadingMoreEnabled(false);//评论数少于一页，不显示加载更多
                        }

                        mList.addAll(inRoomPeopleList.getData().getList());
                        mAdapter.setItem(mList);
                    }
                });
    }

    @Override
    public void loadData() {
        getDataList(page);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.ly_room_info, R.id.comment_post, R.id.tv_msg, R.id.fragment_live})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_room_info://点击头像，弹出显示用户列表
                if (!isShowPeopleList) {
                    isShowPeopleList=true;
                    lyPeopleList.setVisibility(View.VISIBLE);
                    //刷新列表
                    mAdapter.setItem(null);
                    mList.clear();
                    page = 1;
                    getDataList(page);
                }else{
                    isShowPeopleList=false;
                    lyPeopleList.setVisibility(View.GONE);
                }
                break;
            case R.id.comment_post://发送消息
                postMsg();
                break;
            case R.id.tv_msg://弹出键盘，和消息发送布局
                showSoftInput();
                break;
            case R.id.fragment_live://其他区域，关闭键盘,关闭用户列表
                lyPeopleList.setVisibility(View.GONE);
//                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//关闭导航虚拟按键
                hideSoftInput();
                break;
        }
    }

    /**
     * 发送消息
     */
    private void postMsg() {
        commentPost.setEnabled(false);
        //发送评论
        String conts = commentEdit.getText().toString();
        String phone = Hawk.get(APPS.KEY_LOGIN_NAME);
        String msg = "{\"type\":\"say_to_all\",\"from\":\"" + phone + "\",\"ty\":\"1\",\"room_id\":" + roomId + ",\"content\":\"" + conts + "\"}";
        mSocketclient.sendString(msg);
        //清空评论
        commentEdit.setText("");
    }

    // 显示软键盘
    private void showSoftInput() {
        lyPostMsg.setVisibility(View.VISIBLE);
        lyMsg.setVisibility(View.GONE);
        showGift(false);

        commentEdit.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(commentEdit, 0);
    }

    // 隐藏软键盘
    public void hideSoftInput() {
        lyPostMsg.setVisibility(View.GONE);
        lyMsg.setVisibility(View.VISIBLE);
        showGift(true);

        IBinder token = commentEdit.getWindowToken();
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void showGift(boolean isShow) {
        if (btnLike != null) {
            if (isShow) {
                btnLike.setVisibility(View.VISIBLE);
            } else {
                btnLike.setVisibility(View.GONE);
            }
        }
    }
}
