package com.ming.slove.mvnew.tab2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.bilibili.magicasakura.widgets.TintToolbar;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.model.database.MyDB;
import com.ming.slove.mvnew.model.database.NewFriendModel;
import com.ming.slove.mvnew.model.event.ChangeThemeColorEvent;
import com.ming.slove.mvnew.model.event.NewFriendEvent;
import com.ming.slove.mvnew.tab2.friendlist.FriendListFragment;
import com.ming.slove.mvnew.tab2.message.MessageFragment;
import com.ming.slove.mvnew.ui.main.MyViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MingN on 2017/3/27.
 */

public class IMFragment extends Fragment {
    @Bind(R.id.tab_layout)
    MySegmentTabLayout tabLayout;
    @Bind(R.id.toolbar)
    TintToolbar mToolBar;
    @Bind(R.id.view_pager)
    MyViewPager viewPager;
    @Bind(R.id.contentFrame)
    FrameLayout contentFrame;

    String[] mTitles = {"消息", "老乡"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_im, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolBar.setTitle("");

        int themeColor = ThemeUtils.getColorById(getContext().getApplicationContext(), R.color.theme_color_primary);
        tabLayout.setBackgroundColor(themeColor);
        tabLayout.setTextSelectColor(themeColor);

        mFragments.add(new MessageFragment());
        mFragments.add(new FriendListFragment());
//        configTab();
        tabLayout.setTabData(mTitles, this, R.id.contentFrame, mFragments);
    }

    //更换主题后手动刷新上方tab的颜色
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeThemeColor(ChangeThemeColorEvent event) {
        int themeColor = ThemeUtils.getColorById(getContext().getApplicationContext(), R.color.theme_color_primary);
        tabLayout.setBackgroundColor(themeColor);
        tabLayout.setTextSelectColor(themeColor);
    }

    /**
     * 接收到新的朋友请求消息，更新顶部tab“老乡”处消息徽章计数
     *
     * @param event 3
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showCount2(NewFriendEvent event) {
        List<NewFriendModel> nFriends = MyDB.getQueryAll(NewFriendModel.class);
        int count = 0;
        for (NewFriendModel nFriend : nFriends) {
            count += nFriend.getCount();
        }
        if (count > 0) {
            tabLayout.showMsg(1, count);
        } else {
            tabLayout.hideMsg(1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    private void configTab() {
        viewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        tabLayout.setTabData(mTitles);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        viewPager.setSlipping(false);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
