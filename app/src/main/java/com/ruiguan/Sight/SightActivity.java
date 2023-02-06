package com.ruiguan.Sight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ruiguan.R;
import com.ruiguan.Sight.SAir.SAirActivity;
import com.ruiguan.Sight.SAngle.SAngleActivity;
import com.ruiguan.Sight.SBreak.SBreakActivity;
import com.ruiguan.Sight.SForce.SForceActivity;
import com.ruiguan.Sight.SHForce.SHForceActivity;
import com.ruiguan.Sight.SSet.SSetActivity;
import com.ruiguan.Sight.SSound.SSoundActivity;
import com.ruiguan.Sight.SStep.SStepActivity;
import com.ruiguan.Sight.STorque.STorqueActivity;
import com.ruiguan.activities.ActivityCollector;
import com.ruiguan.activities.MenuActivity;
import com.ruiguan.base.BaseActivity;
import com.ruiguan.entity.sightInput;

public class SightActivity extends BaseActivity {
    public static sightInput sight_Input;
    private Button FootForceSBtn;
    private Button HandForceSBtn;
    private Button TorqueSBtn;
    private Button BreakSBtn;
    private Button StepBtn;
    private Button AngleSBtn;
    private Button SoundSBtn;
    private Button AirSBtn;
    private Button SetSBtn;
    private Button BackSBtn;

    private String str_sightNumber;
    private String str_sightLenght;
    private String str_sightType;
    private String str_sightLoad;

    private EditText sightNumber_txt;
    private EditText sightLenght_txt;
    private Spinner sightType_txt;
    private Spinner sightLoad_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sight);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);

        View.OnClickListener bl = new SightActivity.ButtonListener();
        FootForceSBtn= (Button) findViewById(R.id.FootForceSBtn);
        HandForceSBtn= (Button) findViewById(R.id.HandForceSBtn);
        TorqueSBtn= (Button) findViewById(R.id.TorqueSBtn);
        BreakSBtn= (Button) findViewById(R.id.BreakSBtn);
        StepBtn= (Button) findViewById(R.id.StepBtn);
        AngleSBtn= (Button) findViewById(R.id.AngleSBtn);
        SoundSBtn= (Button) findViewById(R.id.SoundSBtn);
        AirSBtn= (Button) findViewById(R.id.AirSBtn);
        SetSBtn= (Button) findViewById(R.id.SetSBtn);
        BackSBtn= (Button) findViewById(R.id.BackSBtn);

        setOnClickListener(FootForceSBtn, bl);
        setOnClickListener(HandForceSBtn, bl);
        setOnClickListener(TorqueSBtn, bl);
        setOnClickListener(BreakSBtn, bl);
        setOnClickListener(AngleSBtn, bl);
        setOnClickListener(StepBtn, bl);
        setOnClickListener(SoundSBtn, bl);
        setOnClickListener(AirSBtn, bl);
        setOnClickListener(SetSBtn, bl);
        setOnClickListener(BackSBtn, bl);

        AngleSBtn.setEnabled(true);
        SoundSBtn.setEnabled(true);
        AirSBtn.setEnabled(false);
        sight_Input=new sightInput();

        sightNumber_txt=findViewById(R.id.sightNumber_txt);
        sightLenght_txt=findViewById(R.id.sightLenght_txt);
        sightType_txt=findViewById(R.id.sightType);
        sightLoad_txt=findViewById(R.id.sightLoad);
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
                case R.id.FootForceSBtn: {
                    str_sightNumber=sightNumber_txt.getText().toString();
                    str_sightLenght=sightLenght_txt.getText().toString();
                    str_sightType=sightType_txt.getSelectedItem().toString();
                    str_sightLoad=sightLoad_txt.getSelectedItem().toString();

                    sight_Input.setsightNumber(str_sightNumber);
                    sight_Input.setsightType(str_sightType);
                    sight_Input.setsightLenght(str_sightLenght);
                    sight_Input.setsightLoad(str_sightLoad);
                    Intent intent = new Intent(SightActivity.this, SForceActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.HandForceSBtn: {
                    str_sightNumber=sightNumber_txt.getText().toString();
                    str_sightLenght=sightLenght_txt.getText().toString();
                    str_sightType=sightType_txt.getSelectedItem().toString();
                    str_sightLoad=sightLoad_txt.getSelectedItem().toString();

                    sight_Input.setsightNumber(str_sightNumber);
                    sight_Input.setsightType(str_sightType);
                    sight_Input.setsightLenght(str_sightLenght);
                    sight_Input.setsightLoad(str_sightLoad);
                    Intent intent1 = new Intent(SightActivity.this, SHForceActivity.class);
                    startActivity(intent1);
                    finish();
                }
                break;
                case R.id.StepBtn: {
                    str_sightNumber=sightNumber_txt.getText().toString();
                    str_sightLenght=sightLenght_txt.getText().toString();
                    str_sightType=sightType_txt.getSelectedItem().toString();
                    str_sightLoad=sightLoad_txt.getSelectedItem().toString();

                    sight_Input.setsightNumber(str_sightNumber);
                    sight_Input.setsightType(str_sightType);
                    sight_Input.setsightLenght(str_sightLenght);
                    sight_Input.setsightLoad(str_sightLoad);
                    Intent intent2 = new Intent(SightActivity.this, SStepActivity.class);
                    startActivity(intent2);
                    finish();
                }
                break;
                case R.id.BreakSBtn: {
                    str_sightNumber=sightNumber_txt.getText().toString();
                    str_sightLenght=sightLenght_txt.getText().toString();
                    str_sightType=sightType_txt.getSelectedItem().toString();
                    str_sightLoad=sightLoad_txt.getSelectedItem().toString();

                    sight_Input.setsightNumber(str_sightNumber);
                    sight_Input.setsightType(str_sightType);
                    sight_Input.setsightLenght(str_sightLenght);
                    sight_Input.setsightLoad(str_sightLoad);
                    Intent intent3 = new Intent(SightActivity.this, SBreakActivity.class);
                    startActivity(intent3);
                    finish();
                }
                break;
                case R.id.AngleSBtn: {
                    str_sightNumber=sightNumber_txt.getText().toString();
                    str_sightLenght=sightLenght_txt.getText().toString();
                    str_sightType=sightType_txt.getSelectedItem().toString();
                    str_sightLoad=sightLoad_txt.getSelectedItem().toString();

                    sight_Input.setsightNumber(str_sightNumber);
                    sight_Input.setsightType(str_sightType);
                    sight_Input.setsightLenght(str_sightLenght);
                    sight_Input.setsightLoad(str_sightLoad);
                    Intent intent4 = new Intent(SightActivity.this, SAngleActivity.class);
                    startActivity(intent4);
                    finish();
                }
                break;
                case R.id.BackSBtn: {
                    str_sightNumber=sightNumber_txt.getText().toString();
                    str_sightLenght=sightLenght_txt.getText().toString();
                    str_sightType=sightType_txt.getSelectedItem().toString();
                    str_sightLoad=sightLoad_txt.getSelectedItem().toString();

                    sight_Input.setsightNumber(str_sightNumber);
                    sight_Input.setsightType(str_sightType);
                    sight_Input.setsightLenght(str_sightLenght);
                    sight_Input.setsightLoad(str_sightLoad);
                    Intent intent5 = new Intent(SightActivity.this, MenuActivity.class);
                    startActivity(intent5);
                    finish();
                }
                break;
                case R.id.SoundSBtn: {
                    str_sightNumber=sightNumber_txt.getText().toString();
                    str_sightLenght=sightLenght_txt.getText().toString();
                    str_sightType=sightType_txt.getSelectedItem().toString();
                    str_sightLoad=sightLoad_txt.getSelectedItem().toString();

                    sight_Input.setsightNumber(str_sightNumber);
                    sight_Input.setsightType(str_sightType);
                    sight_Input.setsightLenght(str_sightLenght);
                    sight_Input.setsightLoad(str_sightLoad);
                    Intent intent6= new Intent(SightActivity.this, SSoundActivity.class);
                    startActivity(intent6);
                    finish();
                }
                break;
                case R.id.AirSBtn: {
                    str_sightNumber=sightNumber_txt.getText().toString();
                    str_sightLenght=sightLenght_txt.getText().toString();
                    str_sightType=sightType_txt.getSelectedItem().toString();
                    str_sightLoad=sightLoad_txt.getSelectedItem().toString();

                    sight_Input.setsightNumber(str_sightNumber);
                    sight_Input.setsightType(str_sightType);
                    sight_Input.setsightLenght(str_sightLenght);
                    sight_Input.setsightLoad(str_sightLoad);
                    Intent intent7 = new Intent(SightActivity.this, SAirActivity.class);
                    startActivity(intent7);
                    finish();
                }
                break;
                case R.id.SetSBtn: {
                    str_sightNumber=sightNumber_txt.getText().toString();
                    str_sightLenght=sightLenght_txt.getText().toString();
                    str_sightType=sightType_txt.getSelectedItem().toString();
                    str_sightLoad=sightLoad_txt.getSelectedItem().toString();

                    sight_Input.setsightNumber(str_sightNumber);
                    sight_Input.setsightType(str_sightType);
                    sight_Input.setsightLenght(str_sightLenght);
                    sight_Input.setsightLoad(str_sightLoad);
                    Intent intent8 = new Intent(SightActivity.this, SSetActivity.class);
                    startActivity(intent8);
                    finish();
                }
                break;
                case R.id.TorqueSBtn: {
                    str_sightNumber=sightNumber_txt.getText().toString();
                    str_sightLenght=sightLenght_txt.getText().toString();
                    str_sightType=sightType_txt.getSelectedItem().toString();
                    str_sightLoad=sightLoad_txt.getSelectedItem().toString();

                    sight_Input.setsightNumber(str_sightNumber);
                    sight_Input.setsightType(str_sightType);
                    sight_Input.setsightLenght(str_sightLenght);
                    sight_Input.setsightLoad(str_sightLoad);
                    Intent intent9 = new Intent(SightActivity.this, STorqueActivity.class);
                    startActivity(intent9);
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
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
