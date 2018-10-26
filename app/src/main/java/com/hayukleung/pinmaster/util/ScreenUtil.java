package com.hayukleung.pinmaster.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by hayukleung@gmail.com on 2018/10/26.
 */

public class ScreenUtil {

    /**
     * 竖屏
     */
    private final static int ORIENTATION_VERTICAL = 0x0001;

    /**
     * 横屏
     */
    private final static int ORIENTATION_HORIZONTAL = 0x0002;

    private static ScreenUtil singleInstance;

    /**
     * 宽
     */
    private int widthPx;

    /**
     * 高
     */
    private int heightPx;

    /**
     * 密度dpi
     */
    private int densityDpi;

    /**
     * 缩放系数 densityDpi / 160
     */
    private float densityScale;

    /**
     * 文字缩放系数
     */
    private float fontScale;

    /**
     * 屏幕朝向
     */
    private int orientation;

    /**
     * 私有构造方法
     *
     * @param context
     */
    private ScreenUtil(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        this.widthPx = metrics.widthPixels;
        this.heightPx = metrics.heightPixels;
        this.densityDpi = metrics.densityDpi;
        this.densityScale = metrics.density;
        this.fontScale = metrics.scaledDensity;
        this.orientation = heightPx > widthPx ? ORIENTATION_VERTICAL : ORIENTATION_HORIZONTAL;
    }

    /**
     * 获取实例
     *
     * @param context
     * @return
     */
    public static void init(Context context) {
        if (singleInstance == null) {
            singleInstance = new ScreenUtil(context);
        }
    }

    /**
     * 根据设备屏幕密度将px转换为dp
     *
     * @param valuePx
     * @return
     */
    public static int px2dp(float valuePx) {
        return (int) (valuePx / singleInstance.densityScale + 0.5f);
    }

    /**
     * 根据设备屏幕密度将dp转换为px
     *
     * @param valueDp
     * @return
     */
    public static int dp2px(float valueDp) {
        return (int) (valueDp * singleInstance.densityScale + 0.5f);
    }

    /**
     * 根据设备屏幕密度将px转换为sp
     *
     * @param valuePx
     * @return
     */
    public static int px2sp(float valuePx) {
        return (int) (valuePx / singleInstance.fontScale + 0.5f);
    }

    /**
     * 根据设备屏幕密度将sp转换为px
     *
     * @param valueSp
     * @return
     */
    public static int sp2px(float valueSp) {
        return (int) (valueSp * singleInstance.fontScale + 0.5f);
    }

    /**
     * 将px值转换为dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @param scale   （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dp(float pxValue, float scale) {
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dp值转换为px值，保证尺寸大小不变
     *
     * @param dpValue
     * @param scale   （DisplayMetrics类中属性density）
     * @return
     */
    public static int dp2px(float dpValue, float scale) {
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue, float fontScale) {
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float spValue, float fontScale) {
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        return singleInstance.widthPx;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        return singleInstance.heightPx;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;

        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }
}
