# BLE数据保存功能说明

## 功能概述
已为FastBle项目添加了自动保存BLE数据到文件的功能。当接收到BLE设备的特征值变化通知时，数据会自动保存到手机存储中。

## 实现的功能

### 1. 文件权限
- 在 `AndroidManifest.xml` 中添加了文件读写权限：
  - `WRITE_EXTERNAL_STORAGE`
  - `READ_EXTERNAL_STORAGE`

### 2. 文件保存工具类
- 创建了 `FileUtils.java` 工具类，提供以下功能：
  - 自动创建数据文件夹
  - 生成带时间戳的文件名
  - 保存数据到文件，包含时间戳和数据内容
  - 支持外部存储和内部存储

### 3. 数据保存逻辑
- 修改了 `CharacteristicOperationFragment.java` 中的两个回调方法：
  - `BleNotifyCallback.onCharacteristicChanged()`
  - `BleIndicateCallback.onCharacteristicChanged()`
- 每次接收到数据变化时，除了在UI上显示外，还会自动保存到文件

## 文件保存位置

### 外部存储（优先）
- 路径：`/storage/emulated/0/FastBleData/`
- 文件名格式：`{设备名称}_{特征UUID}_{时间戳}.txt`

### 内部存储（备选）
- 如果外部存储不可用，使用内部存储
- 路径：`/data/data/com.clj.blesample/FastBleData/`

## 文件格式
```
[2024-01-15 14:30:25] 01 02 03 04 05
[2024-01-15 14:30:26] 06 07 08 09 0A
[2024-01-15 14:30:27] 0B 0C 0D 0E 0F
```

## 使用方法

1. 确保应用有存储权限
2. 连接BLE设备
3. 开启Notify或Indicate功能
4. 数据会自动保存到文件，无需额外操作

## 注意事项

- 文件会按时间戳命名，避免覆盖
- 每次数据变化都会追加到当前文件
- 如果外部存储不可用，会自动使用内部存储
- 建议定期清理旧的数据文件以节省存储空间

## 代码修改说明

### 修改的文件：
1. `app/src/main/AndroidManifest.xml` - 添加文件权限
2. `app/src/main/java/com/clj/blesample/utils/FileUtils.java` - 新建文件工具类
3. `app/src/main/java/com/clj/blesample/operation/CharacteristicOperationFragment.java` - 修改数据回调

### 主要代码变更：
- 在 `onCharacteristicChanged` 回调中添加了文件保存逻辑
- 使用设备名称和特征UUID生成文件名
- 添加时间戳和数据内容到文件
