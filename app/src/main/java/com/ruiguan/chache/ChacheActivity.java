package com.ruiguan.chache;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.ruiguan.R;
import com.ruiguan.activities.ActivityCollector;
import com.ruiguan.activities.MenuActivity;
import com.ruiguan.base.BaseActivity;
import com.ruiguan.chache.Air.AirActivity;
import com.ruiguan.chache.Angle.AngleActivity;
import com.ruiguan.chache.Break.BreakActivity;
import com.ruiguan.chache.DAngle.DAngleActivity;
import com.ruiguan.chache.Force.ForceActivity;
import com.ruiguan.chache.HForce.HForceActivity;
import com.ruiguan.chache.Set.SetActivity;
import com.ruiguan.chache.Sound.SoundActivity;
import com.ruiguan.chache.Torque.TorqueActivity;
import com.ruiguan.entity.chacheInput;

public class ChacheActivity extends BaseActivity {
    public static chacheInput chache_Input;
    private Button FootForceBtn;
    private Button HandForceBtn;
    private Button AngleBtn;
    private Button TorqueBtn;
    private Button BreakBtn;
    private Button DAngleBtn;
    private Button SoundBtn;
    private Button AirBtn;
    private Button SetBtn;
    private Button BackBtn;
    private String str_chacheNumber;
    private String str_chacheType;
    private String str_chacheGroup;
    private EditText chacheNumber_txt;
    private Spinner chacheType_txt;
    private Spinner chacheGroup_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chache);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);

        View.OnClickListener bl = new ButtonListener();
        FootForceBtn = (Button) findViewById(R.id.FootForceBtn);
        HandForceBtn = (Button) findViewById(R.id.HandForceBtn);
        TorqueBtn = (Button) findViewById(R.id.TorqueBtn);
        BreakBtn = (Button) findViewById(R.id.BreakBtn);
        AngleBtn = (Button) findViewById(R.id.AngleBtn);
        DAngleBtn= (Button) findViewById(R.id.DAngleBtn);
        SoundBtn = (Button) findViewById(R.id.SoundBtn);
        AirBtn = (Button) findViewById(R.id.AirBtn);
        SetBtn = (Button) findViewById(R.id.SetBtn);
        BackBtn = (Button) findViewById(R.id.BackBtn);

        setOnClickListener(FootForceBtn, bl);
        setOnClickListener(HandForceBtn, bl);
        setOnClickListener(AngleBtn, bl);
        setOnClickListener(TorqueBtn, bl);
        setOnClickListener(BreakBtn, bl);
        setOnClickListener(DAngleBtn, bl);
        setOnClickListener(SoundBtn, bl);
        setOnClickListener(AirBtn, bl);
        setOnClickListener(SetBtn, bl);
        setOnClickListener(BackBtn, bl);

        AirBtn.setEnabled(false);
        chache_Input=new chacheInput();

        chacheNumber_txt=findViewById(R.id.chacheNumber_txt);
        chacheType_txt=findViewById(R.id.chacheType);
        chacheGroup_txt=findViewById(R.id.chacheGroup);

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
                case R.id.FootForceBtn: {
                    str_chacheNumber=chacheNumber_txt.getText().toString();
                    str_chacheType=chacheType_txt.getSelectedItem().toString();
                    str_chacheGroup=chacheGroup_txt.getSelectedItem().toString();
                    chache_Input.setchacheNumber(str_chacheNumber);
                    chache_Input.setchacheType(str_chacheType);
                    chache_Input.setchacheGroup(str_chacheGroup);

                    Intent intent = new Intent(ChacheActivity.this, ForceActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.HandForceBtn: {
                    str_chacheNumber=chacheNumber_txt.getText().toString();
                    str_chacheType=chacheType_txt.getSelectedItem().toString();
                    str_chacheGroup=chacheGroup_txt.getSelectedItem().toString();
                    chache_Input.setchacheNumber(str_chacheNumber);
                    chache_Input.setchacheType(str_chacheType);
                    chache_Input.setchacheGroup(str_chacheGroup);

                    Intent intent1 = new Intent(ChacheActivity.this, HForceActivity.class);
                    startActivity(intent1);
                    finish();
                }
                break;
                case R.id.AngleBtn: {
                    str_chacheNumber=chacheNumber_txt.getText().toString();
                    str_chacheType=chacheType_txt.getSelectedItem().toString();
                    str_chacheGroup=chacheGroup_txt.getSelectedItem().toString();
                    chache_Input.setchacheNumber(str_chacheNumber);
                    chache_Input.setchacheType(str_chacheType);
                    chache_Input.setchacheGroup(str_chacheGroup);

                    Intent intent2 = new Intent(ChacheActivity.this, AngleActivity.class);
                    startActivity(intent2);
                    finish();
                }
                break;
                case R.id.TorqueBtn: {
                    str_chacheNumber=chacheNumber_txt.getText().toString();
                    str_chacheType=chacheType_txt.getSelectedItem().toString();
                    str_chacheGroup=chacheGroup_txt.getSelectedItem().toString();
                    chache_Input.setchacheNumber(str_chacheNumber);
                    chache_Input.setchacheType(str_chacheType);
                    chache_Input.setchacheGroup(str_chacheGroup);

                    Intent intent3 = new Intent(ChacheActivity.this, TorqueActivity.class);
                    startActivity(intent3);
                    finish();
                }
                break;
                case R.id.BreakBtn: {
                    str_chacheNumber=chacheNumber_txt.getText().toString();
                    str_chacheType=chacheType_txt.getSelectedItem().toString();
                    str_chacheGroup=chacheGroup_txt.getSelectedItem().toString();
                    chache_Input.setchacheNumber(str_chacheNumber);
                    chache_Input.setchacheType(str_chacheType);
                    chache_Input.setchacheGroup(str_chacheGroup);

                    Intent intent4 = new Intent(ChacheActivity.this, BreakActivity.class);
                    startActivity(intent4);
                    finish();
                }
                break;
                case R.id.DAngleBtn: {
                    str_chacheNumber=chacheNumber_txt.getText().toString();
                    str_chacheType=chacheType_txt.getSelectedItem().toString();
                    str_chacheGroup=chacheGroup_txt.getSelectedItem().toString();
                    chache_Input.setchacheNumber(str_chacheNumber);
                    chache_Input.setchacheType(str_chacheType);
                    chache_Input.setchacheGroup(str_chacheGroup);

                    Intent intent5 = new Intent(ChacheActivity.this, DAngleActivity.class);
                    startActivity(intent5);
                    finish();
                }
                break;
                case R.id.SoundBtn: {
                    str_chacheNumber=chacheNumber_txt.getText().toString();
                    str_chacheType=chacheType_txt.getSelectedItem().toString();
                    str_chacheGroup=chacheGroup_txt.getSelectedItem().toString();
                    chache_Input.setchacheNumber(str_chacheNumber);
                    chache_Input.setchacheType(str_chacheType);
                    chache_Input.setchacheGroup(str_chacheGroup);

                    Intent intent6 = new Intent(ChacheActivity.this, SoundActivity.class);
                    startActivity(intent6);
                    finish();
                }
                break;
                case R.id.AirBtn: {
                    str_chacheNumber=chacheNumber_txt.getText().toString();
                    str_chacheType=chacheType_txt.getSelectedItem().toString();
                    str_chacheGroup=chacheGroup_txt.getSelectedItem().toString();
                    chache_Input.setchacheNumber(str_chacheNumber);
                    chache_Input.setchacheType(str_chacheType);
                    chache_Input.setchacheGroup(str_chacheGroup);

                    Intent intent7 = new Intent(ChacheActivity.this, AirActivity.class);
                    startActivity(intent7);
                    finish();
                }
                break;
                case R.id.SetBtn: {
                    Intent intent8 = new Intent(ChacheActivity.this, SetActivity.class);
                    startActivity(intent8);
                    finish();
                }
                break;
                case R.id.BackBtn: {
                    Intent intent9 = new Intent(ChacheActivity.this, MenuActivity.class);
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
