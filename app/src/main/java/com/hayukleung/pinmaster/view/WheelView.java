package com.hayukleung.pinmaster.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 转轮
 * <p>
 * Created by hayukleung@gmail.com on 2018/10/25.
 */
public class WheelView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final float DELTA_ANGLE = 3f;
    private static final int PIN_STEP_MAX = 20;
    private Paint mPaint;
    private RectF mRectF;
    private float mCurrentAngle = 0f;
    private SurfaceHolder mSurfaceHolder;
    private boolean mDrawing = false;
    private boolean mShooting = false;
    private int mPinStep = 0;

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
            drawWheel(canvas);
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
     * 绘制轮盘
     *
     * @param canvas
     */
    private void drawWheel(Canvas canvas) {
        // int count = canvas.save();
        mRectF.set(getWidth() * 0.48f, getWidth() * 0.9f, getWidth() * 0.52f, getWidth() * 0.9f + getHeight() * 0.05f);
        mPaint.setColor(Color.BLACK);
        // canvas.rotate(-currentAngle());
        canvas.drawRect(mRectF, mPaint);
        // canvas.restoreToCount(count);

        mRectF.set(getWidth() * 0.1f, getWidth() * 0.1f, getWidth() * 0.9f, getWidth() * 0.9f);
        mPaint.setColor(Color.RED);
        canvas.drawArc(mRectF, 0f + currentAngle(), 90f, true, mPaint);
        canvas.drawArc(mRectF, 180f + currentAngle(), 90f, true, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawArc(mRectF, 90f + currentAngle(), 90f, true, mPaint);
        canvas.drawArc(mRectF, 270f + currentAngle(), 90f, true, mPaint);

        next();
    }

    /**
     * 绘制子弹
     *
     * @param canvas
     */
    private void drawPin(Canvas canvas) {

        // 清除子弹路径
        mRectF.set(getWidth() * 0.48f, getHeight() * 0.5f, getWidth() * 0.52f, getHeight());
        mPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        canvas.drawRect(mRectF, mPaint);

        // 子弹高度 5% getHeight
        // 子弹宽度 4% getWidth
        if (mShooting) {
            if (PIN_STEP_MAX == mPinStep) {
                // 射击完成
                mPinStep = 0;
                mShooting = false;
                return;
            }
            float stepLength = getHeight() * 0.45f / PIN_STEP_MAX;
            mRectF.set(getWidth() * 0.48f, getHeight() * 0.95f - stepLength * mPinStep, getWidth() * 0.52f, getHeight() - stepLength * mPinStep);
            mPaint.setColor(Color.BLACK);
            canvas.drawRect(mRectF, mPaint);
            mPinStep++;
        } else {
            mRectF.set(getWidth() * 0.48f, getHeight() * 0.95f, getWidth() * 0.52f, getHeight());
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
        int hMeasureSpec = MeasureSpec.makeMeasureSpec(size * 2, MeasureSpec.EXACTLY);
        setMeasuredDimension(wMeasureSpec, hMeasureSpec);
    }

    private float currentAngle() {
        return mCurrentAngle;
    }

    private void next() {
        mCurrentAngle += DELTA_ANGLE;
        mCurrentAngle = mCurrentAngle % 360f;
    }

    public void shoot() {
        if (mShooting) {
            return;
        }
        mShooting = true;
        mPinStep = 0;
    }

    /**
     * 没卵用
     *
     * @param rect
     */
    private void clearCanvas(Rect rect) {

        Canvas canvas = null;
        try {
            canvas = mSurfaceHolder.lockCanvas(rect);
            canvas.drawColor(Color.WHITE);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
