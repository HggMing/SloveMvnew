package com.ming.slove.mvnew.model.bean;

import java.util.List;

/**
 * Created by MingN on 2016/12/27.
 */

public class IncomeMonth {


    /**
     * err : 0
     * list : [{"type":"jichu","money":"4294.03"},{"type":"fandian","money":"0.00"},{"type":"tichen","money":"0.00"}]
     */

    private int err;
    private List<ListBean> list;

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * type : jichu
         * money : 4294.03
         */

        private String type;
        private String money;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }
    }
}
