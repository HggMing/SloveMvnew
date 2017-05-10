package com.ming.slove.mvnew.tab3.affairs;


import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.common.base.LazyLoadFragment;
import com.ming.slove.mvnew.common.utils.MyItemDecoration;
import com.ming.slove.mvnew.model.bean.NewsList;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 政务相关：
 */
public class NewsListFragment extends LazyLoadFragment implements NewsListAdapter.OnItemClickListener {
    final private static int PAGE_SIZE = 20;
    @Bind(R.id.g_affairs_list)
    XRecyclerView mXRecyclerView;
    @Bind(R.id.content_empty)
    TextView contentEmpty;
    @Bind(R.id.m_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    List<NewsList.DataBean.ListBean> mList = new ArrayList<>();
    private NewsListAdapter mAdapter = new NewsListAdapter();
    private int page = 1;
    private int type;

    //配置RecyclerView
    private void configXRecyclerView() {
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));//设置布局管理器
        mXRecyclerView.addItemDecoration(new MyItemDecoration(getContext()));//添加分割线
//        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画

        mXRecyclerView.setAdapter(mAdapter);//设置adapter
        mAdapter.setOnItemClickListener(NewsListFragment.this);

        mXRecyclerView.setPullRefreshEnabled(false);
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);

        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mXRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                getDataList(++page);
                mXRecyclerView.loadMoreComplete();
            }
        });
    }

    private void getDataList(int page) {
        String vid = this.getArguments().getString(GovernmentAffairsActivity.VILLAGE_ID);
        type = this.getArguments().getInt(GovernmentAffairsActivity.FRAGMENT_TYPE);
        OtherApi.getService()
                .get_NewsList(vid, type, page, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NewsList>() {
                    @Override
                    public void onCompleted() {
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(NewsList newsList) {
                        mList.addAll(newsList.getData().getList());
                        if (mList.isEmpty() || mList == null) {
                            contentEmpty.setVisibility(View.VISIBLE);
                            String s = null;
                            switch (type) {
                                case 1:
                                    s = "新闻";
                                    break;
                                case 2:
                                    s = "政策";
                                    break;
                                case 3:
                                    s = "服务";
                                    break;
                                case 4:
                                    s = "资讯";
                                    break;
                            }
                            contentEmpty.setText("该村还没有" + s + "相关内容");
                        } else {
                            contentEmpty.setVisibility(View.GONE);
                        }
                        mAdapter.setItem(mList);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_tab3_ga;
    }

    @Override
    public void initViews(View view) {
        configXRecyclerView();//XRecyclerView配置

        // 刷新时，指示器旋转后变化的颜色
        int themeColor = ThemeUtils.getColorById(getContext(), R.color.theme_color_primary);
        mRefreshLayout.setColorSchemeColors(themeColor);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.setItem(null);
                mList.clear();
                page = 1;
                getDataList(page);
            }
        });
    }

    @Override
    public void loadData() {
        getDataList(page);//获取followList数据和cnt值
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getContext(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.NEWS_ID, mList.get(position).getId());
        intent.putExtra(NewsDetailActivity.TYPE, type);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}
