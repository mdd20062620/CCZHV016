package com.ruiguan.chache.Break;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.ruiguan.activities.MenuActivity.input_data;
import static com.ruiguan.chache.ChacheActivity.chache_Input;

public class BreakDB extends SQLiteOpenHelper {
    private String str_company;
    private String str_number;
    private String str_chacheNumber;
    private String str_chacheType;
    private String str_chacheGroup;

    public BreakDB(Context context) {
        super(context, "break.db", null, 1);
    }
    //创建table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + "breakSave" + " (" + "id"+
                " INTEGER primary key autoincrement, " + "date" + " text, "+ "company" +" text, "+"number"+" text, "
                + "chacheNumber" +" text, "+ "chacheType" +" text, "+ "chacheGroup" +" text, "
                +"breakSpeed"+" real, " +"ASpeedMax"+" real, "+"breakDis"+" real, "+"breakTime"+" real, "+"tabanForceMax"+" real);";
        db.execSQL(sql);
        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_chacheNumber= chache_Input.getchacheNumber();
        str_chacheType= chache_Input.getchacheType();
        str_chacheGroup= chache_Input.getchacheGroup();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + "breakSave";
        db.execSQL(sql);
        onCreate(db);
    }

    public Cursor select() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query("breakSave", null, null, null, null, null, null);
        return cursor;
    }
    //增加操作
    public long insert(String str_date,String str_breakSpeed,String str_ASpeedMax,String str_breakDis,String str_breakTime,String str_tabanForceMax)
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
        cv.put("breakSpeed", str_breakSpeed);
        cv.put("ASpeedMax", str_ASpeedMax);
        cv.put("breakDis", str_breakDis);
        cv.put("breakTime", str_breakTime);
        cv.put("tabanForceMax", str_tabanForceMax);

        long row = db.insert("breakSave", null, cv);
        return row;
    }
    //删除操作
    public void delete(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "id" + " = ?";
        String[] whereValue ={ Integer.toString(id) };
        db.delete("breakSave", where, whereValue);
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
        cv.put("ratedSpeed","2.5");
        cv.put("overspeed", "2.89");
        db.update("breakSave", cv, where, whereValue);
    }
}