package com.ruiguan.Sight.SStep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

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
import com.ruiguan.base.BaseActivity;
import com.ruiguan.printer.PrintDataService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.ruiguan.Sight.SightActivity.sight_Input;
import static com.ruiguan.activities.MenuActivity.input_data;

public class SStepSaveActivity extends BaseActivity implements AdapterView.OnItemClickListener  {
    private SStepDB saveSStepDB;
    private Cursor cursorSStep;

    private Button delCurSStepBtn;
    private Button delAllSStepBtn;
    private Button printCurSStepBtn;
    private Button printAllSStepBtn;
    private Button exportAllSStepBtn;
    private Button exportCurSStepBtn;
    private Button backSStepBtn;
    private Button exitSStepBtn;

    private Drawable delCurSStepBtnpressed;
    private Drawable delAllSStepBtnpressed;
    private Drawable printCurSStepBtnpressed;
    private Drawable printAllSStepBtnpressed;
    private Drawable exportAllSStepBtnpressed;
    private Drawable exportCurSStepBtnpressed;

    private String str_date;
    private String str_company;
    private String str_number;
    private String str_sightNumber;
    private String str_sightLenght;
    private String str_sightType;
    private String str_sightLoad;
    private String str_Dis;
    private String str_errLongitude;
    private String str_errLattitude;
    private String str_startDis;
    private String str_endDis;
    private String str_errDisMax;
    private String str_stepMax;


    private ListView savelistView;
    private int data_ID = 0,ID = 0;
    private Handler handler = new Handler();
    java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.000");

    private boolean isPrinterReady = false;
    private PrintDataService printDataService = null;
    private BluetoothDevice printDevice= null;
    private boolean PrintConnect = false;
    private String deviceAddress = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sstep_save);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);

        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_sightNumber= sight_Input.getsightNumber();
        str_sightLenght= sight_Input.getsightLenght();
        str_sightType= sight_Input.getsightType();
        str_sightLoad= sight_Input.getsightLoad();

        printCurSStepBtn = (Button) findViewById(R.id.printCurStepBtn);
        printAllSStepBtn = (Button) findViewById(R.id.printAllStepBtn);
        delCurSStepBtn = (Button) findViewById(R.id.delCurStepBtn);
        delAllSStepBtn= (Button) findViewById(R.id.delAllStepBtn);
        exportCurSStepBtn= (Button) findViewById(R.id.exportCurStepBtn);
        exportAllSStepBtn= (Button) findViewById(R.id.exportAllStepBtn);
        backSStepBtn= (Button) findViewById(R.id.backStepBtn);
        exitSStepBtn= (Button) findViewById(R.id.exitStepBtn);
        View.OnClickListener bl = new SStepSaveActivity.ButtonListener();
        setOnClickListener(printCurSStepBtn, bl);
        setOnClickListener(printAllSStepBtn, bl);
        setOnClickListener(delCurSStepBtn, bl);
        setOnClickListener(delAllSStepBtn, bl);
        setOnClickListener(exportCurSStepBtn, bl);
        setOnClickListener(exportAllSStepBtn, bl);
        setOnClickListener(backSStepBtn, bl);
        setOnClickListener(exitSStepBtn, bl);
        setUpViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final String METHODTAG = ".onResume";
    }
    //销毁后调用
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        saveSStepDB.close();
        cursorSStep.close();
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
                case R.id.printCurStepBtn: {
                    printCurSStepBtn.setEnabled(false);
                    printCurSStepBtnpressed = getResources().getDrawable(R.drawable.print);
                    printCurSStepBtnpressed.setBounds(0, 0, printCurSStepBtnpressed.getMinimumWidth(), printCurSStepBtnpressed.getMinimumHeight());
                    printCurSStepBtn.setCompoundDrawables(null, printCurSStepBtnpressed, null, null);

                    if (printDevice == null) {           //首次连接打印机
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(SStepSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SStepSaveActivity.this.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                    } else {          //打印数据
                        printDataService = new PrintDataService(SStepSaveActivity.this,deviceAddress);
                        printDataService.putDevice(printDevice);
                        if(cursorSStep.moveToFirst()){
                            ID = cursorSStep.getInt(0);
                            if (ID == data_ID) {
                                str_date = cursorSStep.getString(1);
                                str_company = cursorSStep.getString(2);
                                str_number = cursorSStep.getString(3);

                                str_sightNumber= cursorSStep.getString(4);
                                str_sightLenght= cursorSStep.getString(5);
                                str_sightType= cursorSStep.getString(6);
                                str_sightLoad= cursorSStep.getString(7);

                                str_Dis = cursorSStep.getString(8);
                                str_errLongitude = cursorSStep.getString(9);
                                str_errLattitude = cursorSStep.getString(10);
                                str_startDis= cursorSStep.getString(11);
                                str_endDis= cursorSStep.getString(12);
                                str_errDisMax = cursorSStep.getString(13);
                                str_stepMax = cursorSStep.getString(14);
                                PrintMeasureData();
                                break;
                            }
                            while(cursorSStep.moveToNext()){//遍历数据表中的数据
                                ID = cursorSStep.getInt(0);
                                if (ID == data_ID) {
                                    str_date = cursorSStep.getString(1);
                                    str_company = cursorSStep.getString(2);
                                    str_number = cursorSStep.getString(3);

                                    str_sightNumber= cursorSStep.getString(4);
                                    str_sightLenght= cursorSStep.getString(5);
                                    str_sightType= cursorSStep.getString(6);
                                    str_sightLoad= cursorSStep.getString(7);

                                    str_Dis = cursorSStep.getString(8);
                                    str_errLongitude = cursorSStep.getString(9);
                                    str_errLattitude = cursorSStep.getString(10);
                                    str_startDis= cursorSStep.getString(11);
                                    str_endDis= cursorSStep.getString(12);
                                    str_errDisMax = cursorSStep.getString(13);
                                    str_stepMax = cursorSStep.getString(14);
                                    PrintMeasureData();
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
                case R.id.printAllStepBtn: {
                    printAllSStepBtn.setEnabled(false);
                    printAllSStepBtnpressed = getResources().getDrawable(R.drawable.print);
                    printAllSStepBtnpressed.setBounds(0, 0, printAllSStepBtnpressed.getMinimumWidth(), printAllSStepBtnpressed.getMinimumHeight());
                    printAllSStepBtn.setCompoundDrawables(null, printAllSStepBtnpressed, null, null);
                    if (printDataService == null) {           //首次连接打印机
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(SStepSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SStepSaveActivity.this.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                    } else {          //打印数据
                        printDataService.putDevice(printDevice);
                        if(cursorSStep.moveToFirst()){
                            str_date = cursorSStep.getString(1);
                            str_company = cursorSStep.getString(2);
                            str_number = cursorSStep.getString(3);

                            str_sightNumber= cursorSStep.getString(4);
                            str_sightLenght= cursorSStep.getString(5);
                            str_sightType= cursorSStep.getString(6);
                            str_sightLoad= cursorSStep.getString(7);

                            str_Dis = cursorSStep.getString(8);
                            str_errLongitude = cursorSStep.getString(9);
                            str_errLattitude = cursorSStep.getString(10);
                            str_startDis= cursorSStep.getString(11);
                            str_endDis= cursorSStep.getString(12);
                            str_errDisMax = cursorSStep.getString(13);
                            str_stepMax = cursorSStep.getString(14);
                            PrintMeasureData();
                            while(cursorSStep.moveToNext()){//遍历数据表中的数据
                                str_date = cursorSStep.getString(1);
                                str_company = cursorSStep.getString(2);
                                str_number = cursorSStep.getString(3);

                                str_sightNumber= cursorSStep.getString(4);
                                str_sightLenght= cursorSStep.getString(5);
                                str_sightType= cursorSStep.getString(6);
                                str_sightLoad= cursorSStep.getString(7);

                                str_Dis = cursorSStep.getString(8);
                                str_errLongitude = cursorSStep.getString(9);
                                str_errLattitude = cursorSStep.getString(10);
                                str_startDis= cursorSStep.getString(11);
                                str_endDis= cursorSStep.getString(12);
                                str_errDisMax = cursorSStep.getString(13);
                                str_stepMax = cursorSStep.getString(14);
                                PrintMeasureData();
                            }
                        }
                    }
                }
                break;
                case R.id.delCurStepBtn: {
                    delCurSStepBtn.setEnabled(false);
                    delCurSStepBtnpressed = getResources().getDrawable(R.drawable.delete);
                    delCurSStepBtnpressed.setBounds(0, 0, delCurSStepBtnpressed.getMinimumWidth(), delCurSStepBtnpressed.getMinimumHeight());
                    delCurSStepBtn.setCompoundDrawables(null, delCurSStepBtnpressed, null, null);
                    stepDeleteS();
                }
                break;
                case R.id.delAllStepBtn: {
                    delAllSStepBtn.setEnabled(false);
                    delAllSStepBtnpressed = getResources().getDrawable(R.drawable.delete);
                    delAllSStepBtnpressed.setBounds(0, 0, delAllSStepBtnpressed.getMinimumWidth(), delAllSStepBtnpressed.getMinimumHeight());
                    delAllSStepBtn.setCompoundDrawables(null,delAllSStepBtnpressed, null, null);
                    while(cursorSStep.moveToFirst()){
                        data_ID = cursorSStep.getInt(0);
                        stepDeleteS();
                    }
                }
                break;
                case R.id.exportCurStepBtn: {
                    exportCurSStepBtn.setEnabled(false);
                    exportCurSStepBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportCurSStepBtnpressed.setBounds(0, 0, exportCurSStepBtnpressed.getMinimumWidth(), exportCurSStepBtnpressed.getMinimumHeight());
                    exportCurSStepBtn.setCompoundDrawables(null,exportCurSStepBtnpressed, null, null);
                    if(cursorSStep.moveToFirst()){
                        ID = cursorSStep.getInt(0);
                        if (ID == data_ID) {
                            str_date = cursorSStep.getString(1);
                            str_company = cursorSStep.getString(2);
                            str_number = cursorSStep.getString(3);

                            str_sightNumber= cursorSStep.getString(4);
                            str_sightLenght= cursorSStep.getString(5);
                            str_sightType= cursorSStep.getString(6);
                            str_sightLoad= cursorSStep.getString(7);

                            str_Dis = cursorSStep.getString(8);
                            str_errLongitude = cursorSStep.getString(9);
                            str_errLattitude = cursorSStep.getString(10);
                            str_startDis= cursorSStep.getString(11);
                            str_endDis= cursorSStep.getString(12);
                            str_errDisMax = cursorSStep.getString(13);
                            str_stepMax = cursorSStep.getString(14);
                            CreatePdf();
                            Toast.makeText(SStepSaveActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车观光景区坡度检测报告", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        while(cursorSStep.moveToNext()){//遍历数据表中的数据
                            ID = cursorSStep.getInt(0);
                            if (ID == data_ID) {
                                str_date = cursorSStep.getString(1);
                                str_company = cursorSStep.getString(2);
                                str_number = cursorSStep.getString(3);

                                str_sightNumber= cursorSStep.getString(4);
                                str_sightLenght= cursorSStep.getString(5);
                                str_sightType= cursorSStep.getString(6);
                                str_sightLoad= cursorSStep.getString(7);

                                str_Dis = cursorSStep.getString(8);
                                str_errLongitude = cursorSStep.getString(9);
                                str_errLattitude = cursorSStep.getString(10);
                                str_startDis= cursorSStep.getString(11);
                                str_endDis= cursorSStep.getString(12);
                                str_errDisMax = cursorSStep.getString(13);
                                str_stepMax = cursorSStep.getString(14);
                                CreatePdf();
                                Toast.makeText(SStepSaveActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车观光景区坡度检测报告", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }
                break;
                case R.id.exportAllStepBtn: {
                    exportAllSStepBtn.setEnabled(false);
                    exportAllSStepBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportAllSStepBtnpressed.setBounds(0, 0, exportAllSStepBtnpressed.getMinimumWidth(), exportAllSStepBtnpressed.getMinimumHeight());
                    exportAllSStepBtn.setCompoundDrawables(null,exportAllSStepBtnpressed, null, null);
                    CreatePdfAll();
                    Toast.makeText(SStepSaveActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车观光景区坡度检测报告", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.backStepBtn: {
                    Intent intent = new Intent(SStepSaveActivity.this, SStepActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.exitStepBtn: {
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
    public void setUpViews(){
        saveSStepDB = new SStepDB(this);
        cursorSStep = saveSStepDB.select();
        savelistView = (ListView)findViewById(R.id.savelistStep);
        savelistView.setAdapter(new SStepSaveActivity.saveListAdapter(this, cursorSStep));
        savelistView.setOnItemClickListener(this);
    }
    public void stepAddS(String str_dis,String str_errLongitude,String str_errLattitude,String str_startDis,String str_endDis,String str_errDisMax,String str_stepMax){
        Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
        String   str_date   =   formatter.format(curDate);
        saveSStepDB.insert(str_date,str_dis,str_errLongitude,str_errLattitude,str_startDis,str_endDis,str_errDisMax,str_stepMax);
        cursorSStep.requery();
        savelistView.invalidateViews();
    }
    public void stepDeleteS(){
        if (data_ID == 0) {
            return;
        }
        saveSStepDB.delete(data_ID);
        cursorSStep.requery();
        savelistView.invalidateViews();
//        Toast.makeText(this, "删除成功!", Toast.LENGTH_SHORT).show();
    }

    public void stepUpdate(){
        saveSStepDB.update(data_ID);
        cursorSStep.requery();
        savelistView.invalidateViews();
        Toast.makeText(this, "Update Successed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cursorSStep.moveToPosition(position);
        data_ID = cursorSStep.getInt(0);
        Toast.makeText(this, "已选中!", Toast.LENGTH_SHORT).show();
    }

    public class saveListAdapter extends BaseAdapter {
        private Context mContext;
        private Cursor mCursor;
        public saveListAdapter(Context context,Cursor cursor) {
            mContext = context;
            mCursor = cursor;
        }
        @Override
        public int getCount() {
            return mCursor.getCount();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getListView(position, convertView, parent);
        }
    }
    @SuppressLint("SetTextI18n")
    private View getListView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView =getLayoutInflater().inflate(R.layout.save_list, null);//加载布局
        }
        cursorSStep.moveToPosition(position);

        TextView date_txt = (TextView) convertView.findViewById(R.id.dateSave);
        TextView company_txt = (TextView) convertView.findViewById(R.id.companySave);
        TextView deviceNum_txt = (TextView) convertView.findViewById(R.id.deviceNumSave);

        TextView sightNumber_txt = (TextView) convertView.findViewById(R.id.chacheNumber);
        TextView sightLenght_txt = (TextView) convertView.findViewById(R.id.chacheType);
        TextView sightType_txt = (TextView) convertView.findViewById(R.id.chacheGroup);
        TextView sightLoad_txt = (TextView) convertView.findViewById(R.id.breakSpeed);

        TextView dis_txt = (TextView) convertView.findViewById(R.id.ratedSpeed);
        TextView errLongitude_txt = (TextView) convertView.findViewById(R.id.breakDis);
        TextView errLattitude_txt = (TextView) convertView.findViewById(R.id.breakTime);
        TextView startDis_txt = (TextView) convertView.findViewById(R.id.breakForce);
        TextView endDis_txt = (TextView) convertView.findViewById(R.id.breaklength);
        TextView errDisMax_txt = (TextView) convertView.findViewById(R.id.angleErr);
        TextView stepMax_txt = (TextView) convertView.findViewById(R.id.forceErr);


        date_txt.setText("检测时间："+cursorSStep.getString(1));
        company_txt.setText("受检单位："+cursorSStep.getString(2));
        deviceNum_txt.setText("设备编号："+cursorSStep.getString(3));
        sightNumber_txt.setText("车牌编号："+cursorSStep.getString(4));
        sightLenght_txt.setText("车身长度："+cursorSStep.getString(5)+"m");
        sightType_txt.setText("车辆类型："+cursorSStep.getString(6));
        sightLoad_txt.setText("负载类型："+cursorSStep.getString(7));
        dis_txt.setText("距离值："+cursorSStep.getString(8)+"m");
        errLongitude_txt.setText("超标坡度起始经度值："+cursorSStep.getString(9));
        errLattitude_txt.setText("超标坡度起始纬度值："+cursorSStep.getString(10)+"m");
        startDis_txt.setText("超标坡度起点距离值："+cursorSStep.getString(11)+"m");
        endDis_txt.setText("超标坡度终点值："+cursorSStep.getString(12));
        errDisMax_txt.setText("超标距离值："+cursorSStep.getString(13)+"m");
        stepMax_txt.setText("最大坡度值："+cursorSStep.getString(14)+"%");
        return convertView;
    }

    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            delCurSStepBtn.setEnabled(true);
            delCurSStepBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delCurSStepBtnpressed.setBounds(0, 0, delCurSStepBtnpressed.getMinimumWidth(), delCurSStepBtnpressed.getMinimumHeight());
            delCurSStepBtn.setCompoundDrawables(null, delCurSStepBtnpressed, null, null);

            delAllSStepBtn.setEnabled(true);
            delAllSStepBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delAllSStepBtnpressed.setBounds(0, 0, delAllSStepBtnpressed.getMinimumWidth(), delAllSStepBtnpressed.getMinimumHeight());
            delAllSStepBtn.setCompoundDrawables(null, delAllSStepBtnpressed, null, null);

            printCurSStepBtn.setEnabled(true);
            printCurSStepBtnpressed = getResources().getDrawable(R.drawable.print1);
            printCurSStepBtnpressed.setBounds(0, 0, printCurSStepBtnpressed.getMinimumWidth(), printCurSStepBtnpressed.getMinimumHeight());
            printCurSStepBtn.setCompoundDrawables(null, printCurSStepBtnpressed, null, null);

            printAllSStepBtn.setEnabled(true);
            printAllSStepBtnpressed = getResources().getDrawable(R.drawable.print1);
            printAllSStepBtnpressed.setBounds(0, 0, printAllSStepBtnpressed.getMinimumWidth(), printAllSStepBtnpressed.getMinimumHeight());
            printAllSStepBtn.setCompoundDrawables(null, printAllSStepBtnpressed, null, null);

            exportCurSStepBtn.setEnabled(true);
            exportCurSStepBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportCurSStepBtnpressed.setBounds(0, 0, exportCurSStepBtnpressed.getMinimumWidth(), exportCurSStepBtnpressed.getMinimumHeight());
            exportCurSStepBtn.setCompoundDrawables(null, exportCurSStepBtnpressed, null, null);

            exportAllSStepBtn.setEnabled(true);
            exportAllSStepBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportAllSStepBtnpressed.setBounds(0, 0, exportAllSStepBtnpressed.getMinimumWidth(), exportAllSStepBtnpressed.getMinimumHeight());
            exportAllSStepBtn.setCompoundDrawables(null, exportAllSStepBtnpressed, null, null);
        }
    };
    Runnable PrinterRunnable = new Runnable() {
        @Override
        public void run() {
            if(PrintConnect){         //连接成功并已经打印数据，则关闭蓝牙
                // if(printDataService != null) printDataService.disconnect();
                // printDataService = null;
                handler.removeCallbacks(PrinterRunnable);
                PrintConnect = false;
            }
            else{
                SharedPreferences shares = getSharedPreferences( "BLE_Info", Activity.MODE_PRIVATE );
                if(shares.getBoolean("BondPrinter",false)){
                    printDataService = new PrintDataService(SStepSaveActivity.this,shares.getString("Printer",""));
                    Toast.makeText(SStepSaveActivity.this,"打印机连接中...",Toast.LENGTH_LONG).show();
                }
                if(printDataService != null){
                    PrintConnect = printDataService.connect();
                    if(PrintConnect){
                        PrintMeasureData();
                        handler.removeCallbacks (PrinterRunnable);
                    }
                }
                handler.postDelayed(PrinterRunnable,100);
            }
        }
    };
    //打印测试数据
    private void PrintMeasureData(){
        str_date = cursorSStep.getString(1);
        str_company = cursorSStep.getString(2);
        str_number = cursorSStep.getString(3);

        str_sightNumber= cursorSStep.getString(4);
        str_sightLenght= cursorSStep.getString(5);
        str_sightType= cursorSStep.getString(6);
        str_sightLoad= cursorSStep.getString(7);

        str_Dis = cursorSStep.getString(8);
        str_errLongitude = cursorSStep.getString(9);
        str_errLattitude = cursorSStep.getString(10);
        str_startDis= cursorSStep.getString(11);
        str_endDis= cursorSStep.getString(12);
        str_errDisMax = cursorSStep.getString(13);
        str_stepMax = cursorSStep.getString(14);

        printDataService.send("\n*******************************\n");
        printDataService.send("观光车辆/观光列车坡度检测结果");
        printDataService.send("\n*******************************\n");
        printDataService.send("检测时间"+": "+ str_date+"\n");
        printDataService.send("受检单位"+": "+str_company+"\n");//
        printDataService.send("设备编号"+": "+ str_number+"\n");//
        printDataService.send("车牌编号"+": "+ str_sightNumber+"\n");//
        printDataService.send("车身长"+": "+ str_sightLenght+"m"+"\n");//
        printDataService.send("车辆类型"+": "+ str_sightType+"\n");//
        printDataService.send("负载类型"+": "+ str_sightLoad+"\n");//
        printDataService.send("距离值"+": "+ str_Dis+"m"+"\n");//

        printDataService.send("超标坡度起始经度值"+": "+ str_errLongitude +"°"+"\n");//
        printDataService.send("超标坡度起始纬度值"+": "+ str_errLattitude+"°"+"\n");//
        printDataService.send("超标坡度起点距离值"+": "+ str_startDis+"m"+"\n");//
        printDataService.send("超标坡度终点距离值"+": "+ str_endDis+"m"+"\n");//
        printDataService.send("超标距离值"+": "+ str_errDisMax+"m"+"\n");//stepMax
        printDataService.send("最大坡度值"+": "+ str_stepMax+"%"+"\n");//stepMax

        printDataService.send("*******************************\n\n\n\n");
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
        SStepSaveActivity.this.sendBroadcast(intent);

    }
    //创建PDF文件-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(SStepSaveActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
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
                    cell.setPhrase(new Phrase(str_date,setChineseFont()))    ;mtable.addCell(cell);
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

                    cell.setPhrase(new Phrase("距离值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_Dis,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("超标坡度起始经度值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_errLongitude,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("超标坡度起始纬度值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_errLattitude,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("超标坡度起点距离值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_startDis,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("超标坡度终点距离值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_endDis,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("超标距离值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_errDisMax,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("最大坡度值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_stepMax,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("%",setChineseFont()))    ;mtable.addCell(cell);

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

    private void CreatePdfAll(){
        verifyStoragePermissions(SStepSaveActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
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
                    if(cursorSStep.moveToFirst()){
                        str_date = cursorSStep.getString(1);
                        str_company = cursorSStep.getString(2);
                        str_number = cursorSStep.getString(3);

                        str_sightNumber= cursorSStep.getString(4);
                        str_sightLenght= cursorSStep.getString(5);
                        str_sightType= cursorSStep.getString(6);
                        str_sightLoad= cursorSStep.getString(7);

                        str_Dis = cursorSStep.getString(8);
                        str_errLongitude = cursorSStep.getString(9);
                        str_errLattitude = cursorSStep.getString(10);
                        str_startDis= cursorSStep.getString(11);
                        str_endDis= cursorSStep.getString(12);
                        str_errDisMax = cursorSStep.getString(13);
                        str_stepMax = cursorSStep.getString(14);

                        pdfcontext = new Paragraph("观光车辆/观光列车观光景区坡度检测报告",setChineseTitleFont());
                        pdfcontext.setAlignment(Element.ALIGN_CENTER);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        //创建一个有3列的表格
                        //创建一个有3列的表格
                        PdfPTable table1 = new PdfPTable(3);
                        table1.setWidthPercentage(99);
                        //定义一个表格单元
                        PdfPCell cell2 = new PdfPCell();
                        cell2.setMinimumHeight(45);
                        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPTable mtable2 = new PdfPTable(3);
                        mtable2.setSplitLate(false);
                        mtable2.setSplitRows(true);
                        mtable2.setWidthPercentage(99);
                        mtable2.setWidths(new float[]{300,200,200});
                        cell2.setColspan(1);
                        cell2.setBackgroundColor(new BaseColor(255,255,255));
                        cell2.setPhrase(new Phrase("检测时间：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_date,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("受检单位：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_company,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("设备编号：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_number,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("车牌编号：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_sightNumber,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("车身长：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_sightLenght,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(" m",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("车辆类型：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_sightType,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("负载类型：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_sightLoad,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("距离值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_Dis,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("m",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("超标坡度起始经度值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_errLongitude,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("°",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("超标坡度起始纬度值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_errLattitude,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("°",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("超标坡度起点距离值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_startDis,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("m",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("超标坡度终点距离值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_endDis,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("m",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("超标距离值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_errDisMax,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("m",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("最大坡度值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_stepMax,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("%",setChineseFont()))    ;mtable2.addCell(cell2);

                        doc.add(mtable2);
                        while(cursorSStep.moveToNext()){//遍历数据表中的数据
                            str_date = cursorSStep.getString(1);
                            str_company = cursorSStep.getString(2);
                            str_number = cursorSStep.getString(3);

                            str_sightNumber= cursorSStep.getString(4);
                            str_sightLenght= cursorSStep.getString(5);
                            str_sightType= cursorSStep.getString(6);
                            str_sightLoad= cursorSStep.getString(7);

                            str_Dis = cursorSStep.getString(8);
                            str_errLongitude = cursorSStep.getString(9);
                            str_errLattitude = cursorSStep.getString(10);
                            str_startDis= cursorSStep.getString(11);
                            str_endDis= cursorSStep.getString(12);
                            str_errDisMax = cursorSStep.getString(13);
                            str_stepMax = cursorSStep.getString(14);

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
                            cell.setMinimumHeight(45);
                            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                            PdfPTable mtable = new PdfPTable(3);
                            mtable.setSplitLate(false);
                            mtable.setSplitRows(true);
                            mtable.setWidthPercentage(99);
                            mtable.setWidths(new float[]{300,200,200});
                            cell.setColspan(1);
                            cell.setBackgroundColor(new BaseColor(255,255,255));
                            cell.setPhrase(new Phrase("检测时间：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_date,setChineseFont()))    ;mtable.addCell(cell);
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

                            cell.setPhrase(new Phrase("距离值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_Dis,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("超标坡度起始经度值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_errLongitude,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("超标坡度起始纬度值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_errLattitude,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("°",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("超标坡度起点距离值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_startDis,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("超标坡度终点距离值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_endDis,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("超标距离值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_errDisMax,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("最大坡度值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_stepMax,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("%",setChineseFont()))    ;mtable.addCell(cell);
                            doc.add(mtable);
                        }
                    }
                    doc.close();
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车观光景区坡度检测报告" +  File.separator + curDate.toString () +".pdf");
                }catch (FileNotFoundException e1) {
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

}
