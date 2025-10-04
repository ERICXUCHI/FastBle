package com.clj.blesample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class MainActivityTest extends Activity {
    private static final String TAG = "MainActivityTest";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivityTest onCreate被调用");
        
        Button button = new Button(this);
        button.setText("测试MainActivity");
        button.setOnClickListener(v -> {
            Toast.makeText(this, "MainActivityTest正常工作", Toast.LENGTH_SHORT).show();
        });
        
        setContentView(button);
        
        Toast.makeText(this, "MainActivityTest已创建", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivityTest onResume被调用");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivityTest onDestroy被调用");
    }
}
