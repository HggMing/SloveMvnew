package com.ming.slove.mvnew.common.utils;

import android.support.annotation.NonNull;

import com.ming.slove.mvnew.app.APPS;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientAddress;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketPacketHelper;
import com.vilyever.socketclient.helper.SocketResponsePacket;
import com.vilyever.socketclient.util.CharsetUtil;

/**
 * Created by MingN on 2017/2/13.
 */

public class SocketTools {
    private SocketClient mSocketClient;

    public SocketTools() {
        mSocketClient = getSocketClient();
    }

    public void connect(SocketClientDelegate delegate) {
        mSocketClient.registerSocketClientDelegate(delegate);
        mSocketClient.connect();
    }

    private SocketClient getSocketClient() {
        if (mSocketClient == null) {
            //远程端连接信息配置(远程端IP地址,远程端端口号,连接超时时长，单位毫秒)
            SocketClientAddress socketClientAddress = null;
            String baseUrl_Test = "http://product1.yibanke.com/";
            if (baseUrl_Test.equals(APPS.BASE_URL)) {
                socketClientAddress = new SocketClientAddress("118.178.232.77", 7272);//测试
            } else {
                socketClientAddress = new SocketClientAddress("121.40.105.149", 7272);//正式
            }

            mSocketClient = new SocketClient(socketClientAddress);

            //设置自动转换String类型到byte[]类型的编码
            mSocketClient.setCharsetName(CharsetUtil.UTF_8); // 设置编码为UTF-8

            //设置远程端发送到本地的心跳包信息内容，用于判断接收到的数据包是否是心跳包
            String heartBeat = "00001800{\"op_type\":\"jump\"}";
            mSocketClient.getHeartBeatHelper().setDefaultReceiveData(CharsetUtil.stringToData(heartBeat, CharsetUtil.UTF_8));

            /**
             * 设置读取策略为自动读取指定长度
             */
            mSocketClient.getSocketPacketHelper().setReadStrategy(SocketPacketHelper.ReadStrategy.AutoReadByLength);
            mSocketClient.getSocketPacketHelper().setReceivePacketLengthDataLength(8);
            mSocketClient.getSocketPacketHelper().setReceivePacketDataLengthConvertor(new SocketPacketHelper.ReceivePacketDataLengthConvertor() {
                @Override
                public int obtainReceivePacketDataLength(SocketPacketHelper helper, byte[] packetLengthData) {
                    /**
                     * 简单将byte[]转换为int
                     */
                    int length = Integer.parseInt(CharsetUtil.dataToString(packetLengthData, CharsetUtil.UTF_8)) / 100;

                    return length;
                }
            });
        }
        return mSocketClient;
    }
}
