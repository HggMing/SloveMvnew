package com.ming.slove.mvnew.tab3.newpost;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.MyServiceClient;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.MyGallerFinal;
import com.ming.slove.mvnew.common.utils.PhotoOperate;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.common.widgets.gallerfinal.FunctionConfig;
import com.ming.slove.mvnew.common.widgets.gallerfinal.GalleryFinal;
import com.ming.slove.mvnew.common.widgets.gallerfinal.model.PhotoInfo;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.UploadFiles;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NewPostActivity extends BackActivity implements NewPostAdapter.OnItemClickListener {

    public static String VILLAGE_ID = "village_id_post";
    @Bind(R.id.edit)
    EditText edit;
    @Bind(R.id.popPicture)
    ImageView popPicture;
    @Bind(R.id.popPhoto)
    ImageView popPhoto;
    @Bind(R.id.gridRecyclerView)
    RecyclerView mXRecyclerView;

    private NewPostAdapter mAdapter = new NewPostAdapter();
    private RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
    List<PhotoInfo> imageList = new ArrayList<>();

    private String vid;//村id
    private String auth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_new_post);
        auth = Hawk.get(APPS.USER_AUTH);
        configXRecyclerView();
    }

    //配置RecyclerView
    private void configXRecyclerView() {
        mAdapter.setOnItemClickListener(this);
        mXRecyclerView.setAdapter(mAdapter);//设置adapter
        mXRecyclerView.setLayoutManager(mLayoutManager);//设置布局管理器

        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
    }

    private void loadPicture() {
        MyGallerFinal aFinal = new MyGallerFinal();
        GalleryFinal.init(aFinal.getCoreConfig(this));
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setMutiSelectMaxSize(9)//配置多选数量
                .setEnableEdit(false)//开启编辑功能
                .setEnableCrop(false)//开启裁剪功能
                .setEnableRotate(false)//开启旋转功能
                .setEnableCamera(false)//开启相机功能
                .setSelected(imageList)//添加已选列表,只是在列表中默认呗选中不会过滤图片
                .build();
        GalleryFinal.openGalleryMuti(1002, functionConfig, mOnHanlderResultCallback);
    }

    GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            imageList.clear();
            imageList.addAll(resultList);
            mAdapter.setItem(imageList);
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {

        }
    };

    private void loadPhoto() {
        MyGallerFinal aFinal = new MyGallerFinal();
        GalleryFinal.init(aFinal.getCoreConfig(this));
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableEdit(false)//开启编辑功能
                .setEnableCrop(false)//开启裁剪功能
                .setEnableRotate(false)//开启旋转功能
                .setEnableCamera(false)//开启相机功能
                .setEnablePreview(true)//开启预览功能
                .setSelected(imageList)//添加已选列表,只是在列表中默认呗选中不会过滤图片
                .build();
        GalleryFinal.openCamera(1003, functionConfig, mOnHanlderResultCallback2);
    }

    GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback2 = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (imageList.size() < 9) {
                imageList.add(resultList.get(0));
            } else {
                Toast.makeText(NewPostActivity.this, "图片已达到最大选择数量", Toast.LENGTH_SHORT).show();
            }
            mAdapter.setItem(imageList);
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 将菜单图标添加到toolbar
        getMenuInflater().inflate(R.menu.menu_new_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_post) {
            item.setEnabled(false);
            vid = getIntent().getStringExtra(VILLAGE_ID);
            final String conts = edit.getText().toString();

            if (StringUtils.isEmpty(conts)) {
                Toast.makeText(NewPostActivity.this, "请输入帖子内容。", Toast.LENGTH_SHORT).show();
                item.setEnabled(true);
                return true;
            }

            dialog = new ProgressDialog(NewPostActivity.this);
            dialog.setMessage("发布中...");
            dialog.setCancelable(false);
            dialog.show();

            Subscriber<Result> subscriber = new Subscriber<Result>() {
                @Override
                public void onCompleted() {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onError(Throwable e) {
                    Log.i("mm", e.getMessage());
                }

                @Override
                public void onNext(Result result) {
                    Toast.makeText(NewPostActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            };

            if (imageList.isEmpty() || imageList == null) {
                MyServiceClient.getService().post_BBSPost(auth, vid, null, conts, null, null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);
                return true;
            } else {
                Observable.from(imageList)
                        .flatMap(new Func1<PhotoInfo, Observable<UploadFiles>>() {
                            @Override
                            public Observable<UploadFiles> call(PhotoInfo photoInfo) {
//                                File file = new File(photoInfo.getPhotoPath());
                                //对图片压缩处理
                                File file = null;
                                try {
                                    file = new PhotoOperate().scal(photoInfo.getPhotoPath());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                RequestBody requestBody = null;
                                if (file != null) {
                                    requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                                }
                                RequestBody authBody = RequestBody.create(MediaType.parse("text/plain"), auth);

                                return MyServiceClient.getService().post_UploadImage(authBody, requestBody);
                            }
                        })
                        .map(new Func1<UploadFiles, String>() {
                            @Override
                            public String call(UploadFiles uploadFiles) {
//                            Log.i("mm:文件上传消息提示", uploadFiles.getMsg());
                                return uploadFiles.getInsert_id() + ",";
                            }
                        })
                        .buffer(imageList.size())
                        .map(new Func1<List<String>, String>() {
                            @Override
                            public String call(List<String> strings) {
                                String t = "";
                                for (int i = 0; i < strings.size(); i++) {
                                    t += strings.get(i);
                                }
//                            Log.i("mm:文件id串", t);
                                return t.substring(0, t.length() - 1);
                            }
                        })
                        .flatMap(new Func1<String, Observable<Result>>() {
                            @Override
                            public Observable<Result> call(String s) {
                                return MyServiceClient.getService().post_BBSPost(auth, vid, null, conts, null, s);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.popPicture, R.id.popPhoto})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.popPicture://点击选择图片
                loadPicture();
                break;
            case R.id.popPhoto://点击拍照
                loadPhoto();
                break;
        }
    }

    @Override
    public void onItemClick(View view, final int position) {
        switch (view.getId()) {
            case R.id.post_picture://点击图片查看大图
                Intent intent = new Intent(this, BigImageViewActivity.class);
                intent.putExtra(BigImageViewActivity.IMAGE_LIST, (Serializable) imageList);
                intent.putExtra(BigImageViewActivity.IMAGE_INDEX, position);
                startActivity(intent);
                break;
            case R.id.del_picture://点击右上角的×，删除选择的图片
                MyDialog.Builder builder = new MyDialog.Builder(this);
                builder.setTitle("提示")
                        .setMessage("要删除这张照片吗?")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mAdapter.notifyItemRemoved(position);
                                        imageList.remove(position);
                                        mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                if (!isFinishing()) {
                    builder.create().show();
                }
                break;
        }
    }
}
