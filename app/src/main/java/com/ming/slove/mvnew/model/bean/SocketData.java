package com.ming.slove.mvnew.model.bean;

/**
 * Created by MingN on 2017/2/13.
 */

public class SocketData {

    /**
     * op_type : come_in_room
     * data : {"room_id":10073,"phone":"13547999999","account":"13547999999","name":"张傻","k":"11121212","ctime":"112121212"}
     */

    private String op_type;
    private DataBean data;

    public String getOp_type() {
        return op_type;
    }

    public void setOp_type(String op_type) {
        this.op_type = op_type;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * room_id : 10073
         * phone : 13547999999
         * account : 13547999999
         * name : 张傻
         * k : 11121212
         * ctime : 112121212
         */

        private int room_id;
        private String phone;
        private String account;
        private String name;
        private String k;
        private String ctime;
        //房间点赞数据
        private int zan_num;
        //房间内消息
        private FromBean from;
        private ToBean to;
        private int ty;
        private String content;
        private int time;


        public int getRoom_id() {
            return room_id;
        }

        public void setRoom_id(int room_id) {
            this.room_id = room_id;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
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

        public int getZan_num() {
            return zan_num;
        }

        public void setZan_num(int zan_num) {
            this.zan_num = zan_num;
        }

        public FromBean getFrom() {
            return from;
        }

        public void setFrom(FromBean from) {
            this.from = from;
        }

        public ToBean getTo() {
            return to;
        }

        public void setTo(ToBean to) {
            this.to = to;
        }

        public int getTy() {
            return ty;
        }

        public void setTy(int ty) {
            this.ty = ty;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public static class FromBean {
            /**
             * phone : 13547983127
             * account : 13547983127
             * name : 许贵林
             */

            private String phone;
            private String account;
            private String name;

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
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
        }

        public static class ToBean {
            /**
             * phone : 15682558303
             * account : 15682558303
             * name : 张锋
             */

            private String phone;
            private String account;
            private String name;

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
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
        }
    }
}
