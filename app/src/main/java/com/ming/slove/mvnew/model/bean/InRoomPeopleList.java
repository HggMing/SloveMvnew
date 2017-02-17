package com.ming.slove.mvnew.model.bean;

import java.util.List;

/**
 * Created by MingN on 2017/1/22.
 */

public class InRoomPeopleList {
    /**
     * err : 0
     * data : {"cnt":"2","list":[{"id":"484","account":"19910001001","name":"打击盗版","room_id":"10418","k":"7f0000010b5400000007","ctime":"1485076389","is_zb":"0"}]}
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
         * cnt : 2
         * list : [{"id":"484","account":"19910001001","name":"打击盗版","room_id":"10418","k":"7f0000010b5400000007","ctime":"1485076389","is_zb":"0"}]
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

        public static class ListBean {
            /**
             * id : 484
             * account : 19910001001
             * name : 打击盗版
             * room_id : 10418
             * k : 7f0000010b5400000007
             * ctime : 1485076389
             * is_zb : 0
             */

            private String id;
            private String account;
            private String name;
            private String room_id;
            private String k;
            private String ctime;
            private String is_zb;
            private String head;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getAccount() {
                return account;
            }

            public void setAccount(String account) {
                this.account = account;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getRoom_id() {
                return room_id;
            }

            public void setRoom_id(String room_id) {
                this.room_id = room_id;
            }

            public String getK() {
                return k;
            }

            public void setK(String k) {
                this.k = k;
            }

            public String getCtime() {
                return ctime;
            }

            public void setCtime(String ctime) {
                this.ctime = ctime;
            }

            public String getIs_zb() {
                return is_zb;
            }

            public void setIs_zb(String is_zb) {
                this.is_zb = is_zb;
            }

            public String getHead() {
                return head;
            }

            public void setHead(String head) {
                this.head = head;
            }
        }
    }
}
