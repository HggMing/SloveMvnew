package com.ming.slove.mvnew.shop.shoptab1.books;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.melnykov.fab.FloatingActionButton;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.MyServiceClient;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.app.ThemeHelper;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.base.LazyLoadFragment;
import com.ming.slove.mvnew.common.utils.BaseTools;
import com.ming.slove.mvnew.model.bean.Book2List;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 已借图书
 */
public class BooksTab2Fragment extends LazyLoadFragment {

    @Bind(R.id.m_x_recyclerview)
    XRecyclerView mXRecyclerView;
    @Bind(R.id.m_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.content_empty)
    TextView contentEmpty;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private BooksAdapter mAdapter;
    private List<Book2List.DataBean.ListBean> mList = new ArrayList<>();

    final private static int PAGE_SIZE = 10;
    private int page = 1;

    @Override
    public int getLayout() {
        return R.layout.fragment_books;
    }

    @Override
    public void initViews(View view) {
        config();
        // 刷新时，指示器旋转后变化的颜色
        String theme = ThemeHelper.getThemeColorName(getContext());
        int themeColorRes = getResources().getIdentifier(theme, "color", getContext().getPackageName());
        mRefreshLayout.setColorSchemeResources(themeColorRes);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.setItem(null);
                mList.clear();
                page = 1;
                initData(page);
            }
        });
    }

    @Override
    public void loadData() {
        initData(page);
    }

    private void config() {
        //设置fab
        fab.setVisibility(View.GONE);
        //设置recyclerview布局
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mXRecyclerView.addItemDecoration(new NoDecoration(getContext()));//添加空白分割线
        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画

        //设置adapter
        mAdapter = new BooksAdapter();
        mXRecyclerView.setAdapter(mAdapter);

        mXRecyclerView.setPullRefreshEnabled(false);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                initData(++page);
                mXRecyclerView.loadMoreComplete();
            }
        });
    }

    private void initData(final int page) {
        String vid = Hawk.get(APPS.MANAGER_VID);
        MyServiceClient.getService()
                .get_BookListTab2(vid, page, PAGE_SIZE)
                .flatMap(new Func1<Book2List, Observable<Book2List.DataBean.ListBean>>() {
                    @Override
                    public Observable<Book2List.DataBean.ListBean> call(Book2List book2List) {
                        return Observable.from(book2List.getData().getList());
                    }
                })
                .filter(new Func1<Book2List.DataBean.ListBean, Boolean>() {
                    @Override
                    public Boolean call(Book2List.DataBean.ListBean listBean) {
                        return "0".equals(listBean.getStatus());//0为已借，1为已还
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Book2List.DataBean.ListBean>() {
                    @Override
                    public void onCompleted() {
                        if (mList.isEmpty()) {
                            contentEmpty.setVisibility(View.VISIBLE);
                            contentEmpty.setText(R.string.empty_book_tab1);
                        } else {
                            contentEmpty.setVisibility(View.GONE);
                        }
                        mAdapter.setItem(mList);
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Book2List.DataBean.ListBean listBean) {
                        mList.add(listBean);
                    }
                });
    }

    static class BooksAdapter extends BaseRecyclerViewAdapter<Book2List.DataBean.ListBean, BooksAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_tab1_books2, parent, false);
            return new ViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Book2List.DataBean.ListBean data = mList.get(position);
            //借阅人姓名
            holder.tvUser.setText("借阅人：" + data.getName());
            //借阅人手机号
            holder.tvPhone.setText(data.getPhone());
            //显示书号
            holder.tvNum.setText("书号：  " + data.getBooknum());
            //显示书名,需要先做base64解码
            String bookName = new String(Base64.decode(data.getBookname(), Base64.DEFAULT));
            holder.tvName.setText("书名：《" + bookName + "》");
            //显示借书时间
            String date = data.getCtime();
            String timeFormat = BaseTools.formatDateByFormat(date, "yyyy-MM-dd HH:mm");
            holder.tvTime.setText("借阅时间：" + timeFormat);
        }


        static class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tv_user)
            TextView tvUser;
            @Bind(R.id.tv_phone)
            TextView tvPhone;
            @Bind(R.id.tv_num)
            TextView tvNum;
            @Bind(R.id.tv_name)
            TextView tvName;
            @Bind(R.id.tv_time)
            TextView tvTime;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
