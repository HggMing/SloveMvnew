package com.ming.slove.mvnew.model.event;

import com.ming.slove.mvnew.model.database.ChatMsgModel;

/**
 * 新消息事件
 * Created by Ming on 2016/7/14.
 */
public class NewMsgEvent {
    private ChatMsgModel chatMsg;

    public NewMsgEvent(ChatMsgModel chatMsg) {
        this.chatMsg = chatMsg;
    }

    public ChatMsgModel getChatMsg() {
        return chatMsg;
    }
}
