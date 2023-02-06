package com.ruiguan.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ruiguan.R;
import com.ruiguan.base.BaseActivity;

public class RadioDialog_ZN  extends BaseActivity implements View.OnClickListener {
    Dialog mRadioDialog;
    private TextView mBtnOk;
    private TextView mBtnCancel;
    private Button mBtnOk1;
    private Button mBtnCancel1;
    private TextView mPrompt;
    private TextView mPrompt1;
    private TextView mPrompt2;
    private TextView mPrompt3;
    private TextView mPrompt4 ;
    private View.OnClickListener okOnClickListener;
    private View.OnClickListener cancelOnClickListener;
    private Activity context;
    private View view;

    public void setOkOnClickListener(View.OnClickListener okOnClickListener) {
        this.okOnClickListener = okOnClickListener;
    }
    public void setCancelOnClickListener(View.OnClickListener cancelOnClickListener) {
        this.cancelOnClickListener = cancelOnClickListener;
    }
    public RadioDialog_ZN(Activity context, String prompt, String okName, String caName) {
        // 首先得到整个View
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_zn, null);
        this.context=context;
        this.view=view;

        // 页面中显示文本
        mPrompt =  view.findViewById(R.id.text_title);
        mPrompt1 =  view.findViewById(R.id.text1);
        mPrompt2 =  view.findViewById(R.id.text2);
        mPrompt3 =  view.findViewById(R.id.text3);
        mPrompt4 =  view.findViewById(R.id.text4);

        mBtnOk =  view.findViewById(R.id.button_ok);
        mBtnCancel =  view.findViewById(R.id.button_cancel);
        mBtnOk1 =  view.findViewById(R.id.button_ok1);
        mBtnCancel1 =  view.findViewById(R.id.button_cancel1);
        View.OnClickListener bl = new RadioDialog_ZN.ButtonListener();
        setOnClickListener(mBtnOk1, bl);
        setOnClickListener(mBtnCancel1, bl);

        // 显示文本
        mPrompt.setText(prompt);
        mPrompt1.setText("概述：从车前后方向看轮胎时，主销轴向车身内侧倾斜，该角度称为主销内倾角。" +
                "主销内倾角还使得主销轴线与路面交点到车轮中心平面与地面交线的距离减小，从而减小转向时驾驶员加在方向盘上的力，" +
                "使转向操纵轻便，同时也可减少从转向轮传到方向盘上的冲击力。但主销内倾角也不宜过大，否则加速了轮胎的磨损");
        mPrompt2.setText("测量方法：请按照步骤操作，执行步骤（1）!");
        mPrompt3.setText("（1）使转角仪刻度置于零位，前轮对准正前方，踩下制动踏板，将前轮向外侧转20°，后点击“确认”；");
        mPrompt4.setText("（2）再将前轮向内侧转20°后点击“确认”。" );

        mBtnOk.setText(okName);
        mBtnCancel.setText(caName);
        mBtnOk1.setText(okName);
        mBtnCancel1.setText(caName);
        mBtnOk.setEnabled(false);
        mBtnCancel.setEnabled(false);
        mBtnOk.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        // 创建自定义样式的Dialog
        mRadioDialog = new Dialog(context, R.style.loading_dialog);
        //int w = context.getWindowManager().getDefaultDisplay().getWidth();
        //view.setMinimumWidth(w);//设置dialog的宽度
        // 设置返回键无效
        mRadioDialog.setCanceledOnTouchOutside (false);
        mRadioDialog.setContentView(view, new ActionBar.LayoutParams(//设置dialog
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Toast.makeText(context, "请按步骤操作！", Toast.LENGTH_SHORT).show();
    }

    public void show() {
        if (!mRadioDialog.isShowing()){
            mRadioDialog.show();
            Window dialogWindow = mRadioDialog.getWindow();
            WindowManager m = context.getWindowManager();
            Display d = m.getDefaultDisplay();
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            // 设置高度和宽度
            p.height = (int) (d.getHeight()*0.45 ); // 高度设置为屏幕的0.6
            p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
            p.gravity = Gravity.TOP;//设置位置
            p.alpha = 0.8f;//设置透明度
            dialogWindow.setAttributes(p);
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
                case R.id.button_ok1: {
                    mPrompt3.setTextColor(Color.rgb(0, 191, 255));
                    mPrompt2.setText("测量方法：请按照步骤操作，执行步骤（2）!");
                    mBtnOk1.setTextColor(Color.rgb(164, 164, 164));
                    mBtnCancel1.setTextColor(Color.rgb(164, 164, 164));
                    mBtnOk.setTextColor(Color.rgb(0, 191, 255));
                    mBtnCancel.setTextColor(Color.rgb(0, 191, 255));
                    mBtnOk.setEnabled(true);
                    mBtnCancel.setEnabled(true);
                    //Toast.makeText(context, "请进行步（2）骤操作！", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.button_cancel1: {
                    mBtnOk.setEnabled(false);
                    mBtnCancel.setEnabled(false);
                    mBtnOk1.setTextColor(Color.rgb(164, 164, 164));
                    mBtnCancel1.setTextColor(Color.rgb(0, 191, 255));
                    //Toast.makeText(context, "请进行步（1）骤操作！", Toast.LENGTH_SHORT).show();
                }
                break;

                default: {
                }
                break;
            }
        }
    }

    public void close() {
        if (mRadioDialog != null) {
            mRadioDialog.dismiss();
            mRadioDialog = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_cancel:
                if (cancelOnClickListener != null) {
                    cancelOnClickListener.onClick(view);
                }
                break;
            default:
                if (okOnClickListener != null) {
                    okOnClickListener.onClick(view);
                    mPrompt4.setTextColor(Color.rgb(0, 191, 255));
                }
                break;
        }
    }
}

