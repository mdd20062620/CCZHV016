package com.ruiguan.chache.Angle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.ruiguan.activities.MenuActivity.input_data;
import static com.ruiguan.chache.ChacheActivity.chache_Input;

public class AngleDB extends SQLiteOpenHelper {
    private String str_date;
    private String str_company;
    private String str_number;
    private String str_chacheNumber;
    private String str_chacheType;
    private String str_chacheGroup;;
    public AngleDB(Context context) {
        super(context, "AngleSave.db", null, 1);
    }
    //创建table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + "AngleSave" + " (" + "id"+
                " INTEGER primary key autoincrement, " + "date" + " text, "+ "company" +" text, "+"number" +" text, "
                +"chacheNumber" +" text, "+ "chacheType" +" text, "+ "chacheGroup" +" text, " +"testItem"+" text, "
                +"angleMaxL"+" real, "+"angleMaxR"+" real, "+"angleMaxWL"+" real, "+"angleMaxWR"+" real, "
                +"angleMaxZHL"+" real, "+"angleMaxZHR"+" real, " +"angleMaxZNL"+" real, "+"angleMaxZNR"+" real);";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + "AngleSave";
        db.execSQL(sql);
        onCreate(db);
    }
    public Cursor select() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query("AngleSave", null, null, null, null, null, null);
        return cursor;
    }
    //增加操作
    public long insert(String str_date,String str_testItem,String str_angleMaxL,String str_angleMaxR,String str_angleMaxWL,String str_angleMaxWR,String str_angleMaxZHL,String str_angleMaxZHR,String str_angleMaxZNL,String str_angleMaxZNR)
    {
        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_chacheNumber= chache_Input.getchacheNumber();
        str_chacheType= chache_Input.getchacheType();
        str_chacheGroup= chache_Input.getchacheGroup();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", str_date);
        cv.put("company", str_company);
        cv.put("number", str_number);
        cv.put("chacheNumber", str_chacheNumber);
        cv.put("chacheType", str_chacheType);
        cv.put("chacheGroup", str_chacheGroup);
        cv.put("testItem",str_testItem);
        cv.put("angleMaxL",str_angleMaxL);
        cv.put("angleMaxR",str_angleMaxR);
        cv.put("angleMaxWL",str_angleMaxWL);
        cv.put("angleMaxWR",str_angleMaxWR);
        cv.put("angleMaxZHL",str_angleMaxZHL);
        cv.put("angleMaxZHR",str_angleMaxZHR);
        cv.put("angleMaxZNL",str_angleMaxZNL);
        cv.put("angleMaxZNR",str_angleMaxZNR);

        long row = db.insert("AngleSave", null, cv);
        return row;
    }
    //删除操作
    public void delete(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "id" + " = ?";
        String[] whereValue ={ Integer.toString(id) };
        db.delete("AngleSave", where, whereValue);
    }
    //修改操作
    public void update(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "id" + " = ?";
        String[] whereValue = { Integer.toString(id) };

        ContentValues cv = new ContentValues();
        cv.put("date", "20190629");
        cv.put("company", "大连机电工程有限公司");
        cv.put("number", "RT240");
        cv.put("testItem","手刹力");
        cv.put("forceMax", "256.36");
        db.update("AngleSave", cv, where, whereValue);
    }
}
