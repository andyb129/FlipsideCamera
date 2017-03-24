package com.sixteenplusfour.android.flipsidecamera.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by andyb129 on 16/12/2015.
 */
public class SwipeImageView extends ImageView {

    private boolean mIsSwipeEnabled;
    private final GestureDetector gestureDetector = new GestureDetector(new GestureListener());
    private OnSwipeListener listener;

    public interface OnSwipeListener
    {
        void onSwipeRight();
        void onSwipeLeft();
    }

    public SwipeImageView(Context context) {
        super(context);
    }

    public SwipeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mIsSwipeEnabled ? gestureDetector.onTouchEvent(event) : super.onTouchEvent(event);
    }

    public void onSwipeRight()
    {
        if (listener != null) {
            listener.onSwipeRight();
        }
    }

    public void onSwipeLeft()
    {
        if (listener != null) {
            listener.onSwipeLeft();
        }
    }

    public void setSwipeListener(OnSwipeListener listener) {
        this.listener = listener;
    }

    public void setIsSwipeEnabled(boolean isSwipeEnabled) {
        this.mIsSwipeEnabled = isSwipeEnabled;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //onTouch(e);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                } else {
                    // onTouch(e);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
}
