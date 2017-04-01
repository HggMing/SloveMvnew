package com.ming.slove.mvnew.tab3.villagelist;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.melnykov.fab.FloatingActionButton;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.LazyLoadFragment;
import com.ming.slove.mvnew.common.utils.MyItemDecoration;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.model.bean.FollowVillageList;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.event.ChangeThemeColorEvent;
import com.ming.slove.mvnew.tab2.friendlist.FriendListActivity;
import com.ming.slove.mvnew.tab3.addfollow.FollowVillageActivity;
import com.ming.slove.mvnew.tab3.livevideo.VideoRoomListActivity;
import com.ming.slove.mvnew.tab3.villagebbs.VillageBbsActivity;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class VillageListFragment extends LazyLoadFragment implements VillageListAdapter.OnItemClickListener {

    @Bind(R.id.listview_header_text)
    XRecyclerView mXRecyclerView;
    @Bind(R.id.content_empty)
    TextView contentEmpty;
    @Bind(R.id.error)
    View onError;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private VillageListAdapter mAdapter;
    List<FollowVillageList.DataEntity.ListEntity> mList = new ArrayList<>();

    final private static int PAGE_SIZE = 20;
    private int page = 1;
    private String auth;

    @Override
    public int getLayout() {
        return R.layout.fragment_tab3;
    }

    @Override
    public void initViews(View view) {
        auth = Hawk.get(APPS.USER_AUTH);

        setHasOptionsMenu(true);
        configXRecyclerView();//XRecyclerView配置

        onError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onError.setVisibility(View.GONE);
                mAdapter.clear();
                mList.clear();
                page = 1;
                getDataList(page);
            }
        });
        showFab();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void loadData() {
        getDataList(page);//获取followList数据和cnt值
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_follow) {
            Intent intent = new Intent(getContext(), FollowVillageActivity.class);
            startActivityForResult(intent, 12355);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //开启fab
    public void showFab() {
        fab.attachToRecyclerView(mXRecyclerView);//fab随recyclerView的滚动，隐藏和出现
        int themeColor = ThemeUtils.getColorById(getContext().getApplicationContext(), R.color.theme_color_primary);
        int themeColor2 = ThemeUtils.getColorById(getContext().getApplicationContext(), R.color.theme_color_primary_dark);
        fab.setColorNormal(themeColor);//fab背景颜色
        fab.setColorPressed(themeColor2);//fab点击后背景颜色
        fab.setColorRipple(themeColor2);//fab点击后涟漪颜色

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowVillageActivity.class);
                startActivityForResult(intent,12355);
            }
        });
    }

    //更换主题后手动刷新fab颜色
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeThemeColor(ChangeThemeColorEvent event) {
        int themeColor = ThemeUtils.getColorById(getContext().getApplicationContext(), R.color.theme_color_primary);
        int themeColor2 = ThemeUtils.getColorById(getContext().getApplicationContext(), R.color.theme_color_primary_dark);
        fab.setColorNormal(themeColor);//fab背景颜色
        fab.setColorPressed(themeColor2);//fab点击后背景颜色
        fab.setColorRipple(themeColor2);//fab点击后涟漪颜色
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 12355:
                if (resultCode == Activity.RESULT_OK) {
                    //关注村圈后更新列表
                    mAdapter.clear();
                    mList.clear();
                    page = 1;
                    getDataList(page);
                }
                break;
        }
    }

    //配置RecyclerView
    private void configXRecyclerView() {
        mXRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));//设置布局管理器
        mAdapter = new VillageListAdapter();
        mXRecyclerView.setAdapter(mAdapter);//设置adapter
        mAdapter.setOnItemClickListener(VillageListFragment.this);

        mXRecyclerView.addItemDecoration(new MyItemDecoration(getContext()));//添加分割线
        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画

        mXRecyclerView.setPullRefreshEnabled(false);
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
//        mXRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);//自定义下拉刷新箭头图标

        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mAdapter.setItem(null);
                mList.clear();
                page = 1;
                getDataList(page);
                mXRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                getDataList(++page);
                mXRecyclerView.loadMoreComplete();
            }
        });
        //添加直播入口head
        View header = LayoutInflater.from(getContext()).inflate(R.layout.head_live_video, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
        mXRecyclerView.addHeaderView(header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "进入直播间", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), VideoRoomListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getDataList(int page) {
        OtherApi.getService().get_FollowList(auth, page, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FollowVillageList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onError.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(FollowVillageList followVillageList) {
                        if (followVillageList != null && followVillageList.getErr() == 0) {
                            mList.addAll(followVillageList.getData().getList());
                            if (mList.isEmpty() || mList == null) {
                                contentEmpty.setVisibility(View.VISIBLE);
                            } else {
                                contentEmpty.setVisibility(View.GONE);
                            }
                            mAdapter.setItem(mList);
                        }
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        //点击选项操作
        String vid = mList.get(position).getVillage_id();
        String vname = mList.get(position).getVillage_name();
        String vpic = APPS.BASE_URL + mList.get(position).getPic();
        Intent intent = new Intent();
        intent.putExtra(VillageBbsActivity.VILLAGE_ID, vid);
        intent.putExtra(VillageBbsActivity.VILLAGE_NAME, vname);
        intent.putExtra(VillageBbsActivity.VILLAGE_PIC, vpic);
        intent.setClass(getContext(), VillageBbsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        //长按选项操作
        String villageName = mList.get(position).getVillage_name();
        MyDialog.Builder builder = new MyDialog.Builder(getContext());
        builder.setTitle("提示")
                .setMessage("取消关注" + villageName + "?" + "\n" + "(取消关注后，你将不再看到该村的任何信息，同时该村将会从“我的村”列表中移除！)")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeFromServer(position);
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

    /**
     * 发送请求，从服务器取消关注村圈
     *
     * @param position 点击项
     */
    private void removeFromServer(final int position) {
        String vid = mList.get(position).getVillage_id();
        OtherApi.getService()
                .getCall_DelFollowList(auth, vid)
                .enqueue(new Callback<Result>() {
                             @Override
                             public void onResponse(Call<Result> call, Response<Result> response) {
                                 if (response.isSuccessful()) {
                                     Result result = response.body();
                                     if (result != null && result.getErr() == 0) {
                                         // Toast.makeText(mActivity, result.getMsg(), Toast.LENGTH_SHORT).show();
                                         mList.remove(position);
                                         mAdapter.notifyItemRemoved(position + 1);
//                                         mAdapter.notifyItemRangeChanged(position, mList.size()+2);
                                         mAdapter.notifyDataSetChanged();
                                     }
                                 }
                             }

                             @Override
                             public void onFailure(Call<Result> call, Throwable t) {
                             }
                         }

                );
    }
}
