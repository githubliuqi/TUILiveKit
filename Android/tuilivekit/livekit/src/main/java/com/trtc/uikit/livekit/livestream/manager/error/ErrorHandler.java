package com.trtc.uikit.livekit.livestream.manager.error;

import com.tencent.cloud.tuikit.engine.common.TUICommonDefine;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.trtc.uikit.component.common.ErrorLocalized;

public class ErrorHandler {

    public static void onError(TUICommonDefine.Error error) {
        ErrorLocalized.onError(error);
    }

    public static void handleMessage(String message) {
        ToastUtil.toastShortMessage(message);
    }
}
