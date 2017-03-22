package com.ming.slove.mvnew.api.other;

import com.ming.slove.mvnew.api.ApiUtils;

/**
 * 其他未归类接口
 */
public class OtherApi {
    public static boolean isApiChanged;
    private static OtherService mService;

    public static OtherService getService() {
        if (mService == null || isApiChanged) {
            mService = ApiUtils.createService(OtherService.class);
            isApiChanged = false;
        }
        return mService;
    }
}




