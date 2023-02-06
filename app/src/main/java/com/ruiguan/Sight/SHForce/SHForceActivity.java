package com.ruiguan.Sight.SHForce;

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
import com.ruiguan.Sight.SightActivity;
import com.ruiguan.activities.ActivityCollector;
import com.ruiguan.activities.MainActivity;
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

public class SHForceActivity extends SHForceSaveActivity {
    private BluetoothDevice deviceHForce= null;
    private DeviceModule moduleHForce= null;
    private HoldBluetooth mHoldBluetoothHForce= null;
    private BluetoothAdapter bluetoothAdapter = null;
    private List<DeviceModule> modules;
    private final String CONNECTED = "已连接",CONNECTING = "连接中",DISCONNECT = "断线了";

    private String str_company;
    private String str_number;
    private String str_sightNumber;
    private String str_sightLenght;
    private String str_sightType;
    private String str_sightLoad;

    private Button startSHFBtn;
    private Button stopSHFBtn;
    private Button scanSHFBtn;
    private Button printSHFBtn;
    private Button saveSHFBtn;
    private Button exportSHFBtn;
    private Button zeroSHFBtn;
    private Button backSHFBtn;
    private Button exitSHFBtn;
    private Button deviceSHFBtn;
    private Button addSHFBtn;
    private Button shforceHelpBtn;
//    private Button delFBtn;

    private Drawable startSHFBtnpressed;
    private Drawable stopSHFBtnpressed;
    private Drawable scanSHFBtnpressed;
    private Drawable printSHFBtnpressed;
    private Drawable saveSHFBtnpressed;
    private Drawable exportSHFBtnpressed;
    private Drawable zeroSHFBtnpressed;
    private Drawable deviceSHFBtnpressed;
    private Drawable addSHFBtnpressed;
//    private Drawable delFBtnpressed;

    private TextView realSHForce_txt;
    private TextView SHForceMax_txt;
    private TextView statusSHF_txt;

    private float ChartYMax;
    private float ChartXMax;

    //    private ArrayAdapter<String> adapter;
    private byte[] senddata;
    private byte[] forceReceive;

    private float realForce;
    private float ForceMax;

    private String str_realForce;
    private String str_forceMax;

    private BaseAdapter mAdapter;
    private ListView forcelistView;
    private List<String> dataForce = new ArrayList<String>();

    private boolean Finish=false;
    private Handler handler = new Handler();
    private LineChart chartSHForce;
    private ArrayList<Float> realForce_Data = new ArrayList<>();
    private SHForceActivity.DynamicLineChartManager dynamicLineChartManager_SHForce;
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合

    private PrintDataService printDataService = null;
    private boolean PrintConnect = false;
    java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.000");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shforce);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        senddata=new byte[9];
        forceReceive=new byte[17];
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

        final HoldBluetooth.UpdateList updateList = new HoldBluetooth.UpdateList() {
            @Override
            public void update(boolean isStart,DeviceModule deviceModule) {

            }

            @Override
            public void updateMessyCode(boolean isStart, DeviceModule deviceModule) {
            }
        };

        mHoldBluetoothHForce= new HoldBluetooth();
        mHoldBluetoothHForce.initHoldBluetooth(SHForceActivity.this,updateList);
        initMembers();
        initDataSHForceListener();
        ShowWave();
        handler.postDelayed(BleRunnable,2000);
    }
    //初始化蓝牙数据的监听
    private void initDataSHForceListener() {
        HoldBluetooth.OnReadDataListener dataListener = new HoldBluetooth.OnReadDataListener() {
            @Override
            public void readData(String mac, byte[] data) {
                if (deviceHForce.getAddress().equals(mac)){
                    forceReceive=data;
                }
            }
            @Override
            public void reading(boolean isStart) {

            }
            @Override
            public void connectSucceed() {
                modules = mHoldBluetoothHForce.getConnectedArray();
                for(int i=0;i<modules.size();i++)
                {
                    if(modules.get(i).getMac().equals(deviceHForce.getAddress()))
                    {
                        setHForceState(CONNECTED);//设置连接状态
                        Log.d("HForceActivity","HForce蓝牙连接成功！");
                    }
                }
            }
            @Override
            public void errorDisconnect(final DeviceModule deviceModule) {//蓝牙异常断开

                if(deviceModule.getMac().equals(deviceHForce.getAddress()))
                {
                    setHForceState(DISCONNECT);//设置断开状态
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
        mHoldBluetoothHForce.setOnReadListener(dataListener);
    }

    private void setHForceState(String state){
        switch (state){
            case CONNECTED://连接成功
                statusSHF_txt.setText("已连接");
                deviceSHFBtnpressed = getResources().getDrawable(R.drawable.btle_connected);
                deviceSHFBtnpressed.setBounds(0, 0,  deviceSHFBtnpressed.getMinimumWidth(),  deviceSHFBtnpressed.getMinimumHeight());
                deviceSHFBtn.setCompoundDrawables(null,  deviceSHFBtnpressed, null, null);
                setEnableButton();
                break;

            case CONNECTING://连接中
                statusSHF_txt.setText("连接中");
                deviceSHFBtnpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                deviceSHFBtnpressed.setBounds(0, 0,  deviceSHFBtnpressed.getMinimumWidth(),  deviceSHFBtnpressed.getMinimumHeight());
                deviceSHFBtn.setCompoundDrawables(null,  deviceSHFBtnpressed, null, null);
                break;

            case DISCONNECT://连接断开
                statusSHF_txt.setText("断开");
                deviceSHFBtnpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                deviceSHFBtnpressed.setBounds(0, 0,  deviceSHFBtnpressed.getMinimumWidth(),  deviceSHFBtnpressed.getMinimumHeight());
                deviceSHFBtn.setCompoundDrawables(null,  deviceSHFBtnpressed, null, null);
                break;
        }
    }

    Runnable BleRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences shares2 = getSharedPreferences( "Hand_Decive", Activity.MODE_PRIVATE );
            if(!shares2.getBoolean("BondDecive",false))
            {
                Intent intent = new Intent(SHForceActivity.this, MainActivity.class);
                startActivity(intent);
            }else
            {
                deviceHForce= bluetoothAdapter.getRemoteDevice(shares2.getString("Hand",""));
                if(deviceHForce == null)
                {
                    Toast.makeText(SHForceActivity.this,"未绑定制停距离蓝牙！",Toast.LENGTH_LONG).show();
                }else{
                    DeviceModule deviceModuleHForce = new DeviceModule(deviceHForce.getName(),deviceHForce);
                    moduleHForce= deviceModuleHForce;
                    mHoldBluetoothHForce.connect(moduleHForce);
                    //controlClientBrake = SocketThread.getClient(deviceBrake);
                    Log.d("mHoldBluetoothSHForce","开始连接蓝牙");
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

        realSHForce_txt= (TextView) findViewById(R.id.realSHForce_txt);
        SHForceMax_txt=(TextView) findViewById(R.id.SHForceMax_txt);
        statusSHF_txt= (TextView) findViewById(R.id.statusSHF_txt);

        startSHFBtn = findViewById(R.id.startSHFBtn);
        stopSHFBtn = findViewById(R.id.stopSHFBtn);
        scanSHFBtn = findViewById(R.id.scanSHFBtn);
        printSHFBtn = findViewById(R.id.printSHFBtn);
        saveSHFBtn = findViewById(R.id.saveSHFBtn);
        exportSHFBtn = findViewById(R.id.exportSHFBtn);
        backSHFBtn = findViewById(R.id.backSHFBtn);
        zeroSHFBtn= findViewById(R.id. zeroSHFBtn);
        exitSHFBtn = findViewById(R.id.exitSHFBtn);
        deviceSHFBtn= findViewById(R.id.deviceSHF_txt);
        addSHFBtn = findViewById(R.id.addSHFBtn);
        shforceHelpBtn= findViewById(R.id.shforceHelpBtn);
//        delFBtn = findViewById(R.id.delFBtn);
        View.OnClickListener bl = new SHForceActivity.ButtonListener();
        setOnClickListener(startSHFBtn, bl);
        setOnClickListener(stopSHFBtn, bl);
        setOnClickListener(scanSHFBtn, bl);
        setOnClickListener(printSHFBtn, bl);
        setOnClickListener(saveSHFBtn, bl);
        setOnClickListener(exportSHFBtn, bl);
        setOnClickListener(backSHFBtn, bl);
        setOnClickListener(zeroSHFBtn, bl);
        setOnClickListener(exitSHFBtn, bl);
        setOnClickListener(addSHFBtn, bl);
        setOnClickListener(shforceHelpBtn, bl);
//        setOnClickListener(delFBtn, bl);
        setButton();
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return dataForce.size();
            }
            @Override
            public Object getItem(int position) {
                return dataForce.get(position);
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

        forcelistView = (ListView)findViewById(R.id.listSHForce);
        forcelistView.setAdapter(mAdapter);
        forcelistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                str_forceMax=dataForce.get(position);
                Toast.makeText(SHForceActivity.this, "已选中要保存的数据", Toast.LENGTH_SHORT).show();
            }
        });


        chartSHForce= findViewById(R.id.chartSHF);
        names.add ("");
        colour.add (Color.argb (255, 255, 125, 0));            //定义Fre颜色
        dynamicLineChartManager_SHForce = new SHForceActivity.DynamicLineChartManager(chartSHForce, names.get (0), colour.get (0), 0);
    }
    @SuppressLint("SetTextI18n")
    private View getListView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView =getLayoutInflater().inflate(R.layout.force_list, null);//加载布局
        }
        TextView realValue_txt = (TextView) convertView.findViewById(R.id.realValue);
        TextView maxValue_txt = (TextView) convertView.findViewById(R.id.maxValue);

        realValue_txt.setText(Integer.toString(position+1));
        maxValue_txt .setText(dataForce.get(position));

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
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            final String METHODTAG = ".ButtonListener.onClick";
            switch (v.getId()) {
                case R.id.startSHFBtn: {
                    Finish = false;
                    startSHFBtn.setEnabled(false);
                    startSHFBtnpressed = getResources().getDrawable(R.drawable.start);
                    startSHFBtnpressed.setBounds(0, 0, startSHFBtnpressed.getMinimumWidth(), startSHFBtnpressed.getMinimumHeight());
                    startSHFBtn.setCompoundDrawables(null, startSHFBtnpressed, null, null);
                    for(int i=0;i<3;i++) {
                        senddata[0] = 0x4B;
                        senddata[1] = 0x53;
                        senddata[2] = 0x02;
                        senddata[3] = 0x00;
                        senddata[4] = 0x02;
                        senddata[5] = 0x00;
                        senddata[6] = 0x01;
                        senddata[7] = 0x3A;
                        senddata[8] = 0x3B;
                        mHoldBluetoothHForce.sendData(moduleHForce,senddata);
                    }

                    ChartYMax = 0.0f;
                    ChartXMax = 0.0f;
                    realForce_Data.clear();
                    dynamicLineChartManager_SHForce.clear();
                    realForce= 0.0f;
                    ForceMax= 0.0f;
                    str_realForce = myformat.format(realForce);
                    str_forceMax = myformat.format(ForceMax);
                    realSHForce_txt.setText(str_realForce);
                    SHForceMax_txt.setText(str_forceMax);
                    SHForceMax_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                    handler.postDelayed(ReceiveRunnable,10);
                }
                break;
                case R.id.stopSHFBtn: {
                    Finish = true;
                    stopSHFBtn.setEnabled(false);
                    stopSHFBtnpressed = getResources().getDrawable(R.drawable.stop);
                    stopSHFBtnpressed.setBounds(0, 0, stopSHFBtnpressed.getMinimumWidth(), stopSHFBtnpressed.getMinimumHeight());
                    stopSHFBtn.setCompoundDrawables(null, stopSHFBtnpressed, null, null);
                    for(int i=0;i<3;i++) {
                        senddata[0] = 0x4B;
                        senddata[1] = 0x53;
                        senddata[2] = 0x02;
                        senddata[3] = 0x00;
                        senddata[4] = 0x02;
                        senddata[5] = 0x00;
                        senddata[6] = 0x05;
                        senddata[7] = 0x3A;
                        senddata[8] = 0x3B;
                        mHoldBluetoothHForce.sendData(moduleHForce,senddata);
                    }

                    handler.removeCallbacks (ReceiveRunnable);
                }
                break;
                case R.id.scanSHFBtn: {
                    Intent intent = new Intent(SHForceActivity.this, SHForceSaveActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.printSHFBtn: {
                    printSHFBtn.setEnabled(false);
                    printSHFBtnpressed = getResources().getDrawable(R.drawable.print);
                    printSHFBtnpressed.setBounds(0, 0, printSHFBtnpressed.getMinimumWidth(), printSHFBtnpressed.getMinimumHeight());
                    printSHFBtn.setCompoundDrawables(null, printSHFBtnpressed, null, null);
                    if (Finish) {
                        if (printDataService == null) {           //首次连接打印机
                            SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                            if (!shares.getBoolean("BondPrinter", false)) {
                                Toast.makeText(SHForceActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SHForceActivity.this.getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                        } else {          //打印数据
                            PrintMeasureData();
                        }
                    } else {
                        Toast.makeText(SHForceActivity.this, "没有可以打印的数据", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                case R.id.saveSHFBtn: {
                    saveSHFBtn.setEnabled(false);
                    saveSHFBtnpressed = getResources().getDrawable(R.drawable.save);
                    saveSHFBtnpressed.setBounds(0, 0, saveSHFBtnpressed.getMinimumWidth(), saveSHFBtnpressed.getMinimumHeight());
                    saveSHFBtn.setCompoundDrawables(null, saveSHFBtnpressed, null, null);
                    forceAddSHF(str_forceMax);
                }
                break;
                case R.id.exportSHFBtn: {
                    exportSHFBtn.setEnabled(false);
                    exportSHFBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportSHFBtnpressed.setBounds(0, 0, exportSHFBtnpressed.getMinimumWidth(), exportSHFBtnpressed.getMinimumHeight());
                    exportSHFBtn.setCompoundDrawables(null, exportSHFBtnpressed, null, null);
                    CreatePdf();
                    Toast.makeText(SHForceActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车手刹力检测报告", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.backSHFBtn: {
                    Intent intent1 = new Intent(SHForceActivity.this, SightActivity.class);
                    startActivity(intent1);
                    finish();
                }
                break;
                case R.id.shforceHelpBtn: {
                    showControlDialog();
                }
                break;
                case R.id.zeroSHFBtn: {
                    zeroSHFBtn.setEnabled(false);
                    zeroSHFBtnpressed = getResources().getDrawable(R.drawable.start);
                    zeroSHFBtnpressed.setBounds(0, 0, zeroSHFBtnpressed.getMinimumWidth(), zeroSHFBtnpressed.getMinimumHeight());
                    zeroSHFBtn.setCompoundDrawables(null, zeroSHFBtnpressed, null, null);
                    realForce= 0.0f;
                    ForceMax= 0.0f;
                    str_realForce = myformat.format(realForce);
                    str_forceMax = myformat.format(ForceMax);
                    realSHForce_txt.setText(str_realForce);
                    SHForceMax_txt.setText(str_forceMax);
                }
                break;
                case R.id.addSHFBtn: {
                    addSHFBtn.setEnabled(false);
                    addSHFBtnpressed = getResources().getDrawable(R.drawable.add);
                    addSHFBtnpressed.setBounds(0, 0,addSHFBtnpressed.getMinimumWidth(), addSHFBtnpressed.getMinimumHeight());
                    addSHFBtn.setCompoundDrawables(null, addSHFBtnpressed, null, null);
                    dataForce.add(str_forceMax);
                    mAdapter.notifyDataSetChanged();
                    forcelistView.invalidateViews();
                }
                break;
                case R.id.exitSHFBtn: {
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
                new AlertDialog.Builder(SHForceActivity.this);
        normalDialog.setIcon(R.drawable.help1);
        normalDialog.setTitle("GB/T 21268-2014 非公路用旅游观光车通用技术条件");
        normalDialog.setMessage("1、驾驶员施加于操纵装置上的力：手操纵时，应小于或等于400N。");
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
            int HForceRevNum=0;
            byte[] mReceiveHForce=new byte[17];
            String[] str_ReceiveData=new String[17];
           // forceReceive=controlClientHand.receive;
            if(forceReceive.length>=17)
            {
                for(int i=0;i<forceReceive.length;i++)
                {
                    if(forceReceive[HForceRevNum]==0x4B)
                    {
                        break;
                    }else{
                        HForceRevNum++;
                    }
                }
                for(int i=0;i<17;i++)
                {
                    if((HForceRevNum+i)<forceReceive.length)
                    {
                        mReceiveHForce[i] =forceReceive[HForceRevNum+i];
                    }else{
                        mReceiveHForce[i] =forceReceive[HForceRevNum+i-forceReceive.length];
                    }
                }

                for(int i=0;i<17;i++)
                {
                    str_ReceiveData[i]=myformat.format( mReceiveHForce[i]);
                }

            Log.d("sForceActivity","     "+str_ReceiveData[0]+"  "+str_ReceiveData[1]+" "+str_ReceiveData[2]+"  "+str_ReceiveData[3]+" "+str_ReceiveData[4]+"  "+str_ReceiveData[5]+" "+
                    str_ReceiveData[6]+"  "+str_ReceiveData[7]+" "+str_ReceiveData[8]+"  "+str_ReceiveData[9]+" "+str_ReceiveData[10]+"  "+str_ReceiveData[11]+" "+
                    str_ReceiveData[12]+"  "+str_ReceiveData[13]+" "+str_ReceiveData[14]+str_ReceiveData[15]+str_ReceiveData[16]+"  ");
            if((mReceiveHForce[0]==0x4B) && (mReceiveHForce[1]==0x53)) {
                switch (mReceiveHForce[2]) {
                    case 0x02:
                        int tmp = 0;
                        tmp = (char) (mReceiveHForce[4] & 0xFF) * 256 + (char) (mReceiveHForce[5] & 0xFF);
                        tmp = tmp & 0xFFFF;
                        if (tmp <= 0x7FFF) realForce = (float) tmp / 10.0f;
                        else realForce = (float) (0x10000 - tmp) / 10.0f * (-1.0f);

                        realForce_Data.add(realForce);

                        if(realForce>10.0f)
                        {
                            if (Math.abs(ForceMax) < Math.abs(realForce)) {
                                ForceMax = realForce;
                            }
                            str_realForce = myformat.format(realForce);
                            realSHForce_txt.setText(str_realForce);
                            str_forceMax = myformat.format(ForceMax);
                            SHForceMax_txt.setText(str_forceMax);
                            if(Math.abs(ForceMax)>400.0f)
                            {
                                SHForceMax_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                            }
                        }else
                        {
                            realForce=0.0f;
                            str_realForce = myformat.format(realForce);
                            realSHForce_txt.setText(str_realForce);
                            str_forceMax = myformat.format(ForceMax);
                            SHForceMax_txt.setText(str_forceMax);
                        }

                        if (ChartYMax < Math.abs(realForce)) {
                            ChartYMax = Math.abs(realForce);
                        }
                        ChartXMax = ChartXMax + 0.03f;
                        if (ChartXMax > 100000) {
                            ChartXMax = 0;
                        }

                        dynamicLineChartManager_SHForce.setYAxis(ChartYMax * 1.2f, 0, 5);
                        dynamicLineChartManager_SHForce.setXAxis(ChartXMax * 1.2f, 0, 10, 0);
                        dynamicLineChartManager_SHForce.addEntry(realForce);
                        break;
                    default:
                        break;
                }
            }
            }
            handler.postDelayed(ReceiveRunnable,30);
        }
    };

    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            startSHFBtn.setEnabled(true);
            startSHFBtnpressed = getResources().getDrawable(R.drawable.start1);
            startSHFBtnpressed.setBounds(0, 0, startSHFBtnpressed.getMinimumWidth(), startSHFBtnpressed.getMinimumHeight());
            startSHFBtn.setCompoundDrawables(null, startSHFBtnpressed, null, null);

            stopSHFBtn.setEnabled(true);
            stopSHFBtnpressed = getResources().getDrawable(R.drawable.stop1);
            stopSHFBtnpressed.setBounds(0, 0, stopSHFBtnpressed.getMinimumWidth(), stopSHFBtnpressed.getMinimumHeight());
            stopSHFBtn.setCompoundDrawables(null, stopSHFBtnpressed, null, null);

            scanSHFBtn.setEnabled(true);
            scanSHFBtnpressed = getResources().getDrawable(R.drawable.scan1);
            scanSHFBtnpressed.setBounds(0, 0, scanSHFBtnpressed.getMinimumWidth(), scanSHFBtnpressed.getMinimumHeight());
            scanSHFBtn.setCompoundDrawables(null, scanSHFBtnpressed, null, null);

            printSHFBtn.setEnabled(true);
            printSHFBtnpressed = getResources().getDrawable(R.drawable.print1);
            printSHFBtnpressed.setBounds(0, 0, printSHFBtnpressed.getMinimumWidth(), printSHFBtnpressed.getMinimumHeight());
            printSHFBtn.setCompoundDrawables(null, printSHFBtnpressed, null, null);

            saveSHFBtn.setEnabled(true);
            saveSHFBtnpressed = getResources().getDrawable(R.drawable.save1);
            saveSHFBtnpressed.setBounds(0, 0, saveSHFBtnpressed.getMinimumWidth(), saveSHFBtnpressed.getMinimumHeight());
            saveSHFBtn.setCompoundDrawables(null, saveSHFBtnpressed, null, null);

            exportSHFBtn.setEnabled(true);
            exportSHFBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportSHFBtnpressed.setBounds(0, 0, exportSHFBtnpressed.getMinimumWidth(), exportSHFBtnpressed.getMinimumHeight());
            exportSHFBtn.setCompoundDrawables(null, exportSHFBtnpressed, null, null);

            zeroSHFBtn.setEnabled(true);
            zeroSHFBtnpressed = getResources().getDrawable(R.drawable.zero1);
            zeroSHFBtnpressed.setBounds(0, 0, zeroSHFBtnpressed.getMinimumWidth(), zeroSHFBtnpressed.getMinimumHeight());
            zeroSHFBtn.setCompoundDrawables(null, zeroSHFBtnpressed, null, null);

            addSHFBtn.setEnabled(true);
            addSHFBtnpressed = getResources().getDrawable(R.drawable.add1);
            addSHFBtnpressed.setBounds(0, 0, addSHFBtnpressed.getMinimumWidth(), addSHFBtnpressed.getMinimumHeight());
            addSHFBtn.setCompoundDrawables(null, addSHFBtnpressed, null, null);
        }
    };
    private void setEnableButton()
    {
        startSHFBtn.setEnabled(true);
        startSHFBtnpressed = getResources().getDrawable(R.drawable.start1);
        startSHFBtnpressed.setBounds(0, 0, startSHFBtnpressed.getMinimumWidth(), startSHFBtnpressed.getMinimumHeight());
        startSHFBtn.setCompoundDrawables(null, startSHFBtnpressed, null, null);

        stopSHFBtn.setEnabled(true);
        stopSHFBtnpressed = getResources().getDrawable(R.drawable.stop1);
        stopSHFBtnpressed.setBounds(0, 0, stopSHFBtnpressed.getMinimumWidth(), stopSHFBtnpressed.getMinimumHeight());
        stopSHFBtn.setCompoundDrawables(null, stopSHFBtnpressed, null, null);

        scanSHFBtn.setEnabled(true);
        scanSHFBtnpressed = getResources().getDrawable(R.drawable.scan1);
        scanSHFBtnpressed.setBounds(0, 0, scanSHFBtnpressed.getMinimumWidth(), scanSHFBtnpressed.getMinimumHeight());
        scanSHFBtn.setCompoundDrawables(null, scanSHFBtnpressed, null, null);

        printSHFBtn.setEnabled(true);
        printSHFBtnpressed = getResources().getDrawable(R.drawable.print1);
        printSHFBtnpressed.setBounds(0, 0, printSHFBtnpressed.getMinimumWidth(), printSHFBtnpressed.getMinimumHeight());
        printSHFBtn.setCompoundDrawables(null, printSHFBtnpressed, null, null);

        saveSHFBtn.setEnabled(true);
        saveSHFBtnpressed = getResources().getDrawable(R.drawable.save1);
        saveSHFBtnpressed.setBounds(0, 0, saveSHFBtnpressed.getMinimumWidth(), saveSHFBtnpressed.getMinimumHeight());
        saveSHFBtn.setCompoundDrawables(null, saveSHFBtnpressed, null, null);

        zeroSHFBtn.setEnabled(true);
        zeroSHFBtnpressed = getResources().getDrawable(R.drawable.zero1);
        zeroSHFBtnpressed.setBounds(0, 0, zeroSHFBtnpressed.getMinimumWidth(), zeroSHFBtnpressed.getMinimumHeight());
        zeroSHFBtn.setCompoundDrawables(null, zeroSHFBtnpressed, null, null);

        exportSHFBtn.setEnabled(true);
        exportSHFBtnpressed = getResources().getDrawable(R.drawable.export1);
        exportSHFBtnpressed.setBounds(0, 0, exportSHFBtnpressed.getMinimumWidth(), exportSHFBtnpressed.getMinimumHeight());
        exportSHFBtn.setCompoundDrawables(null, exportSHFBtnpressed, null, null);

        addSHFBtn.setEnabled(true);
        addSHFBtnpressed = getResources().getDrawable(R.drawable.add1);
        addSHFBtnpressed.setBounds(0, 0, addSHFBtnpressed.getMinimumWidth(), addSHFBtnpressed.getMinimumHeight());
        addSHFBtn.setCompoundDrawables(null, addSHFBtnpressed, null, null);

    }
    private void setButton()
    {
        startSHFBtn.setEnabled(false);
        startSHFBtnpressed = getResources().getDrawable(R.drawable.start);
        startSHFBtnpressed.setBounds(0, 0, startSHFBtnpressed.getMinimumWidth(), startSHFBtnpressed.getMinimumHeight());
        startSHFBtn.setCompoundDrawables(null, startSHFBtnpressed, null, null);

        stopSHFBtn.setEnabled(false);
        stopSHFBtnpressed = getResources().getDrawable(R.drawable.stop);
        stopSHFBtnpressed.setBounds(0, 0, stopSHFBtnpressed.getMinimumWidth(), stopSHFBtnpressed.getMinimumHeight());
        stopSHFBtn.setCompoundDrawables(null, stopSHFBtnpressed, null, null);

        printSHFBtn.setEnabled(false);
        printSHFBtnpressed = getResources().getDrawable(R.drawable.print);
        printSHFBtnpressed.setBounds(0, 0, printSHFBtnpressed.getMinimumWidth(), printSHFBtnpressed.getMinimumHeight());
        printSHFBtn.setCompoundDrawables(null, printSHFBtnpressed, null, null);

        saveSHFBtn.setEnabled(false);
        saveSHFBtnpressed = getResources().getDrawable(R.drawable.save);
        saveSHFBtnpressed.setBounds(0, 0, saveSHFBtnpressed.getMinimumWidth(), saveSHFBtnpressed.getMinimumHeight());
        saveSHFBtn.setCompoundDrawables(null, saveSHFBtnpressed, null, null);

        exportSHFBtn.setEnabled(false);
        exportSHFBtnpressed = getResources().getDrawable(R.drawable.export);
        exportSHFBtnpressed.setBounds(0, 0, exportSHFBtnpressed.getMinimumWidth(), exportSHFBtnpressed.getMinimumHeight());
        exportSHFBtn.setCompoundDrawables(null, exportSHFBtnpressed, null, null);

        zeroSHFBtn.setEnabled(false);
        zeroSHFBtnpressed = getResources().getDrawable(R.drawable.zero);
        zeroSHFBtnpressed.setBounds(0, 0, zeroSHFBtnpressed.getMinimumWidth(), zeroSHFBtnpressed.getMinimumHeight());
        zeroSHFBtn.setCompoundDrawables(null, zeroSHFBtnpressed, null, null);

        addSHFBtn.setEnabled(false);
        addSHFBtnpressed = getResources().getDrawable(R.drawable.add);
        addSHFBtnpressed.setBounds(0, 0, addSHFBtnpressed.getMinimumWidth(), addSHFBtnpressed.getMinimumHeight());
        addSHFBtn.setCompoundDrawables(null, addSHFBtnpressed, null, null);
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
                    printDataService = new PrintDataService(SHForceActivity.this,shares.getString("Printer",""));
                    //Toast.makeText(overActivity.this,"蓝牙打印机连接中...",Toast.LENGTH_LONG).show();
                }
                if(printDataService != null){
                    PrintConnect = printDataService.connect();
                    if(PrintConnect){
                        Toast.makeText(SHForceActivity.this,"蓝牙打印机连接成功...",Toast.LENGTH_LONG).show();
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
        printDataService.send("观光车辆/观光列车手刹力检测结果");
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
        printDataService.send("最大受力值"+": "+ str_forceMax+"N"+"\n");//
        printDataService.send("*******************************\n\n\n\n");
        Toast.makeText(SHForceActivity.this,"打印完成！",Toast.LENGTH_SHORT).show();
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
        SHForceActivity.this.sendBroadcast(intent);

    }
    //创建PDF文件-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(SHForceActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //创建目录
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车手刹力检测报告"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车手刹力检测报告"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车手刹力检测报告" + File.separator + curDate.toString ()+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    pdfcontext = new Paragraph("观光车辆/观光列车手刹力检测报告",setChineseTitleFont());
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

                    cell.setPhrase(new Phrase("最大受力值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_forceMax,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);

                    doc.add(mtable);
                    doc.close();
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车手刹力检测报告" +  File.separator + curDate.toString () +".pdf");
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
            chartSHForce.setVisibility(View.VISIBLE);
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
            chartSHForce.setVisibility (View.VISIBLE);
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
            chartSHForce.notifyDataSetChanged();
            chartSHForce.moveViewToX(0.00f);
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
        dynamicLineChartManager_SHForce.setData(realForce_Data,0.1f);
        dynamicLineChartManager_SHForce.setYAxis(1000, 0, 5);
        dynamicLineChartManager_SHForce.setXAxis(120, 0, 10,0);
        dynamicLineChartManager_SHForce.setHightLimitLine(0f, "");
        dynamicLineChartManager_SHForce.desChart(names.get (0),165);
    }
        @Override
        protected void onDestroy() {
            ActivityCollector.removeActivity(this);
            super.onDestroy();
            if(deviceHForce != null)
            {
                mHoldBluetoothHForce.disconnect(moduleHForce);
            }
        }
}