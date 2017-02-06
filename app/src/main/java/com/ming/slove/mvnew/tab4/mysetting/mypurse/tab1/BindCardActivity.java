package com.ming.slove.mvnew.tab4.mysetting.mypurse.tab1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.api.other.OtherApi;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.ming.slove.mvnew.common.base.BaseRecyclerViewAdapter;
import com.ming.slove.mvnew.common.widgets.dialog.MyDialog;
import com.ming.slove.mvnew.model.bean.CardList;
import com.ming.slove.mvnew.model.bean.Result;
import com.orhanobut.hawk.Hawk;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BindCardActivity extends BackActivity {
    private BindCardAdapter mAdapter;
    private static final int REFRESH = 1100;
    private int type;

    private String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle(R.string.title_activity_bind_card);

        initView();
        initData();
    }

    private void initView() {
        auth = Hawk.get(APPS.USER_AUTH);
        showLoading(true);
        //设置adapter
        mAdapter = new BindCardAdapter(this);
        addRecycleView(mAdapter);
        //设置添加按钮
        showFab(mRecyclerView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BindCardActivity.this, AddCardsActivity.class);
                intent.putExtra(AddCardsActivity.TYPE, type);
                startActivityForResult(intent, REFRESH);
            }
        });
    }

    private void initData() {
        //设置数据
        OtherApi.getService()
                .get_CardList(auth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CardList>() {
                    @Override
                    public void onCompleted() {
                        showLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CardList cardList) {
                        if (cardList.getData().isEmpty() || cardList.getData() == null) {
                            type = -1;
                            showEmpty(R.string.empty_bind_card);
                        } else {
                            type = 0;
                            hideEmpty();
                        }
                        mAdapter.setItem(cardList.getData());
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REFRESH) {
            if (resultCode == RESULT_OK) {
                initData();
                setResult(RESULT_OK);
            }
        }
    }


    static class BindCardAdapter extends BaseRecyclerViewAdapter<CardList.DataBean, BindCardAdapter.ViewHolder> {

        Activity mActivity;

        public BindCardAdapter(Activity mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab4_cards, parent, false);
            return new ViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final CardList.DataBean data = mList.get(position);

            //显示开户行
            String bank_name = data.getBank_name();
            holder.bankName.setText(bank_name);
            //显示卡号后4位
            String bank_no = data.getBank_no();
            if (bank_no.length() >= 4) {
                bank_no = bank_no.substring(bank_no.length() - 4, bank_no.length());
            }
            holder.bankNumber.setText("**** **** **** " + bank_no);

            final int mSelectedItem = Hawk.get(APPS.SELECTED_CARD, 0);
            holder.chose.setChecked(position == mSelectedItem);
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Hawk.put(APPS.SELECTED_CARD, position);
                    notifyItemRangeChanged(0, mList.size());

                    mActivity.setResult(Activity.RESULT_OK);
                    mActivity.finish();
                }
            });
            holder.item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //长按解除银行卡绑定
                    MyDialog.Builder builder = new MyDialog.Builder(mActivity);
                    builder.setTitle("提示")
                            .setMessage("解除与该银行卡的绑定？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String auth = Hawk.get(APPS.USER_AUTH);
                                    OtherApi.getService()
                                            .post_DelCard(auth, data.getId())
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
                                                    mList.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyItemRangeRemoved(position, mList.size() - position);
                                                    //删除卡后，选择的卡序号改变
                                                    if (mSelectedItem > position) {
                                                        Hawk.put(APPS.SELECTED_CARD, mSelectedItem - 1);
                                                    } else if (mSelectedItem == position) {
                                                        Hawk.delete(APPS.SELECTED_CARD);
                                                        mActivity.setResult(Activity.RESULT_OK);
                                                    }
                                                }
                                            });
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    if (!mActivity.isFinishing()) {
                        builder.create().show();
                    }
                    return true;
                }
            });
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.icon)
            View icon;
            @Bind(R.id.bank_name)
            TextView bankName;
            @Bind(R.id.bank_number)
            TextView bankNumber;
            @Bind(R.id.item)
            CardView item;
            @Bind(R.id.chose)
            RadioButton chose;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

}


