package com.trtc.uikit.livekit.livestream.view.audience.playing.coguest.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trtc.tuikit.common.ui.PopupDialog;
import com.trtc.uikit.component.audioeffect.AudioEffectPanel;
import com.trtc.uikit.livekit.R;
import com.trtc.uikit.livekit.livestream.manager.LiveStreamManager;

import java.util.ArrayList;
import java.util.List;

public class SettingsListAdapter extends RecyclerView.Adapter<SettingsListAdapter.ViewHolder> {
    private static final int                 ITEM_TYPE_AUDIO_EFFECT = 1;
    private final        List<SettingsItem>  mData                  = new ArrayList<>();
    private final        Context             mContext;
    private final        LiveStreamManager   mLiveStreamManager;
    private final        SettingsPanelDialog mSettingsDialog;
    private              PopupDialog         mAudioEffectDialog;

    public SettingsListAdapter(Context context, LiveStreamManager liveStreamManager, SettingsPanelDialog dialog) {
        mContext = context;
        mLiveStreamManager = liveStreamManager;
        mSettingsDialog = dialog;
        initData();
    }

    private void initData() {
        mData.add(new SettingsItem(mContext.getString(R.string.livekit_audio_effect)
                , R.drawable.livekit_settings_audio_effect, ITEM_TYPE_AUDIO_EFFECT));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.livekit_anchor_settings_panel_item,
                parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textTitle.setText(mData.get(position).title);
        holder.imageIcon.setImageResource(mData.get(position).icon);
        holder.layoutRoot.setTag(mData.get(position).type);
        holder.layoutRoot.setOnClickListener((view) -> {
            int type = (Integer) view.getTag();
            switch (type) {
                case ITEM_TYPE_AUDIO_EFFECT:
                    showAudioEffectPanel();
                    break;
                default:
                    break;

            }
        });
    }

    private void showAudioEffectPanel() {
        mSettingsDialog.dismiss();
        if (mAudioEffectDialog == null) {
            mAudioEffectDialog = new PopupDialog(mContext);
            AudioEffectPanel audioEffectPanel = new AudioEffectPanel(mContext);
            audioEffectPanel.init(mLiveStreamManager.getRoomState().roomId);
            audioEffectPanel.setOnBackButtonClickListener(() -> mAudioEffectDialog.dismiss());
            mAudioEffectDialog.setView(audioEffectPanel);
        }
        mAudioEffectDialog.show();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layoutRoot;
        public TextView     textTitle;
        public ImageView    imageIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            layoutRoot = itemView.findViewById(R.id.ll_root);
            textTitle = itemView.findViewById(R.id.tv_title);
            imageIcon = itemView.findViewById(R.id.iv_icon);
        }
    }


    public static class SettingsItem {
        public String title;
        public int    icon;
        public int    type;

        public SettingsItem(String title, int icon, int type) {
            this.title = title;
            this.icon = icon;
            this.type = type;
        }
    }
}