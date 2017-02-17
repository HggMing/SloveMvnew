package com.ming.slove.mvnew.ui.facelogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintButton;
import com.bilibili.magicasakura.widgets.TintEditText;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BaseFragment;
import com.ming.slove.mvnew.common.utils.ImageUtils;
import com.ming.slove.mvnew.common.widgets.customcamera.TakePhotoActivity;
import com.ming.slove.mvnew.ui.main.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.ming.slove.mvnew.common.utils.BaseTools.checkNotNull;

/**
 * View
 */
public class FaceLoginFragment extends BaseFragment implements FaceLoginContract.View {
    @Bind(R.id.et_phone)
    TintEditText etPhone;
    @Bind(R.id.tv_firstnotice)
    TextView tvFirstnotice;
    @Bind(R.id.btn_face)
    ImageView btnFace;
    @Bind(R.id.img_face)
    ImageView imgFace;
    @Bind(R.id.takelayout_face)
    RelativeLayout takelayoutFace;
    @Bind(R.id.img_faceretake)
    ImageView imgFaceretake;
    @Bind(R.id.layout_face)
    RelativeLayout layoutFace;
    @Bind(R.id.btn_ok)
    TintButton btnOk;

    private FaceLoginContract.Presenter mPresenter;
    private final int REQUEST_PHOTO = 11800;
    private String photoPath;

    public static FaceLoginFragment newInstance() {
        return new FaceLoginFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(FaceLoginContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facelogin, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void initView(View view) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.btn_face, R.id.img_faceretake, R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_face://拍正面免冠照（先请求拍照权限）
                takePhoto();
                break;
            case R.id.img_faceretake://重拍正面免冠照
                takePhoto();
                break;
            case R.id.btn_ok:
                String phone = etPhone.getText().toString().trim();
                mPresenter.userLogin(phone, photoPath);
                break;
        }
    }

    private void takePhoto() {
        //使用自定义相机拍照方案
        Intent intent = new Intent(getContext(), TakePhotoActivity.class);
        intent.putExtra(TakePhotoActivity.TYPE, TakePhotoActivity.FACE);
        startActivityForResult(intent, REQUEST_PHOTO);
    }

    @Override
    public void setBtnOk(boolean isCanClick, String s) {
        btnOk.setClickable(isCanClick);
        btnOk.setText(s);
    }

    @Override
    public void loginSuccess() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHOTO) {
            if (resultCode == RESULT_OK) {
                btnFace.setVisibility(View.GONE);
                imgFaceretake.setVisibility(View.VISIBLE);

                photoPath = APPS.FILE_PATH_CAMERACACHE + TakePhotoActivity.FACE + ".jpg";
                ImageUtils.showPictureNoChache(this, photoPath, imgFace);
                mPresenter.setHasFacePhoto(true);
            }
        }
    }
}
