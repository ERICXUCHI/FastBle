package com.clj.blesample.operation;


import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.clj.blesample.R;
import com.clj.blesample.comm.Observer;
import com.clj.blesample.comm.ObserverManager;
import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

public class OperationActivity extends AppCompatActivity implements Observer {

    public static final String KEY_DATA = "key_data";

    private BleDevice bleDevice;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic characteristic;
    private int charaProp;

    private Toolbar toolbar;
    private List<Fragment> fragments = new ArrayList<>();
    private int currentPage = 0;
    private String[] titles = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_operation);
        initData();
        initView();
        initPage();

        ObserverManager.getInstance().addObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.util.Log.d("OperationActivity", "onDestroy被调用");
        // 完全注释掉BLE相关操作，防止任何可能的冲突
        // BleManager.getInstance().clearCharacterCallback(bleDevice);
        ObserverManager.getInstance().deleteObserver(this);
    }

    @Override
    public void disConnected(BleDevice device) {
        if (device != null && bleDevice != null && device.getKey().equals(bleDevice.getKey())) {
            android.util.Log.d("OperationActivity", "设备断开连接: " + device.getName());
            // 显示Toast提示设备断开，但不退出程序
            android.widget.Toast.makeText(this, "BLE设备已断开连接，但程序继续运行", android.widget.Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentPage != 0) {
                currentPage--;
                changePage(currentPage);
                return true;
            } else {
                android.util.Log.d("OperationActivity", "用户按返回键退出到首页");
                // 使用moveTaskToBack而不是finish，避免Activity栈问题
                moveTaskToBack(true);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(titles[0]);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage != 0) {
                    currentPage--;
                    changePage(currentPage);
                } else {
                    android.util.Log.d("OperationActivity", "用户点击工具栏返回按钮退出到首页");
                    // 显示选择对话框
                    showReturnOptionsDialog();
                }
            }
        });
    }

    private void initData() {
        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        if (bleDevice == null)
            finish();

        titles = new String[]{
                getString(R.string.service_list),
                getString(R.string.characteristic_list),
                getString(R.string.console)};
    }

    private void initPage() {
        prepareFragment();
        changePage(0);
    }

    public void changePage(int page) {
        currentPage = page;
        toolbar.setTitle(titles[page]);
        updateFragment(page);
        if (currentPage == 1) {
            ((CharacteristicListFragment) fragments.get(1)).showData();
        } else if (currentPage == 2) {
            ((CharacteristicOperationFragment) fragments.get(2)).showData();
        }
    }

    private void prepareFragment() {
        fragments.add(new ServiceListFragment());
        fragments.add(new CharacteristicListFragment());
        fragments.add(new CharacteristicOperationFragment());
        for (Fragment fragment : fragments) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).hide(fragment).commit();
        }
    }

    private void updateFragment(int position) {
        if (position > fragments.size() - 1) {
            return;
        }
        for (int i = 0; i < fragments.size(); i++) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = fragments.get(i);
            if (i == position) {
                transaction.show(fragment);
            } else {
                transaction.hide(fragment);
            }
            transaction.commit();
        }
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public BluetoothGattService getBluetoothGattService() {
        return bluetoothGattService;
    }

    public void setBluetoothGattService(BluetoothGattService bluetoothGattService) {
        this.bluetoothGattService = bluetoothGattService;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    public int getCharaProp() {
        return charaProp;
    }

    public void setCharaProp(int charaProp) {
        this.charaProp = charaProp;
    }


}
