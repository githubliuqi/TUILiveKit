package com.trtc.uikit.component.audiencelist.service;

import static com.trtc.uikit.component.audiencelist.store.AudienceListState.AUDIENCE_COUNT_CACHE_SIZE;

import android.text.TextUtils;

import com.tencent.cloud.tuikit.engine.room.TUIRoomDefine;
import com.tencent.cloud.tuikit.engine.room.TUIRoomObserver;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.trtc.uikit.component.audiencelist.store.AudienceListState;
import com.trtc.uikit.component.common.CommonLogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public class AudienceListObserver extends TUIRoomObserver {
    private static final CommonLogger LOGGER = CommonLogger.getCommonLogger("AudienceListObserver");

    protected AudienceListState mAudienceListState;

    public AudienceListObserver(AudienceListState audienceListState) {
        mAudienceListState = audienceListState;
    }

    @Override
    public void onRoomUserCountChanged(String roomId, int userCount) {
        if (userCount <= 0) {
            return;
        }
        List<Integer> cacheList = mAudienceListState.lastAudienceCountCache;
        if (cacheList.size() == AUDIENCE_COUNT_CACHE_SIZE) {
            cacheList.remove(0);
        }
        cacheList.add(userCount);
        int sum = 0;
        for (Integer item : cacheList) {
            sum += item;
        }
        int averageCount = sum / cacheList.size() - 1;
        averageCount = Math.max(averageCount, 0);
        mAudienceListState.audienceCount.set(averageCount);
    }

    @Override
    public void onRemoteUserEnterRoom(String roomId, TUIRoomDefine.UserInfo userInfo) {
        if (userInfo.userId.equals(mAudienceListState.ownerId)) {
            return;
        }
        for (TUIRoomDefine.UserInfo info : mAudienceListState.audienceList.get()) {
            if (info.userId.equals(userInfo.userId)) {
                return;
            }
        }
        TUIRoomDefine.UserInfo audienceUser = new TUIRoomDefine.UserInfo();
        audienceUser.userId = userInfo.userId;
        audienceUser.userName = userInfo.userName;
        audienceUser.avatarUrl = userInfo.avatarUrl;
        audienceUser.userRole = userInfo.userRole;

        List<String> ids = new ArrayList<>();
        ids.add(userInfo.userId);
        V2TIMManager.getInstance().getUsersInfo(ids, new V2TIMValueCallback<List<V2TIMUserFullInfo>>() {
            @Override
            public void onSuccess(List<V2TIMUserFullInfo> v2TIMUserFullInfos) {
                for (V2TIMUserFullInfo fullInfo : v2TIMUserFullInfos) {
                    if (Objects.equals(userInfo.userId, fullInfo.getUserID())) {
                        audienceUser.nameCard = String.valueOf(fullInfo.getLevel());
                        break;
                    }
                }
                addUser(audienceUser);
            }

            @Override
            public void onError(int code, String desc) {
                LOGGER.error("getUsersInfo onError:" + code + "," + desc);
                addUser(audienceUser);
            }

            private void addUser(TUIRoomDefine.UserInfo user) {
                TUIRoomDefine.UserInfo oldUser = null;
                LinkedHashSet<TUIRoomDefine.UserInfo> audienceList = mAudienceListState.audienceList.get();
                for (TUIRoomDefine.UserInfo item : audienceList) {
                    if (TextUtils.equals(item.userId, user.userId)) {
                        oldUser = item;
                        break;
                    }
                }
                if (oldUser != null) {
                    audienceList.remove(oldUser);
                }
                audienceList.add(user);
                LOGGER.info("addUser,userId:" + user.userId + ",listSize:" + audienceList.size());
                mAudienceListState.audienceList.set(audienceList);
            }
        });
    }

    @Override
    public void onRemoteUserLeaveRoom(String roomId, TUIRoomDefine.UserInfo userInfo) {
        Iterator<TUIRoomDefine.UserInfo> iterator = mAudienceListState.audienceList.get().iterator();
        while (iterator.hasNext()) {
            TUIRoomDefine.UserInfo audienceUser = iterator.next();
            if (audienceUser.userId.equals(userInfo.userId)) {
                iterator.remove();
                LOGGER.info("removeUser,userId:" + userInfo.userId + ",listSize:" + mAudienceListState.audienceList.get().size());
                mAudienceListState.audienceList.notifyDataChanged();
                break;
            }
        }
    }

    @Override
    public void onUserInfoChanged(TUIRoomDefine.UserInfo userInfo, List<TUIRoomDefine.UserInfoModifyFlag> modifyFlag) {
        boolean hasChanged = false;
        LinkedHashSet<TUIRoomDefine.UserInfo> userList = mAudienceListState.audienceList.get();
        for (TUIRoomDefine.UserInfo info : userList) {
            if (TextUtils.equals(info.userId, userInfo.userId)) {
                if (modifyFlag.contains(TUIRoomDefine.UserInfoModifyFlag.USER_ROLE)) {
                    info.userRole = userInfo.userRole;
                    hasChanged = true;
                }
                break;
            }
        }
        if (hasChanged) {
            mAudienceListState.audienceList.notifyDataChanged();
        }
    }
}
