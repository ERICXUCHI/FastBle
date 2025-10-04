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
        try {
            // 创建文件夹
            File folder = createDataFolder(context);
            if (folder == null) {
                Log.e(TAG, "无法创建数据文件夹");
                return;
            }
            
            // 生成文件名（包含时间戳）
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = String.format("%s_%s_%s.txt", deviceName, characteristicUuid, timestamp);
            File file = new File(folder, fileName);
            
            // 写入数据
            FileWriter writer = new FileWriter(file, true);
            String logEntry = String.format("[%s] %s\n", 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()),
                data);
            writer.write(logEntry);
            writer.flush();
            writer.close();
            
            Log.d(TAG, "数据已保存到文件: " + file.getAbsolutePath());
            
        } catch (IOException e) {
            Log.e(TAG, "保存文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建数据文件夹
     * @return 文件夹对象，如果创建失败返回null
     */
    private static File createDataFolder() {
        try {
            // 优先使用应用内部存储，不需要权限
            File internalDir = new File(Environment.getExternalStorageDirectory(), "Android/data/com.clj.blesample/files/" + FOLDER_NAME);
            if (!internalDir.exists()) {
                if (internalDir.mkdirs()) {
                    Log.d(TAG, "创建应用内部存储文件夹: " + internalDir.getAbsolutePath());
                    return internalDir;
                }
            } else {
                return internalDir;
            }
            
            // 备选方案：使用应用私有目录
            File privateDir = new File(Environment.getDataDirectory(), "data/com.clj.blesample/files/" + FOLDER_NAME);
            if (!privateDir.exists()) {
                if (privateDir.mkdirs()) {
                    Log.d(TAG, "创建私有存储文件夹: " + privateDir.getAbsolutePath());
                    return privateDir;
                }
            } else {
                return privateDir;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "创建文件夹失败: " + e.getMessage());
        }
        
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
}
