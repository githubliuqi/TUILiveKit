package com.trtc.uikit.livekit.livestreamcore.common.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.tencent.qcloud.tuicore.permission.PermissionCallback;
import com.tencent.qcloud.tuicore.permission.PermissionRequester;
import com.trtc.uikit.livekit.livestreamcore.R;

import java.util.ArrayList;
import java.util.List;

public class PermissionRequest {

    public static void requestMicrophonePermissions(Context context, PermissionCallback callback) {
        StringBuilder title = new StringBuilder().append(context.getString(R.string.livestreamcore_permission_microphone));
        StringBuilder reason = new StringBuilder();
        reason.append(context.getString(R.string.livestreamcore_permission_mic_reason));
        List<String> permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.RECORD_AUDIO);

        PermissionCallback permissionCallback = new PermissionCallback() {
            @Override
            public void onGranted() {
                if (callback != null) {
                    callback.onGranted();
                }
            }

            @Override
            public void onDenied() {
                super.onDenied();
                if (callback != null) {
                    callback.onDenied();
                }
            }
        };

        ApplicationInfo applicationInfo = context.getApplicationInfo();
        String appName = context.getPackageManager().getApplicationLabel(applicationInfo).toString();

        String[] permissions = permissionList.toArray(new String[0]);
        PermissionRequester.newInstance(permissions)
                .title(context.getString(R.string.livestreamcore_permission_title, appName, title))
                .description(reason.toString())
                .settingsTip(context.getString(R.string.livestreamcore_permission_tips, title) + "\n" + reason)
                .callback(permissionCallback)
                .request();
    }

    public static void requestCameraPermissions(Context context, PermissionCallback callback) {
        StringBuilder title = new StringBuilder().append(context.getString(R.string.livestreamcore_permission_camera));
        StringBuilder reason = new StringBuilder();
        reason.append(context.getString(R.string.livestreamcore_permission_camera_reason));
        List<String> permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.CAMERA);

        PermissionCallback permissionCallback = new PermissionCallback() {
            @Override
            public void onGranted() {
                if (callback != null) {
                    callback.onGranted();
                }
            }

            @Override
            public void onDenied() {
                super.onDenied();
                if (callback != null) {
                    callback.onDenied();
                }
            }
        };

        ApplicationInfo applicationInfo = context.getApplicationInfo();
        String appName = context.getPackageManager().getApplicationLabel(applicationInfo).toString();

        String[] permissions = permissionList.toArray(new String[0]);
        PermissionRequester.newInstance(permissions)
                .title(context.getString(R.string.livestreamcore_permission_title, appName, title))
                .description(reason.toString())
                .settingsTip(context.getString(R.string.livestreamcore_permission_tips, title) + "\n" + reason)
                .callback(permissionCallback)
                .request();
    }
}
