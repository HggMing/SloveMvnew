package com.ming.slove.mvnew.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 获取用户信息 返回
 * Created by Ming on 2016/3/15.
 */
public class UserInfo {

    /**
     * err : 0
     * msg :
     * data : {"uid":"12018","phone":"18140006179","logname":"18140006179","sex":"0","uname":"一拳","birth":"","ctime":"1456132445","cid":"","ctype":"0","dev":"0","loc":"","lastlog":"1458011327","lastip":"222.211.215.186","lastdev":"-1","logcnt":"369","head":"/Public/head/11018/2V7CQKTWMEPD1456337347.jpg","province_id":"","province_name":"","city_id":"","city_name":"","county_id":"","county_name":"","town_id":"","town_name":"","village_id":"","village_name":""}
     */

    private int err;
    private String msg;
    /**
     * uid : 12018
     * phone : 18140006179
     * logname : 18140006179
     * sex : 0
     * uname : 一拳
     * birth :
     * ctime : 1456132445
     * cid :
     * ctype : 0
     * dev : 0
     * loc :
     * lastlog : 1458011327
     * lastip : 222.211.215.186
     * lastdev : -1
     * logcnt : 369
     * head : /Public/head/11018/2V7CQKTWMEPD1456337347.jpg
     * province_id :
     * province_name :
     * city_id :
     * city_name :
     * county_id :
     * county_name :
     * town_id :
     * town_name :
     * village_id :
     * village_name :
     */

    private DataEntity data;

    public void setErr(int err) {
        this.err = err;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public int getErr() {
        return err;
    }

    public String getMsg() {
        return msg;
    }

    public DataEntity getData() {
        return data;
    }

    public static class DataEntity implements Parcelable {
        private String uid;
        private String phone;
        private String logname;
        private String sex;
        private String uname;
        private String birth;
        private String ctime;
        private String cid;
        private String ctype;
        private String dev;
        private String loc;
        private String lastlog;
        private String lastip;
        private String lastdev;
        private String logcnt;
        private String head;
        private String province_id;
        private String province_name;
        private String city_id;
        private String city_name;
        private String county_id;
        private String county_name;
        private String town_id;
        private String town_name;
        private String village_id;
        private String village_name;

        public void setUid(String uid) {
            this.uid = uid;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setLogname(String logname) {
            this.logname = logname;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public void setBirth(String birth) {
            this.birth = birth;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public void setCtype(String ctype) {
            this.ctype = ctype;
        }

        public void setDev(String dev) {
            this.dev = dev;
        }

        public void setLoc(String loc) {
            this.loc = loc;
        }

        public void setLastlog(String lastlog) {
            this.lastlog = lastlog;
        }

        public void setLastip(String lastip) {
            this.lastip = lastip;
        }

        public void setLastdev(String lastdev) {
            this.lastdev = lastdev;
        }

        public void setLogcnt(String logcnt) {
            this.logcnt = logcnt;
        }

        public void setHead(String head) {
            this.head = head;
        }

        public void setProvince_id(String province_id) {
            this.province_id = province_id;
        }

        public void setProvince_name(String province_name) {
            this.province_name = province_name;
        }

        public void setCity_id(String city_id) {
            this.city_id = city_id;
        }

        public void setCity_name(String city_name) {
            this.city_name = city_name;
        }

        public void setCounty_id(String county_id) {
            this.county_id = county_id;
        }

        public void setCounty_name(String county_name) {
            this.county_name = county_name;
        }

        public void setTown_id(String town_id) {
            this.town_id = town_id;
        }

        public void setTown_name(String town_name) {
            this.town_name = town_name;
        }

        public void setVillage_id(String village_id) {
            this.village_id = village_id;
        }

        public void setVillage_name(String village_name) {
            this.village_name = village_name;
        }

        public String getUid() {
            return uid;
        }

        public String getPhone() {
            return phone;
        }

        public String getLogname() {
            return logname;
        }

        public String getSex() {
            return sex;
        }

        public String getUname() {
            return uname;
        }

        public String getBirth() {
            return birth;
        }

        public String getCtime() {
            return ctime;
        }

        public String getCid() {
            return cid;
        }

        public String getCtype() {
            return ctype;
        }

        public String getDev() {
            return dev;
        }

        public String getLoc() {
            return loc;
        }

        public String getLastlog() {
            return lastlog;
        }

        public String getLastip() {
            return lastip;
        }

        public String getLastdev() {
            return lastdev;
        }

        public String getLogcnt() {
            return logcnt;
        }

        public String getHead() {
            return head;
        }

        public String getProvince_id() {
            return province_id;
        }

        public String getProvince_name() {
            return province_name;
        }

        public String getCity_id() {
            return city_id;
        }

        public String getCity_name() {
            return city_name;
        }

        public String getCounty_id() {
            return county_id;
        }

        public String getCounty_name() {
            return county_name;
        }

        public String getTown_id() {
            return town_id;
        }

        public String getTown_name() {
            return town_name;
        }

        public String getVillage_id() {
            return village_id;
        }

        public String getVillage_name() {
            return village_name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.uid);
            dest.writeString(this.phone);
            dest.writeString(this.logname);
            dest.writeString(this.sex);
            dest.writeString(this.uname);
            dest.writeString(this.birth);
            dest.writeString(this.ctime);
            dest.writeString(this.cid);
            dest.writeString(this.ctype);
            dest.writeString(this.dev);
            dest.writeString(this.loc);
            dest.writeString(this.lastlog);
            dest.writeString(this.lastip);
            dest.writeString(this.lastdev);
            dest.writeString(this.logcnt);
            dest.writeString(this.head);
            dest.writeString(this.province_id);
            dest.writeString(this.province_name);
            dest.writeString(this.city_id);
            dest.writeString(this.city_name);
            dest.writeString(this.county_id);
            dest.writeString(this.county_name);
            dest.writeString(this.town_id);
            dest.writeString(this.town_name);
            dest.writeString(this.village_id);
            dest.writeString(this.village_name);
        }

        public DataEntity() {
        }

        protected DataEntity(Parcel in) {
            this.uid = in.readString();
            this.phone = in.readString();
            this.logname = in.readString();
            this.sex = in.readString();
            this.uname = in.readString();
            this.birth = in.readString();
            this.ctime = in.readString();
            this.cid = in.readString();
            this.ctype = in.readString();
            this.dev = in.readString();
            this.loc = in.readString();
            this.lastlog = in.readString();
            this.lastip = in.readString();
            this.lastdev = in.readString();
            this.logcnt = in.readString();
            this.head = in.readString();
            this.province_id = in.readString();
            this.province_name = in.readString();
            this.city_id = in.readString();
            this.city_name = in.readString();
            this.county_id = in.readString();
            this.county_name = in.readString();
            this.town_id = in.readString();
            this.town_name = in.readString();
            this.village_id = in.readString();
            this.village_name = in.readString();
        }

        public static final Parcelable.Creator<DataEntity> CREATOR = new Parcelable.Creator<DataEntity>() {
            @Override
            public DataEntity createFromParcel(Parcel source) {
                return new DataEntity(source);
            }

            @Override
            public DataEntity[] newArray(int size) {
                return new DataEntity[size];
            }
        };
    }
}
