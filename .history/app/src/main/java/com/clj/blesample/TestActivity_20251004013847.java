package com.clj.blesample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class TestActivity extends Activity {
    private static final String TAG = "TestActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "TestActivity onCreate被调用");
        
        Button button = new Button(this);
        button.setText("返回MainActivity");
        button.setOnClickListener(v -> {
            Log.d(TAG, "点击返回按钮");
            finish();
        });
        
        setContentView(button);
        
        Toast.makeText(this, "TestActivity已创建", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "TestActivity onDestroy被调用");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "TestActivity onResume被调用");
    }
}
