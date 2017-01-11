package com.ming.slove.mvnew.tab3.villagebbs.likeusers;

import android.os.Bundle;

import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.MyItemDecoration2;
import com.ming.slove.mvnew.model.bean.ZanList;

import java.util.List;

public class LikeUserListActivity extends BackActivity {

    public static String LIKE_USER_COUNT = "like_user_count";//点赞人总数
    public static String LIKE_USER_LIST = "like_user_list";//点赞人列表

    private LikeUserListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int count = getIntent().getIntExtra(LIKE_USER_COUNT, 0);
        setToolbarTitle(count + "个人觉得很赞");

        initView();
        initData();
    }

    private void initView() {
        mAdapter = new LikeUserListAdapter();
        addRecycleView(mAdapter);
        mRecyclerView.addItemDecoration(new MyItemDecoration2(this));//添加分割线

    }

    private void initData() {
        List<ZanList.DataBean.ListBean> likeList = getIntent().getParcelableArrayListExtra(LIKE_USER_LIST);
        mAdapter.setItem(likeList);
    }
}
