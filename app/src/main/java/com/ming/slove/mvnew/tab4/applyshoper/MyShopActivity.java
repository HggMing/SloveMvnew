package com.ming.slove.mvnew.tab4.applyshoper;

import android.os.Bundle;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.common.base.BackActivity;

public class MyShopActivity extends BackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_shop);
        setToolbarTitle(R.string.title_activity_my_shop);
    }
}
