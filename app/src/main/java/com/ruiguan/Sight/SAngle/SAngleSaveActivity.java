package com.ruiguan.Sight.SAngle;

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

public class SAngleSaveActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private SAngleDB saveASDB;
    private Cursor cursorAS;

    private Button delCurABtn;
    private Button delAllABtn;
    private Button printCurABtn;
    private Button printAllABtn;
    private Button exportAllABtn;
    private Button exportCurABtn;
    private Button backABtn;
    private Button exitABtn;

    private Drawable delCurABtnpressed;
    private Drawable delAllABtnpressed;
    private Drawable printCurABtnpressed;
    private Drawable printAllABtnpressed;
    private Drawable exportCurABtnpressed;
    private Drawable exportAllABtnpressed;

    private String str_date;
    private String str_company;
    private String str_number;
    private String str_sightNumber;
    private String str_sightLenght;
    private String str_sightType;
    private String str_sightLoad;
    private String str_testItem;

    private String str_angleMaxL;
    private String str_angleMaxR;
    private String str_angleMaxWL;
    private String str_angleMaxWR;
    private String str_angleMaxZHL;
    private String str_angleMaxZHR;
    private String str_angleMaxZNL;
    private String str_angleMaxZNR;

    private float angleMaxL;
    private float angleMaxR;
    private float angleMaxWL;
    private float angleMaxWR;
    private float angleMaxZHL;
    private float angleMaxZHR;
    private float angleMaxZNL;
    private float angleMaxZNR;

    private ListView savelistView;
    private BaseAdapter mAdapter;
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
        setContentView(R.layout.activity_sangle_save);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);

        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_sightNumber= sight_Input.getsightNumber();
        str_sightLenght= sight_Input.getsightLenght();
        str_sightType= sight_Input.getsightType();
        str_sightLoad= sight_Input.getsightLoad();

        printCurABtn = (Button) findViewById(R.id.printCurASBtn);
        printAllABtn = (Button) findViewById(R.id.printAllASBtn);
        delCurABtn = (Button) findViewById(R.id.delCurASBtn);
        delAllABtn= (Button) findViewById(R.id.delAllASBtn);
        exportCurABtn= (Button) findViewById(R.id.exportCurASBtn);
        exportAllABtn= (Button) findViewById(R.id.exportAllASBtn);
        backABtn= (Button) findViewById(R.id.backASBtn);
        exitABtn= (Button) findViewById(R.id.exitASBtn);
        View.OnClickListener bl = new SAngleSaveActivity.ButtonListener();
        setOnClickListener(printCurABtn, bl);
        setOnClickListener(printAllABtn, bl);
        setOnClickListener(delCurABtn, bl);
        setOnClickListener(delAllABtn, bl);
        setOnClickListener(exportCurABtn, bl);
        setOnClickListener(exportAllABtn, bl);
        setOnClickListener(backABtn, bl);
        setOnClickListener(exitABtn, bl);
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
        saveASDB.close();
        cursorAS.close();
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
                case R.id.printCurASBtn: {
                    printCurABtn.setEnabled(false);
                    printCurABtnpressed = getResources().getDrawable(R.drawable.print);
                    printCurABtnpressed.setBounds(0, 0, printCurABtnpressed.getMinimumWidth(), printCurABtnpressed.getMinimumHeight());
                    printCurABtn.setCompoundDrawables(null, printCurABtnpressed, null, null);

                    if (printDevice == null) {           //首次连接打印机
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(SAngleSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SAngleSaveActivity.this.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                    } else {          //打印数据
                        printDataService = new PrintDataService(SAngleSaveActivity.this,deviceAddress);
                        printDataService.putDevice(printDevice);
                        if(cursorAS.moveToFirst()){
                            ID = cursorAS.getInt(0);
                            if (ID == data_ID) {
                                str_date = cursorAS.getString(1);
                                str_company = cursorAS.getString(2);
                                str_number = cursorAS.getString(3);
                                str_sightNumber= cursorAS.getString(4);
                                str_sightLenght= cursorAS.getString(5);
                                str_sightType= cursorAS.getString(6);
                                str_sightLoad= cursorAS.getString(7);
                                str_testItem = cursorAS.getString(8);

                                angleMaxL= cursorAS.getFloat(9);
                                str_angleMaxL= myformat.format(angleMaxL);
                                angleMaxR= cursorAS.getFloat(10);
                                str_angleMaxR= myformat.format(angleMaxR);
                                angleMaxWL= cursorAS.getFloat(11);
                                str_angleMaxWL= myformat.format(angleMaxWL);
                                angleMaxWR= cursorAS.getFloat(12);
                                str_angleMaxWR= myformat.format(angleMaxWR);
                                angleMaxZHL= cursorAS.getFloat(13);
                                str_angleMaxZHL= myformat.format(angleMaxZHL);
                                angleMaxZHR= cursorAS.getFloat(14);
                                str_angleMaxZHR= myformat.format(angleMaxZHR);
                                angleMaxZNL= cursorAS.getFloat(15);
                                str_angleMaxZNL= myformat.format(angleMaxZNL);
                                angleMaxZNR= cursorAS.getFloat(16);
                                str_angleMaxZNR= myformat.format(angleMaxZNR);

                                PrintMeasureData();
                                break;
                            }
                            while(cursorAS.moveToNext()){//遍历数据表中的数据
                                ID = cursorAS.getInt(0);
                                if (ID == data_ID) {
                                    str_date = cursorAS.getString(1);
                                    str_company = cursorAS.getString(2);
                                    str_number = cursorAS.getString(3);
                                    str_sightNumber= cursorAS.getString(4);
                                    str_sightLenght= cursorAS.getString(5);
                                    str_sightType= cursorAS.getString(6);
                                    str_sightLoad= cursorAS.getString(7);
                                    str_testItem = cursorAS.getString(8);

                                    angleMaxL= cursorAS.getFloat(9);
                                    str_angleMaxL= myformat.format(angleMaxL);
                                    angleMaxR= cursorAS.getFloat(10);
                                    str_angleMaxR= myformat.format(angleMaxR);
                                    angleMaxWL= cursorAS.getFloat(11);
                                    str_angleMaxWL= myformat.format(angleMaxWL);
                                    angleMaxWR= cursorAS.getFloat(12);
                                    str_angleMaxWR= myformat.format(angleMaxWR);
                                    angleMaxZHL= cursorAS.getFloat(13);
                                    str_angleMaxZHL= myformat.format(angleMaxZHL);
                                    angleMaxZHR= cursorAS.getFloat(14);
                                    str_angleMaxZHR= myformat.format(angleMaxZHR);
                                    angleMaxZNL= cursorAS.getFloat(15);
                                    str_angleMaxZNL= myformat.format(angleMaxZNL);
                                    angleMaxZNR= cursorAS.getFloat(16);
                                    str_angleMaxZNR= myformat.format(angleMaxZNR);

                                    PrintMeasureData();
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
                case R.id.printAllASBtn: {
                    printAllABtn.setEnabled(false);
                    printAllABtnpressed = getResources().getDrawable(R.drawable.print);
                    printAllABtnpressed.setBounds(0, 0, printAllABtnpressed.getMinimumWidth(), printAllABtnpressed.getMinimumHeight());
                    printAllABtn.setCompoundDrawables(null, printAllABtnpressed, null, null);
                    if (printDataService == null) {           //首次连接打印机
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(SAngleSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SAngleSaveActivity.this.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                    } else {          //打印数据
                        printDataService.putDevice(printDevice);
                        if(cursorAS.moveToFirst()){
                            str_date = cursorAS.getString(1);
                            str_company = cursorAS.getString(2);
                            str_number = cursorAS.getString(3);
                            str_sightNumber= cursorAS.getString(4);
                            str_sightLenght= cursorAS.getString(5);
                            str_sightType= cursorAS.getString(6);
                            str_sightLoad= cursorAS.getString(7);
                            str_testItem = cursorAS.getString(8);

                            angleMaxL= cursorAS.getFloat(9);
                            str_angleMaxL= myformat.format(angleMaxL);
                            angleMaxR= cursorAS.getFloat(10);
                            str_angleMaxR= myformat.format(angleMaxR);
                            angleMaxWL= cursorAS.getFloat(11);
                            str_angleMaxWL= myformat.format(angleMaxWL);
                            angleMaxWR= cursorAS.getFloat(12);
                            str_angleMaxWR= myformat.format(angleMaxWR);
                            angleMaxZHL= cursorAS.getFloat(13);
                            str_angleMaxZHL= myformat.format(angleMaxZHL);
                            angleMaxZHR= cursorAS.getFloat(14);
                            str_angleMaxZHR= myformat.format(angleMaxZHR);
                            angleMaxZNL= cursorAS.getFloat(15);
                            str_angleMaxZNL= myformat.format(angleMaxZNL);
                            angleMaxZNR= cursorAS.getFloat(16);
                            str_angleMaxZNR= myformat.format(angleMaxZNR);
                            PrintMeasureData();
                            while(cursorAS.moveToNext()){//遍历数据表中的数据
                                str_date = cursorAS.getString(1);
                                str_company = cursorAS.getString(2);
                                str_number = cursorAS.getString(3);
                                str_sightNumber= cursorAS.getString(4);
                                str_sightLenght= cursorAS.getString(5);
                                str_sightType= cursorAS.getString(6);
                                str_sightLoad= cursorAS.getString(7);
                                str_testItem = cursorAS.getString(8);

                                angleMaxL= cursorAS.getFloat(9);
                                str_angleMaxL= myformat.format(angleMaxL);
                                angleMaxR= cursorAS.getFloat(10);
                                str_angleMaxR= myformat.format(angleMaxR);
                                angleMaxWL= cursorAS.getFloat(11);
                                str_angleMaxWL= myformat.format(angleMaxWL);
                                angleMaxWR= cursorAS.getFloat(12);
                                str_angleMaxWR= myformat.format(angleMaxWR);
                                angleMaxZHL= cursorAS.getFloat(13);
                                str_angleMaxZHL= myformat.format(angleMaxZHL);
                                angleMaxZHR= cursorAS.getFloat(14);
                                str_angleMaxZHR= myformat.format(angleMaxZHR);
                                angleMaxZNL= cursorAS.getFloat(15);
                                str_angleMaxZNL= myformat.format(angleMaxZNL);
                                angleMaxZNR= cursorAS.getFloat(16);
                                str_angleMaxZNR= myformat.format(angleMaxZNR);
                                PrintMeasureData();
                            }
                        }
                    }
                }
                break;
                case R.id.delCurASBtn: {
                    delCurABtn.setEnabled(false);
                    delCurABtnpressed = getResources().getDrawable(R.drawable.delete);
                    delCurABtnpressed.setBounds(0, 0, delCurABtnpressed.getMinimumWidth(), delCurABtnpressed.getMinimumHeight());
                    delCurABtn.setCompoundDrawables(null, delCurABtnpressed, null, null);
                    angleDelete();
                }
                break;
                case R.id.delAllASBtn: {
                    delAllABtn.setEnabled(false);
                    delAllABtnpressed = getResources().getDrawable(R.drawable.delete);
                    delAllABtnpressed.setBounds(0, 0, delAllABtnpressed.getMinimumWidth(), delAllABtnpressed.getMinimumHeight());
                    delAllABtn.setCompoundDrawables(null,delAllABtnpressed, null, null);
                    while(cursorAS.moveToFirst()){
                        data_ID = cursorAS.getInt(0);
                        angleDelete();
                    }
                }
                break;
                case R.id.exportCurASBtn: {
                    exportCurABtn.setEnabled(false);
                    exportCurABtnpressed = getResources().getDrawable(R.drawable.export);
                    exportCurABtnpressed.setBounds(0, 0, exportCurABtnpressed.getMinimumWidth(), exportCurABtnpressed.getMinimumHeight());
                    exportCurABtn.setCompoundDrawables(null,exportCurABtnpressed, null, null);
                    if(cursorAS.moveToFirst()){
                        ID = cursorAS.getInt(0);
                        if (ID == data_ID) {
                            str_date = cursorAS.getString(1);
                            str_company = cursorAS.getString(2);
                            str_number = cursorAS.getString(3);
                            str_sightNumber = cursorAS.getString(4);
                            str_sightLenght = cursorAS.getString(5);
                            str_sightType = cursorAS.getString(6);
                            str_sightLoad = cursorAS.getString(7);
                            str_testItem = cursorAS.getString(8);

                            angleMaxL = cursorAS.getFloat(9);
                            str_angleMaxL = myformat.format(angleMaxL);
                            angleMaxR = cursorAS.getFloat(10);
                            str_angleMaxR = myformat.format(angleMaxR);
                            angleMaxWL = cursorAS.getFloat(11);
                            str_angleMaxWL = myformat.format(angleMaxWL);
                            angleMaxWR = cursorAS.getFloat(12);
                            str_angleMaxWR = myformat.format(angleMaxWR);
                            angleMaxZHL = cursorAS.getFloat(13);
                            str_angleMaxZHL = myformat.format(angleMaxZHL);
                            angleMaxZHR = cursorAS.getFloat(14);
                            str_angleMaxZHR = myformat.format(angleMaxZHR);
                            angleMaxZNL = cursorAS.getFloat(15);
                            str_angleMaxZNL = myformat.format(angleMaxZNL);
                            angleMaxZNR = cursorAS.getFloat(16);
                            str_angleMaxZNR = myformat.format(angleMaxZNR);
                            CreatePdf();
                            Toast.makeText(SAngleSaveActivity.this, "数据已导出到手机根目录", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        while(cursorAS.moveToNext()) {//遍历数据表中的数据
                            ID = cursorAS.getInt(0);
                            if (ID == data_ID) {
                                str_date = cursorAS.getString(1);
                                str_company = cursorAS.getString(2);
                                str_number = cursorAS.getString(3);
                                str_sightNumber = cursorAS.getString(4);
                                str_sightLenght = cursorAS.getString(5);
                                str_sightType = cursorAS.getString(6);
                                str_sightLoad = cursorAS.getString(7);
                                str_testItem = cursorAS.getString(8);

                                angleMaxL = cursorAS.getFloat(9);
                                str_angleMaxL = myformat.format(angleMaxL);
                                angleMaxR = cursorAS.getFloat(10);
                                str_angleMaxR = myformat.format(angleMaxR);
                                angleMaxWL = cursorAS.getFloat(11);
                                str_angleMaxWL = myformat.format(angleMaxWL);
                                angleMaxWR = cursorAS.getFloat(12);
                                str_angleMaxWR = myformat.format(angleMaxWR);
                                angleMaxZHL = cursorAS.getFloat(13);
                                str_angleMaxZHL = myformat.format(angleMaxZHL);
                                angleMaxZHR = cursorAS.getFloat(14);
                                str_angleMaxZHR = myformat.format(angleMaxZHR);
                                angleMaxZNL = cursorAS.getFloat(15);
                                str_angleMaxZNL = myformat.format(angleMaxZNL);
                                angleMaxZNR = cursorAS.getFloat(16);
                                str_angleMaxZNR = myformat.format(angleMaxZNR);
                                CreatePdf();
                                Toast.makeText(SAngleSaveActivity.this, "数据已导出到手机根目录", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }
                break;
                case R.id.exportAllASBtn: {
                    exportAllABtn.setEnabled(false);
                    exportAllABtnpressed = getResources().getDrawable(R.drawable.export);
                    exportAllABtnpressed.setBounds(0, 0, exportAllABtnpressed.getMinimumWidth(), exportAllABtnpressed.getMinimumHeight());
                    exportAllABtn.setCompoundDrawables(null,exportAllABtnpressed, null, null);
                    CreatePdfAll();
                    Toast.makeText(SAngleSaveActivity.this, "数据已导出到手机根目录", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.backASBtn: {
                    Intent intent = new Intent(SAngleSaveActivity.this, SAngleActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.exitASBtn: {
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
        saveASDB = new SAngleDB(this);
        cursorAS = saveASDB.select();
        savelistView = (ListView)findViewById(R.id.savelistAS);
        savelistView.setAdapter(new SAngleSaveActivity.saveListAdapter(this, cursorAS));
        savelistView.setOnItemClickListener(this);

    }
    public void angleAdd(String str_TestItem,String str_angleMaxL,String str_angleMaxR,String str_angleMaxWL,String str_angleMaxWR,String str_angleMaxZHL,String str_angleMaxZHR,String str_angleMaxZNL,String str_angleMaxZNR)
    {
        Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
        String   str_date   =   formatter.format(curDate);
        saveASDB.insert(str_date,str_TestItem,str_angleMaxL,str_angleMaxR,str_angleMaxWL,str_angleMaxWR,str_angleMaxZHL,str_angleMaxZHR,str_angleMaxZNL,str_angleMaxZNR);
        cursorAS.requery();
        savelistView.invalidateViews();
    }
    public void angleDelete(){
        if (data_ID == 0) {
            return;
        }
        saveASDB.delete(data_ID);
        cursorAS.requery();
        savelistView.invalidateViews();
        Toast.makeText(this, "删除成功!", Toast.LENGTH_SHORT).show();
    }

    public void angleUpdate(){
        saveASDB.update(data_ID);
        cursorAS.requery();
        savelistView.invalidateViews();
        Toast.makeText(this, "Update Successed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cursorAS.moveToPosition(position);
        data_ID = cursorAS.getInt(0);
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
            convertView =getLayoutInflater().inflate(R.layout.angle_list, null);//加载布局
        }
        cursorAS.moveToPosition(position);
        TextView date_txt = (TextView) convertView.findViewById(R.id.date);
        TextView company_txt = (TextView) convertView.findViewById(R.id.company);
        TextView deviceNum_txt = (TextView) convertView.findViewById(R.id.deviceNum);
        TextView sightNumber_txt = (TextView) convertView.findViewById(R.id.chacheNumber);
        TextView sightLenght_txt= (TextView) convertView.findViewById(R.id.sightLenght);
        TextView sightType_txt = (TextView) convertView.findViewById(R.id.chacheType);
        TextView sightLoad_txt = (TextView) convertView.findViewById(R.id.chacheGroup);
        TextView testItem_txt = (TextView) convertView.findViewById(R.id.testItem);

        TextView angleMaxL_txt = (TextView) convertView.findViewById(R.id.angleMaxL);
        TextView angleMaxR_txt = (TextView) convertView.findViewById(R.id.angleMaxR);
        TextView angleMaxWL_txt = (TextView) convertView.findViewById(R.id.angleMaxWL);
        TextView angleMaxWR_txt = (TextView) convertView.findViewById(R.id.angleMaxWR);
        TextView angleMaxZHL_txt = (TextView) convertView.findViewById(R.id.angleMaxZHL);
        TextView angleMaxZHR_txt = (TextView) convertView.findViewById(R.id.angleMaxZHR);
        TextView angleMaxZNL_txt = (TextView) convertView.findViewById(R.id.angleMaxZNL);
        TextView angleMaxZNR_txt = (TextView) convertView.findViewById(R.id.angleMaxZNR);

        date_txt.setText("检测时间："+cursorAS.getString(1));
        company_txt.setText("受检单位："+cursorAS.getString(2));
        deviceNum_txt.setText("设备编号："+cursorAS.getString(3));
        sightNumber_txt.setText("车牌编号："+cursorAS.getString(4));
        sightLenght_txt.setText("车身长度："+cursorAS.getString(5)+"m");
        sightType_txt.setText("车辆类型："+cursorAS.getString(6));
        sightLoad_txt.setText("负载类型："+cursorAS.getString(7));
        testItem_txt.setText("测试项目:"+cursorAS.getString(8));
        angleMaxL_txt.setText("左轮最大转向角值:"+cursorAS.getString(9)+"°");
        angleMaxR_txt.setText("右轮最大转向角值:"+cursorAS.getString(10)+"°");
        angleMaxWL_txt.setText("左轮最大外倾角值:"+cursorAS.getString(11)+"°");
        angleMaxWR_txt.setText("右轮最大外倾角值:"+cursorAS.getString(12)+"°");
        angleMaxZHL_txt.setText("左轮主销后倾角值:"+cursorAS.getString(13)+"°");
        angleMaxZHR_txt.setText("右轮主销后倾角值:"+cursorAS.getString(14)+"°");
        angleMaxZNL_txt.setText("左轮主销内倾角值:"+cursorAS.getString(15)+"°");
        angleMaxZNR_txt.setText("右轮主销内倾角值:"+cursorAS.getString(16)+"°");

        return convertView;
    }

    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            delCurABtn.setEnabled(true);
            delCurABtnpressed = getResources().getDrawable(R.drawable.delete1);
            delCurABtnpressed.setBounds(0, 0, delCurABtnpressed.getMinimumWidth(), delCurABtnpressed.getMinimumHeight());
            delCurABtn.setCompoundDrawables(null, delCurABtnpressed, null, null);

            delAllABtn.setEnabled(true);
            delAllABtnpressed = getResources().getDrawable(R.drawable.delete1);
            delAllABtnpressed.setBounds(0, 0, delAllABtnpressed.getMinimumWidth(), delAllABtnpressed.getMinimumHeight());
            delAllABtn.setCompoundDrawables(null, delAllABtnpressed, null, null);

            printCurABtn.setEnabled(true);
            printCurABtnpressed = getResources().getDrawable(R.drawable.print1);
            printCurABtnpressed.setBounds(0, 0, printCurABtnpressed.getMinimumWidth(), printCurABtnpressed.getMinimumHeight());
            printCurABtn.setCompoundDrawables(null, printCurABtnpressed, null, null);

            printAllABtn.setEnabled(true);
            printAllABtnpressed = getResources().getDrawable(R.drawable.print1);
            printAllABtnpressed.setBounds(0, 0, printAllABtnpressed.getMinimumWidth(), printAllABtnpressed.getMinimumHeight());
            printAllABtn.setCompoundDrawables(null, printAllABtnpressed, null, null);

            exportCurABtn.setEnabled(true);
            exportCurABtnpressed = getResources().getDrawable(R.drawable.export1);
            exportCurABtnpressed.setBounds(0, 0, exportCurABtnpressed.getMinimumWidth(), exportCurABtnpressed.getMinimumHeight());
            exportCurABtn.setCompoundDrawables(null, exportCurABtnpressed, null, null);

            exportAllABtn.setEnabled(true);
            exportAllABtnpressed = getResources().getDrawable(R.drawable.export1);
            exportAllABtnpressed.setBounds(0, 0, exportAllABtnpressed.getMinimumWidth(), exportAllABtnpressed.getMinimumHeight());
            exportAllABtn.setCompoundDrawables(null, exportAllABtnpressed, null, null);
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
                    printDataService = new PrintDataService(SAngleSaveActivity.this,shares.getString("Printer",""));
                    Toast.makeText(SAngleSaveActivity.this,"打印机连接中...",Toast.LENGTH_LONG).show();
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
        str_date = cursorAS.getString(1);
        str_company = cursorAS.getString(2);
        str_number = cursorAS.getString(3);
        str_sightNumber= cursorAS.getString(4);
        str_sightLenght= cursorAS.getString(5);
        str_sightType= cursorAS.getString(6);
        str_sightLoad= cursorAS.getString(7);
        str_testItem = cursorAS.getString(8);

        angleMaxL= cursorAS.getFloat(9);
        str_angleMaxL= myformat.format(angleMaxL);
        angleMaxR= cursorAS.getFloat(10);
        str_angleMaxR= myformat.format(angleMaxR);
        angleMaxWL= cursorAS.getFloat(11);
        str_angleMaxWL= myformat.format(angleMaxWL);
        angleMaxWR= cursorAS.getFloat(12);
        str_angleMaxWR= myformat.format(angleMaxWR);
        angleMaxZHL= cursorAS.getFloat(13);
        str_angleMaxZHL= myformat.format(angleMaxZHL);
        angleMaxZHR= cursorAS.getFloat(14);
        str_angleMaxZHR= myformat.format(angleMaxZHR);
        angleMaxZNL= cursorAS.getFloat(15);
        str_angleMaxZNL= myformat.format(angleMaxZNL);
        angleMaxZNR= cursorAS.getFloat(16);
        str_angleMaxZNR= myformat.format(angleMaxZNR);

        printDataService.send("\n*******************************\n");
        printDataService.send("观光车辆/观光列车车轮转角检测结果");
        printDataService.send("\n*******************************\n");
        printDataService.send("检测时间"+": "+ str_date+"\n");
        printDataService.send("受检单位"+": "+str_company+"\n");//
        printDataService.send("设备编号"+": "+ str_number+"\n");//
        printDataService.send("车牌编号"+": "+ str_sightNumber+"\n");//
        printDataService.send("车身长"+": "+ str_sightLenght+"m"+"\n");//
        printDataService.send("车辆类型"+": "+ str_sightType+"\n");//
        printDataService.send("负载类型"+": "+ str_sightLoad+"\n");//
        printDataService.send("测试项目"+": "+ str_testItem+"\n");//
        printDataService.send("左轮最大转向角值"+": "+ str_angleMaxL+"°"+"\n");//
        printDataService.send("右轮最大转向角值"+": "+ str_angleMaxR+"°"+"\n");//
        printDataService.send("左轮最大外倾角值"+": "+ str_angleMaxWL+"°"+"\n");//
        printDataService.send("右轮最大外倾角值"+": "+ str_angleMaxWR+"°"+"\n");//
        printDataService.send("左轮主销后倾角值"+": "+ str_angleMaxZHL+"°"+"\n");//
        printDataService.send("右轮主销后倾角值"+": "+ str_angleMaxZHR+"°"+"\n");//
        printDataService.send("左轮主销内倾角值"+": "+ str_angleMaxZNL+"°"+"\n");//
        printDataService.send("右轮主销内倾角值"+": "+ str_angleMaxZNR+"°"+"\n");//
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
        SAngleSaveActivity.this.sendBroadcast(intent);

    }
    //创建PDF文件-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(SAngleSaveActivity.this);
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

                    cell.setPhrase(new Phrase("测试项目：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_testItem,setChineseFont()))    ;mtable.addCell(cell);
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

    private void CreatePdfAll(){
        verifyStoragePermissions(SAngleSaveActivity.this);
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
                    if(cursorAS.moveToFirst()){
                        str_date = cursorAS.getString(1);
                        str_company = cursorAS.getString(2);
                        str_number = cursorAS.getString(3);
                        str_sightNumber= cursorAS.getString(4);
                        str_sightLenght= cursorAS.getString(5);
                        str_sightType= cursorAS.getString(6);
                        str_sightLoad= cursorAS.getString(7);
                        str_testItem = cursorAS.getString(8);

                        angleMaxL= cursorAS.getFloat(9);
                        str_angleMaxL= myformat.format(angleMaxL);
                        angleMaxR= cursorAS.getFloat(10);
                        str_angleMaxR= myformat.format(angleMaxR);
                        angleMaxWL= cursorAS.getFloat(11);
                        str_angleMaxWL= myformat.format(angleMaxWL);
                        angleMaxWR= cursorAS.getFloat(12);
                        str_angleMaxWR= myformat.format(angleMaxWR);
                        angleMaxZHL= cursorAS.getFloat(13);
                        str_angleMaxZHL= myformat.format(angleMaxZHL);
                        angleMaxZHR= cursorAS.getFloat(14);
                        str_angleMaxZHR= myformat.format(angleMaxZHR);
                        angleMaxZNL= cursorAS.getFloat(15);
                        str_angleMaxZNL= myformat.format(angleMaxZNL);
                        angleMaxZNR= cursorAS.getFloat(16);
                        str_angleMaxZNR= myformat.format(angleMaxZNR);

                        pdfcontext = new Paragraph("观光车辆/观光列车车轮转角检测报告",setChineseTitleFont());
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
                        cell2.setMinimumHeight(50);
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

                        cell2.setPhrase(new Phrase("测试项目：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_testItem,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(" ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("左轮最大转向角值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleMaxL,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("°",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("右轮最大转向角值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleMaxR,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("°",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("左轮最大外倾角值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleMaxWL,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("°",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("右轮最大外倾角值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleMaxWR,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("°",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("左轮主销后倾角值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleMaxZHL,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("°",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("右轮主销后倾角值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleMaxZHR,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("°",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("左轮主销内倾角值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleMaxZNL,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("°",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("右轮主销内倾角值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleMaxZNR,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("°",setChineseFont()))    ;mtable2.addCell(cell2);
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
                        while(cursorAS.moveToNext()){//遍历数据表中的数据
                            str_date = cursorAS.getString(1);
                            str_company = cursorAS.getString(2);
                            str_number = cursorAS.getString(3);
                            str_sightNumber= cursorAS.getString(4);
                            str_sightLenght= cursorAS.getString(5);
                            str_sightType= cursorAS.getString(6);
                            str_sightLoad= cursorAS.getString(7);
                            str_testItem = cursorAS.getString(8);

                            angleMaxL= cursorAS.getFloat(9);
                            str_angleMaxL= myformat.format(angleMaxL);
                            angleMaxR= cursorAS.getFloat(10);
                            str_angleMaxR= myformat.format(angleMaxR);
                            angleMaxWL= cursorAS.getFloat(11);
                            str_angleMaxWL= myformat.format(angleMaxWL);
                            angleMaxWR= cursorAS.getFloat(12);
                            str_angleMaxWR= myformat.format(angleMaxWR);
                            angleMaxZHL= cursorAS.getFloat(13);
                            str_angleMaxZHL= myformat.format(angleMaxZHL);
                            angleMaxZHR= cursorAS.getFloat(14);
                            str_angleMaxZHR= myformat.format(angleMaxZHR);
                            angleMaxZNL= cursorAS.getFloat(15);
                            str_angleMaxZNL= myformat.format(angleMaxZNL);
                            angleMaxZNR= cursorAS.getFloat(16);
                            str_angleMaxZNR= myformat.format(angleMaxZNR);

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
                            cell.setMinimumHeight(50);
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

                            cell.setPhrase(new Phrase("测试项目：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_testItem,setChineseFont()))    ;mtable.addCell(cell);
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
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车车轮转角检测报告" +  File.separator + curDate.toString () +".pdf");
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