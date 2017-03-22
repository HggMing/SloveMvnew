package com.ming.slove.mvnew.api.video;

/**
 * 直播相关api
 * Created by Ming on 2016/12/9.
 */

public class VideoApi {
    private static VideoService mService;
    private static boolean isApiChanged;

    public static VideoService getService() {
        if (mService == null || isApiChanged) {
            mService = VideoApiUtils.createService(VideoService.class);
            isApiChanged = false;
        }
        return mService;
    }
}
