package com.ming.slove.mvnew.tab3.livevideo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.video.VideoApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.RoomList;
import com.ming.slove.mvnew.tab3.livevideo.inroom.LiveVideoActivity;
import com.ming.slove.mvnew.tab3.livevideo.newroom.AddNewLiveRoomActivity;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VideoRoomListActivity extends BackActivity implements BaseRecyclerViewAdapter.OnItemClickListener {
    private VideoRoomListAdapter mAdapter;
    List<RoomList.DataBean.ListBean> mList = new ArrayList<>();

    private int page = 1;
    final private static int PAGE_SIZE = 5;
    private int REQUEST_CODE = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("直播间");

        initView();
        initData(page);//获取List数据
    }

    //配置
    private void initView() {
        showLoading(true);
        //设置adapter
        mAdapter = new VideoRoomListAdapter();
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
//        mXRecyclerView.addItemDecoration(new MyItemDecoration(this));//添加分割线

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
        showFab(mXRecyclerView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(VideoRoomListActivity.this, "新建直播间", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(VideoRoomListActivity.this, AddNewLiveRoomActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private void initData(int page) {
        VideoApi.getService()
                .get_RoomList(page, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RoomList>() {
                    @Override
                    public void onCompleted() {
                        hideRefresh();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(RoomList roomList) {
                        mList.addAll(roomList.getData().getList());
                        if (mList.isEmpty() || mList == null) {
                            showEmpty(R.string.empty_video_room_list);
                        } else {
                            hideEmpty();
                        }
                        mAdapter.setItem(mList);
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
//        Toast.makeText(this, "进入直播间", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LiveVideoActivity.class);
        intent.putExtra(LiveVideoActivity.VIDEO_ROOM_INFO, mList.get(position));
        intent.putExtra(LiveVideoActivity.VIDEO_ROOM_OWNER_HEAD, (String) view.getTag());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onItemLongClick(View view, final int position) {
//        Toast.makeText(this, "关闭此直播间", Toast.LENGTH_SHORT).show();
        MyDialog.Builder builder = new MyDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("是否删除此直播间?")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delRoom(position);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void delRoom(int position) {
        String auth = Hawk.get(APPS.USER_AUTH);
        VideoApi.getService()
                .post_DelRoom(auth, mList.get(position).getRoom_id())
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
                        if (result.getErr() == 0) {
                            Toast.makeText(VideoRoomListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            mAdapter.setItem(null);
                            mList.clear();
                            page = 1;
                            initData(page);
                        } else {
                            Toast.makeText(VideoRoomListActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }

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
