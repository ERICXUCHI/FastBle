package com.clj.blesample.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 文件工具类，用于保存BLE数据到文件
 */
public class FileUtils {
    
    private static final String TAG = "FileUtils";
    private static final String FOLDER_NAME = "FastBleData";
    
    // 会话相关变量 - 为每个设备维护独立的会话
    private static Map<String, String> deviceSessionFiles = new HashMap<>();
    private static Map<String, String> deviceSessionTimes = new HashMap<>();
    private static Map<String, Boolean> deviceSessionActive = new HashMap<>();
    
    /**
     * 开始新的数据收集会话
     * @param context 上下文
     * @return 会话文件路径
     */
    public static String startNewSession(Context context) {
        try {
            // 生成会话时间戳
            currentSessionTime = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            
            // 创建下载目录
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File dataDir = new File(downloadsDir, FOLDER_NAME);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            
            // 创建会话文件
            String fileName = "BLE_Session_" + currentSessionTime + ".txt";
            currentSessionFile = new File(dataDir, fileName).getAbsolutePath();
            
            // 写入会话开始信息
            FileWriter writer = new FileWriter(currentSessionFile);
            String sessionStart = String.format("=== BLE数据收集会话开始 ===\n");
            sessionStart += String.format("会话时间: %s\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            sessionStart += String.format("文件路径: %s\n", currentSessionFile);
            sessionStart += String.format("=====================================\n\n");
            writer.write(sessionStart);
            writer.flush();
            writer.close();
            
            sessionActive = true;
            Log.d(TAG, "新会话开始: " + currentSessionFile);
            return currentSessionFile;
            
        } catch (Exception e) {
            Log.e(TAG, "开始新会话失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 保存数据到文件
     * @param context 上下文
     * @param data 要保存的数据
     * @param deviceName 设备名称
     * @param characteristicUuid 特征UUID
     */
    public static void saveDataToFile(Context context, String data, String deviceName, String characteristicUuid) {
        Log.d(TAG, "保存数据到会话文件");
        Log.d(TAG, "数据: " + data);
        Log.d(TAG, "设备名: " + deviceName);
        Log.d(TAG, "特征UUID: " + characteristicUuid);
        
        if (context == null) {
            Log.e(TAG, "Context为null，无法保存文件");
            return;
        }
        
        // 检查是否有活跃的BLE通知（通过Fragment静态变量）
        boolean hasActiveNotification = com.clj.blesample.operation.CharacteristicOperationFragment.isNotificationActive() || 
                                       com.clj.blesample.operation.CharacteristicOperationFragment.isIndicateActive();
        
        // 如果有活跃的通知但没有会话文件，自动创建会话
        if (hasActiveNotification && currentSessionFile == null) {
            Log.d(TAG, "检测到活跃通知但无会话文件，自动创建会话");
            startNewSession(context);
        }
        
        // 如果没有活跃通知或会话文件，跳过保存
        if (!hasActiveNotification && !sessionActive) {
            Log.d(TAG, "无活跃通知且会话未活跃，跳过数据保存");
            return;
        }
        
        // 如果还是没有会话文件，使用备选方案
        if (currentSessionFile == null) {
            Log.e(TAG, "无法创建会话文件，使用SharedPreferences备选方案");
            saveToSharedPreferences(context, data, deviceName, characteristicUuid);
            return;
        }
        
        try {
            // 追加数据到当前会话文件
            FileWriter writer = new FileWriter(currentSessionFile, true);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String logEntry = String.format("[%s] Device:%s UUID:%s Data:%s\n", 
                timestamp, deviceName, characteristicUuid, data);
            writer.write(logEntry);
            writer.flush();
            writer.close();
            
            Log.d(TAG, "数据已追加到会话文件: " + currentSessionFile);
            Log.d(TAG, "文件大小: " + new File(currentSessionFile).length() + " bytes");
            
        } catch (Exception e) {
            Log.e(TAG, "保存到会话文件失败: " + e.getMessage());
            // 备选方案：保存到SharedPreferences
            saveToSharedPreferences(context, data, deviceName, characteristicUuid);
        }
    }
    
    /**
     * 结束当前数据收集会话
     * @return 会话文件路径
     */
    public static String endCurrentSession() {
        if (currentSessionFile != null) {
            try {
                // 写入会话结束信息
                FileWriter writer = new FileWriter(currentSessionFile, true);
                String sessionEnd = String.format("\n=== BLE数据收集会话结束 ===\n");
                sessionEnd += String.format("结束时间: %s\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                sessionEnd += String.format("会话文件: %s\n", currentSessionFile);
                sessionEnd += String.format("=====================================\n");
                writer.write(sessionEnd);
                writer.flush();
                writer.close();
                
                Log.d(TAG, "会话结束: " + currentSessionFile);
                String endedFile = currentSessionFile;
                currentSessionFile = null;
                currentSessionTime = null;
                sessionActive = false;
                return endedFile;
                
            } catch (Exception e) {
                Log.e(TAG, "结束会话失败: " + e.getMessage());
            }
        }
        return null;
    }
    
    /**
     * 获取当前会话文件路径
     * @return 当前会话文件路径，如果没有则返回null
     */
    public static String getCurrentSessionFile() {
        return currentSessionFile;
    }
    
    /**
     * 备选保存方案：使用SharedPreferences
     */
    private static void saveToSharedPreferences(Context context, String data, String deviceName, String characteristicUuid) {
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("BLE_DATA", Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();
            
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String key = "ble_data_" + System.currentTimeMillis();
            String value = String.format("[%s] Device:%s UUID:%s Data:%s", 
                timestamp, deviceName, characteristicUuid, data);
            
            editor.putString(key, value);
            boolean saved = editor.commit();
            
            if (saved) {
                Log.d(TAG, "SharedPreferences保存成功: " + key);
            } else {
                Log.e(TAG, "SharedPreferences保存失败");
            }
        } catch (Exception e) {
            Log.e(TAG, "SharedPreferences保存异常: " + e.getMessage());
        }
    }
    
    /**
     * 创建数据文件夹
     * @param context 上下文
     * @return 文件夹对象，如果创建失败返回null
     */
    private static File createDataFolder(Context context) {
        Log.d(TAG, "开始创建数据文件夹");
        
        try {
            // 使用应用的外部文件目录（不需要权限）
            File externalFilesDir = context.getExternalFilesDir(FOLDER_NAME);
            Log.d(TAG, "外部文件目录: " + (externalFilesDir != null ? externalFilesDir.getAbsolutePath() : "null"));
            
            if (externalFilesDir != null) {
                if (!externalFilesDir.exists()) {
                    Log.d(TAG, "外部文件目录不存在，尝试创建");
                    if (externalFilesDir.mkdirs()) {
                        Log.d(TAG, "成功创建外部文件目录: " + externalFilesDir.getAbsolutePath());
                        return externalFilesDir;
                    } else {
                        Log.w(TAG, "创建外部文件目录失败");
                    }
                } else {
                    Log.d(TAG, "外部文件目录已存在: " + externalFilesDir.getAbsolutePath());
                    return externalFilesDir;
                }
            }
            
            // 备选方案：使用应用内部文件目录
            File internalFilesDir = new File(context.getFilesDir(), FOLDER_NAME);
            Log.d(TAG, "内部文件目录: " + internalFilesDir.getAbsolutePath());
            
            if (!internalFilesDir.exists()) {
                Log.d(TAG, "内部文件目录不存在，尝试创建");
                if (internalFilesDir.mkdirs()) {
                    Log.d(TAG, "成功创建内部文件目录: " + internalFilesDir.getAbsolutePath());
                    return internalFilesDir;
                } else {
                    Log.w(TAG, "创建内部文件目录失败");
                }
            } else {
                Log.d(TAG, "内部文件目录已存在: " + internalFilesDir.getAbsolutePath());
                return internalFilesDir;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "创建文件夹失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        Log.e(TAG, "所有文件夹创建方案都失败");
        return null;
    }
    
    /**
     * 检查外部存储是否可用
     * @return true如果可用，false如果不可用
     */
    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    
    // /**
    //  * 测试文件保存功能
    //  * @param context 上下文
    //  */
    // public static void testFileSave(Context context) {
    //     Log.d(TAG, "=== 开始测试会话文件保存功能 ===");
        
    //     // 开始新会话
    //     String sessionFile = startNewSession(context);
    //     if (sessionFile != null) {
    //         Log.d(TAG, "测试会话开始: " + sessionFile);
            
    //         // 测试保存多条数据
    //         saveDataToFile(context, "TEST_DATA_01_02_03_04", "TestDevice", "test-uuid-123");
    //         saveDataToFile(context, "TEST_DATA_05_06_07_08", "TestDevice", "test-uuid-456");
    //         saveDataToFile(context, "TEST_DATA_09_0A_0B_0C", "TestDevice", "test-uuid-789");
            
    //         // 结束会话
    //         String endedFile = endCurrentSession();
    //         Log.d(TAG, "测试会话结束: " + endedFile);
    //     }
        
    //     Log.d(TAG, "=== 会话文件保存测试完成 ===");
    // }
    
    /**
     * 读取保存的BLE数据
     * @param context 上下文
     * @return 保存的数据列表
     */
    public static java.util.List<String> getSavedData(Context context) {
        java.util.List<String> dataList = new java.util.ArrayList<>();
        
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("BLE_DATA", Context.MODE_PRIVATE);
            java.util.Map<String, ?> allPrefs = prefs.getAll();
            
            for (java.util.Map.Entry<String, ?> entry : allPrefs.entrySet()) {
                if (entry.getKey().startsWith("ble_data_")) {
                    dataList.add(entry.getValue().toString());
                }
            }
            
            Log.d(TAG, "读取到 " + dataList.size() + " 条保存的数据");
            
        } catch (Exception e) {
            Log.e(TAG, "读取保存数据失败: " + e.getMessage());
        }
        
        return dataList;
    }
    
    /**
     * 清空保存的数据
     * @param context 上下文
     */
    public static void clearSavedData(Context context) {
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("BLE_DATA", Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();
            Log.d(TAG, "已清空保存的数据");
        } catch (Exception e) {
            Log.e(TAG, "清空数据失败: " + e.getMessage());
        }
    }
}
