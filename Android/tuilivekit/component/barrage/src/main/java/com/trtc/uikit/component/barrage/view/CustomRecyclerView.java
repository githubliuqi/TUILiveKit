package com.trtc.uikit.component.barrage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomRecyclerView extends RecyclerView {

    private final GestureDetector mGestureDetector;
    boolean mIsLongPressed = false;

    public CustomRecyclerView(Context context) {
        this(context, null);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(@NonNull MotionEvent e) {
                mIsLongPressed = true;
            }
        });
    }

    public boolean isLongPressed() {
        return mIsLongPressed;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mIsLongPressed) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                mIsLongPressed = false;
            }
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mGestureDetector != null && mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }
}

