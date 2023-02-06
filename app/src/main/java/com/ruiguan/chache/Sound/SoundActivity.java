package com.ruiguan.chache.Sound;

import android.Manifest;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

public class SoundActivity extends SoundSaveActivity  {
    private BluetoothDevice deviceSound= null;
    private DeviceModule moduleSound= null;
    private HoldBluetooth mHoldBluetoothSound= null;
    private BluetoothAdapter bluetoothAdapter = null;
    private List<DeviceModule> modules;
    private final String CONNECTED = "已连接",CONNECTING = "连接中",DISCONNECT = "断线了";

    private String str_company;
    private String str_number;
    private String str_chacheNumber;
    private String str_chacheType;
    private String str_chacheGroup;

    private Button startSoundBtn;
    private Button stopSoundBtn;
    private Button scanSoundBtn;
    private Button printSoundBtn;
    private Button saveSoundBtn;
    private Button exportSoundBtn;
    private Button backSoundBtn;
    private Button exitSoundBtn;
    private Button deviceSoundBtn;
    private Button zeroSoundBtn;
    private Button soundHelpBtn;

    private Drawable startSoundBtnpressed;
    private Drawable stopSoundBtnpressed;
    private Drawable scanSoundBtnpressed;
    private Drawable printSoundBtnpressed;
    private Drawable saveSoundBtnpressed;
    private Drawable exportSoundBtnpressed;
    private Drawable deviceSoundBtnpressed;
    private Drawable zeroSoundBtnpressed;

    private TextView realSound_txt;
    private TextView SoundMax_txt;
    private TextView status_txt;

    private byte[] senddata;
    private byte[] soundReceive;

    private float realSound;
    private float SoundMax;

    private String str_realSound;
    private String str_SoundMax;

    private float ChartYMax;
    private float ChartXMax;

    private Handler handler = new Handler();
    private LineChart chartSound;
    private ArrayList<Float> realSound_Data = new ArrayList<>();
    private SoundActivity.DynamicLineChartManager dynamicLineChartManager_Sound;
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合

    private boolean Finish=false;
    private PrintDataService printDataService = null;
    private boolean PrintConnect = false;
    java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.000");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);

        senddata=new byte[9];
        soundReceive=new byte[17];
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        final HoldBluetooth.UpdateList updateList = new HoldBluetooth.UpdateList() {
            @Override
            public void update(boolean isStart,DeviceModule deviceModule) {

            }

            @Override
            public void updateMessyCode(boolean isStart, DeviceModule deviceModule) {
            }
        };

        mHoldBluetoothSound= new HoldBluetooth();
        mHoldBluetoothSound.initHoldBluetooth(SoundActivity.this,updateList);
        initDataSoundListener();

        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_chacheNumber= chache_Input.getchacheNumber();
        str_chacheType= chache_Input.getchacheType();
        str_chacheGroup= chache_Input.getchacheGroup();

        realSound_txt= (TextView) findViewById(R.id.realSound_txt);
        SoundMax_txt=(TextView) findViewById(R.id.SoundMax_txt);
        status_txt= (TextView) findViewById(R.id.statusSound_txt);

        startSoundBtn = findViewById(R.id.startSoundBtn);
        stopSoundBtn = findViewById(R.id.stopSoundBtn);
        scanSoundBtn = findViewById(R.id.scanSoundBtn);
        printSoundBtn = findViewById(R.id.printSoundBtn);
        saveSoundBtn = findViewById(R.id.saveSoundBtn);
        exportSoundBtn = findViewById(R.id.exportSoundBtn);
        backSoundBtn = findViewById(R.id.backSoundBtn);
        exitSoundBtn = findViewById(R.id.exitSoundBtn);
        deviceSoundBtn = findViewById(R.id.deviceSoundBtn);
        zeroSoundBtn= findViewById(R.id.zeroSoundBtn);
        soundHelpBtn= findViewById(R.id.soundHelpBtn);
        View.OnClickListener bl = new SoundActivity.ButtonListener();
        setOnClickListener(startSoundBtn, bl);
        setOnClickListener(stopSoundBtn, bl);
        setOnClickListener(scanSoundBtn, bl);
        setOnClickListener(printSoundBtn, bl);
        setOnClickListener(saveSoundBtn, bl);
        setOnClickListener(exportSoundBtn, bl);
        setOnClickListener(backSoundBtn, bl);
        setOnClickListener(exitSoundBtn, bl);
        setOnClickListener(zeroSoundBtn, bl);
        setOnClickListener(soundHelpBtn, bl);
        setButton();
        chartSound =findViewById(R.id.chartSound);
        names.add ("");
        colour.add (Color.argb (255, 255, 125, 0));            //定义Fre颜色
        dynamicLineChartManager_Sound = new SoundActivity.DynamicLineChartManager(chartSound, names.get (0), colour.get (0), 0);
        ShowWave();
        handler.postDelayed(BleRunnable,2000);
    }
    //初始化蓝牙数据的监听
    private void initDataSoundListener() {
        HoldBluetooth.OnReadDataListener dataListener = new HoldBluetooth.OnReadDataListener() {
            @Override
            public void readData(String mac, byte[] data) {
                if (deviceSound.getAddress().equals(mac)){
                    soundReceive=data;
                }
            }
            @Override
            public void reading(boolean isStart) {

            }
            @Override
            public void connectSucceed() {
                modules = mHoldBluetoothSound.getConnectedArray();
                for(int i=0;i<modules.size();i++)
                {
                    if(modules.get(i).getMac().equals(deviceSound.getAddress()))
                    {
                        setSoundState(CONNECTED);//设置连接状态
                        Log.d("SoundActivity","Sound蓝牙连接成功！");
                    }
                }
            }
            @Override
            public void errorDisconnect(final DeviceModule deviceModule) {//蓝牙异常断开

                if(deviceModule.getMac().equals(deviceSound.getAddress()))
                {
                    setSoundState(DISCONNECT);//设置断开状态
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
        mHoldBluetoothSound.setOnReadListener(dataListener);
    }

    private void setSoundState(String state){
        switch (state){
            case CONNECTED://连接成功
                status_txt.setText("已连接");
                deviceSoundBtnpressed = getResources().getDrawable(R.drawable.btle_connected);
                deviceSoundBtnpressed.setBounds(0, 0,  deviceSoundBtnpressed.getMinimumWidth(),  deviceSoundBtnpressed.getMinimumHeight());
                deviceSoundBtn.setCompoundDrawables(null,  deviceSoundBtnpressed, null, null);
                setEnableButton();
                break;

            case CONNECTING://连接中
                status_txt.setText("连接中");
                deviceSoundBtnpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                deviceSoundBtnpressed.setBounds(0, 0,  deviceSoundBtnpressed.getMinimumWidth(),  deviceSoundBtnpressed.getMinimumHeight());
                deviceSoundBtn.setCompoundDrawables(null,  deviceSoundBtnpressed, null, null);
                break;

            case DISCONNECT://连接断开
                status_txt.setText("断开");
                deviceSoundBtnpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                deviceSoundBtnpressed.setBounds(0, 0,  deviceSoundBtnpressed.getMinimumWidth(),  deviceSoundBtnpressed.getMinimumHeight());
                deviceSoundBtn.setCompoundDrawables(null,  deviceSoundBtnpressed, null, null);
                break;
        }
    }

    Runnable BleRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences shares2 = getSharedPreferences( "Sound_Decive", Activity.MODE_PRIVATE );
            if(!shares2.getBoolean("BondDecive",false))
            {
                Intent intent = new Intent(SoundActivity.this, MainActivity.class);
                startActivity(intent);
            }else
            {
                deviceSound= bluetoothAdapter.getRemoteDevice(shares2.getString("Sound",""));
                if(deviceSound == null)
                {
                    Toast.makeText(SoundActivity.this,"未绑定制停距离蓝牙！",Toast.LENGTH_LONG).show();
                }else{
                    DeviceModule deviceModuleSound = new DeviceModule(deviceSound.getName(),deviceSound);
                    moduleSound= deviceModuleSound;
                    mHoldBluetoothSound.connect(moduleSound);
                    //controlClientBrake = SocketThread.getClient(deviceBrake);
                    Log.d("mHoldBluetoothSound","开始连接蓝牙");
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
                case R.id.startSoundBtn: {
                    startSoundBtn.setEnabled(false);
                    startSoundBtnpressed = getResources().getDrawable(R.drawable.start);
                    startSoundBtnpressed.setBounds(0, 0, startSoundBtnpressed.getMinimumWidth(), startSoundBtnpressed.getMinimumHeight());
                    startSoundBtn.setCompoundDrawables(null, startSoundBtnpressed, null, null);
                    for(int i=0;i<3;i++) {
                        senddata[0] = 0x4B;
                        senddata[1] = 0x53;
                        senddata[2] = 0x07;
                        senddata[3] = 0x00;
                        senddata[4] = 0x01;
                        senddata[5] = 0x00;
                        senddata[6] = 0x01;
                        senddata[7] = 0x3A;
                        senddata[8] = 0x3B;
                        mHoldBluetoothSound.sendData(moduleSound,senddata);
                    }
                    ChartYMax = 0.0f;
                    ChartXMax = 0.0f;
                    realSound_Data.clear();
                    dynamicLineChartManager_Sound.clear();
                    realSound= 0.0f;
                    SoundMax= 0.0f;
                    str_realSound = myformat.format(realSound);
                    str_SoundMax = myformat.format(SoundMax);
                    realSound_txt.setText(str_realSound);
                    SoundMax_txt.setText(str_SoundMax);
                    SoundMax_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                    handler.postDelayed(ReceiveRunnable,10);
                }
                break;
                case R.id.zeroSoundBtn: {
                    zeroSoundBtn.setEnabled(false);
                    zeroSoundBtnpressed = getResources().getDrawable(R.drawable.zero);
                    zeroSoundBtnpressed.setBounds(0, 0, zeroSoundBtnpressed.getMinimumWidth(), zeroSoundBtnpressed.getMinimumHeight());
                    zeroSoundBtn.setCompoundDrawables(null, zeroSoundBtnpressed, null, null);
                    SoundMax= 0.0f;
                    str_SoundMax = myformat.format(SoundMax);
                    SoundMax_txt.setText(str_SoundMax);
                }
                break;
                case R.id.stopSoundBtn: {
                    Finish = true;
                    stopSoundBtn.setEnabled(false);
                    stopSoundBtnpressed = getResources().getDrawable(R.drawable.stop);
                    stopSoundBtnpressed.setBounds(0, 0, stopSoundBtnpressed.getMinimumWidth(), stopSoundBtnpressed.getMinimumHeight());
                    stopSoundBtn.setCompoundDrawables(null, stopSoundBtnpressed, null, null);
                    for(int i=0;i<3;i++) {
                        senddata[0] = 0x4B;
                        senddata[1] = 0x53;
                        senddata[2] = 0x07;
                        senddata[3] = 0x00;
                        senddata[4] = 0x01;
                        senddata[5] = 0x00;
                        senddata[6] = 0x05;
                        senddata[7] = 0x3A;
                        senddata[8] = 0x3B;
                        mHoldBluetoothSound.sendData(moduleSound,senddata);
                    }
                    handler.removeCallbacks (ReceiveRunnable);
                }
                break;
                case R.id.scanSoundBtn: {
                    Intent intent = new Intent(SoundActivity.this, SoundSaveActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.printSoundBtn: {
                    printSoundBtn.setEnabled(false);
                    printSoundBtnpressed = getResources().getDrawable(R.drawable.print);
                    printSoundBtnpressed.setBounds(0, 0, printSoundBtnpressed.getMinimumWidth(), printSoundBtnpressed.getMinimumHeight());
                    printSoundBtn.setCompoundDrawables(null, printSoundBtnpressed, null, null);
                    if (Finish) {
                        if (printDataService == null) {           //首次连接打印机
                            SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                            if (!shares.getBoolean("BondPrinter", false)) {
                                Toast.makeText(SoundActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SoundActivity.this.getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                        } else {          //打印数据
                            PrintMeasureData();
                        }
                    } else {
                        Toast.makeText(SoundActivity.this, "没有可以打印的数据", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                case R.id.saveSoundBtn: {
                    saveSoundBtn.setEnabled(false);
                    saveSoundBtnpressed = getResources().getDrawable(R.drawable.save);
                    saveSoundBtnpressed.setBounds(0, 0, saveSoundBtnpressed.getMinimumWidth(), saveSoundBtnpressed.getMinimumHeight());
                    saveSoundBtn.setCompoundDrawables(null, saveSoundBtnpressed, null, null);
                    SoundAdd(str_SoundMax);
                }
                break;
                case R.id.exportSoundBtn: {
                    exportSoundBtn.setEnabled(false);
                    exportSoundBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportSoundBtnpressed.setBounds(0, 0, exportSoundBtnpressed.getMinimumWidth(), exportSoundBtnpressed.getMinimumHeight());
                    exportSoundBtn.setCompoundDrawables(null, exportSoundBtnpressed, null, null);
                    CreatePdf();
                    Toast.makeText(SoundActivity.this, "数据已导出到手机根目录/Documents/叉车噪声检测报告", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.backSoundBtn: {
                    Intent intent1 = new Intent(SoundActivity.this, ChacheActivity.class);
                    startActivity(intent1);
                    finish();
                }
                break;
                case R.id.soundHelpBtn: {
                    showControlDialog();
                }
                break;
                case R.id.exitSoundBtn: {
                    finish();
                    ActivityCollector.finishAll();
                }
                break;
                default: {
                }
                break;
            }
            handler.postDelayed(sendRunnable, 1000);
        }
    }
    private void showControlDialog(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(SoundActivity.this);
        normalDialog.setIcon(R.drawable.help1);
        normalDialog.setTitle("GB/T 16178-2011 场(厂)内机动车辆安全检验技术要求");
        normalDialog.setMessage("1、车辆设置的喇叭，其声级应在90dB(A)-115dB(A)范围内。");
        normalDialog.setPositiveButton("确  定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });

        normalDialog.show();
    }
    Runnable ReceiveRunnable = new Runnable() {
        @Override
        public void run() {
            int SoundRevNum=0;
            byte[] mReceiveSound=new byte[17];
            String[] str_ReceiveData=new String[17];
            //soundReceive=controlClientSound.receive;
         if(soundReceive.length>=17)
         {
            for(int i=0;i<soundReceive.length;i++)
            {
                if(soundReceive[SoundRevNum]==0x4B)
                {
                    break;
                }else{
                    SoundRevNum++;
                }
            }
            for(int i=0;i<17;i++)
            {
                if((SoundRevNum+i)<soundReceive.length)
                {
                    mReceiveSound[i] =soundReceive[SoundRevNum+i];
                }else{
                    mReceiveSound[i] =soundReceive[SoundRevNum+i-soundReceive.length];
                }
            }
            for(int i=0;i<17;i++)
            {
                str_ReceiveData[i]=myformat.format(mReceiveSound[i]);
            }
            Log.d("soundActivity","     "+str_ReceiveData[0]+"  "+str_ReceiveData[1]+" "+str_ReceiveData[2]+"  "+str_ReceiveData[3]+" "+str_ReceiveData[4]+"  "+str_ReceiveData[5]+" "+
                    str_ReceiveData[6]+"  "+str_ReceiveData[7]+" "+str_ReceiveData[8]+"  "+str_ReceiveData[9]+" "+str_ReceiveData[10]+"  "+str_ReceiveData[11]+" "+
                    str_ReceiveData[12]+"  "+str_ReceiveData[13]+" "+str_ReceiveData[14]+str_ReceiveData[15]+str_ReceiveData[16]+"  ");
            if((mReceiveSound[0]==0x4B) && (mReceiveSound[1]==0x53)) {
                switch (mReceiveSound[2]) {
                    case 0x07:
                        int tmp = 0;
                        tmp = (char) (mReceiveSound[4] & 0xFF) * 256 + (char) (mReceiveSound[5] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) realSound= (float) tmp/ 100.0f;
                        else realSound = (float) (0x10000 - tmp) / 100.0f * (-1.0f);
                        realSound_Data.add(realSound);
                        if(Math.abs(SoundMax)<Math.abs(realSound))
                        {
                            SoundMax=realSound;
                        }
                            str_realSound = myformat.format(realSound);
                            realSound_txt.setText(str_realSound);
                            str_SoundMax = myformat.format(SoundMax);
                            SoundMax_txt.setText(str_SoundMax);
                            if((Math.abs(SoundMax)>=90.0f)&&(Math.abs(SoundMax)<=115.0f))
                            {
                                SoundMax_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                            }
                        if(ChartYMax<Math.abs(realSound))
                        {
                            ChartYMax =Math.abs(realSound);
                        }
                        ChartXMax =ChartXMax+0.03f;
                        if(ChartXMax>10000)
                        {
                            ChartXMax=0;
                        }
                        dynamicLineChartManager_Sound.setYAxis(ChartYMax*1.2f, 0, 5);
                        dynamicLineChartManager_Sound.setXAxis(ChartXMax*1.2f, 0, 10,0);
                        dynamicLineChartManager_Sound.addEntry(realSound);
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
            startSoundBtn.setEnabled(true);
            startSoundBtnpressed = getResources().getDrawable(R.drawable.start1);
            startSoundBtnpressed.setBounds(0, 0, startSoundBtnpressed.getMinimumWidth(), startSoundBtnpressed.getMinimumHeight());
            startSoundBtn.setCompoundDrawables(null, startSoundBtnpressed, null, null);

            stopSoundBtn.setEnabled(true);
            stopSoundBtnpressed = getResources().getDrawable(R.drawable.stop1);
            stopSoundBtnpressed.setBounds(0, 0, stopSoundBtnpressed.getMinimumWidth(), stopSoundBtnpressed.getMinimumHeight());
            stopSoundBtn.setCompoundDrawables(null, stopSoundBtnpressed, null, null);

            scanSoundBtn.setEnabled(true);
            scanSoundBtnpressed = getResources().getDrawable(R.drawable.scan1);
            scanSoundBtnpressed.setBounds(0, 0, scanSoundBtnpressed.getMinimumWidth(), scanSoundBtnpressed.getMinimumHeight());
            scanSoundBtn.setCompoundDrawables(null, scanSoundBtnpressed, null, null);

            printSoundBtn.setEnabled(true);
            printSoundBtnpressed = getResources().getDrawable(R.drawable.print1);
            printSoundBtnpressed.setBounds(0, 0, printSoundBtnpressed.getMinimumWidth(), printSoundBtnpressed.getMinimumHeight());
            printSoundBtn.setCompoundDrawables(null, printSoundBtnpressed, null, null);

            saveSoundBtn.setEnabled(true);
            saveSoundBtnpressed = getResources().getDrawable(R.drawable.save1);
            saveSoundBtnpressed.setBounds(0, 0, saveSoundBtnpressed.getMinimumWidth(), saveSoundBtnpressed.getMinimumHeight());
            saveSoundBtn.setCompoundDrawables(null, saveSoundBtnpressed, null, null);

            exportSoundBtn.setEnabled(true);
            exportSoundBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportSoundBtnpressed.setBounds(0, 0, exportSoundBtnpressed.getMinimumWidth(), exportSoundBtnpressed.getMinimumHeight());
            exportSoundBtn.setCompoundDrawables(null, exportSoundBtnpressed, null, null);

            zeroSoundBtn.setEnabled(true);
            zeroSoundBtnpressed = getResources().getDrawable(R.drawable.zero1);
            zeroSoundBtnpressed.setBounds(0, 0, zeroSoundBtnpressed.getMinimumWidth(), zeroSoundBtnpressed.getMinimumHeight());
            zeroSoundBtn.setCompoundDrawables(null, zeroSoundBtnpressed, null, null);
        }
    };
    private void setEnableButton()
    {
        startSoundBtn.setEnabled(true);
        startSoundBtnpressed = getResources().getDrawable(R.drawable.start1);
        startSoundBtnpressed.setBounds(0, 0, startSoundBtnpressed.getMinimumWidth(), startSoundBtnpressed.getMinimumHeight());
        startSoundBtn.setCompoundDrawables(null, startSoundBtnpressed, null, null);

        stopSoundBtn.setEnabled(true);
        stopSoundBtnpressed = getResources().getDrawable(R.drawable.stop1);
        stopSoundBtnpressed.setBounds(0, 0, stopSoundBtnpressed.getMinimumWidth(), stopSoundBtnpressed.getMinimumHeight());
        stopSoundBtn.setCompoundDrawables(null, stopSoundBtnpressed, null, null);

        scanSoundBtn.setEnabled(true);
        scanSoundBtnpressed = getResources().getDrawable(R.drawable.scan1);
        scanSoundBtnpressed.setBounds(0, 0, scanSoundBtnpressed.getMinimumWidth(), scanSoundBtnpressed.getMinimumHeight());
        scanSoundBtn.setCompoundDrawables(null, scanSoundBtnpressed, null, null);

        printSoundBtn.setEnabled(true);
        printSoundBtnpressed = getResources().getDrawable(R.drawable.print1);
        printSoundBtnpressed.setBounds(0, 0, printSoundBtnpressed.getMinimumWidth(), printSoundBtnpressed.getMinimumHeight());
        printSoundBtn.setCompoundDrawables(null, printSoundBtnpressed, null, null);

        saveSoundBtn.setEnabled(true);
        saveSoundBtnpressed = getResources().getDrawable(R.drawable.save1);
        saveSoundBtnpressed.setBounds(0, 0, saveSoundBtnpressed.getMinimumWidth(), saveSoundBtnpressed.getMinimumHeight());
        saveSoundBtn.setCompoundDrawables(null, saveSoundBtnpressed, null, null);

        exportSoundBtn.setEnabled(true);
        exportSoundBtnpressed = getResources().getDrawable(R.drawable.export1);
        exportSoundBtnpressed.setBounds(0, 0, exportSoundBtnpressed.getMinimumWidth(), exportSoundBtnpressed.getMinimumHeight());
        exportSoundBtn.setCompoundDrawables(null, exportSoundBtnpressed, null, null);

        zeroSoundBtn.setEnabled(true);
        zeroSoundBtnpressed = getResources().getDrawable(R.drawable.zero1);
        zeroSoundBtnpressed.setBounds(0, 0, zeroSoundBtnpressed.getMinimumWidth(), zeroSoundBtnpressed.getMinimumHeight());
        zeroSoundBtn.setCompoundDrawables(null, zeroSoundBtnpressed, null, null);
    }

    private void setButton()
    {
        startSoundBtn.setEnabled(false);
        startSoundBtnpressed = getResources().getDrawable(R.drawable.start);
        startSoundBtnpressed.setBounds(0, 0, startSoundBtnpressed.getMinimumWidth(), startSoundBtnpressed.getMinimumHeight());
        startSoundBtn.setCompoundDrawables(null, startSoundBtnpressed, null, null);

        stopSoundBtn.setEnabled(false);
        stopSoundBtnpressed = getResources().getDrawable(R.drawable.stop);
        stopSoundBtnpressed.setBounds(0, 0, stopSoundBtnpressed.getMinimumWidth(), stopSoundBtnpressed.getMinimumHeight());
        stopSoundBtn.setCompoundDrawables(null, stopSoundBtnpressed, null, null);

        printSoundBtn.setEnabled(false);
        printSoundBtnpressed = getResources().getDrawable(R.drawable.print);
        printSoundBtnpressed.setBounds(0, 0, printSoundBtnpressed.getMinimumWidth(), printSoundBtnpressed.getMinimumHeight());
        printSoundBtn.setCompoundDrawables(null, printSoundBtnpressed, null, null);

        saveSoundBtn.setEnabled(false);
        saveSoundBtnpressed = getResources().getDrawable(R.drawable.save);
        saveSoundBtnpressed.setBounds(0, 0, saveSoundBtnpressed.getMinimumWidth(), saveSoundBtnpressed.getMinimumHeight());
        saveSoundBtn.setCompoundDrawables(null, saveSoundBtnpressed, null, null);

        exportSoundBtn.setEnabled(false);
        exportSoundBtnpressed = getResources().getDrawable(R.drawable.export);
        exportSoundBtnpressed.setBounds(0, 0, exportSoundBtnpressed.getMinimumWidth(), exportSoundBtnpressed.getMinimumHeight());
        exportSoundBtn.setCompoundDrawables(null, exportSoundBtnpressed, null, null);

        zeroSoundBtn.setEnabled(false);
        zeroSoundBtnpressed = getResources().getDrawable(R.drawable.zero);
        zeroSoundBtnpressed.setBounds(0, 0, zeroSoundBtnpressed.getMinimumWidth(), zeroSoundBtnpressed.getMinimumHeight());
        zeroSoundBtn.setCompoundDrawables(null, zeroSoundBtnpressed, null, null);
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
                    printDataService = new PrintDataService(SoundActivity.this,shares.getString("Printer",""));
                    //Toast.makeText(overActivity.this,"蓝牙打印机连接中...",Toast.LENGTH_LONG).show();
                }
                if(printDataService != null){
                    PrintConnect = printDataService.connect();
                    if(PrintConnect){
                        Toast.makeText(SoundActivity.this,"蓝牙打印机连接成功...",Toast.LENGTH_LONG).show();
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
        printDataService.send("叉车噪声检测结果");
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
        printDataService.send("最大噪声值"+": "+ str_SoundMax+"dB"+"\n");//
        printDataService.send("*******************************\n\n\n\n");
        Toast.makeText(SoundActivity.this,"打印完成！",Toast.LENGTH_SHORT).show();
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
        SoundActivity.this.sendBroadcast(intent);

    }
    //创建PDF文件-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(SoundActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //创建目录
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车噪声检测报告"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车噪声检测报告"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车噪声检测报告" + File.separator + curDate.toString ()+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    pdfcontext = new Paragraph("叉车噪声检测报告",setChineseTitleFont());
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

                    cell.setPhrase(new Phrase("最大噪声值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_SoundMax,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("dB",setChineseFont()))    ;mtable.addCell(cell);

                    doc.add(mtable);
                    doc.close();
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车噪声检测报告" +  File.separator + curDate.toString () +".pdf");
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
        private int position;

        private LineData lineData;
        private LineDataSet lineDataSet;
        private LineChartMarkView mv;
        private Legend legend;
        private void setData(ArrayList<Float> value,float index) {
            ArrayList<Entry> values = new ArrayList<>();
            for (int i = 0; i < value.size()- 1; i++) {
                values.add(new Entry((float)(i*index), (float) value.get(i)));
            }
            lineDataSet.setValues(values);
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
            chartSound.setVisibility(View.VISIBLE);
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
            chartSound.setVisibility (View.VISIBLE);
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
            lineDataSet = new LineDataSet(null, "实时噪声值(dB)");
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

            lineData = new LineData(lineDataSet);
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
            lineChart.invalidate();
        }

        private void addEntry(float force){
            Entry entry = new Entry(lineDataSet.getEntryCount()*0.03f,force);
            lineData.addEntry(entry, 0);
            chartSound.notifyDataSetChanged();
            chartSound.moveViewToX(0.00f);
            lineChart.invalidate();
        }
        private void freshChart(float force) {
            lineChart.fitScreen();
        }
        private void clear() {
            lineDataSet.clear();
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
            LegFre.setTextColor(Color.RED);
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

        /**
         * 设置高限制线
         *
         * @param high
         * @param name
         */
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
        dynamicLineChartManager_Sound.setData(realSound_Data,0.1f);
        dynamicLineChartManager_Sound.setYAxis(500, 0, 5);
        dynamicLineChartManager_Sound.setXAxis(120, 0, 10,0);
        dynamicLineChartManager_Sound.setHightLimitLine(0f, "");
        dynamicLineChartManager_Sound.desChart(names.get (0),165);
    }
    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        super.onDestroy();
        if(deviceSound != null)
        {
            mHoldBluetoothSound.disconnect(moduleSound);
        }
    }
}
