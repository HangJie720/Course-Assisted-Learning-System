package com.ijustyce.fastandroiddev.base;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by yangchun on 16/3/10.  自定义的viewPager，可以禁止左右滑动
 */
public class BaseViewPager extends ViewPager {

    private boolean isCanScroll = true;

    private float mLastMotionX, mLastMotionY;
    private int mActivePointerId, mTouchSlop;
    private boolean mIsBeingDragged;

    private static final int TOUCH_V = 1, TOUCH_H = 2, TOUCH_UNKNOWN = 3;   //  滑动方向：1-竖直、2-水平、3-未知

    public BaseViewPager(Context context) {

        super(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    }

    public BaseViewPager(Context context, AttributeSet attrs) {

        super(context, attrs);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    }

    /**
     * 是否禁止左右滑动
     * @param isCanScroll   TRUE的时候可以左右滑动，false的时候不能左右滑动，默认是true
     */
    public void setCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        //  由于interceptTouch返回的值，很多次是未知，所以这里不能判断相等，而是要判断不等！！！
        if (!isCanScroll){

            return interceptTouch(ev) == TOUCH_H;   //  如果竖直返回true，true则拦截touch事件
        }

        return super.onInterceptTouchEvent(ev);
    }

    //  这个方法一般会调用多次，其中，很多次是未知，然后才是正确的值！所以，一定要注意了！
    private int interceptTouch(MotionEvent ev) {

        final int action = ev.getAction();

        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {

                // Remember where the motion event started
                mIsBeingDragged = false;
                mLastMotionY = ev.getY();
                mLastMotionX = ev.getX();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }

            case MotionEvent.ACTION_MOVE:
                if (!mIsBeingDragged) {
                    final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    final float x = MotionEventCompat.getX(ev, pointerIndex);
                    final float y = MotionEventCompat.getY(ev, pointerIndex);
                    final float xDiff = Math.abs(x - mLastMotionX);
                    final float yDiff = Math.abs(y - mLastMotionY);
                    float primaryDiff = 0;
                    float secondaryDiff = 0;

                    primaryDiff = yDiff;
                    secondaryDiff = xDiff;

                    if (primaryDiff > mTouchSlop && primaryDiff > secondaryDiff) {
                        mIsBeingDragged = true;
                        return TOUCH_V; //  竖直滑动
                    }if (secondaryDiff > mTouchSlop && primaryDiff < secondaryDiff){
                        mIsBeingDragged = true;
                        return TOUCH_H; //  水平滑动
                    }
                }
                break;
        }
        return TOUCH_UNKNOWN;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        //  由于interceptTouch返回的值，很多次是未知，所以这里不能判断相等，而是要判断不等！！！
        if (!isCanScroll){

            return interceptTouch(ev) == TOUCH_H;   //  如果竖直返回true，true则拦截touch事件
        }

        return super.onTouchEvent(ev);
    }
}

