package com.ruiguan.Sight.SAngle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

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
import com.ruiguan.Sight.SightActivity;
import com.ruiguan.activities.ActivityCollector;
import com.ruiguan.activities.MainActivity;
import com.ruiguan.activities.RadioDialog_W;
import com.ruiguan.activities.RadioDialog_ZH;
import com.ruiguan.activities.RadioDialog_ZN;
import com.ruiguan.activities.RadioDialog_before;
import com.ruiguan.activities.single.HoldBluetooth;
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

import static com.ruiguan.Sight.SightActivity.sight_Input;
import static com.ruiguan.activities.MenuActivity.input_data;
public class SAngleActivity extends SAngleSaveActivity {

    private BluetoothDevice deviceAngleL= null;
    private BluetoothDevice deviceAngleR= null;
    private DeviceModule moduleAngleL= null;
    private DeviceModule  moduleAngleR= null;
    private HoldBluetooth mHoldBluetoothAngleL= null;
    private HoldBluetooth mHoldBluetoothAngleR= null;
    private BluetoothAdapter bluetoothAdapter = null;
    private List<DeviceModule> modules;
    private final String CONNECTED = "已连接",CONNECTING = "连接中",DISCONNECT = "断线了";

    private RadioDialog_before alert_before; //缩放
    private RadioDialog_W alert_W ; //缩放
    private RadioDialog_ZH alert_ZH; //缩放
    private RadioDialog_ZN alert_ZN; //缩放
    private String str_company;
    private String str_number;
    private String str_sightNumber;
    private String str_sightLenght;
    private String str_sightType;
    private String str_sightLoad;
    private Spinner AngleType_txt;
    private String str_TestItem;
    private String str_AngleType;

    private RadioGroup radioBtnGroupA = null;
    private Button startABtn;
    private Button stopABtn;
    private Button scanABtn;
    private Button printABtn;
    private Button saveABtn;
    private Button exportABtn;
    private Button zeroABtn;
    private Button backABtn;
    private Button exitABtn;
    private Button angleMaxBtn;
    private Button AngleLBLE;
    private Button AngleRBLE;

    private Button beforeBtn;
    private Button angleWBtn;
    private Button angleZHBtn;
    private Button angleZNBtn;

    private Drawable startABtnpressed;
    private Drawable stopABtnpressed;
    private Drawable scanABtnpressed;
    private Drawable printABtnpressed;
    private Drawable zeroABtnpressed;
    private Drawable saveABtnpressed;
    private Drawable exportABtnpressed;
    private Drawable AngleLBLEpressed;
    private Drawable AngleRBLEpressed;

    private TextView statusAL_txt;
    private TextView statusAR_txt;
    private TextView realAngleL_txt;
    private TextView realAngleR_txt;
    private TextView angleMaxL_txt;
    private TextView angleMaxR_txt;
    private TextView angleMaxWL_txt;
    private TextView angleMaxWR_txt;
    private TextView angleMaxZHL_txt;
    private TextView angleMaxZHR_txt;
    private TextView angleMaxZNL_txt;
    private TextView angleMaxZNR_txt;

    private TextView  AngleXL_txt;
    private TextView  AngleYL_txt;
    private TextView  AngleZL_txt;
    private TextView  AngleXR_txt;
    private TextView  AngleYR_txt;
    private TextView  AngleZR_txt;

    private float ChartYMax;
    private float ChartXMax;

    private byte[] senddata;
    private byte[] angleReceiveL;
    private byte[] angleReceiveR;
    private byte[] angleReceive;

    private float FirstAngleLX=0.0f;
    private float FirstAngleLY=0.0f;
    private float FirstAngleLZ=0.0f;
    private float FirstAngleRX=0.0f;
    private float FirstAngleRY=0.0f;
    private float FirstAngleRZ=0.0f;

    private float AngleLX=0.0f;
    private float AngleLY=0.0f;
    private float AngleLZ=0.0f;
    private float AngleRX=0.0f;
    private float AngleRY=0.0f;
    private float AngleRZ=0.0f;

    private float angleMaxL=0.0f;
    private float angleMaxR=0.0f;

    private float angleZHFirstL=0.0f;
    private float angleZHSecondL=0.0f;
    private float angleZHFirstR=0.0f;
    private float angleZHSecondR=0.0f;

    private float angleZNFirstL=0.0f;
    private float angleZNSecondL=0.0f;
    private float angleZNFirstR=0.0f;
    private float angleZNSecondR=0.0f;

    private float angleMaxZHL=0.0f;
    private float angleMaxZHR=0.0f;

    private float angleMaxZNL=0.0f;;
    private float angleMaxZNR=0.0f;;

    private String str_realAngleL;
    private String str_realAngleR;
    private String str_angleMaxL;
    private String str_angleMaxR;
    private String str_angleMaxWL;
    private String str_angleMaxWR;
    private String str_angleMaxZHL;
    private String str_angleMaxZHR;
    private String str_angleMaxZNL;
    private String str_angleMaxZNR;
    ProgressDialog pd;
    private int firstNum=0;
    private boolean Finish=false;
    private boolean leftFlag=false;
    private boolean rightFlag=false;
    private boolean angleMaxFlag=false;
    private boolean angleWFlag=false;
    private boolean angleHFlag=false;
    private boolean angleNFlag=false;
    private Handler handler = new Handler();
    private LineChart chartAngle;

    private BaseAdapter mAdapter;
    private ListView anglelistView;
    private List<String> dataAngle = new ArrayList<String>();

    private ArrayList<Float> realAngle_DataL = new ArrayList<>();
    private ArrayList<Float> realAngle_DataR = new ArrayList<>();
    private SAngleActivity.DynamicLineChartManager dynamicLineChartManager_SAngle;
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合
    private PrintDataService printDataService = null;
    private boolean PrintConnect = false;
    java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.000");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sangle);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);
        senddata=new byte[9];
        angleReceiveL=new byte[17];
        angleReceiveR=new byte[17];
        angleReceive=new byte[17];
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
        mHoldBluetoothAngleL= new HoldBluetooth();
        mHoldBluetoothAngleL.initHoldBluetooth(SAngleActivity.this,updateList1);
        mHoldBluetoothAngleR= new HoldBluetooth();
        mHoldBluetoothAngleR.initHoldBluetooth(SAngleActivity.this,updateList);
        initDataSAngleLListener();
        initDataSAngleRListener();
        initMembers();
        ShowWave();
        handler.postDelayed(BleRunnable,2000);
    }
    //初始化蓝牙数据的监听
    private void initDataSAngleLListener() {
        HoldBluetooth.OnReadDataListener dataListener = new HoldBluetooth.OnReadDataListener() {
            @Override
            public void readData(String mac, byte[] data) {
                if (deviceAngleL.getAddress().equals(mac)){
                    angleReceiveL=data;
                } else if(deviceAngleR.getAddress().equals(mac))
                {
                    angleReceiveR=data;
                }
            }
            @Override
            public void reading(boolean isStart) {

            }
            @Override
            public void connectSucceed() {
                modules = mHoldBluetoothAngleL.getConnectedArray();
                for(int i=0;i<modules.size();i++)
                {
                    if(modules.get(i).getMac().equals(deviceAngleL.getAddress()))
                    {
                        setAngleLState(CONNECTED);//设置连接状态
                        Log.d("AngleActivity","AngleL蓝牙连接成功！");
                    }else if(modules.get(i).getMac().equals(deviceAngleR.getAddress()))
                    {
                        setAngleRState(CONNECTED);//设置连接状态
                        Log.d("AngleActivity","AngleR蓝牙连接成功！");
                    }
                }
            }
            @Override
            public void errorDisconnect(final DeviceModule deviceModule) {//蓝牙异常断开

                if(deviceModule.getMac().equals(deviceAngleL.getAddress()))
                {
                    setAngleLState(DISCONNECT);//设置断开状态
                }else if(deviceModule.getMac().equals(deviceAngleR.getAddress()))
                {
                    setAngleRState(DISCONNECT);//设置断开状态
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
        mHoldBluetoothAngleL.setOnReadListener(dataListener);
    }
    //初始化蓝牙数据的监听
    private void initDataSAngleRListener() {
        HoldBluetooth.OnReadDataListener dataListener = new HoldBluetooth.OnReadDataListener() {
            @Override
            public void readData(String mac, byte[] data) {
                if (deviceAngleL.getAddress().equals(mac)){
                    angleReceiveL=data;
                } else if(deviceAngleR.getAddress().equals(mac))
                {
                    angleReceiveR=data;
                }
            }
            @Override
            public void reading(boolean isStart) {

            }
            @Override
            public void connectSucceed() {
                modules = mHoldBluetoothAngleR.getConnectedArray();
                for(int i=0;i<modules.size();i++)
                {
                    if(modules.get(i).getMac().equals(deviceAngleL.getAddress()))
                    {
                        setAngleLState(CONNECTED);//设置连接状态
                        Log.d("AngleActivity","AngleL蓝牙连接成功！");
                    }else if(modules.get(i).getMac().equals(deviceAngleR.getAddress()))
                    {
                        setAngleRState(CONNECTED);//设置连接状态
                        Log.d("AngleActivity","AngleR蓝牙连接成功！");
                    }
                }
            }
            @Override
            public void errorDisconnect(final DeviceModule deviceModule) {//蓝牙异常断开
                if(deviceModule.getMac().equals(deviceAngleL.getAddress()))
                {
                    setAngleLState(DISCONNECT);//设置断开状态
                }else if(deviceModule.getMac().equals(deviceAngleR.getAddress()))
                {
                    setAngleRState(DISCONNECT);//设置断开状态
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
        mHoldBluetoothAngleR.setOnReadListener(dataListener);
    }
    private void setAngleLState(String state){
        switch (state){
            case CONNECTED://连接成功
                statusAL_txt.setText("已连接");
                AngleLBLEpressed = getResources().getDrawable(R.drawable.btle_connected);
                AngleLBLEpressed.setBounds(0, 10, AngleLBLEpressed.getMinimumWidth(), AngleLBLEpressed.getMinimumHeight());
                AngleLBLE.setCompoundDrawables(null, AngleLBLEpressed, null, null);
                setEnableButton();
                break;

            case CONNECTING://连接中
                statusAL_txt.setText("连接中");
                AngleLBLEpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                AngleLBLEpressed.setBounds(0, 10, AngleLBLEpressed.getMinimumWidth(), AngleLBLEpressed.getMinimumHeight());
                AngleLBLE.setCompoundDrawables(null, AngleLBLEpressed, null, null);
                break;

            case DISCONNECT://连接断开
                statusAL_txt.setText("断开");
                AngleLBLEpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                AngleLBLEpressed.setBounds(0, 10, AngleLBLEpressed.getMinimumWidth(), AngleLBLEpressed.getMinimumHeight());
                AngleLBLE.setCompoundDrawables(null, AngleLBLEpressed, null, null);
                break;
        }
    }

    private void setAngleRState(String state){
        switch (state){
            case CONNECTED://连接成功
                statusAR_txt.setText("已连接");
                AngleRBLEpressed = getResources().getDrawable(R.drawable.btle_connected);
                AngleRBLEpressed.setBounds(0, 10,  AngleRBLEpressed.getMinimumWidth(),  AngleRBLEpressed.getMinimumHeight());
                AngleRBLE.setCompoundDrawables(null,  AngleRBLEpressed, null, null);
                setEnableButton();
                break;

            case CONNECTING://连接中
                statusAR_txt.setText("连接中");
                AngleRBLEpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                AngleRBLEpressed.setBounds(0, 10,  AngleRBLEpressed.getMinimumWidth(),  AngleRBLEpressed.getMinimumHeight());
                AngleRBLE.setCompoundDrawables(null,  AngleRBLEpressed, null, null);
                break;

            case DISCONNECT://连接断开
                statusAR_txt.setText("断开");
                AngleRBLEpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                AngleRBLEpressed.setBounds(0, 10,  AngleRBLEpressed.getMinimumWidth(),  AngleRBLEpressed.getMinimumHeight());
                AngleRBLE.setCompoundDrawables(null,  AngleRBLEpressed, null, null);
                break;
        }
    }
    Runnable BleRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences shares2 = getSharedPreferences( "AngleL_Decive", Activity.MODE_PRIVATE );
            if(!shares2.getBoolean("BondDecive",false))
            {
                Intent intent = new Intent(SAngleActivity.this, MainActivity.class);
                startActivity(intent);
            }else
            {
                deviceAngleL= bluetoothAdapter.getRemoteDevice(shares2.getString("AngleL",""));
                if(deviceAngleL == null)
                {
                    Toast.makeText(SAngleActivity.this,"未绑定制停距离蓝牙！",Toast.LENGTH_LONG).show();
                }else{
                    DeviceModule deviceModuleBrake = new DeviceModule(deviceAngleL.getName(),deviceAngleL);
                    moduleAngleL= deviceModuleBrake;
                    mHoldBluetoothAngleL.connect(moduleAngleL);
                    //controlClientBrake = SocketThread.getClient(deviceBrake);
                    Log.d("mHoldBluetoothAngleL","开始连接蓝牙");
                }
            }
            handler.postDelayed(BleRunnableAngleR,2000);
        }
    };
    Runnable BleRunnableAngleR = new Runnable() {
        @Override
        public void run() {
            SharedPreferences shares = getSharedPreferences("AngleR_Decive", Activity.MODE_PRIVATE);
            if (!shares.getBoolean("BondDecive", false)) {
                Intent intent = new Intent(SAngleActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                deviceAngleR = bluetoothAdapter.getRemoteDevice(shares.getString("AngleR", ""));
                if(deviceAngleR == null)
                {
                    Toast.makeText(SAngleActivity.this,"未绑定踏板蓝牙！",Toast.LENGTH_LONG).show();
                }else{
                    DeviceModule deviceModuleFoot = new DeviceModule(deviceAngleR.getName(),deviceAngleR);
                    moduleAngleR= deviceModuleFoot;
                    mHoldBluetoothAngleR.connect(moduleAngleR);
                    //controlClientFoot = SocketThread.getClient(deviceFoot);
                    Log.d("mHoldBluetoothAngleR","开始连接蓝牙");
                }
            }
        }
    };

    private void initMembers() {
        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_sightNumber= sight_Input.getsightNumber();
        str_sightLenght= sight_Input.getsightLenght();
        str_sightType= sight_Input.getsightType();
        str_sightLoad= sight_Input.getsightLoad();
        AngleType_txt=findViewById(R.id.AngleTypeS);

        statusAL_txt = (TextView) findViewById(R.id.statusSAL_txt);
        statusAR_txt = (TextView) findViewById(R.id.statusSAR_txt);
        realAngleL_txt = (TextView) findViewById(R.id.realAngleLS_txt);
        realAngleR_txt = (TextView) findViewById(R.id.realAngleRS_txt);
        angleMaxL_txt = (TextView) findViewById(R.id.angleMaxLS_txt);
        angleMaxR_txt = (TextView) findViewById(R.id.angleMaxRS_txt);
        angleMaxWL_txt = (TextView) findViewById(R.id.angleMaxWLS_txt);
        angleMaxWR_txt = (TextView) findViewById(R.id.angleMaxWRS_txt);
        angleMaxZHL_txt = (TextView) findViewById(R.id.angleMaxZHLS_txt);
        angleMaxZHR_txt = (TextView) findViewById(R.id.angleMaxZHRS_txt);
        angleMaxZNL_txt = (TextView) findViewById(R.id.angleMaxZNLS_txt);
        angleMaxZNR_txt = (TextView) findViewById(R.id.angleMaxZNRS_txt);

        AngleXL_txt= (TextView) findViewById(R.id.AngleXLS_txt);
        AngleYL_txt= (TextView) findViewById(R.id.AngleYLS_txt);
        AngleZL_txt= (TextView) findViewById(R.id.AngleZLS_txt);
        AngleXR_txt= (TextView) findViewById(R.id.AngleXRS_txt);
        AngleYR_txt= (TextView) findViewById(R.id.AngleYRS_txt);
        AngleZR_txt= (TextView) findViewById(R.id.AngleZRS_txt);

        radioBtnGroupA = findViewById(R.id.radioSAButton);
        int buttonCount = radioBtnGroupA.getChildCount();
        for (int i = 0; i < buttonCount; i++)
        {
            if (radioBtnGroupA.getChildAt(i) instanceof RadioButton) {
                radioBtnGroupA.getChildAt(i).setOnClickListener(onClickListener);
            }
        }

        startABtn = findViewById(R.id.startASBtn);
        stopABtn = findViewById(R.id.stopASBtn);
        scanABtn = findViewById(R.id.scanASBtn);
        printABtn = findViewById(R.id.printASBtn);
        saveABtn = findViewById(R.id.saveASBtn);
        exportABtn = findViewById(R.id.exportASBtn);
        backABtn = findViewById(R.id.backASBtn);
        exitABtn = findViewById(R.id.exitASBtn);
        zeroABtn = findViewById(R.id.zeroASBtn);
        angleMaxBtn= findViewById(R.id.angleMaxSBtn);
        AngleLBLE= findViewById(R.id.AngleLSBLE);
        AngleRBLE= findViewById(R.id.AngleRSBLE);
        beforeBtn= findViewById(R.id.beforeSBtn);
        angleWBtn= findViewById(R.id.angleWSBtn);
        angleZHBtn= findViewById(R.id.angleZHSBtn);
        angleZNBtn= findViewById(R.id.angleZNSBtn);
        View.OnClickListener bl = new SAngleActivity.ButtonListener();
        setOnClickListener(startABtn, bl);
        setOnClickListener(stopABtn, bl);
        setOnClickListener(scanABtn, bl);
        setOnClickListener(printABtn, bl);
        setOnClickListener(saveABtn, bl);
        setOnClickListener(exportABtn, bl);
        setOnClickListener(zeroABtn, bl);
        setOnClickListener(backABtn, bl);
        setOnClickListener(exitABtn, bl);
        setOnClickListener(angleMaxBtn, bl);
        setOnClickListener(beforeBtn, bl);
        setOnClickListener(angleWBtn, bl);
        setOnClickListener(angleZHBtn, bl);
        setOnClickListener(angleZNBtn, bl);
        pd = new ProgressDialog(this);
        startABtn.setEnabled(false);
        startABtnpressed = getResources().getDrawable(R.drawable.start);
        startABtnpressed.setBounds(0, 0, startABtnpressed.getMinimumWidth(), startABtnpressed.getMinimumHeight());
        startABtn.setCompoundDrawables(null, startABtnpressed, null, null);

        stopABtn.setEnabled(false);
        stopABtnpressed = getResources().getDrawable(R.drawable.stop);
        stopABtnpressed.setBounds(0, 0, stopABtnpressed.getMinimumWidth(), stopABtnpressed.getMinimumHeight());
        stopABtn.setCompoundDrawables(null, stopABtnpressed, null, null);

        scanABtn.setEnabled(false);
        scanABtnpressed = getResources().getDrawable(R.drawable.scan);
        scanABtnpressed.setBounds(0, 0, scanABtnpressed.getMinimumWidth(), scanABtnpressed.getMinimumHeight());
        scanABtn.setCompoundDrawables(null, scanABtnpressed, null, null);

        printABtn.setEnabled(false);
        printABtnpressed = getResources().getDrawable(R.drawable.print);
        printABtnpressed.setBounds(0, 0, printABtnpressed.getMinimumWidth(), printABtnpressed.getMinimumHeight());
        printABtn.setCompoundDrawables(null, printABtnpressed, null, null);

        saveABtn.setEnabled(false);
        saveABtnpressed = getResources().getDrawable(R.drawable.save);
        saveABtnpressed.setBounds(0, 0, saveABtnpressed.getMinimumWidth(), saveABtnpressed.getMinimumHeight());
        saveABtn.setCompoundDrawables(null, saveABtnpressed, null, null);

        exportABtn.setEnabled(false);
        exportABtnpressed = getResources().getDrawable(R.drawable.export);
        exportABtnpressed.setBounds(0, 0, exportABtnpressed.getMinimumWidth(), exportABtnpressed.getMinimumHeight());
        exportABtn.setCompoundDrawables(null, exportABtnpressed, null, null);

        zeroABtn.setEnabled(false);
        zeroABtnpressed = getResources().getDrawable(R.drawable.zero);
        zeroABtnpressed.setBounds(0, 0, zeroABtnpressed.getMinimumWidth(), zeroABtnpressed.getMinimumHeight());
        zeroABtn.setCompoundDrawables(null, zeroABtnpressed, null, null);

        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return dataAngle.size();
            }
            @Override
            public Object getItem(int position) {
                return dataAngle.get(position);
            }
            @Override
            public long getItemId(int position) {
                return position;
            }
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                return getListView(position, convertView, parent);
            }
        };

        chartAngle= findViewById(R.id.chartSAngle);
        names.add ("");
        colour.add (Color.argb (255, 255, 125, 0));            //定义Fre颜色
        dynamicLineChartManager_SAngle = new SAngleActivity.DynamicLineChartManager(chartAngle, names.get (0), colour.get (0), 0);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.leftSBtn:
                    leftFlag=true;
                    rightFlag=false;
                    str_TestItem="左转向";
                    break;
                case R.id.rightSBtn:
                    leftFlag=false;
                    rightFlag=true;
                    str_TestItem="右转向";
                    break;
                default:
                    break;
            }
        }
    };
    @SuppressLint("SetTextI18n")
    private View getListView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView =getLayoutInflater().inflate(R.layout.force_list, null);//加载布局
        }
        TextView realValue_txt = (TextView) convertView.findViewById(R.id.realValue);
        TextView maxValue_txt = (TextView) convertView.findViewById(R.id.maxValue);

        realValue_txt.setText(Integer.toString(position+1));
        maxValue_txt .setText("左轮最大角度"+dataAngle.get(position));

        return convertView;
    }

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
    private void setEnableButton()
    {
        startABtn.setEnabled(true);
        startABtnpressed = getResources().getDrawable(R.drawable.start1);
        startABtnpressed.setBounds(0, 0, startABtnpressed.getMinimumWidth(), startABtnpressed.getMinimumHeight());
        startABtn.setCompoundDrawables(null, startABtnpressed, null, null);

        stopABtn.setEnabled(true);
        stopABtnpressed = getResources().getDrawable(R.drawable.stop1);
        stopABtnpressed.setBounds(0, 0, stopABtnpressed.getMinimumWidth(), stopABtnpressed.getMinimumHeight());
        stopABtn.setCompoundDrawables(null, stopABtnpressed, null, null);

        scanABtn.setEnabled(true);
        scanABtnpressed = getResources().getDrawable(R.drawable.scan1);
        scanABtnpressed.setBounds(0, 0, scanABtnpressed.getMinimumWidth(), scanABtnpressed.getMinimumHeight());
        scanABtn.setCompoundDrawables(null, scanABtnpressed, null, null);

        printABtn.setEnabled(true);
        printABtnpressed = getResources().getDrawable(R.drawable.print1);
        printABtnpressed.setBounds(0, 0, printABtnpressed.getMinimumWidth(), printABtnpressed.getMinimumHeight());
        printABtn.setCompoundDrawables(null, printABtnpressed, null, null);

        saveABtn.setEnabled(true);
        saveABtnpressed = getResources().getDrawable(R.drawable.save1);
        saveABtnpressed.setBounds(0, 0, saveABtnpressed.getMinimumWidth(), saveABtnpressed.getMinimumHeight());
        saveABtn.setCompoundDrawables(null, saveABtnpressed, null, null);

        exportABtn.setEnabled(true);
        exportABtnpressed = getResources().getDrawable(R.drawable.export1);
        exportABtnpressed.setBounds(0, 0, exportABtnpressed.getMinimumWidth(), exportABtnpressed.getMinimumHeight());
        exportABtn.setCompoundDrawables(null, exportABtnpressed, null, null);

        zeroABtn.setEnabled(true);
        zeroABtnpressed = getResources().getDrawable(R.drawable.zero1);
        zeroABtnpressed.setBounds(0, 0, zeroABtnpressed.getMinimumWidth(), zeroABtnpressed.getMinimumHeight());
        zeroABtn.setCompoundDrawables(null, zeroABtnpressed, null, null);
    }
    private void setButton()
    {
        startABtn.setEnabled(false);
        startABtnpressed = getResources().getDrawable(R.drawable.start);
        startABtnpressed.setBounds(0, 0, startABtnpressed.getMinimumWidth(), startABtnpressed.getMinimumHeight());
        startABtn.setCompoundDrawables(null, startABtnpressed, null, null);

        stopABtn.setEnabled(false);
        stopABtnpressed = getResources().getDrawable(R.drawable.stop);
        stopABtnpressed.setBounds(0, 0, stopABtnpressed.getMinimumWidth(), stopABtnpressed.getMinimumHeight());
        stopABtn.setCompoundDrawables(null, stopABtnpressed, null, null);

        printABtn.setEnabled(false);
        printABtnpressed = getResources().getDrawable(R.drawable.print);
        printABtnpressed.setBounds(0, 0, printABtnpressed.getMinimumWidth(), printABtnpressed.getMinimumHeight());
        printABtn.setCompoundDrawables(null, printABtnpressed, null, null);

        saveABtn.setEnabled(false);
        saveABtnpressed = getResources().getDrawable(R.drawable.save);
        saveABtnpressed.setBounds(0, 0, saveABtnpressed.getMinimumWidth(), saveABtnpressed.getMinimumHeight());
        saveABtn.setCompoundDrawables(null, saveABtnpressed, null, null);

        exportABtn.setEnabled(false);
        exportABtnpressed = getResources().getDrawable(R.drawable.export);
        exportABtnpressed.setBounds(0, 0, exportABtnpressed.getMinimumWidth(), exportABtnpressed.getMinimumHeight());
        exportABtn.setCompoundDrawables(null, exportABtnpressed, null, null);

        zeroABtn.setEnabled(false);
        zeroABtnpressed = getResources().getDrawable(R.drawable.zero);
        zeroABtnpressed.setBounds(0, 0, zeroABtnpressed.getMinimumWidth(), zeroABtnpressed.getMinimumHeight());
        zeroABtn.setCompoundDrawables(null, zeroABtnpressed, null, null);
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            final String METHODTAG = ".ButtonListener.onClick";
            switch (v.getId()) {
                case R.id.startASBtn: {
                    Finish = false;
                    startABtn.setEnabled(false);
                    startABtnpressed = getResources().getDrawable(R.drawable.start);
                    startABtnpressed.setBounds(0, 0, startABtnpressed.getMinimumWidth(), startABtnpressed.getMinimumHeight());
                    startABtn.setCompoundDrawables(null, startABtnpressed, null, null);
                    for(int i=0;i<3;i++) {
                        senddata[0] = 0x4B;
                        senddata[1] = 0x53;
                        senddata[2] = 0x09;
                        senddata[3] = 0x00;
                        senddata[4] = 0x01;
                        senddata[5] = 0x00;
                        senddata[6] = 0x01;
                        senddata[7] = 0x3A;
                        senddata[8] = 0x3B;
                        mHoldBluetoothAngleL.sendData(moduleAngleL,senddata);
                    }
                    for(int i=0;i<3;i++) {
                        senddata[0]=0x4B;
                        senddata[1]=0x53;
                        senddata[2]=0x10;
                        senddata[3]=0x00;
                        senddata[4]=0x01;
                        senddata[5]=0x00;
                        senddata[6]=0x01;
                        senddata[7]=0x3A;
                        senddata[8]=0x3B;
                        mHoldBluetoothAngleR.sendData(moduleAngleR,senddata);
                    }
                    FirstAngleLX=0.0f;
                    FirstAngleLY=0.0f;
                    FirstAngleLZ=0.0f;
                    FirstAngleRX=0.0f;
                    FirstAngleRY=0.0f;
                    FirstAngleRZ=0.0f;

                    AngleLX=0.0f;
                    AngleLY=0.0f;
                    AngleLZ=0.0f;
                    AngleRX=0.0f;
                    AngleRY=0.0f;
                    AngleRZ=0.0f;

                    ChartYMax = 0.0f;
                    ChartXMax = 0.0f;

                    realAngle_DataL.clear();
                    realAngle_DataR.clear();
                    dynamicLineChartManager_SAngle.clear();

                    str_AngleType=AngleType_txt.getSelectedItem().toString();
                    switch(str_AngleType)
                    {
                        case "最大转向角":
                            angleMaxFlag=true;
                            angleWFlag=false;
                            angleHFlag=false;
                            angleNFlag=false;
                            angleMaxL=0.0f;
                            angleMaxR=0.0f;
                            break;
                        case "前轮外倾角":
                            angleMaxFlag=false;
                            angleWFlag=true;
                            angleHFlag=false;
                            angleNFlag=false;
                            showFFTAlertDialog1();
                            break;
                        case "主销后倾角":
                            angleMaxFlag=false;
                            angleWFlag=false;
                            angleHFlag=true;
                            angleNFlag=false;
                            angleZHFirstL=0.0f;
                            angleZHSecondL=0.0f;
                            angleZHFirstR=0.0f;
                            angleZHSecondR=0.0f;
                            angleMaxZHL=0.0f;
                            angleMaxZHR=0.0f;
                            showFFTAlertDialog2();
                            break;
                        case "主销内倾角":
                            angleMaxFlag=false;
                            angleWFlag=false;
                            angleHFlag=false;
                            angleNFlag=true;
                            angleZNFirstL=0.0f;
                            angleZNSecondL=0.0f;
                            angleZNFirstR=0.0f;
                            angleZNSecondR=0.0f;
                            angleMaxZNL=0.0f;
                            angleMaxZNR=0.0f;
                            showFFTAlertDialog3();
                            break;
                        default:
                            break;
                    }
                    Log.d("AngleTypeActivity","     "+"测试项目："+str_AngleType);
                    handler.postDelayed(FirstRunnable,100);
                    //pd.setMessage("正在读取初始位置");
                    //pd.show();
                }
                break;
                case R.id.stopASBtn: {
                    Finish = true;
                    stopABtn.setEnabled(false);
                    stopABtnpressed = getResources().getDrawable(R.drawable.stop);
                    stopABtnpressed.setBounds(0, 0, stopABtnpressed.getMinimumWidth(), stopABtnpressed.getMinimumHeight());
                    stopABtn.setCompoundDrawables(null, stopABtnpressed, null, null);
                    for(int i=0;i<3;i++) {
                        senddata[0] = 0x4B;
                        senddata[1] = 0x53;
                        senddata[2] = 0x09;
                        senddata[3] = 0x00;
                        senddata[4] = 0x01;
                        senddata[5] = 0x00;
                        senddata[6] = 0x05;
                        senddata[7] = 0x3A;
                        senddata[8] = 0x3B;
                        mHoldBluetoothAngleL.sendData(moduleAngleL,senddata);
                    }
                    for(int i=0;i<3;i++) {
                        senddata[0]=0x4B;
                        senddata[1]=0x53;
                        senddata[2]=0x10;
                        senddata[3]=0x00;
                        senddata[4]=0x01;
                        senddata[5]=0x00;
                        senddata[6]=0x05;
                        senddata[7]=0x3A;
                        senddata[8]=0x3B;
                        mHoldBluetoothAngleR.sendData(moduleAngleR,senddata);
                    }
                    handler.removeCallbacks (ReceiveRunnable);
                }
                break;
                case R.id.scanASBtn: {
                    Intent intent = new Intent(SAngleActivity.this, SAngleSaveActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.printASBtn: {
                    printABtn.setEnabled(false);
                    printABtnpressed = getResources().getDrawable(R.drawable.print);
                    printABtnpressed.setBounds(0, 0, printABtnpressed.getMinimumWidth(), printABtnpressed.getMinimumHeight());
                    printABtn.setCompoundDrawables(null, printABtnpressed, null, null);
                    if (Finish) {
                        if (printDataService == null) {           //首次连接打印机
                            SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                            if (!shares.getBoolean("BondPrinter", false)) {
                                Toast.makeText(SAngleActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SAngleActivity.this.getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                        } else {          //打印数据
                            PrintMeasureData();
                        }
                    } else {
                        Toast.makeText(SAngleActivity.this, "没有可以打印的数据", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                case R.id.saveASBtn: {
                    saveABtn.setEnabled(false);
                    saveABtnpressed = getResources().getDrawable(R.drawable.save);
                    saveABtnpressed.setBounds(0, 0, saveABtnpressed.getMinimumWidth(), saveABtnpressed.getMinimumHeight());
                    saveABtn.setCompoundDrawables(null, saveABtnpressed, null, null);
                    angleAdd(str_TestItem,str_angleMaxL,str_angleMaxR,str_angleMaxWL,str_angleMaxWR,str_angleMaxZHL,str_angleMaxZHR,str_angleMaxZNL,str_angleMaxZNR);

                }
                break;
                case R.id.exportASBtn: {
                    exportABtn.setEnabled(false);
                    exportABtnpressed = getResources().getDrawable(R.drawable.export);
                    exportABtnpressed.setBounds(0, 0, exportABtnpressed.getMinimumWidth(), exportABtnpressed.getMinimumHeight());
                    exportABtn.setCompoundDrawables(null, exportABtnpressed, null, null);
                    CreatePdf();
                    Toast.makeText(SAngleActivity.this, "数据已导出到手机根目录", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.zeroASBtn: {
                    zeroABtn.setEnabled(false);
                    zeroABtnpressed = getResources().getDrawable(R.drawable.zero);
                    zeroABtnpressed.setBounds(0, 0, zeroABtnpressed.getMinimumWidth(), zeroABtnpressed.getMinimumHeight());
                    zeroABtn.setCompoundDrawables(null, zeroABtnpressed, null, null);

                    str_angleMaxL="";
                    str_angleMaxR="";
                    str_angleMaxWL="";
                    str_angleMaxWR="";
                    angleMaxL_txt.setText(str_angleMaxL);
                    angleMaxR_txt.setText(str_angleMaxR);
                    angleMaxWL_txt.setText(str_angleMaxL);
                    angleMaxWR_txt.setText(str_angleMaxR);
                    angleZHFirstL=0.0f;
                    angleZHSecondL=0.0f;
                    angleZHFirstR=0.0f;
                    angleZHSecondR=0.0f;
                    angleMaxZHL=0.0f;
                    angleMaxZHR=0.0f;
                    str_angleMaxZHL = myformat.format(angleMaxZHL);
                    str_angleMaxZHR = myformat.format(angleMaxZHR);
                    angleMaxZHL_txt.setText(str_angleMaxZHL);
                    angleMaxZHR_txt.setText(str_angleMaxZHR);

                    angleZNFirstL=0.0f;
                    angleZNSecondL=0.0f;
                    angleZNFirstR=0.0f;
                    angleZNSecondR=0.0f;
                    angleMaxZNL=0.0f;
                    angleMaxZNR=0.0f;
                    str_angleMaxZNL = myformat.format(angleMaxZNL);
                    str_angleMaxZNR = myformat.format(angleMaxZNR);
                    angleMaxZNL_txt.setText(str_angleMaxZNL);
                    angleMaxZNR_txt.setText(str_angleMaxZNR);
                }
                break;
                case R.id.angleMaxSBtn: {
                    str_angleMaxL="";
                    str_angleMaxR="";
                    angleMaxL_txt.setText(str_angleMaxL);
                    angleMaxR_txt.setText(str_angleMaxR);
                }
                break;
                case R.id.backASBtn: {
                    Intent intent1 = new Intent(SAngleActivity.this, SightActivity.class);
                    startActivity(intent1);
                    finish();
                }
                break;
                case R.id.exitASBtn: {
                    finish();
                    ActivityCollector.finishAll();
                }
                break;
                case R.id.beforeSBtn: {
                    showFFTAlertDialog();
                }
                break;
                case R.id.angleWSBtn: {
                    showFFTAlertDialog1();
                }
                break;
                case R.id.angleZHSBtn: {
                    showFFTAlertDialog2();
                }
                break;
                case R.id.angleZNSBtn: {
                    showFFTAlertDialog3();
                }
                break;
                default: {
                }
                break;
            }
            handler.postDelayed(sendRunnable, 1000);
        }
    }
    public void showFFTAlertDialog() {
        alert_before = new RadioDialog_before(this, "测试前准备：", "确定", "取消");
        alert_before.show();
        alert_before.setCancelOnClickListener(v -> alert_before.close());
        alert_before.setOkOnClickListener(v -> alert_before.close());
    }
    public void showFFTAlertDialog1() {
        alert_W = new RadioDialog_W(this, "前轮外倾角测量：", "确定", "取消");
        alert_W.show();
        alert_W.setCancelOnClickListener(v -> alert_W.close());
        alert_W.setOkOnClickListener(v -> {
            alert_W.close();
        });
    }
    public void showFFTAlertDialog2() {
        alert_ZH = new RadioDialog_ZH(this, "主销后倾角测量：", "确定", "取消");
        alert_ZH.show();
        alert_ZH.setCancelOnClickListener(v -> alert_ZH.close());
        alert_ZH.setOkOnClickListener(v -> {
            alert_ZH.close();
        });
    }
    public void showFFTAlertDialog3() {
        alert_ZN = new RadioDialog_ZN(this, "主销内倾角测量：", "确定", "取消");
        alert_ZN.show();
        alert_ZN.setCancelOnClickListener(v -> alert_ZN.close());
        alert_ZN.setOkOnClickListener(v -> {
            alert_ZN.close();
        });
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
    Runnable FirstRunnable = new Runnable() {
        @Override
        public void run() {
            int AngleLRevNum=0;
            int AngleRRevNum=0;
            float xtempreceiveL=0.0f;
            float ytempreceiveL=0.0f;
            float ztempreceiveL=0.0f;
            float xtempreceiveR=0.0f;
            float ytempreceiveR=0.0f;
            float ztempreceiveR=0.0f;
            String[] str_ReceiveData=new String[17];
            String[] str_ReceiveData1=new String[17];
            byte[] mReceiveAngleL=new byte[17];
            byte[] mReceiveAngleR=new byte[17];
            FirstAngleLX=0.0f;
            FirstAngleLY=0.0f;
            FirstAngleLZ=0.0f;
            FirstAngleRX=0.0f;
            FirstAngleRY=0.0f;
            FirstAngleRZ=0.0f;
      if((angleReceiveL.length>=17)&&(angleReceiveR.length>=17))
        {
            for(int i=0;i<angleReceiveL.length;i++)
            {
                if(angleReceiveL[AngleLRevNum]==0x4B)
                {
                    break;
                }else{
                    AngleLRevNum++;
                }
            }

            for(int i=0;i<angleReceiveR.length;i++)
            {
                if(angleReceiveR[AngleRRevNum]==0x4B)
                {
                    break;
                }else{
                    AngleRRevNum++;
                }
            }

            for(int i=0;i<17;i++)
            {
                if((AngleLRevNum+i)<angleReceiveL.length)
                {
                    mReceiveAngleL[i] =angleReceiveL[AngleLRevNum+i];
                }else{
                    mReceiveAngleL[i] =angleReceiveL[AngleLRevNum+i-angleReceiveL.length];
                }
            }

            for(int i=0;i<17;i++)
            {
                if((AngleRRevNum+i)<17)
                {
                    mReceiveAngleR[i] =angleReceiveR[AngleRRevNum+i];
                }else{
                    mReceiveAngleR[i] =angleReceiveR[AngleRRevNum+i-angleReceiveR.length];
                }
            }
            //angleReceiveL=controlClientAngleL.receive;
           //angleReceiveR=controlClientAngleR.receive;

           for(int i=0;i<17;i++)
            {
                str_ReceiveData[i]=myformat.format(mReceiveAngleL[i]);
            }

            Log.d("angleLActivity","     "+str_ReceiveData[0]+"  "+str_ReceiveData[1]+" "+str_ReceiveData[2]+"  "+str_ReceiveData[3]+" "+str_ReceiveData[4]+"  "+str_ReceiveData[5]+" "+
                    str_ReceiveData[6]+"  "+str_ReceiveData[7]+" "+str_ReceiveData[8]+"  "+str_ReceiveData[9]+" "+str_ReceiveData[10]+"  "+str_ReceiveData[11]+" "+
                    str_ReceiveData[12]+"  "+str_ReceiveData[13]+" "+str_ReceiveData[14]+str_ReceiveData[15]+str_ReceiveData[16]+"  ");

            for(int i=0;i<17;i++)
            {
                str_ReceiveData1[i]=myformat.format(mReceiveAngleR[i]);
            }

            Log.d("angleRActivity","     "+str_ReceiveData1[0]+"  "+str_ReceiveData1[1]+" "+str_ReceiveData1[2]+"  "+str_ReceiveData1[3]+" "+str_ReceiveData1[4]+"  "+str_ReceiveData1[5]+" "+
                    str_ReceiveData1[6]+"  "+str_ReceiveData1[7]+" "+str_ReceiveData1[8]+"  "+str_ReceiveData1[9]+" "+str_ReceiveData1[10]+"  "+str_ReceiveData1[11]+" "+
                    str_ReceiveData1[12]+"  "+str_ReceiveData1[13]+" "+str_ReceiveData1[14]+str_ReceiveData1[15]+str_ReceiveData1[16]+"  ");

            if((mReceiveAngleL[0]==0x4B) && (mReceiveAngleL[1]==0x53)) {
                switch (mReceiveAngleL[2]) {
                    case 0x09:
                        angleReceive[0]=mReceiveAngleL[0];
                        angleReceive[1]=mReceiveAngleL[1];
                        angleReceive[2]=mReceiveAngleL[2];
                        angleReceive[3]=mReceiveAngleL[4];
                        angleReceive[4]=mReceiveAngleL[5];
                        angleReceive[5]=mReceiveAngleL[6];
                        angleReceive[6]=mReceiveAngleL[7];
                        angleReceive[7]=mReceiveAngleL[8];
                        angleReceive[8]=mReceiveAngleL[9];
                        angleReceive[9]=mReceiveAngleR[4];
                        angleReceive[10]=mReceiveAngleR[5];
                        angleReceive[11]=mReceiveAngleR[6];
                        angleReceive[12]=mReceiveAngleR[7];
                        angleReceive[13]=mReceiveAngleR[8];
                        angleReceive[14]=mReceiveAngleR[9];
                        angleReceive[15]=mReceiveAngleR[15];
                        angleReceive[16]=mReceiveAngleR[16];

                        break;
                    default : break;
                }
            }
        }
            if((angleReceive[0]==0x4B) && (angleReceive[1]==0x53))
            {
                switch (angleReceive[2]) {
                    case 0x09:
                        int tmp = 0;
                        tmp = (char) (angleReceive[4] & 0xFF) * 256 + (char) (angleReceive[3] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) xtempreceiveL = (float)tmp/32768.0f*180.0f;
                        else xtempreceiveL = (float) (0x10000 - tmp) /32768.0f*180.0f * (-1.0f);

                        tmp = (char) (angleReceive[6] & 0xFF) * 256 + (char) (angleReceive[5] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) ytempreceiveL = (float)tmp/32768.0f*180.0f;
                        else ytempreceiveL = (float) (0x10000 - tmp) /32768.0f*180.0f * (-1.0f);

                        tmp = (char) (angleReceive[8] & 0xFF) * 256 + (char) (angleReceive[7] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) ztempreceiveL =(float)tmp/32768.0f*180.0f;
                        else ztempreceiveL  = (float) (0x10000 - tmp) /32768.0f*180.0f * (-1.0f);

                        tmp = (char) (angleReceive[10] & 0xFF) * 256 + (char) (angleReceive[9] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) xtempreceiveR = (float)tmp/32768.0f*180.0f;
                        else xtempreceiveR  = (float) (0x10000 - tmp) /32768.0f*180.0f * (-1.0f);

                        tmp = (char) (angleReceive[12] & 0xFF) * 256 + (char) (angleReceive[11] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) ytempreceiveR = (float)tmp/32768.0f*180.0f;
                        else ytempreceiveR   = (float) (0x10000 - tmp) /32768.0f*180.0f * (-1.0f);

                        tmp = (char) (angleReceive[14] & 0xFF) * 256 + (char) (angleReceive[13] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) ztempreceiveR = (float)tmp/32768.0f*180.0f;
                        else ztempreceiveR  = (float) (0x10000 - tmp) /32768.0f*180.0f * (-1.0f);

                        FirstAngleLX=xtempreceiveL;
                        FirstAngleLY=ytempreceiveL;
                        FirstAngleLZ=ztempreceiveL;
                        FirstAngleRX=xtempreceiveR;
                        FirstAngleRY=ytempreceiveR;
                        FirstAngleRZ=ztempreceiveR;
                        break;
                    default : break;
                }
            }
            Log.d("angleLActivity","     "+"FirstAngleLX="+myformat.format(FirstAngleLX)+"  "+"FirstAngleLY="+myformat.format(FirstAngleLY)+"FirstAngleLZ="+myformat.format(FirstAngleLZ));
            Log.d("angleRActivity","     "+"FirstAngleRX="+myformat.format(FirstAngleRX)+"  "+"FirstAngleRY="+myformat.format(FirstAngleRY)+"FirstAngleRZ="+myformat.format(FirstAngleRZ));
            firstNum++;
            if(firstNum<20)
            {
                handler.postDelayed(FirstRunnable,100);
            }else
            {
                firstNum=0;
                handler.removeCallbacks (FirstRunnable);
                handler.postDelayed(ReceiveRunnable,10);
                Toast.makeText(SAngleActivity.this, "初始定位设置完毕", Toast.LENGTH_SHORT).show();
            }
        }
    };
    Runnable ReceiveRunnable = new Runnable() {
        @Override
        public void run() {
            pd.dismiss();
            int AngleLRevNum=0;
            int AngleRRevNum=0;
            float xtempreceiveL=0.0f;
            float ytempreceiveL=0.0f;
            float ztempreceiveL=0.0f;
            float xtempreceiveR=0.0f;
            float ytempreceiveR=0.0f;
            float ztempreceiveR=0.0f;
            String[] str_ReceiveData=new String[17];
            String[] str_ReceiveData1=new String[17];
            byte[] mReceiveAngleL=new byte[17];
            byte[] mReceiveAngleR=new byte[17];

      if((angleReceiveL.length>=17)&&(angleReceiveR.length>=17))
      {
          for (int i = 0; i < angleReceiveL.length; i++)
          {
              if (angleReceiveL[AngleLRevNum] == 0x4B) {
                  break;
              } else {
                  AngleLRevNum++;
              }
          }

          for (int i = 0; i < angleReceiveR.length; i++)
          {
              if (angleReceiveR[AngleRRevNum] == 0x4B) {
                  break;
              } else {
                  AngleRRevNum++;
              }
          }

          for (int i = 0; i < 17; i++) {
              if ((AngleLRevNum + i) < angleReceiveL.length) {
                  mReceiveAngleL[i] = angleReceiveL[AngleLRevNum + i];
              } else {
                  mReceiveAngleL[i] = angleReceiveL[AngleLRevNum + i - angleReceiveL.length];
              }
          }

          for (int i = 0; i < 17; i++) {
              if ((AngleRRevNum + i) < angleReceiveR.length) {
                  mReceiveAngleR[i] = angleReceiveR[AngleRRevNum + i];
              } else {
                  mReceiveAngleR[i] = angleReceiveR[AngleRRevNum + i - angleReceiveR.length];
              }
          }
          //angleReceiveL=controlClientAngleL.receive;
          //angleReceiveR=controlClientAngleR.receive;

          for (int i = 0; i < 17; i++) {
              str_ReceiveData[i] = myformat.format(mReceiveAngleL[i]);
          }

          Log.d("angleLActivity", "     " + str_ReceiveData[0] + "  " + str_ReceiveData[1] + " " + str_ReceiveData[2] + "  " + str_ReceiveData[3] + " " + str_ReceiveData[4] + "  " + str_ReceiveData[5] + " " +
                  str_ReceiveData[6] + "  " + str_ReceiveData[7] + " " + str_ReceiveData[8] + "  " + str_ReceiveData[9] + " " + str_ReceiveData[10] + "  " + str_ReceiveData[11] + " " +
                  str_ReceiveData[12] + "  " + str_ReceiveData[13] + " " + str_ReceiveData[14] + str_ReceiveData[15] + str_ReceiveData[16] + "  ");

          for (int i = 0; i < 17; i++) {
              str_ReceiveData1[i] = myformat.format(mReceiveAngleR[i]);
          }

          Log.d("angleRActivity", "     " + str_ReceiveData1[0] + "  " + str_ReceiveData1[1] + " " + str_ReceiveData1[2] + "  " + str_ReceiveData1[3] + " " + str_ReceiveData1[4] + "  " + str_ReceiveData1[5] + " " +
                  str_ReceiveData1[6] + "  " + str_ReceiveData1[7] + " " + str_ReceiveData1[8] + "  " + str_ReceiveData1[9] + " " + str_ReceiveData1[10] + "  " + str_ReceiveData1[11] + " " +
                  str_ReceiveData1[12] + "  " + str_ReceiveData1[13] + " " + str_ReceiveData1[14] + str_ReceiveData1[15] + str_ReceiveData1[16] + "  ");

          if ((mReceiveAngleL[0] == 0x4B) && (mReceiveAngleL[1] == 0x53)) {
              switch (mReceiveAngleL[2]) {
                  case 0x09:
                      angleReceive[0] = mReceiveAngleL[0];
                      angleReceive[1] = mReceiveAngleL[1];
                      angleReceive[2] = mReceiveAngleL[2];
                      angleReceive[3] = mReceiveAngleL[4];
                      angleReceive[4] = mReceiveAngleL[5];
                      angleReceive[5] = mReceiveAngleL[6];
                      angleReceive[6] = mReceiveAngleL[7];
                      angleReceive[7] = mReceiveAngleL[8];
                      angleReceive[8] = mReceiveAngleL[9];
                      angleReceive[9] = mReceiveAngleR[4];
                      angleReceive[10] = mReceiveAngleR[5];
                      angleReceive[11] = mReceiveAngleR[6];
                      angleReceive[12] = mReceiveAngleR[7];
                      angleReceive[13] = mReceiveAngleR[8];
                      angleReceive[14] = mReceiveAngleR[9];
                      angleReceive[15] = mReceiveAngleR[15];
                      angleReceive[16] = mReceiveAngleR[16];
                      break;
                  default:
                      break;
              }
          }
      }
            if((angleReceive[0]==0x4B) && (angleReceive[1]==0x53))
            {
                switch (angleReceive[2]) {
                    case 0x09:
                        int tmp = 0;
                        tmp = (char) (angleReceive[4] & 0xFF) * 256 + (char) (angleReceive[3] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) xtempreceiveL = (float)tmp/32768.0f*180.0f;
                        else xtempreceiveL = (float) (0x10000 - tmp) /32768.0f*180.0f* (-1.0f);

                        tmp = (char) (angleReceive[6] & 0xFF) * 256 + (char) (angleReceive[5] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) ytempreceiveL = (float)tmp/32768.0f*180.0f;
                        else ytempreceiveL = (float) (0x10000 - tmp) /32768.0f*180.0f * (-1.0f);

                        tmp = (char) (angleReceive[8] & 0xFF) * 256 + (char) (angleReceive[7] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) ztempreceiveL =(float)tmp/32768.0f*180.0f;
                        else ztempreceiveL  = (float) (0x10000 - tmp) /32768.0f*180.0f* (-1.0f);

                        tmp = (char) (angleReceive[10] & 0xFF) * 256 + (char) (angleReceive[9] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) xtempreceiveR = (float)tmp/32768.0f*180.0f;
                        else xtempreceiveR  = (float) (0x10000 - tmp) /32768.0f*180.0f * (-1.0f);

                        tmp = (char) (angleReceive[12] & 0xFF) * 256 + (char) (angleReceive[11] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) ytempreceiveR = (float)tmp/32768.0f*180.0f;
                        else ytempreceiveR   = (float) (0x10000 - tmp) /32768.0f*180.0f * (-1.0f);

                        tmp = (char) (angleReceive[14] & 0xFF) * 256 + (char) (angleReceive[13] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) ztempreceiveR = (float)tmp/32768.0f*180.0f;
                        else ztempreceiveR  = (float) (0x10000 - tmp) /32768.0f*180.0f * (-1.0f);

                        AngleLX=xtempreceiveL-FirstAngleLX;
                        AngleLY=ytempreceiveL-FirstAngleLY;
                        AngleLZ=ztempreceiveL-FirstAngleLZ;
                        AngleRX=xtempreceiveR-FirstAngleRX;
                        AngleRY=ytempreceiveR-FirstAngleRY;
                        AngleRZ=ztempreceiveR-FirstAngleRZ;
                        // AngleLX=xtempreceiveL;
                        //AngleLY=ytempreceiveL;
                        //AngleLZ=ztempreceiveL;
                        //AngleRX=xtempreceiveR;
                        //AngleRY=ytempreceiveR;
                        //AngleRZ=ztempreceiveR;
                        Log.d("angleLActivity","     "+"AngleLX="+myformat.format(AngleLX)+"  "+"AngleLY="+myformat.format(AngleLY)+"AngleLZ="+myformat.format(AngleLZ));
                        Log.d("angleRActivity","     "+"AngleRX="+myformat.format(AngleRX)+"  "+"AngleRY="+myformat.format(AngleRY)+"AngleRZ="+myformat.format(AngleRZ));

                        AngleXL_txt.setText("左轮水平角度："+myformat.format(AngleLZ));
                        AngleYL_txt.setText("左轮垂直角度："+myformat.format(AngleLX));
                        AngleZL_txt.setText("左轮左右角度："+ myformat.format(AngleLY));
                        AngleXR_txt.setText("右轮水平角度："+myformat.format(AngleRZ));
                        AngleYR_txt.setText("右轮垂直角度："+myformat.format(AngleRX));
                        AngleZR_txt.setText("右轮左右角度："+myformat.format(AngleRY));

                        if(Math.abs(angleMaxL)<Math.abs(AngleLZ))
                        {
                            angleMaxL=AngleLZ;
                        }
                        if(Math.abs(angleMaxR)<Math.abs(AngleRZ))
                        {
                            angleMaxR=AngleRZ;
                        }
                        str_realAngleL = myformat.format(AngleLZ);
                        realAngleL_txt.setText(str_realAngleL);
                        str_realAngleR = myformat.format(AngleRZ);
                        realAngleR_txt.setText(str_realAngleR);
                        realAngle_DataL.add(AngleLZ);
                        realAngle_DataR.add(AngleRZ);
                        if(angleMaxFlag)
                        {
                            str_angleMaxL = myformat.format(angleMaxL);
                            str_angleMaxR = myformat.format(angleMaxR);
                            angleMaxL_txt.setText(str_angleMaxL);
                            angleMaxR_txt.setText(str_angleMaxR);
                        }else if(angleWFlag)
                        {
                            str_angleMaxWL = myformat.format(AngleLY);
                            str_angleMaxWR = myformat.format(AngleRY);
                            angleMaxWL_txt.setText(str_angleMaxWL);
                            angleMaxWR_txt.setText(str_angleMaxWR);
                        }else if(angleHFlag)
                        {
                            if((AngleLZ>20.0f) && (AngleLZ<25.0f))
                            {
                                angleZHFirstL=AngleLY;
                                angleZHSecondR=AngleRY;
                            }else if((AngleLZ>-25.0f) && (AngleLZ<-20.0f))
                            {
                                angleZHSecondL=AngleLY;
                                angleZHFirstR=AngleRY;

                                angleMaxZHL = (float)(Math.abs(angleZHSecondL-angleZHFirstL)/(2*Math.sin(Math.PI*20.0f/180)));
                                angleMaxZHR = (float)(Math.abs(angleZHSecondR-angleZHFirstR)/(2*Math.sin(Math.PI*20.0f/180)));
                                str_angleMaxZHL = myformat.format(angleMaxZHL);
                                str_angleMaxZHR = myformat.format(angleMaxZHR);
                                angleMaxZHL_txt.setText(str_angleMaxZHL);
                                angleMaxZHR_txt.setText(str_angleMaxZHR);
                            }

                        }else if(angleNFlag)
                        {
                            if((AngleLZ>20.0f) && (AngleLZ<25.0f))
                            {
                                angleZNFirstL=AngleLX;
                                angleZNSecondR=AngleRX;
                            }else if((AngleLZ>-25.0f) && (AngleLZ<-20.0f))
                            {
                                angleZNSecondL=AngleLX;
                                angleZNFirstR=AngleRX;

                                angleMaxZNL = (float)(Math.abs(angleZNSecondL-angleZNFirstL)/(2*Math.sin(Math.PI*20.0f/180)));
                                angleMaxZNR = (float)(Math.abs(angleZNSecondR-angleZNFirstR)/(2*Math.sin(Math.PI*20.0f/180)));
                                str_angleMaxZNL = myformat.format(angleMaxZNL);
                                str_angleMaxZNR = myformat.format(angleMaxZNR);
                                angleMaxZNL_txt.setText(str_angleMaxZNL);
                                angleMaxZNR_txt.setText(str_angleMaxZNR);
                            }
                        }
                        if(ChartYMax<Math.abs(AngleLX))
                        {
                            ChartYMax =Math.abs(AngleLX);
                        }
                        if(ChartYMax<Math.abs(AngleRX))
                        {
                            ChartYMax =Math.abs(AngleRX);
                        }
                        if(ChartYMax<Math.abs(AngleLY))
                        {
                            ChartYMax =Math.abs(AngleLY);
                        }
                        if(ChartYMax<Math.abs(AngleRY))
                        {
                            ChartYMax =Math.abs(AngleRY);
                        }
                        if(ChartYMax<Math.abs(AngleLZ))
                        {
                            ChartYMax =Math.abs(AngleLZ);
                        }
                        if(ChartYMax<Math.abs(AngleRZ))
                        {
                            ChartYMax =Math.abs(AngleRZ);
                        }
                        ChartXMax =ChartXMax+0.1f;
                        if(ChartXMax>10000)
                        {
                            ChartXMax=0;
                        }
                        dynamicLineChartManager_SAngle.setYAxis(ChartYMax*1.2f, ChartYMax*1.2f*(-1.0f), 10);
                        dynamicLineChartManager_SAngle.setXAxis(ChartXMax*1.2f, 0, 10,0);
                        dynamicLineChartManager_SAngle.addEntry(AngleLZ,AngleRZ);
                        break;
                    default : break;
                }
            }
            handler.postDelayed(ReceiveRunnable,100);
        }
    };

    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            startABtn.setEnabled(true);
            startABtnpressed = getResources().getDrawable(R.drawable.start1);
            startABtnpressed.setBounds(0, 0, startABtnpressed.getMinimumWidth(), startABtnpressed.getMinimumHeight());
            startABtn.setCompoundDrawables(null, startABtnpressed, null, null);

            stopABtn.setEnabled(true);
            stopABtnpressed = getResources().getDrawable(R.drawable.stop1);
            stopABtnpressed.setBounds(0, 0, stopABtnpressed.getMinimumWidth(), stopABtnpressed.getMinimumHeight());
            stopABtn.setCompoundDrawables(null, stopABtnpressed, null, null);

            scanABtn.setEnabled(true);
            scanABtnpressed = getResources().getDrawable(R.drawable.scan1);
            scanABtnpressed.setBounds(0, 0, scanABtnpressed.getMinimumWidth(), scanABtnpressed.getMinimumHeight());
            scanABtn.setCompoundDrawables(null, scanABtnpressed, null, null);

            printABtn.setEnabled(true);
            printABtnpressed = getResources().getDrawable(R.drawable.print1);
            printABtnpressed.setBounds(0, 0, printABtnpressed.getMinimumWidth(), printABtnpressed.getMinimumHeight());
            printABtn.setCompoundDrawables(null, printABtnpressed, null, null);

            saveABtn.setEnabled(true);
            saveABtnpressed = getResources().getDrawable(R.drawable.save1);
            saveABtnpressed.setBounds(0, 0, saveABtnpressed.getMinimumWidth(), saveABtnpressed.getMinimumHeight());
            saveABtn.setCompoundDrawables(null, saveABtnpressed, null, null);

            exportABtn.setEnabled(true);
            exportABtnpressed = getResources().getDrawable(R.drawable.export1);
            exportABtnpressed.setBounds(0, 0, exportABtnpressed.getMinimumWidth(), exportABtnpressed.getMinimumHeight());
            exportABtn.setCompoundDrawables(null, exportABtnpressed, null, null);
        }
    };

    Runnable PrinterRunnable = new Runnable() {
        @Override
        public void run() {
            if(PrintConnect){         //连接成功并已经打印数据，则关闭蓝牙
                handler.removeCallbacks(PrinterRunnable);
                PrintConnect = false;
            }else{
                SharedPreferences shares = getSharedPreferences( "BLE_Info", Activity.MODE_PRIVATE );
                if(shares.getBoolean("BondPrinter",false))
                {
                    printDataService = new PrintDataService(SAngleActivity.this,shares.getString("Printer",""));
                    //Toast.makeText(overActivity.this,"蓝牙打印机连接中...",Toast.LENGTH_LONG).show();
                }
                if(printDataService != null){
                    PrintConnect = printDataService.connect();
                    if(PrintConnect){
                        Toast.makeText(SAngleActivity.this,"蓝牙打印机连接成功...",Toast.LENGTH_LONG).show();
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
        printDataService.send("观光车辆/观光列车车轮转角检测结果");
        printDataService.send("\n*******************************\n");
        SimpleDateFormat formatter   =   new SimpleDateFormat("检测时间"+":yyyy-MM-dd  HH:mm:ss\n", Locale.CHINA);
        Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
        String   str   =   formatter.format(curDate);
        printDataService.send(str);
        printDataService.send("受检单位"+": "+str_company+"\n");//
        printDataService.send("设备编号"+": "+ str_number+"\n");//
        printDataService.send("车牌编号"+": "+ str_sightNumber+"\n");//
        printDataService.send("车身长"+": "+ str_sightLenght+"m"+"\n");//
        printDataService.send("车辆类型"+": "+ str_sightType+"\n");//
        printDataService.send("负载类型"+": "+ str_sightLoad+"\n");//
        printDataService.send("测试项目"+": "+ str_TestItem+"\n");//
        printDataService.send("左轮最大转向角值"+": "+ str_angleMaxL+"°"+"\n");//
        printDataService.send("右轮最大转向角值"+": "+ str_angleMaxR+"°"+"\n");//
        printDataService.send("左轮最大外倾角值"+": "+ str_angleMaxWL+"°"+"\n");//
        printDataService.send("右轮最大外倾角值"+": "+ str_angleMaxWR+"°"+"\n");//
        printDataService.send("左轮主销后倾角值"+": "+ str_angleMaxZHL+"°"+"\n");//
        printDataService.send("右轮主销后倾角值"+": "+ str_angleMaxZHR+"°"+"\n");//
        printDataService.send("左轮主销内倾角值"+": "+ str_angleMaxZNL+"°"+"\n");//
        printDataService.send("右轮主销内倾角值"+": "+ str_angleMaxZNR+"°"+"\n");//

        printDataService.send("*******************************\n\n\n\n");
        Toast.makeText(SAngleActivity.this,"打印完成！",Toast.LENGTH_SHORT).show();
    }

    //权限读写
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public void notifySystemToScan(String filePath) {         //将文件修改信息通知到系统中
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);

        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        SAngleActivity.this.sendBroadcast(intent);

    }
    //创建PDF文件-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(SAngleActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //创建目录
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车车轮转角检测报告"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车车轮转角检测报告"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车车轮转角检测报告" + File.separator + curDate.toString ()+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    pdfcontext = new Paragraph("观光车辆/观光列车车轮转角检测报告",setChineseTitleFont());
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
                    cell.setPhrase(new Phrase(str_sightNumber,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("车身长：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_sightLenght,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(" m",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("车辆类型：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_sightType,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("负载类型：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_sightLoad,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("测试项目：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_TestItem,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(" ",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("左轮最大转向角值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleMaxL,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("右轮最大转向角值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleMaxR,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("左轮最大外倾角值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleMaxWL,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("右轮最大外倾角值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleMaxWR,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("左轮主销后倾角值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleMaxZHL,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("右轮主销后倾角值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleMaxZHR,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("左轮主销内倾角值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleMaxZNL,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("右轮主销内倾角值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleMaxZNR,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    doc.add(mtable);
                    doc.close();
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车车轮转角检测报告" +  File.separator + curDate.toString () +".pdf");
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
        private LineDataSet lineDataSet,lineDataSet1;
        private LineChartMarkView mv;
        private Legend legend;
        private int position;
        private void setData(ArrayList<Float> value, ArrayList<Float> value1,float index) {
            ArrayList<Entry> values = new ArrayList<>();
            for (int i = 0; i < value.size()- 1; i++) {
                values.add(new Entry((float)(i*index), (float) value.get(i)));
            }

            ArrayList<Entry> values1 = new ArrayList<>();
            for (int i = 0; i < value1.size() - 1; i++) {
                values1.add(new Entry((float)(i*index), (float) value1.get(i)));
            }
            lineDataSet.setValues(values);
            lineDataSet1.setValues(values1);
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
            chartAngle.setVisibility(View.VISIBLE);
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
            chartAngle.setVisibility (View.VISIBLE);
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
            lineDataSet = new LineDataSet(null, "左轮角度(°)");
            lineDataSet.setLineWidth(1.0f);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setColor(Color.RED);
            lineDataSet.setHighLightColor(Color.WHITE);
            //设置曲线填充
            lineDataSet.setDrawFilled(false);
            lineDataSet.setDrawCircleHole(false);
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setDrawValues(false);
            lineDataSet.setMode(LineDataSet.Mode.LINEAR);

            //数据样式
            lineDataSet1 = new LineDataSet(null, "右轮角度(°)");
            lineDataSet1.setLineWidth(1.0f);
            lineDataSet1.setDrawCircles(false);
            lineDataSet1.setColor(Color.BLUE);
            lineDataSet1.setHighLightColor(Color.WHITE);
            //设置曲线填充
            lineDataSet1.setDrawFilled(false);
            lineDataSet1.setDrawCircleHole(false);
            lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet1.setDrawValues(false);
            lineDataSet1.setMode(LineDataSet.Mode.LINEAR);

            lineData = new LineData(lineDataSet,lineDataSet1);
            lineChart.setData(lineData);
            lineChart.animateX(10);
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

        private void addEntry(float speed,float Aspeed){
            //lineData = chartSpeed.getData();
            Entry entry = new Entry(lineDataSet.getEntryCount()*0.1f,speed);
            Entry entry1 = new Entry(lineDataSet1.getEntryCount()*0.1f,Aspeed);
            lineData.addEntry(entry, 0);
            lineData.addEntry(entry1, 1);
            chartAngle.notifyDataSetChanged();
            chartAngle.moveViewToX(0.00f);
        }
        private void freshChart(float speed,float Aspeed) {
            lineChart.fitScreen();
        }
        private void clear() {
            lineDataSet.clear();
            lineDataSet1.clear();
            lineChart.invalidate();
        }

        public void setYAxis(float max, float min, int labelCount) {
            if (max < min) {
                return;
            }
            leftAxis = lineChart.getAxisLeft();
            rightAxis = lineChart.getAxisRight();
            rightAxis.setEnabled(false);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setTextColor(Color.WHITE);
            leftAxis.setGranularityEnabled(false);
            leftAxis.setDrawGridLines(false);
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
        dynamicLineChartManager_SAngle.setData(realAngle_DataL,realAngle_DataR,0.1f);
        dynamicLineChartManager_SAngle.setYAxis(360, 360*(-1.0f), 10);
        dynamicLineChartManager_SAngle.setXAxis(120, 0, 10,0);
        dynamicLineChartManager_SAngle.setHightLimitLine(0f, "");
        dynamicLineChartManager_SAngle.desChart(names.get (0),165);
    }
    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        super.onDestroy();
        if(deviceAngleL != null)
        {
            mHoldBluetoothAngleL.disconnect(moduleAngleL);
        }
        if(deviceAngleR != null)
        {
            mHoldBluetoothAngleR.disconnect(moduleAngleR);
        }
    }
}

