package com.trtc.uikit.livekit.livestreamcore.manager.module;

import static com.tencent.cloud.tuikit.engine.room.TUIRoomDefine.VideoQuality.Q_1080P;

import android.text.TextUtils;

import com.tencent.cloud.tuikit.engine.common.TUICommonDefine;
import com.tencent.cloud.tuikit.engine.common.TUIVideoView;
import com.tencent.cloud.tuikit.engine.room.TUIRoomDefine;
import com.tencent.qcloud.tuicore.TUIConfig;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuicore.permission.PermissionCallback;
import com.trtc.tuikit.common.system.ContextProvider;
import com.trtc.uikit.livekit.livestreamcore.common.utils.Logger;
import com.trtc.uikit.livekit.livestreamcore.common.utils.PermissionRequest;
import com.trtc.uikit.livekit.livestreamcore.manager.api.ILiveStream;
import com.trtc.uikit.livekit.livestreamcore.state.LiveStreamState;
import com.trtc.uikit.livekit.livestreamcore.state.MediaState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaManager extends BaseManager {
    private static final String     TAG = "MediaManager";
    public final         MediaState mMediaState;

    public MediaManager(LiveStreamState state, ILiveStream service) {
        super(state, service);
        mMediaState = state.mediaState;
        initVideoAdvanceSettings();
    }

    @Override
    public void destroy() {
        Logger.info(TAG + " destroy");
        closeLocalCamera();
        unInitVideoAdvanceSettings();
    }

    public void updateVideoQualityEx(TUIRoomDefine.RoomVideoEncoderParams videoEncParams) {
        mMediaState.videoEncParams.setCurrentEnc(videoEncParams);
        mVideoLiveService.updateVideoQualityEx(videoEncParams);
    }

    public void enableMirror(boolean enable) {
        mVideoLiveService.setCameraMirror(enable);
        mMediaState.isMirror.set(enable);
    }

    public void enableAdvancedVisible(boolean visible) {
        mMediaState.videoAdvanceSetting.isVisible = visible;
    }

    public void enableUltimate(boolean enable) {
        mMediaState.videoAdvanceSetting.isUltimateEnabled = enable;
        Map<String, Object> params = new HashMap<>();
        params.put("enable", enable);
        TUICore.callService("VideoAdvanceExtension", "enableUltimate", params);

        if(enable){
            enableBFrame(false);
        }
    }

    public void enableBFrame(boolean enable) {
        mMediaState.videoAdvanceSetting.isBFrameEnabled = enable;
        Map<String, Object> params = new HashMap<>();
        params.put("enable", enable);
        TUICore.callService("VideoAdvanceExtension", "enableBFrame", params);
    }

    public void enableH265(boolean enable) {
        mMediaState.videoAdvanceSetting.isH265Enabled = enable;
        Map<String, Object> params = new HashMap<>();
        params.put("enable", enable);
        TUICore.callService("VideoAdvanceExtension", "enableH265", params);
    }

    public void openLocalCamera(boolean useFrontCamera, TUIRoomDefine.ActionCallback callback) {
        if (mVideoLiveState.mediaState.isFrontCamera.get() != useFrontCamera
                && mVideoLiveState.mediaState.isCameraOpened.get()) {
            switchCamera();
            if (callback != null) {
                callback.onSuccess();
            }
            return;
        }
        mVideoLiveState.mediaState.isFrontCamera.set(useFrontCamera);
        boolean hasCameraPermission = mVideoLiveState.mediaState.hasCameraPermission.get();
        if (hasCameraPermission) {
            openLocalCameraByService(callback);
            return;
        }
        requestCameraPermissions(new TUIRoomDefine.ActionCallback() {
            @Override
            public void onSuccess() {
                openLocalCameraByService(callback);
            }

            @Override
            public void onError(TUICommonDefine.Error error, String s) {
                if (callback != null) {
                    callback.onError(error, s);
                }
            }
        });
    }

    public void closeLocalCamera() {
        mVideoLiveService.closeLocalCamera();
        mVideoLiveState.mediaState.isCameraOpened.set(false);
    }

    public void setLocalVideoView(TUIVideoView view) {
        mVideoLiveService.setLocalVideoView(view);
    }

    public void setRemoteVideoView(String userId, TUIRoomDefine.VideoStreamType streamType, TUIVideoView videoView) {
        mVideoLiveService.setRemoteVideoView(userId, streamType, videoView);
    }

    public void startPlayRemoteVideo(String userId, TUIRoomDefine.VideoStreamType streamType,
                                     TUIRoomDefine.PlayCallback callback) {
        mVideoLiveService.startPlayRemoteVideo(userId, streamType, callback);
    }

    public void stopPlayRemoteVideo(String userId, TUIRoomDefine.VideoStreamType streamType) {
        mVideoLiveService.stopPlayRemoteVideo(userId, streamType);
    }

    public void openLocalMicrophone(TUIRoomDefine.ActionCallback callback) {
        boolean hasMicrophonePermission = mVideoLiveState.mediaState.hasMicrophonePermission.get();
        if (hasMicrophonePermission) {
            openLocalMicrophoneByService(callback);
            return;
        }
        Logger.info(TAG + " requestMicrophonePermissions:[]");
        requestMicrophonePermissions(new TUIRoomDefine.ActionCallback() {
            @Override
            public void onSuccess() {
                openLocalMicrophoneByService(callback);
            }

            @Override
            public void onError(TUICommonDefine.Error error, String s) {
                if (callback != null) {
                    callback.onError(error, s);
                }
            }
        });
    }

    public void closeLocalMicrophone() {
        mVideoLiveService.closeLocalMicrophone();
        mVideoLiveState.mediaState.isMicrophoneOpened.set(false);
    }

    public void unMuteLocalAudio() {
        mVideoLiveService.unMuteLocalAudio(new TUIRoomDefine.ActionCallback() {
            @Override
            public void onSuccess() {
                mVideoLiveState.mediaState.isMicrophoneMuted.set(false);
            }

            @Override
            public void onError(TUICommonDefine.Error error, String message) {
            }
        });
    }

    public void muteLocalAudio(boolean mute) {
        if (mute) {
            mVideoLiveService.muteLocalAudio();
            mVideoLiveState.mediaState.isMicrophoneMuted.set(true);
        } else {
            unMuteLocalAudio();
        }
    }

    public void switchCamera() {
        boolean isFrontCamera = mMediaState.isFrontCamera.get();
        mVideoLiveService.switchCamera(!isFrontCamera);
        mMediaState.isFrontCamera.set(!isFrontCamera);
    }

    public void onSeatListChanged(List<TUIRoomDefine.SeatInfo> seatList) {
        List<TUIRoomDefine.SeatInfo> newList = new ArrayList<>(seatList.size());
        for (TUIRoomDefine.SeatInfo info : seatList) {
            if (!TextUtils.isEmpty(info.userId)) {
                newList.add(info);
            }
        }
        boolean isSelfInSeat = false;
        for (TUIRoomDefine.SeatInfo seatInfo : newList) {
            if (TextUtils.equals(seatInfo.userId, mVideoLiveState.userState.selfInfo.userId)) {
                isSelfInSeat = true;
            }
        }
        if (isSelfInSeat) {
            MediaState.VideoEncParams.VideoEncType targetEncType = newList.size() > 1
                    ? MediaState.VideoEncParams.VideoEncType.SMALL
                    : MediaState.VideoEncParams.VideoEncType.BIG;
            changeVideoEncParams(targetEncType);
        } else {
            mVideoLiveState.mediaState.isCameraOpened.set(false);
            mVideoLiveState.mediaState.isMicrophoneOpened.set(false);
        }
    }

    public void updateVideoEncParams() {
        if (TextUtils.equals(mVideoLiveState.userState.selfInfo.userId, mVideoLiveState.roomState.ownerInfo.userId)) {
            changeVideoEncParams(MediaState.VideoEncParams.VideoEncType.BIG);
        } else {
            changeVideoEncParams(MediaState.VideoEncParams.VideoEncType.SMALL);
        }
    }

    public void requestPermissions(boolean openCamera, TUIRoomDefine.ActionCallback callback) {
        requestMicrophonePermissions(new TUIRoomDefine.ActionCallback() {
            @Override
            public void onSuccess() {
                if (openCamera) {
                    requestCameraPermissions(callback);
                } else {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }
            }

            @Override
            public void onError(TUICommonDefine.Error error, String message) {
                if (callback != null) {
                    callback.onError(error, message);
                }
            }
        });
    }

    private void requestMicrophonePermissions(TUIRoomDefine.ActionCallback callback) {
        Logger.info(TAG + " requestMicrophonePermissions:[]");
        PermissionRequest.requestMicrophonePermissions(ContextProvider.getApplicationContext(),
                new PermissionCallback() {
                    @Override
                    public void onRequesting() {
                        Logger.info(TAG + " requestMicrophonePermissions:[onRequesting}");
                    }

                    @Override
                    public void onGranted() {
                        Logger.info(TAG + " requestMicrophonePermissions:[onGranted]");
                        mMediaState.hasMicrophonePermission.set(true);
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }

                    @Override
                    public void onDenied() {
                        Logger.warn(TAG + " requestMicrophonePermissions:[onDenied]");
                        if (callback != null) {
                            callback.onError(TUICommonDefine.Error.MICROPHONE_NOT_AUTHORIZED,
                                    "requestMicrophonePermissions" +
                                            ":[onDenied]");
                        }
                    }
                });
    }

    private void requestCameraPermissions(TUIRoomDefine.ActionCallback callback) {
        Logger.info(TAG + " requestCameraPermissions:[]");
        PermissionRequest.requestCameraPermissions(ContextProvider.getApplicationContext(), new PermissionCallback() {
            @Override
            public void onRequesting() {
                Logger.info(TAG + " requestCameraPermissions:[onRequesting]");
            }

            @Override
            public void onGranted() {
                Logger.info(TAG + " requestCameraPermissions:[onGranted]");
                mMediaState.hasCameraPermission.set(true);
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onDenied() {
                Logger.info(TAG + " requestCameraPermissions:[onDenied]");
                if (callback != null) {
                    callback.onError(TUICommonDefine.Error.CAMERA_NOT_AUTHORIZED, "requestCameraPermissions:[onDenied" +
                            "]");
                }
            }
        });
    }

    private void initVideoAdvanceSettings() {
        enableUltimate(true);
        enableH265(true);
    }

    private void unInitVideoAdvanceSettings() {
        enableUltimate(false);
        enableH265(false);

        changeVideoEncParams(MediaState.VideoEncParams.VideoEncType.BIG);
        TUIRoomDefine.RoomVideoEncoderParams roomVideoEncoderParams = new TUIRoomDefine.RoomVideoEncoderParams();
        roomVideoEncoderParams.videoResolution = Q_1080P;
        roomVideoEncoderParams.bitrate = 4000;
        roomVideoEncoderParams.fps = 20;
        roomVideoEncoderParams.resolutionMode = TUIRoomDefine.ResolutionMode.PORTRAIT;
        mVideoLiveService.updateVideoQualityEx(roomVideoEncoderParams);
    }

    private void changeVideoEncParams(MediaState.VideoEncParams.VideoEncType encType) {
        Logger.info(TAG + " changeVideoEncParams:" + encType);
        mMediaState.videoEncParams.currentEncType = encType;
        if (encType == MediaState.VideoEncParams.VideoEncType.BIG) {
            updateVideoQualityEx(mMediaState.videoEncParams.big);
        } else if (encType == MediaState.VideoEncParams.VideoEncType.SMALL) {
            updateVideoQualityEx(mMediaState.videoEncParams.small);
        }
    }

    private void openLocalMicrophoneByService(TUIRoomDefine.ActionCallback callback) {
        unMuteLocalAudio();
        mVideoLiveService.openLocalMicrophone(new TUIRoomDefine.ActionCallback() {
            @Override
            public void onSuccess() {
                mVideoLiveState.mediaState.isMicrophoneOpened.set(true);
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(TUICommonDefine.Error error, String message) {
                if (callback != null) {
                    callback.onError(error, message);
                }
            }
        });
    }

    private void openLocalCameraByService(TUIRoomDefine.ActionCallback callback) {
        boolean isFront = mVideoLiveState.mediaState.isFrontCamera.get();
        TUIRoomDefine.VideoQuality quality = mMediaState.videoEncParams.getCurrentEnc().videoResolution;
        mVideoLiveService.openLocalCamera(isFront, quality, new TUIRoomDefine.ActionCallback() {
            @Override
            public void onSuccess() {
                initLivingConfig();
                mVideoLiveState.mediaState.isCameraOpened.set(true);
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(TUICommonDefine.Error error, String message) {
                if (callback != null) {
                    callback.onError(error, message);
                }
            }
        });
    }

    private void initLivingConfig() {
        mVideoLiveService.enableGravitySensor(true);
        mVideoLiveService.setVideoResolutionMode(TUIRoomDefine.ResolutionMode.PORTRAIT);
    }
}
