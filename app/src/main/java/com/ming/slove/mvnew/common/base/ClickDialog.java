package com.ming.slove.mvnew.common.base;

/**
 * Created by Ming on 2016/12/9.
 */

public class ClickDialog {
    public interface OnClickDialog {
        void dialogOk();

        void dialogCannel();
    }

    public interface OnClickDialogOk {
        void dialogOk();
    }
}
