package com.ruiguan.Sight.SHForce;

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

public class SHForceSaveActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private SHForceDB saveSHFDB;
    private Cursor cursorSHF;

    private Button delCurSHFBtn;
    private Button delAllSHFBtn;
    private Button printCurSHFBtn;
    private Button printAllSHFBtn;
    private Button exportCurSHFBtn;
    private Button exportAllSHFBtn;
    private Button backSHFBtn;
    private Button exitSHFBtn;

    private Drawable delCurSHFBtnpressed;
    private Drawable delAllSHFBtnpressed;
    private Drawable printCurSHFBtnpressed;
    private Drawable printAllSHFBtnpressed;
    private Drawable exportCurSHFBtnpressed;
    private Drawable exportAllSHFBtnpressed;

    private String str_date;
    private String str_company;
    private String str_number;
    private String str_sightNumber;
    private String str_sightLenght;
    private String str_sightType;
    private String str_sightLoad;
    private String str_forceMax;
    private float forceMax;

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
        setContentView(R.layout.activity_shforce_save);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);

        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_sightNumber= sight_Input.getsightNumber();
        str_sightLenght= sight_Input.getsightLenght();
        str_sightType= sight_Input.getsightType();
        str_sightLoad= sight_Input.getsightLoad();

        printCurSHFBtn = (Button) findViewById(R.id.printCurSHFBtn);
        printAllSHFBtn = (Button) findViewById(R.id.printAllSHFBtn);
        delCurSHFBtn = (Button) findViewById(R.id.delCurSHFBtn);
        delAllSHFBtn= (Button) findViewById(R.id.delAllSHFBtn);
        exportCurSHFBtn= (Button) findViewById(R.id.exportCurSHFBtn);
        exportAllSHFBtn= (Button) findViewById(R.id.exportAllSHFBtn);
        backSHFBtn= (Button) findViewById(R.id.backSHFBtn);
        exitSHFBtn= (Button) findViewById(R.id.exitSHFBtn);
        View.OnClickListener bl = new SHForceSaveActivity.ButtonListener();
        setOnClickListener(printCurSHFBtn, bl);
        setOnClickListener(printAllSHFBtn, bl);
        setOnClickListener(delCurSHFBtn, bl);
        setOnClickListener(delAllSHFBtn, bl);
        setOnClickListener(exportCurSHFBtn, bl);
        setOnClickListener(exportAllSHFBtn, bl);
        setOnClickListener(backSHFBtn, bl);
        setOnClickListener(exitSHFBtn, bl);
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
        saveSHFDB.close();
        cursorSHF.close();
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
                case R.id.printCurSHFBtn: {
                    printCurSHFBtn.setEnabled(false);
                    printCurSHFBtnpressed = getResources().getDrawable(R.drawable.print);
                    printCurSHFBtnpressed.setBounds(0, 0, printCurSHFBtnpressed.getMinimumWidth(), printCurSHFBtnpressed.getMinimumHeight());
                    printCurSHFBtn.setCompoundDrawables(null, printCurSHFBtnpressed, null, null);

                    if (printDevice == null) {           //首次连接打印机
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(SHForceSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SHForceSaveActivity.this.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                    } else {          //打印数据
                        printDataService = new PrintDataService(SHForceSaveActivity.this,deviceAddress);
                        printDataService.putDevice(printDevice);
                        if(cursorSHF.moveToFirst()){
                            ID = cursorSHF.getInt(0);
                            if (ID == data_ID) {
                                str_date = cursorSHF.getString(1);
                                str_company = cursorSHF.getString(2);
                                str_number = cursorSHF.getString(3);

                                str_sightNumber= cursorSHF.getString(4);
                                str_sightLenght= cursorSHF.getString(5);
                                str_sightType= cursorSHF.getString(6);
                                str_sightLoad= cursorSHF.getString(7);

                                forceMax = cursorSHF.getFloat(8);
                                str_forceMax = myformat.format(forceMax );
                                PrintMeasureData();
                                break;
                            }
                            while(cursorSHF.moveToNext()){//遍历数据表中的数据
                                ID = cursorSHF.getInt(0);
                                if (ID == data_ID) {
                                    str_date = cursorSHF.getString(1);
                                    str_company = cursorSHF.getString(2);
                                    str_number = cursorSHF.getString(3);

                                    str_sightNumber= cursorSHF.getString(4);
                                    str_sightLenght= cursorSHF.getString(5);
                                    str_sightType= cursorSHF.getString(6);
                                    str_sightLoad= cursorSHF.getString(7);

                                    forceMax = cursorSHF.getFloat(8);
                                    str_forceMax = myformat.format(forceMax );
                                    PrintMeasureData();
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
                case R.id.printAllSHFBtn: {
                    printAllSHFBtn.setEnabled(false);
                    printAllSHFBtnpressed = getResources().getDrawable(R.drawable.print);
                    printAllSHFBtnpressed.setBounds(0, 0, printAllSHFBtnpressed.getMinimumWidth(), printAllSHFBtnpressed.getMinimumHeight());
                    printAllSHFBtn.setCompoundDrawables(null, printAllSHFBtnpressed, null, null);
                    if (printDataService == null) {           //首次连接打印机
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(SHForceSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SHForceSaveActivity.this.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                    } else {          //打印数据
                        printDataService.putDevice(printDevice);
                        if(cursorSHF.moveToFirst()){
                            str_date = cursorSHF.getString(1);
                            str_company = cursorSHF.getString(2);
                            str_number = cursorSHF.getString(3);

                            str_sightNumber= cursorSHF.getString(4);
                            str_sightLenght= cursorSHF.getString(5);
                            str_sightType= cursorSHF.getString(6);
                            str_sightLoad= cursorSHF.getString(7);

                            forceMax = cursorSHF.getFloat(8);
                            str_forceMax = myformat.format(forceMax );
                            PrintMeasureData();
                            while(cursorSHF.moveToNext()){//遍历数据表中的数据
                                str_date = cursorSHF.getString(1);
                                str_company = cursorSHF.getString(2);
                                str_number = cursorSHF.getString(3);

                                str_sightNumber= cursorSHF.getString(4);
                                str_sightLenght= cursorSHF.getString(5);
                                str_sightType= cursorSHF.getString(6);
                                str_sightLoad= cursorSHF.getString(7);

                                forceMax = cursorSHF.getFloat(8);
                                str_forceMax = myformat.format(forceMax );
                                PrintMeasureData();
                            }
                        }
                    }
                }
                break;
                case R.id.delCurSHFBtn: {
                    delCurSHFBtn.setEnabled(false);
                    delCurSHFBtnpressed = getResources().getDrawable(R.drawable.delete);
                    delCurSHFBtnpressed.setBounds(0, 0, delCurSHFBtnpressed.getMinimumWidth(), delCurSHFBtnpressed.getMinimumHeight());
                    delCurSHFBtn.setCompoundDrawables(null, delCurSHFBtnpressed, null, null);
                    forceDeleteSHF();
                }
                break;
                case R.id.delAllSHFBtn: {
                    delAllSHFBtn.setEnabled(false);
                    delAllSHFBtnpressed = getResources().getDrawable(R.drawable.delete);
                    delAllSHFBtnpressed.setBounds(0, 0, delAllSHFBtnpressed.getMinimumWidth(), delAllSHFBtnpressed.getMinimumHeight());
                    delAllSHFBtn.setCompoundDrawables(null,delAllSHFBtnpressed, null, null);
                    while(cursorSHF.moveToFirst()){
                        data_ID = cursorSHF.getInt(0);
                        forceDeleteSHF();
                    }
                }
                break;
                case R.id.exportCurSHFBtn: {
                    exportAllSHFBtn.setEnabled(false);
                    exportAllSHFBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportAllSHFBtnpressed.setBounds(0, 0, exportAllSHFBtnpressed.getMinimumWidth(), exportAllSHFBtnpressed.getMinimumHeight());
                    exportAllSHFBtn.setCompoundDrawables(null,exportAllSHFBtnpressed, null, null);
                    if(cursorSHF.moveToFirst()){
                        ID = cursorSHF.getInt(0);
                        if (ID == data_ID) {
                            str_date = cursorSHF.getString(1);
                            str_company = cursorSHF.getString(2);
                            str_number = cursorSHF.getString(3);

                            str_sightNumber= cursorSHF.getString(4);
                            str_sightLenght= cursorSHF.getString(5);
                            str_sightType= cursorSHF.getString(6);
                            str_sightLoad= cursorSHF.getString(7);

                            forceMax = cursorSHF.getFloat(8);
                            str_forceMax = myformat.format(forceMax );
                            CreatePdf();
                            Toast.makeText(SHForceSaveActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车手刹力检测报告", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        while(cursorSHF.moveToNext()){//遍历数据表中的数据
                            ID = cursorSHF.getInt(0);
                            if (ID == data_ID) {
                                str_date = cursorSHF.getString(1);
                                str_company = cursorSHF.getString(2);
                                str_number = cursorSHF.getString(3);

                                str_sightNumber = cursorSHF.getString(4);
                                str_sightLenght = cursorSHF.getString(5);
                                str_sightType = cursorSHF.getString(6);
                                str_sightLoad = cursorSHF.getString(7);

                                forceMax = cursorSHF.getFloat(8);
                                str_forceMax = myformat.format(forceMax);
                                CreatePdf();
                                Toast.makeText(SHForceSaveActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车手刹力检测报告", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }
                break;
                case R.id.exportAllSHFBtn: {
                    exportAllSHFBtn.setEnabled(false);
                    exportAllSHFBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportAllSHFBtnpressed.setBounds(0, 0, exportAllSHFBtnpressed.getMinimumWidth(), exportAllSHFBtnpressed.getMinimumHeight());
                    exportAllSHFBtn.setCompoundDrawables(null,exportAllSHFBtnpressed, null, null);
                    CreatePdfAll();
                    Toast.makeText(SHForceSaveActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车手刹力检测报告", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.backSHFBtn: {
                    Intent intent = new Intent(SHForceSaveActivity.this, SHForceActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.exitSHFBtn: {
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
        saveSHFDB = new SHForceDB(this);
        cursorSHF = saveSHFDB.select();
        savelistView = (ListView)findViewById(R.id.savelistSHF);
        savelistView.setAdapter(new SHForceSaveActivity.saveListAdapter(this, cursorSHF));
        savelistView.setOnItemClickListener(this);
    }
    public void forceAddSHF(String str_forceMax){
        Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
        String   str_date   =   formatter.format(curDate);
        saveSHFDB.insert(str_date,str_forceMax);
        cursorSHF.requery();
        savelistView.invalidateViews();
    }
    public void forceDeleteSHF(){
        if (data_ID == 0) {
            return;
        }
        saveSHFDB.delete(data_ID);
        cursorSHF.requery();
        savelistView.invalidateViews();
//        Toast.makeText(this, "删除成功!", Toast.LENGTH_SHORT).show();
    }

    public void forceUpdate(){
        saveSHFDB.update(data_ID);
        cursorSHF.requery();
        savelistView.invalidateViews();
        Toast.makeText(this, "Update Successed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cursorSHF.moveToPosition(position);
        data_ID = cursorSHF.getInt(0);
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
        cursorSHF.moveToPosition(position);

        TextView date_txt = (TextView) convertView.findViewById(R.id.dateSave);
        TextView company_txt = (TextView) convertView.findViewById(R.id.companySave);
        TextView deviceNum_txt = (TextView) convertView.findViewById(R.id.deviceNumSave);

        TextView sightNumber_txt = (TextView) convertView.findViewById(R.id.chacheNumber);
        TextView sightLenght_txt = (TextView) convertView.findViewById(R.id.chacheType);
        TextView sightType_txt = (TextView) convertView.findViewById(R.id.chacheGroup);
        TextView sightLoad_txt = (TextView) convertView.findViewById(R.id.breakSpeed);

        TextView forceMax_txt = (TextView) convertView.findViewById(R.id.ratedSpeed);

        date_txt.setText("检测时间："+cursorSHF.getString(1));
        company_txt.setText("受检单位："+cursorSHF.getString(2));
        deviceNum_txt.setText("设备编号："+cursorSHF.getString(3));

        sightNumber_txt.setText("车牌编号："+cursorSHF.getString(4));
        sightLenght_txt.setText("车身长度："+cursorSHF.getString(5)+"m");
        sightType_txt.setText("车辆类型："+cursorSHF.getString(6));
        sightLoad_txt.setText("负载类型："+cursorSHF.getString(7));

        forceMax_txt.setText("最大受力值："+cursorSHF.getString(8)+"N");
        return convertView;
    }

    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            delCurSHFBtn.setEnabled(true);
            delCurSHFBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delCurSHFBtnpressed.setBounds(0, 0, delCurSHFBtnpressed.getMinimumWidth(), delCurSHFBtnpressed.getMinimumHeight());
            delCurSHFBtn.setCompoundDrawables(null, delCurSHFBtnpressed, null, null);

            delAllSHFBtn.setEnabled(true);
            delAllSHFBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delAllSHFBtnpressed.setBounds(0, 0, delAllSHFBtnpressed.getMinimumWidth(), delAllSHFBtnpressed.getMinimumHeight());
            delAllSHFBtn.setCompoundDrawables(null, delAllSHFBtnpressed, null, null);

            printCurSHFBtn.setEnabled(true);
            printCurSHFBtnpressed = getResources().getDrawable(R.drawable.print1);
            printCurSHFBtnpressed.setBounds(0, 0, printCurSHFBtnpressed.getMinimumWidth(), printCurSHFBtnpressed.getMinimumHeight());
            printCurSHFBtn.setCompoundDrawables(null, printCurSHFBtnpressed, null, null);

            printAllSHFBtn.setEnabled(true);
            printAllSHFBtnpressed = getResources().getDrawable(R.drawable.print1);
            printAllSHFBtnpressed.setBounds(0, 0, printAllSHFBtnpressed.getMinimumWidth(), printAllSHFBtnpressed.getMinimumHeight());
            printAllSHFBtn.setCompoundDrawables(null, printAllSHFBtnpressed, null, null);

            exportCurSHFBtn.setEnabled(true);
            exportCurSHFBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportCurSHFBtnpressed.setBounds(0, 0, exportCurSHFBtnpressed.getMinimumWidth(), exportCurSHFBtnpressed.getMinimumHeight());
            exportCurSHFBtn.setCompoundDrawables(null, exportCurSHFBtnpressed, null, null);

            exportAllSHFBtn.setEnabled(true);
            exportAllSHFBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportAllSHFBtnpressed.setBounds(0, 0, exportAllSHFBtnpressed.getMinimumWidth(), exportAllSHFBtnpressed.getMinimumHeight());
            exportAllSHFBtn.setCompoundDrawables(null, exportAllSHFBtnpressed, null, null);
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
                    printDataService = new PrintDataService(SHForceSaveActivity.this,shares.getString("Printer",""));
                    Toast.makeText(SHForceSaveActivity.this,"打印机连接中...",Toast.LENGTH_LONG).show();
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
        str_date = cursorSHF.getString(1);
        str_company = cursorSHF.getString(2);
        str_number = cursorSHF.getString(3);

        str_sightNumber= cursorSHF.getString(4);
        str_sightLenght= cursorSHF.getString(5);
        str_sightType= cursorSHF.getString(6);
        str_sightLoad= cursorSHF.getString(7);

        forceMax = cursorSHF.getFloat(8);
        str_forceMax = myformat.format(forceMax );

        printDataService.send("\n*******************************\n");
        printDataService.send("观光车辆/观光列车手刹力检测结果");
        printDataService.send("\n*******************************\n");
        printDataService.send("检测时间"+": "+ str_date+"\n");
        printDataService.send("受检单位"+": "+str_company+"\n");//
        printDataService.send("设备编号"+": "+ str_number+"\n");//
        printDataService.send("车牌编号"+": "+ str_sightNumber+"\n");//
        printDataService.send("车身长"+": "+ str_sightLenght+"m"+"\n");//
        printDataService.send("车辆类型"+": "+ str_sightType+"\n");//
        printDataService.send("负载类型"+": "+ str_sightLoad+"\n");//
        printDataService.send("最大受力值"+": "+str_forceMax+"N"+"\n");//
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
        SHForceSaveActivity.this.sendBroadcast(intent);

    }
    //创建PDF文件-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(SHForceSaveActivity.this);
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
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车手刹力检测报告" + File.separator + str_date+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
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

                    cell.setPhrase(new Phrase("最大受力值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_forceMax,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);

                    doc.add(mtable);
                    doc.close();
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车手刹力检测报告" +  File.separator + str_date +".pdf");
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
        verifyStoragePermissions(SHForceSaveActivity.this);
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
                    if(cursorSHF.moveToFirst()){
                        str_date = cursorSHF.getString(1);
                        str_company = cursorSHF.getString(2);
                        str_number = cursorSHF.getString(3);

                        str_sightNumber= cursorSHF.getString(4);
                        str_sightLenght= cursorSHF.getString(5);
                        str_sightType= cursorSHF.getString(6);
                        str_sightLoad= cursorSHF.getString(7);

                        forceMax = cursorSHF.getFloat(8);
                        str_forceMax = myformat.format(forceMax );

                        pdfcontext = new Paragraph("观光车辆/观光列车手刹力检测报告",setChineseTitleFont());
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
                        cell2.setMinimumHeight(35);
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

                        cell2.setPhrase(new Phrase("最大受力值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_forceMax,setChineseFont()))    ;mtable2.addCell(cell2);
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
                        while(cursorSHF.moveToNext()){//遍历数据表中的数据
                            str_date = cursorSHF.getString(1);
                            str_company = cursorSHF.getString(2);
                            str_number = cursorSHF.getString(3);

                            str_sightNumber= cursorSHF.getString(4);
                            str_sightLenght= cursorSHF.getString(5);
                            str_sightType= cursorSHF.getString(6);
                            str_sightLoad= cursorSHF.getString(7);

                            forceMax = cursorSHF.getFloat(8);
                            str_forceMax = myformat.format(forceMax );
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
                            cell.setMinimumHeight(35);
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

                            cell.setPhrase(new Phrase("最大受力值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_forceMax,setChineseFont()))    ;mtable.addCell(cell);
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
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车手刹力检测报告" +  File.separator + curDate.toString () +".pdf");
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