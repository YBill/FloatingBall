package com.floatingball;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.activity_main);
//        FloatBallView view = new FloatBallView(this);
//        viewGroup.addView(view);

        Intent intent = new Intent(MainActivity.this, FloatBallService.class);
        Bundle data = new Bundle();
        data.putInt("type", FloatBallService.TYPE_ADD);
        intent.putExtras(data);
        startService(intent);

    }
}
