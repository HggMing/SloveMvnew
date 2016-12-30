package com.ming.slove.mvnew.tab4.mysetting.myorder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.ThemeHelper;
import com.ming.slove.mvnew.api.MyServiceClient;
import com.ming.slove.mvnew.common.base.LazyLoadFragment;
import com.ming.slove.mvnew.common.utils.MyItemDecoration;
import com.ming.slove.mvnew.model.bean.NewsList;
import com.ming.slove.mvnew.tab3.affairs.GovernmentAffairsActivity;
import com.ming.slove.mvnew.tab3.affairs.NewsListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ming on 2016/11/15.
 */
public class MyOrderListFragment extends LazyLoadFragment {

    @Override
    public int getLayout() {
        return R.layout.fragment_books;
    }

    @Override
    public void initViews(View view) {
    }

    @Override
    public void loadData() {
    }
}
