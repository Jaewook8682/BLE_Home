package com.setting.myapplication.bt;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.setting.myapplication.obd.ComFrame;
import com.setting.myapplication.obd.ReceiveListener;
import com.setting.myapplication.obd.ReceiveParser;
import com.setting.myapplication.userUtil.Util_Byte;
import com.setting.myapplication.userUtil.Util_Log;

import java.util.List;
import java.util.UUID;

/**
 * bluetooth manager
 */

public class BTManager2 implements BTListener, ReceiveListener {
    private final String TAG = "BTManager2";

    public String BTNAME = "GPSHUD";//"WeFiND HUD";//"BT GPS";//"IOT GPS";//""; //"CUBE"; //"JDY-08"; AutoKeeper// //
    //public String UUID_NOTIFY = "0000FE62-0000-1000-8000-00805F9B34FB";
    //-
    public String UUID_NOTIFY = "BEB5470E-36E1-4688-B7F5-EA07361B26A8";

    //c97433f1-be8f-4dc8-b6f0-5343e6100eb4
    //public String UUID_READ = "0000FE61-0000-1000-8000-00805F9B34FB";
    //public String UUID_WRITE = "0000FE61-0000-1000-8000-00805F9B34FB";
    public String UUID_READ = "11110004-4455-6677-8899-AABBCCDDEEFF";
    public String UUID_WRITE = "11110004-4455-6677-8899-AABBCCDDEEFF";

//WEFIND HUD
/*    public String UUID_NOTIFY = "0000FE62-0000-1000-8000-00805F9B34FB";

    //c97433f1-be8f-4dc8-b6f0-5343e6100eb4
    public String UUID_READ = "0000FE61-0000-1000-8000-00805F9B34FB";
    public String UUID_WRITE = "0000FE61-0000-1000-8000-00805F9B34FB";*/


    public String UUID_WRITE2 = "0000FE61-0000-1000-8000-00805F9B34FB";
    public String UUID_READ2 = "0000FE61-0000-1000-8000-00805F9B34FB";

    private static final int CMD_RETURN_TIMEOUT = 5000; // 해당 시간 안에 응답이 있어야 함.


    private boolean isLog = true;
    private boolean isTest = false;

    private int nMtu = 512;


    private static volatile BTManager2 singletonInstance = null;

    public static BTManager2 getInstance(Context context) {
        if (singletonInstance == null) {
            synchronized (BTManager2.class) {
                if (singletonInstance == null) {
                    singletonInstance = new BTManager2(context);
                }
            }
        }
        return singletonInstance;
    }

    public static BTManager2 getInstance() {
        return singletonInstance;
    }

    public static void releaseInstance() {
        synchronized (BTManager2.class) {
            if (singletonInstance != null)
                singletonInstance.exit();

            singletonInstance = null;
        }
    }


    private Context context = null;


    private BluetoothAdapter m_BluetoothAdapter = null;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    //데이터 통신용
    private BluetoothGattCharacteristic mWriteCharacteristic;
    private BluetoothGattCharacteristic mReadCharacteristic;
    //Data path switching 및 UART 속도 변경 - 제어용
    private BluetoothGattCharacteristic mWriteCharacteristic2;
    private BluetoothGattCharacteristic mReadCharacteristic2;


    private BluetoothLeService2 bluetoothLeService2;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    private String deviceAddress = ""; // 블루투스 MAC Address

    private EnumBTState enumBTState = EnumBTState.disconnected;


    private boolean btConnected = false; // 블루투스 연결 여부

    private boolean isConnectOnServiceConnected = false; // 서비스 연결 전에 연결 요청이 있으면 서비스 연결 후 다시 연결 시도

    private BTListener btListener; // 블루투스 상태 및 패킷 수신
    private ReceiveListener receiveListener; // 완성된 패킷 수신
    private ReceiveParser receiveParser; // 수신 한 패킷 parsing 패킷이 완성 되면 리스너로 알려 줌.

    /**
     * bt 상태 및 수신 데이터 수신 받기 위한 리스너
     */
    public void setBtListener(BTListener listener) {
        btListener = listener;
    }

    /**
     * 수신 완료 된 패킷을 받기 위한 리스너
     */
    public void setReceiveParserListener(ReceiveListener listener) {
        receiveListener = listener;
    }


    public BTManager2(Context context) {
        init(context);
    }



    public void init(Context context) {
        this.context = context;



        addLog("test = 1");

        //userPref = new UserPref(context.getApplicationContext());

        receiveParser = new ReceiveParser();
        receiveParser.setListener(this);
        receiveParser.start();

        BluetoothManager bluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        m_BluetoothAdapter = bluetoothManager.getAdapter();

        Intent gattServiceIntent = new Intent(this.context, BluetoothLeService2.class);
        boolean result = this.context.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        if (isLog)
            addLog("bindService result=" + result);
    }

    /**
     * 연결 상태
     */
    public EnumBTState getEnumBTState() {
        return enumBTState;
    }

    /**
     * 블루투스 연결 여부
     **/
    public boolean isBtConnected() {
        return btConnected;
    }



    /**
     * 현재 연결된 블루투스의 Mac Address
     */
    public String getDeviceAddress() {
        return deviceAddress;
    }

    /**
     * 종료
     **/
    public void exit() {


        disconnect();

        try {
            context.unbindService(mServiceConnection);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (singletonInstance != null)
                singletonInstance.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        bluetoothLeService2 = null;
        singletonInstance = null;

        receiveParser.exit();



    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            if (isLog)
                addLog("onServiceConnected componentName = " + componentName);

            bluetoothLeService2 = ((BluetoothLeService2.LocalBinder) service).getService();

            if (!bluetoothLeService2.initialize(context, BTManager2.this)) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }

            if (isLog)
                addLog("deviceAddress = " + deviceAddress);

            if (isConnectOnServiceConnected)
                connect(deviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if (isLog)
                addLog("onServiceDisconnected componentName = " + componentName);

            bluetoothLeService2 = null;
        }
    };


    /**
     * 맥주소를 통한 연결 요청
     *
     * @param addr 블루투스 MAC Address
     */
    public boolean connect(String addr) {
        if (isLog)
            addLog("connect = " + addr);

        deviceAddress = addr;

        if (bluetoothLeService2 == null) {
            if (isLog)
                addLog("mBluetoothLeService == null");

            isConnectOnServiceConnected = true;

            return false;
        }

        boolean result = bluetoothLeService2.connect(deviceAddress);

        if (isLog)
            addLog("Connect request result=" + result);

        return result;
    }


    /**
     * 연결 종료
     */
    public void disconnect() {
        btConnected = false;

        if (bluetoothLeService2 != null)
            bluetoothLeService2.disconnect();
    }


    /**
     * read / write / notify  UUID를 찾아 서비스 연결
     *
     * @param gattServices
     * @return
     */
    private boolean setGattServices(List<BluetoothGattService> gattServices) {
        boolean rtn = false;

        if (isLog)
            addLog("displayGattServices gattServices = " + gattServices);


        if (gattServices == null)
            return false;

        String uuid;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                uuid = gattCharacteristic.getUuid().toString();
                byte property = (byte) gattCharacteristic.getProperties();

                //WRITE READ NOTIFY
                Log.e(TAG, "UUID = " + uuid.toUpperCase() + " " + UUID_NOTIFY + " " + property + " " + (property & BluetoothGattCharacteristic.PROPERTY_WRITE) + (property & BluetoothGattCharacteristic.PROPERTY_READ) + (property & BluetoothGattCharacteristic.PROPERTY_NOTIFY));
                if (isLog)
                    addLog("UUID = " + uuid.toUpperCase() + " " + UUID_NOTIFY + " " + property + " " + (property & BluetoothGattCharacteristic.PROPERTY_WRITE) + " " + (property & BluetoothGattCharacteristic.PROPERTY_READ) + " " + (property & BluetoothGattCharacteristic.PROPERTY_NOTIFY));
                if (uuid.toUpperCase().equals(UUID_NOTIFY.toUpperCase())) {
                    mNotifyCharacteristic = gattCharacteristic;
                    bluetoothLeService2.setCharacteristicNotification(mNotifyCharacteristic, true);

                    rtn = true;
                    if (isLog)
                        addLog("NOTIFY OK" + uuid);
                    Log.d(TAG, "UUID_NOTIFY OK = " + uuid);
                }
                if (uuid.toUpperCase().equals(UUID_WRITE)) {
                    mWriteCharacteristic = gattCharacteristic;
                    rtn = true;
                    if (isLog)
                        addLog("WRITE OK" + uuid);
                    Log.d(TAG, "UUID_WRITE OK = " + UUID_WRITE);
                }
                if (uuid.toUpperCase().equals(UUID_READ)) {
                /*    mReadCharacteristic = gattCharacteristic;
                    rtn = true;
                    if (isLog)
                        addLog("READ OK"+uuid);
                    Log.d(TAG, "UUID_READ OK = " + uuid);*/
                }
             /*   if(uuid.toUpperCase().equals(UUID_WRITE2)){
                    mWriteCharacteristic2 = gattCharacteristic;
                    rtn = true;
                    if (isLog)
                        addLog("WRITE OK2");
                }*/

               /* if(uuid.toUpperCase().equals(UUID_READ2)){
                    mReadCharacteristic2 = gattCharacteristic;
                    mBluetoothLeService.setCharacteristicNotification(mReadCharacteristic2, true);
                    rtn = true;
                    if (isLog)
                        addLog("WRITE OK2");
                }*/
            }
        }

        // TODO 임시 read, write, notify 중 하나라도 있으면 성공으로 처리
        return rtn;
    }

    public void receiveTest(byte[] buf) {
        if (isTest) {
            receiveParser.push(buf, buf.length);
        }
    }


    public boolean sendData(byte[] buf) {
        boolean rtn = false;

        if (isLog) {
            addLog("TX" + buf.length + nMtu);
        }
        addLog(Util_Byte.hexToString("TX", buf));

        if (mWriteCharacteristic != null) {
            if (bluetoothLeService2 != null)
                if (bluetoothLeService2.mBluetoothGatt != null) {
                    byte[] divebytes = new byte[nMtu];

                    for (int i = 0; i - 1 < buf.length / nMtu; i++) {
                        byte[] tx = null;

                        if (((i + 1) * nMtu) > buf.length) {
                            tx = new byte[buf.length - ((i) * nMtu)];
                        } else {
                            tx = new byte[nMtu];
                        }

                        System.arraycopy(buf, i * nMtu, tx, 0, tx.length);

                        addLog("TX" + tx.length);
                        mWriteCharacteristic.setValue(tx);
                        BluetoothGattService service = mWriteCharacteristic.getService();
                        if (service == null) {
                            return false;
                        }
                        //연결 해제인 경우 처리
                        if (!isBtConnected()) {
                            return false;
                        }


                        if (Build.VERSION.SDK_INT > 30 && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return false;
                        }
                        rtn = bluetoothLeService2.mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);


                    }


                }
        }

        if (isLog)
            addLog("send " + (rtn ? "ok" : "fail"));

        return rtn;
    }

   /*
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {


        if (VDBG) Log.d(TAG, "writeCharacteristic() - uuid: " + characteristic.getUuid());
        if (mService == null || mClientIf == 0 || characteristic.getValue() == null) return false;

        BluetoothGattService service = characteristic.getService();
        if (service == null) return false;

        BluetoothDevice device = service.getDevice();
        if (device == null) return false;

        synchronized (mDeviceBusy) {
            if (mDeviceBusy) return false;
            mDeviceBusy = true;
        }

        try {
            mService.writeCharacteristic(mClientIf, device.getAddress(),
                    characteristic.getInstanceId(), characteristic.getWriteType(),
                    AUTHENTICATION_NONE, characteristic.getValue());
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            mDeviceBusy = false;
            return false;
        }

        return true;
    }*/


    @Override
    public void onBTState(EnumBTState state) {

        enumBTState = state;

        if (btListener != null)
            btListener.onBTState(state);

        if (isLog)
            addLog(enumBTState.toString());

        switch (enumBTState) {
            case disconnected: {
                // 장비와 연결이 끊어졌음.
                btConnected = false;
                break;
            }
            case scanning: {
                break;
            }
            case gatt_connecting: {
                break;
            }
            case gatt_connected: {
                // TODO 장비와 연결은 되었지만 데이터를 주고 받을 수 있는 상태는 아님.
                btConnected = true;
                break;
            }
            case services_discovered: {
                // TODO 데이터 주고/받는 서비스 까지 연결 되야 연결로 보아야 할 것 같음.
                btConnected = true;

                addLog("onServicesDiscovered2");

                if (bluetoothLeService2 != null) {
                    if (setGattServices(bluetoothLeService2.getSupportedGattServices())) {


                        enumBTState = EnumBTState.service_connected;

                        if (isLog)
                            addLog(enumBTState.toString());

                        if (btListener != null)
                            btListener.onBTState(enumBTState);




                    } else {
                        // TODO 서비스가 없을 경우 예외 처리 필요
                    }
                }
                break;
            }
            case service_connected: {
                break;
            }

        }
    }


    @Override
    public void onReceive(UUID uuid, byte[] data) {

        if (receiveParser != null) {
            receiveParser.push(data, data.length);
        }
    }

    @Override
    public void onChanged(byte[] data) {
        Util_Byte.LogToHexString("onChange", data, data.length);
        if (receiveListener != null)
            receiveListener.onReceivedPacket(data, data.length);
    }

    @Override
    public void setBleMtu(int mtu) {
        addLog("onServicesDiscovered6 " + mtu);
        this.nMtu = mtu;
        addLog("setBleMtu" + nMtu);
        // this.nMtu = 50;
    }


    int odbTripCount = 0; // OBD에 저장된 trip 갯수

    /**
     * 정상적인 패킷 수신 시 호출 됨
     */
    @Override
    public void onReceivedPacket(byte[] data, int nLen) {
        // 화면에서 처리
        if (receiveListener != null)
            receiveListener.onReceivedPacket(data, nLen);

    }




    public boolean send(byte cmd, byte[] payload) {
        ComFrame frame = new ComFrame();
        frame.makeFrame(cmd, payload);

        switch (cmd) {

        }

        // 블루투스를 통한 전송
        return sendData(frame.getBuf());
    }

    /**
     * OBD에 로그인 전송 후 응답이 오는지 체크
     */
    private Runnable loginTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            // 로그인 실패 (OBD 응답이 없음)
            enumBTState = EnumBTState.obd_logging_fail;
            if (btListener != null)
                btListener.onBTState(enumBTState);
        }
    };


    public void addLog(String strLog) {

        Log.d(TAG, strLog);


        Message message = new Message();
        message.what = 0;
        message.getData().putString("log", strLog);

    }

}
