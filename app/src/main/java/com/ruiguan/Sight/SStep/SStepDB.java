package com.ruiguan.Sight.SStep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.ruiguan.Sight.SightActivity.sight_Input;
import static com.ruiguan.activities.MenuActivity.input_data;

public class SStepDB extends SQLiteOpenHelper {
    private String str_company;
    private String str_number;
    private String str_sightNumber;
    private String str_sightLenght;
    private String str_sightType;
    private String str_sightLoad;

    public SStepDB(Context context) {
        super(context, "SStepSave.db", null, 1);
    }
    //创建table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + "SStepSave" + " (" + "id"+
                " INTEGER primary key autoincrement, " + "date" + " text, "+ "company" +" text, "+"number"+" text, "
                +"sightNumber" +" text, "+ "sightLenght" +" text, "+ "sightType" +" text, "+ "sightLoad" +" text, "+ "dis" +" text, "
                + "errLongitude" +" real, "+ "errLattitude" +" text, "+ "startDis" +" text, "+ "endDis" +" text, "+ "errDisMax" +" text, "+"stepMax"+" text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + "SStepSave";
        db.execSQL(sql);
        onCreate(db);
    }
    public Cursor select() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query("SStepSave", null, null, null, null, null, null);
        return cursor;
    }
    //增加操作
    public long insert(String str_date,String str_Dis,String str_errLongitude,String str_errLattitude,String str_startDis,String str_endDis,String str_errDisMax,String str_stepMax)
    {
        str_company=input_data.getCom();
        str_number=input_data.getNumber();
        str_sightNumber= sight_Input.getsightNumber();
        str_sightLenght= sight_Input.getsightLenght();
        str_sightType= sight_Input.getsightType();
        str_sightLoad= sight_Input.getsightLoad();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", str_date);
        cv.put("company", str_company);
        cv.put("number", str_number);
        cv.put("sightNumber", str_sightNumber);
        cv.put("sightLenght", str_sightLenght);
        cv.put("sightType", str_sightType);
        cv.put("sightLoad", str_sightLoad);
        cv.put("Dis", str_Dis);
        cv.put("errLongitude", str_errLongitude);
        cv.put("errLattitude", str_errLattitude);
        cv.put("startDis", str_startDis);
        cv.put("endDis", str_endDis);
        cv.put("errDisMax", str_errDisMax);
        cv.put("stepMax", str_stepMax);


        long row = db.insert("SStepSave", null, cv);
        return row;
    }
    //删除操作
    public void delete(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "id" + " = ?";
        String[] whereValue ={ Integer.toString(id) };
        db.delete("SStepSave", where, whereValue);
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
        db.update("SStepSave", cv, where, whereValue);
    }

}
