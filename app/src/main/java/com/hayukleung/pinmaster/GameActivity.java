package com.hayukleung.pinmaster;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.hayukleung.pinmaster.util.DialogUtil;
import com.hayukleung.pinmaster.view.HitCallback;
import com.hayukleung.pinmaster.view.PinPathView;
import com.hayukleung.pinmaster.view.ResultCallback;
import com.hayukleung.pinmaster.view.WheelView;

import java.util.Locale;

/**
 * Created by hayukleung@gmail.com on 2018/10/25.
 */

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        final AppCompatTextView scoreTextView = findViewById(R.id.score);
        scoreTextView.setText(String.format(Locale.CHINA, "%d 分", 0));
        final WheelView wheelView = findViewById(R.id.wheel_view);
        final PinPathView pinPathView = findViewById(R.id.pin_path_view);
        final AppCompatButton button = findViewById(R.id.button);
        wheelView.setResultCallback(new ResultCallback() {
            @Override
            public void onContinue(int score) {
                scoreTextView.setText(String.format(Locale.CHINA, "%d 分", score));
            }

            @Override
            public void onFail(int score) {
                button.setEnabled(false);
                pinPathView.clearPath();
                // wheelView.clearPath();
                DialogUtil.show2("fail", getSupportFragmentManager(), "游戏结束", "再来一次？", "退出", "再来一次", new DialogUtil.Listener2() {
                    @Override
                    public void onNegativeClick() {
                        onBackPressed();
                    }

                    @Override
                    public void onPositiveClick() {
                        scoreTextView.setText(String.format(Locale.CHINA, "%d 分", 0));
                        wheelView.start();
                        pinPathView.start();
                        button.setEnabled(true);
                    }
                });
                scoreTextView.setText(String.format(Locale.CHINA, "%d 分", score));
            }
        });

        pinPathView.setHitCallback(new HitCallback() {
            @Override
            public void onHit() {
                wheelView.hit();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinPathView.setPinHeight(wheelView.getPinHeight());
                pinPathView.setPinWidth(wheelView.getPinWidth());
                pinPathView.shoot();
            }
        });
    }
}
