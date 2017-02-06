package com.ming.slove.mvnew.api.video;

import com.ming.slove.mvnew.api.ApiUtils;

/**
 * 直播相关api
 * Created by Ming on 2016/12/9.
 */

public class VideoApi {
    private static VideoService mService;
    public static boolean isApiChanged;

    public static VideoService getService() {
        if (mService == null || isApiChanged) {
            mService = ApiUtils.createService(VideoService.class);
            isApiChanged = false;
        }
        return mService;
    }
}
