package com.clj.blesample.operation;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.clj.blesample.R;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.clj.blesample.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class CharacteristicOperationFragment extends Fragment {

    public static final int PROPERTY_READ = 1;
    public static final int PROPERTY_WRITE = 2;
    public static final int PROPERTY_WRITE_NO_RESPONSE = 3;
    public static final int PROPERTY_NOTIFY = 4;
    public static final int PROPERTY_INDICATE = 5;

    private LinearLayout layout_container;
    private final List<String> childList = new ArrayList<>();
    
    // 静态变量跟踪通知状态，防止退出页面时自动停止
    private static boolean isNotifyActive = false;
    private static boolean isIndicateActive = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_characteric_operation, null);
        initView(v);
        return v;
    }

    private void initView(View v) {
        layout_container = (LinearLayout) v.findViewById(R.id.layout_container);
    }

    public void showData() {
        final BleDevice bleDevice = ((OperationActivity) getActivity()).getBleDevice();
        final BluetoothGattCharacteristic characteristic = ((OperationActivity) getActivity()).getCharacteristic();
        final int charaProp = ((OperationActivity) getActivity()).getCharaProp();
        String child = characteristic.getUuid().toString() + String.valueOf(charaProp);

        for (int i = 0; i < layout_container.getChildCount(); i++) {
            layout_container.getChildAt(i).setVisibility(View.GONE);
        }
        if (childList.contains(child)) {
            layout_container.findViewWithTag(bleDevice.getKey() + characteristic.getUuid().toString() + charaProp).setVisibility(View.VISIBLE);
        } else {
            childList.add(child);

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_characteric_operation, null);
            view.setTag(bleDevice.getKey() + characteristic.getUuid().toString() + charaProp);
            LinearLayout layout_add = (LinearLayout) view.findViewById(R.id.layout_add);
            final TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
            txt_title.setText(String.valueOf(characteristic.getUuid().toString() + getActivity().getString(R.string.data_changed)));
            final TextView txt = (TextView) view.findViewById(R.id.txt);
            txt.setMovementMethod(ScrollingMovementMethod.getInstance());

            switch (charaProp) {
                case PROPERTY_READ: {
                    View view_add = LayoutInflater.from(getActivity()).inflate(R.layout.layout_characteric_operation_button, null);
                    Button btn = (Button) view_add.findViewById(R.id.btn);
                    btn.setText(getActivity().getString(R.string.read));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BleManager.getInstance().read(
                                    bleDevice,
                                    characteristic.getService().getUuid().toString(),
                                    characteristic.getUuid().toString(),
                                    new BleReadCallback() {

                                        @Override
                                        public void onReadSuccess(final byte[] data) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, HexUtil.formatHexString(data, true));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onReadFailure(final BleException exception) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, exception.toString());
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                    layout_add.addView(view_add);
                }
                break;

                case PROPERTY_WRITE: {
                    View view_add = LayoutInflater.from(getActivity()).inflate(R.layout.layout_characteric_operation_et, null);
                    final EditText et = (EditText) view_add.findViewById(R.id.et);
                    Button btn = (Button) view_add.findViewById(R.id.btn);
                    btn.setText(getActivity().getString(R.string.write));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String hex = et.getText().toString();
                            if (TextUtils.isEmpty(hex)) {
                                return;
                            }
                            BleManager.getInstance().write(
                                    bleDevice,
                                    characteristic.getService().getUuid().toString(),
                                    characteristic.getUuid().toString(),
                                    HexUtil.hexStringToBytes(hex),
                                    new BleWriteCallback() {

                                        @Override
                                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, "write success, current: " + current
                                                            + " total: " + total
                                                            + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onWriteFailure(final BleException exception) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, exception.toString());
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                    layout_add.addView(view_add);
                }
                break;

                case PROPERTY_WRITE_NO_RESPONSE: {
                    View view_add = LayoutInflater.from(getActivity()).inflate(R.layout.layout_characteric_operation_et, null);
                    final EditText et = (EditText) view_add.findViewById(R.id.et);
                    Button btn = (Button) view_add.findViewById(R.id.btn);
                    btn.setText(getActivity().getString(R.string.write));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String hex = et.getText().toString();
                            if (TextUtils.isEmpty(hex)) {
                                return;
                            }
                            BleManager.getInstance().write(
                                    bleDevice,
                                    characteristic.getService().getUuid().toString(),
                                    characteristic.getUuid().toString(),
                                    HexUtil.hexStringToBytes(hex),
                                    new BleWriteCallback() {

                                        @Override
                                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, "write success, current: " + current
                                                            + " total: " + total
                                                            + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onWriteFailure(final BleException exception) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, exception.toString());
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                    layout_add.addView(view_add);
                }
                break;

                case PROPERTY_NOTIFY: {
                    View view_add = LayoutInflater.from(getActivity()).inflate(R.layout.layout_characteric_operation_button, null);
                    final Button btn = (Button) view_add.findViewById(R.id.btn);
                    // 根据静态状态设置按钮文本
                    if (isNotifyActive) {
                        btn.setText(getActivity().getString(R.string.close_notification));
                    } else {
                        btn.setText(getActivity().getString(R.string.open_notification));
                    }
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isNotifyActive) {
                                // 开启通知
                                btn.setText(getActivity().getString(R.string.close_notification));
                                isNotifyActive = true;
                                
                                // 开始新的数据收集会话
                                String sessionFile = com.clj.blesample.utils.FileUtils.startNewSession(getActivity());
                                if (sessionFile != null) {
                                    android.util.Log.d("BLE_SESSION", "通知会话已开始: " + sessionFile);
                                    android.widget.Toast.makeText(getActivity(), "数据收集会话已开始，退出页面后继续运行", android.widget.Toast.LENGTH_SHORT).show();
                                }
                                
                                BleManager.getInstance().notify(
                                        bleDevice,
                                        characteristic.getService().getUuid().toString(),
                                        characteristic.getUuid().toString(),
                                        new BleNotifyCallback() {

                                            @Override
                                            public void onNotifySuccess() {
                                                android.util.Log.d("BLE_NOTIFY", "Notify成功");
                                                // 移除UI更新，避免Fragment销毁后的崩溃
                                                // runOnUiThread(new Runnable() {
                                                //     @Override
                                                //     public void run() {
                                                //         addText(txt, "Notify成功");
                                                //     }
                                                // });
                                            }

                                            @Override
                                            public void onNotifyFailure(final BleException exception) {
                                                android.util.Log.e("BLE_NOTIFY", "Notify失败: " + exception.toString());
                                                // 移除UI更新，避免Fragment销毁后的崩溃
                                                // runOnUiThread(new Runnable() {
                                                //     @Override
                                                //     public void run() {
                                                //         addText(txt, exception.toString());
                                                //     }
                                                // });
                                            }

                                            @Override
                                            public void onCharacteristicChanged(byte[] data) {
                                                // 使用传入的data参数
                                                String hexData = HexUtil.formatHexString(data, true);
                                                String deviceName = bleDevice.getName() != null ? bleDevice.getName() : bleDevice.getMac();
                                                String characteristicUuid = characteristic.getUuid().toString();
                                                
                                                // 保存数据到文件
                                                android.util.Log.d("BLE_DATA", "收到数据: " + hexData + " 设备: " + deviceName);
                                                try {
                                                    // 使用静态方式获取Context，避免getActivity()崩溃
                                                    if (com.clj.blesample.MainActivity.getAppContext() != null) {
                                                        FileUtils.saveDataToFile(
                                                            com.clj.blesample.MainActivity.getAppContext(), 
                                                            hexData, 
                                                            deviceName, 
                                                            characteristicUuid
                                                        );
                                                        android.util.Log.d("BLE_DATA", "数据已保存到文件");
                                                    } else {
                                                        android.util.Log.e("BLE_DATA", "无法获取Application Context");
                                                    }
                                                } catch (Exception e) {
                                                    android.util.Log.e("BLE_DATA", "保存数据时出错: " + e.getMessage());
                                                }
                                                
                                                // 记录日志
                                                android.util.Log.d("BLE_DATA", "收到数据: " + hexData + " 设备: " + deviceName);
                                                android.util.Log.e("BLE_FORCE_LOG", "强制日志 - 数据: " + hexData);
                                                
                                                // 只在Fragment活跃时更新UI
                                                if (isAdded() && getActivity() != null && !getActivity().isFinishing()) {
                                                    try {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                addText(txt, hexData);
                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        android.util.Log.e("BLE_UI", "UI更新出错: " + e.getMessage());
                                                    }
                                                } else {
                                                    android.util.Log.d("BLE_UI", "Fragment不活跃，跳过UI更新");
                                                }
                                            }
                                        });
                            } else {
                                // 关闭通知
                                btn.setText(getActivity().getString(R.string.open_notification));
                                isNotifyActive = false;
                                
                                // 结束当前数据收集会话
                                String endedFile = com.clj.blesample.utils.FileUtils.endCurrentSession();
                                if (endedFile != null) {
                                    android.util.Log.d("BLE_SESSION", "通知会话已结束: " + endedFile);
                                    android.widget.Toast.makeText(getActivity(), "数据收集会话已结束", android.widget.Toast.LENGTH_SHORT).show();
                                }
                                
                                BleManager.getInstance().stopNotify(
                                        bleDevice,
                                        characteristic.getService().getUuid().toString(),
                                        characteristic.getUuid().toString());
                            }
                        }
                    });
                    layout_add.addView(view_add);
                }
                break;

                case PROPERTY_INDICATE: {
                    View view_add = LayoutInflater.from(getActivity()).inflate(R.layout.layout_characteric_operation_button, null);
                    final Button btn = (Button) view_add.findViewById(R.id.btn);
                    // 根据静态状态设置按钮文本
                    if (isIndicateActive) {
                        btn.setText(getActivity().getString(R.string.close_notification));
                    } else {
                        btn.setText(getActivity().getString(R.string.open_notification));
                    }
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isIndicateActive) {
                                // 开启指示
                                btn.setText(getActivity().getString(R.string.close_notification));
                                isIndicateActive = true;
                                
                                // 开始新的数据收集会话
                                String sessionFile = com.clj.blesample.utils.FileUtils.startNewSession(getActivity());
                                if (sessionFile != null) {
                                    android.util.Log.d("BLE_SESSION", "指示会话已开始: " + sessionFile);
                                    android.widget.Toast.makeText(getActivity(), "数据收集会话已开始，退出页面后继续运行", android.widget.Toast.LENGTH_SHORT).show();
                                }
                                
                                BleManager.getInstance().indicate(
                                        bleDevice,
                                        characteristic.getService().getUuid().toString(),
                                        characteristic.getUuid().toString(),
                                        new BleIndicateCallback() {

                                            @Override
                                            public void onIndicateSuccess() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        addText(txt, "indicate success");
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onIndicateFailure(final BleException exception) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        addText(txt, exception.toString());
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCharacteristicChanged(byte[] data) {
                                                // 使用传入的data参数，而不是characteristic.getValue()
                                                String hexData = HexUtil.formatHexString(data, true);
                                                
                                                // 获取设备名称和特征UUID
                                                String deviceName = bleDevice.getName() != null ? bleDevice.getName() : bleDevice.getMac();
                                                String characteristicUuid = characteristic.getUuid().toString();
                                                
                                                // 保存数据到文件
                                                android.util.Log.d("BLE_DATA", "收到数据: " + hexData + " 设备: " + deviceName);
                                                try {
                                                    // 使用静态方式获取Context，避免getActivity()崩溃
                                                    if (com.clj.blesample.MainActivity.getAppContext() != null) {
                                                        FileUtils.saveDataToFile(
                                                            com.clj.blesample.MainActivity.getAppContext(), 
                                                            hexData, 
                                                            deviceName, 
                                                            characteristicUuid
                                                        );
                                                        android.util.Log.d("BLE_DATA", "数据已保存到文件");
                                                    } else {
                                                        android.util.Log.e("BLE_DATA", "无法获取Application Context");
                                                    }
                                                } catch (Exception e) {
                                                    android.util.Log.e("BLE_DATA", "保存数据时出错: " + e.getMessage());
                                                }
                                                
                                                // 记录日志
                                                android.util.Log.d("BLE_DATA", "收到数据: " + hexData + " 设备: " + deviceName);
                                                android.util.Log.e("BLE_FORCE_LOG", "强制日志 - 数据: " + hexData);
                                                
                                                // 只在Fragment活跃时更新UI
                                                if (isAdded() && getActivity() != null && !getActivity().isFinishing()) {
                                                    try {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                addText(txt, hexData);
                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        android.util.Log.e("BLE_UI", "UI更新出错: " + e.getMessage());
                                                    }
                                                } else {
                                                    android.util.Log.d("BLE_UI", "Fragment不活跃，跳过UI更新");
                                                }
                                            }
                                        });
                            } else {
                                // 关闭指示
                                btn.setText(getActivity().getString(R.string.open_notification));
                                isIndicateActive = false;
                                
                                // 结束当前数据收集会话
                                String endedFile = com.clj.blesample.utils.FileUtils.endCurrentSession();
                                if (endedFile != null) {
                                    android.util.Log.d("BLE_SESSION", "指示会话已结束: " + endedFile);
                                    android.widget.Toast.makeText(getActivity(), "数据收集会话已结束", android.widget.Toast.LENGTH_SHORT).show();
                                }
                                
                                BleManager.getInstance().stopIndicate(
                                        bleDevice,
                                        characteristic.getService().getUuid().toString(),
                                        characteristic.getUuid().toString());
                            }
                        }
                    });
                    layout_add.addView(view_add);
                }
                break;
            }

            layout_container.addView(view);
        }
    }

    private void runOnUiThread(Runnable runnable) {
        if (isAdded() && getActivity() != null)
            getActivity().runOnUiThread(runnable);
    }

    private void addText(TextView textView, String content) {
        textView.append(content);
        textView.append("\n");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }
    
    /**
     * 获取通知状态
     * @return 是否有通知在运行
     */
    public static boolean isNotificationActive() {
        return isNotifyActive || isIndicateActive;
    }
    
    /**
     * 检查是否有活跃的Indicate
     */
    public static boolean isIndicateActive() {
        return isIndicateActive;
    }
    
    /**
     * 获取通知状态描述
     * @return 状态描述
     */
    public static String getNotificationStatus() {
        if (isNotifyActive && isIndicateActive) {
            return "Notify和Indicate都在运行";
        } else if (isNotifyActive) {
            return "Notify正在运行";
        } else if (isIndicateActive) {
            return "Indicate正在运行";
        } else {
            return "没有通知在运行";
        }
    }


}
