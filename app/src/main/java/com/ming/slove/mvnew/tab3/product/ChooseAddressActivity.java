package com.ming.slove.mvnew.tab3.product;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.model.bean.Result;
import com.ming.slove.mvnew.model.bean.ShoppingAddress;
import com.ming.slove.mvnew.tab4.mysetting.shoppingaddress.EditShoppingAdressActivity;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChooseAddressActivity extends BackActivity {
    private ChooseAddressAdapter mAdapter;
    List<ShoppingAddress.DataBean> mList = new ArrayList<>();
    private String auth;

    private final int REFRESH = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle(R.string.title_activity_shopping_address);

        initView();
        initData();
    }

    private void initView() {
        auth = Hawk.get(APPS.USER_AUTH);
        showLoading(true);

        //设置adapter
        mAdapter = new ChooseAddressAdapter(this);
        addRecycleView(mAdapter);

        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //修改设为默认地址
                ShoppingAddress.DataBean data = mList.get(position);
                String sd_is_def = data.getIs_def();
                if ("1".equals(sd_is_def)) {
                    sd_is_def = "0";
                } else {
                    sd_is_def = "1";
                }

                OtherApi.getService()
                        .post_EditShoppingAddress(auth, data.getId(), data.getUname(), data.getAddr(), data.getTel(), sd_is_def, data.getZipcode())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Result>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Result result) {
                                initData();
                            }
                        });
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        //设置添加按钮
        showFab(mRecyclerView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        Toast.makeText(ShoppingAddressActivity.this, "添加收货地址", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChooseAddressActivity.this, EditShoppingAdressActivity.class);
                startActivityForResult(intent, REFRESH);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REFRESH) {
            if (resultCode == RESULT_OK) {
                initData();
            }
        }
    }

    private void initData() {
        //设置数据
        OtherApi.getService()
                .get_ShoppingAddress(auth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShoppingAddress>() {
                    @Override
                    public void onCompleted() {
                        showLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ShoppingAddress shoppingAddress) {
                        mList.clear();
                        mList.addAll(shoppingAddress.getData());
                        if (mList.isEmpty()) {
                            showEmpty(R.string.empty_choose_address);
                        } else {
                            hideEmpty();
                        }
                        mAdapter.setItem(mList);
                    }
                });
    }

    static class ChooseAddressAdapter extends BaseRecyclerViewAdapter<ShoppingAddress.DataBean, ChooseAddressAdapter.ViewHolder> {

        Activity mActivity;

        public ChooseAddressAdapter(Activity mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab4_shopping_address, parent, false);
            return new ViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final ShoppingAddress.DataBean data = mList.get(position);

            //显示收货人姓名
            String uname = data.getUname();
            holder.name.setText(uname);
            //显示手机号
            String phone = data.getTel();
            if (phone.length() == 11) {
                phone = phone.substring(0, 3) + "****" + phone.substring(7, 11);
                holder.phone.setText(phone);
                holder.phone.setTextColor(mActivity.getResources().getColor(R.color.font_black_4));
            } else {
                holder.phone.setText("手机号有误，请修改");
                holder.phone.setTextColor(mActivity.getResources().getColor(R.color.color08));
            }
            //显示收货地址
            String address = data.getAddr();
            String zipCode = data.getZipcode();
            holder.address.setText(address + "    " + zipCode);
            //显示是否设为默认
            String isDef = data.getIs_def();
            if ("1".equals(isDef)) {
                holder.isDefault.setChecked(true);
                holder.isDefault.setText("默认地址");
            } else {
                holder.isDefault.setChecked(false);
                holder.isDefault.setText("设为默认");
            }

            holder.isDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.isDefault, position);
                }
            });

            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                Toast.makeText(mActivity, "确认选择", Toast.LENGTH_SHORT).show();
                    ShoppingAddress.DataBean data2 = new ShoppingAddress.DataBean(data.getUname(), data.getTel(), data.getAddr(), data.getZipcode());
                    Intent intent = new Intent();
                    intent.putExtra(ProductPayActivity.KEY_USER_ADDR_INFO, data2);
                    mActivity.setResult(RESULT_OK, intent);
                    mActivity.finish();
                }
            });

        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.name)
            TextView name;
            @Bind(R.id.phone)
            TextView phone;
            @Bind(R.id.address)
            TextView address;
            @Bind(R.id.item)
            CardView item;
            @Bind(R.id.is_default)
            CheckBox isDefault;
            @Bind(R.id.arrow)
            View arrow;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);

                arrow.setVisibility(View.GONE);
            }
        }
    }
}

