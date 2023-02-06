package com.ruiguan.chache.DAngle;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class DAngleActivity extends DAngleSaveActivity {
    private BluetoothDevice deviceDAngle= null;
    private DeviceModule moduleDAngle= null;
    private HoldBluetooth mHoldBluetoothDAngle= null;
    private BluetoothAdapter bluetoothAdapter = null;
    private List<DeviceModule> modules;
    private final String CONNECTED = "已连接",CONNECTING = "连接中",DISCONNECT = "断线了";

    private String str_company;
    private String str_number;
    private String str_chacheNumber;
    private String str_chacheType;
    private String str_chacheGroup;

    private Button startDBtn;
    private Button finalDBtn;
    private Button stopDBtn;
    private Button scanDBtn;
    private Button printDBtn;
    private Button saveDBtn;
    private Button exportDBtn;
    private Button backDBtn;
    private Button addDBtn;
    private Button deviceDAngleBtn;
    private Button dangleHelpBtn;

    private Drawable startDBtnpressed;
    private Drawable finalDBtnpressed;
    private Drawable stopDBtnpressed;
    private Drawable scanDBtnpressed;
    private Drawable printDBtnpressed;
    private Drawable saveDBtnpressed;
    private Drawable exportDBtnpressed;
    private Drawable deviceDAngleBtnpressed;
    private Drawable addDBtnpressed;

    private TextView StartDis_txt;
    private TextView FinalDis_txt;
    private TextView Dis_txt;
    private TextView Time_txt;
    private TextView StartAngle_txt;
    private TextView FinalAngle_txt;
    private TextView DAngle_txt;
    private TextView statusDAngle_txt;
    private TextView ratedLoad_txt;

    private float DisAvg;
    private float DAngleAvg;

    private String  str_DisAvg;
    private String  str_DAngleAvg;

    private TextView DisAvg_txt;
    private TextView DAngleAvg_txt;

    private BaseAdapter mAdapter;
    private ListView danglelistView;
    private List<String> dataDangle = new ArrayList<String>();

    private float ChartYMax;
    private float ChartXMax;

    private byte[] senddata;
    private byte[] dangleReceive;

    private float realDis;
    private float realAngle;
    private float startDis;
    private float startAngle;
    private float finalDis;
    private float finalAngle;
    private float DAnglelDis;
    private float DAngleAngle;
    private float DAngleTime;
    private float ratedLoad;

    private Integer MeasureNum;
    private String  str_startDis;
    private String  str_startAngle;
    private String  str_finalDis;
    private String  str_finalAngle;
    private String  str_DAnglelDis;
    private String  str_DAngleAngle;
    private String  str_DAngleTime;

    private Handler handler = new Handler();
    private LineChart chartDAngle;

    private ArrayList<Float> disMax_Data = new ArrayList<>();
    private ArrayList<Float> angleMax_Data = new ArrayList<>();
    private ArrayList<Float> realForce_Data = new ArrayList<>();
    private ArrayList<Float> realAngle_Data = new ArrayList<>();
    private DAngleActivity.DynamicLineChartManager dynamicLineChartManager_DAngle;
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合

    private boolean startFlag=false;
    private boolean finalFlag=false;
    private boolean Finish=false;
    private PrintDataService printDataService = null;
    private boolean PrintConnect = false;
    java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.000");
    java.text.DecimalFormat myformatNum=new java.text.DecimalFormat("0");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangle);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);
        senddata=new byte[9];
        dangleReceive=new byte[17];
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

        final HoldBluetooth.UpdateList updateList = new HoldBluetooth.UpdateList() {
            @Override
            public void update(boolean isStart,DeviceModule deviceModule) {

            }
            @Override
            public void updateMessyCode(boolean isStart, DeviceModule deviceModule) {
            }
        };

        mHoldBluetoothDAngle= new HoldBluetooth();
        mHoldBluetoothDAngle.initHoldBluetooth(DAngleActivity.this,updateList);
        initMembers();
        initDataDAngleListener();
        ShowWave();
        handler.postDelayed(BleRunnable,2000);
    }
    private void initMembers() {
        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_chacheNumber= chache_Input.getchacheNumber();
        str_chacheType= chache_Input.getchacheType();
        str_chacheGroup= chache_Input.getchacheGroup();

        StartDis_txt= (TextView) findViewById(R.id.StartDis_txt);
        FinalDis_txt= (TextView) findViewById(R.id.FinalDis_txt);
        Dis_txt= (TextView) findViewById(R.id.Dis_txt);
        Time_txt= (TextView) findViewById(R.id.Time_txt);
        StartAngle_txt= (TextView) findViewById(R.id.StartAngle_txt);
        FinalAngle_txt= (TextView) findViewById(R.id.FinalAngle_txt);
        DAngle_txt= (TextView) findViewById(R.id.DAngle_txt);
        statusDAngle_txt= (TextView) findViewById(R.id.statusDAngle_txt);
        ratedLoad_txt= (TextView) findViewById(R.id.ratedLoad_txt);

        DisAvg_txt= (TextView) findViewById(R.id.DisAvg_txt);
        DAngleAvg_txt= (TextView) findViewById(R.id.DAngleAvg_txt);

        startDBtn = findViewById(R.id.startDBtn);
        finalDBtn = findViewById(R.id.finalDBtn);
        stopDBtn = findViewById(R.id.stopDBtn);
        scanDBtn = findViewById(R.id.scanDBtn);
        printDBtn = findViewById(R.id.printDBtn);
        saveDBtn = findViewById(R.id.saveDBtn);
        exportDBtn = findViewById(R.id.exportDBtn);
        backDBtn = findViewById(R.id.backDBtn);
        addDBtn = findViewById(R.id.addDBtn);
        deviceDAngleBtn= findViewById(R.id.deviceDAngleBtn);
        dangleHelpBtn= findViewById(R.id.dangleHelpBtn);
        View.OnClickListener bl = new DAngleActivity.ButtonListener();
        setOnClickListener(startDBtn, bl);
        setOnClickListener(finalDBtn, bl);
        setOnClickListener(stopDBtn, bl);
        setOnClickListener(scanDBtn, bl);
        setOnClickListener(printDBtn, bl);
        setOnClickListener(saveDBtn, bl);
        setOnClickListener(exportDBtn, bl);
        setOnClickListener(backDBtn, bl);
        setOnClickListener(addDBtn, bl);
        setOnClickListener(dangleHelpBtn, bl);
        setButton();

        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return dataDangle.size();
            }
            @Override
            public Object getItem(int position) {
                return dataDangle.get(position);
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

        danglelistView = (ListView)findViewById(R.id.dangleList);
        danglelistView.setAdapter(mAdapter);
        danglelistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
               // str_forceMax=dataDangle.get(position);
                Toast.makeText(DAngleActivity.this, "已选中要保存的数据", Toast.LENGTH_SHORT).show();
            }
        });

        chartDAngle= findViewById(R.id.chartDAngle);
        names.add ("");
        colour.add (Color.argb (255, 255, 125, 0));            //定义Fre颜色
        dynamicLineChartManager_DAngle = new DAngleActivity.DynamicLineChartManager(chartDAngle, names.get (0), colour.get (0), 0);
    }
    @SuppressLint("SetTextI18n")
    private View getListView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView =getLayoutInflater().inflate(R.layout.force_list, null);//加载布局
        }
        TextView realValue_txt = (TextView) convertView.findViewById(R.id.realValue);
        TextView maxValue_txt = (TextView) convertView.findViewById(R.id.maxValue);

        realValue_txt.setText(Integer.toString(position+1));
        maxValue_txt .setText(dataDangle.get(position));
        return convertView;
    }
    //初始化蓝牙数据的监听
    private void initDataDAngleListener() {
        HoldBluetooth.OnReadDataListener dataListener = new HoldBluetooth.OnReadDataListener() {
            @Override
            public void readData(String mac, byte[] data) {
                if (deviceDAngle.getAddress().equals(mac)){
                    dangleReceive=data;
                }
            }
            @Override
            public void reading(boolean isStart) {

            }
            @Override
            public void connectSucceed() {
                modules = mHoldBluetoothDAngle.getConnectedArray();
                for(int i=0;i<modules.size();i++)
                {
                    if(modules.get(i).getMac().equals(deviceDAngle.getAddress()))
                    {
                        setDAngleState(CONNECTED);//设置连接状态
                        Log.d("DAngleActivity","DAngle蓝牙连接成功！");
                    }
                }
            }
            @Override
            public void errorDisconnect(final DeviceModule deviceModule) {//蓝牙异常断开

                if(deviceModule.getMac().equals(deviceDAngle.getAddress()))
                {
                    setDAngleState(DISCONNECT);//设置断开状态
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
        mHoldBluetoothDAngle.setOnReadListener(dataListener);
    }

    private void setDAngleState(String state){
        switch (state){
            case CONNECTED://连接成功
                statusDAngle_txt.setText("已连接");
                deviceDAngleBtnpressed = getResources().getDrawable(R.drawable.btle_connected);
                deviceDAngleBtnpressed.setBounds(0, 0,  deviceDAngleBtnpressed.getMinimumWidth(),  deviceDAngleBtnpressed.getMinimumHeight());
                deviceDAngleBtn.setCompoundDrawables(null,  deviceDAngleBtnpressed, null, null);
                setEnableButton();
                break;

            case CONNECTING://连接中
                statusDAngle_txt.setText("连接中");
                deviceDAngleBtnpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                deviceDAngleBtnpressed.setBounds(0, 0,  deviceDAngleBtnpressed.getMinimumWidth(),  deviceDAngleBtnpressed.getMinimumHeight());
                deviceDAngleBtn.setCompoundDrawables(null,  deviceDAngleBtnpressed, null, null);
                break;

            case DISCONNECT://连接断开
                statusDAngle_txt.setText("断开");
                deviceDAngleBtnpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                deviceDAngleBtnpressed.setBounds(0, 0,  deviceDAngleBtnpressed.getMinimumWidth(),  deviceDAngleBtnpressed.getMinimumHeight());
                deviceDAngleBtn.setCompoundDrawables(null,  deviceDAngleBtnpressed, null, null);
                break;
        }
    }

    Runnable BleRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences shares2 = getSharedPreferences( "DAngle_Decive", Activity.MODE_PRIVATE );
            if(!shares2.getBoolean("BondDecive",false))
            {
                Intent intent = new Intent(DAngleActivity.this, MainActivity.class);
                startActivity(intent);
            }else
            {
                deviceDAngle= bluetoothAdapter.getRemoteDevice(shares2.getString("DAngle",""));
                if(deviceDAngle == null)
                {
                    Toast.makeText(DAngleActivity.this,"未绑定下滑量蓝牙！",Toast.LENGTH_LONG).show();
                }else{
                    DeviceModule deviceModuleDAngle = new DeviceModule(deviceDAngle.getName(),deviceDAngle);
                    moduleDAngle= deviceModuleDAngle;
                    mHoldBluetoothDAngle.connect(moduleDAngle);
                    //controlClientBrake = SocketThread.getClient(deviceBrake);
                    Log.d("mHoldBluetoothDAngle","开始连接蓝牙");
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
                case R.id.startDBtn: {
                    startDBtn.setEnabled(false);
                    startDBtnpressed = getResources().getDrawable(R.drawable.start);
                    startDBtnpressed.setBounds(0, 0, startDBtnpressed.getMinimumWidth(), startDBtnpressed.getMinimumHeight());
                    startDBtn.setCompoundDrawables(null, startDBtnpressed, null, null);
                    for(int i=0;i<3;i++)
                    {
                        senddata[0]=0x4B;
                        senddata[1]=0x53;
                        senddata[2]=0x06;
                        senddata[3]=0x00;
                        senddata[4]=0x01;
                        senddata[5]=0x00;
                        senddata[6]=0x01;
                        senddata[7]=0x3A;
                        senddata[8]=0x3B;
                        mHoldBluetoothDAngle.sendData(moduleDAngle,senddata);
                    }

                    ChartYMax = 0.0f;
                    ChartXMax = 0.0f;
                    realForce_Data.clear();
                    realAngle_Data.clear();
                    realDis= 0.0f;
                    realAngle= 0.0f;
                    startDis= 0.0f;
                    startAngle= 0.0f;
                    finalDis= 0.0f;
                    finalAngle= 0.0f;
                    DAnglelDis= 0.0f;
                    DAngleAngle= 0.0f;
                    DAngleTime= 0.0f;
                    MeasureNum=0;
                    ratedLoad= Float.valueOf(ratedLoad_txt.getText().toString());
                    Dis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                    DAngle_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                    str_startDis= myformat.format(startDis);
                    str_startAngle= myformat.format(startAngle);
                    str_finalDis= myformat.format(finalDis);
                    str_finalAngle= myformat.format(finalAngle);
                    str_DAnglelDis= myformat.format(DAnglelDis);
                    str_DAngleAngle= myformat.format(DAngleAngle);
                    str_DAngleTime= myformat.format(DAngleTime);

                    StartDis_txt.setText(str_startDis);
                    StartAngle_txt.setText(str_startAngle);
                    FinalDis_txt.setText(str_finalDis);
                    FinalAngle_txt.setText(str_finalAngle);
                    Dis_txt.setText(str_DAnglelDis);
                    DAngle_txt.setText(str_DAngleAngle);
                    Time_txt.setText(str_DAngleTime);
                    startFlag=true;
                    finalFlag=false;
                    dynamicLineChartManager_DAngle.clear();
                    handler.postDelayed(ReceiveRunnable,10);
                }
                break;
                case R.id.finalDBtn: {
                    finalDBtn.setEnabled(false);
                    finalDBtnpressed = getResources().getDrawable(R.drawable.start);
                    finalDBtnpressed.setBounds(0, 0, finalDBtnpressed.getMinimumWidth(), finalDBtnpressed.getMinimumHeight());
                    finalDBtn.setCompoundDrawables(null, finalDBtnpressed, null, null);
                    for(int i=0;i<3;i++)
                    {
                        senddata[0]=0x4B;
                        senddata[1]=0x53;
                        senddata[2]=0x06;
                        senddata[3]=0x00;
                        senddata[4]=0x01;
                        senddata[5]=0x00;
                        senddata[6]=0x01;
                        senddata[7]=0x3A;
                        senddata[8]=0x3B;
                        mHoldBluetoothDAngle.sendData(moduleDAngle,senddata);
                    }

                   // ChartYMax = 0.0f;
                   // ChartXMax = 0.0f;
                    realForce_Data.clear();
                    realAngle_Data.clear();
                    realDis= 0.0f;
                    realAngle= 0.0f;
                    finalDis= 0.0f;
                    finalAngle= 0.0f;
                    DAnglelDis= 0.0f;
                    DAngleAngle= 0.0f;
                    MeasureNum=0;
                    ratedLoad= Float.valueOf(ratedLoad_txt.getText().toString());
                    str_finalDis= myformat.format(finalDis);
                    str_finalAngle= myformat.format(finalAngle);
                    str_DAnglelDis= myformat.format(DAnglelDis);
                    str_DAngleAngle= myformat.format(DAngleAngle);

                    FinalDis_txt.setText(str_finalDis);
                    FinalAngle_txt.setText(str_finalAngle);
                    Dis_txt.setText(str_DAnglelDis);
                    DAngle_txt.setText(str_DAngleAngle);
                    startFlag=false;
                    finalFlag=true;
                    dynamicLineChartManager_DAngle.clear();
                    handler.postDelayed(ReceiveRunnable,10);
                }
                break;
                case R.id.stopDBtn: {
                    Finish=true;
                    stopDBtn.setEnabled(false);
                    stopDBtnpressed = getResources().getDrawable(R.drawable.stop);
                    stopDBtnpressed.setBounds(0, 0, stopDBtnpressed.getMinimumWidth(), stopDBtnpressed.getMinimumHeight());
                    stopDBtn.setCompoundDrawables(null, stopDBtnpressed, null, null);
                    for(int i=0;i<3;i++)
                    {
                        senddata[0]=0x4B;
                        senddata[1]=0x53;
                        senddata[2]=0x06;
                        senddata[3]=0x00;
                        senddata[4]=0x01;
                        senddata[5]=0x00;
                        senddata[6]=0x05;
                        senddata[7]=0x3A;
                        senddata[8]=0x3B;
                        mHoldBluetoothDAngle.sendData(moduleDAngle,senddata);
                    }

                    MeasureNum=0;
                    handler.removeCallbacks (ReceiveRunnable);
                }
                break;
                case R.id.scanDBtn: {
                    Intent intent = new Intent(DAngleActivity.this, DAngleSaveActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.printDBtn: {
                    printDBtn.setEnabled(false);
                    printDBtnpressed = getResources().getDrawable(R.drawable.print);
                    printDBtnpressed.setBounds(0, 0, printDBtnpressed.getMinimumWidth(), printDBtnpressed.getMinimumHeight());
                    printDBtn.setCompoundDrawables(null, printDBtnpressed, null, null);
                    if (Finish) {
                        if (printDataService == null) {           //首次连接打印机
                            SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                            if (!shares.getBoolean("BondPrinter", false)) {
                                Toast.makeText(DAngleActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DAngleActivity.this.getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                        } else {          //打印数据
                            PrintMeasureData();
                        }
                    } else {
                        Toast.makeText(DAngleActivity.this, "没有可以打印的数据", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
                case R.id.saveDBtn: {
                    saveDBtn.setEnabled(false);
                    saveDBtnpressed = getResources().getDrawable(R.drawable.save);
                    saveDBtnpressed.setBounds(0, 0, saveDBtnpressed.getMinimumWidth(), saveDBtnpressed.getMinimumHeight());
                    saveDBtn.setCompoundDrawables(null, saveDBtnpressed, null, null);
                    DAngleAdd(str_DisAvg,str_DAngleAvg);
                }
                break;
                case R.id.addDBtn: {
                    addDBtn.setEnabled(false);
                    addDBtnpressed = getResources().getDrawable(R.drawable.add);
                    addDBtnpressed.setBounds(0, 0,addDBtnpressed.getMinimumWidth(), addDBtnpressed.getMinimumHeight());
                    addDBtn.setCompoundDrawables(null, addDBtnpressed, null, null);
                    dataDangle.add("下滑量："+str_DAnglelDis+"m"+"/"+"倾角值:"+str_DAngleAngle+"°");

                    disMax_Data.add(DAnglelDis);
                    angleMax_Data.add(DAngleAngle);

                    mAdapter.notifyDataSetChanged();
                    danglelistView.invalidateViews();
                    handler.postDelayed(ResultRunnable,10);
                }
                break;
                case R.id.exportDBtn: {
                    exportDBtn.setEnabled(false);
                    exportDBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportDBtnpressed.setBounds(0, 0, exportDBtnpressed.getMinimumWidth(), exportDBtnpressed.getMinimumHeight());
                    exportDBtn.setCompoundDrawables(null, exportDBtnpressed, null, null);
                    CreatePdf();
                    Toast.makeText(DAngleActivity.this, "数据已导出到手机根目录/Documents/叉车下滑量及门架倾角检测报告", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.backDBtn: {
                    Intent intent1 = new Intent(DAngleActivity.this, ChacheActivity.class);
                    startActivity(intent1);
                    finish();
                }
                break;
                case R.id.dangleHelpBtn: {
                    showControlDialog();
                }
                break;
                default: {
                }
                break;
            }
            handler.postDelayed(sendRunnable, 1000);
        }
    }
    Runnable ResultRunnable = new Runnable() {
        @Override
        public void run() {
            float tempErrForce = 0.0f;
            DisAvg = 0.0f;
            for (int i = 0; i < disMax_Data.size(); i++) {
                DisAvg = DisAvg + Float.valueOf(disMax_Data.get(i));
            }
            DisAvg = DisAvg / disMax_Data.size();
            str_DisAvg = myformat.format(DisAvg);
            DisAvg_txt.setText(str_DisAvg);

            DAngleAvg= 0.0f;
            for (int i = 0; i < angleMax_Data.size(); i++) {
                DAngleAvg = DAngleAvg + Float.valueOf(angleMax_Data.get(i));
            }
            DAngleAvg = DAngleAvg / angleMax_Data.size();
            str_DAngleAvg = myformat.format(DAngleAvg);
            DAngleAvg_txt.setText(str_DAngleAvg);
        }
     };

    private void showControlDialog(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(DAngleActivity.this);
        normalDialog.setIcon(R.drawable.help1);
        normalDialog.setTitle("GB/T 16178-2011 场(厂)内机动车辆安全检验技术要求");
        normalDialog.setMessage("货叉自然下滑量和门架倾角的自然变化量：" +
                "\n1、对额定起重量为0.5t-10t的二级门架叉车，货叉自然下滑量前10min不应大于100mm，门架或货叉倾角的自然变化量前10min不应大于5°；"+
                "\n2、对大于10t-45t的二级门架叉车，货叉自然下滑量前10min不应大于200mm,门架或货叉倾角的自然变化量前10min不应大于5°。");
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
            int DAngleRevNum=0;
            byte[] mReceiveDAngle=new byte[17];
            String[] str_ReceiveData=new String[17];
           // dangleReceive=controlClientDAngle.receive;
       if(dangleReceive.length>=17)
       {
            for(int i=0;i<dangleReceive.length;i++)
            {
                if(dangleReceive[DAngleRevNum]==0x4B)
                {
                    break;
                }else{
                    DAngleRevNum++;
                }
            }
            for(int i=0;i<17;i++)
            {
                if((DAngleRevNum+i)<dangleReceive.length)
                {
                    mReceiveDAngle[i] =dangleReceive[DAngleRevNum+i];
                }else{
                    mReceiveDAngle[i] =dangleReceive[DAngleRevNum+i-dangleReceive.length];
                }
            }

            for(int i=0;i<17;i++)
            {
                str_ReceiveData[i]=myformat.format( mReceiveDAngle[i]);
            }
            Log.d("DAngleActivity","     "+str_ReceiveData[0]+"  "+str_ReceiveData[1]+" "+str_ReceiveData[2]+"  "+str_ReceiveData[3]+" "+str_ReceiveData[4]+"  "+str_ReceiveData[5]+" "+
                    str_ReceiveData[6]+"  "+str_ReceiveData[7]+" "+str_ReceiveData[8]+"  "+str_ReceiveData[9]+" "+str_ReceiveData[10]+"  "+str_ReceiveData[11]+" "+
                    str_ReceiveData[12]+"  "+str_ReceiveData[13]+" "+str_ReceiveData[14]+str_ReceiveData[15]+str_ReceiveData[16]+"  ");
            if((mReceiveDAngle[0]==0x4B) && (mReceiveDAngle[1]==0x53)) {
                switch (mReceiveDAngle[2]) {
                    case 0x06:
                        int tmp = 0;
                        tmp = (char) (mReceiveDAngle[4] & 0xFF) * 256 + (char) (mReceiveDAngle[5] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) realDis= (float) tmp/ 1000.0f;
                        else realDis = (float) (0x10000 - tmp) / 1000.0f * (-1.0f);

                        tmp = (char) (mReceiveDAngle[6] & 0xFF) * 256 + (char) (mReceiveDAngle[7] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) realAngle= (float) tmp/ 100.0f;
                        else realAngle = (float) (0x10000 - tmp) / 100.0f * (-1.0f);

                        realForce_Data.add(realDis);
                        realAngle_Data.add(realAngle);

                        if(MeasureNum<10)
                        {
                            if(startFlag)
                            {
                                MeasureNum++;
                                startDis=realDis;
                                str_startDis=myformat.format(startDis);
                                StartDis_txt.setText(str_startDis);
                                startAngle=realAngle;
                                str_startAngle=myformat.format(startAngle);
                                StartAngle_txt.setText(str_startAngle);
                                DAnglelDis=0.0f;
                                finalDis=0.0f;
                                DAngleAngle=0.0f;
                                finalAngle=0.0f;

                            }else if(finalFlag)
                            {
                                MeasureNum++;
                                finalDis=realDis;
                                str_finalDis=myformat.format(finalDis);
                                FinalDis_txt.setText(str_finalDis);
                                finalAngle=realAngle;
                                str_finalAngle=myformat.format(finalAngle);
                                FinalAngle_txt.setText(str_finalAngle);

                                DAnglelDis=Math.abs(finalDis-startDis);
                                str_DAnglelDis=myformat.format(DAnglelDis);
                                Dis_txt.setText(str_DAnglelDis);
                                DAngleAngle=finalAngle-startAngle;
                                str_DAngleAngle=myformat.format(DAngleAngle);
                                DAngle_txt.setText(str_DAngleAngle);

                            }
                        }
                       if((ratedLoad>=0.5f)&&(ratedLoad<=10.0f))
                       {
                           if(DAnglelDis>0.1f)
                           {
                               Dis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                           }else{
                               Dis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                           }

                           if(Math.abs(DAngleAngle)>5.0f)
                           {
                               DAngle_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                           }else{

                               DAngle_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                           }


                       }else if((ratedLoad>10.0f)&&(ratedLoad<=45.0f))
                       {
                           if(DAnglelDis>0.2f)
                           {
                               Dis_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;

                           }else{
                               Dis_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;

                           }

                           if(Math.abs(DAngleAngle)>5.0f)
                           {
                               DAngle_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                           }else{

                               DAngle_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                           }
                       }

                        DAngleTime=DAngleTime+0.0172f;
                        str_DAngleTime=myformat.format(DAngleTime);
                        Time_txt.setText(str_DAngleTime);

                        if(ChartYMax<Math.abs(realDis))
                        {
                            ChartYMax =Math.abs(realDis);
                        }
                        if(ChartYMax<Math.abs(realAngle))
                        {
                            ChartYMax =Math.abs(realAngle);
                        }
                        ChartXMax =ChartXMax+0.0172f;
                        if(ChartXMax>10000)
                        {
                            ChartXMax=0;
                        }
                        dynamicLineChartManager_DAngle.setYAxis(ChartYMax*1.2f, 0, 5);
                        dynamicLineChartManager_DAngle.setXAxis(ChartXMax*1.2f, 0, 10,0);
                        dynamicLineChartManager_DAngle.addEntry(realDis,realAngle);
                        break;
                    default : break;
                }
            }
       }
            handler.postDelayed(ReceiveRunnable,10);
        }
    };

    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            startDBtn.setEnabled(true);
            startDBtnpressed = getResources().getDrawable(R.drawable.startpoint1);
            startDBtnpressed.setBounds(0, 0, startDBtnpressed.getMinimumWidth(), startDBtnpressed.getMinimumHeight());
            startDBtn.setCompoundDrawables(null, startDBtnpressed, null, null);

            finalDBtn.setEnabled(true);
            finalDBtnpressed = getResources().getDrawable(R.drawable.final1);
            finalDBtnpressed.setBounds(0, 0, finalDBtnpressed.getMinimumWidth(), finalDBtnpressed.getMinimumHeight());
            finalDBtn.setCompoundDrawables(null, finalDBtnpressed, null, null);

            stopDBtn.setEnabled(true);
            stopDBtnpressed = getResources().getDrawable(R.drawable.stop1);
            stopDBtnpressed.setBounds(0, 0, stopDBtnpressed.getMinimumWidth(), stopDBtnpressed.getMinimumHeight());
            stopDBtn.setCompoundDrawables(null, stopDBtnpressed, null, null);

            scanDBtn.setEnabled(true);
            scanDBtnpressed = getResources().getDrawable(R.drawable.scan1);
            scanDBtnpressed.setBounds(0, 0, scanDBtnpressed.getMinimumWidth(), scanDBtnpressed.getMinimumHeight());
            scanDBtn.setCompoundDrawables(null, scanDBtnpressed, null, null);

            printDBtn.setEnabled(true);
            printDBtnpressed = getResources().getDrawable(R.drawable.print1);
            printDBtnpressed.setBounds(0, 0, printDBtnpressed.getMinimumWidth(), printDBtnpressed.getMinimumHeight());
            printDBtn.setCompoundDrawables(null, printDBtnpressed, null, null);

            saveDBtn.setEnabled(true);
            saveDBtnpressed = getResources().getDrawable(R.drawable.save1);
            saveDBtnpressed.setBounds(0, 0, saveDBtnpressed.getMinimumWidth(), saveDBtnpressed.getMinimumHeight());
            saveDBtn.setCompoundDrawables(null, saveDBtnpressed, null, null);

            exportDBtn.setEnabled(true);
            exportDBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportDBtnpressed.setBounds(0, 0, exportDBtnpressed.getMinimumWidth(), exportDBtnpressed.getMinimumHeight());
            exportDBtn.setCompoundDrawables(null, exportDBtnpressed, null, null);

            addDBtn.setEnabled(true);
            addDBtnpressed = getResources().getDrawable(R.drawable.add1);
            addDBtnpressed.setBounds(0, 0,addDBtnpressed.getMinimumWidth(), addDBtnpressed.getMinimumHeight());
            addDBtn.setCompoundDrawables(null, addDBtnpressed, null, null);
        }
    };
    private void setEnableButton()
    {
        startDBtn.setEnabled(true);
        startDBtnpressed = getResources().getDrawable(R.drawable.startpoint1);
        startDBtnpressed.setBounds(0, 0, startDBtnpressed.getMinimumWidth(), startDBtnpressed.getMinimumHeight());
        startDBtn.setCompoundDrawables(null, startDBtnpressed, null, null);

        finalDBtn.setEnabled(true);
        finalDBtnpressed = getResources().getDrawable(R.drawable.final1);
        finalDBtnpressed.setBounds(0, 0, finalDBtnpressed.getMinimumWidth(), finalDBtnpressed.getMinimumHeight());
        finalDBtn.setCompoundDrawables(null, finalDBtnpressed, null, null);

        stopDBtn.setEnabled(true);
        stopDBtnpressed = getResources().getDrawable(R.drawable.stop1);
        stopDBtnpressed.setBounds(0, 0, stopDBtnpressed.getMinimumWidth(), stopDBtnpressed.getMinimumHeight());
        stopDBtn.setCompoundDrawables(null, stopDBtnpressed, null, null);

        scanDBtn.setEnabled(true);
        scanDBtnpressed = getResources().getDrawable(R.drawable.scan1);
        scanDBtnpressed.setBounds(0, 0, scanDBtnpressed.getMinimumWidth(), scanDBtnpressed.getMinimumHeight());
        scanDBtn.setCompoundDrawables(null, scanDBtnpressed, null, null);

        printDBtn.setEnabled(true);
        printDBtnpressed = getResources().getDrawable(R.drawable.print1);
        printDBtnpressed.setBounds(0, 0, printDBtnpressed.getMinimumWidth(), printDBtnpressed.getMinimumHeight());
        printDBtn.setCompoundDrawables(null, printDBtnpressed, null, null);

        saveDBtn.setEnabled(true);
        saveDBtnpressed = getResources().getDrawable(R.drawable.save1);
        saveDBtnpressed.setBounds(0, 0, saveDBtnpressed.getMinimumWidth(), saveDBtnpressed.getMinimumHeight());
        saveDBtn.setCompoundDrawables(null, saveDBtnpressed, null, null);

        exportDBtn.setEnabled(true);
        exportDBtnpressed = getResources().getDrawable(R.drawable.export1);
        exportDBtnpressed.setBounds(0, 0, exportDBtnpressed.getMinimumWidth(), exportDBtnpressed.getMinimumHeight());
        exportDBtn.setCompoundDrawables(null, exportDBtnpressed, null, null);
    }
    private void setButton()
    {
        startDBtn.setEnabled(false);
        startDBtnpressed = getResources().getDrawable(R.drawable.startpoint);
        startDBtnpressed.setBounds(0, 0, startDBtnpressed.getMinimumWidth(), startDBtnpressed.getMinimumHeight());
        startDBtn.setCompoundDrawables(null, startDBtnpressed, null, null);

        finalDBtn.setEnabled(false);
        finalDBtnpressed = getResources().getDrawable(R.drawable.finalpoint);
        finalDBtnpressed.setBounds(0, 0, finalDBtnpressed.getMinimumWidth(), finalDBtnpressed.getMinimumHeight());
        finalDBtn.setCompoundDrawables(null, finalDBtnpressed, null, null);

        stopDBtn.setEnabled(false);
        stopDBtnpressed = getResources().getDrawable(R.drawable.stop);
        stopDBtnpressed.setBounds(0, 0, stopDBtnpressed.getMinimumWidth(), stopDBtnpressed.getMinimumHeight());
        stopDBtn.setCompoundDrawables(null, stopDBtnpressed, null, null);

        printDBtn.setEnabled(false);
        printDBtnpressed = getResources().getDrawable(R.drawable.print);
        printDBtnpressed.setBounds(0, 0, printDBtnpressed.getMinimumWidth(), printDBtnpressed.getMinimumHeight());
        printDBtn.setCompoundDrawables(null, printDBtnpressed, null, null);

        saveDBtn.setEnabled(false);
        saveDBtnpressed = getResources().getDrawable(R.drawable.save);
        saveDBtnpressed.setBounds(0, 0, saveDBtnpressed.getMinimumWidth(), saveDBtnpressed.getMinimumHeight());
        saveDBtn.setCompoundDrawables(null, saveDBtnpressed, null, null);

        exportDBtn.setEnabled(false);
        exportDBtnpressed = getResources().getDrawable(R.drawable.export);
        exportDBtnpressed.setBounds(0, 0, exportDBtnpressed.getMinimumWidth(), exportDBtnpressed.getMinimumHeight());
        exportDBtn.setCompoundDrawables(null, exportDBtnpressed, null, null);
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
                    printDataService = new PrintDataService(DAngleActivity.this,shares.getString("Printer",""));
                    //Toast.makeText(overActivity.this,"蓝牙打印机连接中...",Toast.LENGTH_LONG).show();
                }
                if(printDataService != null){
                    PrintConnect = printDataService.connect();
                    if(PrintConnect){
                        Toast.makeText(DAngleActivity.this,"蓝牙打印机连接成功...",Toast.LENGTH_LONG).show();
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
        printDataService.send("叉车下滑量及门架倾角检测结果");
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

        for (int i = 0; i < disMax_Data.size(); i++) {
            printDataService.send("第几次测量"+": "+ myformatNum.format(i+1)+"\n");//
            printDataService.send("下滑量值"+": "+ myformat.format(disMax_Data.get(i))+"m"+"\n");//
            printDataService.send("门架倾角值"+": "+ myformat.format(angleMax_Data.get(i))+"°"+"\n");//
        }
        printDataService.send("平均下滑量值"+": "+ str_DisAvg+"m"+"\n");//
        printDataService.send("平均门架倾角值"+": "+ str_DAngleAvg+"°"+"\n");//
        printDataService.send("*******************************\n\n\n\n");
        Toast.makeText(DAngleActivity.this,"打印完成！",Toast.LENGTH_SHORT).show();
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
        DAngleActivity.this.sendBroadcast(intent);

    }
    //创建PDF文件-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(DAngleActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //创建目录
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车下滑量及门架倾角检测报告"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + "叉车下滑量及门架倾角检测报告"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车下滑量及门架倾角检测报告" + File.separator + curDate.toString ()+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    pdfcontext = new Paragraph("叉车下滑量及门架倾角检测报告",setChineseTitleFont());
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

                    for (int i = 0; i < disMax_Data.size(); i++) {

                        cell.setPhrase(new Phrase("下滑量值：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(myformat.format(disMax_Data.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                        cell.setPhrase(new Phrase("门架倾角值：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(myformat.format(angleMax_Data.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);
                    }
                    cell.setPhrase(new Phrase("平均下滑量值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_DisAvg,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("平均门架倾角值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_DAngleAvg,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    doc.add(mtable);
                    doc.close();
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车下滑量及门架倾角检测报告" +  File.separator + curDate.toString () +".pdf");
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
            chartDAngle.setVisibility(View.VISIBLE);
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
            chartDAngle.setVisibility (View.VISIBLE);
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
            lineDataSet = new LineDataSet(null, "距离值(m)");
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
            lineDataSet1 = new LineDataSet(null, "角度(°)");
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
        }

        private void addEntry(float speed,float Aspeed){
            //lineData = chartSpeed.getData();
            Entry entry = new Entry(lineDataSet.getEntryCount()*0.0172f,speed);
            Entry entry1 = new Entry(lineDataSet1.getEntryCount()*0.0172f,Aspeed);
            lineData.addEntry(entry, 0);
            lineData.addEntry(entry1, 1);
            chartDAngle.notifyDataSetChanged();
            chartDAngle.moveViewToX(0.00f);
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
        dynamicLineChartManager_DAngle.setData(realAngle_Data, realForce_Data,0.1f);
        dynamicLineChartManager_DAngle.setYAxis(1000, 0, 5);
        dynamicLineChartManager_DAngle.setXAxis(120, 0, 10,0);
        dynamicLineChartManager_DAngle.setHightLimitLine(0f, "");
        dynamicLineChartManager_DAngle.desChart(names.get (0),165);
    }
    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        super.onDestroy();
        if(deviceDAngle != null)
        {
            mHoldBluetoothDAngle.disconnect(moduleDAngle);
        }
    }
}
