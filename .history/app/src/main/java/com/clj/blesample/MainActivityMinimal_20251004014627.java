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
        
        Button button = new Button(this);
        button.setText("最小化MainActivity测试");
        button.setOnClickListener(v -> {
            Toast.makeText(this, "最小化MainActivity正常工作", Toast.LENGTH_SHORT).show();
        });
        
        setContentView(button);
        
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
