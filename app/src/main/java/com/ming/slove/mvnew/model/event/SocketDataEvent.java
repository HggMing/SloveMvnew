package com.ming.slove.mvnew.model.event;

import com.ming.slove.mvnew.model.bean.SocketData;
import com.vilyever.socketclient.SocketClient;

/**
 * 传递socket的数据 到播放页面的fragment
 * Created by MingN on 2017/2/14.
 */

public class SocketDataEvent {
    private SocketData socketData;
    private SocketClient mSocketclient;

    public SocketDataEvent(SocketData socketData,SocketClient mSocketclient) {
        this.socketData = socketData;
        this.mSocketclient=mSocketclient;
    }

    public SocketData getSocketData() {
        return socketData;
    }

    public void setSocketData(SocketData socketData) {
        this.socketData = socketData;
    }

    public SocketClient getmSocketclient() {
        return mSocketclient;
    }

    public void setmSocketclient(SocketClient mSocketclient) {
        this.mSocketclient = mSocketclient;
    }
}
