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
    private final String CONNECTED = "?????????",CONNECTING = "?????????",DISCONNECT = "?????????";

    private AMap aMap;                            //???????????????
    private MapView mMapView;                    //??????????????????
    private LatLng oldLocation;                 //???????????????
    private LatLng startLatLng;
    private LatLng centerLatLng;
    private boolean mFirstFix;                  //???????????????????????????
    PolylineOptions options;                   //???????????????
    private int optionsMarkerindex=0;         //???????????????????????????
    List<PolylineOptions> optionsMarker;      //???????????????
    private ArrayList<Marker> errStepMarker=new ArrayList<Marker> ();
    private ArrayList<Float> errLatitude=new ArrayList<Float>();
    private ArrayList<Float> errLongitude=new ArrayList<Float>();
    private ArrayList<Float> startDis=new ArrayList<Float>();
    private ArrayList<Float> endDis=new ArrayList<Float>();
    private ArrayList<Float> errDisMarker=new ArrayList<Float>();
    private ArrayList<Float> errStepMax=new ArrayList<Float>();
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;           //???????????????
    private AMapLocationClientOption mLocationOption;     //??????????????????
    private MyLocationStyle myLocationStyle;
    private final int SDK_PERMISSION = 1;                //????????????
    private SensorEventHelper mSensorHelper;             //????????????????????????????????????

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
    private DialPlateView dialPlateView;//???????????????

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
        //????????????????????????
        mMapView = (MapView) findViewById(R.id.map);      //????????????????????????
        mMapView.onCreate(savedInstanceState);
        options = new PolylineOptions();                  //??????????????????
        optionsMarker= new ArrayList<PolylineOptions>();  //??????????????????
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
        getPersimmions();                               //????????????????????????
        handler.postDelayed(BleRunnable,2000);
    }
    //??????????????????????????????
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
                        setStepState(CONNECTED);//??????????????????
                        Log.d("SStepActivity","Step?????????????????????");
                    }
                }
            }
            @Override
            public void errorDisconnect(final DeviceModule deviceModule) {//??????????????????

                if(deviceModule.getMac().equals(deviceStep.getAddress()))
                {
                    setStepState(DISCONNECT);//??????????????????
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
            case CONNECTED://????????????
                statusSStep_txt.setText("?????????");
                deviceSStepBtnpressed = getResources().getDrawable(R.drawable.btle_connected);
                deviceSStepBtnpressed.setBounds(0, 0, deviceSStepBtnpressed.getMinimumWidth(), deviceSStepBtnpressed.getMinimumHeight());
                deviceSStepBtn.setCompoundDrawables(null, deviceSStepBtnpressed, null, null);
                setEnableButton();
                break;
            case CONNECTING://?????????
                statusSStep_txt.setText("?????????");
                deviceSStepBtnpressed = getResources().getDrawable(R.drawable.btle_disconnected);
                deviceSStepBtnpressed.setBounds(0, 0, deviceSStepBtnpressed.getMinimumWidth(), deviceSStepBtnpressed.getMinimumHeight());
                deviceSStepBtn.setCompoundDrawables(null, deviceSStepBtnpressed, null, null);
                break;
            case DISCONNECT://????????????
                statusSStep_txt.setText("??????");
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
                    Toast.makeText(SStepActivity.this,"????????????????????????",Toast.LENGTH_LONG).show();
                }else{
                    DeviceModule deviceModuleStep = new DeviceModule(deviceStep.getName(),deviceStep);
                    moduleStep= deviceModuleStep;
                    mHoldBluetoothStep.connect(moduleStep);
                    //controlClientBrake = SocketThread.getClient(deviceBrake);
                    Log.d("mHoldBluetoothStep","??????????????????");
                }
            }
        }
    };
    private void initMembers() {
        if (aMap == null) {
            aMap = mMapView.getMap();                  //????????????
        }
        //??????
        aMap.setLocationSource(this);// ??????????????????
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// ????????????????????????????????????
        aMap.setMyLocationEnabled(true);// ?????????true??????????????????????????????????????????false??????????????????????????????????????????????????????false
        // ???????????????????????????????????? ?????????????????? LOCATION_TYPE_LOCATE????????? LOCATION_TYPE_MAP_FOLLOW ????????????????????????????????? LOCATION_TYPE_MAP_ROTATE
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);

        mSensorHelper = new SensorEventHelper(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();     //???????????????????????????
        }
        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_sightNumber= sight_Input.getsightNumber();
        str_sightLenght= sight_Input.getsightLenght();
        str_sightType= sight_Input.getsightType();
        str_sightLoad= sight_Input.getsightLoad();

        if(str_sightType.equals("????????????"))
        {
            sightTypeFlag=false;
        }else if(str_sightType.equals("????????????"))
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
            if (sightTypeFlag)//????????????
            {
                stepset_txt.setText("??????????????????20km/h:"+"????????????"+str_StepMAX20L+"%;"+"??????????????????10km/h:"+"????????????"+str_StepMAX10L+"%");
            }else//?????????
            {

                stepset_txt.setText("??????????????????30km/h:"+"????????????"+str_StepMAX30+"%;"+"??????????????????20km/h:"+"????????????"+str_StepMAX20+"%");
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
            if (sightTypeFlag)//????????????
            {
                stepset_txt.setText("??????????????????20km/h:"+"????????????"+str_StepMAX20L+"%;"+"??????????????????10km/h:"+"????????????"+str_StepMAX10L+"%");
            }else//?????????
            {

                stepset_txt.setText( "??????????????????30km/h:"+"????????????"+str_StepMAX30+"%;"+"??????????????????20km/h:"+"????????????"+str_StepMAX20+"%");
            }
        }
    }
    /**
     * ????????????????????????
     */
    private void getPersimmions() {
        /***
         * ??????????????????????????????????????????????????????????????????????????????
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            // ??????????????????
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
     *???????????????????????????
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        activate(mListener);         //??????????????????
    }
    /**
     *????????????????????????????????????????????????????????????
     */
    private void setUpMap(LatLng oldData,LatLng newData ) {
        PolylineOptions optionsTemp = new PolylineOptions();
        options.add(oldData,newData);           //????????????
        //options.clear();
        options.width(5);                      //????????????
        //?????????????????????,??????colorValues ?????????????????????????????????color?????????????????????
        options.color(Color.YELLOW);
        // options.colorValues(colorList);
        //??????????????????????????????????????????
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
            optionsMarker.get(optionsMarkerindex-1).add(oldData,newData);           //????????????
            optionsMarker.get(optionsMarkerindex-1).width(20);                      //????????????
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
        aMap.addPolyline(options);              //?????????????????????
        if(optionsMarker.size()>0)
        {
            aMap.addPolyline(optionsMarker.get(optionsMarkerindex-1));              //?????????????????????
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
        aMap = mMapView.getMap();             //????????????

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
                markerOption.icon(BitmapDescriptorFactory.fromView(getMarkerView("???????????????"+myformat.format(errStepMax.get(i))+"%"+"????????????"+myformat.format(errDisMarker.get(i))+"m")));
                markerOptionlst.add(markerOption);
            }
        }

        errStepMarker=aMap.addMarkers(markerOptionlst, true);
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//???????????????????????????
        if(errStepMarker!=null)
        {
            for(int i=0;i<errStepMarker.size();i++)
            {
                boundsBuilder.include(errStepMarker.get(i).getPosition());//???????????????include?????????LatLng?????????
            }
        }
        //boundsBuilder.include(mLocMarker.getPosition());//???????????????????????????
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 15));//????????????????????????????????????*/
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// ??????????????????????????????
                //???????????????
                LatLng newLocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                realLat=(float)amapLocation.getLatitude();
                realLon=(float)amapLocation.getLongitude();
                if(!mFirstFix){
                    //??????????????????????????????
                    startLatLng=newLocation;
                    oldLocation = newLocation;
                    //????????????????????????
                    //Toast.makeText(this, amapLocation.getAddress() + "", Toast.LENGTH_SHORT).show();
                    mFirstFix = true;
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(newLocation.latitude, newLocation.longitude), 16, 0, 0)));
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//???????????????????????????
                    boundsBuilder.include(newLocation);//???????????????????????????
                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 15));//????????????????????????????????????*/
                } else{
                  /*  mCircle.setCenter(location);                    //??????????????????????????????
                    mCircle.setRadius(amapLocation.getAccuracy());  //??????????????????
                    mLocMarker.setPosition(location);               //???????????????????????????*/
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(newLocation));
                }
                // setMarker(newLocation);//??????????????????
                //??????????????????
                if(oldLocation != newLocation){
                    //????????????????????????
                    if(startFlag)
                    {
                        setUpMap( oldLocation , newLocation );
                    }
                    //????????????????????????
                    oldLocation = newLocation;
                }
            } else {
                String errText = "????????????," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                //Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * ????????????
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {  //????????????????????????
            mlocationClient = new AMapLocationClient(this);     //?????????????????????
            mLocationOption = new AMapLocationClientOption();   //???????????????????????????
            myLocationStyle = new MyLocationStyle();//??????????????????????????????
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// ?????????????????????????????
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// ???????????????????????????
            //??????????????????
            mlocationClient.setLocationListener(this);
            //??????????????????????????????
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //??????????????????,????????????,?????????2000ms
            //mLocationOption.setInterval(2000);
            mLocationOption.setOnceLocation(false);
            //??????????????????
            mlocationClient.setLocationOption(mLocationOption);
            // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
            // ??????????????????????????????????????????????????????????????????2000ms?????????????????????????????????stopLocation()???????????????????????????
            // ???????????????????????????????????????????????????onDestroy()??????
            // ?????????????????????????????????????????????????????????????????????stopLocation()???????????????????????????sdk???????????????
            mlocationClient.startLocation();
        }
    }
    /**
     * ????????????
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
     * ?????????????????????????????????????????????
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    /**
     * ?????????????????????????????????????????????
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    /**
     * ??????????????????
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

                    realStep_txt.setTextColor(Color.parseColor("#ffffff"));//??????????????????;
                    //activate(mListener);         //??????????????????
                    handler.postDelayed(FirstRunnable,1000);
                    // pd.setMessage("????????????????????????");
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
                        if (printDataService == null) {           //?????????????????????
                            SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                            if (!shares.getBoolean("BondPrinter", false)) {
                                Toast.makeText(SStepActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SStepActivity.this.getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            handler.postDelayed(PrinterRunnable, 100);          //??????????????????????????????
                        } else {          //????????????
                            PrintMeasureData();
                        }
                    } else {
                        Toast.makeText(SStepActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SStepActivity.this, "?????????????????????????????????/Documents/????????????/??????????????????????????????????????????", Toast.LENGTH_SHORT).show();
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
        normalDialog.setTitle("TSG 81-2022???(???)???????????????????????????????????????");
        normalDialog.setMessage("??????????????????????????????????????????" +
                "\n1?????????????????????????????????30km/h????????????????????????????????????10%???"+
                "\n2?????????????????????????????????20km/h????????????????????????????????????15%?????????10%???"+
                "\n?????????????????????????????????????????????"+
                "\n1?????????????????????????????????20km/h????????????????????????????????????4%???"+
                "\n2?????????????????????????????????10km/h????????????????????????????????????7%?????????4%");
        normalDialog.setPositiveButton("???  ???",
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
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/??????????????????????????????????????????" + File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/??????????????????????????????????????????" + File.separator);
                    }
                    Date curDate = new Date(System.currentTimeMillis());//??????????????????
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/??????????????????????????????????????????" + File.separator + curDate.toString() + ".PNG");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/??????????????????????????????????????????" + File.separator + curDate.toString() + ".PNG");
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
                    Toast.makeText(SStepActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
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
                dialPlateView.upDataValues(realStep);//??????????????????

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

                if (sightTypeFlag)//????????????
                {
                    if((Math.abs(realStep)>=StepMAX10L)&&(Math.abs(realSpeed)<=10)){
                        colorFlag = true;
                        realStep_txt.setTextColor(Color.parseColor("#ff0101"));//??????????????????;
                    }else if ((Math.abs(realStep)>=StepMAX20L)&&(Math.abs(realSpeed)<=20))
                    {
                        colorFlag = true;
                        realStep_txt.setTextColor(Color.parseColor("#ff0101"));//??????????????????;
                   } else {
                        colorFlag = false;
                        realStep_txt.setTextColor(Color.parseColor("#ffffff"));//??????????????????;
                    }
                } else//?????????
                    {
                         if((Math.abs(realStep)>=StepMAX20)&&(Math.abs(realSpeed)<=20)){
                            colorFlag = true;
                            realStep_txt.setTextColor(Color.parseColor("#ff0101"));//??????????????????;
                        }else if ((Math.abs(realStep)>=StepMAX30)&&(Math.abs(realSpeed)<=30))
                        {
                            colorFlag = true;
                            realStep_txt.setTextColor(Color.parseColor("#ff0101"));//??????????????????;
                         } else {
                            colorFlag = false;
                            realStep_txt.setTextColor(Color.parseColor("#ffffff"));//??????????????????;
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
            if(PrintConnect){         //???????????????????????????????????????????????????
                handler.removeCallbacks(PrinterRunnable);
                PrintConnect = false;
            }else{
                SharedPreferences shares = getSharedPreferences( "BLE_Info", Activity.MODE_PRIVATE );
                if(shares.getBoolean("BondPrinter",false))
                {
                    printDataService = new PrintDataService(SStepActivity.this,shares.getString("Printer",""));
                    //Toast.makeText(overActivity.this,"????????????????????????...",Toast.LENGTH_LONG).show();
                }
                if(printDataService != null){
                    PrintConnect = printDataService.connect();
                    if(PrintConnect){
                        Toast.makeText(SStepActivity.this,"???????????????????????????...",Toast.LENGTH_LONG).show();
                        handler.removeCallbacks (PrinterRunnable);
                    }
                }
                handler.postDelayed(PrinterRunnable,100);
            }
        }
    };
    //??????????????????
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
        printDataService.send("????????????/??????????????????????????????????????????");
        printDataService.send("\n*******************************\n");
        SimpleDateFormat formatter   =   new SimpleDateFormat("????????????"+":yyyy-MM-dd  HH:mm:ss\n", Locale.CHINA);
        Date curDate =  new Date(System.currentTimeMillis());//??????????????????
        String   str   =   formatter.format(curDate);
        printDataService.send(str);
        printDataService.send("????????????"+": "+str_company+"\n");//
        printDataService.send("????????????"+": "+ str_number+"\n");//
        printDataService.send("????????????"+": "+ str_sightNumber+"\n");//
        printDataService.send("?????????"+": "+ str_sightLenght+"m"+"\n");//
        printDataService.send("????????????"+": "+ str_sightType+"\n");//
        printDataService.send("????????????"+": "+ str_sightLoad+"\n");//
        printDataService.send("?????????"+": "+ str_Dis+"m"+"\n");//

        if(markerNum>0)
        {
            for(int i=0;i<markerNum;i++)
            {
                printDataService.send("???????????????????????????"+": "+ myformatLat.format(errLongitude.get(i))+"??"+"\n");//
                printDataService.send("???????????????????????????"+": "+ myformatLat.format(errLatitude.get(i))+"??"+"\n");//
                printDataService.send("???????????????????????????"+": "+ myformatLat.format(startDis.get(i))+"m"+"\n");//
                printDataService.send("???????????????????????????"+": "+ myformatLat.format(endDis.get(i))+"m"+"\n");//
                printDataService.send("???????????????"+": "+ myformatLat.format(errDisMarker.get(i))+"m"+"\n");//
                printDataService.send("?????????????????????"+": "+ myformatLat.format(errStepMax.get(i))+"%"+"\n");//
            }
        }else{
            printDataService.send("???????????????"+": "+ str_stepMaxAll+"%"+"\n");//
        }
        printDataService.send("*******************************\n\n\n\n");
        Toast.makeText(SStepActivity.this,"???????????????",Toast.LENGTH_SHORT).show();
    }

    //????????????
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

    public void notifySystemToScan(String filePath) {         //???????????????????????????????????????
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        SStepActivity.this.sendBroadcast(intent);
    }
    //??????PDF??????-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(SStepActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// ????????????document??????
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
                    //????????????
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/??????????????????????????????????????????"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/??????????????????????????????????????????"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//??????????????????
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/??????????????????????????????????????????" + File.separator + curDate.toString ()+".pdf"); // pdf_address???Pdf???????????????sd????????????
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    pdfcontext = new Paragraph("????????????/??????????????????????????????????????????",setChineseTitleFont());
                    pdfcontext.setAlignment(Element.ALIGN_CENTER);
                    doc.add(pdfcontext);
                    pdfcontext = new Paragraph("\n\r");
                    pdfcontext.setLeading(3);
                    doc.add(pdfcontext);
                    //???????????????3????????????

                    //???????????????3????????????
                    PdfPTable table = new PdfPTable(3);
                    table.setWidthPercentage(99);
                    //????????????????????????
                    PdfPCell cell = new PdfPCell();
                    cell.setMinimumHeight(20);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPTable mtable = new PdfPTable(3);
                    mtable.setWidthPercentage(99);
                    mtable.setWidths(new float[]{300,200,200});
                    cell.setColspan(1);
                    cell.setBackgroundColor(new BaseColor(255,255,255));
                    cell.setPhrase(new Phrase("???????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(curDate.toString (),setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("???????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_company,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("???????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_number,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("???????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_sightNumber,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_sightLenght,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(" m",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("???????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_sightType,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("???????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_sightLoad,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("?????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_Dis,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("m ",setChineseFont()))    ;mtable.addCell(cell);

                    if(markerNum>0)
                    {
                        for(int i=0;i<markerNum;i++)
                        {
                            cell.setPhrase(new Phrase("??????????????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(myformatLat.format(errLongitude.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("??",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("??????????????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(myformatLat.format(errLatitude.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("??",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("??????????????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(myformatLat.format(startDis.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("??????????????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(myformatLat.format(endDis.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("??????????????????",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(myformatLat.format(errDisMarker.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("????????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(myformatLat.format(errStepMax.get(i)),setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("%",setChineseFont()))    ;mtable.addCell(cell);
                        }
                    }else{
                        cell.setPhrase(new Phrase("??????????????????",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(str_stepMaxAll,setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("%",setChineseFont()))    ;mtable.addCell(cell);
                    }
                    doc.add(mtable);
                    doc.close();
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/??????????????????????????????????????????" +  File.separator + curDate.toString () +".pdf");
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
                 * ?????????????????????
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
                            File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/??????????????????????????????????????????" + File.separator);
                            if (!destDir.exists()) {
                                destDir.mkdirs();
                                notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/??????????????????????????????????????????" + File.separator);
                            }
                            Date curDate = new Date(System.currentTimeMillis());//??????????????????
                            fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/??????????????????????????????????????????" + File.separator + curDate.toString() + ".PNG");
                            boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.flush();
                            fos.close();
                            notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/??????????????????????????????????????????" + File.separator + curDate.toString() + ".PNG");
                            StringBuffer buffer = new StringBuffer();
                            if (b)
                                buffer.append("???????????? ");
                            else {
                                buffer.append("???????????? ");
                            }
                            if (status != 0)
                                buffer.append("????????????????????????????????????");
                            else {
                                buffer.append( "???????????????????????????????????????");
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
     * ??????PDF??????(????????????)
     */
    public Font setChineseFont() {
        BaseFont bf = null;
        Font fontChinese = null;
        try {
            // STSong-Light : Adobe?????????
            // UniGB-UCS2-H : pdf ??????
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
            // STSong-Light : Adobe?????????
            // UniGB-UCS2-H : pdf ??????
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
     * ????????????????????????????????????????????????
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
