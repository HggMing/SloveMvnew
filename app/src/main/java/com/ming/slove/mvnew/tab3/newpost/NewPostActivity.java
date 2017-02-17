package com.ming.slove.mvnew.tab3.newpost;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintRadioButton;
import com.bumptech.glide.Glide;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.MyPictureSelector;
import com.ming.slove.mvnew.common.utils.PhotoOperate;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.common.widgets.video.MyVideoPlayer;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.UploadFiles;
import com.orhanobut.hawk.Hawk;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.util.FileUtils;
import com.yalantis.ucrop.util.PictureConfig;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
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
    @Bind(R.id.gridRecyclerView)
    RecyclerView mXRecyclerView;
    @Bind(R.id.m_player)
    MyVideoPlayer mPlayer;
    @Bind(R.id.img_add_video)
    ImageView addVideo;
    @Bind(R.id.radio_all)
    RadioGroup radioAll;
    @Bind(R.id.rb_picture)
    TintRadioButton rbPicture;
    @Bind(R.id.rb_video)
    TintRadioButton rbVideo;

    private NewPostAdapter mAdapter = new NewPostAdapter();
    private RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
    private List<LocalMedia> imageList = new ArrayList<>();

    private String vid;//村id
    private String auth;
    private ProgressDialog dialog;

    private Uri cameraUri;

    private int choseType = 1;//1、选择图片2、选择视频

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_new_post);

        initView();
    }

    private void initView() {
        auth = Hawk.get(APPS.USER_AUTH);
        configXRecyclerView();
        //切换发送图片和发送视频
        radioAll.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rbPicture.getId()) {
                    choseType = 1;
                    imageList.clear();

                    mXRecyclerView.setVisibility(View.VISIBLE);
                    addVideo.setVisibility(View.GONE);
                    mPlayer.setVisibility(View.GONE);
                } else {
                    choseType = 2;
                    imageList.clear();

                    mXRecyclerView.setVisibility(View.GONE);
                    addVideo.setVisibility(View.VISIBLE);
                    mPlayer.setVisibility(View.GONE);
                }
            }
        });
        //点击发送视频
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVideo();
            }
        });
    }

    //配置RecyclerView
    private void configXRecyclerView() {
        mAdapter.setOnItemClickListener(this);
        mXRecyclerView.setAdapter(mAdapter);//设置adapter
        mXRecyclerView.setLayoutManager(mLayoutManager);//设置布局管理器

        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
    }

    /**
     * 直接调用系统相机拍照
     */
    private void loadPhoto() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "没有SD卡", Toast.LENGTH_SHORT).show();
            return;
        }
        File cameraFile = FileUtils.createCameraFile(this, 1);
        if (cameraFile != null) {
            cameraUri = Uri.fromFile(cameraFile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
        } else {
            Toast.makeText(this, "拍照失败：创建文件失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 调用图库选择或拍摄图片
     */
    private void loadPicture() {
        MyPictureSelector pictureSelector = new MyPictureSelector(this);
        pictureSelector.selectorMultiplePicture(imageList);
    }

    /**
     * 调用图库选择或拍摄视频
     */
    private void loadVideo() {
        MyPictureSelector pictureSelector = new MyPictureSelector(this);
        pictureSelector.selectorVideo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_IMAGE:
                    List<LocalMedia> mediaList = (List<LocalMedia>) data.getSerializableExtra(PictureConfig.REQUEST_OUTPUT);
                    if (mediaList != null) {
                        imageList.clear();
                        imageList.addAll(mediaList);
                        if (choseType == 1) {//返回图片显示
                            mAdapter.setItem(imageList);
                        } else {//返回视频显示
                            addVideo.setVisibility(View.GONE);
                            mPlayer.setVisibility(View.VISIBLE);

                            String url = mediaList.get(0).getPath();
                            if (!StringUtils.isEmpty(url)) {
                                Glide.with(this).load(url).thumbnail(0.5f).into(mPlayer.thumbImageView); //封面图片
                                mPlayer.setUp(url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
                            }
                        }
                    }
                    break;
                case PictureConfig.REQUEST_CAMERA:
                    // 拍照返回
                    if (cameraUri != null) {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, cameraUri));//更新图库

                        if (imageList.size() < 9) {
                            LocalMedia a = new LocalMedia();
                            a.setPath(cameraUri.getPath());
                            a.setType(1);
                            imageList.add(a);
                        } else {
                            Toast.makeText(NewPostActivity.this, "图片已达到最大选择数量", Toast.LENGTH_SHORT).show();
                        }
                        mAdapter.setItem(imageList);

                    } else {
                        Toast.makeText(this, "拍照失败，请重新拍摄。", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

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
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(NewPostActivity.this, "发布失败，请检查网络。", Toast.LENGTH_SHORT).show();
                    Log.i("mm", e.getMessage());
                }

                @Override
                public void onNext(Result result) {
                    Toast.makeText(NewPostActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            };

            if (imageList.isEmpty() || imageList == null) {
                OtherApi.getService().post_BBSPost(auth, vid, null, conts, null, null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);
                return true;
            } else {
                Observable.from(imageList)
                        .flatMap(new Func1<LocalMedia, Observable<UploadFiles>>() {
                            @Override
                            public Observable<UploadFiles> call(LocalMedia localMedia) {
//                                File file = new File(localMedia.getPath());
                                RequestBody authBody = RequestBody.create(MediaType.parse("text/plain"), auth);
                                RequestBody requestBody = null;

                                if (choseType == 1) {//图片
                                    //对图片压缩处理
                                    File file = null;
                                    try {
                                        file = new PhotoOperate().scal(localMedia.getPath());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (file != null) {
                                        requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                                    }
                                    return OtherApi.getService().post_UploadImage(authBody, requestBody);
                                } else {//视频
                                    File file = new File(localMedia.getPath());
                                    requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                                    RequestBody typeBody = RequestBody.create(MediaType.parse("text/plain"), "2");
                                    return OtherApi.getService().post_UploadVideo(authBody, typeBody, requestBody);
                                }
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
                                return OtherApi.getService().post_BBSPost(auth, vid, null, conts, null, s);
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

    @Override
    public void onItemClick(View view, final int position) {
        switch (view.getId()) {
            case R.id.img_add://打开图库，添加图片
                loadPicture();
                break;
            case R.id.post_picture:
                //点击图片查看大图
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
