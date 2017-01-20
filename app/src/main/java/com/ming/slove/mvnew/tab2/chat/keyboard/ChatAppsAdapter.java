package com.ming.slove.mvnew.tab2.chat.keyboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.model.bean.ChatAppBean;
import com.ming.slove.mvnew.model.event.SendImageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class ChatAppsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<ChatAppBean> mDdata = new ArrayList<>();

    public ChatAppsAdapter(Context context, ArrayList<ChatAppBean> data) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        if (data != null) {
            this.mDdata = data;
        }
    }

    @Override
    public int getCount() {
        return mDdata.size();
    }

    @Override
    public Object getItem(int position) {
        return mDdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_keyboard_app, null);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ChatAppBean appBean = mDdata.get(position);
        if (appBean != null) {
            viewHolder.iv_icon.setBackgroundResource(appBean.getIcon());
            viewHolder.tv_name.setText(appBean.getFuncName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(mContext,appBean.getFuncName(), Toast.LENGTH_SHORT).show();
                    switch (position) {
                        case 0://图片
                            EventBus.getDefault().post(new SendImageEvent("1"));//将点击事件传到Activity'
                            break;
                        case 1://拍照
                            EventBus.getDefault().post(new SendImageEvent("2"));//将点击事件传到Activity'
                            break;
                    }
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView iv_icon;
        public TextView tv_name;
    }
}