package com.ming.slove.mvnew.model.event;

/**
 * 刷新tab1上的新消息计数
 * Created by Ming on 2016/8/19.
 */
public class RefreshTab2Event {
    public int getCount() {
        return count;
    }

    int count;

    public RefreshTab2Event(int count) {
        this.count = count;
    }
}
