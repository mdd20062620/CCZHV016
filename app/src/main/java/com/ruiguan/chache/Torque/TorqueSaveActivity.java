package com.ruiguan.chache.Torque;

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

import static com.ruiguan.activities.MenuActivity.input_data;
import static com.ruiguan.chache.ChacheActivity.chache_Input;

public class TorqueSaveActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private TorqueDB saveTDB;
    private Cursor cursorT;

    private Button delCurTBtn;
    private Button delAllTBtn;
    private Button printCurTBtn;
    private Button printAllTBtn;
    private Button exportCurTBtn;
    private Button exportAllTBtn;
    private Button backTBtn;
    private Button exitTBtn;

    private Drawable delCurTBtnpressed;
    private Drawable delAllTBtnpressed;
    private Drawable printCurTBtnpressed;
    private Drawable printAllTBtnpressed;
    private Drawable exportCurTBtnpressed;
    private Drawable exportAllTBtnpressed;

    private String str_date;
    private String str_company;
    private String str_number;
    private String str_chacheNumber;
    private String str_chacheType;
    private String str_chacheGroup;

    private String str_angleMaxL;
    private String str_angleMaxR;
    private String str_forceMaxL;
    private String str_forceMaxR;
    private String str_forceErrMax;
    private String str_angleErrMax;

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
        setContentView(R.layout.activity_torque_save);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);

        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_chacheNumber= chache_Input.getchacheNumber();
        str_chacheType= chache_Input.getchacheType();
        str_chacheGroup= chache_Input.getchacheGroup();

        printCurTBtn = (Button) findViewById(R.id.printCurTBtn);
        printAllTBtn = (Button) findViewById(R.id.printAllTBtn);
        delCurTBtn = (Button) findViewById(R.id.delCurTBtn);
        delAllTBtn= (Button) findViewById(R.id.delAllTBtn);
        exportCurTBtn= (Button) findViewById(R.id.exportCurTBtn);
        exportAllTBtn= (Button) findViewById(R.id.exportAllTBtn);
        backTBtn= (Button) findViewById(R.id.backTBtn);
        exitTBtn= (Button) findViewById(R.id.exitTBtn);
        View.OnClickListener bl = new TorqueSaveActivity.ButtonListener();
        setOnClickListener(printCurTBtn, bl);
        setOnClickListener(printAllTBtn, bl);
        setOnClickListener(delCurTBtn, bl);
        setOnClickListener(delAllTBtn, bl);
        setOnClickListener(exportCurTBtn, bl);
        setOnClickListener(exportAllTBtn, bl);
        setOnClickListener(backTBtn, bl);
        setOnClickListener(exitTBtn, bl);
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
        saveTDB.close();
        cursorT.close();
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
                case R.id.printCurTBtn: {
                    printCurTBtn.setEnabled(false);
                    printCurTBtnpressed = getResources().getDrawable(R.drawable.print);
                    printCurTBtnpressed.setBounds(0, 0, printCurTBtnpressed.getMinimumWidth(), printCurTBtnpressed.getMinimumHeight());
                    printCurTBtn.setCompoundDrawables(null, printCurTBtnpressed, null, null);

                    if (printDevice == null) {           //首次连接打印机
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(TorqueSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TorqueSaveActivity.this.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                    } else {          //打印数据
                        printDataService = new PrintDataService(TorqueSaveActivity.this,deviceAddress);
                        printDataService.putDevice(printDevice);
                        if(cursorT.moveToFirst()){
                            ID = cursorT.getInt(0);
                            if (ID == data_ID) {
                                str_date = cursorT.getString(1);
                                str_company = cursorT.getString(2);
                                str_number = cursorT.getString(3);

                                str_chacheNumber= cursorT.getString(4);
                                str_chacheType= cursorT.getString(5);
                                str_chacheGroup= cursorT.getString(6);

                                str_angleMaxL= cursorT.getString(7);
                                str_angleMaxR= cursorT.getString(8);
                                str_forceMaxL= cursorT.getString(9);
                                str_forceMaxR= cursorT.getString(10);
                                str_angleErrMax= cursorT.getString(11);
                                str_forceErrMax= cursorT.getString(12);

                                PrintMeasureData();
                                break;
                            }
                            while(cursorT.moveToNext()){//遍历数据表中的数据
                                ID = cursorT.getInt(0);
                                if (ID == data_ID) {
                                    str_date = cursorT.getString(1);
                                    str_company = cursorT.getString(2);
                                    str_number = cursorT.getString(3);

                                    str_chacheNumber= cursorT.getString(4);
                                    str_chacheType= cursorT.getString(5);
                                    str_chacheGroup= cursorT.getString(6);

                                    str_angleMaxL= cursorT.getString(7);
                                    str_angleMaxR= cursorT.getString(8);
                                    str_forceMaxL= cursorT.getString(9);
                                    str_forceMaxR= cursorT.getString(10);
                                    str_angleErrMax= cursorT.getString(11);
                                    str_forceErrMax= cursorT.getString(12);

                                    PrintMeasureData();
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
                case R.id.printAllTBtn: {
                    printAllTBtn.setEnabled(false);
                    printAllTBtnpressed = getResources().getDrawable(R.drawable.print);
                    printAllTBtnpressed.setBounds(0, 0, printAllTBtnpressed.getMinimumWidth(), printAllTBtnpressed.getMinimumHeight());
                    printAllTBtn.setCompoundDrawables(null, printAllTBtnpressed, null, null);
                    if (printDataService == null) {           //首次连接打印机
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(TorqueSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TorqueSaveActivity.this.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                    } else {          //打印数据
                        printDataService.putDevice(printDevice);
                        if(cursorT.moveToFirst()){
                            str_date = cursorT.getString(1);
                            str_company = cursorT.getString(2);
                            str_number = cursorT.getString(3);

                            str_chacheNumber= cursorT.getString(4);
                            str_chacheType= cursorT.getString(5);
                            str_chacheGroup= cursorT.getString(6);

                            str_angleMaxL= cursorT.getString(7);
                            str_angleMaxR= cursorT.getString(8);
                            str_forceMaxL= cursorT.getString(9);
                            str_forceMaxR= cursorT.getString(10);
                            str_angleErrMax= cursorT.getString(11);
                            str_forceErrMax= cursorT.getString(12);
                            PrintMeasureData();
                            while(cursorT.moveToNext()){//遍历数据表中的数据
                                str_date = cursorT.getString(1);
                                str_company = cursorT.getString(2);
                                str_number = cursorT.getString(3);

                                str_chacheNumber= cursorT.getString(4);
                                str_chacheType= cursorT.getString(5);
                                str_chacheGroup= cursorT.getString(6);

                                str_angleMaxL= cursorT.getString(7);
                                str_angleMaxR= cursorT.getString(8);
                                str_forceMaxL= cursorT.getString(9);
                                str_forceMaxR= cursorT.getString(10);
                                str_angleErrMax= cursorT.getString(11);
                                str_forceErrMax= cursorT.getString(12);
                                PrintMeasureData();
                            }
                        }
                    }
                }
                break;
                case R.id.delCurTBtn: {
                    delCurTBtn.setEnabled(false);
                    delCurTBtnpressed = getResources().getDrawable(R.drawable.delete);
                    delCurTBtnpressed.setBounds(0, 0, delCurTBtnpressed.getMinimumWidth(), delCurTBtnpressed.getMinimumHeight());
                    delCurTBtn.setCompoundDrawables(null, delCurTBtnpressed, null, null);
                    torqueDelete();
                }
                break;
                case R.id.delAllTBtn: {
                    delAllTBtn.setEnabled(false);
                    delAllTBtnpressed = getResources().getDrawable(R.drawable.delete);
                    delAllTBtnpressed.setBounds(0, 0, delAllTBtnpressed.getMinimumWidth(), delAllTBtnpressed.getMinimumHeight());
                    delAllTBtn.setCompoundDrawables(null,delAllTBtnpressed, null, null);
                    while(cursorT.moveToFirst()){
                        data_ID = cursorT.getInt(0);
                        torqueDelete();
                    }
                }
                break;
                case R.id.exportCurTBtn: {
                    exportCurTBtn.setEnabled(false);
                    exportCurTBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportCurTBtnpressed.setBounds(0, 0, exportCurTBtnpressed.getMinimumWidth(), exportCurTBtnpressed.getMinimumHeight());
                    exportCurTBtn.setCompoundDrawables(null,exportCurTBtnpressed, null, null);
                    if(cursorT.moveToFirst()){
                        ID = cursorT.getInt(0);
                        if (ID == data_ID) {
                            str_date = cursorT.getString(1);
                            str_company = cursorT.getString(2);
                            str_number = cursorT.getString(3);

                            str_chacheNumber= cursorT.getString(4);
                            str_chacheType= cursorT.getString(5);
                            str_chacheGroup= cursorT.getString(6);

                            str_angleMaxL= cursorT.getString(7);
                            str_angleMaxR= cursorT.getString(8);
                            str_forceMaxL= cursorT.getString(9);
                            str_forceMaxR= cursorT.getString(10);
                            str_angleErrMax= cursorT.getString(11);
                            str_forceErrMax= cursorT.getString(12);
                            CreatePdf();
                            Toast.makeText(TorqueSaveActivity.this, "数据已导出到手机根目录/Documents/叉车方向盘转向力与转向角度检测报告", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        while(cursorT.moveToNext()){//遍历数据表中的数据
                            ID = cursorT.getInt(0);
                            if (ID == data_ID) {
                                str_date = cursorT.getString(1);
                                str_company = cursorT.getString(2);
                                str_number = cursorT.getString(3);

                                str_chacheNumber= cursorT.getString(4);
                                str_chacheType= cursorT.getString(5);
                                str_chacheGroup= cursorT.getString(6);

                                str_angleMaxL= cursorT.getString(7);
                                str_angleMaxR= cursorT.getString(8);
                                str_forceMaxL= cursorT.getString(9);
                                str_forceMaxR= cursorT.getString(10);
                                str_angleErrMax= cursorT.getString(11);
                                str_forceErrMax= cursorT.getString(12);
                                CreatePdf();
                                Toast.makeText(TorqueSaveActivity.this, "数据已导出到手机根目录/Documents/叉车方向盘转向力与转向角度检测报告", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }
                break;
                case R.id.exportAllTBtn: {
                    exportAllTBtn.setEnabled(false);
                    exportAllTBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportAllTBtnpressed.setBounds(0, 0, exportAllTBtnpressed.getMinimumWidth(), exportAllTBtnpressed.getMinimumHeight());
                    exportAllTBtn.setCompoundDrawables(null,exportAllTBtnpressed, null, null);
                    CreatePdfAll();
                    Toast.makeText(TorqueSaveActivity.this, "数据已导出到手机根目录/Documents/叉车方向盘转向力与转向角度检测报告", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.backTBtn: {
                    Intent intent = new Intent(TorqueSaveActivity.this, TorqueActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.exitTBtn: {
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
        saveTDB = new TorqueDB(this);
        cursorT = saveTDB.select();
        savelistView = (ListView)findViewById(R.id.savelistT);
        savelistView.setAdapter(new TorqueSaveActivity.saveListAdapter(this, cursorT));
        savelistView.setOnItemClickListener(this);
    }
    public void torqueAdd(String str_angleMaxL,String str_angleMaxR,String str_forceMaxL,String str_forceMaxR,String str_angleMax,String str_forceMax){
        Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
        String   str_date   =   formatter.format(curDate);
        saveTDB.insert(str_date,str_angleMaxL,str_angleMaxR,str_forceMaxL,str_forceMaxR,str_angleMax,str_forceMax);
        cursorT.requery();
        savelistView.invalidateViews();
    }
    public void torqueDelete(){
        if (data_ID == 0) {
            return;
        }
        saveTDB.delete(data_ID);
        cursorT.requery();
        savelistView.invalidateViews();
        Toast.makeText(this, "删除成功!", Toast.LENGTH_SHORT).show();
    }

    public void torqueUpdate(){
        saveTDB.update(data_ID);
        cursorT.requery();
        savelistView.invalidateViews();
        Toast.makeText(this, "Update Successed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cursorT.moveToPosition(position);
        data_ID = cursorT.getInt(0);
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
        cursorT.moveToPosition(position);
        TextView date_txt = (TextView) convertView.findViewById(R.id.dateSave);
        TextView company_txt = (TextView) convertView.findViewById(R.id.companySave);
        TextView deviceNum_txt = (TextView) convertView.findViewById(R.id.deviceNumSave);

        TextView chacheNumber_txt = (TextView) convertView.findViewById(R.id.chacheNumber);
        TextView chacheType_txt = (TextView) convertView.findViewById(R.id.chacheType);
        TextView chacheGroup_txt = (TextView) convertView.findViewById(R.id.chacheGroup);

        TextView angleMaxL_txt = (TextView) convertView.findViewById(R.id.breakSpeed);
        TextView angleMaxR_txt = (TextView) convertView.findViewById(R.id.ratedSpeed);

        TextView forceMaxL_txt = (TextView) convertView.findViewById(R.id.breakDis);
        TextView forceMaxR_txt = (TextView) convertView.findViewById(R.id.breakTime);

        TextView breakSpeed_txt = (TextView) convertView.findViewById(R.id.breakForce);
        TextView ASpeedMax_txt = (TextView) convertView.findViewById(R.id.breaklength);

        date_txt.setText("检测时间："+cursorT.getString(1));
        company_txt.setText("受检单位："+cursorT.getString(2));
        deviceNum_txt.setText("设备编号："+cursorT.getString(3));

        chacheNumber_txt.setText("车牌编号："+cursorT.getString(4));
        chacheType_txt.setText("车辆类型："+cursorT.getString(5));
        chacheGroup_txt.setText("车辆组别："+cursorT.getString(6));

        angleMaxL_txt.setText("左最大角度值："+cursorT.getString(7)+"°");
        angleMaxR_txt.setText("右最大角度值："+cursorT.getString(8)+"°");

        forceMaxL_txt.setText("左最大受力值："+cursorT.getString(9)+"N");
        forceMaxR_txt.setText("右最大受力值："+cursorT.getString(10)+"N");

        breakSpeed_txt.setText("最大角度差值："+cursorT.getString(11)+"°");
        ASpeedMax_txt.setText("最大受力差值："+cursorT.getString(12)+"N");

        return convertView;
    }

    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            delCurTBtn.setEnabled(true);
            delCurTBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delCurTBtnpressed.setBounds(0, 0, delCurTBtnpressed.getMinimumWidth(), delCurTBtnpressed.getMinimumHeight());
            delCurTBtn.setCompoundDrawables(null, delCurTBtnpressed, null, null);

            delAllTBtn.setEnabled(true);
            delAllTBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delAllTBtnpressed.setBounds(0, 0, delAllTBtnpressed.getMinimumWidth(), delAllTBtnpressed.getMinimumHeight());
            delAllTBtn.setCompoundDrawables(null, delAllTBtnpressed, null, null);

            printCurTBtn.setEnabled(true);
            printCurTBtnpressed = getResources().getDrawable(R.drawable.print1);
            printCurTBtnpressed.setBounds(0, 0, printCurTBtnpressed.getMinimumWidth(), printCurTBtnpressed.getMinimumHeight());
            printCurTBtn.setCompoundDrawables(null, printCurTBtnpressed, null, null);

            printAllTBtn.setEnabled(true);
            printAllTBtnpressed = getResources().getDrawable(R.drawable.print1);
            printAllTBtnpressed.setBounds(0, 0, printAllTBtnpressed.getMinimumWidth(), printAllTBtnpressed.getMinimumHeight());
            printAllTBtn.setCompoundDrawables(null, printAllTBtnpressed, null, null);

            exportCurTBtn.setEnabled(true);
            exportCurTBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportCurTBtnpressed.setBounds(0, 0, exportCurTBtnpressed.getMinimumWidth(), exportCurTBtnpressed.getMinimumHeight());
            exportCurTBtn.setCompoundDrawables(null, exportCurTBtnpressed, null, null);

            exportAllTBtn.setEnabled(true);
            exportAllTBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportAllTBtnpressed.setBounds(0, 0, exportAllTBtnpressed.getMinimumWidth(), exportAllTBtnpressed.getMinimumHeight());
            exportAllTBtn.setCompoundDrawables(null, exportAllTBtnpressed, null, null);
        }
    };
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
                    printDataService = new PrintDataService(TorqueSaveActivity.this,shares.getString("Printer",""));
                    Toast.makeText(TorqueSaveActivity.this,"打印机连接中...",Toast.LENGTH_LONG).show();
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
        str_date = cursorT.getString(1);
        str_company = cursorT.getString(2);
        str_number = cursorT.getString(3);

        str_chacheNumber= cursorT.getString(4);
        str_chacheType= cursorT.getString(5);
        str_chacheGroup= cursorT.getString(6);

        str_angleMaxL= cursorT.getString(7);
        str_angleMaxR= cursorT.getString(8);
        str_forceMaxL= cursorT.getString(9);
        str_forceMaxR= cursorT.getString(10);
        str_angleErrMax= cursorT.getString(11);
        str_forceErrMax= cursorT.getString(12);

        printDataService.send("\n*******************************\n");
        printDataService.send("叉车方向盘转向力与转向角度检测结果");
        printDataService.send("\n*******************************\n");
        printDataService.send("检测时间"+": "+ str_date+"\n");
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
        TorqueSaveActivity.this.sendBroadcast(intent);
    }
    //创建PDF文件-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(TorqueSaveActivity.this);
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
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车方向盘转向力与转向角度检测报告" + File.separator + str_date+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
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
                    cell.setPhrase(new Phrase(str_date,setChineseFont()))    ;mtable.addCell(cell);
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
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车方向盘转向力与转向角度检测报告" +  File.separator + str_date +".pdf");
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
        verifyStoragePermissions(TorqueSaveActivity.this);
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
                    if(cursorT.moveToFirst()){
                        str_date = cursorT.getString(1);
                        str_company = cursorT.getString(2);
                        str_number = cursorT.getString(3);

                        str_chacheNumber= cursorT.getString(4);
                        str_chacheType= cursorT.getString(5);
                        str_chacheGroup= cursorT.getString(6);

                        str_angleMaxL= cursorT.getString(7);
                        str_angleMaxR= cursorT.getString(8);
                        str_forceMaxL= cursorT.getString(9);
                        str_forceMaxR= cursorT.getString(10);
                        str_angleErrMax= cursorT.getString(11);
                        str_forceErrMax= cursorT.getString(12);

                        pdfcontext = new Paragraph("叉车方向盘转向力与转向角度检测报告",setChineseTitleFont());
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
                        cell2.setPhrase(new Phrase(str_chacheNumber,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("车辆类型：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_chacheType,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("车辆组别：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_chacheGroup,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("左最大角度值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleMaxL,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("°",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("右最大角度值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleMaxR,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("°",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("左最大受力值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_forceMaxL,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("N",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("右最大受力值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_forceMaxR,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("N",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("最大角度差值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleErrMax,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("°",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("最大受力差值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_forceErrMax,setChineseFont()))    ;mtable2.addCell(cell2);
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
                        while(cursorT.moveToNext()){//遍历数据表中的数据
                            str_date = cursorT.getString(1);
                            str_company = cursorT.getString(2);
                            str_number = cursorT.getString(3);

                            str_chacheNumber= cursorT.getString(4);
                            str_chacheType= cursorT.getString(5);
                            str_chacheGroup= cursorT.getString(6);

                            str_angleMaxL= cursorT.getString(7);
                            str_angleMaxR= cursorT.getString(8);
                            str_forceMaxL= cursorT.getString(9);
                            str_forceMaxR= cursorT.getString(10);
                            str_angleErrMax= cursorT.getString(11);
                            str_forceErrMax= cursorT.getString(12);

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
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车方向盘转向力与转向角度检测报告" +  File.separator + curDate.toString () +".pdf");
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
