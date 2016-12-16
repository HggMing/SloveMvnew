package com.ming.slove.mvnew.model.database;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 保存帖子是否举报
 * Created by Ming on 2016/6/13.
 */
@Table("bbs_detail")
public class BbsDetailModel extends BaseModel {

    @PrimaryKey(AssignType.BY_MYSELF)
    @Column("_bid")
    private String bid;//帖子id
    @Column("_is_report")
    @Default("false")
    private boolean isReport;//是否已举报

    public BbsDetailModel(String bid, boolean isReport) {
        this.bid = bid;
        this.isReport = isReport;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public boolean isReport() {
        return isReport;
    }

    public void setReport(boolean report) {
        isReport = report;
    }
}
