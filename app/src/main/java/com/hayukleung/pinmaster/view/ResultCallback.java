package com.hayukleung.pinmaster.view;

import android.support.annotation.UiThread;

/**
 * Created by hayukleung@gmail.com on 2018/10/26.
 */

public interface ResultCallback {

    @UiThread
    void onContinue(int score);

    @UiThread
    void onFail(int score);
}
