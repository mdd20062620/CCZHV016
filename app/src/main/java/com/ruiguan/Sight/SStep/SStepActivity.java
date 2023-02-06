package com.ruiguan.Sight.SStep;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
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
import com.ruiguan.util.SensorEventHelper;
import com.ruiguan.view.DialPlateView;

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
import static java.lang.Math.PI;

public class SStepActivity extends SStepSaveActivity implements LocationSource,AMapLocationListener{
    private BluetoothDevice deviceStep= null;
    private DeviceModule moduleStep= null;
    private HoldBluetooth mHoldBluetoothStep= null;
    private BluetoothAdapter bluetoothAdapter = null;
    private List<DeviceModule> modules;
    private final String CONNECTED = "已连接",CONNECTING = "连接中",DISCONNECT = "断线了";

    private AMap aMap;                            //地图控制器
    private MapView mMapView;                    //定义地图控件
    private LatLng oldLocation;                 //过去的位置
    private LatLng startLatLng;
    private LatLng centerLatLng;
    private boolean mFirstFix;                  //记录是否第一次定位
    PolylineOptions options;                   //线段选项类
    private int optionsMarkerindex=0;         //记录是否第一次定位
    List<PolylineOptions> optionsMarker;      //线段选项类
    private ArrayList<Marker> errStepMarker=new ArrayList<Marker> ();
    private ArrayList<Float> errLatitude=new ArrayList<Float>();
    private ArrayList<Float> errLongitude=new ArrayList<Float>();
    private ArrayList<Float> startDis=new ArrayList<Float>();
    private ArrayList<Float> endDis=new ArrayList<Float>();
    private ArrayList<Float> errDisMarker=new ArrayList<Float>();
    private ArrayList<Float> errStepMax=new ArrayList<Float>();
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;           //定位服务类
    private AMapLocationClientOption mLocationOption;     //设置定位参数
    private MyLocationStyle myLocationStyle;
    private final int SDK_PERMISSION = 1;                //申请权限
    private SensorEventHelper mSensorHelper;             //使用方向传感器模拟指南针

    private String str_company;
    private String str_number;
    private String str_sightNumber;
    private String str_sightLenght;
    private String str_sightType;
    private String str_sightLoad;

    private Button stepStartBtn;
    private Button stepStopBtn;
    private Button stepScanBtn;
    private Button stepPrintBtn;
    private Button stepSaveBtn;
    private Button stepExportBtn;
    private Button stepBackBtn;
    private Button stepExportMapBtn;
    private Button deviceSStepBtn;
    private Button stepHelpBtn;

    private Drawable stepStartBtnpressed;
    private Drawable stepStopBtnpressed;
    private Drawable stepScanBtnpressed;
    private Drawable stepPrintBtnpressed;
    private Drawable stepSaveBtnpressed;
    private Drawable stepExportBtnpressed;
    private Drawable stepExportMapBtnpressed;
    private Drawable deviceSStepBtnpressed;

    private TextView stepDis_txt;
    private TextView statusSStep_txt;
    private TextView realStep_txt;
    private TextView realSpeed_txt;
    private TextView speedMax_txt;
    private TextView stepAngleS_txt;
    //private TextView stepAngleHS_txt;
    private TextView realLongitude_txt;
    private TextView realLattitude_txt;

    private byte[] senddata;
    private byte[] stepReceive;

    private float yAngle;
    //private float zAngle;
    private float Lattitude=0.0f;
    private float Longitude=0.0f;
    private float realLat=0.0f;
    private float realLon=0.0f;

    private float yAngleFirst;
    private float zAngleFirst;
    private float LonFirst;
    private float LatFirst;

    private float Dis=0.0f;
    private float errDis=0.0f;
    private float realStep=0.0f;
    private float stepMax=0.0f;
    private float stepMaxAll=0.0f;

    private float realSpeed=0.0f;
    private float speedMax=0.0f;

    //private float realLongitude;
    //private float realLattitude;

    private String str_yAngle;
    //private String str_zAngle;
    private String str_Dis;
    private String str_realStep;
    private String str_stepMax;
    private String str_stepMaxAll;

    private String str_realSpeed;
    private String str_speedMax;

    private String str_realLongitude;
    private String str_realLattitude;
    private String str_errLongitude;
    private String str_errLattitude;
    // ProgressDialog pd;
    private boolean sightTypeFlag=false;
    private boolean markerFlag=false;
    private boolean colorFlag=false;
    private boolean colorFirstFlag=false;
    private boolean startFlag=false;
    private DialPlateView dialPlateView;//显示仪表盘

    private boolean Finish=false;
    private Handler handler = new Handler();
    private PrintDataService printDataService = null;
    private boolean PrintConnect = false;
    java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.000");
    java.text.DecimalFormat myformatLat=new java.text.DecimalFormat("0.00000");

    private String str_StepMAX30;
    private String str_StepMAX20;
    private String str_StepMAX20L;
    private String str_StepMAX10L;

    private float StepMAX30=0.0f;
    private float StepMAX20=0.0f;
    private float StepMAX20L=0.0f;
    private float StepMAX10L=0.0f;

    private TextView stepset_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sstep);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
       // ActivityCollector.addActivity(this);
        senddata=new byte[9];
        stepReceive=new byte[17];
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);      //获取地图控件引用
        mMapView.onCreate(savedInstanceState);
        options = new PolylineOptions();                  //创建线段选项
        optionsMarker= new ArrayList<PolylineOptions>();  //创建线段选项
        errStepMarker=new ArrayList<Marker> ();
        errLatitude=new ArrayList<Float>();
        errLongitude=new ArrayList<Float>();
        errDisMarker=new ArrayList<Float>();
        errStepMax=new ArrayList<Float>();
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

        final HoldBluetooth.UpdateList updateList = new HoldBluetooth.UpdateList() {
            @Override
            public void update(boolean isStart,DeviceModule deviceModule) {

            }

            @Override
            public void updateMessyCode(boolean isStart, DeviceModule deviceModule) {
            }
        };

        mHoldBluetoothStep= new HoldBluetooth();
        mHoldBluetoothStep.initHoldBluetooth(SStepActivity.this,updateList);
        initMembers();
        initDataStepListener();
        getPersimmions();                               //获取定位动态权限
        handler.postDelayed(BleRunnable,2000);
    }
    //初始化蓝牙数据的监听
    private void initDataStepListener() {
        HoldBluetooth.OnReadDataListener dataListener = new HoldBluetooth.OnReadDataListener() {
            @Override
            public void readData(String mac, byte[] data) {
                if (deviceStep.getAddress().equals(mac)){
                    stepReceive=data;
                }
            }
            @Override
            public void reading(boolean isStart) {

            }
            @Override
            public void connectSucceed() {
                modules = mHoldBluetoothStep.getConnectedArray();
                for(int i=0;i<modules.size();i++)
                {
                    if(modules.get(i).getMac().equals(deviceStep.getAddress()))
                    {
                        setStepState(CONNECTED);//设置连接状态
                        Log.d("SStepActivity","Step蓝牙连接成功！");
                    }
                }
            }
            @Override
            public void errorDisconnect(final DeviceModule deviceModule) {//蓝牙异常断开

                if(deviceModule.getMac().equals(deviceStep.getAddress()))
                {
                    setStepState(DISCONNECT);//设置断开状态
                }
                handler.removeCallbacks (ReceiveRunnable);
                setButton();
            }
            @Override
            public void readNumber(int number) {
                //ReceiveNumMax=number;
            }

            @Override
            public void readLog(String className, String data, String lv) {

            }

            @Override
            public void readVelocity(int velocity) {

            }
        };
        mHoldBluetoothStep.setOnReadListener(dataListener);
    }

    private void setStepState(String state){
        switch (state){
            case CONNECTED://连接成功
                statusSStep_txt.setText("已连接");
                deviceSStepBtnpressed = getResources().getDrawable(R.drawable.btle_connected);
                deviceSStepBtnpressed.setBounds(0, 0, deviceSStepBtnpressed.getMinimumWidth(), deviceSStepBtnpressed.getMinimumHeight());
                deviceSStepBtn.setCompoundDrawables(null, deviceSStepBtnpressed, null, null);
                setEnableButton();
                break;
            case CONNECTING://连接中
                statusSStep_txt.setText("连接中");
                deviceSStepBtnpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                deviceSStepBtnpressed.setBounds(0, 0, deviceSStepBtnpressed.getMinimumWidth(), deviceSStepBtnpressed.getMinimumHeight());
                deviceSStepBtn.setCompoundDrawables(null, deviceSStepBtnpressed, null, null);
                break;
            case DISCONNECT://连接断开
                statusSStep_txt.setText("断开");
                deviceSStepBtnpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                deviceSStepBtnpressed.setBounds(0, 0, deviceSStepBtnpressed.getMinimumWidth(), deviceSStepBtnpressed.getMinimumHeight());
                deviceSStepBtn.setCompoundDrawables(null, deviceSStepBtnpressed, null, null);
                break;
        }
    }

    Runnable BleRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences shares2 = getSharedPreferences( "Steps_Decive", Activity.MODE_PRIVATE );
            if(!shares2.getBoolean("BondDecive",false))
            {
                Intent intent = new Intent(SStepActivity.this, MainActivity.class);
                startActivity(intent);
            }else
            {
                deviceStep= bluetoothAdapter.getRemoteDevice(shares2.getString("Steps",""));
                if(deviceStep == null)
                {
                    Toast.makeText(SStepActivity.this,"未绑定坡度蓝牙！",Toast.LENGTH_LONG).show();
                }else{
                    DeviceModule deviceModuleStep = new DeviceModule(deviceStep.getName(),deviceStep);
                    moduleStep= deviceModuleStep;
                    mHoldBluetoothStep.connect(moduleStep);
                    //controlClientBrake = SocketThread.getClient(deviceBrake);
                    Log.d("mHoldBluetoothStep","开始连接蓝牙");
                }
            }
        }
    };
    private void initMembers() {
        if (aMap == null) {
            aMap = mMapView.getMap();                  //地图控制
        }
        //定位
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位 LOCATION_TYPE_LOCATE、跟随 LOCATION_TYPE_MAP_FOLLOW 或地图根据面向方向旋转 LOCATION_TYPE_MAP_ROTATE
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);

        mSensorHelper = new SensorEventHelper(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();     //注测方向传感器监听
        }
        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_sightNumber= sight_Input.getsightNumber();
        str_sightLenght= sight_Input.getsightLenght();
        str_sightType= sight_Input.getsightType();
        str_sightLoad= sight_Input.getsightLoad();

        if(str_sightType.equals("观光车辆"))
        {
            sightTypeFlag=false;
        }else if(str_sightType.equals("观光列车"))
        {
            sightTypeFlag=true;
        }
        realLongitude_txt= (TextView) findViewById(R.id.realLongitude_txt);
        realLattitude_txt= (TextView) findViewById(R.id.realLattitude_txt);
        realStep_txt= (TextView) findViewById(R.id.realStep_txt);
        stepAngleS_txt= (TextView) findViewById(R.id.stepAngleS_txt);
        //stepAngleHS_txt= (TextView) findViewById(R.id.stepAngleHS_txt);
        stepDis_txt= (TextView) findViewById(R.id.stepDis_txt);
        statusSStep_txt= (TextView) findViewById(R.id.statusSStep_txt);
        realSpeed_txt= (TextView) findViewById(R.id.realSpeedStep_txt);
        speedMax_txt= (TextView) findViewById(R.id.speedMaxStep_txt);
        dialPlateView = (DialPlateView) findViewById(R.id.show);

        stepStartBtn = findViewById(R.id.stepStartBtn);
        stepStopBtn = findViewById(R.id.stepStopBtn);
        stepScanBtn = findViewById(R.id.stepScanBtn);
        stepPrintBtn = findViewById(R.id.stepPrintBtn);
        stepSaveBtn = findViewById(R.id.stepSaveBtn);
        stepExportBtn = findViewById(R.id.stepExportBtn);
        stepExportMapBtn = findViewById(R.id.stepExportMapBtn);
        stepBackBtn = findViewById(R.id.stepBackBtn);
        deviceSStepBtn = findViewById(R.id.deviceSStepBtn);
        stepHelpBtn = findViewById(R.id.stepHelpBtn);
        View.OnClickListener bl = new SStepActivity.ButtonListener();
        setOnClickListener(stepStartBtn, bl);
        setOnClickListener(stepStopBtn, bl);
        setOnClickListener(stepScanBtn, bl);
        setOnClickListener(stepPrintBtn, bl);
        setOnClickListener(stepSaveBtn, bl);
        setOnClickListener(stepExportBtn, bl);
        setOnClickListener(stepBackBtn, bl);
        setOnClickListener(stepExportMapBtn, bl);
        setOnClickListener(stepHelpBtn, bl);
        setButton();

        stepset_txt=(TextView) findViewById(R.id.stepset_txt);

        SharedPreferences shares1 = getSharedPreferences( "stepValue", Activity.MODE_PRIVATE );
        if(shares1.getBoolean("stepValueDecive",false))
        {
            str_StepMAX30=shares1.getString("StepMAX30","");
            str_StepMAX20=shares1.getString("StepMAX20","");
            str_StepMAX20L=shares1.getString("StepMAX20L","");
            str_StepMAX10L=shares1.getString("StepMAX10L","");

            StepMAX30=Float.valueOf(str_StepMAX30);
            StepMAX20=Float.valueOf(str_StepMAX20);
            StepMAX20L=Float.valueOf(str_StepMAX20L);
            StepMAX10L=Float.valueOf(str_StepMAX10L);
            if (sightTypeFlag)//观光列车
            {
                stepset_txt.setText("最大行驶速度20km/h:"+"最大坡度"+str_StepMAX20L+"%;"+"最大行驶速度10km/h:"+"最大坡度"+str_StepMAX10L+"%");
            }else//观光车
            {

                stepset_txt.setText("最大行驶速度30km/h:"+"最大坡度"+str_StepMAX30+"%;"+"最大行驶速度20km/h:"+"最大坡度"+str_StepMAX20+"%");
            }
        }else{
            str_StepMAX30="10";
            str_StepMAX20="15";
            str_StepMAX20L="4";
            str_StepMAX10L="7";

            StepMAX30=10.0f;
            StepMAX20=15.0f;
            StepMAX20L=4.0f;
            StepMAX10L=7.0f;
            if (sightTypeFlag)//观光列车
            {
                stepset_txt.setText("最大行驶速度20km/h:"+"最大坡度"+str_StepMAX20L+"%;"+"最大行驶速度10km/h:"+"最大坡度"+str_StepMAX10L+"%");
            }else//观光车
            {

                stepset_txt.setText( "最大行驶速度30km/h:"+"最大坡度"+str_StepMAX30+"%;"+"最大行驶速度20km/h:"+"最大坡度"+str_StepMAX20+"%");
            }
        }
    }
    /**
     * 添加定位动态权限
     */
    private void getPersimmions() {
        /***
         * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION);
            }
        }
    }
    /**
     *动态权限的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        activate(mListener);         //调用激活定位
    }
    /**
     *根据过去的位置和新的位置绘制移动轨迹路线
     */
    private void setUpMap(LatLng oldData,LatLng newData ) {
        PolylineOptions optionsTemp = new PolylineOptions();
        options.add(oldData,newData);           //设置位置
        //options.clear();
        options.width(5);                      //设置宽度
        //加入对应的颜色,使用colorValues 即表示使用多颜色，使用color表示使用单色线
        options.color(Color.YELLOW);
        // options.colorValues(colorList);
        //加上这个属性，表示使用渐变线
        options.useGradient(true);
        if(colorFlag==true && colorFirstFlag==false)
        {
            optionsMarker.add(optionsTemp);
            optionsMarkerindex=optionsMarkerindex+1;
            colorFirstFlag=true;
            markerFlag=true;
            errLatitude.add(realLat);
            errLongitude.add(realLon);
            str_errLongitude=myformatLat.format(realLon);
            str_errLattitude=myformatLat.format(realLat);
            errDis=Math.abs(Dis);
        }

        if(colorFlag)
        {
            optionsMarker.get(optionsMarkerindex-1).add(oldData,newData);           //设置位置
            optionsMarker.get(optionsMarkerindex-1).width(20);                      //设置宽度
            optionsMarker.get(optionsMarkerindex-1).color(Color.RED);
            optionsMarker.get(optionsMarkerindex-1).useGradient(true);
        }else
        {
            if(markerFlag)
            {
                startDis.add(errDis);
                endDis.add(Dis);
                errDis=Math.abs(Dis-errDis);
                errDisMarker.add(errDis);
                errStepMax.add(stepMax);
                stepMax=0.0f;
            }
            markerFlag=false;
            colorFirstFlag=false;
        }
        aMap.addPolyline(options);              //地图中添加线段
        if(optionsMarker.size()>0)
        {
            aMap.addPolyline(optionsMarker.get(optionsMarkerindex-1));              //地图中添加线段
        }
    }

    private View getMarkerView(String pm_val) {
        View view = getLayoutInflater().inflate(R.layout.location_marker, null);
        TextView tv_val = (TextView) view.findViewById(R.id.map_custom_text);
        tv_val.setText(pm_val);
        return view;
    }
    private void addMarkersToMap() {
        LatLng latLng;
        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
        aMap = mMapView.getMap();             //地图控制

        int markerNum=0;
        markerNum=errLongitude.size();
        if(markerNum>errStepMax.size())
        {
            markerNum=errStepMax.size();
        }
        if(markerNum>errDisMarker.size())
        {
            markerNum=errDisMarker.size();
        }
        if(markerNum>0)
        {
            for (int i = 0; i <markerNum; i++)
            {
                MarkerOptions markerOption = new MarkerOptions();
                latLng = new LatLng(errLatitude.get(i),errLongitude.get(i));
                markerOption.position(latLng);
                markerOption.anchor(0.5f,0.5f);
                markerOption.title(String.valueOf(i));
                markerOption.snippet("default point");
                markerOption.icon(BitmapDescriptorFactory.fromView(getMarkerView("超标坡度："+myformat.format(errStepMax.get(i))+"%"+"超标距离"+myformat.format(errDisMarker.get(i))+"m")));
                markerOptionlst.add(markerOption);
            }
        }

        errStepMarker=aMap.addMarkers(markerOptionlst, true);
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
        if(errStepMarker!=null)
        {
            for(int i=0;i<errStepMarker.size();i++)
            {
                boundsBuilder.include(errStepMarker.get(i).getPosition());//把所有点都include进去（LatLng类型）
            }
        }
        //boundsBuilder.include(mLocMarker.getPosition());//把当前坐标添加进去
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 15));//第二个参数为四周留空宽度*/
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统默认定位图标
                //保存经纬度
                LatLng newLocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                realLat=(float)amapLocation.getLatitude();
                realLon=(float)amapLocation.getLongitude();
                if(!mFirstFix){
                    //记录第一次的定位信息
                    startLatLng=newLocation;
                    oldLocation = newLocation;
                    //显示当前位置信息
                    //Toast.makeText(this, amapLocation.getAddress() + "", Toast.LENGTH_SHORT).show();
                    mFirstFix = true;
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(newLocation.latitude, newLocation.longitude), 16, 0, 0)));
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
                    boundsBuilder.include(newLocation);//把当前坐标添加进去
                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 15));//第二个参数为四周留空宽度*/
                } else{
                  /*  mCircle.setCenter(location);                    //设置圆心的经纬度坐标
                    mCircle.setRadius(amapLocation.getAccuracy());  //设置圆形半径
                    mLocMarker.setPosition(location);               //设置图标经纬度位置*/
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(newLocation));
                }
                // setMarker(newLocation);//添加定位图标
                //位置有变化时
                if(oldLocation != newLocation){
                    //设置新的位置信息
                    if(startFlag)
                    {
                        setUpMap( oldLocation , newLocation );
                    }
                    //记录新的位置信息
                    oldLocation = newLocation;
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                //Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {  //如果定位服务为空
            mlocationClient = new AMapLocationClient(this);     //创建定位服务类
            mLocationOption = new AMapLocationClientOption();   //创建设置定位参数类
            myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色 
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔,单位毫秒,默认为2000ms
            //mLocationOption.setInterval(2000);
            mLocationOption.setOnceLocation(false);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }
    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
    /**
     * 界面获取焦点时同时获取地图焦点
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    /**
     * 界面停止时停止地图，并停止定位
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
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
                case R.id.stepStartBtn: {
                    stepStartBtn.setEnabled(false);
                    stepStartBtnpressed = getResources().getDrawable(R.drawable.start);
                    stepStartBtnpressed.setBounds(0, 0, stepStartBtnpressed.getMinimumWidth(), stepStartBtnpressed.getMinimumHeight());
                    stepStartBtn.setCompoundDrawables(null, stepStartBtnpressed, null, null);
                    for(int i=0;i<3;i++) {
                        senddata[0] = 0x4B;
                        senddata[1] = 0x53;
                        senddata[2] = 0x15;
                        senddata[3] = 0x00;
                        senddata[4] = 0x01;
                        senddata[5] = 0x00;
                        senddata[6] = 0x01;
                        senddata[7] = 0x3A;
                        senddata[8] = 0x3B;
                        //controlClientSteps.send(senddata);
                        mHoldBluetoothStep.sendData(moduleStep,senddata);
                    }
                    mMapView.onResume();
                    aMap.clear();

                    Lattitude=0.0f;
                    Longitude=0.0f;

                    yAngleFirst=0.0f;
                    zAngleFirst=0.0f;
                    LonFirst=0.0f;
                    LatFirst=0.0f;

                    yAngle=0.0f;
                    //zAngle=0.0f;
                    Dis=0.0f;
                    errDis=0.0f;
                    realStep=0.0f;
                    stepMax=0.0f;
                    stepMaxAll=0.0f;

                    realSpeed=0.0f;
                    speedMax=0.0f;

                    markerFlag=false;
                    colorFlag=false;
                    startFlag=true;

                    errLatitude.clear();
                    errLongitude.clear();
                    if(errStepMarker!=null)
                    {
                        errStepMarker.clear();
                    }
                    errDisMarker.clear();
                    errStepMax.clear();
                    startDis.clear();
                    endDis.clear();

                    optionsMarkerindex=0;
                    optionsMarker.clear();
                    str_realStep="";
                    str_stepMax="";
                    str_stepMaxAll="";
                    str_errLongitude="";
                    str_errLattitude="";
                    str_realSpeed="";
                    str_speedMax="";

                    str_yAngle=myformat.format(yAngle);
                   // str_zAngle=myformat.format(zAngle);
                    str_Dis=myformat.format(Dis);
                    str_realStep=myformat.format(realStep);

                    realStep_txt.setText(str_realStep);
                    stepAngleS_txt.setText(str_yAngle);

                    realSpeed_txt.setText(str_realSpeed);
                    speedMax_txt.setText(str_speedMax);
                    //stepAngleHS_txt.setText(str_zAngle);
                    stepDis_txt.setText(str_Dis);

                    realStep_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                    //activate(mListener);         //调用激活定位
                    handler.postDelayed(FirstRunnable,1000);
                    // pd.setMessage("正在读取初始位置");
                    // pd.show();
                }
                break;
                case R.id.stepStopBtn: {
                    Finish = true;
                    stepStartBtn.setEnabled(true);
                    stepStartBtnpressed = getResources().getDrawable(R.drawable.start1);
                    stepStartBtnpressed.setBounds(0, 0, stepStartBtnpressed.getMinimumWidth(), stepStartBtnpressed.getMinimumHeight());
                    stepStartBtn.setCompoundDrawables(null, stepStartBtnpressed, null, null);

                    stepStopBtn.setEnabled(false);
                    stepStopBtnpressed = getResources().getDrawable(R.drawable.stop);
                    stepStopBtnpressed.setBounds(0, 0, stepStopBtnpressed.getMinimumWidth(), stepStopBtnpressed.getMinimumHeight());
                    stepStopBtn.setCompoundDrawables(null, stepStopBtnpressed, null, null);
                    for(int i=0;i<3;i++) {
                        senddata[0] = 0x4B;
                        senddata[1] = 0x53;
                        senddata[2] = 0x15;
                        senddata[3] = 0x00;
                        senddata[4] = 0x01;
                        senddata[5] = 0x00;
                        senddata[6] = 0x05;
                        senddata[7] = 0x3A;
                        senddata[8] = 0x3B;
                        //controlClientSteps.send(senddata);
                        mHoldBluetoothStep.sendData(moduleStep,senddata);
                    }
                    startFlag=false;
                    //mMapView.onPause();
                    colorFlag = false;
                    addMarkersToMap();
                    //deactivate();
                    handler.removeCallbacks (ReceiveRunnable);
                }
                break;
                case R.id.stepScanBtn: {
                    stepScanBtn.setEnabled(false);
                    stepScanBtnpressed = getResources().getDrawable(R.drawable.scan);
                    stepScanBtnpressed.setBounds(0, 0, stepScanBtnpressed.getMinimumWidth(), stepScanBtnpressed.getMinimumHeight());
                    stepScanBtn.setCompoundDrawables(null, stepScanBtnpressed, null, null);
                    Intent intent1 = new Intent(SStepActivity.this, SStepSaveActivity.class);
                    startActivity(intent1);
                    finish();
                }
                break;
                case R.id.stepPrintBtn: {
                    stepPrintBtn.setEnabled(false);
                    stepPrintBtnpressed = getResources().getDrawable(R.drawable.print);
                    stepPrintBtnpressed.setBounds(0, 0,stepPrintBtnpressed.getMinimumWidth(), stepPrintBtnpressed.getMinimumHeight());
                    stepPrintBtn.setCompoundDrawables(null, stepPrintBtnpressed, null, null);
                    if (Finish) {
                        if (printDataService == null) {           //首次连接打印机
                            SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                            if (!shares.getBoolean("BondPrinter", false)) {
                                Toast.makeText(SStepActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SStepActivity.this.getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                        } else {          //打印数据
                            PrintMeasureData();
                        }
                    } else {
                        Toast.makeText(SStepActivity.this, "没有可以打印的数据", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                case R.id.stepSaveBtn: {
                    stepSaveBtn.setEnabled(false);
                    stepSaveBtnpressed = getResources().getDrawable(R.drawable.save);
                    stepSaveBtnpressed.setBounds(0, 0, stepSaveBtnpressed.getMinimumWidth(), stepSaveBtnpressed.getMinimumHeight());
                    stepSaveBtn.setCompoundDrawables(null, stepSaveBtnpressed, null, null);

                    int markerNum=0;
                    markerNum=errLongitude.size();
                    if(markerNum>errStepMax.size())
                    {
                        markerNum=errStepMax.size();
                    }

                    if(markerNum>errDisMarker.size())
                    {
                        markerNum=errDisMarker.size();
                    }
                    if(markerNum>0)
                    {
                        for(int i=0;i<markerNum;i++)
                        {
                            stepAddS(str_Dis,myformatLat.format(errLongitude.get(i)),myformatLat.format(errLatitude.get(i)),myformatLat.format(startDis.get(i)),myformatLat.format(endDis.get(i)),myformatLat.format(errDisMarker.get(i)),myformatLat.format(errStepMax.get(i)));
                        }
                    }else{
                        stepAddS(str_Dis,str_realLongitude,str_realLattitude,"0","0","0",str_stepMaxAll);
                    }
                }
                break;
                case R.id.stepExportBtn: {
                    stepExportBtn.setEnabled(false);
                    stepExportBtnpressed = getResources().getDrawable(R.drawable.export);
                    stepExportBtnpressed.setBounds(0, 0, stepExportBtnpressed.getMinimumWidth(), stepExportBtnpressed.getMinimumHeight());
                    stepExportBtn.setCompoundDrawables(null, stepExportBtnpressed, null, null);

                    // View chartView1 =findViewById(R.id.chartAmplitude);
                    //Bitmap topBitmap =getBitmapFromView(chartView1);
                    //Bitmap  saveBitmap = mergeBitmap_TB(topBitmap, bottomBitmap, true);
                    // saveBitmap(bottomBitmap);
                    CreatePdf();
                    Toast.makeText(SStepActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车观光景区坡度检测报告", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.stepBackBtn: {
                    Intent intent1 = new Intent(SStepActivity.this, SightActivity.class);
                    startActivity(intent1);
                    finish();
                }
                break;
                case R.id.stepHelpBtn: {
                    showControlDialog();
                }
                break;
                case R.id.stepExportMapBtn: {
                    stepExportMapBtn.setEnabled(false);
                    stepExportMapBtnpressed = getResources().getDrawable(R.drawable.map);
                    stepExportMapBtnpressed.setBounds(0, 0, stepExportMapBtnpressed.getMinimumWidth(), stepExportMapBtnpressed.getMinimumHeight());
                    stepExportMapBtn.setCompoundDrawables(null, stepExportMapBtnpressed, null, null);
                    CreateMap();
                }
                break;
                default: {
                }
                break;
            }
            handler.postDelayed(sendRunnable, 3000);
        }
    }
    private void showControlDialog(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(SStepActivity.this);
        normalDialog.setIcon(R.drawable.help1);
        normalDialog.setTitle("TSG 81-2022场(厂)内专用机动车辆安全技术规程");
        normalDialog.setMessage("观光车应当同时符合以下要求：" +
                "\n1、最大运行速度小于等于30km/h时，最大行驶坡度小于等于10%；"+
                "\n2、最大运行速度小于等于20km/h时，最大行驶坡度小于等于15%且大于10%；"+
                "\n观光列车应当同时符合以下要求："+
                "\n1、最大运行速度小于等于20km/h时，最大行驶坡度小于等于4%；"+
                "\n2、最大运行速度小于等于10km/h时，最大行驶坡度小于等于7%且大于4%");
        normalDialog.setPositiveButton("确  定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        normalDialog.show();
    }
    public static Bitmap mergeBitmap_TB(Bitmap topBitmap, Bitmap bottomBitmap, boolean isBaseMax) {

        if (topBitmap == null || topBitmap.isRecycled()
                || bottomBitmap == null || bottomBitmap.isRecycled()) {
            return null;
        }
        int width = 0;
        if (isBaseMax) {
            width = topBitmap.getWidth() > bottomBitmap.getWidth() ? topBitmap.getWidth() : bottomBitmap.getWidth();
        } else {
            width = topBitmap.getWidth() < bottomBitmap.getWidth() ? topBitmap.getWidth() : bottomBitmap.getWidth();
        }
        Bitmap tempBitmapT = topBitmap;
        Bitmap tempBitmapB = bottomBitmap;

        if (topBitmap.getWidth() != width) {
            tempBitmapT = Bitmap.createScaledBitmap(topBitmap, width, (int)(topBitmap.getHeight()*1f/topBitmap.getWidth()*width), false);
        } else if (bottomBitmap.getWidth() != width) {
            tempBitmapB = Bitmap.createScaledBitmap(bottomBitmap, width, (int)(bottomBitmap.getHeight()*1f/bottomBitmap.getWidth()*width), false);
        }

        int height = tempBitmapT.getHeight() + tempBitmapB.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Rect topRect = new Rect(0, 0, tempBitmapT.getWidth(), tempBitmapT.getHeight());
        Rect bottomRect  = new Rect(0, 0, tempBitmapB.getWidth(), tempBitmapB.getHeight());

        Rect bottomRectT  = new Rect(0, tempBitmapT.getHeight(), width, height);

        canvas.drawBitmap(tempBitmapT, topRect, topRect, null);
        canvas.drawBitmap(tempBitmapB, bottomRect, bottomRectT, null);
        return bitmap;
    }
    public static Bitmap getBitmapFromView(View graph) {
        Bitmap returnedBitmap = Bitmap.createBitmap(graph.getWidth(), graph.getHeight(), Bitmap.Config.ARGB_8888);
//        Bitmap returnedBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = graph.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        graph.draw(canvas);
        return returnedBitmap;
    }
    private void saveBitmap(Bitmap bitmap)
    {
        verifyStoragePermissions(SStepActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos;
                try {
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车观光景区坡度检测地图" + File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车观光景区坡度检测地图" + File.separator);
                    }
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车观光景区坡度检测地图" + File.separator + curDate.toString() + ".PNG");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车观光景区坡度检测地图" + File.separator + curDate.toString() + ".PNG");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void setEnableButton()
    {
        stepStartBtn.setEnabled(true);
        stepStartBtnpressed = getResources().getDrawable(R.drawable.start1);
        stepStartBtnpressed.setBounds(0, 0, stepStartBtnpressed.getMinimumWidth(), stepStartBtnpressed.getMinimumHeight());
        stepStartBtn.setCompoundDrawables(null, stepStartBtnpressed, null, null);

        stepStopBtn.setEnabled(true);
        stepStopBtnpressed = getResources().getDrawable(R.drawable.stop1);
        stepStopBtnpressed.setBounds(0, 0, stepStopBtnpressed.getMinimumWidth(), stepStopBtnpressed.getMinimumHeight());
        stepStopBtn.setCompoundDrawables(null, stepStopBtnpressed, null, null);

        stepScanBtn.setEnabled(true);
        stepScanBtnpressed = getResources().getDrawable(R.drawable.scan1);
        stepScanBtnpressed.setBounds(0, 0, stepScanBtnpressed.getMinimumWidth(), stepScanBtnpressed.getMinimumHeight());
        stepScanBtn.setCompoundDrawables(null, stepScanBtnpressed, null, null);

        stepPrintBtn.setEnabled(true);
        stepPrintBtnpressed = getResources().getDrawable(R.drawable.print1);
        stepPrintBtnpressed.setBounds(0, 0,stepPrintBtnpressed.getMinimumWidth(), stepPrintBtnpressed.getMinimumHeight());
        stepPrintBtn.setCompoundDrawables(null, stepPrintBtnpressed, null, null);

        stepSaveBtn.setEnabled(true);
        stepSaveBtnpressed = getResources().getDrawable(R.drawable.save1);
        stepSaveBtnpressed.setBounds(0, 0, stepSaveBtnpressed.getMinimumWidth(), stepSaveBtnpressed.getMinimumHeight());
        stepSaveBtn.setCompoundDrawables(null, stepSaveBtnpressed, null, null);

        stepExportBtn.setEnabled(true);
        stepExportBtnpressed = getResources().getDrawable(R.drawable.export1);
        stepExportBtnpressed.setBounds(0, 0, stepExportBtnpressed.getMinimumWidth(), stepExportBtnpressed.getMinimumHeight());
        stepExportBtn.setCompoundDrawables(null, stepExportBtnpressed, null, null);

        stepExportMapBtn.setEnabled(true);
        stepExportMapBtnpressed = getResources().getDrawable(R.drawable.map1);
        stepExportMapBtnpressed.setBounds(0, 0, stepExportMapBtnpressed.getMinimumWidth(), stepExportMapBtnpressed.getMinimumHeight());
        stepExportMapBtn.setCompoundDrawables(null, stepExportMapBtnpressed, null, null);

    }
    private void setButton()
    {
        stepStartBtn.setEnabled(false);
        stepStartBtnpressed = getResources().getDrawable(R.drawable.start);
        stepStartBtnpressed.setBounds(0, 0, stepStartBtnpressed.getMinimumWidth(), stepStartBtnpressed.getMinimumHeight());
        stepStartBtn.setCompoundDrawables(null, stepStartBtnpressed, null, null);

        stepStopBtn.setEnabled(false);
        stepStopBtnpressed = getResources().getDrawable(R.drawable.stop);
        stepStopBtnpressed.setBounds(0, 0, stepStopBtnpressed.getMinimumWidth(), stepStopBtnpressed.getMinimumHeight());
        stepStopBtn.setCompoundDrawables(null, stepStopBtnpressed, null, null);

        stepPrintBtn.setEnabled(false);
        stepPrintBtnpressed = getResources().getDrawable(R.drawable.print);
        stepPrintBtnpressed.setBounds(0, 0,stepPrintBtnpressed.getMinimumWidth(), stepPrintBtnpressed.getMinimumHeight());
        stepPrintBtn.setCompoundDrawables(null, stepPrintBtnpressed, null, null);

        stepSaveBtn.setEnabled(false);
        stepSaveBtnpressed = getResources().getDrawable(R.drawable.save);
        stepSaveBtnpressed.setBounds(0, 0, stepSaveBtnpressed.getMinimumWidth(), stepSaveBtnpressed.getMinimumHeight());
        stepSaveBtn.setCompoundDrawables(null, stepSaveBtnpressed, null, null);

        stepExportBtn.setEnabled(false);
        stepExportBtnpressed = getResources().getDrawable(R.drawable.export);
        stepExportBtnpressed.setBounds(0, 0, stepExportBtnpressed.getMinimumWidth(), stepExportBtnpressed.getMinimumHeight());
        stepExportBtn.setCompoundDrawables(null, stepExportBtnpressed, null, null);

        stepExportMapBtn.setEnabled(false);
        stepExportMapBtnpressed = getResources().getDrawable(R.drawable.map);
        stepExportMapBtnpressed.setBounds(0, 0, stepExportMapBtnpressed.getMinimumWidth(), stepExportMapBtnpressed.getMinimumHeight());
        stepExportMapBtn.setCompoundDrawables(null, stepExportMapBtnpressed, null, null);
    }

    Runnable FirstRunnable = new Runnable() {
        @Override
        public void run() {
            int StepRevNum=0;
            byte[] mReceiveStep=new byte[17];
            String[] str_ReceiveData = new String[17];
            //stepReceive = controlClientSteps.receive;
            yAngleFirst = 0.0f;
            zAngleFirst = 0.0f;
            if(stepReceive.length>=17) {
                for (int i = 0; i < stepReceive.length; i++) {
                    if (stepReceive[StepRevNum] == 0x4B) {
                        break;
                    } else {
                        StepRevNum++;
                    }
                }
                for (int i = 0; i < 17; i++) {
                    if ((StepRevNum + i) < stepReceive.length) {
                        mReceiveStep[i] = stepReceive[StepRevNum + i];
                    } else {
                        mReceiveStep[i] = stepReceive[StepRevNum + i - stepReceive.length];
                    }
                }
                for (int i = 0; i < 17; i++) {
                    str_ReceiveData[i] = myformat.format(mReceiveStep[i]);
                }

                Log.d("First", "     " + str_ReceiveData[0] + "  " + str_ReceiveData[1] + " " + str_ReceiveData[2] + "  " + str_ReceiveData[3] + " " + str_ReceiveData[4] + "  " + str_ReceiveData[5] + " " +
                        str_ReceiveData[6] + "  " + str_ReceiveData[7] + " " + str_ReceiveData[8] + "  " + str_ReceiveData[9] + " " + str_ReceiveData[10] + "  " + str_ReceiveData[11] + " " +
                        str_ReceiveData[12] + "  " + str_ReceiveData[13] + " " + str_ReceiveData[14] + str_ReceiveData[15] + str_ReceiveData[16] + "  ");

                if ((mReceiveStep[0] == 0x4B) && (mReceiveStep[1] == 0x53)) {
                    switch (mReceiveStep[2]) {
                        case 0x15:
                            int tmp = 0;
                            int tmpLon = 0;
                            int tmpLat = 0;
                            tmp = (char) (mReceiveStep[4] & 0xFF) * 256 + (char) (mReceiveStep[3] & 0xFF);
                            tmp = tmp & 0xFFFF;
                            if (tmp <= 0x7FFF) yAngleFirst = (float) tmp / 32768.0f * 180.0f;
                            else
                                yAngleFirst = (float) (0x10000 - tmp) / 32768.0f * 180.0f * (-1.0f);

                            tmp = (char) (mReceiveStep[6] & 0xFF) * 256 + (char) (mReceiveStep[5] & 0xFF);
                            tmp = tmp & 0xFFFF;
                            if (tmp <= 0x7FFF) zAngleFirst = (float) tmp / 32768.0f * 180.0f;
                            else
                                zAngleFirst = (float) (0x10000 - tmp) / 32768.0f * 180.0f * (-1.0f);

                            tmpLon = (char) (mReceiveStep[10] & 0xFF) * 256 * 256 * 256 + (char) (mReceiveStep[9] & 0xFF) * 256 * 256 + (char) (mReceiveStep[8] & 0xFF) * 256 + (char) (mReceiveStep[7] & 0xFF);
                            tmpLon = tmpLon & 0xFFFFFFFF;
                            LonFirst = tmpLon / 10000000.0f;

                            tmpLat = (char) (mReceiveStep[14] & 0xFF) * 256 * 256 * 256 + (char) (mReceiveStep[13] & 0xFF) * 256 * 256 + (char) (mReceiveStep[12] & 0xFF) * 256 + (char) (mReceiveStep[11] & 0xFF);
                            tmpLat = tmpLat & 0xFFFFFFFF;
                            LatFirst = tmpLat / 10000000.0f;

                            Log.d("First", "     " + "yAngleFirst=" + myformat.format(yAngleFirst) + "  " + "zAngleFirst=" + myformat.format(zAngleFirst) + "  " + "LonFirst=" + myformat.format(LonFirst) + "LatFirst=" + myformat.format(LatFirst));
                            break;
                        default:
                            break;
                    }
                }
                realLongitude_txt.setText(myformatLat.format(LonFirst));
                realLattitude_txt.setText(myformatLat.format(LatFirst));
                if((Math.abs(LonFirst)>0.0f)&&(Math.abs(LatFirst)>0.0f)){
                    Toast.makeText(SStepActivity.this, "初始化完成！", Toast.LENGTH_SHORT).show();
                    handler.removeCallbacks (FirstRunnable);
                    handler.postDelayed(ReceiveRunnable, 100);
                }else{
                    handler.postDelayed(FirstRunnable, 100);
                }
            }
        }
    };

    Runnable ReceiveRunnable = new Runnable() {
        @Override
        public void run() {
            // pd.dismiss();
            int StepRevNum=0;
            byte[] mReceiveStep=new byte[17];
            String[] str_ReceiveData=new String[17];
            //stepReceive=controlClientSteps.receive;
            if(stepReceive.length>=17) {
                for (int i = 0; i < stepReceive.length; i++) {
                    if (stepReceive[StepRevNum] == 0x4B) {
                        break;
                    } else {
                        StepRevNum++;
                    }
                }
                for (int i = 0; i < 17; i++) {
                    if ((StepRevNum + i) < stepReceive.length) {
                        mReceiveStep[i] = stepReceive[StepRevNum + i];
                    } else {
                        mReceiveStep[i] = stepReceive[StepRevNum + i - stepReceive.length];
                    }
                }
                for (int i = 0; i < 17; i++) {
                    str_ReceiveData[i] = myformat.format(mReceiveStep[i]);
                }

                Log.d("sstepActivity", "     " + str_ReceiveData[0] + "  " + str_ReceiveData[1] + " " + str_ReceiveData[2] + "  " + str_ReceiveData[3] + " " + str_ReceiveData[4] + "  " + str_ReceiveData[5] + " " +
                        str_ReceiveData[6] + "  " + str_ReceiveData[7] + " " + str_ReceiveData[8] + "  " + str_ReceiveData[9] + " " + str_ReceiveData[10] + "  " + str_ReceiveData[11] + " " +
                        str_ReceiveData[12] + "  " + str_ReceiveData[13] + " " + str_ReceiveData[14] + "  " + str_ReceiveData[15] + "  " + str_ReceiveData[16]);
                if ((mReceiveStep[0] == 0x4B) && (mReceiveStep[1] == 0x53)) {
                    switch (mReceiveStep[2]) {
                        case 0x15:
                            int tmp = 0;
                            int tmpLon = 0;
                            int tmpLat = 0;
                            tmp = (char) (mReceiveStep[4] & 0xFF) * 256 + (char) (mReceiveStep[3] & 0xFF);
                            tmp = tmp & 0xFFFF;
                            if (tmp <= 0x7FFF) yAngle = (float) tmp / 32768.0f * 180.0f;
                            else yAngle = (float) (0x10000 - tmp) / 32768.0f * 180.0f * (-1.0f);

                            tmp = (char) (mReceiveStep[15] & 0xFF) * 256 + (char) (mReceiveStep[16] & 0xFF);
                            tmp = tmp & 0xFFFF;
                            realSpeed = (float) tmp / 1000.0f;
                           // else realSpeed = (float) (0x10000 - tmp) / 1000.0f * (-1.0f);

                            tmpLon = (char) (mReceiveStep[10] & 0xFF) * 256 * 256 * 256 + (char) (mReceiveStep[9] & 0xFF) * 256 * 256 + (char) (mReceiveStep[8] & 0xFF) * 256 + (char) (mReceiveStep[7] & 0xFF);
                            tmpLon = tmpLon & 0xFFFFFFFF;
                            Longitude = tmpLon / 10000000.0f;

                            tmpLat = (char) (mReceiveStep[14] & 0xFF) * 256 * 256 * 256 + (char) (mReceiveStep[13] & 0xFF) * 256 * 256 + (char) (mReceiveStep[12] & 0xFF) * 256 + (char) (mReceiveStep[11] & 0xFF);
                            tmpLat = tmpLat & 0xFFFFFFFF;
                            Lattitude = tmpLat / 10000000.0f;

                            Log.d("SStepActivity", "     " + "yAngle=" + myformat.format(yAngle) + "  " + "  " + "Longitude=" + myformat.format(Longitude) + "Lattitude=" + myformat.format(Lattitude));
                            break;
                        default:
                            break;
                    }
                }
                yAngle = yAngle - yAngleFirst;
                str_realLongitude = myformatLat.format(Longitude);
                str_realLattitude = myformatLat.format(Lattitude);
                realLongitude_txt.setText(str_realLongitude);
                realLattitude_txt.setText(str_realLattitude);

                Dis = Dis +realSpeed*0.0168f/3.6f;
                realStep = (float) Math.sin(yAngle * PI / 180.00f) / (float) Math.cos(yAngle * PI / 180.00f) * 100;
                dialPlateView.upDataValues(realStep);//更新控件显示

                if (stepMax < Math.abs(realStep)) {
                    stepMax = Math.abs(realStep);
                }
                if (stepMaxAll < Math.abs(realStep)) {
                    stepMaxAll = Math.abs(realStep);
                }

                if (speedMax < Math.abs(realSpeed)) {
                    speedMax = Math.abs(realSpeed);
                }

                str_stepMax = myformat.format(stepMax);
                str_stepMaxAll = myformat.format(stepMaxAll);
                str_realStep = myformat.format(realStep);
                str_yAngle = myformat.format(yAngle);
                str_Dis = myformat.format(Dis);
                str_realSpeed = myformat.format(realSpeed);
                str_speedMax = myformat.format(speedMax);

                realStep_txt.setText(str_realStep);
                stepAngleS_txt.setText(str_yAngle);
                stepDis_txt.setText(str_Dis);

                realSpeed_txt.setText(str_realSpeed);
                speedMax_txt.setText(str_speedMax);

                realLongitude_txt.setText(str_realLongitude);
                realLattitude_txt.setText(str_realLattitude);

                if (sightTypeFlag)//观光列车
                {
                    if((Math.abs(realStep)>=StepMAX10L)&&(Math.abs(realSpeed)<=10)){
                        colorFlag = true;
                        realStep_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                    }else if ((Math.abs(realStep)>=StepMAX20L)&&(Math.abs(realSpeed)<=20))
                    {
                        colorFlag = true;
                        realStep_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                   } else {
                        colorFlag = false;
                        realStep_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                    }
                } else//观光车
                    {
                         if((Math.abs(realStep)>=StepMAX20)&&(Math.abs(realSpeed)<=20)){
                            colorFlag = true;
                            realStep_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                        }else if ((Math.abs(realStep)>=StepMAX30)&&(Math.abs(realSpeed)<=30))
                        {
                            colorFlag = true;
                            realStep_txt.setTextColor(Color.parseColor("#ff0101"));//设置颜色红色;
                         } else {
                            colorFlag = false;
                            realStep_txt.setTextColor(Color.parseColor("#ffffff"));//设置颜色白色;
                        }
                   }
                }
            handler.postDelayed(ReceiveRunnable,10);
        }
    };
    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            stepStopBtn.setEnabled(true);
            stepStopBtnpressed = getResources().getDrawable(R.drawable.stop1);
            stepStopBtnpressed.setBounds(0, 0, stepStopBtnpressed.getMinimumWidth(), stepStopBtnpressed.getMinimumHeight());
            stepStopBtn.setCompoundDrawables(null, stepStopBtnpressed, null, null);

            stepScanBtn.setEnabled(true);
            stepScanBtnpressed = getResources().getDrawable(R.drawable.scan1);
            stepScanBtnpressed.setBounds(0, 0, stepScanBtnpressed.getMinimumWidth(), stepScanBtnpressed.getMinimumHeight());
            stepScanBtn.setCompoundDrawables(null, stepScanBtnpressed, null, null);

            stepPrintBtn.setEnabled(true);
            stepPrintBtnpressed = getResources().getDrawable(R.drawable.print1);
            stepPrintBtnpressed.setBounds(0, 0,stepPrintBtnpressed.getMinimumWidth(), stepPrintBtnpressed.getMinimumHeight());
            stepPrintBtn.setCompoundDrawables(null, stepPrintBtnpressed, null, null);

            stepSaveBtn.setEnabled(true);
            stepSaveBtnpressed = getResources().getDrawable(R.drawable.save1);
            stepSaveBtnpressed.setBounds(0, 0, stepSaveBtnpressed.getMinimumWidth(), stepSaveBtnpressed.getMinimumHeight());
            stepSaveBtn.setCompoundDrawables(null, stepSaveBtnpressed, null, null);

            stepExportBtn.setEnabled(true);
            stepExportBtnpressed = getResources().getDrawable(R.drawable.export1);
            stepExportBtnpressed.setBounds(0, 0, stepExportBtnpressed.getMinimumWidth(), stepExportBtnpressed.getMinimumHeight());
            stepExportBtn.setCompoundDrawables(null, stepExportBtnpressed, null, null);

            stepExportMapBtn.setEnabled(true);
            stepExportMapBtnpressed = getResources().getDrawable(R.drawable.map1);
            stepExportMapBtnpressed.setBounds(0, 0, stepExportMapBtnpressed.getMinimumWidth(), stepExportMapBtnpressed.getMinimumHeight());
            stepExportMapBtn.setCompoundDrawables(null, stepExportMapBtnpressed, null, null);
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
                    printDataService = new PrintDataService(SStepActivity.this,shares.getString("Printer",""));
                    //Toast.makeText(overActivity.this,"蓝牙打印机连接中...",Toast.LENGTH_LONG).show();
                }
                if(printDataService != null){
                    PrintConnect = printDataService.connect();
                    if(PrintConnect){
                        Toast.makeText(SStepActivity.this,"蓝牙打印机连接成功...",Toast.LENGTH_LONG).show();
                        handler.removeCallbacks (PrinterRunnable);
                    }
                }
                handler.postDelayed(PrinterRunnable,100);
            }
        }
    };
    //打印测试数据
    private void PrintMeasureData(){
        int markerNum=0;
        markerNum=errLongitude.size();
        if(markerNum>errStepMax.size())
        {
            markerNum=errStepMax.size();
        }

        if(markerNum>errDisMarker.size())
        {
            markerNum=errDisMarker.size();
        }
        printDataService.send("\n*******************************\n");
        printDataService.send("观光车辆/观光列车观光景区坡度检测结果");
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
        printDataService.send("距离值"+": "+ str_Dis+"m"+"\n");//

        if(markerNum>0)
        {
            for(int i=0;i<markerNum;i++)
            {
                printDataService.send("超标坡度起始经度值"+": "+ myformatLat.format(errLongitude.get(i))+"°"+"\n");//
                printDataService.send("超标坡度起始纬度值"+": "+ myformatLat.format(errLatitude.get(i))+"°"+"\n");//
                printDataService.send("超标坡度起点距离值"+": "+ myformatLat.format(startDis.get(i))+"m"+"\n");//
                printDataService.send("超标坡度终点距离值"+": "+ myformatLat.format(endDis.get(i))+"m"+"\n");//
                printDataService.send("超标距离值"+": "+ myformatLat.format(errDisMarker.get(i))+"m"+"\n");//
                printDataService.send("最大超标坡度值"+": "+ myformatLat.format(errStepMax.get(i))+"%"+"\n");//
            }
        }else{
            printDataService.send("最大坡度值"+": "+ str_stepMaxAll+"%"+"\n");//
        }
        printDataService.send("*******************************\n\n\n\n");
        Toast.makeText(SStepActivity.this,"打印完成！",Toast.LENGTH_SHORT).show();
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
        SStepActivity.this.sendBroadcast(intent);
    }
    //创建PDF文件-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(SStepActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    int markerNum=0;
                    markerNum=errLongitude.size();
                    if(markerNum>errStepMax.size())
                    {
                        markerNum=errStepMax.size();
                    }

                    if(markerNum>errDisMarker.size())
                    {
                        markerNum=errDisMarker.size();
                    }
                    //创建目录
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车观光景区坡度检测报告"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车观光景区坡度检测报告"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车观光景区坡度检测报告" + File.separator + curDate.toString ()+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    pdfcontext = new Paragraph("观光车辆/观光列车观光景区坡度检测报告",setChineseTitleFont());
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

                    cell.setPhrase(new Phrase("距离值",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_Dis,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("m ",setChineseFont()))    ;mtable.addCell(cell);

                    if(markerNum>0)
                    {
                        for(int i=0;i<markerNum;i++)
                        {
                            cell.setPhrase(new Phrase("超标坡度起始经度值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(myformatLat.format(errLongitude.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("超标坡度起始纬度值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(myformatLat.format(errLatitude.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("超标坡度起点距离值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(myformatLat.format(startDis.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("超标坡度终点距离值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(myformatLat.format(endDis.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("超标距离值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(myformatLat.format(errDisMarker.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("最大超标坡度值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(myformatLat.format(errStepMax.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("%",setChineseFont()))    ;mtable.addCell(cell);
                        }
                    }else{
                        cell.setPhrase(new Phrase("最大坡度值：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(str_stepMaxAll,setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("%",setChineseFont()))    ;mtable.addCell(cell);
                    }
                    doc.add(mtable);
                    doc.close();
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车观光景区坡度检测报告" +  File.separator + curDate.toString () +".pdf");
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
    private void CreateMap()
    {
        verifyStoragePermissions(SStepActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * 对地图进行截屏
                 */
                aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
                    @Override
                    public void onMapScreenShot(Bitmap bitmap) {

                    }

                    @Override
                    public void onMapScreenShot(Bitmap bitmap, int status) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        if(null == bitmap){
                            return;
                        }
                        FileOutputStream fos;
                        try {
                            File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车观光景区坡度检测地图" + File.separator);
                            if (!destDir.exists()) {
                                destDir.mkdirs();
                                notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车观光景区坡度检测地图" + File.separator);
                            }
                            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                            fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车观光景区坡度检测地图" + File.separator + curDate.toString() + ".PNG");
                            boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.flush();
                            fos.close();
                            notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车观光景区坡度检测地图" + File.separator + curDate.toString() + ".PNG");
                            StringBuffer buffer = new StringBuffer();
                            if (b)
                                buffer.append("截屏成功 ");
                            else {
                                buffer.append("截屏失败 ");
                            }
                            if (status != 0)
                                buffer.append("地图渲染完成，截屏无网格");
                            else {
                                buffer.append( "地图未渲染完成，截屏有网格");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            fontChinese = new Font(bf, 20, Font.NORMAL);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fontChinese;
    }
    /**
     * 界面销毁时销毁地图并销毁定位服务
     */
    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        super.onDestroy();
        mMapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
        if(deviceStep != null)
        {
            mHoldBluetoothStep.disconnect(moduleStep);
        }
    }
}
