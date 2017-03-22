package com.ming.slove.mvnew.model.databean;

/**
 * mimi框架要提交的数据
 * Created by MingN on 2017/2/23.
 */

public class WebAppUserInfo {
    private String auth;
    private String uname;
    private String headpic;
    private String level;

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
