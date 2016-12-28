package com.ming.slove.mvnew.model.bean;

import java.util.List;

/**
 * Created by MingN on 2016/12/28.
 */

public class IncomeReward {

    /**
     * err : 0
     * list : [{"memo":"手机话费返点奖励","realmoney":"0.00"},{"memo":"电费充值返点奖励","realmoney":"0.00"}]
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
         * memo : 手机话费返点奖励
         * realmoney : 0.00
         */

        private String memo;
        private String realmoney;

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public String getRealmoney() {
            return realmoney;
        }

        public void setRealmoney(String realmoney) {
            this.realmoney = realmoney;
        }
    }
}
