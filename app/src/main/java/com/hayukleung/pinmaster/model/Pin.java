package com.hayukleung.pinmaster.model;

import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * Created by hayukleung@gmail.com on 2018/10/26.
 */

public class Pin implements Comparable<Pin> {

    private final float angle;

    public Pin(float angle) {
        this.angle = angle;
    }

    public float getAngle() {
        return angle;
    }

    public int deltaX100(Pin pin) {
        return Math.abs((int) ((this.angle - pin.angle) * 100f));
    }

    @Override
    public int compareTo(@NonNull Pin o) {
        return (int) ((this.angle - o.angle) * 100f);
    }

    @Override
    public int hashCode() {
        return String.format(Locale.CHINA, "%.2f", this.angle).hashCode();
    }
}
