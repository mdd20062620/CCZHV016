package com.ruiguan.chache.HForce;

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

public class HForceSaveActivity extends BaseActivity implements AdapterView.OnItemClickListener {
private HForceDB saveHFDB;
private Cursor cursorHF;

private Button delCurHFBtn;
private Button delAllHFBtn;
private Button printCurHFBtn;
private Button printAllHFBtn;
private Button exportCurHFBtn;
private Button exportAllHFBtn;
private Button backHFBtn;
private Button exitHFBtn;

private Drawable delCurHFBtnpressed;
private Drawable delAllHFBtnpressed;
private Drawable printCurHFBtnpressed;
private Drawable printAllHFBtnpressed;
private Drawable exportCurHFBtnpressed;
private Drawable exportAllHFBtnpressed;

private String str_date;
private String str_company;
private String str_number;
private String str_chacheNumber;
private String str_chacheType;
private String str_chacheGroup;
private String str_hforceMax;
private float hforceMax;

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
        setContentView(R.layout.activity_hforce_save);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);

        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_chacheNumber= chache_Input.getchacheNumber();
        str_chacheType= chache_Input.getchacheType();
        str_chacheGroup= chache_Input.getchacheGroup();

        printCurHFBtn = (Button) findViewById(R.id.printCurHFBtn);
        printAllHFBtn = (Button) findViewById(R.id.printAllHFBtn);
        delCurHFBtn = (Button) findViewById(R.id.delCurHFBtn);
        delAllHFBtn= (Button) findViewById(R.id.delAllHFBtn);
        exportCurHFBtn= (Button) findViewById(R.id.exportCurHFBtn);
        exportAllHFBtn= (Button) findViewById(R.id.exportAllHFBtn);
        backHFBtn= (Button) findViewById(R.id.backHFBtn);
        exitHFBtn= (Button) findViewById(R.id.exitHFBtn);
        View.OnClickListener bl = new HForceSaveActivity.ButtonListener();
        setOnClickListener(printCurHFBtn, bl);
        setOnClickListener(printAllHFBtn, bl);
        setOnClickListener(delCurHFBtn, bl);
        setOnClickListener(delAllHFBtn, bl);
        setOnClickListener(exportCurHFBtn, bl);
        setOnClickListener(exportAllHFBtn, bl);
        setOnClickListener(backHFBtn, bl);
        setOnClickListener(exitHFBtn, bl);
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
        saveHFDB.close();
        cursorHF.close();
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
            case R.id.printCurHFBtn: {
                printCurHFBtn.setEnabled(false);
                printCurHFBtnpressed = getResources().getDrawable(R.drawable.print);
                printCurHFBtnpressed.setBounds(0, 0, printCurHFBtnpressed.getMinimumWidth(), printCurHFBtnpressed.getMinimumHeight());
                printCurHFBtn.setCompoundDrawables(null, printCurHFBtnpressed, null, null);

                if (printDevice == null) {           //首次连接打印机
                    SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                    if (!shares.getBoolean("BondPrinter", false)) {
                        Toast.makeText(HForceSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HForceSaveActivity.this.getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                } else {          //打印数据
                    printDataService = new PrintDataService(HForceSaveActivity.this,deviceAddress);
                    printDataService.putDevice(printDevice);
                    if(cursorHF.moveToFirst()){
                        ID = cursorHF.getInt(0);
                        if (ID == data_ID) {
                            str_date = cursorHF.getString(1);
                            str_company = cursorHF.getString(2);
                            str_number = cursorHF.getString(3);

                            str_chacheNumber= cursorHF.getString(4);
                            str_chacheType= cursorHF.getString(5);
                            str_chacheGroup= cursorHF.getString(6);

                            hforceMax = cursorHF.getFloat(7);
                            str_hforceMax = myformat.format(hforceMax );
                            PrintMeasureData();
                            break;
                        }
                        while(cursorHF.moveToNext()){//遍历数据表中的数据
                            ID = cursorHF.getInt(0);
                            if (ID == data_ID) {
                                str_date = cursorHF.getString(1);
                                str_company = cursorHF.getString(2);
                                str_number = cursorHF.getString(3);

                                str_chacheNumber= cursorHF.getString(4);
                                str_chacheType= cursorHF.getString(5);
                                str_chacheGroup= cursorHF.getString(6);

                                hforceMax = cursorHF.getFloat(7);
                                str_hforceMax = myformat.format(hforceMax );
                                PrintMeasureData();
                                break;
                            }
                        }
                    }
                }
            }
            break;
            case R.id.printAllHFBtn: {
                printAllHFBtn.setEnabled(false);
                printAllHFBtnpressed = getResources().getDrawable(R.drawable.print);
                printAllHFBtnpressed.setBounds(0, 0, printAllHFBtnpressed.getMinimumWidth(), printAllHFBtnpressed.getMinimumHeight());
                printAllHFBtn.setCompoundDrawables(null, printAllHFBtnpressed, null, null);
                if (printDataService == null) {           //首次连接打印机
                    SharedPreferences shares = getSharedPreferences("BLE_Info", Activity.MODE_PRIVATE);
                    if (!shares.getBoolean("BondPrinter", false)) {
                        Toast.makeText(HForceSaveActivity.this, "未找到配对打印机！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HForceSaveActivity.this.getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    handler.postDelayed(PrinterRunnable, 100);          //启动监听蓝牙设备进程
                } else {          //打印数据
                    printDataService.putDevice(printDevice);
                    if(cursorHF.moveToFirst()){
                        str_date = cursorHF.getString(1);
                        str_company = cursorHF.getString(2);
                        str_number = cursorHF.getString(3);

                        str_chacheNumber= cursorHF.getString(4);
                        str_chacheType= cursorHF.getString(5);
                        str_chacheGroup= cursorHF.getString(6);

                        hforceMax = cursorHF.getFloat(7);
                        str_hforceMax = myformat.format(hforceMax );
                        PrintMeasureData();
                        while(cursorHF.moveToNext()){//遍历数据表中的数据
                            str_date = cursorHF.getString(1);
                            str_company = cursorHF.getString(2);
                            str_number = cursorHF.getString(3);

                            str_chacheNumber= cursorHF.getString(4);
                            str_chacheType= cursorHF.getString(5);
                            str_chacheGroup= cursorHF.getString(6);

                            hforceMax = cursorHF.getFloat(7);
                            str_hforceMax = myformat.format(hforceMax );
                            PrintMeasureData();
                        }
                    }
                }
            }
            break;
            case R.id.delCurHFBtn: {
                delCurHFBtn.setEnabled(false);
                delCurHFBtnpressed = getResources().getDrawable(R.drawable.delete);
                delCurHFBtnpressed.setBounds(0, 0, delCurHFBtnpressed.getMinimumWidth(), delCurHFBtnpressed.getMinimumHeight());
                delCurHFBtn.setCompoundDrawables(null, delCurHFBtnpressed, null, null);
                forceDelete();
            }
            break;
            case R.id.delAllHFBtn: {
                delAllHFBtn.setEnabled(false);
                delAllHFBtnpressed = getResources().getDrawable(R.drawable.delete);
                delAllHFBtnpressed.setBounds(0, 0, delAllHFBtnpressed.getMinimumWidth(), delAllHFBtnpressed.getMinimumHeight());
                delAllHFBtn.setCompoundDrawables(null,delAllHFBtnpressed, null, null);
                while(cursorHF.moveToFirst()){
                    data_ID = cursorHF.getInt(0);
                    forceDelete();
                }
            }
            break;
            case R.id.exportCurHFBtn: {
                exportCurHFBtn.setEnabled(false);
                exportCurHFBtnpressed = getResources().getDrawable(R.drawable.export);
                exportCurHFBtnpressed.setBounds(0, 0, exportCurHFBtnpressed.getMinimumWidth(), exportCurHFBtnpressed.getMinimumHeight());
                exportCurHFBtn.setCompoundDrawables(null,exportCurHFBtnpressed, null, null);
                if(cursorHF.moveToFirst()){
                    ID = cursorHF.getInt(0);
                    if (ID == data_ID) {
                        str_date = cursorHF.getString(1);
                        str_company = cursorHF.getString(2);
                        str_number = cursorHF.getString(3);

                        str_chacheNumber= cursorHF.getString(4);
                        str_chacheType= cursorHF.getString(5);
                        str_chacheGroup= cursorHF.getString(6);

                        hforceMax = cursorHF.getFloat(7);
                        str_hforceMax = myformat.format(hforceMax );
                        CreatePdf();
                        Toast.makeText(HForceSaveActivity.this, "数据已导出到手机根目录/Documents/叉车手刹力检测报告", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    while(cursorHF.moveToNext()){//遍历数据表中的数据
                        ID = cursorHF.getInt(0);
                        if (ID == data_ID) {
                            str_date = cursorHF.getString(1);
                            str_company = cursorHF.getString(2);
                            str_number = cursorHF.getString(3);

                            str_chacheNumber= cursorHF.getString(4);
                            str_chacheType= cursorHF.getString(5);
                            str_chacheGroup= cursorHF.getString(6);

                            hforceMax = cursorHF.getFloat(7);
                            str_hforceMax = myformat.format(hforceMax );
                            CreatePdf();
                            Toast.makeText(HForceSaveActivity.this, "数据已导出到手机根目录/Documents/叉车手刹力检测报告", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            }
            break;
            case R.id.exportAllHFBtn: {
                exportAllHFBtn.setEnabled(false);
                exportAllHFBtnpressed = getResources().getDrawable(R.drawable.export);
                exportAllHFBtnpressed.setBounds(0, 0, exportAllHFBtnpressed.getMinimumWidth(), exportAllHFBtnpressed.getMinimumHeight());
                exportAllHFBtn.setCompoundDrawables(null,exportAllHFBtnpressed, null, null);
                CreatePdfAll();
                Toast.makeText(HForceSaveActivity.this, "数据已导出到手机根目录/Documents/叉车手刹力检测报告", Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.backHFBtn: {
                Intent intent = new Intent(HForceSaveActivity.this, HForceActivity.class);
                startActivity(intent);
                finish();
            }
            break;
            case R.id.exitHFBtn: {
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
        saveHFDB = new HForceDB(this);
        cursorHF = saveHFDB.select();
        savelistView = (ListView)findViewById(R.id.savelistHF);
        savelistView.setAdapter(new HForceSaveActivity.saveListAdapter(this, cursorHF));
        savelistView.setOnItemClickListener(this);
    }
    public void hforceAdd(String str_forceMax){
        Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
        String   str_date   =   formatter.format(curDate);
        saveHFDB.insert(str_date,str_forceMax);
        cursorHF.requery();
        savelistView.invalidateViews();
    }
    public void forceDelete(){
        if (data_ID == 0) {
            return;
        }
        saveHFDB.delete(data_ID);
        cursorHF.requery();
        savelistView.invalidateViews();
//        Toast.makeText(this, "删除成功!", Toast.LENGTH_SHORT).show();
    }

    public void forceUpdate(){
        saveHFDB.update(data_ID);
        cursorHF.requery();
        savelistView.invalidateViews();
        Toast.makeText(this, "Update Successed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cursorHF.moveToPosition(position);
        data_ID = cursorHF.getInt(0);
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
        cursorHF.moveToPosition(position);

        TextView date_txt = (TextView) convertView.findViewById(R.id.dateSave);
        TextView company_txt = (TextView) convertView.findViewById(R.id.companySave);
        TextView deviceNum_txt = (TextView) convertView.findViewById(R.id.deviceNumSave);

        TextView chacheNumber_txt = (TextView) convertView.findViewById(R.id.chacheNumber);
        TextView chacheType_txt = (TextView) convertView.findViewById(R.id.chacheType);
        TextView chacheGroup_txt = (TextView) convertView.findViewById(R.id.chacheGroup);

        TextView forceMax_txt = (TextView) convertView.findViewById(R.id.breakSpeed);

        date_txt.setText("检测时间："+cursorHF.getString(1));
        company_txt.setText("受检单位："+cursorHF.getString(2));
        deviceNum_txt.setText("设备编号："+cursorHF.getString(3));

        chacheNumber_txt.setText("车牌编号："+cursorHF.getString(4));
        chacheType_txt.setText("车辆类型："+cursorHF.getString(5));
        chacheGroup_txt.setText("车辆组别："+cursorHF.getString(6));
        forceMax_txt.setText("最大受力值："+cursorHF.getString(7)+"N");
        return convertView;
    }

    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            delCurHFBtn.setEnabled(true);
            delCurHFBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delCurHFBtnpressed.setBounds(0, 0, delCurHFBtnpressed.getMinimumWidth(), delCurHFBtnpressed.getMinimumHeight());
            delCurHFBtn.setCompoundDrawables(null, delCurHFBtnpressed, null, null);

            delAllHFBtn.setEnabled(true);
            delAllHFBtnpressed = getResources().getDrawable(R.drawable.delete1);
            delAllHFBtnpressed.setBounds(0, 0, delAllHFBtnpressed.getMinimumWidth(), delAllHFBtnpressed.getMinimumHeight());
            delAllHFBtn.setCompoundDrawables(null, delAllHFBtnpressed, null, null);

            printCurHFBtn.setEnabled(true);
            printCurHFBtnpressed = getResources().getDrawable(R.drawable.print1);
            printCurHFBtnpressed.setBounds(0, 0, printCurHFBtnpressed.getMinimumWidth(), printCurHFBtnpressed.getMinimumHeight());
            printCurHFBtn.setCompoundDrawables(null, printCurHFBtnpressed, null, null);

            printAllHFBtn.setEnabled(true);
            printAllHFBtnpressed = getResources().getDrawable(R.drawable.print1);
            printAllHFBtnpressed.setBounds(0, 0, printAllHFBtnpressed.getMinimumWidth(), printAllHFBtnpressed.getMinimumHeight());
            printAllHFBtn.setCompoundDrawables(null, printAllHFBtnpressed, null, null);

            exportCurHFBtn.setEnabled(true);
            exportCurHFBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportCurHFBtnpressed.setBounds(0, 0, exportCurHFBtnpressed.getMinimumWidth(), exportCurHFBtnpressed.getMinimumHeight());
            exportCurHFBtn.setCompoundDrawables(null, exportCurHFBtnpressed, null, null);

            exportAllHFBtn.setEnabled(true);
            exportAllHFBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportAllHFBtnpressed.setBounds(0, 0, exportAllHFBtnpressed.getMinimumWidth(), exportAllHFBtnpressed.getMinimumHeight());
            exportAllHFBtn.setCompoundDrawables(null, exportAllHFBtnpressed, null, null);
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
                    printDataService = new PrintDataService(HForceSaveActivity.this,shares.getString("Printer",""));
                    Toast.makeText(HForceSaveActivity.this,"打印机连接中...",Toast.LENGTH_LONG).show();
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
        str_date = cursorHF.getString(1);
        str_company = cursorHF.getString(2);
        str_number = cursorHF.getString(3);

        str_chacheNumber= cursorHF.getString(4);
        str_chacheType= cursorHF.getString(5);
        str_chacheGroup= cursorHF.getString(6);

        hforceMax = cursorHF.getFloat(7);
        str_hforceMax = myformat.format(hforceMax );

        printDataService.send("\n*******************************\n");
        printDataService.send("叉车手刹力检测结果");
        printDataService.send("\n*******************************\n");
        printDataService.send("检测时间"+": "+ str_date+"\n");
        printDataService.send("受检单位"+": "+str_company+"\n");//
        printDataService.send("设备编号"+": "+ str_number+"\n");//
        printDataService.send("车牌编号"+": "+ str_chacheNumber+"\n");//
        printDataService.send("车辆类型"+": "+ str_chacheType+"\n");//
        printDataService.send("车辆组别"+": "+ str_chacheGroup+"\n");//
        printDataService.send("最大受力值"+": "+str_hforceMax+"N"+"\n");//
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
        HForceSaveActivity.this.sendBroadcast(intent);

    }
    //创建PDF文件-----------------------------------------------------------------
    Document doc;
    private void CreatePdf(){
        verifyStoragePermissions(HForceSaveActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //创建目录
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车手刹力检测报告"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车手刹力检测报告"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车手刹力检测报告" + File.separator +str_date+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    pdfcontext = new Paragraph("叉车手刹力检测报告",setChineseTitleFont());
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

                    cell.setPhrase(new Phrase("最大受力值：",setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase(str_hforceMax,setChineseFont()))    ;mtable.addCell(cell);
                    cell.setPhrase(new Phrase("N",setChineseFont()))    ;mtable.addCell(cell);

                    doc.add(mtable);
                    doc.close();
                    fos.flush();
                    fos.close();
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车手刹力检测报告" +  File.separator + str_date+".pdf");
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
        verifyStoragePermissions(HForceSaveActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doc = new Document(PageSize.A4, 36, 36, 36, 36);// 创建一个document对象
                FileOutputStream fos;
                Paragraph pdfcontext;
                try {
                    //创建目录
                    File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车手刹力检测报告"+ File.separator);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                        notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车手刹力检测报告"+ File.separator );
                    }

                    Uri uri = Uri.fromFile(destDir);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
                    getApplication().getApplicationContext().sendBroadcast(intent);
                    Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
                    fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车手刹力检测报告" + File.separator + curDate.toString ()+".pdf"); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    if(cursorHF.moveToFirst()){
                        str_date = cursorHF.getString(1);
                        str_company = cursorHF.getString(2);
                        str_number = cursorHF.getString(3);

                        str_chacheNumber= cursorHF.getString(4);
                        str_chacheType= cursorHF.getString(5);
                        str_chacheGroup= cursorHF.getString(6);

                        hforceMax = cursorHF.getFloat(7);
                        str_hforceMax = myformat.format(hforceMax );

                        pdfcontext = new Paragraph("叉车手刹力检测报告",setChineseTitleFont());
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
                        cell2.setMinimumHeight(38);
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

                        cell2.setPhrase(new Phrase("最大受力值：",setChineseFont()))    ;mtable2.addCell(cell2);
                        cell2.setPhrase(new Phrase(str_hforceMax,setChineseFont()))    ;mtable2.addCell(cell2);
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
                        while(cursorHF.moveToNext()){//遍历数据表中的数据
                            str_date = cursorHF.getString(1);
                            str_company = cursorHF.getString(2);
                            str_number = cursorHF.getString(3);

                            str_chacheNumber= cursorHF.getString(4);
                            str_chacheType= cursorHF.getString(5);
                            str_chacheGroup= cursorHF.getString(6);

                            hforceMax = cursorHF.getFloat(7);
                            str_hforceMax = myformat.format(hforceMax );

                            pdfcontext = new Paragraph("叉车手刹力检测报告",setChineseTitleFont());
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
                            cell.setMinimumHeight(38);
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

                            cell.setPhrase(new Phrase("最大受力值：",setChineseFont()))    ;mtable.addCell(cell);
                            cell.setPhrase(new Phrase(str_hforceMax,setChineseFont()))    ;mtable.addCell(cell);
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
                    notifySystemToScan(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "叉车手刹力检测报告" +  File.separator + curDate.toString () +".pdf");
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


