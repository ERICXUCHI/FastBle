package com.clj.blesample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class MainActivitySimple extends Activity {
    private static final String TAG = "MainActivitySimple";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivitySimple onCreate被调用");
        
        Button testButton = new Button(this);
        testButton.setText("测试按钮");
        testButton.setOnClickListener(v -> {
            Toast.makeText(this, "按钮点击正常", Toast.LENGTH_SHORT).show();
        });
        
        Button mainButton = new Button(this);
        mainButton.setText("启动真正的MainActivity");
        mainButton.setOnClickListener(v -> {
            Log.d(TAG, "启动MainActivity");
            android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
            startActivity(intent);
        });
        
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.addView(testButton);
        layout.addView(mainButton);
        
        setContentView(layout);
        
        Toast.makeText(this, "MainActivitySimple已创建", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivitySimple onResume被调用");
        Toast.makeText(this, "MainActivitySimple onResume", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivitySimple onDestroy被调用");
    }
}
