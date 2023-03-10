package com.ruiguan.Sight.SForce;

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

public class SForceSaveActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private SForceDB saveSFDB;
    private Cursor cursorSF;

    private Button delCurSFBtn;
    private Button delAllSFBtn;
    private Button printCurSFBtn;
    private Button printAllSFBtn;
    private Button exportCurSFBtn;
    private Button exportAllSFBtn;
    private Button backSFBtn;
    private Button exitSFBtn;

    private Drawable delCurSFBtnpressed;
    private Drawable delAllSFBtnpressed;
    private Drawable printCurSFBtnpressed;
    private Drawable printAllSFBtnpressed;
    private Drawable exportCurSFBtnpressed;
    private Drawable exportAllSFBtnpressed;

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
        setContentView(R.layout.activity_sforce_save);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);

        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_sightNumber= sight_Input.getsightNumber();
        str_sightLenght= sight_Input.getsightLenght();
        str_sightType= sight_Input.getsightType();
        str_sightLoad= sight_Input.getsightLoad();

        printCurSFBtn = (Button) findViewById(R.id.printCurSFBtn);
        printAllSFBtn = (Button) findViewById(R.id.printAllSFBtn);
        delCurSFBtn = (Button) findViewById(R.id.delCurSFBtn);
        delAllSFBtn= (Button) findViewById(R.id.delAllSFBtn);
        exportCurSFBtn= (Button) findViewById(R.id.exportCurSFBtn);
        exportAllSFBtn= (Button) findViewById(R.id.exportAllSFBtn);
        backSFBtn= (Button) findViewById(R.id.backSFBtn);
        exitSFBtn= (Button) findViewById(R.id.exitSFBtn);
        View.OnClickListener bl = new SForceSaveActivity.ButtonListener();
        setOnClickListener(printCurSFBtn, bl);
        setOnClickListener(printAllSFBtn, bl);
        setOnClickListener(delCurSFBtn, bl);
        setOnClickListener(delAllSFBtn, bl);
        setOnClickListener(exportCurSFBtn, bl);
        setOnClickListener(exportAllSFBtn, bl);
        setOnClickListener(backSFBtn, bl);
        setOnClickListener(exitSFBtn, bl);
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
        saveSFDB.close();
        cursorSF.close();
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
                case R.id.printCurSFBtn: {
                    printCurSFBtn.setEnabled(false);
                    printCurSFBtnpressed = getResources().getDrawable(R.drawable.print);
                    printCurSFBtnpressed.setBounds(0, 0, printCurSFBtnpressed.getMinimumWidth(), printCurSFBtnpressed.getMinimumHeight());
                    printCurSFBtn.setCompoundDrawables(null, printCurSFBtnpressed, null, null);

                    if (printDevice == null) {           //?????????????????????
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(SForceSaveActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SForceSaveActivity.this.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //??????????????????????????????
                    } else {          //????????????
                        printDataService = new PrintDataService(SForceSaveActivity.this,deviceAddress);
                        printDataService.putDevice(printDevice);
                        if(cursorSF.moveToFirst()){
                            ID = cursorSF.getInt(0);
                            if (ID == data_ID) {
                                str_date = cursorSF.getString(1);
                                str_company = cursorSF.getString(2);
                                str_number = cursorSF.getString(3);

                                str_sightNumber= cursorSF.getString(4);
                                str_sightLenght= cursorSF.getString(5);
                                str_sightType= cursorSF.getString(6);
                                str_sightLoad= cursorSF.getString(7);

                                forceMax = cursorSF.getFloat(8);
                                str_forceMax = myformat.format(forceMax );
                                PrintMeasureData();
                                break;
                            }
                            while(cursorSF.moveToNext()){//???????????????????????????
                                ID = cursorSF.getInt(0);
                                if (ID == data_ID) {
                                    str_date = cursorSF.getString(1);
                                    str_company = cursorSF.getString(2);
                                    str_number = cursorSF.getString(3);

                                    str_sightNumber= cursorSF.getString(4);
                                    str_sightLenght= cursorSF.getString(5);
                                    str_sightType= cursorSF.getString(6);
                                    str_sightLoad= cursorSF.getString(7);

                                    forceMax = cursorSF.getFloat(8);
                                    str_forceMax = myformat.format(forceMax );
                                    PrintMeasureData();
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
                case R.id.printAllSFBtn: {
                    printAllSFBtn.setEnabled(false);
                    printAllSFBtnpressed = getResources().getDrawable(R.drawable.print);
                    printAllSFBtnpressed.setBounds(0, 0, printAllSFBtnpressed.getMinimumWidth(), printAllSFBtnpressed.getMinimumHeight());
                    printAllSFBtn.setCompoundDrawables(null, printAllSFBtnpressed, null, null);
                    if (printDataService == null) {           //?????????????????????
                        SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                        if (!shares.getBoolean("BondPrinter", false)) {
                            Toast.makeText(SForceSaveActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SForceSaveActivity.this.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        handler.postDelayed(PrinterRunnable, 100);          //??????????????????????????????
                    } else {          //????????????
                        printDataService.putDevice(printDevice);
                        if(cursorSF.moveToFirst()){
                            str_date = cursorSF.getString(1);
                            str_company = cursorSF.getString(2);
                            str_number = cursorSF.getString(3);

                            str_sightNumber= cursorSF.getString(4);
                            str_sightLenght= cursorSF.getString(5);
                            str_sightType= cursorSF.getString(6);
                            str_sightLoad= cursorSF.getString(7);

                            forceMax = cursorSF.getFloat(8);
                            str_forceMax = myformat.format(forceMax );
                            PrintMeasureData();
                            while(cursorSF.moveToNext()){//???????????????????????????
                                str_date = cursorSF.getString(1);
                                str_company = cursorSF.getString(2);
                                str_number = cursorSF.getString(3);

                                str_sightNumber= cursorSF.getString(4);
                                str_sightLenght= cursorSF.getString(5);
                                str_sightType= cursorSF.getString(6);
                                str_sightLoad= cursorSF.getString(7);

                                forceMax = cursorSF.getFloat(8);
                                str_forceMax = myformat.format(forceMax );
                                PrintMeasureData();
                            }
                        }
                    }
                }
                break;
                case R.id.delCurSFBtn: {
                    delCurSFBtn.setEnabled(false);
                    delCurSFBtnpressed = getResources().getDrawable(R.drawable.delete);
                    delCurSFBtnpressed.setBounds(0, 0, delCurSFBtnpressed.getMinimumWidth(), delCurSFBtnpressed.getMinimumHeight());
                    delCurSFBtn.setCompoundDrawables(null, delCurSFBtnpressed, null, null);
                    forceDeleteS();
                }
                break;
                case R.id.delAllSFBtn: {
                    delAllSFBtn.setEnabled(false);
                    delAllSFBtnpressed = getResources().getDrawable(R.drawable.delete);
                    delAllSFBtnpressed.setBounds(0, 0, delAllSFBtnpressed.getMinimumWidth(), delAllSFBtnpressed.getMinimumHeight());
                    delAllSFBtn.setCompoundDrawables(null,delAllSFBtnpressed, null, null);
                    while(cursorSF.moveToFirst()){
                        data_ID = cursorSF.getInt(0);
                        forceDeleteS();
                    }
                }
                break;
                case R.id.exportAllSFBtn: {
                    exportAllSFBtn.setEnabled(false);
                    exportAllSFBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportAllSFBtnpressed.setBounds(0, 0, exportAllSFBtnpressed.getMinimumWidth(), exportAllSFBtnpressed.getMinimumHeight());
                    exportAllSFBtn.setCompoundDrawables(null,exportAllSFBtnpressed, null, null);
                    CreatePdfAll();
                    Toast.makeText(SForceSaveActivity.this, "?????????????????????????????????/Documents/????????????/?????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.exportCurSFBtn: {
                    exportCurSFBtn.setEnabled(false);
                    exportCurSFBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportCurSFBtnpressed.setBounds(0, 0, exportCurSFBtnpressed.getMinimumWidth(), exportCurSFBtnpressed.getMinimumHeight());
                    exportCurSFBtn.setCompoundDrawables(null,exportCurSFBtnpressed, null, null);
                    if(cursorSF.moveToFirst()){
                        ID = cursorSF.getInt(0);
                        if (ID == data_ID) {
                            str_date = cursorSF.getString(1);
                            str_company = cursorSF.getString(2);
                            str_number = cursorSF.getString(3);

                            str_sightNumber= cursorSF.getString(4);
                            str_sightLenght= cursorSF.getString(5);
                            str_sightType= cursorSF.getString(6);
                            str_sightLoad= cursorSF.getString(7);

                            forceMax = cursorSF.getFloat(8);
                            str_forceMax = myformat.format(forceMax);
                            CreatePdf();
                            Toast.makeText(SForceSaveActivity.this, "?????????????????????????????????/Documents/????????????/?????????????????????????????????", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        while(cursorSF.moveToNext()){//???????????????????????????
                            ID = cursorSF.getInt(0);
                            if (ID == data_ID) {
                                str_date = cursorSF.getString(1);
                                str_company = cursorSF.getString(2);
                                str_number = cursorSF.getString(3);

                                str_sightNumber= cursorSF.getString(4);
                                str_sightLenght= cursorSF.getString(5);
                                str_sightType= cursorSF.getString(6);
                                str_sightLoad= cursorSF.getString(7);

                                forceMax = cursorSF.getFloat(8);
                                str_forceMax = myformat.format(forceMax );
                                CreatePdf();
                                Toast.makeText(SForceSaveActivity.this, "?????????????????????????????????/Documents/????????????/?????????????????????????????????", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }
                break;
                case R.id.backSFBtn: {
                    Intent intent = new Intent(SForceSaveActivity.this, SForceActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.exitSFBtn: {
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
        saveSFDB = new SForceDB(this);
        cursorSF = saveSFDB.select();
        savelistView = (ListView)findViewById(R.id.savelistSF);
        savelistView.setAdapter(new SForceSaveActivity.saveListAdapter(this, cursorSF));
        savelistView.setOnItemClickListener(this);
    }
    public void forceAddS(String str_forceMax){
        Date curDate =  new Date(System.currentTimeMillis());//??????????????????
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
        String   str_date   =   formatter.format(curDate);
        saveSFDB.insert(str_date,str_forceMax);
        cursorSF.requery();
        savelistView.invalidateViews();
    }
    public void forceDeleteS(){
        if (data_ID == 0) {
            return;
        }
        saveSFDB.delete(data_ID);
        cursorSF.requery();
        savelistView.invalidateViews();
//        Toast.makeText(this, "????????????!", Toast.LENGTH_SHORT).show();
    }

    public void forceUpdate(){
        saveSFDB.update(data_ID);
        cursorSF.requery();
        savelistView.invalidateViews();
        Toast.makeText(this, "Update Successed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cursorSF.moveToPosition(position);
        data_ID = cursorSF.getInt(0);
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
        cursorSF.moveToPosition(position);

        TextView date_txt = (TextView) convertView.findViewById(R.id.dateSave);
        TextView company_txt = (TextView) convertView.findViewById(R.id.companySave);
        TextView deviceNum_txt = (TextView) convertView.findViewById(R.id.deviceNumSave);

        TextView sightNumber_txt = (TextView) convertView.findViewById(R.id.chacheNumber);
        TextView sightLenght_txt = (TextView) convertView.findViewById(R.id.chacheType);
        TextView sightType_txt = (TextView) convertView.findViewById(R.id.chacheGroup);
        TextView sightLoad_txt = (TextView) convertView.findViewById(R.id.breakSpeed);
        TextView forceMax_txt = (TextView) convertView.findViewById(R.id.ratedSpeed);

        date_txt.setText("???????????????"+cursorSF.getString(1));
        company_txt.setText("???????????????"+cursorSF.getString(2));
        deviceNum_txt.setText("???????????????"+cursorSF.getString(3));
        sightNumber_txt.setText("???????????????"+cursorSF.getString(4));
        sightLenght_txt.setText("???????????????"+cursorSF.getString(5)+"m");
        sightType_txt.setText("???????????????"+cursorSF.getString(6));
        sightLoad_txt.setText("???????????????"+cursorSF.getString(7));
        forceMax_txt.setText("??????????????????"+cursorSF.getString(8)+"N");
        return convertView;
    }

    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            delCurSFBtn.setEnabled(true);
            delCurSFBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delCurSFBtnpressed.setBounds(0, 0, delCurSFBtnpressed.getMinimumWidth(), delCurSFBtnpressed.getMinimumHeight());
            delCurSFBtn.setCompoundDrawables(null, delCurSFBtnpressed, null, null);

            delAllSFBtn.setEnabled(true);
            delAllSFBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delAllSFBtnpressed.setBounds(0, 0, delAllSFBtnpressed.getMinimumWidth(), delAllSFBtnpressed.getMinimumHeight());
            delAllSFBtn.setCompoundDrawables(null, delAllSFBtnpressed, null, null);

            printCurSFBtn.setEnabled(true);
            printCurSFBtnpressed = getResources().getDrawable(R.drawable.print1);
            printCurSFBtnpressed.setBounds(0, 0, printCurSFBtnpressed.getMinimumWidth(), printCurSFBtnpressed.getMinimumHeight());
            printCurSFBtn.setCompoundDrawables(null, printCurSFBtnpressed, null, null);

            printAllSFBtn.setEnabled(true);
            printAllSFBtnpressed = getResources().getDrawable(R.drawable.print1);
            printAllSFBtnpressed.setBounds(0, 0, printAllSFBtnpressed.getMinimumWidth(), printAllSFBtnpressed.getMinimumHeight());
            printAllSFBtn.setCompoundDrawables(null, printAllSFBtnpressed, null, null);

            exportCurSFBtn.setEnabled(true);
            exportCurSFBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportCurSFBtnpressed.setBounds(0, 0, exportCurSFBtnpressed.getMinimumWidth(), exportCurSFBtnpressed.getMinimumHeight());
            exportCurSFBtn.setCompoundDrawables(null, exportCurSFBtnpressed, null, null);

            exportAllSFBtn.setEnabled(true);
            exportAllSFBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportAllSFBtnpressed.setBounds(0, 0, exportAllSFBtnpressed.getMinimumWidth(), exportAllSFBtnpressed.getMinimumHeight());
            exportAllSFBtn.setCompoundDrawables(null, exportAllSFBtnpressed, null, null);
        }
    };
    Runnable PrinterRunnable = new Runnable() {
        @Override
        public void run() {
            if(PrintConnect){         //???????????????????????????????????????????????????
                // if(printDataService != null) printDataService.disconnect();
                // printDataService = null;
                handler.removeCallbacks(PrinterRunnable);
                PrintConnect = false;
            }
            else{
                SharedPreferences shares = getSharedPreferences( "BLE_Info", Activity.MODE_PRIVATE );
                if(shares.getBoolean("BondPrinter",false)){
                    printDataService = new PrintDataService(SForceSaveActivity.this,shares.getString("Printer",""));
                    Toast.makeText(SForceSaveActivity.this,"??????????????????...",Toast.LENGTH_LONG).show();
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
        str_date = cursorSF.getString(1);
        str_company = cursorSF.getString(2);
        str_number = cursorSF.getString(3);

        str_sightNumber= cursorSF.getString(4);
        str_sightLenght= cursorSF.getString(5);
        str_sightType= cursorSF.getString(6);
        str_sightLoad= cursorSF.getString(7);

        forceMax = cursorSF.getFloat(8);
        str_forceMax = myformat.format(forceMax );

        printDataService.send("\n*******************************\n");
        printDataService.send("????????????/?????????????????????????????????");
        printDataService.send("\n*******************************\n");
        printDataService.send("????????????"+": "+ str_date+"\n");
        printDataService.send("????????????"+": "+str_company+"\n");//
        printDataService.send("????????????"+": "+ str_number+"\n");//
        printDataService.send("????????????"+": "+ str_sightNumber+"\n");//
        printDataService.send("?????????"+": "+ str_sightLenght+"m"+"\n");//
        printDataService.send("????????????"+": "+ str_sightType+"\n");//
        printDataService.send("????????????"+": "+ str_sightLoad+"\n");//
        printDataService.send("???????????????"+": "+str_forceMax+"N"+"\n");//
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
        SForceSaveActivity.this.sendBroadcast(intent);

    }
    //??????PDF??????-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(SForceSaveActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// ????????????document??????
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //????????????
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????"+ File.separator );
                    }
                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//??????????????????
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????" + File.separator + str_date+".pdf"); // pdf_address???Pdf???????????????sd????????????
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    pdfcontext = new Paragraph("????????????/?????????????????????????????????",setChineseTitleFont());
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

                    cell.setPhrase(new Phrase("??????????????????",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_forceMax,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);

                    doc.add(mtable);
                    doc.close();
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????" +  File.separator + str_date+".pdf");
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
        verifyStoragePermissions(SForceSaveActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// ????????????document??????
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //????????????
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//??????????????????
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????" + File.separator + curDate.toString ()+".pdf"); // pdf_address???Pdf???????????????sd????????????
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    if(cursorSF.moveToFirst()){
                        str_date = cursorSF.getString(1);
                        str_company = cursorSF.getString(2);
                        str_number = cursorSF.getString(3);

                        str_sightNumber= cursorSF.getString(4);
                        str_sightLenght= cursorSF.getString(5);
                        str_sightType= cursorSF.getString(6);
                        str_sightLoad= cursorSF.getString(7);

                        forceMax = cursorSF.getFloat(8);
                        str_forceMax = myformat.format(forceMax );

                        pdfcontext = new Paragraph("????????????/?????????????????????????????????",setChineseTitleFont());
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
                        cell2.setMinimumHeight(35);
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

                        cell2.setPhrase(new Phrase("??????????????????",setChineseFont()))    ;mtable2.addCell(cell2);
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
                        while(cursorSF.moveToNext()){//???????????????????????????
                            str_date = cursorSF.getString(1);
                            str_company = cursorSF.getString(2);
                            str_number = cursorSF.getString(3);

                            str_sightNumber= cursorSF.getString(4);
                            str_sightLenght= cursorSF.getString(5);
                            str_sightType= cursorSF.getString(6);
                            str_sightLoad= cursorSF.getString(7);

                            forceMax = cursorSF.getFloat(8);
                            str_forceMax = myformat.format(forceMax );

                            pdfcontext = new Paragraph("????????????/?????????????????????????????????",setChineseTitleFont());
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
                            cell.setMinimumHeight(35);
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

                            cell.setPhrase(new Phrase("??????????????????",setChineseFont()))    ;mtable.addCell(cell);
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
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "????????????/?????????????????????????????????" +  File.separator + curDate.toString () +".pdf");
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