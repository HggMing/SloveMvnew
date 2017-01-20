package com.ming.slove.mvnew.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.bilibili.magicasakura.widgets.TintProgressBar;
import com.bilibili.magicasakura.widgets.TintToolbar;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.melnykov.fab.FloatingActionButton;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.ThemeHelper;

public class BackActivity extends BaseActivity {

    private TextView toolbarTitle;
    private TintToolbar toolbar;
    private FrameLayout parentLayout;
    private SwipeRefreshLayout mRefresh;
    private TintProgressBar mProgress;
    private TextView contentEmpty;
    private FloatingActionButton fab;

    protected RecyclerView mRecyclerView;
    protected XRecyclerView mXRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_base);

        initViews();
    }

    private void initViews() {

        toolbar = (TintToolbar) findViewById(R.id.toolbar_base);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        parentLayout = (FrameLayout) findViewById(R.id.contentFrame);

        mRefresh = (SwipeRefreshLayout) findViewById(R.id.m_refresh);
        mProgress = (TintProgressBar) findViewById(R.id.m_progress);
        contentEmpty = (TextView) findViewById(R.id.content_empty);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        //禁用下拉刷新
        mRefresh.setEnabled(false);
        //设置toolbar
        assert toolbar != null;
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            //设置toolbar后,开启返回图标
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //设备返回图标样式
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_toolbar_back);
        }

    }

    public void setToolbarTitle(@StringRes int resid) {
        toolbarTitle.setText(resid);
    }

    public void setToolbarTitle(String s) {
        toolbarTitle.setText(s);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //是否显示加载进度
    public void showLoading(boolean isShow) {
        if (isShow) {
            mProgress.setVisibility(View.VISIBLE);
        } else {
            mProgress.setVisibility(View.GONE);
        }
    }

    //开启下拉刷新
    public void showRefresh(SwipeRefreshLayout.OnRefreshListener listener) {
        mRefresh.setEnabled(true);
        // 刷新时，指示器旋转后变化的颜色
        String theme = ThemeHelper.getThemeColorName(this);
        int themeColorRes = getResources().getIdentifier(theme, "color", getPackageName());
        mRefresh.setColorSchemeResources(themeColorRes);

        mRefresh.setOnRefreshListener(listener);
    }

    //隐藏刷新动画
    public void hideRefresh() {
        mRefresh.setRefreshing(false);
        showLoading(false);
    }

    //显示无数据界面
    public void showEmpty(@StringRes int resid) {
        contentEmpty.setVisibility(View.VISIBLE);
        contentEmpty.setText(resid);
    }

    //隐藏无数据界面
    public void hideEmpty() {
        contentEmpty.setVisibility(View.GONE);
    }

    //开启fab
    public void showFab(RecyclerView mXRecyclerView, @Nullable View.OnClickListener l) {
        fab.setVisibility(View.VISIBLE);
        if (mXRecyclerView != null)
            fab.attachToRecyclerView(mXRecyclerView);//fab随recyclerView的滚动，隐藏和出现
        int themeColor = ThemeUtils.getColorById(this, R.color.theme_color_primary);
        int themeColor2 = ThemeUtils.getColorById(this, R.color.theme_color_primary_dark);
        fab.setColorNormal(themeColor);//fab背景颜色
        fab.setColorPressed(themeColor2);//fab点击后背景颜色
        fab.setColorRipple(themeColor2);//fab点击后涟漪颜色

        fab.setOnClickListener(l);
    }

    //隐藏fab
    public void hideFab() {
        fab.setVisibility(View.GONE);
    }

    public <T extends RecyclerView.Adapter> void addRecycleView(T mAdapter) {
        setContentView(R.layout.content_recyclerview);
        mRecyclerView = (RecyclerView) findViewById(R.id.m_recyclerview);

        //设置recyclerview布局
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
        mRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
//        mXRecyclerView.addItemDecoration(new MyItemDecoration2(this));//添加分割线

        mRecyclerView.setAdapter(mAdapter);
    }

    public <T extends RecyclerView.Adapter> void addXRecycleView(T mAdapter, XRecyclerView.LoadingListener listener) {
        setContentView(R.layout.content_x_recyclerview);
        mXRecyclerView = (XRecyclerView) findViewById(R.id.m_x_recyclerview);

        //设置recyclerview布局
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
//        mXRecyclerView.addItemDecoration(new MyItemDecoration(this));//添加分割线
        //设置adapter
        mXRecyclerView.setAdapter(mAdapter);
        //设置xRecycleview
        mXRecyclerView.setPullRefreshEnabled(false);//关闭刷新功能
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);

        mXRecyclerView.setLoadingListener(listener);
    }

//    private LinearLayout parentLayout;//把父类activity和子类activity的view都add到这里

    /**
     * 初始化contentview
     */
    private void initContentView(int layoutResID) {
//        ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
//        if (viewGroup != null) {
//            viewGroup.removeAllViews();
//        }
//        parentLayout = new LinearLayout(this);
//        parentLayout.setOrientation(LinearLayout.VERTICAL);
//        if (viewGroup != null) {
//            viewGroup.addView(parentLayout);
//        }
//        LayoutInflater.from(this).inflate(layoutResID, parentLayout, true);
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, parentLayout, true);
    }

    @Override
    public void setContentView(View view) {
        parentLayout.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        parentLayout.addView(view, params);
    }
}
