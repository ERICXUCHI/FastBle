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
        
        Button button = new Button(this);
        button.setText("测试按钮");
        button.setOnClickListener(v -> {
            Toast.makeText(this, "按钮点击正常", Toast.LENGTH_SHORT).show();
        });
        
        setContentView(button);
        
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
