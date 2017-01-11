package com.ming.slove.mvnew.tab3.villagesituation.villageinfo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.MyServiceClient;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.utils.MyItemDecoration;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.VillageInfo;
import com.ming.slove.mvnew.shop.shoptab3.AddVillageInfoActivity;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VillageInfoActivity extends BackActivity implements BaseRecyclerViewAdapter.OnItemClickListener {
    public static String VILLAGE_ID = "the_village_id";
    public static String TYPE = "the_title_name";
    public static String WHERE_CLICK = "where_click";

    private String vid;
    private int type;//1、荣誉室2、活动3、村委（Item不同，单独写）4、美食
    private int where_click = 0;//1、店长点击管理；其他：村圈内点击查看

    private VillageInfoAdapter mAdapter;
    List<VillageInfo.DataBean> mList = new ArrayList<>();

    private int page = 1;
    final private static int PAGE_SIZE = 20;
    private int REQUEST_CODE = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle();

        initView();
        initData(page);//获取List数据
    }

    private void setTitle() {
        where_click = getIntent().getIntExtra(WHERE_CLICK, 0);
        if (where_click == 1) {
            vid = Hawk.get(APPS.MANAGER_VID);
        } else {
            vid = getIntent().getStringExtra(VILLAGE_ID);
        }
        type = getIntent().getIntExtra(TYPE, 0);
        switch (type) {
            case 1:
                setToolbarTitle("荣誉室");
                break;
            case 2:
                setToolbarTitle("活动");
                break;
            case 4:
                setToolbarTitle("美食");
                break;
        }
    }

    //配置
    private void initView() {
        showLoading(true);
        //设置adapter
        mAdapter = new VillageInfoAdapter();
        addXRecycleView(mAdapter, new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                initData(++page);
                mXRecyclerView.loadMoreComplete();
            }
        });
        mXRecyclerView.addItemDecoration(new MyItemDecoration(this));//添加分割线

        mAdapter.setOnItemClickListener(this);

        //下拉刷新
        showRefresh(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.setItem(null);
                mList.clear();
                page = 1;
                initData(page);//获取List数据
            }
        });
        //设置fab
        if (where_click == 1) {
            showFab(mXRecyclerView, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VillageInfoActivity.this, AddVillageInfoActivity.class);
                    intent.putExtra(AddVillageInfoActivity.TYPE, type);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
        } else {
            hideFab();
        }
    }

    private void initData(int page) {
        MyServiceClient.getService()
                .get_VillageInfoList(vid, type, page, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VillageInfo>() {
                    @Override
                    public void onCompleted() {
                        hideRefresh();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(VillageInfo villageInfo) {
                        mList.addAll(villageInfo.getData());
                        if (mList.isEmpty() || mList == null) {
                            showEmpty(R.string.empty_village_info);
                        } else {
                            hideEmpty();
                        }
                        mAdapter.setItem(mList);
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, VillageInfoDetailActivity.class);
        intent.putExtra(VillageInfoDetailActivity.VILLAGE_INFO_DETAIL, mList.get(position));
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        if (where_click == 1) {//店长管理里，长按删除
            MyDialog.Builder builder = new MyDialog.Builder(this);
            builder.setMessage("确定删除该条记录?")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteDate(position);
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
    }

    /**
     * 删除记录
     */
    private void deleteDate(final int position) {
        String id = mList.get(position).getId();
        String auth = Hawk.get(APPS.USER_AUTH);
        MyServiceClient.getService()
                .post_DelVillageInfo(auth, id)
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
                        mList.remove(position);
                        mAdapter.notifyItemRemoved(position + 1);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {//刷新
                mAdapter.setItem(null);
                mList.clear();
                page = 1;
                initData(page);
            }
        }
    }
}
