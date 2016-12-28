package com.ming.slove.mvnew.model.bean;

import java.util.List;

/**
 * Created by MingN on 2016/12/27.
 */

public class IncomeBase {

    /**
     * err : 0
     * list : [{"memo":"扣除冻结金额,订单号:s20161226495053565153","realmoney":"-0.01","atime":"2016-12-26 6:00"},{"memo":"冻结余额,订单号:s20161226495053565153","realmoney":"+0.01","atime":"2016-12-26 6:00"},{"memo":"扣除冻结金额,订单号:s20161226501011019998","realmoney":"-0.01","atime":"2016-12-26 1:50"},{"memo":"冻结余额,订单号:s20161226501011019998","realmoney":"+0.01","atime":"2016-12-26 1:50"},{"memo":"扣除冻结金额,订单号:s20161226975555100535","realmoney":"-0.01","atime":"2016-12-26 1:49"},{"memo":"冻结余额,订单号:s20161226975555100535","realmoney":"+0.01","atime":"2016-12-26 1:49"},{"memo":"交通罚款(川B32198)","realmoney":"+2.00","atime":"2016-12-22 10:19"},{"memo":"电卡()","realmoney":"+1.00","atime":"2016-12-21 7:13"},{"memo":"电卡()","realmoney":"+2.00","atime":"2016-12-21 7:10"},{"memo":"交通罚款(川B32198)","realmoney":"+2.00","atime":"2016-12-21 6:53"},{"memo":"交通罚款(川B32198)","realmoney":"+23.00","atime":"2016-12-21 6:52"},{"memo":"交通罚款(川B32198)","realmoney":"+5.00","atime":"2016-12-21 6:52"},{"memo":"交通罚款(川B32198)","realmoney":"+1.00","atime":"2016-12-21 6:49"},{"memo":"店长完成任务奖励","realmoney":"+200.00","atime":"2016-12-19 6:56"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 6:56"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 6:54"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 6:54"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 6:54"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 6:54"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 6:54"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 6:54"},{"memo":"店长完成任务奖励","realmoney":"+200.00","atime":"2016-12-19 6:53"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 6:51"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 6:44"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 6:27"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 6:27"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 6:27"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 5:50"},{"memo":"订单结算,订单号:s20161219524854974855","realmoney":"-4.00","atime":"2016-12-19 5:48"},{"memo":"订单结算,订单号:s20161219995099551025","realmoney":"-2.00","atime":"2016-12-19 5:31"},{"memo":"订单结算,订单号:s20161219971001021014","realmoney":"-6.00","atime":"2016-12-19 5:27"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 5:16"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 5:08"},{"memo":"店长完成任务奖励","realmoney":"+200.00","atime":"2016-12-19 4:31"},{"memo":"店长完成任务奖励","realmoney":"+100.00","atime":"2016-12-19 4:30"},{"memo":"店长完成任务奖励","realmoney":"+50.00","atime":"2016-12-19 4:26"},{"memo":"订单分成,订单号:s20161218101971015750","realmoney":"+2000.00","atime":"2016-12-18 11:46"},{"memo":"订单分成,订单号:s20161218495049515651","realmoney":"+8.00","atime":"2016-12-18 11:46"},{"memo":"订单结算,订单号:s20161209485352561005","realmoney":"-0.02","atime":"2016-12-09 7:08"},{"memo":"订单结算,订单号:s20161209495148579797","realmoney":"-0.01","atime":"2016-12-09 6:21"},{"memo":"订单结算,订单号:s20161206545610110210","realmoney":"-12.13","atime":"2016-12-09 4:56"},{"memo":"订单结算,订单号:s20161209985752101981","realmoney":"-0.02","atime":"2016-12-09 4:56"}]
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
         * memo : 扣除冻结金额,订单号:s20161226495053565153
         * realmoney : -0.01
         * atime : 2016-12-26 6:00
         */

        private String memo;
        private String realmoney;
        private String atime;

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

        public String getAtime() {
            return atime;
        }

        public void setAtime(String atime) {
            this.atime = atime;
        }
    }
}
