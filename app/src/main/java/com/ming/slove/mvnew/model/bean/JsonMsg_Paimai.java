package com.ming.slove.mvnew.model.bean;

/**
 * Created by MingN on 2017/5/9.
 */

public class JsonMsg_Paimai {

    /**
     * content : 恭喜您在我们村参与竞拍的商品竞拍成功(蒲江不知火丑柑，成交价￥11.00元)，请在1小时内完成支付。
     * url : http://118.178.232.77:9901/auction/pay?pid=123
     * title : 支付
     */

    private String content;
    private String url;
    private String title;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
