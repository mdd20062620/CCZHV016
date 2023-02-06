package com.ruiguan.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ruiguan.R;

public class RadioDialog_before implements View.OnClickListener {
    Dialog mRadioDialog;
    private TextView mBtnOk;
    private TextView mBtnCancel;
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
    public RadioDialog_before(Activity context, String prompt, String okName, String caName) {
        // 首先得到整个View
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_before, null);
        this.context=context;
        this.view=view;

        // 页面中显示文本
        TextView mPrompt =  view.findViewById(R.id.text_title);
        TextView mPrompt1 =  view.findViewById(R.id.text1);
        TextView mPrompt2 =  view.findViewById(R.id.text2);
        TextView mPrompt3 =  view.findViewById(R.id.text3);
        TextView mPrompt4 =  view.findViewById(R.id.text4);

        mBtnOk =  view.findViewById(R.id.button_ok);
        mBtnCancel =  view.findViewById(R.id.button_cancel);
        // 显示文本
        mPrompt.setText(prompt);
        mPrompt1.setText("⑴ 将被测汽车停放在平坦的硬质路面上，前轮呈直线行驶状态，四只轮子应保持相同的规定气压。");
        mPrompt2.setText("⑵ 将左右前轮置于转角仪上，使车轮中心与转角仪中心处于同一垂线上，后轮置于与转角仪等高的垫板上，使车辆保持水平状态。");
        mPrompt3.setText("⑶将测量仪挂架（轮辋夹具）固定在车轮的轮辋上（具体使用方法是：将挂架上下各2个卡爪挂在车轮辋上下的边缘，并调节中心螺杆手柄使之卡紧），将表头吸附在挂架中心的圆盘上，并保持表头水平状态。");
        mPrompt4.setText("(4)拔去转角仪定位销，在车辆自由状态时，将转角仪刻度调整到零位。" );

        mBtnOk.setText(okName);
        mBtnCancel.setText(caName);
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
            p.gravity = Gravity.CENTER;//设置位置
            p.alpha = 0.8f;//设置透明度
            dialogWindow.setAttributes(p);
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
                }
                break;
        }
    }
}

