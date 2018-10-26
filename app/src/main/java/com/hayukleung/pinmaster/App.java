package com.hayukleung.pinmaster;

import android.app.Application;

import com.hayukleung.pinmaster.util.ScreenUtil;

/**
 * Created by hayukleung@gmail.com on 2018/10/26.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ScreenUtil.init(getApplicationContext());
    }
}
