package com.ruiguan.Sight.SSet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ruiguan.R;
import com.ruiguan.Sight.SightActivity;
import com.ruiguan.base.BaseActivity;

public class SSetActivity extends BaseActivity {

    private Button stopSetSBtn;
    private Button zeroSetSBtn;

    private EditText StepMAX30_txt;
    private EditText StepMAX20_txt;
    private EditText StepMAX20L_txt;
    private EditText StepMAX10L_txt;

    private String str_StepMAX30;
    private String str_StepMAX20;
    private String str_StepMAX20L;
    private String str_StepMAX10L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sset);

        stopSetSBtn = findViewById(R.id.okSetSBtn);
        zeroSetSBtn = findViewById(R.id.cencelSetSBtn);
        View.OnClickListener bl = new SSetActivity.ButtonListener();
        setOnClickListener(stopSetSBtn, bl);
        setOnClickListener(zeroSetSBtn, bl);

        StepMAX30_txt=findViewById(R.id.StepMAX30_txt);
        StepMAX20_txt=findViewById(R.id.StepMAX20_txt);
        StepMAX20L_txt=findViewById(R.id.StepMAX20L_txt);
        StepMAX10L_txt=findViewById(R.id.StepMAX10L_txt);

        SharedPreferences shares1 = getSharedPreferences( "stepValue", Activity.MODE_PRIVATE );
        if(shares1.getBoolean("stepValueDecive",false))
        {
            str_StepMAX30=shares1.getString("StepMAX30","");
            str_StepMAX20=shares1.getString("StepMAX20","");
            str_StepMAX20L=shares1.getString("StepMAX20L","");
            str_StepMAX10L=shares1.getString("StepMAX10L","");

            StepMAX30_txt.setText(str_StepMAX30);
            StepMAX20_txt.setText(str_StepMAX20);
            StepMAX20L_txt.setText(str_StepMAX20L);
            StepMAX10L_txt.setText(str_StepMAX10L);
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
                case R.id.okSetSBtn: {
                    str_StepMAX30=StepMAX30_txt.getText().toString();
                    str_StepMAX20=StepMAX20_txt.getText().toString();
                    str_StepMAX20L=StepMAX20L_txt.getText().toString();
                    str_StepMAX10L=StepMAX10L_txt.getText().toString();

                    SharedPreferences mySharedPreferences1 = getSharedPreferences( "stepValue", Activity.MODE_PRIVATE );
                    SharedPreferences.Editor editor1 = mySharedPreferences1.edit();
                    editor1.putString("StepMAX30", str_StepMAX30);
                    editor1.putString("StepMAX20", str_StepMAX20);
                    editor1.putString("StepMAX20L", str_StepMAX20L);
                    editor1.putString("StepMAX10L", str_StepMAX10L);
                    editor1.putBoolean("stepValueDecive",true);
                    editor1.apply();
                    Intent intent2 = new Intent(SSetActivity.this, SightActivity.class);
                    startActivity(intent2);
                    finish();
                }
                break;
                case R.id.cencelSetSBtn: {
                    Intent intent3 = new Intent(SSetActivity.this, SightActivity.class);
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
}
