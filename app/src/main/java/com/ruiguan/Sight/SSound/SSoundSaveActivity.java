package com.ruiguan.Sight.SSound;

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

public class SSoundSaveActivity extends BaseActivity implements AdapterView.OnItemClickListener {
        private SSoundDB saveSSoundDB;
        private Cursor cursorSSound;

        private Button delCurSSoundBtn;
        private Button delAllSSoundBtn;
        private Button printCurSSoundBtn;
        private Button printAllSSoundBtn;
        private Button exportCurSSoundBtn;
        private Button exportAllSSoundBtn;
        private Button backSSoundBtn;
        private Button exitSSoundBtn;

        private Drawable delCurSSoundBtnpressed;
        private Drawable delAllSSoundBtnpressed;
        private Drawable printCurSSoundBtnpressed;
        private Drawable printAllSSoundBtnpressed;
        private Drawable exportCurSSoundBtnpressed;
        private Drawable exportAllSSoundBtnpressed;

        private String str_date;
        private String str_company;
        private String str_number;
        private String str_sightNumber;
        private String str_sightLenght;
        private String str_sightType;
        private String str_sightLoad;
        private String str_SSoundMax;
        private float SSoundMax;

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
            setContentView(R.layout.activity_ssound_save);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ActivityCollector.addActivity(this);

            str_company=input_data.getCom();
            str_number=input_data.getNumber();
            str_sightNumber= sight_Input.getsightNumber();
            str_sightLenght= sight_Input.getsightLenght();
            str_sightType= sight_Input.getsightType();
            str_sightLoad= sight_Input.getsightLoad();

            printCurSSoundBtn = (Button) findViewById(R.id.printCurSSoundBtn);
            printAllSSoundBtn = (Button) findViewById(R.id.printAllSSoundBtn);
            delCurSSoundBtn = (Button) findViewById(R.id.delCurSSoundBtn);
            delAllSSoundBtn= (Button) findViewById(R.id.delAllSSoundBtn);
            exportCurSSoundBtn= (Button) findViewById(R.id.exportCurSSoundBtn);
            exportAllSSoundBtn= (Button) findViewById(R.id.exportAllSSoundBtn);
            backSSoundBtn= (Button) findViewById(R.id.backSSoundBtn);
            exitSSoundBtn= (Button) findViewById(R.id.exitSSoundBtn);
            View.OnClickListener bl = new SSoundSaveActivity.ButtonListener();
            setOnClickListener(printCurSSoundBtn, bl);
            setOnClickListener(printAllSSoundBtn, bl);
            setOnClickListener(delCurSSoundBtn, bl);
            setOnClickListener(delAllSSoundBtn, bl);
            setOnClickListener(exportCurSSoundBtn, bl);
            setOnClickListener(exportAllSSoundBtn, bl);
            setOnClickListener(backSSoundBtn, bl);
            setOnClickListener(exitSSoundBtn, bl);
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
            ActivityCollector.removeActivity(this);
            saveSSoundDB.close();
            cursorSSound.close();
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
                    case R.id.printCurSSoundBtn: {
                        printCurSSoundBtn.setEnabled(false);
                        printCurSSoundBtnpressed = getResources().getDrawable(R.drawable.print);
                        printCurSSoundBtnpressed.setBounds(0, 0, printCurSSoundBtnpressed.getMinimumWidth(), printCurSSoundBtnpressed.getMinimumHeight());
                        printCurSSoundBtn.setCompoundDrawables(null, printCurSSoundBtnpressed, null, null);
                        if (printDevice == null) {           //首次连接打印机
                            SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                            if (!shares.getBoolean("BondPrinter", false)) {
                                Toast.makeText(SSoundSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SSoundSaveActivity.this.getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                        } else {          //打印数据
                            printDataService = new PrintDataService(SSoundSaveActivity.this,deviceAddress);
                            printDataService.putDevice(printDevice);
                            if(cursorSSound.moveToFirst()){
                                ID = cursorSSound.getInt(0);
                                if (ID == data_ID) {
                                    str_date = cursorSSound.getString(1);
                                    str_company = cursorSSound.getString(2);
                                    str_number = cursorSSound.getString(3);

                                    str_sightNumber= cursorSSound.getString(4);
                                    str_sightLenght= cursorSSound.getString(5);
                                    str_sightType= cursorSSound.getString(6);
                                    str_sightLoad= cursorSSound.getString(7);

                                    SSoundMax = cursorSSound.getFloat(8);
                                    str_SSoundMax = myformat.format(SSoundMax );
                                    PrintMeasureData();
                                    break;
                                }
                                while(cursorSSound.moveToNext()){//遍历数据表中的数据
                                    ID = cursorSSound.getInt(0);
                                    if (ID == data_ID) {
                                        str_date = cursorSSound.getString(1);
                                        str_company = cursorSSound.getString(2);
                                        str_number = cursorSSound.getString(3);

                                        str_sightNumber= cursorSSound.getString(4);
                                        str_sightLenght= cursorSSound.getString(5);
                                        str_sightType= cursorSSound.getString(6);
                                        str_sightLoad= cursorSSound.getString(7);

                                        SSoundMax = cursorSSound.getFloat(8);
                                        str_SSoundMax = myformat.format(SSoundMax );
                                        PrintMeasureData();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                    case R.id.printAllSSoundBtn: {
                        printAllSSoundBtn.setEnabled(false);
                        printAllSSoundBtnpressed = getResources().getDrawable(R.drawable.print);
                        printAllSSoundBtnpressed.setBounds(0, 0, printAllSSoundBtnpressed.getMinimumWidth(), printAllSSoundBtnpressed.getMinimumHeight());
                        printAllSSoundBtn.setCompoundDrawables(null, printAllSSoundBtnpressed, null, null);
                        if (printDataService == null) {           //首次连接打印机
                            SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                            if (!shares.getBoolean("BondPrinter", false)) {
                                Toast.makeText(SSoundSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SSoundSaveActivity.this.getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                        } else {          //打印数据
                            printDataService.putDevice(printDevice);
                            if(cursorSSound.moveToFirst()){
                                str_date = cursorSSound.getString(1);
                                str_company = cursorSSound.getString(2);
                                str_number = cursorSSound.getString(3);

                                str_sightNumber= cursorSSound.getString(4);
                                str_sightLenght= cursorSSound.getString(5);
                                str_sightType= cursorSSound.getString(6);
                                str_sightLoad= cursorSSound.getString(7);

                                SSoundMax = cursorSSound.getFloat(8);
                                str_SSoundMax = myformat.format(SSoundMax );
                                PrintMeasureData();
                                while(cursorSSound.moveToNext()){//遍历数据表中的数据
                                    str_date = cursorSSound.getString(1);
                                    str_company = cursorSSound.getString(2);
                                    str_number = cursorSSound.getString(3);

                                    str_sightNumber= cursorSSound.getString(4);
                                    str_sightLenght= cursorSSound.getString(5);
                                    str_sightType= cursorSSound.getString(6);
                                    str_sightLoad= cursorSSound.getString(7);

                                    SSoundMax = cursorSSound.getFloat(8);
                                    str_SSoundMax = myformat.format(SSoundMax );
                                    PrintMeasureData();
                                }
                            }
                        }
                    }
                    break;
                    case R.id.delCurSSoundBtn: {
                        delCurSSoundBtn.setEnabled(false);
                        delCurSSoundBtnpressed = getResources().getDrawable(R.drawable.delete);
                        delCurSSoundBtnpressed.setBounds(0, 0, delCurSSoundBtnpressed.getMinimumWidth(), delCurSSoundBtnpressed.getMinimumHeight());
                        delCurSSoundBtn.setCompoundDrawables(null, delCurSSoundBtnpressed, null, null);
                        SSoundDelete();
                    }
                    break;
                    case R.id.delAllSSoundBtn: {
                        delAllSSoundBtn.setEnabled(false);
                        delAllSSoundBtnpressed = getResources().getDrawable(R.drawable.delete);
                        delAllSSoundBtnpressed.setBounds(0, 0, delAllSSoundBtnpressed.getMinimumWidth(), delAllSSoundBtnpressed.getMinimumHeight());
                        delAllSSoundBtn.setCompoundDrawables(null,delAllSSoundBtnpressed, null, null);
                        while(cursorSSound.moveToFirst()){
                            data_ID = cursorSSound.getInt(0);
                            SSoundDelete();
                        }
                    }
                    break;
                    case R.id.exportCurSSoundBtn: {
                        exportCurSSoundBtn.setEnabled(false);
                        exportCurSSoundBtnpressed = getResources().getDrawable(R.drawable.export);
                        exportCurSSoundBtnpressed.setBounds(0, 0, exportCurSSoundBtnpressed.getMinimumWidth(), exportCurSSoundBtnpressed.getMinimumHeight());
                        exportCurSSoundBtn.setCompoundDrawables(null,exportCurSSoundBtnpressed, null, null);
                        if(cursorSSound.moveToFirst()){
                            ID = cursorSSound.getInt(0);
                            if (ID == data_ID) {
                                str_date = cursorSSound.getString(1);
                                str_company = cursorSSound.getString(2);
                                str_number = cursorSSound.getString(3);

                                str_sightNumber= cursorSSound.getString(4);
                                str_sightLenght= cursorSSound.getString(5);
                                str_sightType= cursorSSound.getString(6);
                                str_sightLoad= cursorSSound.getString(7);

                                SSoundMax = cursorSSound.getFloat(8);
                                str_SSoundMax = myformat.format(SSoundMax );
                                CreatePdf();
                                Toast.makeText(SSoundSaveActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车噪声检测报告", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            while(cursorSSound.moveToNext()){//遍历数据表中的数据
                                ID = cursorSSound.getInt(0);
                                if (ID == data_ID) {
                                    str_date = cursorSSound.getString(1);
                                    str_company = cursorSSound.getString(2);
                                    str_number = cursorSSound.getString(3);

                                    str_sightNumber = cursorSSound.getString(4);
                                    str_sightLenght = cursorSSound.getString(5);
                                    str_sightType = cursorSSound.getString(6);
                                    str_sightLoad = cursorSSound.getString(7);

                                    SSoundMax = cursorSSound.getFloat(8);
                                    str_SSoundMax = myformat.format(SSoundMax);
                                    CreatePdf();
                                    Toast.makeText(SSoundSaveActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车噪声检测报告", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }
                    }
                    break;
                    case R.id.exportAllSSoundBtn: {
                        exportAllSSoundBtn.setEnabled(false);
                        exportAllSSoundBtnpressed = getResources().getDrawable(R.drawable.export);
                        exportAllSSoundBtnpressed.setBounds(0, 0, exportAllSSoundBtnpressed.getMinimumWidth(), exportAllSSoundBtnpressed.getMinimumHeight());
                        exportAllSSoundBtn.setCompoundDrawables(null, exportAllSSoundBtnpressed, null, null);
                        CreatePdfAll();
                        Toast.makeText(SSoundSaveActivity.this, "数据已导出到手机根目录/Documents/观光车辆/观光列车噪声检测报告", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case R.id.backSSoundBtn: {
                        Intent intent = new Intent(SSoundSaveActivity.this, SSoundActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
                    case R.id.exitSSoundBtn: {
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
            saveSSoundDB = new SSoundDB(this);
            cursorSSound = saveSSoundDB.select();
            savelistView = (ListView)findViewById(R.id.savelistSSound);
            savelistView.setAdapter(new SSoundSaveActivity.saveListAdapter(this, cursorSSound));
            savelistView.setOnItemClickListener(this);
        }
        public void SSoundAdd(String str_forceMax){
            Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
            SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
            String   str_date   =   formatter.format(curDate);
            saveSSoundDB.insert(str_date,str_forceMax);
            cursorSSound.requery();
            savelistView.invalidateViews();
        }
        public void SSoundDelete(){
            if (data_ID == 0) {
                return;
            }
            saveSSoundDB.delete(data_ID);
            cursorSSound.requery();
            savelistView.invalidateViews();
//        Toast.makeText(this, "删除成功!", Toast.LENGTH_SHORT).show();
        }

        public void SSoundUpdate(){
            saveSSoundDB.update(data_ID);
            cursorSSound.requery();
            savelistView.invalidateViews();
            Toast.makeText(this, "Update Successed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            cursorSSound.moveToPosition(position);
            data_ID = cursorSSound.getInt(0);
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
            cursorSSound.moveToPosition(position);

            TextView date_txt = (TextView) convertView.findViewById(R.id.dateSave);
            TextView company_txt = (TextView) convertView.findViewById(R.id.companySave);
            TextView deviceNum_txt = (TextView) convertView.findViewById(R.id.deviceNumSave);

            TextView sightNumber_txt = (TextView) convertView.findViewById(R.id.chacheNumber);
            TextView sightLenght_txt = (TextView) convertView.findViewById(R.id.chacheType);
            TextView sightType_txt = (TextView) convertView.findViewById(R.id.chacheGroup);
            TextView sightLoad_txt = (TextView) convertView.findViewById(R.id.breakSpeed);

            TextView SoundMax_txt = (TextView) convertView.findViewById(R.id.ratedSpeed);

            date_txt.setText("检测时间："+cursorSSound.getString(1));
            company_txt.setText("受检单位："+cursorSSound.getString(2));
            deviceNum_txt.setText("设备编号："+cursorSSound.getString(3));

            sightNumber_txt.setText("车牌编号："+cursorSSound.getString(4));
            sightLenght_txt.setText("车身长度："+cursorSSound.getString(5)+"m");
            sightType_txt.setText("车辆类型："+cursorSSound.getString(6));
            sightLoad_txt.setText("负载类型："+cursorSSound.getString(7));

            SoundMax_txt.setText("最大噪声值："+cursorSSound.getString(8)+"dB");
            return convertView;
        }

        private Runnable sendRunnable = new Runnable() {
            @Override
            public void run() {
                delCurSSoundBtn.setEnabled(true);
                delCurSSoundBtnpressed = getResources().getDrawable(R.drawable.delete1);
                delCurSSoundBtnpressed.setBounds(0, 0, delCurSSoundBtnpressed.getMinimumWidth(), delCurSSoundBtnpressed.getMinimumHeight());
                delCurSSoundBtn.setCompoundDrawables(null, delCurSSoundBtnpressed, null, null);

                delAllSSoundBtn.setEnabled(true);
                delAllSSoundBtnpressed = getResources().getDrawable(R.drawable.delete1);
                delAllSSoundBtnpressed.setBounds(0, 0, delAllSSoundBtnpressed.getMinimumWidth(), delAllSSoundBtnpressed.getMinimumHeight());
                delAllSSoundBtn.setCompoundDrawables(null, delAllSSoundBtnpressed, null, null);

                printCurSSoundBtn.setEnabled(true);
                printCurSSoundBtnpressed = getResources().getDrawable(R.drawable.print1);
                printCurSSoundBtnpressed.setBounds(0, 0, printCurSSoundBtnpressed.getMinimumWidth(), printCurSSoundBtnpressed.getMinimumHeight());
                printCurSSoundBtn.setCompoundDrawables(null, printCurSSoundBtnpressed, null, null);

                printAllSSoundBtn.setEnabled(true);
                printAllSSoundBtnpressed = getResources().getDrawable(R.drawable.print1);
                printAllSSoundBtnpressed.setBounds(0, 0, printAllSSoundBtnpressed.getMinimumWidth(), printAllSSoundBtnpressed.getMinimumHeight());
                printAllSSoundBtn.setCompoundDrawables(null, printAllSSoundBtnpressed, null, null);

                exportCurSSoundBtn.setEnabled(true);
                exportCurSSoundBtnpressed = getResources().getDrawable(R.drawable.export1);
                exportCurSSoundBtnpressed.setBounds(0, 0, exportCurSSoundBtnpressed.getMinimumWidth(), exportCurSSoundBtnpressed.getMinimumHeight());
                exportCurSSoundBtn.setCompoundDrawables(null, exportCurSSoundBtnpressed, null, null);

                exportAllSSoundBtn.setEnabled(true);
                exportAllSSoundBtnpressed = getResources().getDrawable(R.drawable.export1);
                exportAllSSoundBtnpressed.setBounds(0, 0, exportAllSSoundBtnpressed.getMinimumWidth(), exportAllSSoundBtnpressed.getMinimumHeight());
                exportAllSSoundBtn.setCompoundDrawables(null, exportAllSSoundBtnpressed, null, null);
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
                        printDataService = new PrintDataService(SSoundSaveActivity.this,shares.getString("Printer",""));
                        Toast.makeText(SSoundSaveActivity.this,"打印机连接中...",Toast.LENGTH_LONG).show();
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
            str_date = cursorSSound.getString(1);
            str_company = cursorSSound.getString(2);
            str_number = cursorSSound.getString(3);

            str_sightNumber= cursorSSound.getString(4);
            str_sightLenght= cursorSSound.getString(5);
            str_sightType= cursorSSound.getString(6);
            str_sightLoad= cursorSSound.getString(7);

            SSoundMax = cursorSSound.getFloat(8);
            str_SSoundMax = myformat.format(SSoundMax );

            printDataService.send("\n*******************************\n");
            printDataService.send("观光车辆/观光列车噪声检测结果");
            printDataService.send("\n*******************************\n");
            printDataService.send("检测时间"+": "+ str_date+"\n");
            printDataService.send("受检单位"+": "+str_company+"\n");//
            printDataService.send("设备编号"+": "+ str_number+"\n");//
            printDataService.send("车牌编号"+": "+ str_sightNumber+"\n");//
            printDataService.send("车身长"+": "+ str_sightLenght+"m"+"\n");//
            printDataService.send("车辆类型"+": "+ str_sightType+"\n");//
            printDataService.send("负载类型"+": "+ str_sightLoad+"\n");//
            printDataService.send("最大噪声值"+": "+ str_SSoundMax+"dB"+"\n");//
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
            SSoundSaveActivity.this.sendBroadcast(intent);
        }
        //创建PDF文件-----------------------------------------------------------------
        Document doc;
        private void CreatePdf(){
            verifyStoragePermissions(SSoundSaveActivity.this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                    FileOutputStream fos;
                    Paragraph pdfcontext;
                    try {
                        //创建目录
                        File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车噪声检测报告"+ File.separator);
                        if (!destDir.exists()) {
                            destDir.mkdirs();
                            notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车噪声检测报告"+ File.separator );
                        }

                        Uri uri = Uri.fromFile(destDir);
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                        getApplication().getApplicationContext().sendBroadcast(intent);
                        Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
                        fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车噪声检测报告" + File.separator + str_date+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
                        PdfWriter.getInstance(doc, fos);
                        doc.open();
                        doc.setPageCount(1);
                        pdfcontext = new Paragraph("观光车辆/观光列车噪声检测报告",setChineseTitleFont());
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

                        cell.setPhrase(new Phrase("最大噪声值：",setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase(str_SSoundMax,setChineseFont()))    ;mtable.addCell(cell);
                        cell.setPhrase(new Phrase("dB",setChineseFont()))    ;mtable.addCell(cell);

                        doc.add(mtable);
                        doc.close();
                        fos.flush();
                        fos.close();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车噪声检测报告" +  File.separator + str_date +".pdf");
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
        verifyStoragePermissions(SSoundSaveActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //创建目录
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车噪声检测报告"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车噪声检测报告"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车噪声检测报告" + File.separator + curDate.toString ()+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    if(cursorSSound.moveToFirst()){
                        str_date = cursorSSound.getString(1);
                        str_company = cursorSSound.getString(2);
                        str_number = cursorSSound.getString(3);

                        str_sightNumber= cursorSSound.getString(4);
                        str_sightLenght= cursorSSound.getString(5);
                        str_sightType= cursorSSound.getString(6);
                        str_sightLoad= cursorSSound.getString(7);

                        SSoundMax = cursorSSound.getFloat(8);
                        str_SSoundMax = myformat.format(SSoundMax );

                        pdfcontext = new Paragraph("观光车辆/观光列车噪声检测报告",setChineseTitleFont());
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

                        cell2.setPhrase(new Phrase("最大噪声值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_SSoundMax,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("dB",setChineseFont()))    ;mtable2.addCell(cell2);
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
                        while(cursorSSound.moveToNext()){//遍历数据表中的数据
                            str_date = cursorSSound.getString(1);
                            str_company = cursorSSound.getString(2);
                            str_number = cursorSSound.getString(3);

                            str_sightNumber= cursorSSound.getString(4);
                            str_sightLenght= cursorSSound.getString(5);
                            str_sightType= cursorSSound.getString(6);
                            str_sightLoad= cursorSSound.getString(7);

                            SSoundMax = cursorSSound.getFloat(8);
                            str_SSoundMax = myformat.format(SSoundMax );

                            pdfcontext = new Paragraph("观光车辆/观光列车噪声检测报告",setChineseTitleFont());
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

                            cell.setPhrase(new Phrase("最大噪声值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_SSoundMax,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("dB",setChineseFont()))    ;mtable.addCell(cell);
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
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "观光车辆/观光列车噪声检测报告" +  File.separator + curDate.toString () +".pdf");
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
