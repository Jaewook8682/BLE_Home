package com.setting.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.setting.myapplication.adapter.DataAdapter;
import com.setting.myapplication.adapter.DeviceAdapter;
import com.setting.myapplication.bt.BTManager1;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBLEScanner;
    ArrayList<BluetoothDevice> alDevice = new ArrayList<>();

    RecyclerView recycler;
    DeviceAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        initList();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
        // Checks if Bluetooth LE Scanner is available.
        if (mBLEScanner == null) {
            Toast.makeText(this, "Can not find BLE Scanner", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        scanLeDevice();


    }

    private void initList() {
        recycler = findViewById(R.id.recycler);

        adapter = new DeviceAdapter(this, alDevice);
        recycler.setLayoutManager(new LinearLayoutManager(BluetoothActivity.this));
        recycler.setAdapter(adapter);
        adapter.setClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                int pos = (int) view.getTag();
                Intent intent = new Intent();
                intent.putExtra("type", getIntent().getIntExtra("type", 0));
                intent.putExtra("name", alDevice.get(pos).getName());
                intent.putExtra("address", alDevice.get(pos).getAddress());
                intent.putExtra("device",alDevice.get(pos));
                setResult(Activity.RESULT_OK, intent);

                finish();
            }
        });

        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recycler.addItemDecoration(decoration);


    }

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private boolean scanning;
    private Handler handler = new Handler();

    private void scanLeDevice() {
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    mBLEScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            mBLEScanner.startScan(mScanCallback);
        } else {
            scanning = false;
            mBLEScanner.stopScan(mScanCallback);
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (Build.VERSION.SDK_INT >30 && ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        //    Log.e("onScanResult", result.getDevice().getName() + " " + result.getDevice().getAddress());
            processResult(result);
        }

        @Override        public void onBatchScanResults(List<ScanResult> results) {
            Log.e("onBatchScan",results.size()+" ");
            for (ScanResult result : results) {
                processResult(result);
            }
        }

        @Override        public void onScanFailed(int errorCode) {
        }

        private void processResult(final ScanResult result) {
            runOnUiThread(new Runnable() {
                @Override                public void run() {
                    if(result.getDevice().getName()!=null) {
                        if (!alDevice.contains(result.getDevice())) {
                            alDevice.add(result.getDevice());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    };
}