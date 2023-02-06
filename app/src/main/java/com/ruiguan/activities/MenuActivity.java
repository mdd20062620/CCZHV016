package com.ruiguan.activities;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ruiguan.R;
import com.ruiguan.Sight.SightActivity;
import com.ruiguan.base.BaseActivity;
import com.ruiguan.chache.ChacheActivity;
import com.ruiguan.entity.inputData;


public class MenuActivity extends BaseActivity {
    public static inputData input_data;
    private Button ChacheBtn;
    private Button SightBtn;
    private String str_company;
    private String str_number;
    private EditText company_txt;
    private EditText number_txt;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);

        View.OnClickListener bl = new MenuActivity.ButtonListener();
        ChacheBtn=findViewById(R.id.ChacheBtn);
        SightBtn=findViewById(R.id.SightBtn);
        setOnClickListener(ChacheBtn, bl);
        setOnClickListener(SightBtn, bl);

        company_txt=findViewById(R.id.company_txt);
        number_txt=findViewById(R.id.number_txt);
        input_data=new inputData();
        SharedPreferences shares1 = getSharedPreferences( "ratedValue", Activity.MODE_PRIVATE );
        if(shares1.getBoolean("ratedValueDecive",false))
        {
            str_company=shares1.getString("company","");
            str_number=shares1.getString("number","");
            company_txt.setText(str_company);
            number_txt.setText(str_number);
        }
         handler.postDelayed(getInputRunnable,100);
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
                case R.id.ChacheBtn:
                    str_company=company_txt.getText().toString();
                    str_number=number_txt.getText().toString();
                    input_data.setCom(str_company);
                    input_data.setNumber(str_number);

                    SharedPreferences mySharedPreferences1 = getSharedPreferences( "ratedValue", Activity.MODE_PRIVATE );
                    SharedPreferences.Editor editor1 = mySharedPreferences1.edit();
                    editor1.putString("company", str_company);
                    editor1.putString("number", str_number);
                    editor1.putBoolean("ratedValueDecive",true);
                    editor1.apply();
                    Intent intent = new Intent(MenuActivity.this, ChacheActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.SightBtn:
                    str_company=company_txt.getText().toString();
                    str_number=number_txt.getText().toString();
                    input_data.setCom(str_company);
                    input_data.setNumber(str_number);
                    SharedPreferences mySharedPreferences2 = getSharedPreferences( "ratedValue", Activity.MODE_PRIVATE );
                    SharedPreferences.Editor editor2 = mySharedPreferences2.edit();
                    editor2.putString("company", str_company);
                    editor2.putString("number", str_number);
                    editor2.putBoolean("ratedValueDecive",true);
                    editor2.apply();
                    Intent intent1 = new Intent(MenuActivity.this, SightActivity.class);
                    startActivity(intent1);
                    finish();
                    break;
                default: {
                }
                break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        handler.removeCallbacks (getInputRunnable);
    }
    Runnable getInputRunnable = new Runnable() {
        @Override
        public void run() {
            str_company=company_txt.getText().toString();
            str_number=number_txt.getText().toString();
            handler.postDelayed(getInputRunnable,100);
        }
    };
}
