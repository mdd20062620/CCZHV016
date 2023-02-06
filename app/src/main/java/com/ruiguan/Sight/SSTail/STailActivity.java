package com.ruiguan.Sight.SSTail;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.ruiguan.R;
import com.ruiguan.Sight.SightActivity;
import com.ruiguan.activities.ActivityCollector;
import com.ruiguan.base.BaseActivity;
import com.ruiguan.view.LineChartMarkView;

import java.util.ArrayList;
import java.util.List;

public class STailActivity extends BaseActivity {
    private Button tailStartBtn;
    private Button tailStopBtn;
    private Button tailScanBtn;
    private Button tailPrintBtn;
    private Button tailSaveBtn;
    private Button tailExportBtn;
    private Button tailBackBtn;
    private Button tailExitBtn;

    private Drawable tailStartBtnpressed;
    private Drawable tailStopBtnpressed;
    private Drawable tailScanBtnpressed;
    private Drawable tailPrintBtnpressed;
    private Drawable tailSaveBtnpressed;
    private Drawable tailExportBtnpressed;

    private Handler handler = new Handler();
    private LineChart chartSTail;
    private ArrayList<Float> realSTail_Data = new ArrayList<>();
    private STailActivity.DynamicLineChartManager dynamicLineChartManager_STail;
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stail);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActivityCollector.addActivity(this);
        initMembers();
    }
    private void initMembers() {
        tailStartBtn = findViewById(R.id.tailStartBtn);
        tailStopBtn = findViewById(R.id.tailStopBtn);
        tailScanBtn = findViewById(R.id.tailScanBtn);
        tailPrintBtn = findViewById(R.id.tailPrintBtn);
        tailSaveBtn = findViewById(R.id.tailSaveBtn);
        tailExportBtn = findViewById(R.id.tailExportBtn);
        tailBackBtn = findViewById(R.id.tailBackBtn);
        tailExitBtn = findViewById(R.id.tailExitBtn);
        View.OnClickListener bl = new STailActivity.ButtonListener();
        setOnClickListener(tailStartBtn, bl);
        setOnClickListener(tailStopBtn, bl);
        setOnClickListener(tailScanBtn, bl);
        setOnClickListener(tailPrintBtn, bl);
        setOnClickListener(tailSaveBtn, bl);
        setOnClickListener(tailExportBtn, bl);
        setOnClickListener(tailBackBtn, bl);
        setOnClickListener(tailExitBtn, bl);

        tailStartBtn.setEnabled(true);
        tailStartBtnpressed = getResources().getDrawable(R.drawable.start1);
        tailStartBtnpressed.setBounds(0, 0, tailStartBtnpressed.getMinimumWidth(), tailStartBtnpressed.getMinimumHeight());
        tailStartBtn.setCompoundDrawables(null, tailStartBtnpressed, null, null);

        tailStopBtn.setEnabled(true);
        tailStopBtnpressed = getResources().getDrawable(R.drawable.stop1);
        tailStopBtnpressed.setBounds(0, 0, tailStopBtnpressed.getMinimumWidth(), tailStopBtnpressed.getMinimumHeight());
        tailStopBtn.setCompoundDrawables(null, tailStopBtnpressed, null, null);

        tailScanBtn.setEnabled(true);
        tailScanBtnpressed = getResources().getDrawable(R.drawable.scan1);
        tailScanBtnpressed.setBounds(0, 0, tailScanBtnpressed.getMinimumWidth(), tailScanBtnpressed.getMinimumHeight());
        tailScanBtn.setCompoundDrawables(null, tailScanBtnpressed, null, null);

        tailPrintBtn.setEnabled(true);
        tailPrintBtnpressed = getResources().getDrawable(R.drawable.print1);
        tailPrintBtnpressed.setBounds(0, 0, tailPrintBtnpressed.getMinimumWidth(), tailPrintBtnpressed.getMinimumHeight());
        tailPrintBtn.setCompoundDrawables(null, tailPrintBtnpressed, null, null);

        tailSaveBtn.setEnabled(true);
        tailSaveBtnpressed = getResources().getDrawable(R.drawable.save1);
        tailSaveBtnpressed.setBounds(0, 0, tailSaveBtnpressed.getMinimumWidth(), tailSaveBtnpressed.getMinimumHeight());
        tailSaveBtn.setCompoundDrawables(null, tailSaveBtnpressed, null, null);

        tailExportBtn.setEnabled(true);
        tailExportBtnpressed = getResources().getDrawable(R.drawable.export1);
        tailExportBtnpressed.setBounds(0, 0, tailExportBtnpressed.getMinimumWidth(), tailExportBtnpressed.getMinimumHeight());
        tailExportBtn.setCompoundDrawables(null, tailExportBtnpressed, null, null);

        chartSTail =findViewById(R.id.tailChart);
        names.add ("");
        colour.add (Color.argb (255, 255, 125, 0));            //定义Fre颜色
        dynamicLineChartManager_STail = new STailActivity.DynamicLineChartManager(chartSTail, names.get (0), colour.get (0), 0);
        ShowWave();
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
                case R.id.tailStartBtn: {
                    tailStartBtn.setEnabled(true);
                    tailStartBtnpressed = getResources().getDrawable(R.drawable.start);
                    tailStartBtnpressed.setBounds(0, 0, tailStartBtnpressed.getMinimumWidth(), tailStartBtnpressed.getMinimumHeight());
                    tailStartBtn.setCompoundDrawables(null, tailStartBtnpressed, null, null);
                }
                break;
                case R.id.tailStopBtn: {
                    tailStopBtn.setEnabled(true);
                    tailStopBtnpressed = getResources().getDrawable(R.drawable.stop);
                    tailStopBtnpressed.setBounds(0, 0, tailStopBtnpressed.getMinimumWidth(), tailStopBtnpressed.getMinimumHeight());
                    tailStopBtn.setCompoundDrawables(null, tailStopBtnpressed, null, null);
                }
                break;
                case R.id.tailScanBtn: {
                    tailScanBtn.setEnabled(true);
                    tailScanBtnpressed = getResources().getDrawable(R.drawable.scan);
                    tailScanBtnpressed.setBounds(0, 0, tailScanBtnpressed.getMinimumWidth(), tailScanBtnpressed.getMinimumHeight());
                    tailScanBtn.setCompoundDrawables(null, tailScanBtnpressed, null, null);
                }
                break;
                case R.id.tailPrintBtn: {
                    tailPrintBtn.setEnabled(true);
                    tailPrintBtnpressed = getResources().getDrawable(R.drawable.print);
                    tailPrintBtnpressed.setBounds(0, 0, tailPrintBtnpressed.getMinimumWidth(), tailPrintBtnpressed.getMinimumHeight());
                    tailPrintBtn.setCompoundDrawables(null, tailPrintBtnpressed, null, null);
                }
                break;
                case R.id.tailSaveBtn : {
                    tailSaveBtn.setEnabled(true);
                    tailSaveBtnpressed = getResources().getDrawable(R.drawable.save);
                    tailSaveBtnpressed.setBounds(0, 0, tailSaveBtnpressed.getMinimumWidth(), tailSaveBtnpressed.getMinimumHeight());
                    tailSaveBtn.setCompoundDrawables(null, tailSaveBtnpressed, null, null);
                }
                break;
                case R.id.tailExportBtn: {
                    tailExportBtn.setEnabled(true);
                    tailExportBtnpressed = getResources().getDrawable(R.drawable.export);
                    tailExportBtnpressed.setBounds(0, 0, tailExportBtnpressed.getMinimumWidth(), tailExportBtnpressed.getMinimumHeight());
                    tailExportBtn.setCompoundDrawables(null, tailExportBtnpressed, null, null);
                }
                break;
                case R.id.tailBackBtn: {
                    Intent intent1 = new Intent(STailActivity.this, SightActivity.class);
                    startActivity(intent1);
                    finish();
                }
                break;
                case R.id.tailExitBtn: {
                    finish();
                    ActivityCollector.finishAll();
                }
                break;
                default: {
                }
                break;
            }
            handler.postDelayed(sendRunnable, 1000);
        }
    }
    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            tailStartBtn.setEnabled(true);
            tailStartBtnpressed = getResources().getDrawable(R.drawable.start1);
            tailStartBtnpressed.setBounds(0, 0, tailStartBtnpressed.getMinimumWidth(), tailStartBtnpressed.getMinimumHeight());
            tailStartBtn.setCompoundDrawables(null, tailStartBtnpressed, null, null);

            tailStopBtn.setEnabled(true);
            tailStopBtnpressed = getResources().getDrawable(R.drawable.stop1);
            tailStopBtnpressed.setBounds(0, 0, tailStopBtnpressed.getMinimumWidth(), tailStopBtnpressed.getMinimumHeight());
            tailStopBtn.setCompoundDrawables(null, tailStopBtnpressed, null, null);

            tailScanBtn.setEnabled(true);
            tailScanBtnpressed = getResources().getDrawable(R.drawable.scan1);
            tailScanBtnpressed.setBounds(0, 0, tailScanBtnpressed.getMinimumWidth(), tailScanBtnpressed.getMinimumHeight());
            tailScanBtn.setCompoundDrawables(null, tailScanBtnpressed, null, null);

            tailPrintBtn.setEnabled(true);
            tailPrintBtnpressed = getResources().getDrawable(R.drawable.print1);
            tailPrintBtnpressed.setBounds(0, 0, tailPrintBtnpressed.getMinimumWidth(), tailPrintBtnpressed.getMinimumHeight());
            tailPrintBtn.setCompoundDrawables(null, tailPrintBtnpressed, null, null);

            tailSaveBtn.setEnabled(true);
            tailSaveBtnpressed = getResources().getDrawable(R.drawable.save1);
            tailSaveBtnpressed.setBounds(0, 0, tailSaveBtnpressed.getMinimumWidth(), tailSaveBtnpressed.getMinimumHeight());
            tailSaveBtn.setCompoundDrawables(null, tailSaveBtnpressed, null, null);

            tailExportBtn.setEnabled(true);
            tailExportBtnpressed = getResources().getDrawable(R.drawable.export1);
            tailExportBtnpressed.setBounds(0, 0, tailExportBtnpressed.getMinimumWidth(), tailExportBtnpressed.getMinimumHeight());
            tailExportBtn.setCompoundDrawables(null, tailExportBtnpressed, null, null);
        }
    };
    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        super.onDestroy();
    }
    public class DynamicLineChartManager implements OnChartGestureListener {
        private LineChart lineChart;
        private YAxis leftAxis;
        private YAxis rightAxis;
        private XAxis xAxis;
        private int position;

        private LineData lineData;
        private LineDataSet lineDataSet;
        private LineChartMarkView mv;
        private Legend legend;
        private void setData(ArrayList<Float> value,float index) {
            ArrayList<Entry> values = new ArrayList<>();
            for (int i = 0; i < value.size()- 1; i++) {
                values.add(new Entry((float)(i*index), (float) value.get(i)));
            }
            lineDataSet.setValues(values);
            lineChart.setData(lineData);
            lineChart.invalidate();
        }

        @Override
        public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        }

        @Override
        public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        }

        @Override
        public void onChartDoubleTapped(MotionEvent me) {
            chartSTail.setVisibility(View.VISIBLE);
        }

        @Override
        public void onChartSingleTapped(MotionEvent me) {
        }

        @Override
        public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        }

        @Override
        public void onChartTranslate(MotionEvent me, float dX, float dY) {
        }

        @Override
        public void onChartLongPressed(MotionEvent me) {
            chartSTail.setVisibility (View.VISIBLE);
        }

        @Override
        public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        }

        //一条曲线
        public DynamicLineChartManager(LineChart mLineChart, String name, int color, int position) {
            this.lineChart = mLineChart;
            this.position = position;
            //滑动缩放相关
            lineChart.setOnChartGestureListener(this);
            //数据样式
            lineDataSet = new LineDataSet(null, "实时受力值(N)");
            lineDataSet.setLineWidth(1.0f);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setColor(Color.BLUE);
            lineDataSet.setHighLightColor(Color.BLACK);
            //设置曲线填充
            lineDataSet.setDrawFilled(false);
            lineDataSet.setDrawCircleHole(false);
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setDrawValues(false);
            lineDataSet.setMode(LineDataSet.Mode.LINEAR);

            lineData = new LineData(lineDataSet);
            lineChart.setData(lineData);
            lineChart.animateX(10);
            lineChart.invalidate();
        }
        private void desChart(String name, int y) {
            //图标样式
            com.github.mikephil.charting.components.Description description = new Description();
            description.setText(name);
            description.setPosition(650,y);
            lineChart.setDescription(description);
            lineChart.setDrawGridBackground(false);
            lineChart.setDrawBorders(false);
            lineChart.invalidate();
        }

        private void addEntry(float force){
            Entry entry = new Entry(lineDataSet.getEntryCount()*0.01f,force);
            lineData.addEntry(entry, 0);
            chartSTail.notifyDataSetChanged();
            chartSTail.moveViewToX(0.00f);
            lineChart.invalidate();
        }
        private void freshChart(float force) {
            lineChart.fitScreen();
        }
        private void clear() {
            lineDataSet.clear();
            lineChart.invalidate();
        }

        public void setYAxis(float max, float min, int labelCount) {
            if (max < min) {
                return;
            }
            leftAxis = lineChart.getAxisLeft();
            rightAxis = lineChart.getAxisRight();
            rightAxis.setEnabled(false);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setGranularityEnabled(false);
            leftAxis.setDrawGridLines(false);
            leftAxis.setAxisMaximum(max);
            leftAxis.setAxisMinimum(min);
            leftAxis.setDrawLimitLinesBehindData(true);
            leftAxis.setLabelCount(labelCount, false);
            rightAxis.setEnabled(false);

            //图例
            legend = lineChart.getLegend();
            legend.setForm(Legend.LegendForm.LINE);
            legend.setTextSize(10f);
            legend.setDrawInside(true);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            lineChart.invalidate();
        }

        public void setLegend(){
            Legend LegFre = lineChart.getLegend();
            LegFre.setForm(Legend.LegendForm.LINE);
            LegFre.setTextSize(10f);
            LegFre.setTextColor(Color.BLUE);
            LegFre.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
            LegFre.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            LegFre.setOrientation(Legend.LegendOrientation.VERTICAL);
            LegFre.setDrawInside(false);
        }

        public void setXAxis(float max, float min, int labelCount,int pos) {
            if (max < min) {
                return;
            }
            xAxis = lineChart.getXAxis();
            if(pos==0){
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            }else{
                xAxis.setPosition(XAxis.XAxisPosition.TOP);
            }

            xAxis.setDrawLabels(true);
            xAxis.setDrawGridLines(false);
            xAxis.setAxisMaximum(max);
            xAxis.setAxisMinimum(min);
            xAxis.setLabelCount(labelCount, false);
            lineChart.invalidate();
        }

        /**
         * 设置高限制线
         *
         * @param high
         * @param name
         */
        public void setHightLimitLine(float high, String name) {
            if (name == null) {
                name = "高限制线";
            }
            LimitLine hightLimit = new LimitLine(high, name);
            hightLimit.setLineWidth(0.1f);
            hightLimit.setTextSize(10f);
            hightLimit.enableDashedLine(8.0f, 4.0f, 4.0f);
            leftAxis.removeAllLimitLines(); //先清除原来的线，后面再加上，防止add方法重复绘制
            leftAxis.addLimitLine(hightLimit);
            hightLimit.setLineColor(Color.BLACK);
            lineChart.invalidate();
        }


        public void setLowLimitLine(float low, String name) {
            if (name == null) {
                name = "低限制线";
            }
            LimitLine hightLimit = new LimitLine(low, name);
            hightLimit.setLineWidth(0.1f);
            hightLimit.setTextSize(10f);
            hightLimit.setLineColor(Color.BLACK);
            hightLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            leftAxis.removeAllLimitLines(); //先清除原来的线，后面再加上，防止add方法重复绘制
            leftAxis.addLimitLine(hightLimit);
            hightLimit.enableDashedLine(8.0f, 4.0f, 4.0f);
            lineChart.invalidate();
        }

        public void setXHightLimitLine(float high, String name) {
            LimitLine hightLimit = new LimitLine(high, name);
            hightLimit.setLineWidth(0.1f);
            hightLimit.setTextSize(10f);
            hightLimit.enableDashedLine(8.0f, 4.0f, 4.0f);

            xAxis.addLimitLine(hightLimit);
            hightLimit.setLineColor(Color.BLACK);
            lineChart.invalidate();
        }

        public void ClearXHightLimitLine(float high, String name) {
            LimitLine hightLimit = new LimitLine(high, name);
            hightLimit.setLineWidth(0.1f);
            hightLimit.setTextSize(10f);
            hightLimit.enableDashedLine(8.0f, 4.0f, 4.0f);
            xAxis.addLimitLine(hightLimit);
            xAxis.removeAllLimitLines(); //先清除原来的线，后面再加上，防止add方法重复绘制
            hightLimit.setLineColor(Color.BLACK);
            lineChart.invalidate();
        }
    }

    public void ShowWave() {
        dynamicLineChartManager_STail.setData(realSTail_Data,0.1f);
        dynamicLineChartManager_STail.setYAxis(360, 0, 10);
        dynamicLineChartManager_STail.setXAxis(120, 0, 10,0);
        dynamicLineChartManager_STail.setHightLimitLine(0f, "");
        dynamicLineChartManager_STail.desChart(names.get (0),165);
    }

}
