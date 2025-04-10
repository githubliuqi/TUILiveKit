package com.trtc.uikit.component.audiencelist.store;

import com.tencent.cloud.tuikit.engine.room.TUIRoomDefine;
import com.trtc.tuikit.common.livedata.LiveData;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class AudienceListState {

    public static final int AUDIENCE_COUNT_CACHE_SIZE = 10;

    public String                                          roomId;
    public String                                          ownerId;
    public LiveData<Integer>                               audienceCount = new LiveData<>(0);
    public LiveData<LinkedHashSet<TUIRoomDefine.UserInfo>> audienceList  = new LiveData<>(new LinkedHashSet<>());

    public List<Integer> lastAudienceCountCache = new ArrayList<>();
}
