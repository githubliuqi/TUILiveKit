package com.trtc.uikit.component.gift.view.animation.manager;

import com.trtc.uikit.component.gift.store.model.Gift;
import com.trtc.uikit.component.gift.store.model.GiftUser;

public class GiftAnimationModel {
    public Gift     gift;
    public int      giftCount;
    public GiftUser sender;
    public boolean  isFromSelf = false;
}
