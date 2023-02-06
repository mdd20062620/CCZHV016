package com.ruiguan.chache.Break;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.hc.bluetoothlibrary.DeviceModule;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ruiguan.R;
import com.ruiguan.activities.ActivityCollector;
import com.ruiguan.activities.MainActivity;
import com.ruiguan.activities.single.HoldBluetooth;
import com.ruiguan.chache.ChacheActivity;
import com.ruiguan.printer.PrintDataService;
import com.ruiguan.view.LineChartMarkView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.ruiguan.activities.MenuActivity.input_data;
import static com.ruiguan.chache.ChacheActivity.chache_Input;
public class BreakActivity extends BreakSaveActivity {
        private static String TAG = BreakActivity.class.getSimpleName();
        private BluetoothDevice deviceFoot= null;
        private BluetoothDevice deviceBrake= null;
        private DeviceModule  moduleFoot= null;
        private DeviceModule  moduleBrake= null;
        private HoldBluetooth mHoldBluetoothFoot= null;
        private HoldBluetooth mHoldBluetoothBrake= null;
        private BluetoothAdapter bluetoothAdapter = null;
        private List<DeviceModule> modules;

        private Button breakStartBtn;
        private Button stopBtn;
        private Button printBtn;
        private Button saveBtn;
        private Button exportBtn;
        private Button scanBtn;
        private Button backBtn;
        private Button exitBtn;
        private Button footBLE;
        private Button brakeBLE;

        private LinearLayout mainLayout;
        private ImageView count_txt;
        private TextView statusF_txt;
        private TextView statusB_txt;
        private EditText startSpeed_txt;
        //private EditText tipSpeed_txt;
       // private float tipSpeed;
       // private String str_tipSpeed;

        private Drawable startBtnpressed;
        private Drawable stopBtnpressed;
        private Drawable printBtnpressed;
        private Drawable saveBtnpressed;
        private Drawable exportBtnpressed;
        private Drawable footBLEpressed;
        private Drawable brakeBLEpressed;

        private byte[] senddata;
        private byte[] senddataFoot;
        private byte[] breakReceive;
        private byte[] breakReceiveFoot;
        private byte[] breakReceiveBrake;
        private float realSpeed;
        private float startSpeed;
        private float speedMax;
        private float breakSpeed;
        private float realASpeed;
        private float ASpeedMax;
        private float breakDisMax;
        private float breakDis0;
        private float breakDis;
        private float runDis;
        private float runDisMax;
        private float breakTime;
        private float tabanForce;
        private float tabanForceMax;
        final int[] arrayId={0,R.drawable.number5,R.drawable.number4,R.drawable.number3,R.drawable.number2,R.drawable.number1};

        private float speedtemp=0.0f;
        private float ytemp=0.0f;

        //private ProgressDialog pd;
        private TextView realSpeed_txt;
        private TextView ASpeedMax_txt;
        private TextView breakDis_txt;
        private TextView breakSpeed_txt;
        private TextView runDisMax_txt;
        private TextView breakTime_txt;
        private TextView breakForceMax_txt;

        private String str_realSpeed;
        private String str_startSpeed;
        private String str_speedMax;
        private String str_breakSpeed;
        private String str_ASpeedMax;
        private String str_runDis;
        private String str_breakDis;
        private String str_breakDis0;
        private String str_breakDisMax;
        private String str_tabanForceMax;
        private String str_breakTime;
        private String str_company;
        private String str_number;
        private String str_chacheNumber;
        private String str_chacheType;
        private String str_chacheGroup;
        private boolean BreakFlag=false;
        private boolean BreakStartFlag=false;
        private boolean RunStartFlag=false;

        private float ChartYMax;
        private float ChartXMax;

        private boolean Finish=false;
        private Handler handler = new Handler();
        private LineChart chartBreak;
        ArrayList<Float> Speed_Data = new ArrayList<>();
        ArrayList<Float> ASpeed_Data = new ArrayList<>();
        ArrayList<Float> Dis_Data = new ArrayList<>();
        private final String CONNECTED = "已连接",CONNECTING = "连接中",DISCONNECT = "断线了";
        private BreakActivity.DynamicLineChartManager dynamicLineChartManager_Break;
        private List<String> names = new ArrayList<>(); //折线名字集合
        private List<Integer> colour = new ArrayList<>();//折线颜色集合
        java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.000");

        private boolean isPrinterReady = false;
        private PrintDataService printDataService = null;
        private boolean PrintConnect = false;
        // 语音合成对象
       /* private SpeechSynthesizer mTts;
        public static String voicerXtts = "xiaoyan";
        // 引擎类型
        private String mEngineType = SpeechConstant.TYPE_XTTS;
        private Toast mToast;*/
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_break);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ActivityCollector.addActivity(this);
            // 请勿在“=”与appid之间添加任何空字符或者转义符
            //SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5d6f8890");
            // 初始化合成对象
           // mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
            senddata=new byte[9];
            senddataFoot=new byte[9];
            breakReceive=new byte[19];
            breakReceiveFoot=new byte[17];
            breakReceiveBrake=new byte[17];

            bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
            final HoldBluetooth.UpdateList updateList = new HoldBluetooth.UpdateList() {
                @Override
                public void update(boolean isStart,DeviceModule deviceModule) {

                }

                @Override
                public void updateMessyCode(boolean isStart, DeviceModule deviceModule) {
                }
            };
            final HoldBluetooth.UpdateList updateList1 = new HoldBluetooth.UpdateList() {
                @Override
                public void update(boolean isStart,DeviceModule deviceModule) {

                }

                @Override
                public void updateMessyCode(boolean isStart, DeviceModule deviceModule) {
                }
            };
            mHoldBluetoothFoot= new HoldBluetooth();
            mHoldBluetoothFoot.initHoldBluetooth(BreakActivity.this,updateList1);
            mHoldBluetoothBrake= new HoldBluetooth();
            mHoldBluetoothBrake.initHoldBluetooth(BreakActivity.this,updateList);
            initMembers();
           // setParam();
            initDataFootListener();
            initDataBrakeListener();
            ShowWave();
            //setEnableButton();
            handler.postDelayed(BleRunnable,2000);
        }
        private void initMembers() {
            str_company=input_data.getCom();
            str_number=input_data.getNumber();
            str_chacheNumber= chache_Input.getchacheNumber();
            str_chacheType= chache_Input.getchacheType();
            str_chacheGroup= chache_Input.getchacheGroup();

            mainLayout=findViewById(R.id.mainLayout);
            startSpeed_txt=(EditText) findViewById(R.id.startSpeed_txt);
            realSpeed_txt= (TextView) findViewById(R.id.breakRealSpeed_txt);
            ASpeedMax_txt=(TextView) findViewById(R.id.breakASpeedMax_txt);
            breakDis_txt=(TextView) findViewById(R.id.breakDis_txt);
           // tipSpeed_txt=(EditText) findViewById(R.id.tipSpeed_txt);

            breakSpeed_txt= (TextView) findViewById(R.id.breakSpeed_txt);
            runDisMax_txt= (TextView) findViewById(R.id.runDisMax_txt);
            breakTime_txt= (TextView) findViewById(R.id.breakTime_txt);
            breakForceMax_txt= (TextView) findViewById(R.id.breakForceMax_txt);
            statusB_txt= (TextView) findViewById(R.id.statusB_txt);
            statusF_txt= (TextView) findViewById(R.id.statusF_txt);
            count_txt=findViewById(R.id.count_txt);

            breakStartBtn = (Button) findViewById(R.id.breakStartBtn);
            stopBtn = (Button) findViewById(R.id.breakStopBtn);
            printBtn = (Button) findViewById(R.id.breakPrintBtn);
            saveBtn = (Button) findViewById(R.id.breakSaveBtn);
            exportBtn = (Button) findViewById(R.id.breakExportBtn);
            scanBtn = (Button) findViewById(R.id.breakScanBtn);
            backBtn = (Button) findViewById(R.id.breakBackBtn);
            exitBtn = (Button) findViewById(R.id.breakExitBtn);
            footBLE= (Button) findViewById(R.id.footBLE);
            brakeBLE= (Button) findViewById(R.id.brakeBLE);

            View.OnClickListener bl = new BreakActivity.ButtonListener();
            setOnClickListener(breakStartBtn, bl);
            setOnClickListener(stopBtn, bl);
            setOnClickListener(printBtn, bl);
            setOnClickListener(saveBtn, bl);
            setOnClickListener(exportBtn, bl);
            setOnClickListener(scanBtn, bl);
            setOnClickListener(backBtn, bl);
            setOnClickListener(exitBtn, bl);

            SharedPreferences shares1 = getSharedPreferences( "speedValue", Activity.MODE_PRIVATE );
            if(!shares1.getBoolean("speedValueDecive",false))
            {

            }else {
                str_startSpeed=shares1.getString("startSpeed","");
                startSpeed_txt.setText(str_startSpeed);
            }
            setButton();
            chartBreak= (LineChart) findViewById(R.id.breakChart);
            names.add ("");
            colour.add (Color.argb (255, 255, 125, 0));            //定义Fre颜色
            dynamicLineChartManager_Break = new BreakActivity.DynamicLineChartManager(chartBreak, names.get (0), colour.get (0), 0);
        }
    //初始化蓝牙数据的监听
    private void initDataBrakeListener() {
        HoldBluetooth.OnReadDataListener dataListener = new HoldBluetooth.OnReadDataListener() {
            @Override
            public void readData(String mac, byte[] data) {
                if (deviceBrake.getAddress().equals(mac)){
                    breakReceiveBrake=data;
                } else if(deviceFoot.getAddress().equals(mac))
                {
                    breakReceiveFoot=data;
                }
            }
            @Override
            public void reading(boolean isStart) {

            }
            @Override
            public void connectSucceed() {
                modules = mHoldBluetoothBrake.getConnectedArray();
                for(int i=0;i<modules.size();i++)
                {
                    if(modules.get(i).getMac().equals(deviceBrake.getAddress()))
                    {
                        setBrakeState(CONNECTED);//设置连接状态
                        Log.d("BreakActivity","Brake蓝牙连接成功！");
                    }else if(modules.get(i).getMac().equals(deviceFoot.getAddress()))
                    {
                        setFootState(CONNECTED);//设置连接状态
                        Log.d("BreakActivity","Foot蓝牙连接成功！");
                    }
                }
            }
            @Override
            public void errorDisconnect(final DeviceModule deviceModule) {//蓝牙异常断开

                if(deviceModule.getMac().equals(deviceBrake.getAddress()))
                {
                    setBrakeState(DISCONNECT);//设置断开状态
                }else if(deviceModule.getMac().equals(deviceFoot.getAddress()))
                {
                    setFootState(DISCONNECT);//设置断开状态
                 }
                handler.removeCallbacks (ReceiveRunnable);
                setButton();
            }
            @Override
            public void readNumber(int number) {

            }

            @Override
            public void readLog(String className, String data, String lv) {

            }

            @Override
            public void readVelocity(int velocity) {

            }
        };
        mHoldBluetoothBrake.setOnReadListener(dataListener);
    }
    //初始化蓝牙数据的监听
    private void initDataFootListener() {
        HoldBluetooth.OnReadDataListener dataListener = new HoldBluetooth.OnReadDataListener() {
            @Override
            public void readData(String mac, byte[] data) {
                if (deviceBrake.getAddress().equals(mac)){
                    breakReceiveBrake=data;
                } else if(deviceFoot.getAddress().equals(mac))
                {
                    breakReceiveFoot=data;
                }
            }
            @Override
            public void reading(boolean isStart) {

            }
            @Override
            public void connectSucceed() {
                modules = mHoldBluetoothFoot.getConnectedArray();
                for(int i=0;i<modules.size();i++)
                {
                    if(modules.get(i).getMac().equals(deviceBrake.getAddress()))
                    {
                        setBrakeState(CONNECTED);//设置连接状态
                        Log.d("BreakActivity","Brake蓝牙连接成功！");
                    }else if(modules.get(i).getMac().equals(deviceFoot.getAddress()))
                    {
                        setFootState(CONNECTED);//设置连接状态
                        Log.d("BreakActivity","Foot蓝牙连接成功！");
                    }
                }
            }
            @Override
            public void errorDisconnect(final DeviceModule deviceModule) {//蓝牙异常断开
                if(deviceModule.getMac().equals(deviceBrake.getAddress()))
                {
                    setBrakeState(DISCONNECT);//设置断开状态
                }else if(deviceModule.getMac().equals(deviceFoot.getAddress()))
                {
                    setFootState(DISCONNECT);//设置断开状态
                }
                handler.removeCallbacks (ReceiveRunnable);
                setButton();
            }
            @Override
            public void readNumber(int number) {

            }

            @Override
            public void readLog(String className, String data, String lv) {

            }

            @Override
            public void readVelocity(int velocity) {

            }
        };
        mHoldBluetoothFoot.setOnReadListener(dataListener);
    }
    private void setFootState(String state){
        switch (state){
            case CONNECTED://连接成功
                statusF_txt.setText("已连接");
                footBLEpressed = getResources().getDrawable(R.drawable.btle_connected);
                footBLEpressed.setBounds(0, 0, footBLEpressed.getMinimumWidth(), footBLEpressed.getMinimumHeight());
                footBLE.setCompoundDrawables(null, footBLEpressed, null, null);
                setEnableButton();
                break;

            case CONNECTING://连接中
                statusF_txt.setText("连接中");
                footBLEpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                footBLEpressed.setBounds(0, 0, footBLEpressed.getMinimumWidth(), footBLEpressed.getMinimumHeight());
                footBLE.setCompoundDrawables(null, footBLEpressed, null, null);
                break;

            case DISCONNECT://连接断开
                statusF_txt.setText("断开");
                footBLEpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                footBLEpressed.setBounds(0, 0, footBLEpressed.getMinimumWidth(), footBLEpressed.getMinimumHeight());
                footBLE.setCompoundDrawables(null, footBLEpressed, null, null);
                break;
        }
    }

    private void setBrakeState(String state){
        switch (state){
            case CONNECTED://连接成功
                statusB_txt.setText("已连接");
                brakeBLEpressed = getResources().getDrawable(R.drawable.btle_connected);
                brakeBLEpressed.setBounds(0, 0,  brakeBLEpressed.getMinimumWidth(),  brakeBLEpressed.getMinimumHeight());
                brakeBLE.setCompoundDrawables(null,  brakeBLEpressed, null, null);
                setEnableButton();
                break;

            case CONNECTING://连接中
                statusB_txt.setText("连接中");
                brakeBLEpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                brakeBLEpressed.setBounds(0, 0,  brakeBLEpressed.getMinimumWidth(),  brakeBLEpressed.getMinimumHeight());
                brakeBLE.setCompoundDrawables(null,  brakeBLEpressed, null, null);
                break;

            case DISCONNECT://连接断开
                statusB_txt.setText("断开");
                brakeBLEpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                brakeBLEpressed.setBounds(0, 0,  brakeBLEpressed.getMinimumWidth(),  brakeBLEpressed.getMinimumHeight());
                brakeBLE.setCompoundDrawables(null,  brakeBLEpressed, null, null);
                break;
        }
    }


    Runnable BleRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences shares2 = getSharedPreferences( "Brake_Decive", Activity.MODE_PRIVATE );
            if(!shares2.getBoolean("BondDecive",false))
            {
                Intent intent = new Intent(BreakActivity.this, MainActivity.class);
                startActivity(intent);
            }else
            {
                deviceBrake= bluetoothAdapter.getRemoteDevice(shares2.getString("Brake",""));
                if(deviceBrake == null)
                {
                    Toast.makeText(BreakActivity.this,"未绑定制停距离蓝牙！",Toast.LENGTH_LONG).show();
                }else{
                    DeviceModule deviceModuleBrake = new DeviceModule(deviceBrake.getName(),deviceBrake);
                    moduleBrake= deviceModuleBrake;
                    mHoldBluetoothBrake.connect(moduleBrake);
                    //controlClientBrake = SocketThread.getClient(deviceBrake);
                    Log.d("mHoldBluetoothBrake","开始连接蓝牙");
                }
            }
            handler.postDelayed(BleRunnableFoot,2000);
        }
    };
    Runnable BleRunnableFoot = new Runnable() {
        @Override
        public void run() {
            SharedPreferences shares = getSharedPreferences("Foot_Decive", Activity.MODE_PRIVATE);
            if (!shares.getBoolean("BondDecive", false)) {
                Intent intent = new Intent(BreakActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                deviceFoot = bluetoothAdapter.getRemoteDevice(shares.getString("Foot", ""));
                if(deviceFoot == null)
                {
                    Toast.makeText(BreakActivity.this,"未绑定踏板蓝牙！",Toast.LENGTH_LONG).show();
                }else{
                    DeviceModule deviceModuleFoot = new DeviceModule(deviceFoot.getName(),deviceFoot);
                    moduleFoot= deviceModuleFoot;
                    mHoldBluetoothFoot.connect(moduleFoot);
                    //controlClientFoot = SocketThread.getClient(deviceFoot);
                    Log.d("mHoldBluetoothFoot","开始连接蓝牙");
                }
            }
        }
    };
    public void setOnClickListener(final Button button, final View.OnClickListener buttonListener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (button != null) {
                    button.setOnClickListener(buttonListener);
                }
            }
        });
    }
    private class ButtonListener implements View.OnClickListener {
            @Override
            public void onClick(final View v) {
                final String METHODTAG = ".ButtonListener.onClick";
                switch (v.getId()) {
                    case R.id.breakStartBtn: {
                        Finish=true;
                        breakStartBtn.setEnabled(false);
                        startBtnpressed = getResources().getDrawable(R.drawable.start);
                        startBtnpressed.setBounds(0, 0, startBtnpressed.getMinimumWidth(), startBtnpressed.getMinimumHeight());
                        breakStartBtn.setCompoundDrawables(null, startBtnpressed, null, null);

                            senddata[0]=0x4B;
                            senddata[1]=0x53;
                            senddata[2]=0x03;
                            senddata[3]=0x00;
                            senddata[4]=0x01;
                            senddata[5]=0x00;
                            senddata[6]=0x01;
                            senddata[7]=0x3A;
                            senddata[8]=0x3B;
                            mHoldBluetoothBrake.sendData(moduleBrake,senddata);

                            senddataFoot[0]=0x4B;
                            senddataFoot[1]=0x53;
                            senddataFoot[2]=0x01;
                            senddataFoot[3]=0x00;
                            senddataFoot[4]=0x01;
                            senddataFoot[5]=0x00;
                            senddataFoot[6]=0x03;
                            senddataFoot[7]=0x3A;
                            senddataFoot[8]=0x3B;
                            mHoldBluetoothFoot.sendData(moduleFoot,senddataFoot);

                        Speed_Data.clear();
                        ASpeed_Data.clear();
                        Dis_Data.clear();
                        dynamicLineChartManager_Break.clear();

                        BreakFlag=false;
                        BreakStartFlag=false;
                        RunStartFlag=false;
                        realSpeed= 0.0f;
                        speedMax= 0.0f;
                        breakSpeed= 0.0f;
                        realASpeed= 0.0f;
                        ASpeedMax= 0.0f;
                        breakDisMax= 0.0f;
                        breakDis= 0.0f;
                        runDis= 0.0f;
                        runDisMax= 0.0f;
                        breakTime= 0.0f;
                        tabanForce= 0.0f;
                        tabanForceMax= 0.0f;
                        ChartYMax = 0.0f;
                        ChartXMax = 0.0f;

                        str_startSpeed= startSpeed_txt.getText().toString();
                        //str_tipSpeed= tipSpeed_txt.getText().toString();
                        startSpeed=Float.valueOf(str_startSpeed);
                        //tipSpeed=Float.valueOf(str_tipSpeed);
                        str_realSpeed= myformat.format(realSpeed);
                        str_speedMax= myformat.format(speedMax);
                        str_breakSpeed= myformat.format(breakSpeed);
                        str_ASpeedMax= myformat.format(ASpeedMax);
                        str_runDis= myformat.format(runDis);
                        str_breakDis= myformat.format(breakDis);
                        str_breakDisMax= myformat.format(breakDisMax);
                        str_tabanForceMax= myformat.format(tabanForceMax);
                        str_breakTime= myformat.format(breakTime);

                        //tipSpeed_txt.setText(str_tipSpeed);
                        realSpeed_txt.setText(str_realSpeed);
                        ASpeedMax_txt.setText(str_ASpeedMax);
                        breakDis_txt.setText(str_breakDis);
                        //speedMax_txt.setText(str_speedMax);
                        breakSpeed_txt.setText(str_breakSpeed);
                        runDisMax_txt.setText(str_runDis);
                        breakForceMax_txt.setText(str_tabanForceMax);
                        breakTime_txt.setText(str_breakTime);
                        breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        speedtemp=0.0f;
                        ytemp=0.0f;

                        SharedPreferences mySharedPreferences1 = getSharedPreferences( "speedValue", Activity.MODE_PRIVATE );
                        SharedPreferences.Editor editor1 = mySharedPreferences1.edit();
                        editor1.putString("startSpeed", str_startSpeed);
                        editor1.putBoolean("speedValueDecive",true);
                        editor1.apply();

                        for(int i=0;i<17;i++)
                        {
                            breakReceiveFoot[i]=0x00;
                            breakReceiveBrake[i]=0x00;
                        }
                        mainLayout.setVisibility(View.GONE);
                        count_txt.setVisibility(View.VISIBLE);
                        createAnimationThread();
                       // mTts.startSpeaking("速度已到，请制停,速度已到，请制停,速度已到，请制停", mTtsListener);
                        handler.postDelayed(ReceiveRunnable,100);
                    }
                    break;
                    case R.id.breakStopBtn: {
                        Finish = true;
                        stopBtn.setEnabled(false);
                        stopBtnpressed = getResources().getDrawable(R.drawable.stop);
                        stopBtnpressed.setBounds(0, 0, stopBtnpressed.getMinimumWidth(), stopBtnpressed.getMinimumHeight());
                        stopBtn.setCompoundDrawables(null, stopBtnpressed, null, null);

                        breakStartBtn.setEnabled(true);
                        startBtnpressed = getResources().getDrawable(R.drawable.start1);
                        startBtnpressed.setBounds(0, 0, startBtnpressed.getMinimumWidth(), startBtnpressed.getMinimumHeight());
                        breakStartBtn.setCompoundDrawables(null, startBtnpressed, null, null);

                            senddata[0] = 0x4B;
                            senddata[1] = 0x53;
                            senddata[2] = 0x03;
                            senddata[3] = 0x00;
                            senddata[4] = 0x01;
                            senddata[5] = 0x00;
                            senddata[6] = 0x05;
                            senddata[7] = 0x3A;
                            senddata[8] = 0x3B;
                            mHoldBluetoothBrake.sendData(moduleBrake,senddata);


                            senddataFoot[0]=0x4B;
                            senddataFoot[1]=0x53;
                            senddataFoot[2]=0x01;
                            senddataFoot[3]=0x00;
                            senddataFoot[4]=0x01;
                            senddataFoot[5]=0x00;
                            senddataFoot[6]=0x05;
                            senddataFoot[7]=0x3A;
                            senddataFoot[8]=0x3B;
                            mHoldBluetoothFoot.sendData(moduleFoot,senddataFoot);


                        handler.postDelayed(stopRunnable,100);
                        handler.removeCallbacks (ReceiveRunnable);
                    }
                    break;
                    case R.id.breakPrintBtn: {
                        printBtn.setEnabled(false);
                        printBtnpressed = getResources().getDrawable(R.drawable.print);
                        printBtnpressed.setBounds(0, 0, printBtnpressed.getMinimumWidth(), printBtnpressed.getMinimumHeight());
                        printBtn.setCompoundDrawables(null, printBtnpressed, null, null);
                        if (Finish) {
                            if (printDataService == null) {           //首次连接打印机
                                SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                                if (!shares.getBoolean("BondPrinter", false)) {
                                    //Toast.makeText(BreakActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(BreakActivity.this.getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                                handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                            } else {          //打印数据
                                PrintMeasureData();
                            }
                        } else {
                            Toast.makeText(BreakActivity.this, "没有可以打印的数据", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case R.id.breakSaveBtn: {
                        saveBtn.setEnabled(false);
                        saveBtnpressed = getResources().getDrawable(R.drawable.save);
                        saveBtnpressed.setBounds(0, 0, saveBtnpressed.getMinimumWidth(), saveBtnpressed.getMinimumHeight());
                        saveBtn.setCompoundDrawables(null, saveBtnpressed, null, null);
                        breakAdd(str_breakSpeed,str_ASpeedMax,str_breakDisMax,str_breakTime,str_tabanForceMax);
                    }
                    break;
                    case R.id.breakExportBtn: {
                        exportBtn.setEnabled(false);
                        exportBtnpressed = getResources().getDrawable(R.drawable.export);
                        exportBtnpressed.setBounds(0, 0, exportBtnpressed.getMinimumWidth(), exportBtnpressed.getMinimumHeight());
                        exportBtn.setCompoundDrawables(null, exportBtnpressed, null, null);
                        CreatePdf();
                        Toast.makeText(BreakActivity.this, "数据已导出到手机根目录/Documents/叉车紧急制动距离检测报告", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case R.id.breakScanBtn: {
                        Intent intent = new Intent(BreakActivity.this, BreakSaveActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
                    case R.id.breakExitBtn: {
                        finish();
                        ActivityCollector.finishAll();
                    }
                    break;
                    case R.id.breakBackBtn: {
                        Intent intent1 = new Intent(BreakActivity.this, ChacheActivity.class);
                        startActivity(intent1);
                        finish();
                    }
                    break;
                    default: {
                    }
                    break;
                }
                handler.postDelayed(sendRunnable, 1000);
            }
        }


    Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            int index=(int)msg.obj;
            if(index>5){
                mainLayout.setVisibility(View.VISIBLE);
                count_txt.setVisibility(View.GONE);
                handler.postDelayed(ReceiveRunnable,1000);
            }else{
                count_txt.setImageResource(arrayId[index]);
                count_txt.setScaleX(0);
                count_txt.setScaleY(0);
            }
            String str_scaleX;
            String str_scaleY;
            str_scaleX="scaleX";
            str_scaleY="scaleY";
            //设置X方向上的缩放动画
            ObjectAnimator oa1=ObjectAnimator.ofFloat(count_txt,str_scaleX,0,1);
            oa1.setDuration(500);

            //设置Y方向上的缩放动画
            ObjectAnimator oa2=ObjectAnimator.ofFloat(count_txt,str_scaleY,0,1);
            oa2.setDuration(500);

            AnimatorSet set=new AnimatorSet();
            set.playTogether(oa1,oa2);
            set.start();
        }
    };
    public void createAnimationThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                for (int i = 1; i <= 6; i++) {
                    Message message = myHandler.obtainMessage();
                    message.obj = i;
                    myHandler.sendMessage(message);
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
                case android.R.id.home:
                    finish();
                    break;
            }
            return true;
        }

    private void showRatioDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(BreakActivity.this);
        //normalDialog.setIcon(R.drawable..);
        normalDialog.setTitle("提示！");
        normalDialog.setMessage("       请确保测量模块放置在水平面上！");
        normalDialog.setPositiveButton("确  定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.postDelayed(ReceiveRunnable,1000);
                        Toast.makeText(BreakActivity.this, "初始定位设置完毕", Toast.LENGTH_SHORT).show();
                    }
                });
        normalDialog.show();
    }

    Runnable ReceiveRunnable = new Runnable() {
            @Override
            public void run() {
            int BrakeRevNum=0;
            int FootRevNum=0;
            String[] str_ReceiveData=new String[19];
            String[] str_ReceiveDataBrake=new String[17];
            String[] str_ReceiveDataFoot=new String[17];
            byte[] mReceiveBrake=new byte[17];
            byte[] mReceiveFoot=new byte[17];
        if((breakReceiveBrake.length>=17)&&(breakReceiveFoot.length>=17))
        {
            for(int i=0;i<breakReceiveBrake.length;i++)
            {
                if(breakReceiveBrake[BrakeRevNum]==0x4B)
                {
                    break;
                }else{
                    BrakeRevNum++;
                }
            }

            for(int i=0;i<breakReceiveFoot.length;i++)
            {
                if(breakReceiveFoot[FootRevNum]==0x4B)
                {
                    break;
                }else{
                    FootRevNum++;
                }
            }

            for(int i=0;i<17;i++)
            {
                if((BrakeRevNum+i)<breakReceiveBrake.length)
                {
                    mReceiveBrake[i] =breakReceiveBrake[BrakeRevNum+i];
                }else{
                    mReceiveBrake[i] =breakReceiveBrake[BrakeRevNum+i-breakReceiveBrake.length];
                }
            }

            for(int i=0;i<17;i++)
            {
                if((FootRevNum+i)<breakReceiveFoot.length)
                {
                    mReceiveFoot[i] =breakReceiveFoot[FootRevNum+i];
                }else{
                    mReceiveFoot[i] =breakReceiveFoot[FootRevNum+i-breakReceiveFoot.length];
                }
            }

            for(int i=0;i<17;i++)
            {
                str_ReceiveDataBrake[i]=myformat.format(mReceiveBrake[i]);
                str_ReceiveDataFoot[i]=myformat.format(mReceiveFoot[i]);
            }
            Log.d("ReceiveDataBrake","     "+str_ReceiveDataBrake[0]+"  "+str_ReceiveDataBrake[1]+" "+str_ReceiveDataBrake[2]+"  "+str_ReceiveDataBrake[3]+" "+str_ReceiveDataBrake[4]+"  "+str_ReceiveDataBrake[5]+" "+
                    str_ReceiveDataBrake[6]+"  "+str_ReceiveDataBrake[7]+" "+str_ReceiveDataBrake[8]+"  "+str_ReceiveDataBrake[9]+" "+str_ReceiveDataBrake[10]+"  "+str_ReceiveDataBrake[11]+" "+
                    str_ReceiveDataBrake[12]+"  "+str_ReceiveDataBrake[13]+" "+str_ReceiveDataBrake[14]+str_ReceiveDataBrake[15]+str_ReceiveDataBrake[16]+"  ");

            Log.d("ReceiveDataFoot","     "+str_ReceiveDataFoot[0]+"  "+str_ReceiveDataFoot[1]+" "+str_ReceiveDataFoot[2]+"  "+str_ReceiveDataFoot[3]+" "+str_ReceiveDataFoot[4]+"  "+str_ReceiveDataFoot[5]+" "+
                    str_ReceiveDataFoot[6]+"  "+str_ReceiveDataFoot[7]+" "+str_ReceiveDataFoot[8]+"  "+str_ReceiveDataFoot[9]+" "+str_ReceiveDataFoot[10]+"  "+str_ReceiveDataFoot[11]+" "+
                    str_ReceiveDataFoot[12]+"  "+str_ReceiveDataFoot[13]+" "+str_ReceiveDataFoot[14]+str_ReceiveDataFoot[15]+str_ReceiveDataFoot[16]+"  ");

            if((mReceiveBrake[0]==0x4B) && (mReceiveBrake[1]==0x53)) {
                switch (mReceiveBrake[2]) {
                    case 0x03:
                        for(int i=0;i<15;i++)
                        {
                            breakReceive[i]=mReceiveBrake[i];
                        }
                        break;
                    default : break;
                }
            }

            if((mReceiveFoot[0]==0x4B) && (mReceiveFoot[1]==0x53)) {
                switch (breakReceiveFoot[2]) {
                    case 0x01:
                        breakReceive[15]=mReceiveFoot[4];
                        breakReceive[16]=mReceiveFoot[5];
                        breakReceive[17]=mReceiveFoot[15];
                        breakReceive[18]=mReceiveFoot[16];
                        break;
                    default : break;
                }
            }

            for(int i=0;i<19;i++)
            {
                str_ReceiveData[i]=myformat.format(breakReceive[i]);
            }

            Log.d("breakActivity","     "+str_ReceiveData[0]+"  "+str_ReceiveData[1]+" "+str_ReceiveData[2]+"  "+str_ReceiveData[3]+" "+str_ReceiveData[4]+"  "+str_ReceiveData[5]+" "+
                    str_ReceiveData[6]+"  "+str_ReceiveData[7]+" "+str_ReceiveData[8]+"  "+str_ReceiveData[9]+" "+str_ReceiveData[10]+"  "+str_ReceiveData[11]+" "+
                    str_ReceiveData[12]+"  "+str_ReceiveData[13]+" "+str_ReceiveData[14]+str_ReceiveData[15]+str_ReceiveData[16]+str_ReceiveData[17]+str_ReceiveData[18]+"  ");
            if((breakReceive[0]==0x4B) && (breakReceive[1]==0x53)) {
                switch (breakReceive[2]) {
                    case 0x03:
                        int tmp = 0;
                        tmp = (char) (breakReceive[10] & 0xFF) * 256 + (char) (breakReceive[9] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) ytemp = (float) tmp/ 1000.0f;
                        else ytemp= (float) (0x10000 - tmp)/ 1000.0f * (-1.0f);

                        tmp = (char) (breakReceive[13] & 0xFF) * 256 + (char) (breakReceive[14] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) speedtemp = (float) tmp/ 10.0f;
                        else speedtemp = (float) (0x10000 - tmp)/ 10.0f * (-1.0f);

                        tmp = (char) (breakReceive[15] & 0xFF) * 256 + (char) (breakReceive[16] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) tabanForce = (float) tmp/ 10.0f;
                        else tabanForce = (float) (0x10000 - tmp)/ 10.0f * (-1.0f);
                        Log.d("breakActivity","     "+"BrakeRevNum="+myformat.format(BrakeRevNum)+"     "+"FootRevNum="+myformat.format(FootRevNum)+"     "+"ytemp="+myformat.format(ytemp)+"  "+"speedtemp="+myformat.format(speedtemp));

                        if(Math.abs(ytemp * 9.8f)<10.0f)
                        {
                            realASpeed=ytemp * 9.8f;
                        }

                        if(Math.abs(tabanForce)<10.0f)
                        {
                            RunStartFlag=true;
                        }
                        if(RunStartFlag)
                        {
                            if(Math.abs(speedtemp)<60.0f)
                            {
                                realSpeed = speedtemp;
                            }
                        }
                        Log.d("breakActivity","速度值："+myformat.format(realSpeed)+"km/h");
                       // V0 = realSpeed;

                        if(speedMax<Math.abs(realSpeed))
                        {
                            speedMax=Math.abs(realSpeed);
                        }
                        if(Math.abs(realSpeed)>0.5f)
                        {
                            runDis=runDis+realSpeed* 0.01f/3.6f;//运行距离0.0012
                        }

                        if(runDisMax<Math.abs(runDis))
                        {
                            runDisMax= Math.abs(runDis);
                        }
                        Log.d("breakActivity","位移值："+myformat.format(runDisMax)+"m");
                        Speed_Data.add(Math.abs(realSpeed));
                        ASpeed_Data.add(realASpeed);
                        Dis_Data.add(Math.abs(breakDisMax));

                        str_realSpeed = myformat.format(Math.abs(realSpeed));
                        realSpeed_txt.setText(str_realSpeed);

                        str_speedMax = myformat.format(Math.abs(speedMax));
                       // speedMax_txt.setText(str_speedMax);

                        str_runDis = myformat.format(runDisMax);
                        runDisMax_txt.setText(str_runDis);

                        if((Math.abs(tabanForce)>30.0f)&&(Math.abs(speedMax)>5))
                        {
                            BreakFlag=true;
                            if(breakSpeed<Math.abs(realSpeed))
                            {
                                breakSpeed=Math.abs(realSpeed);
                            }
                        }

                        if(BreakFlag)
                        {
                            if(BreakStartFlag==false)
                            {
                                if((Math.abs(tabanForce)>10.0f)&&(Math.abs(realSpeed)<(breakSpeed*startSpeed)))
                                {
                                    BreakStartFlag=true;
                                    breakDis=runDis;
                                }
                            }

                            if(BreakStartFlag)
                            {
                                str_breakSpeed = myformat.format(Math.abs(breakSpeed));
                                breakSpeed_txt.setText(str_breakSpeed);
                                if(ASpeedMax<Math.abs(realASpeed))
                                {
                                    ASpeedMax=Math.abs(realASpeed);
                                }
                                if(tabanForceMax<tabanForce)
                                {
                                    tabanForceMax=tabanForce;
                                }
                                str_ASpeedMax = myformat.format(Math.abs(ASpeedMax));
                                ASpeedMax_txt.setText(str_ASpeedMax);
                                str_tabanForceMax = myformat.format(tabanForceMax);
                                breakForceMax_txt.setText(str_tabanForceMax);
                                if(Math.abs(realSpeed) >0.5f)
                                {
                                    breakDisMax = Math.abs(runDis - breakDis);
                                    str_breakDisMax = myformat.format(Math.abs(breakDisMax));
                                    breakDis_txt.setText(str_breakDisMax);

                                    breakTime = (breakTime + 0.01f);
                                    str_breakTime = myformat.format(breakTime);
                                    breakTime_txt.setText(str_breakTime);
                                }else
                                {
                                    realSpeed=0.0f;
                                    str_realSpeed = myformat.format(realSpeed);
                                    realSpeed_txt.setText(str_realSpeed);
                                }
                            }
                            if(Math.abs(realSpeed)<(Math.abs(breakSpeed)*0.1f))
                            {
                                handler.postDelayed(stopRunnable,100);
                                handler.removeCallbacks (ReceiveRunnable);
                            }

                        }
                        if(ChartYMax<Math.abs(realASpeed))
                        {
                            ChartYMax =Math.abs(realASpeed);
                        }

                        if(ChartYMax<Math.abs(speedMax))
                        {
                            ChartYMax =Math.abs(speedMax);
                        }

                        if(ChartYMax<Math.abs(breakDisMax))
                        {
                            ChartYMax =Math.abs(breakDisMax);
                        }

                        ChartXMax =ChartXMax+0.01f;
                        if(ChartXMax>10000)
                        {
                            ChartXMax=0;
                        }
                        //str_speedMax = myformat.format(Math.abs(ChartXMax));
                        //speedMax_txt.setText(str_speedMax);
                        dynamicLineChartManager_Break.setYAxis(ChartYMax*1.2f, ChartYMax*1.2f*(-1.0f), 4);
                        dynamicLineChartManager_Break.setXAxis(ChartXMax*1.2f, 0, 10,0);
                        dynamicLineChartManager_Break.addEntry(realSpeed, realASpeed,breakDisMax);
                        break;
                    default : break;
                }
            }
        }
        handler.postDelayed(ReceiveRunnable,10);
      }
   };

    private Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks (ReceiveRunnable);
            str_realSpeed = myformat.format(Math.abs(0.0f));
            realSpeed_txt.setText(str_realSpeed);
            switch (str_chacheGroup)
            {
                case "A1:除A2、B、和C组外的所有车辆，额定起重量/载重量＜16000kg或满载质量＜35000kg，两者取较大值":

                    if(Math.abs(breakSpeed)<5.0f)
                    {
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)*Math.abs(breakSpeed)/23.6f;
                        str_breakDis0= myformat.format(breakDis0);

                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                         }

                    }else if(Math.abs(breakSpeed)<13.4f)
                    {
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)/4.7f;
                        str_breakDis0= myformat.format(breakDis0);
                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }

                    }else{
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)*Math.abs(breakSpeed)/63.6f;
                        str_breakDis0= myformat.format(breakDis0);
                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }
                    }
                    break;
                case "A2:除B和C组外的所有车辆，额定起重量/载重量≥16000kg或满载质量≥35000kg":

                    if(Math.abs(breakSpeed)<5.0f)
                    {
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)*Math.abs(breakSpeed)/19.1f;
                        str_breakDis0= myformat.format(breakDis0);

                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }

                    }else if(Math.abs(breakSpeed)<13.4f)
                    {
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)/3.8f;
                        str_breakDis0= myformat.format(breakDis0);
                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                    }

                    }else{
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)*Math.abs(breakSpeed)/50.9f;
                        str_breakDis0= myformat.format(breakDis0);
                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }
                    }
                    break;
                case "B1:单轮制动或双轮制动牵引车":
                    if(Math.abs(breakSpeed)<5.0f)
                    {
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)*Math.abs(breakSpeed)/33.1f;
                        str_breakDis0= myformat.format(breakDis0);

                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }

                    }else if(Math.abs(breakSpeed)<13.4f)
                    {
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)/6.6f;
                        str_breakDis0= myformat.format(breakDis0);
                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }

                    }else{
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)*Math.abs(breakSpeed)/89.0f;
                        str_breakDis0= myformat.format(breakDis0);
                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }
                    }

                    break;
                case "B2:三轮制动或四轮制动的牵引车":
                    if(Math.abs(breakSpeed)<5.0f)
                    {
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)*Math.abs(breakSpeed)/47.3f;
                        str_breakDis0= myformat.format(breakDis0);

                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }

                    }else if(Math.abs(breakSpeed)<13.4f)
                    {
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)/9.5f;
                        str_breakDis0= myformat.format(breakDis0);
                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }

                    }else{
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)*Math.abs(breakSpeed)/127.1f;
                        str_breakDis0= myformat.format(breakDis0);
                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }
                    }

                    break;
                case "C:操作台可升至1200mm以上的车辆和专门设计的带起升载荷运行的车辆":
                    if(Math.abs(breakSpeed)<4.0f)
                    {
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)*Math.abs(breakSpeed)/11.4f;
                        str_breakDis0= myformat.format(breakDis0);

                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }
                    }else if(Math.abs(breakSpeed)<13.4f)
                    {
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)/2.8f;
                        str_breakDis0= myformat.format(breakDis0);
                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }

                    }else{
                        breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)*Math.abs(breakSpeed)/38.1f;
                        str_breakDis0= myformat.format(breakDis0);
                        if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                        {
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }else{
                            Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                            breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }
                    }

                    break;
                case "D:越野车辆":
                    breakDis0=0.15f*Math.abs(breakSpeed)+Math.abs(breakSpeed)*Math.abs(breakSpeed)/63.5f;
                    str_breakDis0= myformat.format(breakDis0);

                    if(Math.abs(breakDisMax)<Math.abs(breakDis0))
                    {
                        Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"小于"+"标准要求"+str_breakDis0+"满足标准要求",Toast.LENGTH_LONG).show();
                        breakDis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                    }else{
                        Toast.makeText(BreakActivity.this,"制动距离"+str_breakDisMax+"大于"+"标准要求"+str_breakDis0+"不满足标准要求",Toast.LENGTH_LONG).show();
                        breakDis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                    }
                    break;
            }
        }
    };

        private Runnable sendRunnable = new Runnable() {
            @Override
            public void run() {
                stopBtn.setEnabled(true);
                stopBtnpressed = getResources().getDrawable(R.drawable.stop1);
                stopBtnpressed.setBounds(0, 0, stopBtnpressed.getMinimumWidth(), stopBtnpressed.getMinimumHeight());
                stopBtn.setCompoundDrawables(null, stopBtnpressed, null, null);

                printBtn.setEnabled(true);
                printBtnpressed = getResources().getDrawable(R.drawable.print1);
                printBtnpressed.setBounds(0, 0, printBtnpressed.getMinimumWidth(), printBtnpressed.getMinimumHeight());
                printBtn.setCompoundDrawables(null, printBtnpressed, null, null);

                saveBtn.setEnabled(true);
                saveBtnpressed = getResources().getDrawable(R.drawable.save1);
                saveBtnpressed.setBounds(0, 0, saveBtnpressed.getMinimumWidth(), saveBtnpressed.getMinimumHeight());
                saveBtn.setCompoundDrawables(null, saveBtnpressed, null, null);

                exportBtn.setEnabled(true);
                exportBtnpressed = getResources().getDrawable(R.drawable.export1);
                exportBtnpressed.setBounds(0, 0, exportBtnpressed.getMinimumWidth(), exportBtnpressed.getMinimumHeight());
                exportBtn.setCompoundDrawables(null, exportBtnpressed, null, null);
            }
        };
    private void setEnableButton()
    {
        breakStartBtn.setEnabled(true);
        startBtnpressed = getResources().getDrawable(R.drawable.start1);
        startBtnpressed.setBounds(0, 0, startBtnpressed.getMinimumWidth(), startBtnpressed.getMinimumHeight());
        breakStartBtn.setCompoundDrawables(null, startBtnpressed, null, null);

        stopBtn.setEnabled(true);
        stopBtnpressed = getResources().getDrawable(R.drawable.stop1);
        stopBtnpressed.setBounds(0, 0, stopBtnpressed.getMinimumWidth(), stopBtnpressed.getMinimumHeight());
        stopBtn.setCompoundDrawables(null, stopBtnpressed, null, null);

        printBtn.setEnabled(true);
        printBtnpressed = getResources().getDrawable(R.drawable.print1);
        printBtnpressed.setBounds(0, 0, printBtnpressed.getMinimumWidth(), printBtnpressed.getMinimumHeight());
        printBtn.setCompoundDrawables(null, printBtnpressed, null, null);

        saveBtn.setEnabled(true);
        saveBtnpressed = getResources().getDrawable(R.drawable.save1);
        saveBtnpressed.setBounds(0, 0, saveBtnpressed.getMinimumWidth(), saveBtnpressed.getMinimumHeight());
        saveBtn.setCompoundDrawables(null, saveBtnpressed, null, null);

        exportBtn.setEnabled(true);
        exportBtnpressed = getResources().getDrawable(R.drawable.export1);
        exportBtnpressed.setBounds(0, 0, exportBtnpressed.getMinimumWidth(), exportBtnpressed.getMinimumHeight());
        exportBtn.setCompoundDrawables(null, exportBtnpressed, null, null);
    }
        private void setButton()
        {
            breakStartBtn.setEnabled(false);
            startBtnpressed = getResources().getDrawable(R.drawable.start);
            startBtnpressed.setBounds(0, 0, startBtnpressed.getMinimumWidth(), startBtnpressed.getMinimumHeight());
            breakStartBtn.setCompoundDrawables(null, startBtnpressed, null, null);

            stopBtn.setEnabled(false);
            stopBtnpressed = getResources().getDrawable(R.drawable.stop);
            stopBtnpressed.setBounds(0, 0, stopBtnpressed.getMinimumWidth(), stopBtnpressed.getMinimumHeight());
            stopBtn.setCompoundDrawables(null, stopBtnpressed, null, null);

            printBtn.setEnabled(false);
            printBtnpressed = getResources().getDrawable(R.drawable.print);
            printBtnpressed.setBounds(0, 0, printBtnpressed.getMinimumWidth(), printBtnpressed.getMinimumHeight());
            printBtn.setCompoundDrawables(null, printBtnpressed, null, null);

            saveBtn.setEnabled(false);
            saveBtnpressed = getResources().getDrawable(R.drawable.save);
            saveBtnpressed.setBounds(0, 0, saveBtnpressed.getMinimumWidth(), saveBtnpressed.getMinimumHeight());
            saveBtn.setCompoundDrawables(null, saveBtnpressed, null, null);

            exportBtn.setEnabled(false);
            exportBtnpressed = getResources().getDrawable(R.drawable.export);
            exportBtnpressed.setBounds(0, 0, exportBtnpressed.getMinimumWidth(), exportBtnpressed.getMinimumHeight());
            exportBtn.setCompoundDrawables(null, exportBtnpressed, null, null);
        }
    Runnable PrinterRunnable = new Runnable() {
        @Override
        public void run() {
            if(PrintConnect){         //连接成功并已经打印数据，则关闭蓝牙
                handler.removeCallbacks(PrinterRunnable);
                PrintConnect = false;
            }
            else{
                SharedPreferences shares = getSharedPreferences( "BLE_Info", Activity.MODE_PRIVATE );
                if(shares.getBoolean("BondPrinter",false)){
                    printDataService = new PrintDataService(BreakActivity.this,shares.getString("Printer",""));
                    //Toast.makeText(overActivity.this,"蓝牙打印机连接中...",Toast.LENGTH_LONG).show();
                }
                if(printDataService != null){
                    PrintConnect = printDataService.connect();
                    if(PrintConnect){
                        Toast.makeText(BreakActivity.this,"蓝牙打印机连接成功...",Toast.LENGTH_LONG).show();
                        handler.removeCallbacks (PrinterRunnable);
                    }
                }
                handler.postDelayed(PrinterRunnable,100);
            }
        }
    };
    //打印测试数据
    private void PrintMeasureData(){
        printDataService.send("\n*******************************\n");
        printDataService.send("叉车紧急制动距离检测结果");
        printDataService.send("\n*******************************\n");
        SimpleDateFormat formatter   =   new SimpleDateFormat("测试时间"+":yyyy-MM-dd  HH:mm:ss\n", Locale.CHINA);
        Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
        String   str   =   formatter.format(curDate);
        printDataService.send(str);
        printDataService.send("受检单位"+": "+str_company+"\n");//
        printDataService.send("设备编号"+": "+ str_number+"\n");//
        printDataService.send("车牌编号"+": "+ str_chacheNumber+"\n");//
        printDataService.send("车辆类型"+": "+ str_chacheType+"\n");//
        printDataService.send("车辆组别"+": "+ str_chacheGroup+"\n");//
        printDataService.send("制动速度"+": "+ str_breakSpeed+"km/h"+"\n");//
        printDataService.send("最大制动减速度"+": "+ str_ASpeedMax+"m/s^2"+"\n");//
        printDataService.send("制动距离"+": "+ str_breakDisMax+"m"+"\n");//
        printDataService.send("制动时间"+": "+ str_breakTime+"s"+"\n");//
        printDataService.send("制动时刻最大踏板力"+": "+ str_tabanForceMax+"N"+"\n");//
        printDataService.send("*******************************\n\n\n\n");
        Toast.makeText(BreakActivity.this,"打印完成！",Toast.LENGTH_SHORT).show();
    }
        //权限读写
        private static final int REQUEST_EXTERNAL_STORAGE = 1;
        private static String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        public static void verifyStoragePermissions(Activity activity) {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE);
            }
        }

        public void notifySystemToScan(String filePath) {         //将文件修改信息通知到系统中
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File file = new File(filePath);

            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            BreakActivity.this.sendBroadcast(intent);

        }
        //创建PDF文件-----------------------------------------------------------------
        Document doc;
        private void CreatePdf(){
            verifyStoragePermissions(BreakActivity.this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                    FileOutputStream fos;
                    Paragraph pdfcontext;
                    try {
                        //创建目录
                        File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车紧急制动距离检测报告"+ File.separator);
                        if (!destDir.exists()) {
                            destDir.mkdirs();
                            notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车紧急制动距离检测报告"+ File.separator );
                        }
                        Log.d("breakActivity",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
                        Uri uri = Uri.fromFile(destDir);
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                        getApplication().getApplicationContext().sendBroadcast(intent);
                        Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
                        fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车紧急制动距离检测报告" + File.separator + curDate.toString ()+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
                        PdfWriter.getInstance(doc, fos);
                        doc.open();
                        doc.setPageCount(1);
                        pdfcontext = new Paragraph("叉车紧急制动距离检测报告",setChineseTitleFont());
                        pdfcontext.setAlignment(Element.ALIGN_CENTER);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        //创建一个有3列的表格

                        //创建一个有3列的表格
                        PdfPTable table = new PdfPTable(3);
                        table.setWidthPercentage(99);
                        //定义一个表格单元
                        PdfPCell cell = new PdfPCell();
                        cell.setMinimumHeight(20);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPTable mtable = new PdfPTable(3);
                        mtable.setWidthPercentage(99);
                        mtable.setWidths(new float[]{300,200,200});
                        cell.setColspan(1);
                        cell.setBackgroundColor(new BaseColor(255,255,255));
                        cell.setPhrase(new Phrase("检测时间：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(curDate.toString (),setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                        cell.setPhrase(new Phrase("受检单位：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(str_company,setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                        cell.setPhrase(new Phrase("设备编号：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(str_number,setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                        cell.setPhrase(new Phrase("车牌编号：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(str_chacheNumber,setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                        cell.setPhrase(new Phrase("车辆类型：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(str_chacheType,setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                        cell.setPhrase(new Phrase("车辆组别：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(str_chacheGroup,setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                        cell.setPhrase(new Phrase("制动速度：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(str_breakSpeed,setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("km/h",setChineseFont()))    ;mtable.addCell(cell);

                        cell.setPhrase(new Phrase("最大制动减速度：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(str_ASpeedMax,setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("m/s^2",setChineseFont()))    ;mtable.addCell(cell);

                        cell.setPhrase(new Phrase("制动距离：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(str_breakDisMax,setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                        cell.setPhrase(new Phrase("制动时间：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(str_breakTime,setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("s",setChineseFont()))    ;mtable.addCell(cell);

                        cell.setPhrase(new Phrase("制动时刻最大踏板力：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(str_tabanForceMax,setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);

                        doc.add(mtable);
                        doc.close();
                        fos.flush();
                        fos.close();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车紧急制动距离检测报告" +  File.separator + curDate.toString () +".pdf");
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        /**
         * 设置PDF字体(较为耗时)
         */
        public Font setChineseFont() {
            BaseFont bf = null;
            Font fontChinese = null;
            try {
                // STSong-Light : Adobe的字体
                // UniGB-UCS2-H : pdf 字体
                bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                        BaseFont.NOT_EMBEDDED);
                fontChinese = new Font(bf, 12, Font.NORMAL);
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fontChinese;
        }

        public Font setChineseTitleFont() {
            BaseFont bf = null;
            Font fontChinese = null;
            try {
                // STSong-Light : Adobe的字体
                // UniGB-UCS2-H : pdf 字体
                bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                        BaseFont.NOT_EMBEDDED);
                fontChinese = new Font(bf, 20, Font.NORMAL);
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fontChinese;
        }

        public class DynamicLineChartManager implements OnChartGestureListener {
            private LineChart lineChart;
            private YAxis leftAxis;
            private YAxis rightAxis;
            private XAxis xAxis;
            private LineData lineData;
            private LineDataSet lineDataSet,lineDataSet1,lineDataSet2;
            private LineChartMarkView mv;
            private Legend legend;
            private int position;
            private void setData(ArrayList<Float> value, ArrayList<Float> value1,ArrayList<Float> value2,float index) {
                ArrayList<Entry> values = new ArrayList<>();
                for (int i = 0; i < value.size()- 1; i++) {
                    values.add(new Entry((float)(i*index), (float) value.get(i)));
                }
                ArrayList<Entry> values1 = new ArrayList<>();
                for (int i = 0; i < value1.size() - 1; i++) {
                    values1.add(new Entry((float)(i*index), (float) value1.get(i)));
                }
                ArrayList<Entry> values2 = new ArrayList<>();
                for (int i = 0; i < value2.size() - 1; i++) {
                    values2.add(new Entry((float)(i*index), (float) value2.get(i)));
                }
                lineDataSet.setValues(values);
                lineDataSet1.setValues(values1);
                lineDataSet2.setValues(values2);
                lineChart.setData(lineData);
                lineChart.invalidate();
            }
            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
            }
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }
            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                chartBreak.setVisibility(View.VISIBLE);
            }
            @Override
            public void onChartSingleTapped(MotionEvent me) {
            }
            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            }
            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                chartBreak.setVisibility (View.VISIBLE);
            }
            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }
            //一条曲线
            public DynamicLineChartManager(LineChart mLineChart, String name, int color, int position) {
                this.lineChart = mLineChart;
                this.position = position;
                //滑动缩放相关
                lineChart.setOnChartGestureListener(this);
                //数据样式
                lineDataSet = new LineDataSet(null, "速度");
                lineDataSet.setLineWidth(1.0f);
                lineDataSet.setDrawCircles(false);
                lineDataSet.setColor(Color.BLUE);
                lineDataSet.setHighLightColor(Color.WHITE);
                //设置曲线填充
                lineDataSet.setDrawFilled(false);
                lineDataSet.setDrawCircleHole(false);
                lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSet.setDrawValues(false);
                lineDataSet.setMode(LineDataSet.Mode.LINEAR);

                //数据样式
                lineDataSet1 = new LineDataSet(null, "减速度");
                lineDataSet1.setLineWidth(1.0f);
                lineDataSet1.setDrawCircles(false);
                lineDataSet1.setColor(Color.RED);
                lineDataSet1.setHighLightColor(Color.WHITE);
                //设置曲线填充
                lineDataSet1.setDrawFilled(false);
                lineDataSet1.setDrawCircleHole(false);
                lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSet1.setDrawValues(false);
                lineDataSet1.setMode(LineDataSet.Mode.LINEAR);

                //数据样式
                lineDataSet2 = new LineDataSet(null, "制停距离");
                lineDataSet2.setLineWidth(1.0f);
                lineDataSet2.setDrawCircles(false);
                lineDataSet2.setColor(Color.GREEN);
                lineDataSet2.setHighLightColor(Color.WHITE);
                //设置曲线填充
                lineDataSet2.setDrawFilled(false);
                lineDataSet2.setDrawCircleHole(false);
                lineDataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSet2.setDrawValues(false);
                lineDataSet2.setMode(LineDataSet.Mode.LINEAR);

                lineData = new LineData(lineDataSet,lineDataSet1,lineDataSet2);
                lineChart.setData(lineData);
                lineChart.animateX(1500);
                lineChart.invalidate();
            }
            private void desChart(String name, int y) {
                //图标样式
                com.github.mikephil.charting.components.Description description = new Description();
                description.setText(name);
                description.setPosition(650,y);
                lineChart.setDescription(description);
                lineChart.setDrawGridBackground(false);
                lineChart.setDrawBorders(false);
            }

            private void addEntry(float speed,float Aspeed,float Dis){
                //lineData = chartSpeed.getData();
                Entry entry = new Entry(lineDataSet.getEntryCount()*0.0172f,speed);
                Entry entry1 = new Entry(lineDataSet1.getEntryCount()*0.0172f,Aspeed);
                Entry entry2 = new Entry(lineDataSet2.getEntryCount()*0.0172f,Dis);
                lineData.addEntry(entry, 0);
                lineData.addEntry(entry1, 1);
                lineData.addEntry(entry2, 2);
                chartBreak.notifyDataSetChanged();
                chartBreak.moveViewToX(0.00f);
            }
            private void freshChart(float speed,float Aspeed) {
                lineChart.fitScreen();
            }
            private void clear() {
                lineDataSet.clear();
                lineDataSet1.clear();
                lineDataSet2.clear();
            }

            public void setYAxis(float max, float min, int labelCount) {
                if (max < min) {
                    return;
                }
                leftAxis = lineChart.getAxisLeft();
                rightAxis = lineChart.getAxisRight();
                rightAxis.setEnabled(false);
                leftAxis.setAxisMinimum(0f);
                leftAxis.setGranularityEnabled(false);
                leftAxis.setDrawGridLines(false);
                leftAxis.setTextColor(Color.WHITE);
                leftAxis.setAxisMaximum(max);
                leftAxis.setAxisMinimum(min);
                leftAxis.setDrawLimitLinesBehindData(true);
                leftAxis.setLabelCount(labelCount, false);
                rightAxis.setEnabled(false);

                //图例
                legend = lineChart.getLegend();
                legend.setForm(Legend.LegendForm.LINE);
                legend.setTextSize(10f);
                legend.setDrawInside(true);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                lineChart.invalidate();
            }

            public void setLegend(){
                Legend LegFre = lineChart.getLegend();
                LegFre.setForm(Legend.LegendForm.LINE);
                LegFre.setTextSize(12f);
                LegFre.setTextColor(Color.BLUE);
                LegFre.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
                LegFre.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                LegFre.setOrientation(Legend.LegendOrientation.VERTICAL);
                LegFre.setDrawInside(false);
            }

            public void setXAxis(float max, float min, int labelCount,int pos) {
                if (max < min) {
                    return;
                }
                xAxis = lineChart.getXAxis();
                if(pos==0){
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                }else{
                    xAxis.setPosition(XAxis.XAxisPosition.TOP);
                }

                xAxis.setDrawLabels(true);
                xAxis.setDrawGridLines(false);
                xAxis.setAxisMaximum(max);
                xAxis.setAxisMinimum(min);
                xAxis.setTextColor(Color.WHITE);
                xAxis.setLabelCount(labelCount, false);
                lineChart.invalidate();
            }

            public void setHightLimitLine(float high, String name) {
                if (name == null) {
                    name = "高限制线";
                }
                LimitLine hightLimit = new LimitLine(high, name);
                hightLimit.setLineWidth(0.1f);
                hightLimit.setTextSize(10f);
                hightLimit.enableDashedLine(8.0f, 4.0f, 4.0f);
                leftAxis.removeAllLimitLines(); //先清除原来的线，后面再加上，防止add方法重复绘制
                leftAxis.addLimitLine(hightLimit);
                hightLimit.setLineColor(Color.WHITE);
                lineChart.invalidate();
            }
            public void setLowLimitLine(float low, String name) {
                if (name == null) {
                    name = "低限制线";
                }
                LimitLine hightLimit = new LimitLine(low, name);
                hightLimit.setLineWidth(0.1f);
                hightLimit.setTextSize(10f);
                hightLimit.setLineColor(Color.WHITE);
                hightLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                leftAxis.removeAllLimitLines(); //先清除原来的线，后面再加上，防止add方法重复绘制
                leftAxis.addLimitLine(hightLimit);
                hightLimit.enableDashedLine(8.0f, 4.0f, 4.0f);
                lineChart.invalidate();
            }

            public void setXHightLimitLine(float high, String name) {
                LimitLine hightLimit = new LimitLine(high, name);
                hightLimit.setLineWidth(0.1f);
                hightLimit.setTextSize(10f);
                hightLimit.enableDashedLine(8.0f, 4.0f, 4.0f);

                xAxis.addLimitLine(hightLimit);
                hightLimit.setLineColor(Color.WHITE);
                lineChart.invalidate();
            }

            public void ClearXHightLimitLine(float high, String name) {
                LimitLine hightLimit = new LimitLine(high, name);
                hightLimit.setLineWidth(0.1f);
                hightLimit.setTextSize(10f);
                hightLimit.enableDashedLine(8.0f, 4.0f, 4.0f);
                xAxis.addLimitLine(hightLimit);
                xAxis.removeAllLimitLines(); //先清除原来的线，后面再加上，防止add方法重复绘制
                hightLimit.setLineColor(Color.WHITE);
                lineChart.invalidate();
            }
        }

        public void ShowWave() {
            dynamicLineChartManager_Break.setData(Speed_Data, ASpeed_Data, Dis_Data,0.0172f);
            dynamicLineChartManager_Break.setYAxis(100, -100, 4);
            dynamicLineChartManager_Break.setXAxis(120, 0, 10,0);
            dynamicLineChartManager_Break.setHightLimitLine(0f, "");
            dynamicLineChartManager_Break.desChart(names.get (0),165);
        }
    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        super.onDestroy();
       /* if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }*/

        if(deviceBrake != null)
        {
            mHoldBluetoothBrake.disconnect(moduleBrake);
        }
        if(deviceFoot != null){
            mHoldBluetoothFoot.disconnect(moduleFoot);
        }
     }
  }
