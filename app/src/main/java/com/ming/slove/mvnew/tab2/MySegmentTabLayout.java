package com.ming.slove.mvnew.tab2;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.utils.FragmentChangeManager;

import java.util.ArrayList;

/**
 * Created by MingN on 2017/3/27.
 */

public class MySegmentTabLayout extends SegmentTabLayout {

    private FragmentChangeManager mFragmentChangeManager;

    public MySegmentTabLayout(Context context) {
        super(context);
    }

    public MySegmentTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySegmentTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTabData(String[] titles, FragmentActivity fa, int containerViewId, ArrayList<Fragment> fragments) {
        super.setTabData(titles, fa, containerViewId, fragments);
    }

    /** 关联数据支持同时切换fragments */
    public void setTabData(String[] titles, Fragment fragment, int containerViewId, ArrayList<Fragment> fragments) {
        mFragmentChangeManager = new FragmentChangeManager(fragment.getChildFragmentManager(), containerViewId, fragments);
        setTabData(titles);
    }

    @Override
    public void setCurrentTab(int currentTab) {
        super.setCurrentTab(currentTab);
        if (mFragmentChangeManager != null) {
            mFragmentChangeManager.setFragments(currentTab);
        }
    }
}
