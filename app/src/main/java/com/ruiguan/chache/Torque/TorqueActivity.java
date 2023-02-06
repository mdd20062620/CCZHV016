package com.ruiguan.chache.Torque;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import com.ruiguan.activities.ActivityCollector;
import com.ruiguan.activities.MainActivity;
import com.ruiguan.activities.single.HoldBluetooth;
import com.ruiguan.chache.ChacheActivity;
import com.ruiguan.entity.torqueEntity;
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

public class TorqueActivity extends TorqueSaveActivity {
    private BluetoothDevice deviceTorque= null;
    private DeviceModule moduleTorque= null;
    private HoldBluetooth mHoldBluetoothTorque= null;
    private BluetoothAdapter bluetoothAdapter = null;
    private List<DeviceModule> modules;
    private final String CONNECTED = "已连接",CONNECTING = "连接中",DISCONNECT = "断线了";

    private String str_company;
    private String str_number;
    private String str_chacheNumber;
    private String str_chacheType;
    private String str_chacheGroup;

    private Button startTBtn;
    private Button stopTBtn;
    private Button scanTBtn;
    private Button printTBtn;
    private Button saveTBtn;
    private Button exportTBtn;
    private Button backTBtn;
    private Button addTBtn;
    private Button footBLEFT;
    private Button zeroTBtn;
    private Button torqueHelpBtn;

    private Button angleMaxTBtn;
    private Button forceMaxTBtn;
    private Button angleErrMaxTBtn;
    private Button forceErrMaxTBtn;

    private Drawable footBLEFTpressed;
    private Drawable startTBtnpressed;
    private Drawable stopTBtnpressed;
    private Drawable scanTBtnpressed;
    private Drawable printTBtnpressed;
    private Drawable saveTBtnpressed;
    private Drawable exportTBtnpressed;
    private Drawable addTBtnpressed;
    private Drawable zeroTBtnpressed;

    private TextView realAngleLT_txt;
    private TextView realAngleRT_txt;
    private TextView angleMaxLT_txt;
    private TextView angleMaxRT_txt;
    private TextView realForceLT_txt;
    private TextView realForceRT_txt;
    private TextView forceMaxLT_txt;
    private TextView forceMaxRT_txt;
    private TextView angleErrT_txt;
    private TextView forceErrT_txt;
    private TextView statusFT_txt;

    private byte[] senddata;
    private byte[] torqueReceive;
    private float realForce;
    private float realAngle;
    private float realForceL;
    private float forceMaxL;
    private float realAngleL;
    private float angleMaxL;
    private float realForceR;
    private float forceMaxR;
    private float realAngleR;
    private float angleMaxR;
    private float forceErrMax;
    private float angleErrMax;
    private float forceFirst;
    private float angleFirst;
    private int firstNum=0;

    private String str_realForceL;
    private String str_forceMaxL;
    private String str_realAngleL;
    private String str_angleMaxL;
    private String str_realForceR;
    private String str_forceMaxR;
    private String str_realAngleR;
    private String str_angleMaxR;
    private String str_forceErrMax;
    private String str_angleErrMax;
    private RadioGroup radioBtnGroupT = null;
    private boolean Finish=false;
    private boolean angleFlag=true;
    private boolean forceFlag=false;
    private String str_TestItem="方向盘最大自由转动量试验";

    private String str_forceErrSet=" ";
    private float forceErrSet=0.0f;
    private String str_forceErrResult="合格";

    private float ChartYMax;
    private float ChartXMax;

    private BaseAdapter mAdapter;
    private ListView forcelistView;
    private List<torqueEntity> mDatas  = new ArrayList<torqueEntity>();

    private Handler handler = new Handler();
    private LineChart chartTorque;
    private ArrayList<Float> realForce_Data = new ArrayList<>();
    private ArrayList<Float> realAngle_Data = new ArrayList<>();
    private TorqueActivity.DynamicLineChartManager dynamicLineChartManager_Torque;
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合
   // private ProgressDialog pd;
    private PrintDataService printDataService = null;
    private boolean PrintConnect = false;
    java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.000");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torque);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);
        senddata=new byte[9];
        torqueReceive=new byte[17];
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

        final HoldBluetooth.UpdateList updateList = new HoldBluetooth.UpdateList() {
            @Override
            public void update(boolean isStart,DeviceModule deviceModule) {

            }

            @Override
            public void updateMessyCode(boolean isStart, DeviceModule deviceModule) {
            }
        };

        mHoldBluetoothTorque= new HoldBluetooth();
        mHoldBluetoothTorque.initHoldBluetooth(TorqueActivity.this,updateList);
        initMembers();
        initDataTorqueListener();
        ShowWave();
        handler.postDelayed(BleRunnable,2000);
    }
    private void initMembers() {
        str_company = input_data.getCom();
        str_number = input_data.getNumber();
        str_chacheNumber= chache_Input.getchacheNumber();
        str_chacheType= chache_Input.getchacheType();
        str_chacheGroup= chache_Input.getchacheGroup();
        angleFlag=true;
        forceFlag=false;
        str_TestItem="方向盘最大自由转动量试验";

        realAngleLT_txt= (TextView) findViewById(R.id.realAngleLT_txt);
        realAngleRT_txt= (TextView) findViewById(R.id.realAngleRT_txt);
        angleMaxLT_txt= (TextView) findViewById(R.id.angleMaxLT_txt);
        angleMaxRT_txt= (TextView) findViewById(R.id.angleMaxRT_txt);
        realForceLT_txt= (TextView) findViewById(R.id.realForceLT_txt);
        realForceRT_txt= (TextView) findViewById(R.id.realForceRT_txt);
        forceMaxLT_txt= (TextView) findViewById(R.id.forceMaxLT_txt);
        forceMaxRT_txt= (TextView) findViewById(R.id.forceMaxRT_txt);
        angleErrT_txt= (TextView) findViewById(R.id.angleErrT_txt);
        forceErrT_txt= (TextView) findViewById(R.id.forceErrT_txt);
        statusFT_txt= (TextView) findViewById(R.id.statusFT_txt);

        radioBtnGroupT = findViewById(R.id.radioTButton);
        int buttonCount = radioBtnGroupT.getChildCount();
        for (int i = 0; i < buttonCount; i++)
        {
            if (radioBtnGroupT.getChildAt(i) instanceof RadioButton) {
                radioBtnGroupT.getChildAt(i).setOnClickListener(onClickListener);
            }
        }
        angleMaxTBtn= (Button) findViewById(R.id.angleMaxTBtn);
        forceMaxTBtn= (Button) findViewById(R.id.forceMaxTBtn);
        angleErrMaxTBtn= (Button) findViewById(R.id.angleErrMaxTBtn);
        forceErrMaxTBtn=(Button)  findViewById(R.id.forceErrMaxTBtn);

        startTBtn =(Button) findViewById(R.id.startTBtn);
        stopTBtn = (Button) findViewById(R.id.stopTBtn);
        scanTBtn = (Button) findViewById(R.id.scanTBtn);
        printTBtn = (Button) findViewById(R.id.printTBtn);
        saveTBtn = (Button)  findViewById(R.id.saveTBtn);
        exportTBtn = (Button) findViewById(R.id.exportTBtn);
        backTBtn = (Button) findViewById(R.id.backTBtn);
        addTBtn = (Button) findViewById(R.id.addTBtn);
        zeroTBtn= (Button) findViewById(R.id.zeroTBtn);
        footBLEFT= (Button) findViewById(R.id.footBLEFT);
        torqueHelpBtn= (Button) findViewById(R.id.torqueHelpBtn);
        View.OnClickListener bl = new TorqueActivity.ButtonListener();
        setOnClickListener(startTBtn, bl);
        setOnClickListener(stopTBtn, bl);
        setOnClickListener(scanTBtn, bl);
        setOnClickListener(printTBtn, bl);
        setOnClickListener(saveTBtn, bl);
        setOnClickListener(exportTBtn, bl);
        setOnClickListener(backTBtn, bl);
        setOnClickListener(addTBtn, bl);
        setOnClickListener(zeroTBtn, bl);
        setOnClickListener(angleMaxTBtn, bl);
        setOnClickListener(forceMaxTBtn, bl);
        setOnClickListener(angleErrMaxTBtn, bl);
        setOnClickListener(forceErrMaxTBtn, bl);
        setOnClickListener(torqueHelpBtn, bl);

        setButton();
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mDatas.size();
            }
            @Override
            public Object getItem(int position) {
                return mDatas.get(position);
            }
            @Override
            public long getItemId(int position) {
                return position;
            }
            @Override
            public View getView(final int position, View convertView, ViewGroup parent)
            {
                return getListView(position, convertView, parent);
            }
        };

        forcelistView = (ListView)findViewById(R.id.torqueList);
        forcelistView.setAdapter(mAdapter);
        forcelistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                str_angleMaxL=((torqueEntity)mAdapter.getItem(position)).getAngleMaxL();
                str_angleMaxR=((torqueEntity)mAdapter.getItem(position)).getAngleMaxR();
                str_forceMaxL=((torqueEntity)mAdapter.getItem(position)).getForceMaxL();
                str_forceMaxR=((torqueEntity)mAdapter.getItem(position)).getForceMaxR();
                str_angleErrMax=((torqueEntity)mAdapter.getItem(position)).getAngleErrMax();
                str_forceErrMax=((torqueEntity)mAdapter.getItem(position)).getForceErrMax();
                Toast.makeText(TorqueActivity.this, "已选中要保存的数据", Toast.LENGTH_SHORT).show();
            }
        });

       // pd = new ProgressDialog(this);
        chartTorque= findViewById(R.id.chartTorque);
        angleFlag=true;
        forceFlag=false;
        str_TestItem="方向盘最大自由转动量试验";
        names.add ("");
        colour.add (Color.argb (255, 255, 125, 0));            //定义Fre颜色
        dynamicLineChartManager_Torque = new TorqueActivity.DynamicLineChartManager(chartTorque, names.get (0), colour.get (0), 0);
    }
    //初始化蓝牙数据的监听
    private void initDataTorqueListener() {
        HoldBluetooth.OnReadDataListener dataListener = new HoldBluetooth.OnReadDataListener() {
            @Override
            public void readData(String mac, byte[] data) {
                if (deviceTorque.getAddress().equals(mac)){
                    torqueReceive=data;
                }
            }
            @Override
            public void reading(boolean isStart) {

            }
            @Override
            public void connectSucceed() {
                modules = mHoldBluetoothTorque.getConnectedArray();
                for(int i=0;i<modules.size();i++)
                {
                    if(modules.get(i).getMac().equals(deviceTorque.getAddress()))
                    {
                        setTorqueState(CONNECTED);//设置连接状态
                        Log.d("TorqueActivity","Torque蓝牙连接成功！");
                    }
                }
            }
            @Override
            public void errorDisconnect(final DeviceModule deviceModule) {//蓝牙异常断开

                if(deviceModule.getMac().equals(deviceTorque.getAddress()))
                {
                    setTorqueState(DISCONNECT);//设置断开状态
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
        mHoldBluetoothTorque.setOnReadListener(dataListener);
    }

    private void setTorqueState(String state){
        switch (state){
            case CONNECTED://连接成功
                statusFT_txt.setText("已连接");
                footBLEFTpressed = getResources().getDrawable(R.drawable.btle_connected);
                footBLEFTpressed.setBounds(0, 0, footBLEFTpressed.getMinimumWidth(), footBLEFTpressed.getMinimumHeight());
                footBLEFT.setCompoundDrawables(null, footBLEFTpressed, null, null);
                setEnableButton();
                break;

            case CONNECTING://连接中
                statusFT_txt.setText("连接中");
                footBLEFTpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                footBLEFTpressed.setBounds(0, 0, footBLEFTpressed.getMinimumWidth(), footBLEFTpressed.getMinimumHeight());
                footBLEFT.setCompoundDrawables(null, footBLEFTpressed, null, null);
                break;

            case DISCONNECT://连接断开
                statusFT_txt.setText("断开");
                footBLEFTpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                footBLEFTpressed.setBounds(0, 0, footBLEFTpressed.getMinimumWidth(), footBLEFTpressed.getMinimumHeight());
                footBLEFT.setCompoundDrawables(null, footBLEFTpressed, null, null);
                break;
        }
    }

    Runnable BleRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences shares2 = getSharedPreferences( "Torque_Decive", Activity.MODE_PRIVATE );
            if(!shares2.getBoolean("BondDecive",false))
            {
                Intent intent = new Intent(TorqueActivity.this, MainActivity.class);
                startActivity(intent);
            }else
            {
                deviceTorque= bluetoothAdapter.getRemoteDevice(shares2.getString("Torque",""));
                if(deviceTorque == null)
                {
                    Toast.makeText(TorqueActivity.this,"未绑定制停距离蓝牙！",Toast.LENGTH_LONG).show();
                }else{
                    DeviceModule deviceModuleTorque = new DeviceModule(deviceTorque.getName(),deviceTorque);
                    moduleTorque= deviceModuleTorque;
                    mHoldBluetoothTorque.connect(moduleTorque);
                    //controlClientBrake = SocketThread.getClient(deviceBrake);
                    Log.d("mHoldBluetoothTorque","开始连接蓝牙");
                }
            }
        }
    };
    @SuppressLint("SetTextI18n")
    private View getListView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView =getLayoutInflater().inflate(R.layout.torque_list, null);//加载布局
        }
        TextView angleMaxL_txt = (TextView) convertView.findViewById(R.id.angelMaxLT);
        TextView angleMaxR_txt = (TextView) convertView.findViewById(R.id.angelMaxRT);
        TextView forceMaxL_txt = (TextView) convertView.findViewById(R.id.forceMaxLT);
        TextView forceMaxR_txt = (TextView) convertView.findViewById(R.id.forceMaxRT);
        TextView angelErrMax_txt = (TextView) convertView.findViewById(R.id.angelErrMax);
        TextView forceErrMax_txt = (TextView) convertView.findViewById(R.id.forceErrMax);

        torqueEntity torqueData = mDatas.get(position);
        angleMaxL_txt.setText("左最大角度(°):" + torqueData.getAngleMaxL());
        angleMaxR_txt.setText("右最大角度(°):" + torqueData.getAngleMaxR());
        forceMaxL_txt.setText("左最大受力(N):" + torqueData.getForceMaxL());
        forceMaxR_txt.setText("右最大受力(N):" + torqueData.getForceMaxR());
        angelErrMax_txt.setText("最大角度差(°):" + torqueData.getAngleErrMax());
        forceErrMax_txt.setText("最大受力差（N）:" + torqueData.getForceErrMax());

        return convertView;
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.angleTRBtn:
                    angleFlag=true;
                    forceFlag=false;
                    str_TestItem="方向盘最大自由转动量试验";
                    break;
                case R.id.forceTRBtn:
                    angleFlag=false;
                    forceFlag=true;
                    str_TestItem="方向盘转向力试验";
                    break;
                default:
                    break;
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
                case R.id.startTBtn: {
                    Finish = false;
                    startTBtn.setEnabled(false);
                    startTBtnpressed = getResources().getDrawable(R.drawable.start);
                    startTBtnpressed.setBounds(0, 0, startTBtnpressed.getMinimumWidth(), startTBtnpressed.getMinimumHeight());
                    startTBtn.setCompoundDrawables(null, startTBtnpressed, null, null);
                    for(int i=0;i<3;i++) {
                        senddata[0] = 0x4B;
                        senddata[1] = 0x53;
                        senddata[2] = 0x04;
                        senddata[3] = 0x00;
                        senddata[4] = 0x01;
                        senddata[5] = 0x00;
                        senddata[6] = 0x03;
                        senddata[7] = 0x3A;
                        senddata[8] = 0x3B;
                        mHoldBluetoothTorque.sendData(moduleTorque,senddata);
                    }
                    for(int i=0;i<17;i++)
                    {
                        torqueReceive[i]=0;
                    }

                    realForce_Data.clear();
                    dynamicLineChartManager_Torque.clear();
                    realForce=0.0f;
                    realAngle=0.0f;
                    realForceL=0.0f;
                    realAngleL=0.0f;
                    realForceR=0.0f;
                    realAngleR=0.0f;
                    forceFirst=0.0f;
                    angleFirst=0.0f;
                    firstNum=0;
                    ChartYMax = 0.0f;
                    ChartXMax = 0.0f;
                    str_TestItem="";

                    str_realForceL= myformat.format(Math.abs(realForceL));
                    str_forceMaxL= myformat.format(Math.abs(forceMaxL));
                    str_realAngleL= myformat.format(Math.abs(realAngleL));
                    str_angleMaxL= myformat.format(Math.abs(angleMaxL));
                    str_realForceR= myformat.format(Math.abs(realForceR));
                    str_forceMaxR= myformat.format(Math.abs(forceMaxR));
                    str_realAngleR= myformat.format(Math.abs(realAngleR));
                    str_angleMaxR= myformat.format(Math.abs(angleMaxR));

                    realAngleLT_txt.setText(str_realAngleL);
                    realAngleRT_txt.setText(str_realAngleR);
                    angleMaxLT_txt.setText(str_angleMaxL);
                    angleMaxRT_txt.setText(str_angleMaxR);
                    realForceLT_txt.setText(str_realForceL);
                    realForceRT_txt.setText(str_realForceR);
                    forceMaxLT_txt.setText(str_forceMaxL);
                    forceMaxRT_txt.setText(str_forceMaxR);
                    angleMaxLT_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                    angleMaxRT_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                    angleErrT_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                    forceMaxLT_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                    forceMaxRT_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                    forceErrT_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                    handler.postDelayed(ReceiveRunnable,100);
//                    pd.setMessage("正在读取初始位置");
//                    pd.show();
                }
                break;
                case R.id.stopTBtn: {
                    Finish = true;
                    stopTBtn.setEnabled(false);
                    stopTBtnpressed = getResources().getDrawable(R.drawable.stop);
                    stopTBtnpressed.setBounds(0, 0, stopTBtnpressed.getMinimumWidth(), stopTBtnpressed.getMinimumHeight());
                    stopTBtn.setCompoundDrawables(null, stopTBtnpressed, null, null);

                    startTBtn.setEnabled(true);
                    startTBtnpressed = getResources().getDrawable(R.drawable.start1);
                    startTBtnpressed.setBounds(0, 0, startTBtnpressed.getMinimumWidth(), startTBtnpressed.getMinimumHeight());
                    startTBtn.setCompoundDrawables(null, startTBtnpressed, null, null);
                    for(int i=0;i<3;i++) {
                        senddata[0] = 0x4B;
                        senddata[1] = 0x53;
                        senddata[2] = 0x04;
                        senddata[3] = 0x00;
                        senddata[4] = 0x01;
                        senddata[5] = 0x00;
                        senddata[6] = 0x05;
                        senddata[7] = 0x3A;
                        senddata[8] = 0x3B;
                        mHoldBluetoothTorque.sendData(moduleTorque,senddata);
                    }
                    if(forceErrMax<forceErrSet)
                    {
                        str_forceErrResult="合格";
                    }else
                    {
                        str_forceErrResult="不合格";
                    }
                    handler.removeCallbacks (ReceiveRunnable);
                }
                break;
                case R.id.scanTBtn: {
                    Intent intent = new Intent(TorqueActivity.this, TorqueSaveActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.printTBtn: {
                    printTBtn.setEnabled(false);
                    printTBtnpressed = getResources().getDrawable(R.drawable.print);
                    printTBtnpressed.setBounds(0, 0, printTBtnpressed.getMinimumWidth(), printTBtnpressed.getMinimumHeight());
                    printTBtn.setCompoundDrawables(null, printTBtnpressed, null, null);
                    if (Finish) {
                        if (printDataService == null) {           //首次连接打印机
                            SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                            if (!shares.getBoolean("BondPrinter", false)) {
                                Toast.makeText(TorqueActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(TorqueActivity.this.getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                        } else {          //打印数据
                            PrintMeasureData();
                        }
                    } else {
                        Toast.makeText(TorqueActivity.this, "没有可以打印的数据", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                case R.id.saveTBtn: {
                    saveTBtn.setEnabled(false);
                    saveTBtnpressed = getResources().getDrawable(R.drawable.save);
                    saveTBtnpressed.setBounds(0, 0, saveTBtnpressed.getMinimumWidth(), saveTBtnpressed.getMinimumHeight());
                    saveTBtn.setCompoundDrawables(null, saveTBtnpressed, null, null);
                    torqueAdd(str_angleMaxL,str_angleMaxR,str_forceMaxL,str_forceMaxR,str_angleErrMax,str_forceErrMax);
                }
                break;
                case R.id.exportTBtn: {
                    exportTBtn.setEnabled(false);
                    exportTBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportTBtnpressed.setBounds(0, 0, exportTBtnpressed.getMinimumWidth(), exportTBtnpressed.getMinimumHeight());
                    exportTBtn.setCompoundDrawables(null, exportTBtnpressed, null, null);
                    CreatePdf();
                    Toast.makeText(TorqueActivity.this, "数据已导出到手机根目录/Documents/叉车方向盘转向力与转向角度检测报告", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.backTBtn: {
                    Intent intent1 = new Intent(TorqueActivity.this, ChacheActivity.class);
                    startActivity(intent1);
                    finish();
                }
                break;
                case R.id.exitTBtn: {
                    finish();
                    ActivityCollector.finishAll();
                }
                break;
                case R.id.angleMaxTBtn: {
                    angleMaxL=0.0f;
                    angleMaxR=0.0f;
                    str_angleMaxL= myformat.format(angleMaxL);
                    str_angleMaxR= myformat.format(angleMaxR);
                    angleMaxLT_txt.setText(str_angleMaxL);
                    angleMaxRT_txt.setText(str_angleMaxR);
                }
                break;
                case R.id.forceMaxTBtn: {
                    forceMaxL=0.0f;
                    forceMaxR=0.0f;
                    str_forceMaxL= myformat.format(forceMaxL);
                    str_forceMaxR= myformat.format(forceMaxR);
                    forceMaxLT_txt.setText(str_forceMaxL);
                    forceMaxRT_txt.setText(str_forceMaxR);
                }
                break;
                case R.id.angleErrMaxTBtn: {
                    angleErrMax=0.0f;
                    str_angleErrMax= myformat.format(angleErrMax);
                    angleErrT_txt.setText(str_angleErrMax);
                }
                break;
                case R.id.forceErrMaxTBtn: {
                    forceErrMax=0.0f;
                    str_forceErrMax= myformat.format(forceErrMax);
                    forceErrT_txt.setText(str_forceErrMax);
                }
                break;
                case R.id.addTBtn: {
                    addTBtn.setEnabled(false);
                    addTBtnpressed = getResources().getDrawable(R.drawable.add);
                    addTBtnpressed.setBounds(0, 0,addTBtnpressed.getMinimumWidth(), addTBtnpressed.getMinimumHeight());
                    addTBtn.setCompoundDrawables(null, addTBtnpressed, null, null);

                    torqueEntity addEntity = new torqueEntity();
                    addEntity.setAngleMaxL(str_angleMaxL);
                    addEntity.setAngleMaxR(str_angleMaxR);
                    addEntity.setForceMaxL(str_forceMaxL);
                    addEntity.setForceMaxR(str_forceMaxR);
                    addEntity.setAngleErrMax(str_angleErrMax);
                    addEntity.setForceErrMax(str_forceErrMax);
                    mDatas.add(addEntity);
                    mAdapter.notifyDataSetChanged();
                    forcelistView.invalidateViews();
                }
                break;
                case R.id.torqueHelpBtn: {
                    showControlDialog();
                }
                break;
                case R.id.zeroTBtn: {
                    zeroTBtn.setEnabled(false);
                    zeroTBtnpressed = getResources().getDrawable(R.drawable.zero);
                    zeroTBtnpressed.setBounds(0, 0, zeroTBtnpressed.getMinimumWidth(), zeroTBtnpressed.getMinimumHeight());
                    zeroTBtn.setCompoundDrawables(null, zeroTBtnpressed, null, null);

                    angleMaxL=0.0f;
                    angleMaxR=0.0f;
                    str_angleMaxL= myformat.format(angleMaxL);
                    str_angleMaxR= myformat.format(angleMaxR);
                    angleMaxLT_txt.setText(str_angleMaxL);
                    angleMaxRT_txt.setText(str_angleMaxR);

                    forceMaxL=0.0f;
                    forceMaxR=0.0f;
                    str_forceMaxL= myformat.format(forceMaxL);
                    str_forceMaxR= myformat.format(forceMaxR);
                    forceMaxLT_txt.setText(str_forceMaxL);
                    forceMaxRT_txt.setText(str_forceMaxR);

                    angleErrMax=0.0f;
                    str_angleErrMax= myformat.format(angleErrMax);
                    angleErrT_txt.setText(str_angleErrMax);

                    forceErrMax=0.0f;
                    str_forceErrMax= myformat.format(forceErrMax);
                    forceErrT_txt.setText(str_forceErrMax);
                }
                break;
                default: {

                }
                break;
            }
            handler.postDelayed(sendRunnable, 1000);
        }
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
    private void showControlDialog(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TorqueActivity.this);
        normalDialog.setIcon(R.drawable.help1);
        normalDialog.setTitle("TSG N0001-2022 场（厂）内专用机动车辆安全技术监察规程");
        normalDialog.setMessage("1、方向盘操作的叉车原地转向操作力应不大于20N" +
                "\n2、左右转向操作力相差应不大于5N");
        normalDialog.setPositiveButton("确  定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });

        normalDialog.show();
    }

    Runnable FirstRunnable = new Runnable() {
        @Override
        public void run() {
            String[] str_ReceiveData=new String[17];
            angleFirst=0.0f;
            forceFirst=0.0f;
            //torqueReceive=controlClientTorque.receive;
            int TorqueRevNum=0;
            byte[] mReceiveTorque=new byte[17];
            //torqueReceive=controlClientTorque.receive;
            if(torqueReceive.length>=17)
            {
            for(int i=0;i<torqueReceive.length;i++)
            {
                if(torqueReceive[TorqueRevNum]==0x4B)
                {
                    break;
                }else{
                    TorqueRevNum++;
                }
            }
            for(int i=0;i<17;i++)
            {
                if((TorqueRevNum+i)<torqueReceive.length)
                {
                    mReceiveTorque[i] =torqueReceive[TorqueRevNum+i];
                }else{
                    mReceiveTorque[i] =torqueReceive[TorqueRevNum+i-torqueReceive.length];
                }
            }

            for(int i=0;i<17;i++)
            {
                str_ReceiveData[i]=myformat.format(mReceiveTorque[i]);
            }
            Log.d("torqueActivity","     "+str_ReceiveData[0]+"  "+str_ReceiveData[1]+" "+str_ReceiveData[2]+"  "+str_ReceiveData[3]+" "+str_ReceiveData[4]+"  "+str_ReceiveData[5]+" "+
                    str_ReceiveData[6]+"  "+str_ReceiveData[7]+" "+str_ReceiveData[8]+"  "+str_ReceiveData[9]+" "+str_ReceiveData[10]+"  "+str_ReceiveData[11]+" "+
                    str_ReceiveData[12]+"  "+str_ReceiveData[13]+" "+str_ReceiveData[14]+str_ReceiveData[15]+str_ReceiveData[16]+"  ");

            if((mReceiveTorque[0]==0x4B) && (mReceiveTorque[1]==0x53)) {
                switch (mReceiveTorque[2]) {
                    case 0x04:
                        int tmp = 0;
                        tmp = (char) (mReceiveTorque[4] & 0xFF) * 256 + (char) (mReceiveTorque[5] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) realAngle = (float) tmp / 10.0f;
                        else realAngle = (float) (0x10000 - tmp) / 10.0f * (-1.0f);
                        tmp = (char) (mReceiveTorque[6] & 0xFF) * 256 + (char) (mReceiveTorque[7] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) realForce = (float) tmp / 10.0f;
                        else realForce = (float) (0x10000 - tmp) / 10.0f * (-1.0f);

                        angleFirst= angleFirst+realAngle;
                        forceFirst=forceFirst+realForce;
                    break;
                    default : break;
                }
            }
            Log.d("TorqueActivity","   "+"angleFirst="+myformat.format(angleFirst)+"  "+"forceFirst="+myformat.format(forceFirst));
            handler.postDelayed(FirstRunnable,100);
            }
        }
    };
    Runnable ReceiveRunnable = new Runnable() {
        @Override
        public void run() {
            //pd.dismiss();
            int TorqueRevNum=0;
            byte[] mReceiveTorque=new byte[17];
            String[] str_ReceiveData=new String[17];
            //torqueReceive=controlClientTorque.receive;
       if(torqueReceive.length>=17)
        {
            for(int i=0;i<torqueReceive.length;i++)
            {
                if(torqueReceive[TorqueRevNum]==0x4B)
                {
                    break;
                }else{
                    TorqueRevNum++;
                }
            }
            for(int i=0;i<17;i++)
            {
                if((TorqueRevNum+i)<torqueReceive.length)
                {
                    mReceiveTorque[i] =torqueReceive[TorqueRevNum+i];
                }else{
                    mReceiveTorque[i] =torqueReceive[TorqueRevNum+i-torqueReceive.length];
                }
            }

            for(int i=0;i<17;i++)
            {
                str_ReceiveData[i]=myformat.format(mReceiveTorque[i]);
            }
            Log.d("torqueActivity","     "+str_ReceiveData[0]+"  "+str_ReceiveData[1]+" "+str_ReceiveData[2]+"  "+str_ReceiveData[3]+" "+str_ReceiveData[4]+"  "+str_ReceiveData[5]+" "+
                    str_ReceiveData[6]+"  "+str_ReceiveData[7]+" "+str_ReceiveData[8]+"  "+str_ReceiveData[9]+" "+str_ReceiveData[10]+"  "+str_ReceiveData[11]+" "+
                    str_ReceiveData[12]+"  "+str_ReceiveData[13]+" "+str_ReceiveData[14]+str_ReceiveData[15]+str_ReceiveData[16]+"  ");

            if((mReceiveTorque[0]==0x4B) && (mReceiveTorque[1]==0x53)) {
                switch (mReceiveTorque[2]) {
                    case 0x04:
                        int tmp = 0;
                        tmp = (char) (mReceiveTorque[4] & 0xFF) * 256 + (char) (mReceiveTorque[5] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) realAngle = (float) tmp / 10.0f;
                        else realAngle = (float) (0x10000 - tmp) / 10.0f * (-1.0f);

                        tmp = (char) (mReceiveTorque[6] & 0xFF) * 256 + (char) (mReceiveTorque[7] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) realForce = (float) tmp / 10.0f;
                        else realForce = (float) (0x10000 - tmp) / 10.0f * (-1.0f);

//                        realForce=realForce-forceFirst;
//                        realAngle=realAngle-angleFirst;
                        realForce_Data.add(realForce);
                        realAngle_Data.add(realAngle);
                        Log.d("TorqueActivity", "   " + "realForce=" + myformat.format(realForce) + "  " + "realAngle=" + myformat.format(realAngle));

                        if (angleFlag)
                        {
                            if (realAngle > 0)
                            {
                                realAngleR=0.0f;
                                str_realAngleR = myformat.format(Math.abs(realAngleR));
                                realAngleRT_txt.setText(str_realAngleR);
                                realAngleL = realAngle;
                                if(Math.abs(angleMaxL)<Math.abs(realAngleL))
                                {
                                    angleMaxL = realAngleL;
                                }
                                str_realAngleL = myformat.format(Math.abs(realAngleL));
                                realAngleLT_txt.setText(str_realAngleL);

                                str_angleMaxL = myformat.format(Math.abs(angleMaxL));
                                angleMaxLT_txt.setText(str_angleMaxL);

                                if(Math.abs(realAngle) > 15.0f)
                                {
                                    angleMaxLT_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                                }
                            }else
                            {
                                realAngleL=0.0f;
                                str_realAngleL = myformat.format(Math.abs(realAngleL));
                                realAngleLT_txt.setText(str_realAngleL);
                                realAngleR = realAngle;
                                if(Math.abs(angleMaxR)<Math.abs(realAngleR))
                                {
                                    angleMaxR = realAngleR;
                                }
                                str_realAngleR = myformat.format(Math.abs(realAngleR));
                                realAngleRT_txt.setText(str_realAngleR);

                                str_angleMaxR = myformat.format(Math.abs(angleMaxR));
                                angleMaxRT_txt.setText(str_angleMaxR);

                                if(Math.abs(realAngle) > 15.0f)
                                {
                                    angleMaxRT_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                                }
                            }
                            angleErrMax=Math.abs(angleMaxR-angleMaxL);
                            str_angleErrMax= myformat.format(angleErrMax);
                            angleErrT_txt.setText(str_angleErrMax);
                            if(angleErrMax>30.f)
                            {
                                angleErrT_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                            }
                        }

                        if(forceFlag)
                        {
                            if(realForce>0)
                            {
                                realForceR=0.0f;
                                str_realForceR = myformat.format(Math.abs(realForceR));
                                realForceRT_txt.setText(str_realForceR);
                                realForceL = realForce;
                                if(Math.abs(forceMaxL)<Math.abs(realForceL))
                                {
                                    forceMaxL = realForceL;
                                }
                                str_realForceL = myformat.format(Math.abs(realForceL));
                                realForceLT_txt.setText(str_realForceL);

                                str_forceMaxL = myformat.format(Math.abs(forceMaxL));
                                forceMaxLT_txt.setText(str_forceMaxL);

                                if(Math.abs(realForce)>20.0f)
                                {
                                    forceMaxLT_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                                }
                            }else
                            {
                                realForceL=0.0f;
                                str_realForceL = myformat.format(Math.abs(realForceL));
                                realForceLT_txt.setText(str_realForceL);
                                realForceR = realForce;
                                if(Math.abs(forceMaxR)<Math.abs(realForceR))
                                {
                                    forceMaxR = realForceR;
                                }
                                str_realForceR = myformat.format(Math.abs(realForceR));
                                realForceRT_txt.setText(str_realForceR);

                                str_forceMaxR = myformat.format(Math.abs(forceMaxR));
                                forceMaxRT_txt.setText(str_forceMaxR);

                                if(Math.abs(realForce)>20.0f)
                                {
                                    forceMaxRT_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                                }
                            }

                            forceErrMax=Math.abs(forceMaxR+forceMaxL);
                            str_forceErrMax= myformat.format(forceErrMax);
                            forceErrT_txt.setText(str_forceErrMax);
                        }

                        if(ChartYMax<Math.abs(realForce))
                        {
                            ChartYMax =Math.abs(realForce);
                        }
                        if(ChartYMax<Math.abs(realAngle))
                        {
                            ChartYMax =Math.abs(realAngle);
                        }
                        ChartXMax =ChartXMax+0.03f;
                        if(ChartXMax>100000)
                        {
                            ChartXMax=0;
                        }

                        dynamicLineChartManager_Torque.setYAxis(ChartYMax*1.2f, ChartYMax*1.2f*(-1.0f), 4);
                        dynamicLineChartManager_Torque.setXAxis(ChartXMax*1.2f, 0, 10,0);
                        dynamicLineChartManager_Torque.addEntry(realForce,realAngle);
                        break;
                    default : break;
                }
            }
          }
            handler.postDelayed(ReceiveRunnable,30);
        }
    };

    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            stopTBtn.setEnabled(true);
            stopTBtnpressed = getResources().getDrawable(R.drawable.stop1);
            stopTBtnpressed.setBounds(0, 0, stopTBtnpressed.getMinimumWidth(), stopTBtnpressed.getMinimumHeight());
            stopTBtn.setCompoundDrawables(null, stopTBtnpressed, null, null);

            scanTBtn.setEnabled(true);
            scanTBtnpressed = getResources().getDrawable(R.drawable.scan1);
            scanTBtnpressed.setBounds(0, 0, scanTBtnpressed.getMinimumWidth(), scanTBtnpressed.getMinimumHeight());
            scanTBtn.setCompoundDrawables(null, scanTBtnpressed, null, null);

            printTBtn.setEnabled(true);
            printTBtnpressed = getResources().getDrawable(R.drawable.print1);
            printTBtnpressed.setBounds(0, 0, printTBtnpressed.getMinimumWidth(), printTBtnpressed.getMinimumHeight());
            printTBtn.setCompoundDrawables(null, printTBtnpressed, null, null);

            saveTBtn.setEnabled(true);
            saveTBtnpressed = getResources().getDrawable(R.drawable.save1);
            saveTBtnpressed.setBounds(0, 0, saveTBtnpressed.getMinimumWidth(), saveTBtnpressed.getMinimumHeight());
            saveTBtn.setCompoundDrawables(null, saveTBtnpressed, null, null);

            exportTBtn.setEnabled(true);
            exportTBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportTBtnpressed.setBounds(0, 0, exportTBtnpressed.getMinimumWidth(), exportTBtnpressed.getMinimumHeight());
            exportTBtn.setCompoundDrawables(null, exportTBtnpressed, null, null);

            addTBtn.setEnabled(true);
            addTBtnpressed = getResources().getDrawable(R.drawable.add1);
            addTBtnpressed.setBounds(0, 0, addTBtnpressed.getMinimumWidth(), addTBtnpressed.getMinimumHeight());
            addTBtn.setCompoundDrawables(null, addTBtnpressed, null, null);

            zeroTBtn.setEnabled(true);
            zeroTBtnpressed = getResources().getDrawable(R.drawable.zero1);
            zeroTBtnpressed.setBounds(0, 0, zeroTBtnpressed.getMinimumWidth(), zeroTBtnpressed.getMinimumHeight());
            zeroTBtn.setCompoundDrawables(null, zeroTBtnpressed, null, null);
        }
    };
    private void setEnableButton()
    {
        startTBtn.setEnabled(true);
        startTBtnpressed = getResources().getDrawable(R.drawable.start1);
        startTBtnpressed.setBounds(0, 0, startTBtnpressed.getMinimumWidth(), startTBtnpressed.getMinimumHeight());
        startTBtn.setCompoundDrawables(null, startTBtnpressed, null, null);

        stopTBtn.setEnabled(true);
        stopTBtnpressed = getResources().getDrawable(R.drawable.stop1);
        stopTBtnpressed.setBounds(0, 0, stopTBtnpressed.getMinimumWidth(), stopTBtnpressed.getMinimumHeight());
        stopTBtn.setCompoundDrawables(null, stopTBtnpressed, null, null);

        scanTBtn.setEnabled(true);
        scanTBtnpressed = getResources().getDrawable(R.drawable.scan1);
        scanTBtnpressed.setBounds(0, 0, scanTBtnpressed.getMinimumWidth(), scanTBtnpressed.getMinimumHeight());
        scanTBtn.setCompoundDrawables(null, scanTBtnpressed, null, null);

        printTBtn.setEnabled(true);
        printTBtnpressed = getResources().getDrawable(R.drawable.print1);
        printTBtnpressed.setBounds(0, 0, printTBtnpressed.getMinimumWidth(), printTBtnpressed.getMinimumHeight());
        printTBtn.setCompoundDrawables(null, printTBtnpressed, null, null);

        saveTBtn.setEnabled(true);
        saveTBtnpressed = getResources().getDrawable(R.drawable.save1);
        saveTBtnpressed.setBounds(0, 0, saveTBtnpressed.getMinimumWidth(), saveTBtnpressed.getMinimumHeight());
        saveTBtn.setCompoundDrawables(null, saveTBtnpressed, null, null);

        exportTBtn.setEnabled(true);
        exportTBtnpressed = getResources().getDrawable(R.drawable.export1);
        exportTBtnpressed.setBounds(0, 0, exportTBtnpressed.getMinimumWidth(), exportTBtnpressed.getMinimumHeight());
        exportTBtn.setCompoundDrawables(null, exportTBtnpressed, null, null);

        addTBtn.setEnabled(true);
        addTBtnpressed = getResources().getDrawable(R.drawable.add1);
        addTBtnpressed.setBounds(0, 0, addTBtnpressed.getMinimumWidth(), addTBtnpressed.getMinimumHeight());
        addTBtn.setCompoundDrawables(null, addTBtnpressed, null, null);

        zeroTBtn.setEnabled(true);
        zeroTBtnpressed = getResources().getDrawable(R.drawable.zero1);
        zeroTBtnpressed.setBounds(0, 0, zeroTBtnpressed.getMinimumWidth(), zeroTBtnpressed.getMinimumHeight());
        zeroTBtn.setCompoundDrawables(null, zeroTBtnpressed, null, null);

    }

    private void setButton()
    {
        startTBtn.setEnabled(false);
        startTBtnpressed = getResources().getDrawable(R.drawable.start);
        startTBtnpressed.setBounds(0, 0, startTBtnpressed.getMinimumWidth(), startTBtnpressed.getMinimumHeight());
        startTBtn.setCompoundDrawables(null, startTBtnpressed, null, null);

        stopTBtn.setEnabled(false);
        stopTBtnpressed = getResources().getDrawable(R.drawable.stop);
        stopTBtnpressed.setBounds(0, 0, stopTBtnpressed.getMinimumWidth(), stopTBtnpressed.getMinimumHeight());
        stopTBtn.setCompoundDrawables(null, stopTBtnpressed, null, null);

        printTBtn.setEnabled(false);
        printTBtnpressed = getResources().getDrawable(R.drawable.print);
        printTBtnpressed.setBounds(0, 0, printTBtnpressed.getMinimumWidth(), printTBtnpressed.getMinimumHeight());
        printTBtn.setCompoundDrawables(null, printTBtnpressed, null, null);

        saveTBtn.setEnabled(false);
        saveTBtnpressed = getResources().getDrawable(R.drawable.save);
        saveTBtnpressed.setBounds(0, 0, saveTBtnpressed.getMinimumWidth(), saveTBtnpressed.getMinimumHeight());
        saveTBtn.setCompoundDrawables(null, saveTBtnpressed, null, null);

        exportTBtn.setEnabled(false);
        exportTBtnpressed = getResources().getDrawable(R.drawable.export);
        exportTBtnpressed.setBounds(0, 0, exportTBtnpressed.getMinimumWidth(), exportTBtnpressed.getMinimumHeight());
        exportTBtn.setCompoundDrawables(null, exportTBtnpressed, null, null);

        addTBtn.setEnabled(false);
        addTBtnpressed = getResources().getDrawable(R.drawable.add);
        addTBtnpressed.setBounds(0, 0, addTBtnpressed.getMinimumWidth(), addTBtnpressed.getMinimumHeight());
        addTBtn.setCompoundDrawables(null, addTBtnpressed, null, null);

        zeroTBtn.setEnabled(false);
        zeroTBtnpressed = getResources().getDrawable(R.drawable.zero);
        zeroTBtnpressed.setBounds(0, 0, zeroTBtnpressed.getMinimumWidth(), zeroTBtnpressed.getMinimumHeight());
        zeroTBtn.setCompoundDrawables(null, zeroTBtnpressed, null, null);
    }

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
                    printDataService = new PrintDataService(TorqueActivity.this,shares.getString("Printer",""));
                    //Toast.makeText(overActivity.this,"蓝牙打印机连接中...",Toast.LENGTH_LONG).show();
                }
                if(printDataService != null){
                    PrintConnect = printDataService.connect();
                    if(PrintConnect){
                        Toast.makeText(TorqueActivity.this,"蓝牙打印机连接成功...",Toast.LENGTH_LONG).show();
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
        printDataService.send("叉车方向盘转向力与转向角度检测结果");
        printDataService.send("\n*******************************\n");
        SimpleDateFormat formatter   =   new SimpleDateFormat("检测时间"+":yyyy-MM-dd  HH:mm:ss\n", Locale.CHINA);
        Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
        String   str   =   formatter.format(curDate);
        printDataService.send(str);
        printDataService.send("受检单位"+": "+str_company+"\n");//
        printDataService.send("设备编号"+": "+ str_number+"\n");//
        printDataService.send("车牌编号"+": "+ str_chacheNumber+"\n");//
        printDataService.send("车辆类型"+": "+ str_chacheType+"\n");//
        printDataService.send("车辆组别"+": "+ str_chacheGroup+"\n");//
        printDataService.send("左最大角度"+": "+ str_angleMaxL+"°"+"\n");//
        printDataService.send("右最大角度"+": "+ str_angleMaxR+"°"+"\n");//
        printDataService.send("左最大受力"+": "+ str_forceMaxL+"N"+"\n");//
        printDataService.send("右最大受力"+": "+ str_forceMaxR+"N"+"\n");//
        printDataService.send("最大角度差值"+": "+ str_angleErrMax+"°"+"\n");//
        printDataService.send("最大受力差值"+": "+ str_forceErrMax+"N"+"\n");//

        printDataService.send("*******************************\n\n\n\n");
        Toast.makeText(TorqueActivity.this,"打印完成！",Toast.LENGTH_SHORT).show();
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
        TorqueActivity.this.sendBroadcast(intent);
    }
    //创建PDF文件-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(TorqueActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //创建目录
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车方向盘转向力与转向角度检测报告"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车方向盘转向力与转向角度检测报告"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车方向盘转向力与转向角度检测报告" + File.separator + curDate.toString ()+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    pdfcontext = new Paragraph("叉车方向盘转向力与转向角度检测报告",setChineseTitleFont());
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

                    cell.setPhrase(new Phrase("左最大角度值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleMaxL,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("右最大角度值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleMaxR,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("左最大受力值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_forceMaxL,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("右最大受力值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_forceMaxR,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("最大角度差值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleErrMax,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("最大受力差值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_forceErrMax,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);

                    doc.add(mtable);
                    doc.close();
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车方向盘转向力与转向角度检测报告" +  File.separator + curDate.toString () +".pdf");
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
    public Font setChineseFont() {
        BaseFont bf = null;
        Font fontChinese = null;
        try {
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
        private void setData(ArrayList<Float> value,ArrayList<Float> value1,float index) {
            ArrayList<Entry> values = new ArrayList<>();
            for (int i = 0; i < value.size()- 1; i++) {
                values.add(new Entry((float)(i*index), (float) value.get(i)));
            }
            ArrayList<Entry> values1 = new ArrayList<>();
            for (int i = 0; i < value1.size()- 1; i++) {
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
            chartTorque.setVisibility(View.VISIBLE);
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
            chartTorque.setVisibility (View.VISIBLE);
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
            lineDataSet = new LineDataSet(null, "实时受力值(N)");
            lineDataSet.setLineWidth(1.0f);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setColor(Color.BLUE);
            lineDataSet.setHighLightColor(Color.BLACK);
            //设置曲线填充
            lineDataSet.setDrawFilled(false);
            lineDataSet.setDrawCircleHole(false);
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setDrawValues(false);
            lineDataSet.setMode(LineDataSet.Mode.LINEAR);

            //数据样式
            lineDataSet1 = new LineDataSet(null, "实时角度值(°)");
            lineDataSet1.setLineWidth(1.0f);
            lineDataSet1.setDrawCircles(false);
            lineDataSet1.setColor(Color.RED);
            lineDataSet1.setHighLightColor(Color.BLACK);
            //设置曲线填充
            lineDataSet1.setDrawFilled(false);
            lineDataSet1.setDrawCircleHole(false);
            lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet1.setDrawValues(false);
            lineDataSet1.setMode(LineDataSet.Mode.LINEAR);

            lineData = new LineData(lineDataSet,lineDataSet1);
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
            lineChart.invalidate();
        }

        private void addEntry(float force,float angle){
            Entry entry = new Entry(lineDataSet.getEntryCount()*0.03f,force);
            Entry entry1 = new Entry(lineDataSet1.getEntryCount()*0.03f,angle);
            lineData.addEntry(entry, 0);
            lineData.addEntry(entry1, 1);
            chartTorque.notifyDataSetChanged();
            chartTorque.moveViewToX(0.00f);
            lineChart.invalidate();
        }
        private void freshChart(float force) {
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
            leftAxis.setGranularityEnabled(false);
            leftAxis.setDrawGridLines(false);
            leftAxis.setAxisMaximum(max);
            leftAxis.setAxisMinimum(min);
            leftAxis.setTextColor(Color.WHITE);
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
            LegFre.setTextSize(10f);
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
        dynamicLineChartManager_Torque.setData(realForce_Data,realAngle_Data,0.1f);
        dynamicLineChartManager_Torque.setYAxis(400, -400, 4);
        dynamicLineChartManager_Torque.setXAxis(120, 0, 10,0);
        dynamicLineChartManager_Torque.setHightLimitLine(0f, "");
        dynamicLineChartManager_Torque.desChart(names.get (0),165);
    }
    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        super.onDestroy();
        if(deviceTorque != null)
        {
            mHoldBluetoothTorque.disconnect(moduleTorque);
        }
    }
}