package com.ming.slove.mvnew.model.bean;

/**
 * Created by MingN on 2017/1/22.
 */

public class NewRoomInfo {

    /**
     * data : {"title":"123","name":"打击盗版","account":"19910001001","pic":"http://pili-live-snapshot.live.isall.com.cn/ourvillage-live/livestrram1485076917.jpg","num":0,"ctime":1485076917,"url":"rtmp://pili-live-rtmp.live.isall.com.cn/ourvillage-live/livestrram1485076917","url_1":"rtmp://pili-publish.live.isall.com.cn/ourvillage-live/livestrram1485076917","k":"","url_flv":"http://pili-live-hdl.live.isall.com.cn/ourvillage-live/livestrram1485076917.flv","pic_1":"/Public/bbs/file/21364/2017-01-22/20170122s20170122535453100559.files","room_id":"10419"}
     * err : 0
     * msg : 操作成功
     */

    private DataBean data;
    private int err;
    private String msg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * title : 123
         * name : 打击盗版
         * account : 19910001001
         * pic : http://pili-live-snapshot.live.isall.com.cn/ourvillage-live/livestrram1485076917.jpg
         * num : 0
         * ctime : 1485076917
         * url : rtmp://pili-live-rtmp.live.isall.com.cn/ourvillage-live/livestrram1485076917
         * url_1 : rtmp://pili-publish.live.isall.com.cn/ourvillage-live/livestrram1485076917
         * k :
         * url_flv : http://pili-live-hdl.live.isall.com.cn/ourvillage-live/livestrram1485076917.flv
         * pic_1 : /Public/bbs/file/21364/2017-01-22/20170122s20170122535453100559.files
         * url_m3u8:http://pili-live-hdl.live.isall.com.cn/ourvillage-live/livestrram1490241160.m3u8
         * room_id : 10419
         */

        private String title;
        private String name;
        private String account;
        private String pic;
        private int num;
        private int ctime;
        private String url;
        private String url_1;
        private String k;
        private String url_flv;
        private String pic_1;
        private String url_m3u8;
        private String room_id;

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

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public int getCtime() {
            return ctime;
        }

        public void setCtime(int ctime) {
            this.ctime = ctime;
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

        public String getK() {
            return k;
        }

        public void setK(String k) {
            this.k = k;
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

        public String getUrl_m3u8() {
            return url_m3u8;
        }

        public void setUrl_m3u8(String url_m3u8) {
            this.url_m3u8 = url_m3u8;
        }

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }
    }
}
