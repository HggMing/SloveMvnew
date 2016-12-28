package com.ming.slove.mvnew.model.bean;

import java.util.List;

/**
 * Created by MingN on 2016/12/28.
 */

public class IncomeHistory {

    /**
     * err : 0
     * list : [{"actime":"2016-11","result":"1.50"},{"actime":"2016-10","result":"0.00"},{"actime":"2016-09","result":"0.00"},{"actime":"2016-08","result":"0.06"},{"actime":"2016-07","result":"0.00"},{"actime":"2016-06","result":"0.00"}]
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
         * actime : 2016-11
         * result : 1.50
         */

        private String actime;
        private String result;

        public String getActime() {
            return actime;
        }

        public void setActime(String actime) {
            this.actime = actime;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
}
