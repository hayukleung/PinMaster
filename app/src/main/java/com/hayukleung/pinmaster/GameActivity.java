package com.hayukleung.pinmaster;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.hayukleung.pinmaster.view.WheelView;

/**
 * Created by hayukleung@gmail.com on 2018/10/25.
 */

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        final WheelView wheelView = findViewById(R.id.wheel_view);
        AppCompatButton button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.shoot();
            }
        });
    }
}
