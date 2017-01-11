package com.ming.slove.mvnew.tab2.frienddetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.MyServiceClient;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.model.bean.BBSList;
import com.ming.slove.mvnew.tab3.villagebbs.VillageBbsActivity;
import com.ming.slove.mvnew.tab3.villagebbs.VillageBbsAdapter;
import com.ming.slove.mvnew.tab3.villagebbs.bbsdetail.BbsDetailActivity;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FriendBbsActivity extends BackActivity implements VillageBbsAdapter.OnItemClickListener {

    public static String UID = "查看用户帖子，提供用户id";
    public static String USER_NAME = "显示的用户名，用于标题";


    private VillageBbsAdapter mAdapter;
    private List<BBSList.DataEntity.ListEntity> mList = new ArrayList<>();
    private BBSList.DataEntity.ListEntity bbsDetail;

    final private static int PAGE_SIZE = 5;
    private int page = 1;
    private int ppid;//点击查看详情的帖子号
    private final int REQUEST_LIKE_COMMENT_NUMBER = 1000;

    private String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle(getIntent().getStringExtra(USER_NAME));

        initView();
        getBBSList(page);//获取bbsList数据
    }


    private void initView() {
        auth = Hawk.get(APPS.USER_AUTH);
        showLoading(true);
        //设置adapter
        mAdapter = new VillageBbsAdapter();
        addXRecycleView(mAdapter, new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                getBBSList(++page);
                mXRecyclerView.loadMoreComplete();
            }
        });
        mAdapter.setOnItemClickListener(FriendBbsActivity.this);
    }

    private void getBBSList(int page) {
        String mUid = getIntent().getStringExtra(UID);
        MyServiceClient.getService()
                .get_FriendBbsList(auth, mUid, page, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BBSList>() {
                    @Override
                    public void onCompleted() {
                        showLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("mm", e.getMessage());
                    }

                    @Override
                    public void onNext(BBSList bbsList) {
                        if (bbsList != null && bbsList.getErr() == 0) {
                            if (mList.isEmpty() && bbsList.getData().getList().isEmpty()) {
                               showEmpty(R.string.empty_friend_bbs);
                            } else {
                                hideEmpty();
                            }
                            mAdapter.setItem(mList, bbsList.getData().getList());
                        }
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.bbs_item:
//                Toast.makeText(this, "点击整个选项操作", Toast.LENGTH_SHORT).show();
                bbsDetail = mList.get(position);
                ppid = position;
                Intent intent = new Intent(this, BbsDetailActivity.class);
                intent.putExtra(BbsDetailActivity.BBS_DETAIL, bbsDetail);
                startActivityForResult(intent, REQUEST_LIKE_COMMENT_NUMBER);
                break;
            case R.id.bbs_comment:
//                Toast.makeText(this, "点击留言操作", Toast.LENGTH_SHORT).show();
                bbsDetail = mList.get(position);
                ppid = position;
                Intent intent2 = new Intent(this, BbsDetailActivity.class);
                intent2.putExtra(BbsDetailActivity.BBS_DETAIL, bbsDetail);
                intent2.putExtra(BbsDetailActivity.IS_CLICK_COMMENT, true);
                startActivityForResult(intent2, REQUEST_LIKE_COMMENT_NUMBER);
                break;
            default:
                break;

        }
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LIKE_COMMENT_NUMBER://帖子详情页，点赞或评论后，就数据返回显示
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String newCommentNumber = data.getExtras().getString(VillageBbsActivity.COMMENT_NO_NEW);
                        if (newCommentNumber != null && !newCommentNumber.isEmpty()) {
                            mList.get(ppid).setNums(newCommentNumber);
                        }
                        int isCliked = data.getExtras().getInt(VillageBbsActivity.LIKEED_TAG);
                        if (isCliked == 1) {
                            mList.get(ppid).setMy_is_zan(1);
                            String newLikeNumber = data.getExtras().getString(VillageBbsActivity.LIKE_NO_NEW);
                            mList.get(ppid).setZans(newLikeNumber);
                        }
                        mAdapter.setItem(mList);
                    } else {//删除帖子后，刷新列表
                        mAdapter.setItem(null);
                        mList.clear();
                        page = 1;
                        getBBSList(page);
                    }
                }
                break;
        }
    }
}
