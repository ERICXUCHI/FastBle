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
        
        // 方式1: 使用getFilesDir()
        try {
            File internalDir = context.getFilesDir();
            Log.d(TAG, "方式1 - 应用内部目录: " + internalDir.getAbsolutePath());
            
            File dataDir = new File(internalDir, FOLDER_NAME);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            
            File file = new File(dataDir, "BLE_Data.txt");
            FileWriter writer = new FileWriter(file, true);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String logEntry = String.format("[%s] Device:%s UUID:%s Data:%s\n", 
                timestamp, deviceName, characteristicUuid, data);
            writer.write(logEntry);
            writer.flush();
            writer.close();
            
            Log.d(TAG, "方式1成功 - 文件路径: " + file.getAbsolutePath());
            Log.d(TAG, "文件大小: " + file.length() + " bytes");
            saved = true;
            
        } catch (Exception e) {
            Log.e(TAG, "方式1失败: " + e.getMessage());
        }
        
        // 方式2: 使用getCacheDir()
        if (!saved) {
            try {
                File cacheDir = context.getCacheDir();
                Log.d(TAG, "方式2 - 缓存目录: " + cacheDir.getAbsolutePath());
                
                File dataDir = new File(cacheDir, FOLDER_NAME);
                if (!dataDir.exists()) {
                    dataDir.mkdirs();
                }
                
                File file = new File(dataDir, "BLE_Data.txt");
                FileWriter writer = new FileWriter(file, true);
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                String logEntry = String.format("[%s] Device:%s UUID:%s Data:%s\n", 
                    timestamp, deviceName, characteristicUuid, data);
                writer.write(logEntry);
                writer.flush();
                writer.close();
                
                Log.d(TAG, "方式2成功 - 文件路径: " + file.getAbsolutePath());
                Log.d(TAG, "文件大小: " + file.length() + " bytes");
                saved = true;
                
            } catch (Exception e) {
                Log.e(TAG, "方式2失败: " + e.getMessage());
            }
        }
        
        // 方式3: 使用getExternalFilesDir()
        if (!saved) {
            try {
                File externalDir = context.getExternalFilesDir(null);
                if (externalDir != null) {
                    Log.d(TAG, "方式3 - 外部文件目录: " + externalDir.getAbsolutePath());
                    
                    File dataDir = new File(externalDir, FOLDER_NAME);
                    if (!dataDir.exists()) {
                        dataDir.mkdirs();
                    }
                    
                    File file = new File(dataDir, "BLE_Data.txt");
                    FileWriter writer = new FileWriter(file, true);
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    String logEntry = String.format("[%s] Device:%s UUID:%s Data:%s\n", 
                        timestamp, deviceName, characteristicUuid, data);
                    writer.write(logEntry);
                    writer.flush();
                    writer.close();
                    
                    Log.d(TAG, "方式3成功 - 文件路径: " + file.getAbsolutePath());
                    Log.d(TAG, "文件大小: " + file.length() + " bytes");
                    saved = true;
                }
            } catch (Exception e) {
                Log.e(TAG, "方式3失败: " + e.getMessage());
            }
        }
        
        if (saved) {
            Log.d(TAG, "=== 数据保存成功 ===");
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
        Log.d(TAG, "开始测试文件保存功能");
        saveDataToFile(context, "TEST_DATA_01_02_03_04", "TestDevice", "test-uuid-123");
        
        // 尝试直接使用内部存储
        try {
            File internalDir = new File(context.getFilesDir(), FOLDER_NAME);
            if (!internalDir.exists()) {
                internalDir.mkdirs();
            }
            
            File testFile = new File(internalDir, "direct_test.txt");
            FileWriter writer = new FileWriter(testFile);
            writer.write("Direct test file write\n");
            writer.flush();
            writer.close();
            
            Log.d(TAG, "直接文件写入测试成功: " + testFile.getAbsolutePath());
            Log.d(TAG, "文件大小: " + testFile.length() + " bytes");
            
        } catch (Exception e) {
            Log.e(TAG, "直接文件写入测试失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        Log.d(TAG, "文件保存测试完成");
    }
}
