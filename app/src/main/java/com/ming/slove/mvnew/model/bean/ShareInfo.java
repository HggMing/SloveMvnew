package com.ming.slove.mvnew.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MingN on 2017/3/23.
 */

public class ShareInfo implements Parcelable {
    private String title;//标题
    private String summary;//内容
    private String targetUrl;//分享链接地址
    private String thumb;//缩略图地址

    public ShareInfo() {
    }

    public ShareInfo(String title, String summary, String targetUrl, String thumb) {
        this.title = title;
        this.summary = summary;
        this.targetUrl = targetUrl;
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.targetUrl);
        dest.writeString(this.thumb);
    }

    protected ShareInfo(Parcel in) {
        this.title = in.readString();
        this.summary = in.readString();
        this.targetUrl = in.readString();
        this.thumb = in.readString();
    }

    public static final Parcelable.Creator<ShareInfo> CREATOR = new Parcelable.Creator<ShareInfo>() {
        @Override
        public ShareInfo createFromParcel(Parcel source) {
            return new ShareInfo(source);
        }

        @Override
        public ShareInfo[] newArray(int size) {
            return new ShareInfo[size];
        }
    };
}
