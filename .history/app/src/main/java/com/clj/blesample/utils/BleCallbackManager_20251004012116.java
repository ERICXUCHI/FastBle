package com.clj.blesample.utils;

import android.content.Context;
import android.util.Log;

import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * BLE回调管理器，用于在Fragment销毁后继续处理BLE数据
 */
public class BleCallbackManager {
    private static final String TAG = "BleCallbackManager";
    private static BleCallbackManager instance;
    private Context applicationContext;
    private Map<String, BleDevice> activeNotifications = new HashMap<>();
    
    private BleCallbackManager() {}
    
    public static BleCallbackManager getInstance() {
        if (instance == null) {
            instance = new BleCallbackManager();
        }
        return instance;
    }
    
    public void init(Context context) {
        this.applicationContext = context.getApplicationContext();
    }
    
    /**
     * 添加活跃的通知
     */
    public void addActiveNotification(String deviceKey, BleDevice device) {
        activeNotifications.put(deviceKey, device);
        Log.d(TAG, "添加活跃通知: " + deviceKey + " 总数: " + activeNotifications.size());
    }
    
    /**
     * 移除活跃的通知
     */
    public void removeActiveNotification(String deviceKey) {
        activeNotifications.remove(deviceKey);
        Log.d(TAG, "移除活跃通知: " + deviceKey + " 剩余: " + activeNotifications.size());
    }
    
    /**
     * 检查是否有活跃的通知
     */
    public boolean hasActiveNotifications() {
        return !activeNotifications.isEmpty();
    }
    
    /**
     * 获取活跃通知数量
     */
    public int getActiveNotificationCount() {
        return activeNotifications.size();
    }
    
    /**
     * 获取活跃通知状态描述
     */
    public String getActiveNotificationStatus() {
        if (activeNotifications.isEmpty()) {
            return "没有活跃的通知";
        } else {
            return "有 " + activeNotifications.size() + " 个活跃的通知";
        }
    }
    
    /**
     * 创建Notify回调
     */
    public BleNotifyCallback createNotifyCallback(BleDevice device, String serviceUuid, String characteristicUuid) {
        String deviceKey = device.getKey();
        addActiveNotification(deviceKey, device);
        
        return new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {
                Log.d(TAG, "Notify成功: " + deviceKey);
            }
            
            @Override
            public void onNotifyFailure(BleException exception) {
                Log.e(TAG, "Notify失败: " + deviceKey + " " + exception.toString());
                removeActiveNotification(deviceKey);
            }
            
            @Override
            public void onCharacteristicChanged(byte[] data) {
                // 处理接收到的数据
                String hexData = HexUtil.formatHexString(data, true);
                String deviceName = device.getName() != null ? device.getName() : device.getMac();
                
                Log.d(TAG, "收到Notify数据: " + hexData + " 设备: " + deviceName);
                
                // 保存数据到文件
                FileUtils.saveDataToFile(applicationContext, hexData, deviceName, characteristicUuid);
                
                Log.e("BLE_FORCE_LOG", "强制日志 - Notify数据: " + hexData);
            }
        };
    }
    
    /**
     * 创建Indicate回调
     */
    public BleIndicateCallback createIndicateCallback(BleDevice device, String serviceUuid, String characteristicUuid) {
        String deviceKey = device.getKey();
        addActiveNotification(deviceKey, device);
        
        return new BleIndicateCallback() {
            @Override
            public void onIndicateSuccess() {
                Log.d(TAG, "Indicate成功: " + deviceKey);
            }
            
            @Override
            public void onIndicateFailure(BleException exception) {
                Log.e(TAG, "Indicate失败: " + deviceKey + " " + exception.toString());
                removeActiveNotification(deviceKey);
            }
            
            @Override
            public void onCharacteristicChanged(byte[] data) {
                // 处理接收到的数据
                String hexData = HexUtil.formatHexString(data, true);
                String deviceName = device.getName() != null ? device.getName() : device.getMac();
                
                Log.d(TAG, "收到Indicate数据: " + hexData + " 设备: " + deviceName);
                
                // 保存数据到文件
                FileUtils.saveDataToFile(applicationContext, hexData, deviceName, characteristicUuid);
                
                Log.e("BLE_FORCE_LOG", "强制日志 - Indicate数据: " + hexData);
            }
        };
    }
    
    /**
     * 清除所有活跃通知
     */
    public void clearAllActiveNotifications() {
        activeNotifications.clear();
        Log.d(TAG, "清除所有活跃通知");
    }
}
