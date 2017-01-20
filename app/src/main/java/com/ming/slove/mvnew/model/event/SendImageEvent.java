package com.ming.slove.mvnew.model.event;

/**
 * 发送图片消息事件(将点击图片时间，传递到Activity）
 * Created by Ming on 2016/7/27.
 */
public class SendImageEvent {
    private String type;//1、图片选择2、拍照

    public SendImageEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
