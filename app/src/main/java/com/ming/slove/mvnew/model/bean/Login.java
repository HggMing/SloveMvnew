package com.ming.slove.mvnew.model.bean;

import java.util.Map;

/**
 * 登录信息 返回
 * Created by Ming on 2016/3/11.
 */
public class Login {

    /**
     * err : 0
     * msg : 验证通过
     * info : {"uid":"12018","phone":"18140006179","logname":"18140006179","sex":"0","uname":"一拳","birth":"0","ctime":"1456132445","cid":"","loc":"","lastlog":"1457651689","lastip":"171.213.53.193","lastdev":"0"}
     * auth : tVU2yacQlgHvuSCohxm0zSbWpKyj1rK4We3N8KNvoGsqKmYH0YzOOg==
     */

    private int err;
    private String msg;
    private InfoEntity info;
    private ShopownerBean shopowner;
    private Map<String,VidInfoBean> vid_info;
    private String auth;
    private int is_show_yingshan;


    public void setErr(int err) {
        this.err = err;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setInfo(InfoEntity info) {
        this.info = info;
    }

    public void setShopowner(ShopownerBean shopowner) {
        this.shopowner = shopowner;
    }

    public void setVid_info(Map<String, VidInfoBean> vid_info) {
        this.vid_info = vid_info;
    }

    public Map<String, VidInfoBean> getVid_info() {
        return vid_info;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public void setIs_show_yingshan(int is_show_yingshan) {
        this.is_show_yingshan = is_show_yingshan;
    }

    public int getErr() {
        return err;
    }

    public String getMsg() {
        return msg;
    }

    public InfoEntity getInfo() {
        return info;
    }

    public ShopownerBean getShopowner() {
        return shopowner;
    }

    public String getAuth() {
        return auth;
    }

    public int getIs_show_yingshan() {
        return is_show_yingshan;
    }

    public static class InfoEntity {
        /**
         * uid : 12018
         * phone : 18140006179
         * logname : 18140006179
         * sex : 0
         * uname : 一拳
         * birth : 0
         * ctime : 1456132445
         * cid :
         * loc :
         * lastlog : 1457651689
         * lastip : 171.213.53.193
         * lastdev : 0
         */
        private String uid;
        private String phone;
        private String logname;
        private String sex;
        private String uname;
        private String birth;
        private String ctime;
        private String cid;
        private String loc;
        private String lastlog;
        private String lastip;
        private String lastdev;
        private String head;
        private String rank;
        private String user_star;

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

        public void setHead(String head) {
            this.head = head;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public void setUser_star(String user_star) {
            this.user_star = user_star;
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

        public String getHead() {
            return head;
        }

        public String getUser_star() {
            return user_star;
        }

        public String getRank() {
            return rank;
        }
    }

    public static class ShopownerBean {
        /**
         * is_shopowner : 1
         * manager_vid : 510922103201
         */
        private int is_shopowner;
        private String manager_vid;

        public int getIs_shopowner() {
            return is_shopowner;
        }

        public void setIs_shopowner(int is_shopowner) {
            this.is_shopowner = is_shopowner;
        }

        public String getManager_vid() {
            return manager_vid;
        }

        public void setManager_vid(String manager_vid) {
            this.manager_vid = manager_vid;
        }
    }

    public static class VidInfoBean {
        /**
         * id : 244602
         * province_id : 350
         * province_name : 福建省
         * city_id : 350800000000
         * city_name : 龙岩市
         * county_id : 350881000000
         * county_name : 漳平市
         * town_id : 350881100000
         * town_name : 新桥镇
         * village_id : 350881100203
         * village_name : 西埔村
         * bid : 0
         * pwd :
         *office_id :  6
         *groupid :  0
         */

        private String id;
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
        private String bid;
        private String pwd;
        private String office_id;
        private String groupid;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProvince_id() {
            return province_id;
        }

        public void setProvince_id(String province_id) {
            this.province_id = province_id;
        }

        public String getProvince_name() {
            return province_name;
        }

        public void setProvince_name(String province_name) {
            this.province_name = province_name;
        }

        public String getCity_id() {
            return city_id;
        }

        public void setCity_id(String city_id) {
            this.city_id = city_id;
        }

        public String getCity_name() {
            return city_name;
        }

        public void setCity_name(String city_name) {
            this.city_name = city_name;
        }

        public String getCounty_id() {
            return county_id;
        }

        public void setCounty_id(String county_id) {
            this.county_id = county_id;
        }

        public String getCounty_name() {
            return county_name;
        }

        public void setCounty_name(String county_name) {
            this.county_name = county_name;
        }

        public String getTown_id() {
            return town_id;
        }

        public void setTown_id(String town_id) {
            this.town_id = town_id;
        }

        public String getTown_name() {
            return town_name;
        }

        public void setTown_name(String town_name) {
            this.town_name = town_name;
        }

        public String getVillage_id() {
            return village_id;
        }

        public void setVillage_id(String village_id) {
            this.village_id = village_id;
        }

        public String getVillage_name() {
            return village_name;
        }

        public void setVillage_name(String village_name) {
            this.village_name = village_name;
        }

        public String getBid() {
            return bid;
        }

        public void setBid(String bid) {
            this.bid = bid;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public String getOffice_id() {
            return office_id;
        }

        public void setOffice_id(String office_id) {
            this.office_id = office_id;
        }

        public String getGroupid() {
            return groupid;
        }

        public void setGroupid(String groupid) {
            this.groupid = groupid;
        }
    }

}
