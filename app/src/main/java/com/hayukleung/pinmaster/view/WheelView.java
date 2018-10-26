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

import com.hayukleung.pinmaster.model.Pin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 转轮
 * <p>
 * Created by hayukleung@gmail.com on 2018/10/25.
 */
public class WheelView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final float DELTA_ANGLE = 1f;
    private static final int MIN_DELTA_ANGLE = 300;
    private final List<Pin> mPinList = new ArrayList<>();
    private Paint mPaint;
    private RectF mRectF;
    private float mCurrentAngle = 0f;
    private SurfaceHolder mSurfaceHolder;
    private boolean mDrawing = false;
    private float mPinWidth;
    private float mPinHeight;
    private ResultCallback mResultCallback;

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public void setResultCallback(@NonNull ResultCallback resultCallback) {
        mResultCallback = resultCallback;
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
        start();
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
            clearPath(canvas);
            drawWheel(canvas);
            for (Pin pin : mPinList) {
                drawPin(canvas, pin.getAngle());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void clearPath(Canvas canvas) {
        // 清除缓存
        mRectF.set(0, 0, getWidth(), getHeight());
        mPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        canvas.drawRect(mRectF, mPaint);
    }

    /**
     * 绘制轮盘
     *
     * @param canvas
     */
    private void drawWheel(Canvas canvas) {

        // 转盘半径
        float radiusWheel = getWidth() * 0.8f / 2f;
        // 区域半径
        float radiusRegion = getWidth() / 2f;
        float radiusDelta = radiusRegion - radiusWheel;

        // 子弹高度
        mPinHeight = radiusDelta;
        // 子弹宽度
        mPinWidth = mPinHeight / 2f;

        mRectF.set(radiusDelta, radiusDelta, getWidth() - radiusDelta, getWidth() - radiusDelta);

        mPaint.setColor(Color.RED);
        canvas.drawArc(mRectF, 0f + currentAngle(), 90f, true, mPaint);
        canvas.drawArc(mRectF, 180f + currentAngle(), 90f, true, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawArc(mRectF, 90f + currentAngle(), 90f, true, mPaint);
        canvas.drawArc(mRectF, 270f + currentAngle(), 90f, true, mPaint);

        next();
    }

    private void drawPin(Canvas canvas, float angle) {
        // 区域半径
        float radiusRegion = getWidth() / 2f;

        int count = canvas.save();
        mRectF.set(radiusRegion - mPinWidth / 2f, getHeight() - mPinHeight, radiusRegion + mPinWidth / 2f, getHeight());
        mPaint.setColor(Color.BLACK);
        canvas.rotate(currentAngle() - angle, getWidth() / 2f, getHeight() / 2f);
        canvas.drawRect(mRectF, mPaint);
        canvas.restoreToCount(count);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = wSize < hSize ? wSize : hSize;
        int wMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        int hMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        setMeasuredDimension(wMeasureSpec, hMeasureSpec);
    }

    public void hit() {
        Pin pin = new Pin(currentAngle());
        if (mPinList.contains(pin)) {
            onFail();
            return;
        }
        mPinList.add(pin);
        int size = mPinList.size();
        if (1 == size) {
            onContinue();
            return;
        }
        Collections.sort(mPinList);
        int index = mPinList.indexOf(pin);
        int delta;
        // 前一个
        delta = pin.deltaX100(mPinList.get((size + index - 1) % size));
        if (delta < MIN_DELTA_ANGLE) {
            onFail();
            return;
        }
        // 后一个
        delta = pin.deltaX100(mPinList.get((index + 1) % size));
        if (delta < MIN_DELTA_ANGLE) {
            onFail();
        } else {
            onContinue();
        }
    }

    private void onFail() {
        if (null != mResultCallback) {
            mResultCallback.onFail(mPinList.size());
        }
        mDrawing = false;
    }

    private void onContinue() {
        if (null != mResultCallback) {
            mResultCallback.onContinue(mPinList.size());
        }
    }

    public void start() {
        mPinList.clear();
        mDrawing = true;
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(this);
    }

    public void clearPath() {
        mDrawing = false;
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas();
                    clearPath(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        });
    }

    private float currentAngle() {
        return mCurrentAngle;
    }

    private void next() {
        mCurrentAngle += DELTA_ANGLE;
        mCurrentAngle = mCurrentAngle % 360f;
    }

    public float getPinHeight() {
        return mPinHeight;
    }

    public float getPinWidth() {
        return mPinWidth;
    }
}
