package com.ming.slove.mvnew.model.bean;

import java.util.List;

/**
 * Created by MingN on 2017/5/8.
 */

public class JsonMsg_Tuiguang {


    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * img_url : http://product.yibanke.com/Public/product/13453/2017-05-05/20170505s20170505545099565555.jpg
         * title : 汪文华牌婺晓皇菊 单罐约100朵
         * product_url : http://product.yibanke.com/mobile/info?device_type=1&id=438
         * price : 68.00
         */

        private String img_url;
        private String title;
        private String product_url;
        private String price;

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getProduct_url() {
            return product_url;
        }

        public void setProduct_url(String product_url) {
            this.product_url = product_url;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
}
