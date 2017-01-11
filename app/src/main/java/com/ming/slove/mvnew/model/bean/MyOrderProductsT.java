package com.ming.slove.mvnew.model.bean;

import java.util.List;

/**
 * 我的订单，商品列表，特产
 * Created by MingN on 2017/1/5.
 */

public class MyOrderProductsT {

    private List<ProductsBean> products;

    public List<ProductsBean> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsBean> products) {
        this.products = products;
    }

    public static class ProductsBean {
        /**
         * id : 1
         * name : 44CQ5Y6f55Sf5oCB6I+c57G95rK544CR
         * price : 45.00
         * count : 3
         * picurl : /Public/product/7/2016-03-16/2016031620160316974998544957.jpg
         * picurl_1 : /Public/product/7/2016-03-16/2016031620160316974998544957_1.jpg
         */

        private String id;
        private String name;
        private String price;
        private String count;
        private String picurl;
        private String picurl_1;
        private String buy_num;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getPicurl() {
            return picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }

        public String getPicurl_1() {
            return picurl_1;
        }

        public void setPicurl_1(String picurl_1) {
            this.picurl_1 = picurl_1;
        }

        public String getBuy_num() {
            return buy_num;
        }

        public void setBuy_num(String buy_num) {
            this.buy_num = buy_num;
        }
    }
}
