package com.ming.slove.mvnew.common.base;

import android.support.v4.app.FragmentActivity;

public interface BaseView<T> {
    void setPresenter(T presenter);

    void toast(CharSequence text);

    void showDialogOk(String msg, final ClickDialog.OnClickDialogOk onClickDialog);

    void showDialog(String msg, final ClickDialog.OnClickDialog onClickDialog);

    void closeActivity();

}

