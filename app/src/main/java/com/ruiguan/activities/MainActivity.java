package com.ruiguan.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hc.basiclibrary.dialog.CommonDialog;
import com.hc.basiclibrary.ioc.ViewById;
import com.hc.basiclibrary.permission.PermissionUtil;
import com.hc.basiclibrary.recyclerAdapterBasic.ItemClickListener;
import com.hc.basiclibrary.titleBasic.DefaultNavigationBar;
import com.hc.basiclibrary.viewBasic.BasActivity;
import com.hc.bluetoothlibrary.DeviceModule;
import com.ruiguan.R;
import com.ruiguan.activities.single.HoldBluetooth;
import com.ruiguan.activities.tool.Analysis;
import com.ruiguan.chache.Break.BreakActivity;
import com.ruiguan.customView.PopWindowMain;
import com.ruiguan.customView.dialog.PermissionHint;
import com.ruiguan.recyclerData.MainRecyclerAdapter;
import com.ruiguan.storage.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 * @data: 2021-04-02
 * @version: V1.8
 * @update: 更改蓝牙传输和连接方式
 */
public class MainActivity extends BasActivity {
    @ViewById(R.id.main_swipe)
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @ViewById(R.id.main_back_not)
    private LinearLayout mNotBluetooth;

    @ViewById(R.id.main_recycler)
    private RecyclerView mRecyclerView;
    private MainRecyclerAdapter mainRecyclerAdapter;
    private DefaultNavigationBar mTitle;
    private Storage mStorage;
    private List<DeviceModule> mModuleArray = new ArrayList<>();
    private List<DeviceModule> mFilterModuleArray = new ArrayList<>();

    private HoldBluetooth mHoldBluetooth;
    private int mStartDebug = 1;

    private Button connectBtn;
    private Button exitMBtn;
    private Drawable connectBtnpressed;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置头部
        setTitle();
        setContext(this);
    }

    @Override
    public void initAll() {

        mStorage = new Storage(this);//sp存储
        connectBtn=findViewById(R.id.connectBtn);
        exitMBtn=findViewById(R.id.exitMBtn);
        View.OnClickListener bl = new MainActivity.ButtonListener();
        setOnClickListener(connectBtn, bl);
        setOnClickListener(exitMBtn, bl);

        connectBtn.setEnabled(true);
        connectBtnpressed = getResources().getDrawable(R.drawable.start1);
        connectBtnpressed.setBounds(0, 0, connectBtnpressed.getMinimumWidth(), connectBtnpressed.getMinimumHeight());
        connectBtn.setCompoundDrawables(null, connectBtnpressed, null, null);


        //初始化单例模式中的蓝牙扫描回调
        initHoldBluetooth();

        //初始化权限
        initPermission();

        //初始化View
        initView();

        //初始化下拉刷新
        initRefresh();

        //设置RecyclerView的Item的点击事件
      //  setRecyclerListener();
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
                case R.id.connectBtn: {
                    connectBtn.setEnabled(false);
                    connectBtnpressed = getResources().getDrawable(R.drawable.start);
                    connectBtnpressed.setBounds(0, 0, connectBtnpressed.getMinimumWidth(), connectBtnpressed.getMinimumHeight());
                    connectBtn.setCompoundDrawables(null, connectBtnpressed, null, null);

                    for(int i=0;i<mFilterModuleArray.size();i++)
                    {
                        if (mFilterModuleArray.get(i).getName().equals("RGFOOT")) {
                            //controlClientFoot = SocketThread.getClient(listBle.get(i));
                            SharedPreferences mySharedPreferences = getSharedPreferences( "Foot_Decive", Activity.MODE_PRIVATE );
                            SharedPreferences.Editor editor = mySharedPreferences.edit();
                            editor.putString("Foot", mFilterModuleArray.get(i).getMac());
                            editor.putBoolean("BondDecive",true);
                            editor.apply();

                        }else if (mFilterModuleArray.get(i).getName().equals("RGBRAKE")) {
                           // controlClientBrake = SocketThread.getClient(listBle.get(i));
                            SharedPreferences mySharedPreferences1 = getSharedPreferences( "Brake_Decive", Activity.MODE_PRIVATE );
                            SharedPreferences.Editor editor1 = mySharedPreferences1.edit();
                            editor1.putString("Brake", mFilterModuleArray.get(i).getMac());
                            editor1.putBoolean("BondDecive",true);
                            editor1.apply();
                        }else if (mFilterModuleArray.get(i).getName().equals("RGHAND")) {
                          //  controlClientHand = SocketThread.getClient(listBle.get(i));
                            SharedPreferences mySharedPreferences2 = getSharedPreferences( "Hand_Decive", Activity.MODE_PRIVATE );
                            SharedPreferences.Editor editor2 = mySharedPreferences2.edit();
                            editor2.putString("Hand", mFilterModuleArray.get(i).getMac());
                            editor2.putBoolean("BondDecive",true);
                            editor2.apply();
                        }else if (mFilterModuleArray.get(i).getName().equals("RGTORQUE")) {
                           // controlClientTorque= SocketThread.getClient(listBle.get(i));
                            SharedPreferences mySharedPreferences3 = getSharedPreferences( "Torque_Decive", Activity.MODE_PRIVATE );
                            SharedPreferences.Editor editor3 = mySharedPreferences3.edit();
                            editor3.putString("Torque", mFilterModuleArray.get(i).getMac());
                            editor3.putBoolean("BondDecive",true);
                            editor3.apply();
                        } else if (mFilterModuleArray.get(i).getName().equals("RGSTEPS")) {//
                          //  controlClientSteps= SocketThread.getClient(listBle.get(i));
                            SharedPreferences mySharedPreferences4 = getSharedPreferences( "Steps_Decive", Activity.MODE_PRIVATE );
                            SharedPreferences.Editor editor4 = mySharedPreferences4.edit();
                            editor4.putString("Steps", mFilterModuleArray.get(i).getMac());
                            editor4.putBoolean("BondDecive",true);
                            editor4.apply();
                        } else if (mFilterModuleArray.get(i).getName().equals("RGDANGLE")) {
                          //  controlClientDAngle= SocketThread.getClient(listBle.get(i));
                            SharedPreferences mySharedPreferences5 = getSharedPreferences( "DAngle_Decive", Activity.MODE_PRIVATE );
                            SharedPreferences.Editor editor5 = mySharedPreferences5.edit();
                            editor5.putString("DAngle", mFilterModuleArray.get(i).getMac());
                            editor5.putBoolean("BondDecive",true);
                            editor5.apply();
                        } else if (mFilterModuleArray.get(i).getName().equals("RGSOUND")) {
                           // controlClientSound= SocketThread.getClient(listBle.get(i));
                            SharedPreferences mySharedPreferences6 = getSharedPreferences( "Sound_Decive", Activity.MODE_PRIVATE );
                            SharedPreferences.Editor editor6 = mySharedPreferences6.edit();
                            editor6.putString("Sound", mFilterModuleArray.get(i).getMac());
                            editor6.putBoolean("BondDecive",true);
                            editor6.apply();
                        }else if (mFilterModuleArray.get(i).getName().equals("RGANGLEL")) {
                          //  controlClientAngleL= SocketThread.getClient(listBle.get(i));
                            SharedPreferences mySharedPreferences7 = getSharedPreferences( "AngleL_Decive", Activity.MODE_PRIVATE );
                            SharedPreferences.Editor editor7 = mySharedPreferences7.edit();
                            editor7.putString("AngleL", mFilterModuleArray.get(i).getMac());
                            editor7.putBoolean("BondDecive",true);
                            editor7.apply();
                        } else if (mFilterModuleArray.get(i).getName().equals("RGANGLER")) {
                          //  controlClientAngleR= SocketThread.getClient(listBle.get(i));
                            SharedPreferences mySharedPreferences8 = getSharedPreferences( "AngleR_Decive", Activity.MODE_PRIVATE );
                            SharedPreferences.Editor editor8 = mySharedPreferences8.edit();
                            editor8.putString("AngleR", mFilterModuleArray.get(i).getMac());
                            editor8.putBoolean("BondDecive",true);
                            editor8.apply();
                        }else if (mFilterModuleArray.get(i).getName().equals("RGREDIS")) {
                            //  controlClientAngleR= SocketThread.getClient(listBle.get(i));
                            SharedPreferences mySharedPreferences9 = getSharedPreferences( "Dis_Decive", Activity.MODE_PRIVATE );
                            SharedPreferences.Editor editor9 = mySharedPreferences9.edit();
                            editor9.putString("Dis", mFilterModuleArray.get(i).getMac());
                            editor9.putBoolean("BondDecive",true);
                            editor9.apply();
                        }else if (mFilterModuleArray.get(i).getName().equals("MPT-II")) {
                            //controlClientFoot = SocketThread.getClient(listBle.get(i));
                            SharedPreferences mySharedPreferences10 = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor10 = mySharedPreferences10.edit();
                            editor10.putString("Printer", mFilterModuleArray.get(i).getMac());
                            editor10.putBoolean("BondPrinter", true);
                            editor10.apply();
                        }
                        Log.d("Name:", mFilterModuleArray.get(i).getName());
                        Log.d("Address:", mFilterModuleArray.get(i).getMac());
                    }
                     if(mFilterModuleArray.size()>0){
                         Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                         startActivity(intent);
                         finish();
                     }else{
                         toast("未发现可匹配的蓝牙，请确保设备电源已打开！", Toast.LENGTH_LONG);
                     }
                }
                break;
                case R.id.exitMBtn: {
                    finish();
                    ActivityCollector.finishAll();
                }
                break;
                default: {
                }
                break;
            }
            handler.postDelayed(sendRunnable, 2000);
        }
    }
    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            connectBtn.setEnabled(true);
            connectBtnpressed = getResources().getDrawable(R.drawable.start1);
            connectBtnpressed.setBounds(0, 0, connectBtnpressed.getMinimumWidth(), connectBtnpressed.getMinimumHeight());
            connectBtn.setCompoundDrawables(null, connectBtnpressed, null, null);
        }
    };

    private void initHoldBluetooth() {
        //mHoldBluetooth = HoldBluetooth.getInstance();
        mHoldBluetooth = new HoldBluetooth();
        final HoldBluetooth.UpdateList updateList = new HoldBluetooth.UpdateList() {
            @Override
            public void update(boolean isStart,DeviceModule deviceModule) {

                if (isStart && deviceModule == null){//更新距离值
                    mainRecyclerAdapter.notifyDataSetChanged();
                    return;
                }

                if (isStart){
                    setMainBackIcon();
                    mModuleArray.add(deviceModule);
                    addFilterList(deviceModule,true);
                }else {
                    mTitle.updateLoadingState(false);
                }
            }

            @Override
            public void updateMessyCode(boolean isStart, DeviceModule deviceModule) {
                for(int i= 0; i<mModuleArray.size();i++){
                    if (mModuleArray.get(i).getMac().equals(deviceModule.getMac())){
                        mModuleArray.remove(mModuleArray.get(i));
                        mModuleArray.add(i,deviceModule);
                        upDateList();
                        break;
                    }
                }
            }
        };
        mHoldBluetooth.initHoldBluetooth(MainActivity.this,updateList);
    }

    private void initView() {
        setMainBackIcon();
        mainRecyclerAdapter = new MainRecyclerAdapter(this,mFilterModuleArray,R.layout.item_recycler_main);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mainRecyclerAdapter);
    }

    //初始化下拉刷新
    private void initRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//设置刷新监听器
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                refresh();
            }
        });
    }

    //刷新的具体实现
    private void refresh(){
      //  popDialog();
        if (mHoldBluetooth.scan(mStorage.getData(PopWindowMain.BLE_KEY))){
            mModuleArray.clear();
            mFilterModuleArray.clear();
            mTitle.updateLoadingState(true);
            mainRecyclerAdapter.notifyDataSetChanged();
        }
    }

    //根据条件过滤列表，并选择是否更新列表
    private void addFilterList(DeviceModule deviceModule,boolean isRefresh){
        if (mStorage.getData(PopWindowMain.NAME_KEY) && deviceModule.getName().equals("N/A")){
            return;
        }

        if (mStorage.getData(PopWindowMain.BLE_KEY) && !deviceModule.isBLE()){
            return;
        }

        if ((mStorage.getData(PopWindowMain.FILTER_KEY) || mStorage.getData(PopWindowMain.CUSTOM_KEY))
         && !deviceModule.isHcModule(mStorage.getData(PopWindowMain.CUSTOM_KEY),mStorage.getDataString(PopWindowMain.DATA_KEY))){
            return;
        }
        deviceModule.isCollectName(MainActivity.this);
        mFilterModuleArray.add(deviceModule);
        if (isRefresh)
            mainRecyclerAdapter.notifyDataSetChanged();
    }

    //设置头部
    private void setTitle() {
        mTitle = new DefaultNavigationBar
                .Builder(this,(ViewGroup)findViewById(R.id.main_name))
                .setLeftText("蓝牙连接")
                .hideLeftIcon()
                .setRightIcon()
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mStartDebug % 4 ==0){
                            //startActivity(DebugActivity.class);
                        }
                        mStartDebug++;
                    }
                })
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP){
                            toast("此功能系统不支持，请升级手机系统", Toast.LENGTH_LONG);
                            return;
                        }
                        setPopWindow(v);
                        mTitle.updateRightImage(true);
                    }
                })
                .builer();
    }

    //头部下拉窗口
    private void setPopWindow(View v){
        new PopWindowMain(v, MainActivity.this, new PopWindowMain.DismissListener() {
            @Override
            public void onDismissListener(boolean resetEngine) {//弹出窗口销毁的回调
               upDateList();
               mTitle.updateRightImage(false);
               if (resetEngine){//更换搜索引擎，重新搜索
                   mHoldBluetooth.stopScan();
                   refresh();
               }
            }
        });
    }

    //设置点击事件
    /*private void setRecyclerListener() {
        mainRecyclerAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                log("viewId:"+view.getId()+" item_main_icon:"+R.id.item_main_icon);
                if (view.getId() == R.id.item_main_icon){
                    setCollectWindow(position);//收藏窗口
                }else {
                    mHoldBluetooth.setDevelopmentMode(MainActivity.this);//设置是否进入开发模式
                    mHoldBluetooth.connect(mFilterModuleArray.get(position));
                    startActivity(CommunicationActivity.class);
                }
            }
        });
    }

    //收藏窗口
    private void setCollectWindow(int position) {
        log("弹出窗口..");
        CommonDialog.Builder collectBuilder = new CommonDialog.Builder(MainActivity.this);
        collectBuilder.setView(R.layout.hint_collect_vessel).fullWidth().loadAnimation().create().show();
        CollectBluetooth collectBluetooth = collectBuilder.getView(R.id.hint_collect_vessel_view);
        collectBluetooth.setBuilder(collectBuilder).setDevice(mFilterModuleArray.get(position))
                .setCallback(new CollectBluetooth.OnCollectCallback() {
                    @Override
                    public void callback() {
                        upDateList();
                    }
                });
    }*/

    //更新列表
    private void upDateList(){
        mFilterModuleArray.clear();
        for (DeviceModule deviceModule : mModuleArray) {
            addFilterList(deviceModule,false);
        }
        mainRecyclerAdapter.notifyDataSetChanged();
        setMainBackIcon();
    }

    //设置列表的背景图片是否显示
    private void setMainBackIcon(){
        if (mFilterModuleArray.size() == 0){
            mNotBluetooth.setVisibility(View.VISIBLE);
        }else {
            mNotBluetooth.setVisibility(View.GONE);
        }
    }

    //初始化位置权限
    private void initPermission(){
        PermissionUtil.requestEach(MainActivity.this, new PermissionUtil.OnPermissionListener() {
            @Override
            public void onSucceed() {
                //授权成功后打开蓝牙
                log("申请成功");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mHoldBluetooth.bluetoothState()){
                            if (Analysis.isOpenGPS(MainActivity.this))
                                refresh();
                            else
                                startLocation();
                        }
                    }
                },1000);

            }
            @Override
            public void onFailed(boolean showAgain) {
                log("失败: "+showAgain,"e");
                CommonDialog.Builder permissionBuilder = new CommonDialog.Builder(MainActivity.this);
                permissionBuilder.setView(R.layout.hint_permission_vessel).fullWidth().setCancelable(false).loadAnimation().create().show();
                PermissionHint permissionHint = permissionBuilder.getView(R.id.hint_permission_vessel_view);
                permissionHint.setBuilder(permissionBuilder).setPermission(showAgain).setCallback(new PermissionHint.PermissionHintCallback() {
                    @Override
                    public void callback(boolean permission) {
                        if (permission)
                            initPermission();
                        else
                            finish();
                    }
                });
            }
        }, PermissionUtil.LOCATION);
    }

    //开启位置权限
    private void startLocation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("提示")
                .setMessage("请前往打开手机的位置权限!")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 10);
                    }
                }).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //退出这个界面，或是返回桌面时，停止扫描
        mHoldBluetooth.stopScan();
    }
}