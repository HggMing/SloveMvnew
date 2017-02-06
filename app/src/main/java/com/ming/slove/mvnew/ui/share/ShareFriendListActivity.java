package com.ming.slove.mvnew.ui.share;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.MyItemDecoration2;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.common.widgets.pinyin.CharacterParser;
import com.ming.slove.mvnew.common.widgets.pinyin.PinyinComparator;
import com.ming.slove.mvnew.common.widgets.stickyrecyclerheaders.StickyRecyclerHeadersDecoration;
import com.ming.slove.mvnew.model.bean.FriendList;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.tab2.friendlist.FriendListAdapter;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShareFriendListActivity extends BackActivity {

    final private static int PAGE_SIZE = 50;//
    private int page = 1;
    private String auth;

    private FriendListAdapter mAdapter;
    List<FriendList.DataBean.ListBean> mList = new ArrayList<>();

    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;

    private String me;//接收者uid
    private String other;//发送者uid
    public static String SHARE_MSG = "share_msg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle(R.string.title_activity_share_friendlist);

        initView();
        loadData();
    }

    private void initView() {
        auth = Hawk.get(APPS.USER_AUTH);
        showLoading(true);
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
    }

    private void loadData() {
        page = 1;
        OtherApi.getService().get_FriendList(auth, page, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FriendList>() {
                    @Override
                    public void onCompleted() {
                        showLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(FriendList friendList) {
                        if (friendList != null && friendList.getErr() == 0) {

                            combinationLists(friendList);
                            if (mList.isEmpty() || mList == null) {
                                showEmpty(R.string.empty_share_friendlist);
                            } else {
                                hideEmpty();

                                if (mAdapter == null) {
                                    mAdapter = new FriendListAdapter(ShareFriendListActivity.this, mList);
                                    addRecycleView(mAdapter);

                                    final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
                                    mRecyclerView.addItemDecoration(headersDecor);
                                    mRecyclerView.addItemDecoration(new MyItemDecoration2(ShareFriendListActivity.this));//添加分割线

                                    mAdapter.setOnItemClickListener(new FriendListAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            other = mList.get(position).getUid();
                                            //点击弹出是否分享
                                            MyDialog.Builder builder = new MyDialog.Builder(ShareFriendListActivity.this);
                                            builder.setMessage("确定分享给" + mList.get(position).getName() + "?")
                                                    .setPositiveButton("确定",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    shareMsg();
                                                                    finish();
                                                                    dialog.dismiss();
                                                                }
                                                            })
                                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Toast.makeText(ShareFriendListActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .create().show();
                                        }

                                        @Override
                                        public void onItemLongClick(View view, int position) {

                                        }
                                    });
                                    mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                        @Override
                                        public void onChanged() {
                                            headersDecor.invalidateHeaders();
                                        }
                                    });
                                    page = 2;
                                } else {
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
    }

    private void shareMsg() {
        String msg = getIntent().getStringExtra(SHARE_MSG);
        me = Hawk.get(APPS.ME_UID);
        //开始发送
        OtherApi.getService()
                .post_sendMessage(me, other, "100", "yxj", msg, null, "2", 1, "2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Result result) {
                    }
                });
    }

    /**
     * 排好序，重新组合需要的数据
     *
     * @param friendList 好友列表
     */
    private void combinationLists(FriendList friendList) {

        FriendList.DataBean.ListBean tempMember;

        //其他好友
        List<FriendList.DataBean.ListBean> tempList = new ArrayList<>();
        for (int i = 3; i < friendList.getData().getList().size(); i++) {
            tempMember = friendList.getData().getList().get(i);
            if (StringUtils.isEmpty(tempMember.getName())) {
                //若用户名为空，显示手机号，中间四位为*
                String iphone = tempMember.getPhone();
                String showName;
                if (iphone != null) {
                    showName = iphone.substring(0, 3) + "****" + iphone.substring(7, 11);
                } else {
                    showName = "测试用户";
                }
                tempMember.setName(showName);
            }
            String pinyin = characterParser.getSelling(friendList.getData().getList().get(i).getName());//用户名转拼音
            String sortString = pinyin.substring(0, 1).toUpperCase();

            if (sortString.matches("[A-Z]")) {
                tempMember.setSortLetters(sortString.toUpperCase());
            } else {
                tempMember.setSortLetters("#");
            }
            tempList.add(tempMember);
        }
        Collections.sort(tempList, pinyinComparator);

        mList.addAll(tempList);
    }
}
