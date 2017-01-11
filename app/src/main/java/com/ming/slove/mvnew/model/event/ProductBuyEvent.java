package com.ming.slove.mvnew.model.event;

/**
 * Created by Ming on 2016/11/10.
 */

import android.util.SparseArray;

import com.ming.slove.mvnew.model.bean.ProductList;
import com.ming.slove.mvnew.model.bean.ProductNewOrder;

import java.math.BigDecimal;

/**
 * 传递特产购买的购物清单和总价格
 */
public class ProductBuyEvent {

    private BigDecimal priceAll;//商品总价
    private SparseArray<ProductList.DataBean.ListBean> buyList = new SparseArray<>();//已买商品列表
    private ProductNewOrder.DataBean dataFromOrder;//订单信息：单号，价格等

    public ProductBuyEvent(BigDecimal priceAll, SparseArray<ProductList.DataBean.ListBean> buyList) {
        this.priceAll = priceAll;
        this.buyList = buyList;
    }

    public ProductBuyEvent(BigDecimal priceAll, SparseArray<ProductList.DataBean.ListBean> buyList,ProductNewOrder.DataBean dataFromOrder) {
        this(priceAll,buyList);
        this.dataFromOrder=dataFromOrder;
    }

    public BigDecimal getPriceAll() {
        return priceAll;
    }

    public SparseArray<ProductList.DataBean.ListBean> getBuyList() {
        return buyList;
    }

    public ProductNewOrder.DataBean getDataFromOrder() {
        return dataFromOrder;
    }
}
