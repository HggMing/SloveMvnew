package com.ming.slove.mvnew.common.base;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;
import com.bilibili.magicasakura.utils.ThemeUtils;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;

/**
 * Created by Ming on 2016/11/29.
 */

public class BaseFragment extends Fragment {

    public void showDialogOk(String msg, final ClickDialog.OnClickDialogOk onClickDialog) {
        MyDialog.Builder builder = new MyDialog.Builder(getContext());
        builder.setTitle("提示")
                .setCannel(false)
                .setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onClickDialog != null) {
                            onClickDialog.dialogOk();
                        }
                        dialog.dismiss();
                    }
                });
        if (!getActivity().isFinishing()) {
            builder.create().show();
        }
    }

    public void showDialog(String msg, final ClickDialog.OnClickDialog onClickDialog) {
        MyDialog.Builder builder = new MyDialog.Builder(getContext());
        builder.setTitle("提示")
                .setCannel(false)
                .setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onClickDialog != null) {
                            onClickDialog.dialogOk();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onClickDialog != null) {
                            onClickDialog.dialogCannel();
                        }
                        dialog.dismiss();
                    }
                });
        if (!getActivity().isFinishing()) {
            builder.create().show();
        }
    }

    public void toast(CharSequence text) {
        TSnackbar snackbar = TSnackbar
                .make(getView(), text, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setIconLeft(R.drawable.aic_toast, 18);
        snackbar.setIconPadding(16);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ThemeUtils.getColorById(getContext(), R.color.theme_color_primary_trans));//背景颜色
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void toast2(CharSequence text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void closeActivity() {
        getActivity().finish();
    }
}
