package com.ming.slove.mvnew.tab4.mysetting.mypurse;

import android.os.Bundle;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.common.base.BackActivity;

/**
 * 普通用户显示的我的钱包（不包含本月收支）
 */
public class MyPurse2Activity extends BackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_purse2);
        setToolbarTitle(R.string.title_activity_my_purse);

    }
}
