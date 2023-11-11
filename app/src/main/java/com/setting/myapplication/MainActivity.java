package com.setting.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.setting.myapplication.bt.BTListener;
import com.setting.myapplication.bt.BTManager1;
import com.setting.myapplication.bt.BTManager2;
import com.setting.myapplication.bt.BTManager3;
import com.setting.myapplication.bt.BTManager4;
import com.setting.myapplication.bt.EnumBTState;
import com.setting.myapplication.obd.ReceiveListener;
import com.setting.myapplication.userUtil.Util_Byte;

import org.apache.log4j.chainsaw.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import jxl.Sheet;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class MainActivity extends AppCompatActivity {
    //ble 연결
    Button btn_ble1, btn_ble2, btn_ble3, btn_ble4;
    //ble 연결해제
    Button btn_ble1_dis, btn_ble2_dis, btn_ble3_dis, btn_ble4_dis;
    //roll 값
    TextView tv_value_roll1, tv_value_roll2, tv_value_roll3, tv_value_roll4;
    //pitch 값
    TextView tv_value_pitch1, tv_value_pitch2, tv_value_pitch3, tv_value_pitch4;
    //yaw 값
    TextView tv_value_yaw1, tv_value_yaw2, tv_value_yaw3, tv_value_yaw4;
    //허벅지 길이
    EditText et_thigh;
    //종아리 길이
    EditText et_calf;
    //Depth
    TextView tv_depth;
    //KD
    //TextView tv_kd;
    //키라디오 그룹
    RadioGroup rg_height;
    //허벅지 평균 데이터
    float[] thighs = {28.39f, 29.34f, 30.24f, 31.14f, 31.86f};
    //종아리 평균 데이터
    float[] calfs = {47.1f, 48.9f, 50.4f, 51.9f, 53.4f};
    //관찰자 이름
    EditText et_name;
    //피측정자 이름
    EditText et_pname;
    //측정시간
    EditText et_time;
    //샘플링
    EditText et_sampling;
    //시작버튼
    Button btn_start;
    //butt_timing
    Button btn_butt_timing;

    //프로그레스
    ProgressBar progressBar;

    ActivityResultLauncher<Intent> getResult;

    //권한 코드
    private static final int PERMISSIONCODE = 100;
    //첫번째 블루투스
    BTManager1 btManager1;

    //두번째 블루투스
    BTManager2 btManager2;

    //세번째 블루투스
    BTManager3 btManager3;

    //네번째 블루투스
    BTManager4 btManager4;
    //각 입력 데이터
    int mes1[] = new int[6];
    int mes2[] = new int[6];
    int mes3[] = new int[6];
    int mes4[] = new int[6];
    //각 계산데이터
    double axis_x_1;
    double axis_x_2;
    double axis_x_3;
    double axis_x_4;

    double axis_y_1;
    double axis_y_2;
    double axis_y_3;
    double axis_y_4;

    double axis_z_1;
    double axis_z_2;
    double axis_z_3;
    double axis_z_4;


    double roll_1, roll_2, roll_3, roll_4;
    double pitch_1, pitch_2, pitch_3, pitch_4;
    double yaw_1, yaw_2, yaw_3, yaw_4;
    //허벅지 종아리
    double data1, data2;
    //Depth
    private double depth;
    //KD
    //private double kd;
    //저장여부
    boolean isSave = false;
    //butt여부
    int nButt = -1;

    ArrayList<SaveData> alSaveData = new ArrayList<>();

    Handler mHandler;

    int i = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    if (nButt == -1) {
                        nButt = -1;
                        isSave = false;
                        alSaveData.clear();
                        Toast.makeText(MainActivity.this, "butt를 누르지 않아서 저장하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        isSave = false;
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("저장");
                        builder.setMessage("파일을 저장하시겠습니까?");
                        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    //writeJXL(String.format("%s_%s_%s.xls", new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()), et_name.getText().toString(), et_pname.getText().toString()));
                                    saveData();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                    }
                }
            }
        };

        getResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            if (Build.VERSION.SDK_INT > 30 && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            Log.d("MAIN", " " + result.getData().getIntExtra("type", 0));
                            if (result.getData().getIntExtra("type", 0) == 1) {
                                btManager1.connect(result.getData().getStringExtra("address"));
                            } else if (result.getData().getIntExtra("type", 0) == 2) {
                                btManager2.connect(result.getData().getStringExtra("address"));
                            } else if (result.getData().getIntExtra("type", 0) == 3) {
                                btManager3.connect(result.getData().getStringExtra("address"));
                            } else if (result.getData().getIntExtra("type", 0) == 4) {
                                btManager4.connect(result.getData().getStringExtra("address"));
                            }

                        }
                    }
                });
        initRes();
        initBT();
    }

    private void saveData() {

        String filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+"_"+et_name.getText().toString()+"_"+et_pname.getText().toString()+".txt";
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + filename);

        ArrayList<String> alStr = new ArrayList<>();
        for(SaveData item : alSaveData){
            alStr.add(String.format("%.5f",item.yaw1));
            alStr.add(String.format("%.5f",item.pitch1));
            alStr.add(String.format("%.5f",item.roll1));

            alStr.add(String.format("%.5f",item.yaw2));
            alStr.add(String.format("%.5f",item.pitch2));
            alStr.add(String.format("%.5f",item.roll2));

            alStr.add(String.format("%.5f",item.yaw3));
            alStr.add(String.format("%.5f",item.pitch3));
            alStr.add(String.format("%.5f",item.roll3));

            alStr.add(String.format("%.5f",item.yaw4));
            alStr.add(String.format("%.5f",item.pitch4));
            alStr.add(String.format("%.5f",item.roll4));

            alStr.add(String.format("%.5f",item.depth));
            //alStr.add(String.format("%.5f",item.kd));
        }


        SaveData item = alSaveData.get(nButt);
        alStr.add(String.format("%d",nButt));
/*
        alStr.add(String.format("%.5f",item.yaw1));
        alStr.add(String.format("%.5f",item.pitch1));
        alStr.add(String.format("%.5f",item.roll1));

        alStr.add(String.format("%.5f",item.yaw2));
        alStr.add(String.format("%.5f",item.pitch2));
        alStr.add(String.format("%.5f",item.roll2));

        alStr.add(String.format("%.5f",item.yaw3));
        alStr.add(String.format("%.5f",item.pitch3));
        alStr.add(String.format("%.5f",item.roll3));

        alStr.add(String.format("%.5f",item.yaw4));
        alStr.add(String.format("%.5f",item.pitch4));
        alStr.add(String.format("%.5f",item.roll4));

        alStr.add(String.format("%.5f",item.depth));
        alStr.add(String.format("%.5f",item.kd));*/

        Log.e("alStr",alStr.toString());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.write(alStr.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void initBT() {


        initbt1();
        initbt2();
        initbt3();
        initbt4();


    }

    private Timer timer;
    int delay = 100;

    //task 클래스 생성
    public class TaskToDo extends TimerTask {
        int count=0;

        @Override
        public void run() {
            count += 1;
            if(delay*count>=progressBar.getMax()){
                mHandler.sendEmptyMessage(0);
                timer.cancel();
            }
           progressBar.setProgress(delay*count);
        }

        @Override
        public long scheduledExecutionTime() {
            Log.e("test","scheduledExecutionTime");
            return super.scheduledExecutionTime();

        }
    }
    /////////////////////


    //setTimer 메소드 선언
    public void setTimer(long delay, long endTime) {

        progressBar.setMax((int)endTime);
        timer = new Timer();
        timer.schedule(new TaskToDo(), delay, delay);

    }
    private void initbt1() {
        btManager1 = new BTManager1(this);

        btManager1.setBtListener(new BTListener() {
            @Override
            public void onBTState(EnumBTState state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state == EnumBTState.disconnected) {
                            Toast.makeText(MainActivity.this, "BLE1 DISCONNECTED", Toast.LENGTH_SHORT).show();
                        } else if (state == EnumBTState.gatt_connected) {
                            Toast.makeText(MainActivity.this, "BLE1 CONNECTED", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onReceive(UUID uuid, byte[] data) {

            }

            @Override
            public void onChanged(byte[] data) {


            }

            @Override
            public void setBleMtu(int mtu) {

            }
        });
        btManager1.setReceiveParserListener(new ReceiveListener() {
            @Override
            public void onReceivedPacket(byte[] data, int nLen) {

                if(nLen==7) {
                    mes1[0] = data[0];
                    mes1[1] = data[1];
                    mes1[2] = data[2];
                    mes1[3] = data[3];
                    mes1[4] = data[4];
                    mes1[5] = data[5];

                    for(int i =0;i<mes1.length;i++){
                        if(mes1[i]<0){
                            mes1[i]+=256;
                        }
                    }

                    //System.out.println("mes1 "+ mes1[0]+" "+mes1[1]+" "+mes1[2]+" "+mes1[3]+" "+mes1[4]+" "+mes1[5]);

                    procedure(mes1,0);
                    yaw_1 = axis_caculation_Z(axis_x_1);
                    pitch_1 = axis_caculation_xy(axis_y_1);
                    roll_1 = axis_caculation_xy(axis_z_1);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            try {
                                data1 = Double.parseDouble(et_thigh.getText().toString());
                                data2 = Double.parseDouble(et_calf.getText().toString());
                            }catch (Exception e){
                                //e.printStackTrace();
                            }
                            tv_value_roll1.setText(String.format("%.5f",roll_1));
                            tv_value_pitch1.setText(String.format("%.5f",pitch_1));
                            tv_value_yaw1.setText(String.format("%.5f",yaw_1));
                        }
                    });
                }


                /*Util_Byte.LogToHexString("btmanager1", data, data.length);
                String str = new String(data);
                String[] strItem = str.substring(1, str.length() - 1).split(",");
                if (strItem.length == 3) {
                    Log.e("btmanager1", str + " " + strItem[0] + " " + strItem[1] + " " + strItem[2]);

                }*/
            }
        });
    }

    private void initbt2() {
        btManager2 = new BTManager2(this);

        btManager2.setBtListener(new BTListener() {
            @Override
            public void onBTState(EnumBTState state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state == EnumBTState.disconnected) {
                            Toast.makeText(MainActivity.this, "BLE2 DISCONNECTED", Toast.LENGTH_SHORT).show();
                        } else if (state == EnumBTState.gatt_connected) {
                            Toast.makeText(MainActivity.this, "BLE2 CONNECTED", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onReceive(UUID uuid, byte[] data) {

            }

            @Override
            public void onChanged(byte[] data) {


            }

            @Override
            public void setBleMtu(int mtu) {

            }
        });
        btManager2.setReceiveParserListener(new ReceiveListener() {
            @Override
            public void onReceivedPacket(byte[] data, int nLen) {
                //  Util_Byte.LogToHexString("btmanager2",data,data.length);
                if(nLen==7) {
                    mes2[0] = data[0];
                    mes2[1] = data[1];
                    mes2[2] = data[2];
                    mes2[3] = data[3];
                    mes2[4] = data[4];
                    mes2[5] = data[5];

                    for(int i =0;i<mes2.length;i++){
                        if(mes2[i]<0){
                            mes2[i]+=256;
                        }
                    }

                    procedure(mes2,1);
                    yaw_2 = axis_caculation_Z(axis_x_2);
                    pitch_2 = axis_caculation_xy(axis_y_2);
                    roll_2 = axis_caculation_xy(axis_z_2);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            try {
                                data1 = Double.parseDouble(et_thigh.getText().toString());
                                data2 = Double.parseDouble(et_calf.getText().toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            tv_value_roll2.setText(String.format("%.5f",roll_2));
                            tv_value_pitch2.setText(String.format("%.5f",pitch_2));
                            tv_value_yaw2.setText(String.format("%.5f",yaw_2));
                        }
                    });
                }


               /* String str = new String(data);
                String[] strItem = str.substring(1, str.length() - 1).split(",");
                if (strItem.length == 3) {
                    Log.e("btmanager2", str + " " + strItem[0] + " " + strItem[1] + " " + strItem[2]);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                roll_2 = Double.parseDouble(strItem[0]);
                                pitch_2 = Double.parseDouble(strItem[1]);
                                yaw_2 = Double.parseDouble(strItem[2]);
                                data1 = Double.parseDouble(et_thigh.getText().toString());
                                data2 = Double.parseDouble(et_calf.getText().toString());
                            } catch (Exception e) {

                            }
                            tv_depth.setText(String.format("%.2f cm", roll_3 + roll_4 + data1 + data2));
                            tv_value_roll2.setText(strItem[0]);
                            tv_value_pitch2.setText(strItem[1]);
                            tv_value_yaw2.setText(strItem[2]);
                        }
                    });
                }*/
            }
        });
    }

    private void initbt3() {
        btManager3 = new BTManager3(this);

        btManager3.setBtListener(new BTListener() {
            @Override
            public void onBTState(EnumBTState state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state == EnumBTState.disconnected) {
                            Toast.makeText(MainActivity.this, "BLE3 DISCONNECTED", Toast.LENGTH_SHORT).show();
                        } else if (state == EnumBTState.gatt_connected) {
                            Toast.makeText(MainActivity.this, "BLE3 CONNECTED", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onReceive(UUID uuid, byte[] data) {

            }

            @Override
            public void onChanged(byte[] data) {


            }

            @Override
            public void setBleMtu(int mtu) {

            }
        });
        btManager3.setReceiveParserListener(new ReceiveListener() {
            @Override
            public void onReceivedPacket(byte[] data, int nLen) {
                 Util_Byte.LogToHexString("btmanager3",data,data.length);
                 if(nLen==7) {
                     mes3[0] = data[0];
                     mes3[1] = data[1];
                     mes3[2] = data[2];
                     mes3[3] = data[3];
                     mes3[4] = data[4];
                     mes3[5] = data[5];

                     for(int i =0;i<mes3.length;i++){
                         if(mes3[i]<0){
                             mes3[i]+=256;
                         }
                     }

                     procedure(mes3,2);
                     yaw_3 = axis_caculation_Z(axis_x_3);
                     pitch_3 = axis_caculation_xy(axis_y_3);
                     roll_3 = axis_caculation_xy(axis_z_3);

                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {

                             try {
                                 data1 = Double.parseDouble(et_thigh.getText().toString());
                                 data2 = Double.parseDouble(et_calf.getText().toString());
                             }catch (Exception e){
                                e.printStackTrace();
                             }
                             tv_value_roll3.setText(String.format("%.5f",roll_3));
                             tv_value_pitch3.setText(String.format("%.5f",pitch_3));
                             tv_value_yaw3.setText(String.format("%.5f",yaw_3));
                         }
                     });
                 }


//                String str = new String(data);
  //              Log.e("btmanager3",str);
                /*String[] strItem = str.substring(1, str.length() - 1).split(",");
                if (strItem.length == 3) {
                    Log.e("btmanager3", str + " " + strItem[0] + " " + strItem[1] + " " + strItem[2]);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                roll_3 = Double.parseDouble(strItem[0]);
                                pitch_3 = Double.parseDouble(strItem[1]);
                                yaw_3 = Double.parseDouble(strItem[2]);
                                data1 = Double.parseDouble(et_thigh.getText().toString());
                                data2 = Double.parseDouble(et_calf.getText().toString());
                            } catch (Exception e) {

                            }
                            tv_depth.setText(String.format("%.2f cm", roll_3 + roll_4 + data1 + data2));
                            tv_value_roll3.setText(strItem[0]);
                            tv_value_pitch3.setText(strItem[1]);
                            tv_value_yaw3.setText(strItem[2]);
                        }
                    });
                }*/
            }
        });
    }


    private void initbt4() {
        btManager4 = new BTManager4(this);

        btManager4.setBtListener(new BTListener() {
            @Override
            public void onBTState(EnumBTState state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state == EnumBTState.disconnected) {
                            Toast.makeText(MainActivity.this, "BLE4 DISCONNECTED", Toast.LENGTH_SHORT).show();
                        } else if (state == EnumBTState.gatt_connected) {
                            Toast.makeText(MainActivity.this, "BLE4 CONNECTED", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onReceive(UUID uuid, byte[] data) {

            }

            @Override
            public void onChanged(byte[] data) {


            }

            @Override
            public void setBleMtu(int mtu) {

            }
        });
        btManager4.setReceiveParserListener(new ReceiveListener() {
            @Override
            public void onReceivedPacket(byte[] data, int nLen) {
                 Util_Byte.LogToHexString("btmanager4",data,data.length);
                if(nLen==7) {
                    mes4[0] = data[0];
                    mes4[1] = data[1];
                    mes4[2] = data[2];
                    mes4[3] = data[3];
                    mes4[4] = data[4];
                    mes4[5] = data[5];

                    for(int i =0;i<mes4.length;i++){
                        if(mes4[i]<0){
                            mes4[i]+=256;
                        }
                    }

                    procedure(mes4,3);




                    yaw_4 = axis_caculation_Z(axis_x_4);
                    pitch_4 = axis_caculation_xy(axis_y_4);
                    roll_4 = axis_caculation_xy(axis_z_4);
                    Log.e("axis_x",axis_x_4+" "+axis_y_4+" "+axis_z_4+" "+yaw_4+" "+pitch_4+" "+roll_4);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                data1 = Double.parseDouble(et_thigh.getText().toString());
                                data2 = Double.parseDouble(et_calf.getText().toString());


                            }catch (Exception e){
                             //   e.printStackTrace();
                            }

                            tv_depth.setText(String.format("%.5f cm", depth_ca()));
                            //tv_kd.setText(String.format("%.5f cm", KD_ca()));

                            tv_value_roll4.setText(String.format("%.5f",roll_4));
                            tv_value_pitch4.setText(String.format("%.5f",pitch_4));
                            tv_value_yaw4.setText(String.format("%.5f",yaw_4));

                            if (isSave) {
                                alSaveData.add(new SaveData(roll_1, pitch_1, yaw_1, roll_2, pitch_2, yaw_2, roll_3, pitch_3, yaw_3, roll_4, pitch_4, yaw_4, depth));
                            }
                        }
                    });
                }


               /* String str = new String(data);
                String[] strItem = str.substring(1, str.length() - 1).split(",");
                if (strItem.length == 3) {
                    Log.e("btmanager4", str + " " + strItem[0] + " " + strItem[1] + " " + strItem[2]);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                roll_4 = Double.parseDouble(strItem[0]);
                                pitch_4 = Double.parseDouble(strItem[1]);
                                yaw_4 = Double.parseDouble(strItem[2]);
                                data1 = Double.parseDouble(et_thigh.getText().toString());
                                data2 = Double.parseDouble(et_calf.getText().toString());
                                depth = roll_3 + roll_4 + data1 + data2;
                                if (isSave) {
                                    alSaveData.add(new SaveData(roll_1, pitch_1, yaw_1, roll_2, pitch_2, yaw_2, roll_3, pitch_3, yaw_3, roll_4, pitch_4, yaw_4, depth));
                                }
                            } catch (Exception e) {

                            }
                            tv_depth.setText(String.format("%.2f cm", depth));
                            tv_value_roll4.setText(strItem[0]);
                            tv_value_pitch4.setText(strItem[1]);
                            tv_value_yaw4.setText(strItem[2]);
                        }
                    });
                }*/
            }
        });
    }

    private void initRes() {

        btn_ble1 = findViewById(R.id.btn_ble1);
        btn_ble2 = findViewById(R.id.btn_ble2);
        btn_ble3 = findViewById(R.id.btn_ble3);
        btn_ble4 = findViewById(R.id.btn_ble4);


        btn_ble1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                intent.putExtra("type", 1);
                getResult.launch(intent);

            }
        });

        btn_ble2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                intent.putExtra("type", 2);
                getResult.launch(intent);
            }
        });

        btn_ble3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                intent.putExtra("type", 3);
                getResult.launch(intent);
            }
        });

        btn_ble4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                intent.putExtra("type", 4);
                getResult.launch(intent);
            }
        });

        btn_ble1_dis = findViewById(R.id.btn_ble1_dis);
        btn_ble2_dis = findViewById(R.id.btn_ble2_dis);
        btn_ble3_dis = findViewById(R.id.btn_ble3_dis);
        btn_ble4_dis = findViewById(R.id.btn_ble4_dis);

        btn_ble1_dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btManager1.isBtConnected()) {
                    btManager1.disconnect();
                }
            }
        });

        btn_ble2_dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btManager2.isBtConnected()) {
                    btManager2.disconnect();
                }
            }
        });

        btn_ble3_dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btManager3.isBtConnected()) {
                    btManager3.disconnect();
                }
            }
        });

        btn_ble4_dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btManager4.isBtConnected()) {
                    btManager4.disconnect();
                }
            }
        });


        tv_value_roll1 = findViewById(R.id.tv_value_roll1);
        tv_value_pitch1 = findViewById(R.id.tv_value_pitch1);
        tv_value_yaw1 = findViewById(R.id.tv_value_yaw1);

        tv_value_roll2 = findViewById(R.id.tv_value_roll2);
        tv_value_pitch2 = findViewById(R.id.tv_value_pitch2);
        tv_value_yaw2 = findViewById(R.id.tv_value_yaw2);

        tv_value_roll3 = findViewById(R.id.tv_value_roll3);
        tv_value_pitch3 = findViewById(R.id.tv_value_pitch3);
        tv_value_yaw3 = findViewById(R.id.tv_value_yaw3);


        tv_value_roll4 = findViewById(R.id.tv_value_roll4);
        tv_value_pitch4 = findViewById(R.id.tv_value_pitch4);
        tv_value_yaw4 = findViewById(R.id.tv_value_yaw4);

        et_thigh = findViewById(R.id.et_thigh);
        et_calf = findViewById(R.id.et_calf);

        tv_depth = findViewById(R.id.tv_depth);

        //tv_kd = findViewById(R.id.tv_kd);

        rg_height = findViewById(R.id.rg_height);
        rg_height.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.rb_1) {
                    //155~160
                    et_calf.setText(calfs[0] + "");
                    et_thigh.setText(thighs[0] + "");
                } else if (id == R.id.rb_2) {
                    //160~165
                    et_calf.setText(calfs[1] + "");
                    et_thigh.setText(thighs[1] + "");
                } else if (id == R.id.rb_3) {
                    //165~170
                    et_calf.setText(calfs[2] + "");
                    et_thigh.setText(thighs[2] + "");
                } else if (id == R.id.rb_4) {
                    //170~175
                    et_calf.setText(calfs[3] + "");
                    et_thigh.setText(thighs[3] + "");
                } else if (id == R.id.rb_5) {
                    //175~180
                    et_calf.setText(calfs[4] + "");
                    et_thigh.setText(thighs[4] + "");
                } else {

                }
            }
        });



        et_name = findViewById(R.id.et_name);
        et_pname = findViewById(R.id.et_pname);

        et_time = findViewById(R.id.et_time);

        et_sampling = findViewById(R.id.et_sampling);

        btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nButt = -1;
                isSave = true;
                alSaveData.clear();
                int n = 5;
                try {
                    n = Integer.parseInt(et_time.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setTimer(100,n*1000);

            }
        });

        btn_butt_timing = findViewById(R.id.btn_butt_timing);
        btn_butt_timing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nButt = alSaveData.size() - 1;
            }
        });

        progressBar = findViewById(R.id.progress);

        checkPermission();

    }


    //권한 Check
    private void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

        } else if (Build.VERSION.SDK_INT <= 30) {
            // Permission check
            int permissionFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            int permissionWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionReadStroage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            int permissionCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionReadStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionWriteStorage == PackageManager.PERMISSION_DENIED
                    || permissionFineLocation == PackageManager.PERMISSION_DENIED
                    || permissionCoarseLocation == PackageManager.PERMISSION_DENIED
                    || permissionReadStorage == PackageManager.PERMISSION_DENIED
                    || permissionReadStroage == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONCODE);
            } else {

            }
        } else {
            // Permission check
            int permissionBLUETOOTH_SCAN = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
            int permissionBLUETOOTH_CONNECT = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT);
            int permissionWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionReadStroage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int permissionReadStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionWriteStorage == PackageManager.PERMISSION_DENIED
                    || permissionBLUETOOTH_SCAN == PackageManager.PERMISSION_DENIED
                    || permissionBLUETOOTH_CONNECT == PackageManager.PERMISSION_DENIED
                    || permissionReadStorage == PackageManager.PERMISSION_DENIED
                    || permissionReadStroage == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                }, PERMISSIONCODE);
            } else {


            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONCODE:
                break;
        }
    }

    public void writeJXL(String filename) throws Exception {
        WritableWorkbook wb = jxl.Workbook.createWorkbook(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + filename));
        wb.createSheet("new sheet-1", 0);
        WritableSheet sheet = wb.getSheet(0);


        initData(sheet);
        setData(sheet);

        wb.write();
        wb.close();
    }

    private void setData(WritableSheet sheet) throws WriteException {
        WritableCellFormat format = new WritableCellFormat();
        format.setAlignment(Alignment.CENTRE);
        format.setVerticalAlignment(VerticalAlignment.CENTRE);
        Label label = null;
        for (int i = 0; i < alSaveData.size(); i++) {

            label = new Label(i + 2, 1, alSaveData.get(i).getRoll1() + "", format);
            sheet.addCell(label);
            label = new Label(i + 2, 2, alSaveData.get(i).getPitch1() + "", format);
            sheet.addCell(label);
            label = new Label(i + 2, 3, alSaveData.get(i).getYaw1() + "", format);
            sheet.addCell(label);
            label = new Label(i + 2, 4, alSaveData.get(i).getRoll2() + "", format);
            sheet.addCell(label);
            label = new Label(i + 2, 5, alSaveData.get(i).getPitch2() + "", format);
            sheet.addCell(label);
            label = new Label(i + 2, 6, alSaveData.get(i).getYaw2() + "", format);
            sheet.addCell(label);
            label = new Label(i + 2, 7, alSaveData.get(i).getRoll3() + "", format);
            sheet.addCell(label);
            label = new Label(i + 2, 8, alSaveData.get(i).getPitch3() + "", format);
            sheet.addCell(label);
            label = new Label(i + 2, 9, alSaveData.get(i).getYaw3() + "", format);
            sheet.addCell(label);
            label = new Label(i + 2, 10, alSaveData.get(i).getRoll4() + "", format);
            sheet.addCell(label);
            label = new Label(i + 2, 11, alSaveData.get(i).getPitch4() + "", format);
            sheet.addCell(label);
            label = new Label(i + 2, 12, alSaveData.get(i).getYaw4() + "", format);
            sheet.addCell(label);
            label = new Label(i + 2, 13, String.format("%.2f", alSaveData.get(i).getDepth()), format);
            sheet.addCell(label);
        }
        //butt
        label = new Label(alSaveData.size() + 3, 0, "butt_timing", format);
        sheet.addCell(label);
        label = new Label(alSaveData.size() + 3, 1, alSaveData.get(nButt).getRoll1() + "", format);
        sheet.addCell(label);
        label = new Label(alSaveData.size() + 3, 2, alSaveData.get(nButt).getPitch1() + "", format);
        sheet.addCell(label);
        label = new Label(alSaveData.size() + 3, 3, alSaveData.get(nButt).getYaw1() + "", format);
        sheet.addCell(label);
        label = new Label(alSaveData.size() + 3, 4, alSaveData.get(nButt).getRoll2() + "", format);
        sheet.addCell(label);
        label = new Label(alSaveData.size() + 3, 5, alSaveData.get(nButt).getPitch2() + "", format);
        sheet.addCell(label);
        label = new Label(alSaveData.size() + 3, 6, alSaveData.get(nButt).getYaw2() + "", format);
        sheet.addCell(label);
        label = new Label(alSaveData.size() + 3, 7, alSaveData.get(nButt).getRoll3() + "", format);
        sheet.addCell(label);
        label = new Label(alSaveData.size() + 3, 8, alSaveData.get(nButt).getPitch3() + "", format);
        sheet.addCell(label);
        label = new Label(alSaveData.size() + 3, 9, alSaveData.get(nButt).getYaw3() + "", format);
        sheet.addCell(label);
        label = new Label(alSaveData.size() + 3, 10, alSaveData.get(nButt).getRoll4() + "", format);
        sheet.addCell(label);
        label = new Label(alSaveData.size() + 3, 11, alSaveData.get(nButt).getPitch4() + "", format);
        sheet.addCell(label);
        label = new Label(alSaveData.size() + 3, 12, alSaveData.get(nButt).getYaw4() + "", format);
        sheet.addCell(label);
        label = new Label(alSaveData.size() + 3, 13, String.format("%.2f", alSaveData.get(nButt).getDepth()), format);
        sheet.addCell(label);

    }

    //초기 설정
    private void initData(WritableSheet sheet) throws WriteException {
        WritableCellFormat format = new WritableCellFormat();
        format.setAlignment(Alignment.CENTRE);
        format.setVerticalAlignment(VerticalAlignment.CENTRE);

        Label label = null;
        sheet.mergeCells(0, 1, 0, 3);

        label = new Label(0, 1, "BLE1", format);
        sheet.addCell(label);

        sheet.mergeCells(0, 4, 0, 6);
        label = new Label(0, 4, "BLE2", format);
        sheet.addCell(label);

        sheet.mergeCells(0, 7, 0, 9);
        label = new Label(0, 7, "BLE3", format);
        sheet.addCell(label);

        sheet.mergeCells(0, 10, 0, 12);
        label = new Label(0, 10, "BLE4", format);
        sheet.addCell(label);
        sheet.mergeCells(0, 13, 1, 13);
        label = new Label(0, 13, "DEPTH", format);
        sheet.addCell(label);

        label = new Label(1, 1, "ROLL", format);
        sheet.addCell(label);
        label = new Label(1, 2, "PITCH", format);
        sheet.addCell(label);
        label = new Label(1, 3, "YAW", format);
        sheet.addCell(label);
        label = new Label(1, 4, "ROLL", format);
        sheet.addCell(label);
        label = new Label(1, 5, "PITCH", format);
        sheet.addCell(label);
        label = new Label(1, 6, "YAW", format);
        sheet.addCell(label);
        label = new Label(1, 7, "ROLL", format);
        sheet.addCell(label);
        label = new Label(1, 8, "PITCH", format);
        sheet.addCell(label);
        label = new Label(1, 9, "YAW", format);
        sheet.addCell(label);
        label = new Label(1, 10, "ROLL", format);
        sheet.addCell(label);
        label = new Label(1, 11, "PITCH", format);
        sheet.addCell(label);
        label = new Label(1, 12, "YAW", format);
        sheet.addCell(label);
    }

    public void procedure(int[] mes, int type){
        if(type==0){
            axis_x_1 = mes[0]*256+mes[1];
            axis_y_1 = mes[2]*256+mes[3];
            axis_z_1 = mes[4]*256+mes[5];
        }else if(type==1){
            axis_x_2 = mes[0]*256+mes[1];
            axis_y_2 = mes[2]*256+mes[3];
            axis_z_2 = mes[4]*256+mes[5];
        }else if(type==2){
            axis_x_3 = mes[0]*256+mes[1];
            axis_y_3 = mes[2]*256+mes[3];
            axis_z_3 = mes[4]*256+mes[5];
        }else if(type==3){
            axis_x_4 = mes[0]*256+mes[1];
            axis_y_4 = mes[2]*256+mes[3];
            axis_z_4 = mes[4]*256+mes[5];
        }
    }
    public double axis_caculation_Z(double input){
        return (input/182) - 180;
    }

    public double axis_caculation_xy(double input){
        return (input/364) - 90;
    }

    public double depth_ca(){
        depth = data1 * Math.sin(Math.toRadians(roll_3)) + data2 * Math.sin(Math.toRadians(roll_4));
       // Log.e("data depth", data1+" "+Math.sin(roll_3)+" "+(data1*Math.sin(roll_3))+" "+data2+" "+Math.sin(roll_4)+(data1*Math.sin(roll_4)));
        return depth;
    }

    /*
    public double KD_ca(){
        kd = data1 * Math.cos(Math.toRadians(roll_3)) + data2 * Math.cos(Math.toRadians(roll_4));
        return kd;
    }
    */


}