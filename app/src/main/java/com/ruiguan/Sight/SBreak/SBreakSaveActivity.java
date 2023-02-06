package com.ruiguan.Sight.SBreak;

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
import com.ruiguan.Sight.SAngle.SAngleSaveActivity;
import com.ruiguan.activities.ActivityCollector;
import com.ruiguan.activities.MainActivity;
import com.ruiguan.base.BaseActivity;
import com.ruiguan.chache.Break.BreakSaveActivity;
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

public class SBreakSaveActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private SBreakDB saveDB;
    private Cursor mCursor;

    private Button delCurBtn;
    private Button delAllBtn;
    private Button printCurBtn;
    private Button printAllBtn;
    private Button exportCurBtn;
    private Button exportAllBtn;
    private Button backBtn;
    private Button exitBtn;

    private Drawable delCurBtnpressed;
    private Drawable delAllBtnpressed;
    private Drawable printCurBtnpressed;
    private Drawable printAllBtnpressed;
    private Drawable exportCurBtnpressed;
    private Drawable exportAllBtnpressed;

    private String str_company;
    private String str_number;
    private String str_sightNumber;
    private String str_sightLenght;
    private String str_sightType;
    private String str_sightLoad;

    private String str_breakSpeed;
    private String str_ASpeedMax;
    private String str_breakDis;
    private String str_breakTime;
    private String str_tabanForceMax;
    private String str_date;

    private float breakSpeed;
    private float ASpeedMax;
    private float breakDis;
    private float breakTime;
    private float tabanForceMax;

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
        setContentView(R.layout.activity_sbreak_save);
        ActivityCollector.addActivity(this);
        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_sightNumber= sight_Input.getsightNumber();
        str_sightLenght= sight_Input.getsightLenght();
        str_sightType= sight_Input.getsightType();
        str_sightLoad= sight_Input.getsightLoad();

        printCurBtn = (Button) findViewById(R.id.printCurSBtn);
        printAllBtn = (Button) findViewById(R.id.printAllSBtn);
        delCurBtn = (Button) findViewById(R.id.delCurSBtn);
        delAllBtn= (Button) findViewById(R.id.delAllSBtn);
        exportCurBtn= (Button) findViewById(R.id.exportCurSBtn);
        exportAllBtn= (Button) findViewById(R.id.exportAllSBtn);
        backBtn=(Button) findViewById(R.id.backSBtn);
        exitBtn=(Button) findViewById(R.id.exitSBtn);
        View.OnClickListener bl = new SBreakSaveActivity.ButtonListener();
        setOnClickListener(printCurBtn, bl);
        setOnClickListener(printAllBtn, bl);
        setOnClickListener(delCurBtn, bl);
        setOnClickListener(delAllBtn, bl);
        setOnClickListener(exportCurBtn, bl);
        setOnClickListener(exportAllBtn, bl);
        setOnClickListener(backBtn, bl);
        setOnClickListener(exitBtn, bl);
        setUpViews();
        savelistView.setOnItemClickListener(this);
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
        saveDB.close();
        mCursor.close();
        ActivityCollector.removeActivity(this);
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
                case R.id. printCurSBtn: {
                    printCurBtn.setEnabled(false);
                    printCurBtnpressed = getResources().getDrawable(R.drawable.print);
                    printCurBtnpressed.setBounds(0, 0, printCurBtnpressed.getMinimumWidth(), printCurBtnpressed.getMinimumHeight());
                    printCurBtn.setCompoundDrawables(null, printCurBtnpressed, null, null);
                    if (printDevice == null) {           //首次连接打印机
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(SBreakSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SBreakSaveActivity.this.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                    } else {          //打印数据
                        printDataService = new PrintDataService(SBreakSaveActivity.this,deviceAddress);
                        printDataService.putDevice(printDevice);
                        if(mCursor.moveToFirst()){
                            ID = mCursor.getInt(0);
                            if (ID == data_ID) {
                                str_date = mCursor.getString(1);
                                str_company = mCursor.getString(2);
                                str_number = mCursor.getString(3);

                                str_sightNumber= mCursor.getString(4);
                                str_sightLenght= mCursor.getString(5);
                                str_sightType= mCursor.getString(6);
                                str_sightLoad= mCursor.getString(7);

                                breakSpeed= mCursor.getFloat(8);
                                str_breakSpeed= myformat.format(breakSpeed);
                                ASpeedMax = mCursor.getFloat(9);
                                str_ASpeedMax = myformat.format(ASpeedMax);
                                breakDis = mCursor.getFloat(10);
                                str_breakDis = myformat.format(breakDis);
                                breakTime= mCursor.getFloat(11);
                                str_breakTime= myformat.format(breakTime);
                                tabanForceMax= mCursor.getFloat(12);
                                str_tabanForceMax= myformat.format(tabanForceMax);

                                PrintMeasureData();
                                break;
                            }
                            while(mCursor.moveToNext()){//遍历数据表中的数据
                                ID = mCursor.getInt(0);
                                if (ID == data_ID) {
                                    str_date = mCursor.getString(1);
                                    str_company = mCursor.getString(2);
                                    str_number = mCursor.getString(3);

                                    str_sightNumber= mCursor.getString(4);
                                    str_sightLenght= mCursor.getString(5);
                                    str_sightType= mCursor.getString(6);
                                    str_sightLoad= mCursor.getString(7);

                                    breakSpeed= mCursor.getFloat(8);
                                    str_breakSpeed= myformat.format(breakSpeed);
                                    ASpeedMax = mCursor.getFloat(9);
                                    str_ASpeedMax = myformat.format(ASpeedMax);
                                    breakDis = mCursor.getFloat(10);
                                    str_breakDis = myformat.format(breakDis);
                                    breakTime= mCursor.getFloat(11);
                                    str_breakTime= myformat.format(breakTime);
                                    tabanForceMax= mCursor.getFloat(12);
                                    str_tabanForceMax= myformat.format(tabanForceMax);

                                    PrintMeasureData();
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
                case R.id.printAllSBtn: {
                    printAllBtn.setEnabled(false);
                    printAllBtnpressed = getResources().getDrawable(R.drawable.print);
                    printAllBtnpressed.setBounds(0, 0, printAllBtnpressed.getMinimumWidth(), printAllBtnpressed.getMinimumHeight());
                    printAllBtn.setCompoundDrawables(null, printAllBtnpressed, null, null);
                    if (printDataService == null) {           //首次连接打印机
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(SBreakSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SBreakSaveActivity.this.getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                    } else {          //打印数据
                        printDataService.putDevice(printDevice);
                        if(mCursor.moveToFirst()){
                            str_date = mCursor.getString(1);
                            str_company = mCursor.getString(2);
                            str_number = mCursor.getString(3);

                            str_sightNumber= mCursor.getString(4);
                            str_sightLenght= mCursor.getString(5);
                            str_sightType= mCursor.getString(6);
                            str_sightLoad= mCursor.getString(7);

                            breakSpeed= mCursor.getFloat(8);
                            str_breakSpeed= myformat.format(breakSpeed);
                            ASpeedMax = mCursor.getFloat(9);
                            str_ASpeedMax = myformat.format(ASpeedMax);
                            breakDis = mCursor.getFloat(10);
                            str_breakDis = myformat.format(breakDis);
                            breakTime= mCursor.getFloat(11);
                            str_breakTime= myformat.format(breakTime);
                            tabanForceMax= mCursor.getFloat(12);
                            str_tabanForceMax= myformat.format(tabanForceMax);
                            PrintMeasureData();
                            while(mCursor.moveToNext()){//遍历数据表中的数据
                                str_date = mCursor.getString(1);
                                str_company = mCursor.getString(2);
                                str_number = mCursor.getString(3);

                                str_sightNumber= mCursor.getString(4);
                                str_sightLenght= mCursor.getString(5);
                                str_sightType= mCursor.getString(6);
                                str_sightLoad= mCursor.getString(7);

                                breakSpeed= mCursor.getFloat(8);
                                str_breakSpeed= myformat.format(breakSpeed);
                                ASpeedMax = mCursor.getFloat(9);
                                str_ASpeedMax = myformat.format(ASpeedMax);
                                breakDis = mCursor.getFloat(10);
                                str_breakDis = myformat.format(breakDis);
                                breakTime= mCursor.getFloat(11);
                                str_breakTime= myformat.format(breakTime);
                                tabanForceMax= mCursor.getFloat(12);
                                str_tabanForceMax= myformat.format(tabanForceMax);
                                PrintMeasureData();
                            }
                        }
                    }
                }
                break;
                case R.id. delCurSBtn: {
                    delCurBtn.setEnabled(false);
                    delCurBtnpressed = getResources().getDrawable(R.drawable.delete);
                    delCurBtnpressed.setBounds(0, 0, delCurBtnpressed.getMinimumWidth(), delCurBtnpressed.getMinimumHeight());
                    delCurBtn.setCompoundDrawables(null, delCurBtnpressed, null, null);
                    breakDeleteS();
                }
                break;
                case R.id.delAllSBtn: {
                    delAllBtn.setEnabled(false);
                    delAllBtnpressed = getResources().getDrawable(R.drawable.delete);
                    delAllBtnpressed.setBounds(0, 0, delAllBtnpressed.getMinimumWidth(), delAllBtnpressed.getMinimumHeight());
                    delAllBtn.setCompoundDrawables(null,delAllBtnpressed, null, null);
                    while(mCursor.moveToFirst()){
                        data_ID = mCursor.getInt(0);
                        breakDeleteS();
                    }
                }
                break;
                case R.id.exportCurSBtn: {
                    exportCurBtn.setEnabled(false);
                    exportCurBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportCurBtnpressed.setBounds(0, 0, exportCurBtnpressed.getMinimumWidth(), exportCurBtnpressed.getMinimumHeight());
                    exportCurBtn.setCompoundDrawables(null,exportCurBtnpressed, null, null);
                    if(mCursor.moveToFirst()) {
                        ID = mCursor.getInt(0);
                        if (ID == data_ID) {
                            str_date = mCursor.getString(1);
                            str_company = mCursor.getString(2);
                            str_number = mCursor.getString(3);

                            str_sightNumber= mCursor.getString(4);
                            str_sightLenght= mCursor.getString(5);
                            str_sightType= mCursor.getString(6);
                            str_sightLoad= mCursor.getString(7);

                            breakSpeed= mCursor.getFloat(8);
                            str_breakSpeed= myformat.format(breakSpeed);
                            ASpeedMax = mCursor.getFloat(9);
                            str_ASpeedMax = myformat.format(ASpeedMax);
                            breakDis = mCursor.getFloat(10);
                            str_breakDis = myformat.format(breakDis);
                            breakTime= mCursor.getFloat(11);
                            str_breakTime= myformat.format(breakTime);
                            tabanForceMax= mCursor.getFloat(12);
                            str_tabanForceMax= myformat.format(tabanForceMax);
                            CreatePdf();
                            Toast.makeText(SBreakSaveActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车紧急制动距离检测报告", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        while(mCursor.moveToNext())
                        {//遍历数据表中的数据
                            ID = mCursor.getInt(0);
                            if (ID == data_ID) {
                                str_date = mCursor.getString(1);
                                str_company = mCursor.getString(2);
                                str_number = mCursor.getString(3);

                                str_sightNumber= mCursor.getString(4);
                                str_sightLenght= mCursor.getString(5);
                                str_sightType= mCursor.getString(6);
                                str_sightLoad= mCursor.getString(7);

                                breakSpeed= mCursor.getFloat(8);
                                str_breakSpeed= myformat.format(breakSpeed);
                                ASpeedMax = mCursor.getFloat(9);
                                str_ASpeedMax = myformat.format(ASpeedMax);
                                breakDis = mCursor.getFloat(10);
                                str_breakDis = myformat.format(breakDis);
                                breakTime= mCursor.getFloat(11);
                                str_breakTime= myformat.format(breakTime);
                                tabanForceMax= mCursor.getFloat(12);
                                str_tabanForceMax= myformat.format(tabanForceMax);
                                CreatePdf();
                                Toast.makeText(SBreakSaveActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车紧急制动距离检测报告", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }
                break;
                case R.id.exportAllSBtn: {
                    exportAllBtn.setEnabled(false);
                    exportAllBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportAllBtnpressed.setBounds(0, 0, exportAllBtnpressed.getMinimumWidth(), exportAllBtnpressed.getMinimumHeight());
                    exportAllBtn.setCompoundDrawables(null,exportAllBtnpressed, null, null);
                    CreatePdfAll();
                    Toast.makeText(SBreakSaveActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车紧急制动距离检测报告", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id. backSBtn: {
                    Intent intent = new Intent(SBreakSaveActivity.this, SBreakActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.exitSBtn: {
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
    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            delCurBtn.setEnabled(true);
            delCurBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delCurBtnpressed.setBounds(0, 0, delCurBtnpressed.getMinimumWidth(), delCurBtnpressed.getMinimumHeight());
            delCurBtn.setCompoundDrawables(null, delCurBtnpressed, null, null);

            delAllBtn.setEnabled(true);
            delAllBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delAllBtnpressed.setBounds(0, 0, delAllBtnpressed.getMinimumWidth(), delAllBtnpressed.getMinimumHeight());
            delAllBtn.setCompoundDrawables(null, delAllBtnpressed, null, null);

            printCurBtn.setEnabled(true);
            printCurBtnpressed = getResources().getDrawable(R.drawable.print1);
            printCurBtnpressed.setBounds(0, 0, printCurBtnpressed.getMinimumWidth(), printCurBtnpressed.getMinimumHeight());
            printCurBtn.setCompoundDrawables(null, printCurBtnpressed, null, null);

            printAllBtn.setEnabled(true);
            printAllBtnpressed = getResources().getDrawable(R.drawable.print1);
            printAllBtnpressed.setBounds(0, 0, printAllBtnpressed.getMinimumWidth(), printAllBtnpressed.getMinimumHeight());
            printAllBtn.setCompoundDrawables(null, printAllBtnpressed, null, null);

            exportCurBtn.setEnabled(true);
            exportCurBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportCurBtnpressed.setBounds(0, 0, exportCurBtnpressed.getMinimumWidth(), exportCurBtnpressed.getMinimumHeight());
            exportCurBtn.setCompoundDrawables(null, exportCurBtnpressed, null, null);

            exportAllBtn.setEnabled(true);
            exportAllBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportAllBtnpressed.setBounds(0, 0, exportAllBtnpressed.getMinimumWidth(), exportAllBtnpressed.getMinimumHeight());
            exportAllBtn.setCompoundDrawables(null, exportAllBtnpressed, null, null);
        }
    };
    public void setUpViews(){
        saveDB = new SBreakDB(this);
        mCursor = saveDB.select();
        savelistView = (ListView)findViewById(R.id.savelistbrakeS);
        savelistView.setAdapter(new SBreakSaveActivity.saveListAdapter(this, mCursor));
        savelistView.setOnItemClickListener(this);
    }
    public void breakAddS(String str_breakSpeed,String str_ASpeedMax,String str_breakDis,String str_breakTime,String str_tabanForceMax){

        Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
        String   str_date   =   formatter.format(curDate);
        saveDB.insert(str_date,str_breakSpeed,str_ASpeedMax,str_breakDis,str_breakTime,str_tabanForceMax);
        mCursor.requery();
        savelistView.invalidateViews();
    }
    public void breakDeleteS(){
        if (data_ID == 0) {
            return;
        }
        saveDB.delete(data_ID);
        mCursor.requery();
        savelistView.invalidateViews();
//        Toast.makeText(this, "删除成功!", Toast.LENGTH_SHORT).show();
    }

    public void breakUpdate(){
        saveDB.update(data_ID);
        mCursor.requery();
        savelistView.invalidateViews();
        Toast.makeText(this, "Update Successed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCursor.moveToPosition(position);
        data_ID = mCursor.getInt(0);
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
        mCursor.moveToPosition(position);

        TextView date_txt = (TextView) convertView.findViewById(R.id.dateSave);
        TextView company_txt = (TextView) convertView.findViewById(R.id.companySave);
        TextView deviceNum_txt = (TextView) convertView.findViewById(R.id.deviceNumSave);

        TextView sightNumber_txt = (TextView) convertView.findViewById(R.id.chacheNumber);
        TextView sightLenght_txt = (TextView) convertView.findViewById(R.id.chacheType);
        TextView sightType_txt = (TextView) convertView.findViewById(R.id.chacheGroup);
        TextView sightLoad_txt = (TextView) convertView.findViewById(R.id.breakSpeed);

        TextView breakSpeed_txt = (TextView) convertView.findViewById(R.id.ratedSpeed);
        TextView ASpeedMax_txt = (TextView) convertView.findViewById(R.id.breakDis);
        TextView breakDis_txt = (TextView) convertView.findViewById(R.id.breakTime);
        TextView breakTime_txt = (TextView) convertView.findViewById(R.id.breakForce);
        TextView tabanForceMax_txt = (TextView) convertView.findViewById(R.id.breaklength);


        date_txt.setText("检测时间："+mCursor.getString(1));
        company_txt.setText("受检单位："+mCursor.getString(2));
        deviceNum_txt.setText("设备编号："+mCursor.getString(3));

        sightNumber_txt.setText("车牌编号："+mCursor.getString(4));
        sightLenght_txt.setText("车身长度："+mCursor.getString(5)+"m");
        sightType_txt.setText("车辆类型："+mCursor.getString(6));
        sightLoad_txt.setText("负载类型："+mCursor.getString(7));

        breakSpeed_txt.setText("制动速度："+mCursor.getString(8)+"km/h");
        ASpeedMax_txt.setText("最大制动减速度："+mCursor.getString(9)+"m/s^2");
        breakDis_txt.setText("制动距离："+mCursor.getString(10)+"m");
        breakTime_txt.setText("制动时间："+mCursor.getString(11)+"s");
        tabanForceMax_txt.setText("制动时刻最大踏板力："+mCursor.getString(12)+"N");
        return convertView;
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
                    printDataService = new PrintDataService(SBreakSaveActivity.this,shares.getString("Printer",""));
                    Toast.makeText(SBreakSaveActivity.this,"打印机连接中...",Toast.LENGTH_LONG).show();
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
        str_date = mCursor.getString(1);
        str_company = mCursor.getString(2);
        str_number = mCursor.getString(3);

        str_sightNumber= mCursor.getString(4);
        str_sightLenght= mCursor.getString(5);
        str_sightType= mCursor.getString(6);
        str_sightLoad= mCursor.getString(7);

        breakSpeed= mCursor.getFloat(8);
        str_breakSpeed= myformat.format(breakSpeed);
        ASpeedMax = mCursor.getFloat(9);
        str_ASpeedMax = myformat.format(ASpeedMax);
        breakDis = mCursor.getFloat(10);
        str_breakDis = myformat.format(breakDis);
        breakTime= mCursor.getFloat(11);
        str_breakTime= myformat.format(breakTime);
        tabanForceMax= mCursor.getFloat(12);
        str_tabanForceMax= myformat.format(tabanForceMax);

        printDataService.send("\n*******************************\n");
        printDataService.send("观光车辆/观光列车紧急制动距离检测结果");
        printDataService.send("\n*******************************\n");
        printDataService.send("测试时间"+": "+ str_date+"\n");
        printDataService.send("受检单位"+": "+str_company+"\n");//
        printDataService.send("设备编号"+": "+ str_number+"\n");//
        printDataService.send("车牌编号"+": "+ str_sightNumber+"\n");//
        printDataService.send("车身长"+": "+ str_sightLenght+"m"+"\n");//
        printDataService.send("车辆类型"+": "+ str_sightType+"\n");//
        printDataService.send("负载类型"+": "+ str_sightLoad+"\n");//
        printDataService.send("制动速度"+": "+ str_breakSpeed+"km/h"+"\n");//
        printDataService.send("最大制动减速度"+": "+ str_ASpeedMax+"m/s^2"+"\n");//
        printDataService.send("制动距离"+": "+ str_breakDis+"m"+"\n");//
        printDataService.send("制动时间"+": "+ str_breakTime+"s"+"\n");//
        printDataService.send("制动时刻最大踏板力"+": "+ str_tabanForceMax+"N"+"\n");//
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
        SBreakSaveActivity.this.sendBroadcast(intent);

    }
    //创建PDF文件-----------------------------------------------------------------
    com.itextpdf.text.Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(SBreakSaveActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //创建目录
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车紧急制动距离检测报告"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车紧急制动距离检测报告"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车紧急制动距离检测报告" + File.separator + str_date+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    pdfcontext = new Paragraph("观光车辆/观光列车紧急制动距离检测报告",setChineseTitleFont());
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

                    cell.setPhrase(new Phrase("制动速度：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_breakSpeed,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("km/h",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("最大制动减速度：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_ASpeedMax,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("m/s^2",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("制动距离：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_breakDis,setChineseFont()))    ;mtable.addCell(cell);
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
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车紧急制动距离检测报告" +  File.separator + str_date +".pdf");
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
        verifyStoragePermissions(SBreakSaveActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //创建目录
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车紧急制动距离检测报告"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车紧急制动距离检测报告"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车紧急制动距离检测报告" + File.separator + curDate.toString ()+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    if(mCursor.moveToFirst()){
                        str_date = mCursor.getString(1);
                        str_company = mCursor.getString(2);
                        str_number = mCursor.getString(3);

                        str_sightNumber= mCursor.getString(4);
                        str_sightLenght= mCursor.getString(5);
                        str_sightType= mCursor.getString(6);
                        str_sightLoad= mCursor.getString(7);

                        breakSpeed= mCursor.getFloat(8);
                        str_breakSpeed= myformat.format(breakSpeed);
                        ASpeedMax = mCursor.getFloat(9);
                        str_ASpeedMax = myformat.format(ASpeedMax);
                        breakDis = mCursor.getFloat(10);
                        str_breakDis = myformat.format(breakDis);
                        breakTime= mCursor.getFloat(11);
                        str_breakTime= myformat.format(breakTime);
                        tabanForceMax= mCursor.getFloat(12);
                        str_tabanForceMax= myformat.format(tabanForceMax);

                        pdfcontext = new Paragraph("观光车辆/观光列车紧急制动距离检测报告",setChineseTitleFont());
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
                        cell2.setMinimumHeight(52);
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

                        cell2.setPhrase(new Phrase("制动速度：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_breakSpeed,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("m/s",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("最大制动减速度：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_ASpeedMax,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("m/s^2",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("制动距离：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_breakDis,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("m",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("制动时间：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_breakTime,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("s",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("制动时刻最大踏板力：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_tabanForceMax,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("N",setChineseFont()))    ;mtable2.addCell(cell2);
                        doc.add(mtable2);

                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        while(mCursor.moveToNext()){//遍历数据表中的数据
                            str_date = mCursor.getString(1);
                            str_company = mCursor.getString(2);
                            str_number = mCursor.getString(3);

                            str_sightNumber= mCursor.getString(4);
                            str_sightLenght= mCursor.getString(5);
                            str_sightType= mCursor.getString(6);
                            str_sightLoad= mCursor.getString(7);

                            breakSpeed= mCursor.getFloat(8);
                            str_breakSpeed= myformat.format(breakSpeed);
                            ASpeedMax = mCursor.getFloat(9);
                            str_ASpeedMax = myformat.format(ASpeedMax);
                            breakDis = mCursor.getFloat(10);
                            str_breakDis = myformat.format(breakDis);
                            breakTime= mCursor.getFloat(11);
                            str_breakTime= myformat.format(breakTime);
                            tabanForceMax= mCursor.getFloat(12);
                            str_tabanForceMax= myformat.format(tabanForceMax);

                            pdfcontext = new Paragraph("观光车辆/观光列车紧急制动距离检测报告",setChineseTitleFont());
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
                            cell.setMinimumHeight(52);
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

                            cell.setPhrase(new Phrase("制动速度：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_breakSpeed,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("m/s",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("最大制动减速度：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_ASpeedMax,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("m/s^2",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("制动距离：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_breakDis,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("m",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("制动时间：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_breakTime,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("s",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("制动时刻最大踏板力：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_tabanForceMax,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);
                            doc.add(mtable);

                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                            pdfcontext = new Paragraph("\n\r");
                            pdfcontext.setLeading(3);
                            doc.add(pdfcontext);
                        }
                    }
                    doc.close();
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车紧急制动距离检测报告" +  File.separator + curDate.toString () +".pdf");
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
