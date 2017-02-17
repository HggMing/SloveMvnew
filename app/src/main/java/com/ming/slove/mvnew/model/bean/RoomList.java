package com.ming.slove.mvnew.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by MingN on 2017/1/22.
 */

public class RoomList {
    /**
     * err : 0
     * data : {"cnt":"12","list":[{"room_id":"10418","title":"我在直播，快来看哦","name":"张锋","account":"15682558303","num":"6","url":"rtmp://pili-live-rtmp.live.isall.com.cn/ourvillage-live/livestrram1485074731","url_1":"rtmp://pili-publish.live.isall.com.cn/ourvillage-live/livestrram1485074731","pic":"http://pili-live-snapshot.live.isall.com.cn/ourvillage-live/livestrram1485074731.jpg","url_flv":"http://pili-live-hdl.live.isall.com.cn/ourvillage-live/livestrram1485074731.flv","pic_1":"/Public/bbs/file/11015/2017-01-22/20170122s20170122985797979951.jpeg"}]}
     */

    private int err;
    private DataBean data;

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * cnt : 12
         * list : [{"room_id":"10418","title":"我在直播，快来看哦","name":"张锋","account":"15682558303","num":"6","url":"rtmp://pili-live-rtmp.live.isall.com.cn/ourvillage-live/livestrram1485074731","url_1":"rtmp://pili-publish.live.isall.com.cn/ourvillage-live/livestrram1485074731","pic":"http://pili-live-snapshot.live.isall.com.cn/ourvillage-live/livestrram1485074731.jpg","url_flv":"http://pili-live-hdl.live.isall.com.cn/ourvillage-live/livestrram1485074731.flv","pic_1":"/Public/bbs/file/11015/2017-01-22/20170122s20170122985797979951.jpeg"}]
         */

        private String cnt;
        private List<ListBean> list;

        public String getCnt() {
            return cnt;
        }

        public void setCnt(String cnt) {
            this.cnt = cnt;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean implements Parcelable {
            /**
             * room_id : 10418
             * title : 我在直播，快来看哦
             * name : 张锋
             * account : 15682558303
             * num : 6
             * url : rtmp://pili-live-rtmp.live.isall.com.cn/ourvillage-live/livestrram1485074731
             * url_1 : rtmp://pili-publish.live.isall.com.cn/ourvillage-live/livestrram1485074731
             * pic : http://pili-live-snapshot.live.isall.com.cn/ourvillage-live/livestrram1485074731.jpg
             * url_flv : http://pili-live-hdl.live.isall.com.cn/ourvillage-live/livestrram1485074731.flv
             * pic_1 : /Public/bbs/file/11015/2017-01-22/20170122s20170122985797979951.jpeg
             * zan_num": "49",
             *"head": "http://product.yibanke.com/Public/head/11015/KJ6AJETWVGRP1456121024.png"
             */

            private String room_id;
            private String title;
            private String name;
            private String account;
            private String num;
            private String url;
            private String url_1;
            private String pic;
            private String url_flv;
            private String pic_1;
            private String zan_num;
            private String head;

            public String getRoom_id() {
                return room_id;
            }

            public void setRoom_id(String room_id) {
                this.room_id = room_id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAccount() {
                return account;
            }

            public void setAccount(String account) {
                this.account = account;
            }

            public String getNum() {
                return num;
            }

            public void setNum(String num) {
                this.num = num;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getUrl_1() {
                return url_1;
            }

            public void setUrl_1(String url_1) {
                this.url_1 = url_1;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getUrl_flv() {
                return url_flv;
            }

            public void setUrl_flv(String url_flv) {
                this.url_flv = url_flv;
            }

            public String getPic_1() {
                return pic_1;
            }

            public void setPic_1(String pic_1) {
                this.pic_1 = pic_1;
            }

            public String getHead() {
                return head;
            }

            public void setHead(String head) {
                this.head = head;
            }

            public String getZan_num() {
                return zan_num;
            }

            public void setZan_num(String zan_num) {
                this.zan_num = zan_num;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.room_id);
                dest.writeString(this.title);
                dest.writeString(this.name);
                dest.writeString(this.account);
                dest.writeString(this.num);
                dest.writeString(this.url);
                dest.writeString(this.url_1);
                dest.writeString(this.pic);
                dest.writeString(this.url_flv);
                dest.writeString(this.pic_1);
                dest.writeString(this.zan_num);
                dest.writeString(this.head);
            }

            public ListBean() {
            }

            protected ListBean(Parcel in) {
                this.room_id = in.readString();
                this.title = in.readString();
                this.name = in.readString();
                this.account = in.readString();
                this.num = in.readString();
                this.url = in.readString();
                this.url_1 = in.readString();
                this.pic = in.readString();
                this.url_flv = in.readString();
                this.pic_1 = in.readString();
                this.zan_num = in.readString();
                this.head = in.readString();
            }

            public static final Parcelable.Creator<ListBean> CREATOR = new Parcelable.Creator<ListBean>() {
                @Override
                public ListBean createFromParcel(Parcel source) {
                    return new ListBean(source);
                }

                @Override
                public ListBean[] newArray(int size) {
                    return new ListBean[size];
                }
            };
        }
    }
}
