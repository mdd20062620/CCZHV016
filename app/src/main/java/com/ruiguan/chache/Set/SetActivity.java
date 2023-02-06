package com.ruiguan.chache.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ruiguan.R;
import com.ruiguan.Sight.SSet.SSetActivity;
import com.ruiguan.Sight.SightActivity;
import com.ruiguan.activities.ActivityCollector;
import com.ruiguan.base.BaseActivity;
import com.ruiguan.chache.ChacheActivity;

public class SetActivity extends BaseActivity {
    private Button initSetBtn;
    private Button okSetBtn;
    private Button cancelSetBtn;

    private EditText forceMinSet_txt;
    private EditText forceMaxSet_txt;
    private EditText forceErrSet_txt;

    private String str_forceMinSet=" ";
    private String str_forceMaxSet=" ";
    private String str_forceErrSet=" ";

    private float forceMinSet=0.0f;
    private float forceMaxSet=0.0f;
    private float forceErrSet=0.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);
        initSetBtn = findViewById(R.id.initSetBtn);
        okSetBtn = findViewById(R.id.okSetBtn);
        cancelSetBtn = findViewById(R.id.cancelSetBtn);
        View.OnClickListener bl = new SetActivity.ButtonListener();
        setOnClickListener(initSetBtn, bl);
        setOnClickListener(okSetBtn, bl);
        setOnClickListener(cancelSetBtn, bl);

        forceMinSet_txt= findViewById(R.id.forceMinSet_txt);
        forceMaxSet_txt= findViewById(R.id.forceMaxSet_txt);
        forceErrSet_txt= findViewById(R.id.forceErrSet_txt);

        SharedPreferences shares = getSharedPreferences( "setCalibtion", Activity.MODE_PRIVATE );
        if(!shares.getBoolean("setCalibDecive",false))
        {
            Toast.makeText(SetActivity.this, "未保存参数设置数据！", Toast.LENGTH_SHORT).show();
        }else {
            str_forceMinSet=shares.getString("forceMinSet","");
            str_forceMaxSet=shares.getString("forceMaxSet","");
            str_forceErrSet=shares.getString("forceErrSet","");
            forceMinSet=Float.valueOf(str_forceMinSet);
            forceMaxSet=Float.valueOf(str_forceMaxSet);
            forceErrSet=Float.valueOf(str_forceErrSet);
        }

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
                case R.id.initSetBtn: {
                    forceMinSet_txt.setText("6");
                    forceMaxSet_txt.setText("20");
                    forceErrSet_txt.setText("5");
                }
                break;
                case R.id.okSetBtn: {

                    str_forceMinSet=forceMinSet_txt.getText().toString();
                    str_forceMaxSet=forceMaxSet_txt.getText().toString();
                    str_forceErrSet=forceErrSet_txt.getText().toString();

                    forceMinSet=Float.valueOf(str_forceMinSet);
                    forceMaxSet=Float.valueOf(str_forceMaxSet);
                    forceErrSet=Float.valueOf(str_forceErrSet);

                    SharedPreferences mySharedPreferences1 = getSharedPreferences( "setCalibtion", Activity.MODE_PRIVATE );
                    SharedPreferences.Editor editor1 = mySharedPreferences1.edit();
                    editor1.putString("forceMinSet", str_forceMinSet);
                    editor1.putString("forceMaxSet", str_forceMaxSet);
                    editor1.putString("forceErrSet", str_forceErrSet);
                    editor1.putBoolean("setCalibDecive",true);
                    editor1.apply();
                    Intent intent2 = new Intent(SetActivity.this, ChacheActivity.class);
                    startActivity(intent2);
                    finish();
                }
                break;
                case R.id.cancelSetBtn: {
                    Intent intent3 = new Intent(SetActivity.this, ChacheActivity.class);
                    startActivity(intent3);
                    finish();
                }
                break;

                default: {
                }
                break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        super.onDestroy();
    }
}
