package com.ruiguan.Sight.STorque;

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

public class STorqueSaveActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private STorqueDB saveSTDB;
    private Cursor cursorST;
    private Button delCurSTBtn;
    private Button delAllSTBtn;
    private Button printCurSTBtn;
    private Button printAllSTBtn;
    private Button exportCurSTBtn;
    private Button exportAllSTBtn;
    private Button backSTBtn;
    private Button exitSTBtn;

    private Drawable delCurSTBtnpressed;
    private Drawable delAllSTBtnpressed;
    private Drawable printCurSTBtnpressed;
    private Drawable printAllSTBtnpressed;
    private Drawable exportCurSTBtnpressed;
    private Drawable exportAllSTBtnpressed;

    private String str_date;
    private String str_company;
    private String str_number;
    private String str_sightNumber;
    private String str_sightLenght;
    private String str_sightType;
    private String str_sightLoad;

    private String str_angleMaxL;
    private String str_angleMaxR;
    private String str_forceMaxL;
    private String str_forceMaxR;
    private String str_forceErrMax;
    private String str_angleErrMax;

    private ListView savelistSTView;
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
        setContentView(R.layout.activity_storque_save);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);

        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_sightNumber= sight_Input.getsightNumber();
        str_sightLenght= sight_Input.getsightLenght();
        str_sightType= sight_Input.getsightType();
        str_sightLoad= sight_Input.getsightLoad();

        printCurSTBtn = (Button) findViewById(R.id.printCurSTBtn);
        printAllSTBtn = (Button) findViewById(R.id.printAllSTBtn);
        delCurSTBtn = (Button) findViewById(R.id.delCurSTBtn);
        delAllSTBtn= (Button) findViewById(R.id.delAllSTBtn);
        exportCurSTBtn= (Button) findViewById(R.id.exportCurSTBtn);
        exportAllSTBtn= (Button) findViewById(R.id.exportAllSTBtn);
        backSTBtn= (Button) findViewById(R.id.backSTBtn);
        exitSTBtn= (Button) findViewById(R.id.exitSTBtn);
        View.OnClickListener bl = new STorqueSaveActivity.ButtonListener();
        setOnClickListener(printCurSTBtn, bl);
        setOnClickListener(printAllSTBtn, bl);
        setOnClickListener(delCurSTBtn, bl);
        setOnClickListener(delAllSTBtn, bl);
        setOnClickListener(exportCurSTBtn, bl);
        setOnClickListener(exportAllSTBtn, bl);
        setOnClickListener(backSTBtn, bl);
        setOnClickListener(exitSTBtn, bl);
        setUpViews();
    }
    @Override
    protected void onResume() {
        super.onResume();
        final String METHODTAG = ".onResume";
    }
    //???????????????
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        saveSTDB.close();
        cursorST.close();
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
                case R.id.printCurSTBtn: {
                    printCurSTBtn.setEnabled(false);
                    printCurSTBtnpressed = getResources().getDrawable(R.drawable.print);
                    printCurSTBtnpressed.setBounds(0, 0, printCurSTBtnpressed.getMinimumWidth(), printCurSTBtnpressed.getMinimumHeight());
                    printCurSTBtn.setCompoundDrawables(null, printCurSTBtnpressed, null, null);

                    if (printDevice == null) {           //?????????????????????
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(STorqueSaveActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(STorqueSaveActivity.this.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //??????????????????????????????
                    } else {          //????????????
                        printDataService = new PrintDataService(STorqueSaveActivity.this,deviceAddress);
                        printDataService.putDevice(printDevice);
                        if(cursorST.moveToFirst()){
                            while(cursorST.moveToNext()){//???????????????????????????
                                ID = cursorST.getInt(0);
                                if (ID == data_ID) {
                                    str_date = cursorST.getString(1);
                                    str_company = cursorST.getString(2);
                                    str_number = cursorST.getString(3);

                                    str_sightNumber= cursorST.getString(4);
                                    str_sightLenght= cursorST.getString(5);
                                    str_sightType= cursorST.getString(6);
                                    str_sightLoad= cursorST.getString(7);

                                    str_angleMaxL= cursorST.getString(8);
                                    str_angleMaxR= cursorST.getString(9);
                                    str_forceMaxL= cursorST.getString(10);
                                    str_forceMaxR= cursorST.getString(11);
                                    str_angleErrMax= cursorST.getString(12);
                                    str_forceErrMax= cursorST.getString(13);

                                    PrintMeasureData();
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
                case R.id.printAllSTBtn: {
                    printAllSTBtn.setEnabled(false);
                    printAllSTBtnpressed = getResources().getDrawable(R.drawable.print);
                    printAllSTBtnpressed.setBounds(0, 0, printAllSTBtnpressed.getMinimumWidth(), printAllSTBtnpressed.getMinimumHeight());
                    printAllSTBtn.setCompoundDrawables(null, printAllSTBtnpressed, null, null);
                    if (printDataService == null) {           //?????????????????????
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(STorqueSaveActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(STorqueSaveActivity.this.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //??????????????????????????????
                    } else {          //????????????
                        printDataService.putDevice(printDevice);
                        if(cursorST.moveToFirst()){
                            str_date = cursorST.getString(1);
                            str_company = cursorST.getString(2);
                            str_number = cursorST.getString(3);

                            str_sightNumber= cursorST.getString(4);
                            str_sightLenght= cursorST.getString(5);
                            str_sightType= cursorST.getString(6);
                            str_sightLoad= cursorST.getString(7);

                            str_angleMaxL= cursorST.getString(8);
                            str_angleMaxR= cursorST.getString(9);
                            str_forceMaxL= cursorST.getString(10);
                            str_forceMaxR= cursorST.getString(11);
                            str_angleErrMax= cursorST.getString(12);
                            str_forceErrMax= cursorST.getString(13);
                            PrintMeasureData();
                            while(cursorST.moveToNext()){//???????????????????????????
                                str_date = cursorST.getString(1);
                                str_company = cursorST.getString(2);
                                str_number = cursorST.getString(3);

                                str_sightNumber= cursorST.getString(4);
                                str_sightLenght= cursorST.getString(5);
                                str_sightType= cursorST.getString(6);
                                str_sightLoad= cursorST.getString(7);

                                str_angleMaxL= cursorST.getString(8);
                                str_angleMaxR= cursorST.getString(9);
                                str_forceMaxL= cursorST.getString(10);
                                str_forceMaxR= cursorST.getString(11);
                                str_angleErrMax= cursorST.getString(12);
                                str_forceErrMax= cursorST.getString(13);
                                PrintMeasureData();
                            }
                        }
                    }
                }
                break;
                case R.id.delCurSTBtn: {
                    delCurSTBtn.setEnabled(false);
                    delCurSTBtnpressed = getResources().getDrawable(R.drawable.delete);
                    delCurSTBtnpressed.setBounds(0, 0, delCurSTBtnpressed.getMinimumWidth(), delCurSTBtnpressed.getMinimumHeight());
                    delCurSTBtn.setCompoundDrawables(null, delCurSTBtnpressed, null, null);
                    storqueDelete();
                }
                break;
                case R.id.delAllSTBtn: {
                    delAllSTBtn.setEnabled(false);
                    delAllSTBtnpressed = getResources().getDrawable(R.drawable.delete);
                    delAllSTBtnpressed.setBounds(0, 0, delAllSTBtnpressed.getMinimumWidth(), delAllSTBtnpressed.getMinimumHeight());
                    delAllSTBtn.setCompoundDrawables(null,delAllSTBtnpressed, null, null);
                    while(cursorST.moveToFirst()){
                        data_ID = cursorST.getInt(0);
                        storqueDelete();
                    }
                }
                break;
                case R.id.exportCurSTBtn: {
                    exportCurSTBtn.setEnabled(false);
                    exportCurSTBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportCurSTBtnpressed.setBounds(0, 0, exportCurSTBtnpressed.getMinimumWidth(), exportCurSTBtnpressed.getMinimumHeight());
                    exportCurSTBtn.setCompoundDrawables(null,exportCurSTBtnpressed, null, null);
                    if(cursorST.moveToFirst()){
                        ID = cursorST.getInt(0);
                        if (ID == data_ID) {
                            str_date = cursorST.getString(1);
                            str_company = cursorST.getString(2);
                            str_number = cursorST.getString(3);

                            str_sightNumber= cursorST.getString(4);
                            str_sightLenght= cursorST.getString(5);
                            str_sightType= cursorST.getString(6);
                            str_sightLoad= cursorST.getString(7);

                            str_angleMaxL= cursorST.getString(8);
                            str_angleMaxR= cursorST.getString(9);
                            str_forceMaxL= cursorST.getString(10);
                            str_forceMaxR= cursorST.getString(11);
                            str_angleErrMax= cursorST.getString(12);
                            str_forceErrMax= cursorST.getString(13);
                            CreatePdf();
                            Toast.makeText(STorqueSaveActivity.this, "?????????????????????????????????/Documents/????????????/?????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        while(cursorST.moveToNext()){//???????????????????????????
                            ID = cursorST.getInt(0);
                            if (ID == data_ID) {
                                str_date = cursorST.getString(1);
                                str_company = cursorST.getString(2);
                                str_number = cursorST.getString(3);

                                str_sightNumber= cursorST.getString(4);
                                str_sightLenght= cursorST.getString(5);
                                str_sightType= cursorST.getString(6);
                                str_sightLoad= cursorST.getString(7);

                                str_angleMaxL= cursorST.getString(8);
                                str_angleMaxR= cursorST.getString(9);
                                str_forceMaxL= cursorST.getString(10);
                                str_forceMaxR= cursorST.getString(11);
                                str_angleErrMax= cursorST.getString(12);
                                str_forceErrMax= cursorST.getString(13);
                                CreatePdf();
                                Toast.makeText(STorqueSaveActivity.this, "?????????????????????????????????/Documents/????????????/?????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }
                break;
                case R.id.exportAllSTBtn: {
                    exportAllSTBtn.setEnabled(false);
                    exportAllSTBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportAllSTBtnpressed.setBounds(0, 0, exportAllSTBtnpressed.getMinimumWidth(), exportAllSTBtnpressed.getMinimumHeight());
                    exportAllSTBtn.setCompoundDrawables(null,exportAllSTBtnpressed, null, null);
                    CreatePdfAll();
                    Toast.makeText(STorqueSaveActivity.this, "?????????????????????????????????/Documents/????????????/?????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.backSTBtn: {
                    Intent intent = new Intent(STorqueSaveActivity.this, STorqueActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.exitSTBtn: {
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
        saveSTDB = new STorqueDB(this);
        cursorST = saveSTDB.select();
        savelistSTView = (ListView)findViewById(R.id.savelistST);
        savelistSTView.setAdapter(new STorqueSaveActivity.saveListAdapter(this, cursorST));
        savelistSTView.setOnItemClickListener(this);
    }
    public void storqueAdd(String str_angleMaxL,String str_angleMaxR,String str_forceMaxL,String str_forceMaxR,String str_angleMax,String str_forceMax){
        Date curDate =  new Date(System.currentTimeMillis());//??????????????????
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
        String   str_date   =   formatter.format(curDate);
        saveSTDB.insert(str_date,str_angleMaxL,str_angleMaxR,str_forceMaxL,str_forceMaxR,str_angleMax,str_forceMax);
        cursorST.requery();
        savelistSTView.invalidateViews();
    }
    public void storqueDelete(){
        if (data_ID == 0) {
            return;
        }
        saveSTDB.delete(data_ID);
        cursorST.requery();
        savelistSTView.invalidateViews();
        Toast.makeText(this, "????????????!", Toast.LENGTH_SHORT).show();
    }
    public void storqueUpdate(){
        saveSTDB.update(data_ID);
        cursorST.requery();
        savelistSTView.invalidateViews();
        Toast.makeText(this, "Update Successed!", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cursorST.moveToPosition(position);
        data_ID = cursorST.getInt(0);
        Toast.makeText(this, "?????????!", Toast.LENGTH_SHORT).show();
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
            convertView =getLayoutInflater().inflate(R.layout.save_list, null);//????????????
        }
        cursorST.moveToPosition(position);
        TextView date_txt = (TextView) convertView.findViewById(R.id.dateSave);
        TextView company_txt = (TextView) convertView.findViewById(R.id.companySave);
        TextView deviceNum_txt = (TextView) convertView.findViewById(R.id.deviceNumSave);

        TextView sightNumber_txt = (TextView) convertView.findViewById(R.id.chacheNumber);
        TextView sightLenght_txt = (TextView) convertView.findViewById(R.id.chacheType);
        TextView sightType_txt = (TextView) convertView.findViewById(R.id.chacheGroup);
        TextView sightLoad_txt = (TextView) convertView.findViewById(R.id.breakSpeed);

        TextView angleMaxL_txt = (TextView) convertView.findViewById(R.id.ratedSpeed);
        TextView angleMaxR_txt = (TextView) convertView.findViewById(R.id.breakDis);

        TextView forceMaxL_txt = (TextView) convertView.findViewById(R.id.breakTime);
        TextView forceMaxR_txt = (TextView) convertView.findViewById(R.id.breakForce);

        TextView breakSpeed_txt = (TextView) convertView.findViewById(R.id.breaklength);
        TextView ASpeedMax_txt = (TextView) convertView.findViewById(R.id.angleErr);

        date_txt.setText("???????????????"+cursorST.getString(1));
        company_txt.setText("???????????????"+cursorST.getString(2));
        deviceNum_txt.setText("???????????????"+cursorST.getString(3));

        sightNumber_txt.setText("???????????????"+cursorST.getString(4));
        sightLenght_txt.setText("???????????????"+cursorST.getString(5)+"m");
        sightType_txt.setText("???????????????"+cursorST.getString(6));
        sightLoad_txt.setText("???????????????"+cursorST.getString(7));

        angleMaxL_txt.setText("?????????????????????"+cursorST.getString(8)+"??");
        angleMaxR_txt.setText("?????????????????????"+cursorST.getString(9)+"??");

        forceMaxL_txt.setText("?????????????????????"+cursorST.getString(10)+"N");
        forceMaxR_txt.setText("?????????????????????"+cursorST.getString(11)+"N");

        breakSpeed_txt.setText("?????????????????????"+cursorST.getString(12)+"??");
        ASpeedMax_txt.setText("?????????????????????"+cursorST.getString(13)+"N");

        return convertView;
    }

    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            delCurSTBtn.setEnabled(true);
            delCurSTBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delCurSTBtnpressed.setBounds(0, 0, delCurSTBtnpressed.getMinimumWidth(), delCurSTBtnpressed.getMinimumHeight());
            delCurSTBtn.setCompoundDrawables(null, delCurSTBtnpressed, null, null);

            delAllSTBtn.setEnabled(true);
            delAllSTBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delAllSTBtnpressed.setBounds(0, 0, delAllSTBtnpressed.getMinimumWidth(), delAllSTBtnpressed.getMinimumHeight());
            delAllSTBtn.setCompoundDrawables(null, delAllSTBtnpressed, null, null);

            printCurSTBtn.setEnabled(true);
            printCurSTBtnpressed = getResources().getDrawable(R.drawable.print1);
            printCurSTBtnpressed.setBounds(0, 0, printCurSTBtnpressed.getMinimumWidth(), printCurSTBtnpressed.getMinimumHeight());
            printCurSTBtn.setCompoundDrawables(null, printCurSTBtnpressed, null, null);

            printAllSTBtn.setEnabled(true);
            printAllSTBtnpressed = getResources().getDrawable(R.drawable.print1);
            printAllSTBtnpressed.setBounds(0, 0, printAllSTBtnpressed.getMinimumWidth(), printAllSTBtnpressed.getMinimumHeight());
            printAllSTBtn.setCompoundDrawables(null, printAllSTBtnpressed, null, null);

            exportCurSTBtn.setEnabled(true);
            exportCurSTBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportCurSTBtnpressed.setBounds(0, 0, exportCurSTBtnpressed.getMinimumWidth(), exportCurSTBtnpressed.getMinimumHeight());
            exportCurSTBtn.setCompoundDrawables(null, exportCurSTBtnpressed, null, null);

            exportAllSTBtn.setEnabled(true);
            exportAllSTBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportAllSTBtnpressed.setBounds(0, 0, exportAllSTBtnpressed.getMinimumWidth(), exportAllSTBtnpressed.getMinimumHeight());
            exportAllSTBtn.setCompoundDrawables(null, exportAllSTBtnpressed, null, null);
        }
    };
    Runnable PrinterRunnable = new Runnable() {
        @Override
        public void run() {
            if(PrintConnect){         //???????????????????????????????????????????????????
                handler.removeCallbacks(PrinterRunnable);
                PrintConnect = false;
            }
            else{
                SharedPreferences shares = getSharedPreferences( "BLE_Info", Activity.MODE_PRIVATE );
                if(shares.getBoolean("BondPrinter",false)){
                    printDataService = new PrintDataService(STorqueSaveActivity.this,shares.getString("Printer",""));
                    Toast.makeText(STorqueSaveActivity.this,"??????????????????...",Toast.LENGTH_LONG).show();
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
    //??????????????????
    private void PrintMeasureData(){
        str_date = cursorST.getString(1);
        str_company = cursorST.getString(2);
        str_number = cursorST.getString(3);

        str_sightNumber= cursorST.getString(4);
        str_sightLenght= cursorST.getString(5);
        str_sightType= cursorST.getString(6);
        str_sightLoad= cursorST.getString(7);

        str_angleMaxL= cursorST.getString(8);
        str_angleMaxR= cursorST.getString(9);
        str_forceMaxL= cursorST.getString(10);
        str_forceMaxR= cursorST.getString(11);
        str_angleErrMax= cursorST.getString(12);
        str_forceErrMax= cursorST.getString(13);

        printDataService.send("\n*******************************\n");
        printDataService.send("????????????/?????????????????????????????????????????????????????????");
        printDataService.send("\n*******************************\n");
        printDataService.send("????????????"+": "+ str_date+"\n");
        printDataService.send("????????????"+": "+str_company+"\n");//
        printDataService.send("????????????"+": "+ str_number+"\n");//
        printDataService.send("????????????"+": "+ str_sightNumber+"\n");//
        printDataService.send("?????????"+": "+ str_sightLenght+"m"+"\n");//
        printDataService.send("????????????"+": "+ str_sightType+"\n");//
        printDataService.send("????????????"+": "+ str_sightLoad+"\n");//

        printDataService.send("???????????????"+": "+ str_angleMaxL+"??"+"\n");//
        printDataService.send("???????????????"+": "+ str_angleMaxR+"??"+"\n");//
        printDataService.send("???????????????"+": "+ str_forceMaxL+"N"+"\n");//
        printDataService.send("???????????????"+": "+ str_forceMaxR+"N"+"\n");//
        printDataService.send("??????????????????"+": "+ str_angleErrMax+"??"+"\n");//
        printDataService.send("??????????????????"+": "+ str_forceErrMax+"N"+"\n");//
        printDataService.send("*******************************\n\n\n\n");
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
        STorqueSaveActivity.this.sendBroadcast(intent);
    }
    //??????PDF??????-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(STorqueSaveActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// ????????????document??????
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //????????????
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????????????????????????????"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????????????????????????????"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//??????????????????
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????????????????????????????" + File.separator + curDate.toString ()+".pdf"); // pdf_address???Pdf???????????????sd????????????
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    pdfcontext = new Paragraph("????????????/?????????????????????????????????????????????????????????",setChineseTitleFont());
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
                    cell.setPhrase(new Phrase(str_date,setChineseFont()))    ;mtable.addCell(cell);
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

                    cell.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleMaxL,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("??",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleMaxR,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("??",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_forceMaxL,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_forceMaxR,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_angleErrMax,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("??",setChineseFont()))    ;mtable.addCell(cell);

                    cell.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_forceErrMax,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);

                    doc.add(mtable);
                    doc.close();
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????????????????????????????" +  File.separator + curDate.toString () +".pdf");
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
        verifyStoragePermissions(STorqueSaveActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// ????????????document??????
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //????????????
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????????????????????????????"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????????????????????????????"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//??????????????????
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????????????????????????????" + File.separator + curDate.toString ()+".pdf"); // pdf_address???Pdf???????????????sd????????????
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    if(cursorST.moveToFirst()){
                        str_date = cursorST.getString(1);
                        str_company = cursorST.getString(2);
                        str_number = cursorST.getString(3);

                        str_sightNumber= cursorST.getString(4);
                        str_sightLenght= cursorST.getString(5);
                        str_sightType= cursorST.getString(6);
                        str_sightLoad= cursorST.getString(7);

                        str_angleMaxL= cursorST.getString(8);
                        str_angleMaxR= cursorST.getString(9);
                        str_forceMaxL= cursorST.getString(10);
                        str_forceMaxR= cursorST.getString(11);
                        str_angleErrMax= cursorST.getString(12);
                        str_forceErrMax= cursorST.getString(13);

                        pdfcontext = new Paragraph("????????????/?????????????????????????????????????????????????????????",setChineseTitleFont());
                        pdfcontext.setAlignment(Element.ALIGN_CENTER);
                        doc.add(pdfcontext);
                        pdfcontext = new Paragraph("\n\r");
                        pdfcontext.setLeading(3);
                        doc.add(pdfcontext);
                        //???????????????3????????????
                        //???????????????3????????????
                        PdfPTable table1 = new PdfPTable(3);
                        table1.setWidthPercentage(99);
                        //????????????????????????
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
                        cell2.setPhrase(new Phrase("???????????????",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_date,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("???????????????",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_company,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("???????????????",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_number,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("???????????????",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_sightNumber,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("????????????",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_sightLenght,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(" m",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("???????????????",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_sightType,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("???????????????",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_sightLoad,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("      ",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleMaxL,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("??",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleMaxR,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("??",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_forceMaxL,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("N",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_forceMaxR,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("N",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_angleErrMax,setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase("??",setChineseFont()))    ;mtable2.addCell(cell2);

                        cell2.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable2.addCell(cell2);
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
                        while(cursorST.moveToNext()){//???????????????????????????
                            str_date = cursorST.getString(1);
                            str_company = cursorST.getString(2);
                            str_number = cursorST.getString(3);

                            str_sightNumber= cursorST.getString(4);
                            str_sightLenght= cursorST.getString(5);
                            str_sightType= cursorST.getString(6);
                            str_sightLoad= cursorST.getString(7);

                            str_angleMaxL= cursorST.getString(8);
                            str_angleMaxR= cursorST.getString(9);
                            str_forceMaxL= cursorST.getString(10);
                            str_forceMaxR= cursorST.getString(11);
                            str_angleErrMax= cursorST.getString(12);
                            str_forceErrMax= cursorST.getString(13);

                            pdfcontext = new Paragraph("????????????/?????????????????????????????????????????????????????????",setChineseTitleFont());
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
                            cell.setMinimumHeight(50);
                            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                            PdfPTable mtable = new PdfPTable(3);
                            mtable.setSplitLate(false);
                            mtable.setSplitRows(true);
                            mtable.setWidthPercentage(99);
                            mtable.setWidths(new float[]{300,200,200});
                            cell.setColspan(1);
                            cell.setBackgroundColor(new BaseColor(255,255,255));
                            cell.setPhrase(new Phrase("???????????????",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_date,setChineseFont()))    ;mtable.addCell(cell);
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

                            cell.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_angleMaxL,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("??",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_angleMaxR,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("??",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_forceMaxL,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_forceMaxR,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_angleErrMax,setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase("??",setChineseFont()))    ;mtable.addCell(cell);

                            cell.setPhrase(new Phrase("?????????????????????",setChineseFont()))    ;mtable.addCell(cell);
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
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????????????????????????????" +  File.separator + curDate.toString () +".pdf");
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
