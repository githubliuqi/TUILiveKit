package com.trtc.uikit.component.gift.service;

import android.content.Context;

import com.tencent.imsdk.BaseConstants;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.trtc.tuikit.common.system.ContextProvider;
import com.trtc.uikit.component.common.CommonLogger;
import com.trtc.uikit.component.gift.R;

public class ErrorLocalized {

    private static final CommonLogger LOGGER = CommonLogger.getCommonLogger("GiftErrorLocalized");

    public static void onError(int error) {
        if (error == BaseConstants.ERR_SUCC) {
            return;
        }
        String errorMessage = convertToErrorMessage(error);
        LOGGER.error("error:" + error + " " + errorMessage);
        ToastUtil.toastShortMessage(errorMessage);
    }

    private static String convertToErrorMessage(int error) {
        String message = "";
        Context context = ContextProvider.getApplicationContext();
        if (context == null) {
            return "" + error;
        }
        switch (error) {
            case BaseConstants.ERR_SDK_COMM_API_CALL_FREQUENCY_LIMIT:
                message = context.getString(R.string.live_gift_error_freq_limit);
                break;
            case BaseConstants.ERR_SVR_GROUP_SHUTUP_DENY:
                message = context.getString(R.string.live_gift_error_disable_message_by_admin);
                break;
            case BaseConstants.ERR_SDK_BLOCKED_BY_SENSITIVE_WORD:
                message = context.getString(R.string.live_gift_error_sensitive_word);
                break;
            case BaseConstants.ERR_SDK_NET_PKG_SIZE_LIMIT:
                message = context.getString(R.string.live_gift_error_content_is_long);
                break;
            case BaseConstants.ERR_SDK_NET_DISCONNECT:
            case BaseConstants.ERR_SDK_NET_WAIT_ACK_TIMEOUT:
            case BaseConstants.ERR_SDK_NET_ALLREADY_CONN:
            case BaseConstants.ERR_SDK_NET_CONN_TIMEOUT:
            case BaseConstants.ERR_SDK_NET_CONN_REFUSE:
            case BaseConstants.ERR_SDK_NET_NET_UNREACH:
            case BaseConstants.ERR_SDK_NET_WAIT_INQUEUE_TIMEOUT:
            case BaseConstants.ERR_SDK_NET_WAIT_SEND_TIMEOUT:
            case BaseConstants.ERR_SDK_NET_WAIT_SEND_REMAINING_TIMEOUT:
            case BaseConstants.ERR_SDK_NET_WAIT_SEND_TIMEOUT_NO_NETWORK:
            case BaseConstants.ERR_SDK_NET_WAIT_ACK_TIMEOUT_NO_NETWORK:
            case BaseConstants.ERR_SDK_NET_SEND_REMAINING_TIMEOUT_NO_NETWORK:
                message = context.getString(R.string.live_gift_error_network);
                break;
            case BaseConstants.ERR_SVR_GROUP_NOT_FOUND:
                message = context.getString(R.string.live_gift_error_group_not_found);
                break;
            default:
                message = context.getString(R.string.live_gift_error_failed) + error;
        }
        return message;
    }
}

