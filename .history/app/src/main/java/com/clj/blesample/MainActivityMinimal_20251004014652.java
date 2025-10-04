package com.clj.blesample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class MainActivityMinimal extends Activity {
    private static final String TAG = "MainActivityMinimal";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivityMinimal onCreate被调用");
        
        Button testButton = new Button(this);
        testButton.setText("测试按钮");
        testButton.setOnClickListener(v -> {
            Toast.makeText(this, "最小化MainActivity正常工作", Toast.LENGTH_SHORT).show();
        });
        
        Button simpleButton = new Button(this);
        simpleButton.setText("启动MainActivitySimple");
        simpleButton.setOnClickListener(v -> {
            Log.d(TAG, "启动MainActivitySimple");
            android.content.Intent intent = new android.content.Intent(this, MainActivitySimple.class);
            startActivity(intent);
        });
        
        Button mainButton = new Button(this);
        mainButton.setText("启动MainActivity(简化版)");
        mainButton.setOnClickListener(v -> {
            Log.d(TAG, "启动MainActivity");
            android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
            startActivity(intent);
        });
        
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.addView(testButton);
        layout.addView(simpleButton);
        layout.addView(mainButton);
        
        setContentView(layout);
        
        Toast.makeText(this, "MainActivityMinimal已创建", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivityMinimal onResume被调用");
        Toast.makeText(this, "MainActivityMinimal onResume", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivityMinimal onDestroy被调用");
    }
}
