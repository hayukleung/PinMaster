package com.hayukleung.pinmaster.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 子弹路径
 * <p>
 * Created by hayukleung@gmail.com on 2018/10/25.
 */
public class PinPathView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final float DELTA_ANGLE = 3f;
    private static final int PIN_STEP_MAX = 20;
    private Paint mPaint;
    private RectF mRectF;
    private float mCurrentAngle = 0f;
    private SurfaceHolder mSurfaceHolder;
    private boolean mDrawing = false;
    private boolean mShooting = false;
    private int mPinStep = 0;
    private float mPinWidth;
    private float mPinHeight;

    private HitCallback mHitCallback;

    public PinPathView(Context context) {
        this(context, null);
    }

    public PinPathView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinPathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public void setHitCallback(@NonNull HitCallback hitCallback) {
        mHitCallback = hitCallback;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectF = new RectF();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mDrawing = true;
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mDrawing = false;
    }

    @Override
    public void run() {
        while (mDrawing) {
            draw();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void draw() {
        Canvas canvas = null;
        try {
            canvas = mSurfaceHolder.lockCanvas();
            drawPin(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * 绘制子弹
     *
     * @param canvas
     */
    private void drawPin(Canvas canvas) {

        // 清除子弹路径
        mRectF.set(getWidth() / 2f - mPinWidth / 2f, 0, getWidth() / 2f + mPinWidth / 2f, getHeight());
        mPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        canvas.drawRect(mRectF, mPaint);

        if (mShooting) {
            if (PIN_STEP_MAX == mPinStep) {
                // 射击完成
                mPinStep = 0;
                mShooting = false;
                if (null != mHitCallback) {
                    mHitCallback.onHit();
                }
                return;
            }
            float stepLength = getHeight() / PIN_STEP_MAX;
            mRectF.set(getWidth() / 2f - mPinWidth / 2f, getHeight() - mPinHeight - stepLength * mPinStep, getWidth() / 2f + mPinWidth / 2f, getHeight() - stepLength * mPinStep);
            mPaint.setColor(Color.BLACK);
            canvas.drawRect(mRectF, mPaint);
            mPinStep++;
        } else {
            mRectF.set(getWidth() / 2f - mPinWidth / 2f, getHeight() - mPinHeight, getWidth() / 2f + mPinWidth / 2f, getHeight());
            mPaint.setColor(Color.BLACK);
            canvas.drawRect(mRectF, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = wSize < hSize ? wSize : hSize;
        int wMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        int hMeasureSpec = MeasureSpec.makeMeasureSpec((int) (size * 1.5f), MeasureSpec.EXACTLY);
        setMeasuredDimension(wMeasureSpec, hMeasureSpec);
    }

    public void shoot() {
        if (mShooting) {
            return;
        }
        mShooting = true;
        mPinStep = 0;
    }

    public void setPinHeight(float pinHeight) {
        mPinHeight = pinHeight;
    }

    public void setPinWidth(float pinWidth) {
        mPinWidth = pinWidth;
    }
}
