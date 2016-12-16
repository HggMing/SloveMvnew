package com.ming.slove.mvnew.shop.shoptab4;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.utils.StringUtils;
import com.ming.slove.mvnew.model.bean.MyVillUsers;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * 本村用户档案管理
 */
public class VillagerInfoActivity extends BackActivity {
    @Bind(R.id.icon_head)
    ImageView iconHead;
    @Bind(R.id.name)
    TextView name;

    public static String VILLAGE_USER_INFO = "village_user_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_villager_info);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_villager_info);

        initData();
    }

    private void initData() {
        MyVillUsers.DataBean.ListBean data = getIntent().getParcelableExtra(VILLAGE_USER_INFO);
        //用户头像
        String headUrl = APPS.BASE_URL + data.getHead();
        Glide.with(this)
                .load(headUrl)
                .bitmapTransform(new CropCircleTransformation(this))
                .error(R.mipmap.defalt_user_circle)
                .into(iconHead);
        //用户昵称显示
        String showName = data.getUname();
        if (StringUtils.isEmpty(showName)) {
            String phone = data.getPhone();
            showName = phone.substring(0, 3) + "****" + phone.substring(7, 11);
        }
        name.setText(showName);
        //用户性别
        String sexNumber = data.getSex();
        if ("0".equals(sexNumber)) {
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_sex_boy, 0);
        } else {
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_sex_girl, 0);
        }
    }

    @OnClick({R.id.click_cj, R.id.click_jk, R.id.click_xy, R.id.click_xt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.click_cj:
                break;
            case R.id.click_jk:
                break;
            case R.id.click_xy:
                break;
            case R.id.click_xt:
                break;
        }
    }
}
