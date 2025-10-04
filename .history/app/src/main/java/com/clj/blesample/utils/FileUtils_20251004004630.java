package com.clj.blesample.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件工具类，用于保存BLE数据到文件
 */
public class FileUtils {
    
    private static final String TAG = "FileUtils";
    private static final String FOLDER_NAME = "FastBleData";
    
    /**
     * 保存数据到文件
     * @param context 上下文
     * @param data 要保存的数据
     * @param deviceName 设备名称
     * @param characteristicUuid 特征UUID
     */
    public static void saveDataToFile(Context context, String data, String deviceName, String characteristicUuid) {
        Log.d(TAG, "=== 开始保存数据到文件 ===");
        Log.d(TAG, "Context: " + (context != null ? "有效" : "无效"));
        Log.d(TAG, "数据: " + data);
        Log.d(TAG, "设备名: " + deviceName);
        Log.d(TAG, "特征UUID: " + characteristicUuid);
        
        if (context == null) {
            Log.e(TAG, "Context为null，无法保存文件");
            return;
        }
        
        // 尝试多种保存方式
        boolean saved = false;
        
        // 方式1: 使用公共存储目录（用户可见）
        try {
            File publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            Log.d(TAG, "方式1 - 公共下载目录: " + publicDir.getAbsolutePath());
            
            File dataDir = new File(publicDir, FOLDER_NAME);
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                Log.d(TAG, "创建公共目录: " + created);
            }
            
            String fileName = "BLE_Data_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".txt";
            File file = new File(dataDir, fileName);
            FileWriter writer = new FileWriter(file, true);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String logEntry = String.format("[%s] Device:%s UUID:%s Data:%s\n", 
                timestamp, deviceName, characteristicUuid, data);
            writer.write(logEntry);
            writer.flush();
            writer.close();
            
            Log.d(TAG, "方式1成功 - 文件路径: " + file.getAbsolutePath());
            Log.d(TAG, "文件大小: " + file.length() + " bytes");
            Log.d(TAG, "文件用户可见路径: /storage/emulated/0/Download/FastBleData/" + fileName);
            saved = true;
            
        } catch (Exception e) {
            Log.e(TAG, "方式1失败: " + e.getMessage());
        }
        
        // 方式2: 使用Documents目录（用户可见）
        if (!saved) {
            try {
                File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                Log.d(TAG, "方式2 - 文档目录: " + documentsDir.getAbsolutePath());
                
                File dataDir = new File(documentsDir, FOLDER_NAME);
                if (!dataDir.exists()) {
                    boolean created = dataDir.mkdirs();
                    Log.d(TAG, "创建文档目录: " + created);
                }
                
                String fileName = "BLE_Data_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".txt";
                File file = new File(dataDir, fileName);
                FileWriter writer = new FileWriter(file, true);
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                String logEntry = String.format("[%s] Device:%s UUID:%s Data:%s\n", 
                    timestamp, deviceName, characteristicUuid, data);
                writer.write(logEntry);
                writer.flush();
                writer.close();
                
                Log.d(TAG, "方式2成功 - 文件路径: " + file.getAbsolutePath());
                Log.d(TAG, "文件大小: " + file.length() + " bytes");
                Log.d(TAG, "文件用户可见路径: /storage/emulated/0/Documents/FastBleData/" + fileName);
                saved = true;
                
            } catch (Exception e) {
                Log.e(TAG, "方式2失败: " + e.getMessage());
            }
        }
        
        // 方式3: 使用getExternalFilesDir()（应用专用但可能可见）
        if (!saved) {
            try {
                File externalDir = context.getExternalFilesDir(null);
                if (externalDir != null) {
                    Log.d(TAG, "方式3 - 外部文件目录: " + externalDir.getAbsolutePath());
                    
                    File dataDir = new File(externalDir, FOLDER_NAME);
                    if (!dataDir.exists()) {
                        boolean created = dataDir.mkdirs();
                        Log.d(TAG, "创建外部目录: " + created);
                    }
                    
                    String fileName = "BLE_Data_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".txt";
                    File file = new File(dataDir, fileName);
                    FileWriter writer = new FileWriter(file, true);
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    String logEntry = String.format("[%s] Device:%s UUID:%s Data:%s\n", 
                        timestamp, deviceName, characteristicUuid, data);
                    writer.write(logEntry);
                    writer.flush();
                    writer.close();
                    
                    Log.d(TAG, "方式3成功 - 文件路径: " + file.getAbsolutePath());
                    Log.d(TAG, "文件大小: " + file.length() + " bytes");
                    Log.d(TAG, "文件用户可见路径: /storage/emulated/0/Android/data/com.clj.blesample/files/FastBleData/" + fileName);
                    saved = true;
                }
            } catch (Exception e) {
                Log.e(TAG, "方式3失败: " + e.getMessage());
            }
        }
        
        // 最后尝试使用SharedPreferences作为备选方案
        if (!saved) {
            try {
                android.content.SharedPreferences prefs = context.getSharedPreferences("BLE_DATA", Context.MODE_PRIVATE);
                android.content.SharedPreferences.Editor editor = prefs.edit();
                
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                String key = "ble_data_" + System.currentTimeMillis();
                String value = String.format("[%s] Device:%s UUID:%s Data:%s", 
                    timestamp, deviceName, characteristicUuid, data);
                
                editor.putString(key, value);
                boolean prefSaved = editor.commit();
                
                if (prefSaved) {
                    Log.d(TAG, "SharedPreferences保存成功: " + key);
                    saved = true;
                } else {
                    Log.e(TAG, "SharedPreferences保存失败");
                }
            } catch (Exception e) {
                Log.e(TAG, "SharedPreferences保存异常: " + e.getMessage());
            }
        }
        
        if (saved) {
            Log.d(TAG, "=== 数据保存成功 ===");
            Log.d(TAG, "用户可以在以下位置找到文件：");
            Log.d(TAG, "1. 下载文件夹: /storage/emulated/0/Download/FastBleData/");
            Log.d(TAG, "2. 文档文件夹: /storage/emulated/0/Documents/FastBleData/");
            Log.d(TAG, "3. 应用文件夹: /storage/emulated/0/Android/data/com.clj.blesample/files/FastBleData/");
        } else {
            Log.e(TAG, "=== 所有保存方式都失败了 ===");
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
    
    /**
     * 测试文件保存功能
     * @param context 上下文
     */
    public static void testFileSave(Context context) {
        Log.d(TAG, "=== 开始测试文件保存功能 ===");
        
        // 测试SharedPreferences
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("BLE_TEST", Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();
            editor.putString("test_data", "TEST_SHARED_PREFS");
            editor.putLong("test_time", System.currentTimeMillis());
            boolean saved = editor.commit();
            Log.d(TAG, "SharedPreferences测试: " + (saved ? "成功" : "失败"));
        } catch (Exception e) {
            Log.e(TAG, "SharedPreferences测试失败: " + e.getMessage());
        }
        
        // 测试基本文件保存
        saveDataToFile(context, "TEST_DATA_01_02_03_04", "TestDevice", "test-uuid-123");
        
        // 尝试最简单的方式（使用公共存储）
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File simpleFile = new File(downloadsDir, "BLE_Simple_Test.txt");
            java.io.FileWriter writer = new java.io.FileWriter(simpleFile);
            writer.write("Simple test - " + new java.util.Date().toString() + "\n");
            writer.close();
            
            Log.d(TAG, "简单文件测试成功: " + simpleFile.getAbsolutePath());
            Log.d(TAG, "文件存在: " + simpleFile.exists());
            Log.d(TAG, "文件大小: " + simpleFile.length() + " bytes");
            Log.d(TAG, "用户可见路径: /storage/emulated/0/Download/BLE_Simple_Test.txt");
            
        } catch (Exception e) {
            Log.e(TAG, "简单文件测试失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        Log.d(TAG, "=== 文件保存测试完成 ===");
    }
    
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
