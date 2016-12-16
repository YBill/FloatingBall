package com.floatingball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * Created by Bill on 2016/12/16.
 */

public class FloatBallView extends View {

    private Paint mPaint;

    // 球背景
    private int ballBgRadius = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 20, getContext().getResources().getDisplayMetrics()
    ); // 半径
    private String ballBgColor = "#484848";

    // 小球
    private int ballSmallRadius = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()
    );
    private String ballSmallColor = "#a3a3a3";

    // 大球,按下时
    private int ballBigRadius = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 15, getContext().getResources().getDisplayMetrics()
    );
    private String ballBigColor = "#d1d1d1";

    private boolean isDown = false; // 手指按下

    public FloatBallView(Context context) {
        super(context);
        init();
    }

    public FloatBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatBallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private GestureDetector mGestureDetector;

    private void init() {
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mGestureDetector = new GestureDetector(getContext(), new MyGestureListener(getContext()));
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(2 * ballBgRadius, 2 * ballBgRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.parseColor(ballBgColor));
        canvas.drawCircle(ballBgRadius, ballBgRadius, ballBgRadius, mPaint);

        if (isDown) {
            mPaint.setColor(Color.parseColor(ballBigColor));
            canvas.drawCircle(ballBgRadius, ballBgRadius, ballBigRadius, mPaint);
        } else {
            mPaint.setColor(Color.parseColor(ballSmallColor));
            canvas.drawCircle(ballBgRadius, ballBgRadius, ballSmallRadius, mPaint);
        }

    }

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    public void setLayoutParams(WindowManager.LayoutParams params) {
        mLayoutParams = params;
    }

    private float mLastDownX;
    private float mLastDownY;
    private float mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    private int mOffsetToParent = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 25, getContext().getResources().getDisplayMetrics()
    );


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDown = true;
                mLastDownX = event.getX();
                mLastDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouchSlop(event)) {
                    return true;
                }
                if(isLongTouch){
                    mLayoutParams.x = (int) (event.getRawX());
                    mLayoutParams.y = (int) (event.getRawY() - getStatusBarHeight());
                    mWindowManager.updateViewLayout(FloatBallView.this, mLayoutParams);
                } else {
                    doGesture(event);
                }

                break;
            case MotionEvent.ACTION_UP:
                isDown = false;
                break;
        }

        invalidate();

        return super.onTouchEvent(event);
    }

    private void doGesture(MotionEvent event) {
        float offsetX = event.getX() - mLastDownX;
        float offsetY = event.getY() - mLastDownY;

        if (Math.abs(offsetX) < mTouchSlop && Math.abs(offsetY) < mTouchSlop) {
            return;
        }

        if (Math.abs(offsetX) > Math.abs(offsetY)) {

        }else{

        }

    }

    /**
     * 获取通知栏高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 判断是否是轻微滑动
     *
     * @param event
     * @return
     */
    private boolean isTouchSlop(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (Math.abs(x - mLastDownX) < mTouchSlop && Math.abs(y - mLastDownY) < mTouchSlop) {
            return true;
        }
        return false;
    }

    private boolean isLongTouch;

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        public MyGestureListener(Context context) {

        }

        @Override
        public boolean onDown(MotionEvent e) {
            // 单击，触摸屏按下时立刻触发
            Log.d("Bill", "onDown");
            isLongTouch = false;
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            // 短按，触摸屏按下后片刻后抬起，会触发这个手势，如果迅速抬起则不会
            Log.d("Bill", "onShowPress");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // 抬起，手指离开触摸屏时触发(长按、滚动、滑动时，不会触发这个手势)
            Log.d("Bill", "onSingleTapUp");
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // 滚动，触摸屏按下后移动
            Log.d("Bill", "onScroll");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // 长按，触摸屏按下后既不抬起也不移动，过一段时间后触发
            Log.d("Bill", "onLongPress");
            isLongTouch = true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // 滑动，触摸屏按下后快速移动并抬起，会先触发滚动手势，跟着触发一个滑动手势
            Log.d("Bill", "onFling");
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // 双击，手指在触摸屏上迅速点击第二下时触发
            Log.d("Bill", "onDoubleTap");
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            // 双击的按下跟抬起各触发一次
            Log.d("Bill", "onDoubleTapEvent");
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // 单击确认，即很快的按下并抬起，但并不连续点击第二下
            Log.d("Bill", "onSingleTapConfirmed");
            return false;
        }

    }

}
