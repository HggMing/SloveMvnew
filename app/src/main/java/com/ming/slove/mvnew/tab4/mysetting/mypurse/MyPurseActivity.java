package com.ming.slove.mvnew.tab4.mysetting.mypurse;

import android.os.Bundle;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.common.base.TabsActivity;

/**
 * 店长显示的我的钱包（包含本月收支）
 */
public class MyPurseActivity extends TabsActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle(R.string.title_activity_my_purse);

        // 初始化mTitles、mFragments等ViewPager需要的数据
        initData();
    }

    private void initData() {
        // Tab的标题
        mTitles = new String[]{"余额明细", "本月收入"};

        //初始化填充到ViewPager中的Fragment集合
        mFragments.add(0, new MyPurseTab1Fragment());
        mFragments.add(1, new MyPurseTab2Fragment());

        mAdapter.setItem(mTitles, mFragments);
    }
}


