package com.ruiguan.Sight.SAir;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class SAirActivity extends BaseActivity {
    private Button startSAirBtn;
    private Button stopSAirBtn;
    private Button scanSAirBtn;
    private Button printSAirBtn;
    private Button saveSAirBtn;
    private Button exportSAirBtn;
    private Button backSAirBtn;
    private Button exitSAirBtn;

    private Drawable startSAirBtnpressed;
    private Drawable stopSAirBtnpressed;
    private Drawable scanSAirBtnpressed;
    private Drawable printSAirBtnpressed;
    private Drawable saveSAirBtnpressed;
    private Drawable exportSAirBtnpressed;
    private Handler handler = new Handler();
    private LineChart chartSAir;
    private ArrayList<Float> realSAir_Data = new ArrayList<>();
    private SAirActivity.DynamicLineChartManager dynamicLineChartManager_SAir;
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sair);
        startSAirBtn = findViewById(R.id.startSAirBtn);
        stopSAirBtn = findViewById(R.id.stopSAirBtn);
        scanSAirBtn = findViewById(R.id.scanSAirBtn);
        printSAirBtn = findViewById(R.id.printSAirBtn);
        saveSAirBtn = findViewById(R.id.saveSAirBtn);
        exportSAirBtn = findViewById(R.id.exportSAirBtn);
        backSAirBtn = findViewById(R.id.backSAirBtn);
        exitSAirBtn = findViewById(R.id.exitSAirBtn);
        View.OnClickListener bl = new SAirActivity.ButtonListener();
        setOnClickListener(startSAirBtn, bl);
        setOnClickListener(stopSAirBtn, bl);
        setOnClickListener(scanSAirBtn, bl);
        setOnClickListener(printSAirBtn, bl);
        setOnClickListener(saveSAirBtn, bl);
        setOnClickListener(exportSAirBtn, bl);
        setOnClickListener(backSAirBtn, bl);
        setOnClickListener(exitSAirBtn, bl);

        startSAirBtn.setEnabled(true);
        startSAirBtnpressed = getResources().getDrawable(R.drawable.start1);
        startSAirBtnpressed.setBounds(0, 0, startSAirBtnpressed.getMinimumWidth(), startSAirBtnpressed.getMinimumHeight());
        startSAirBtn.setCompoundDrawables(null, startSAirBtnpressed, null, null);

        stopSAirBtn.setEnabled(true);
        stopSAirBtnpressed = getResources().getDrawable(R.drawable.stop1);
        stopSAirBtnpressed.setBounds(0, 0, stopSAirBtnpressed.getMinimumWidth(), stopSAirBtnpressed.getMinimumHeight());
        stopSAirBtn.setCompoundDrawables(null, stopSAirBtnpressed, null, null);

        scanSAirBtn.setEnabled(true);
        scanSAirBtnpressed = getResources().getDrawable(R.drawable.scan1);
        scanSAirBtnpressed.setBounds(0, 0, scanSAirBtnpressed.getMinimumWidth(), scanSAirBtnpressed.getMinimumHeight());
        scanSAirBtn.setCompoundDrawables(null, scanSAirBtnpressed, null, null);

        printSAirBtn.setEnabled(true);
        printSAirBtnpressed = getResources().getDrawable(R.drawable.print1);
        printSAirBtnpressed.setBounds(0, 0, printSAirBtnpressed.getMinimumWidth(), printSAirBtnpressed.getMinimumHeight());
        printSAirBtn.setCompoundDrawables(null, printSAirBtnpressed, null, null);

        saveSAirBtn.setEnabled(true);
        saveSAirBtnpressed = getResources().getDrawable(R.drawable.save1);
        saveSAirBtnpressed.setBounds(0, 0, saveSAirBtnpressed.getMinimumWidth(), saveSAirBtnpressed.getMinimumHeight());
        saveSAirBtn.setCompoundDrawables(null, saveSAirBtnpressed, null, null);

        exportSAirBtn.setEnabled(true);
        exportSAirBtnpressed = getResources().getDrawable(R.drawable.export1);
        exportSAirBtnpressed.setBounds(0, 0, exportSAirBtnpressed.getMinimumWidth(), exportSAirBtnpressed.getMinimumHeight());
        exportSAirBtn.setCompoundDrawables(null, exportSAirBtnpressed, null, null);
        chartSAir =findViewById(R.id.chartSAir);

        names.add ("");
        colour.add (Color.argb (255, 255, 125, 0));            //定义Fre颜色
        dynamicLineChartManager_SAir = new SAirActivity.DynamicLineChartManager(chartSAir, names.get (0), colour.get (0), 0);
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
                case R.id.startSAirBtn: {
                    startSAirBtn.setEnabled(false);
                    startSAirBtnpressed = getResources().getDrawable(R.drawable.start);
                    startSAirBtnpressed.setBounds(0, 0, startSAirBtnpressed.getMinimumWidth(), startSAirBtnpressed.getMinimumHeight());
                    startSAirBtn.setCompoundDrawables(null, startSAirBtnpressed, null, null);
                }
                break;
                case R.id.stopSAirBtn: {
                    stopSAirBtn.setEnabled(false);
                    stopSAirBtnpressed = getResources().getDrawable(R.drawable.stop);
                    stopSAirBtnpressed.setBounds(0, 0, stopSAirBtnpressed.getMinimumWidth(), stopSAirBtnpressed.getMinimumHeight());
                    stopSAirBtn.setCompoundDrawables(null, stopSAirBtnpressed, null, null);
                }
                break;
                case R.id.scanSAirBtn: {

                }
                break;
                case R.id.printSAirBtn: {
                    printSAirBtn.setEnabled(false);
                    printSAirBtnpressed = getResources().getDrawable(R.drawable.print);
                    printSAirBtnpressed.setBounds(0, 0, printSAirBtnpressed.getMinimumWidth(), printSAirBtnpressed.getMinimumHeight());
                    printSAirBtn.setCompoundDrawables(null, printSAirBtnpressed, null, null);
                }
                break;
                case R.id.saveSAirBtn: {
                    saveSAirBtn.setEnabled(false);
                    saveSAirBtnpressed = getResources().getDrawable(R.drawable.save);
                    saveSAirBtnpressed.setBounds(0, 0, saveSAirBtnpressed.getMinimumWidth(), saveSAirBtnpressed.getMinimumHeight());
                    saveSAirBtn.setCompoundDrawables(null, saveSAirBtnpressed, null, null);
                }
                break;
                case R.id.exportSAirBtn: {
                    exportSAirBtn.setEnabled(false);
                    exportSAirBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportSAirBtnpressed.setBounds(0, 0, exportSAirBtnpressed.getMinimumWidth(), exportSAirBtnpressed.getMinimumHeight());
                    exportSAirBtn.setCompoundDrawables(null, exportSAirBtnpressed, null, null);
                    Toast.makeText(SAirActivity.this, "数据已导出到手机根目录", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.backSAirBtn: {
                    Intent intent1 = new Intent(SAirActivity.this, SightActivity.class);
                    startActivity(intent1);
                    finish();
                }
                break;
                case R.id.exitSAirBtn: {
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
            startSAirBtn.setEnabled(true);
            startSAirBtnpressed = getResources().getDrawable(R.drawable.start1);
            startSAirBtnpressed.setBounds(0, 0, startSAirBtnpressed.getMinimumWidth(), startSAirBtnpressed.getMinimumHeight());
            startSAirBtn.setCompoundDrawables(null, startSAirBtnpressed, null, null);

            stopSAirBtn.setEnabled(true);
            stopSAirBtnpressed = getResources().getDrawable(R.drawable.stop1);
            stopSAirBtnpressed.setBounds(0, 0, stopSAirBtnpressed.getMinimumWidth(), stopSAirBtnpressed.getMinimumHeight());
            stopSAirBtn.setCompoundDrawables(null, stopSAirBtnpressed, null, null);

            scanSAirBtn.setEnabled(true);
            scanSAirBtnpressed = getResources().getDrawable(R.drawable.scan1);
            scanSAirBtnpressed.setBounds(0, 0, scanSAirBtnpressed.getMinimumWidth(), scanSAirBtnpressed.getMinimumHeight());
            scanSAirBtn.setCompoundDrawables(null, scanSAirBtnpressed, null, null);

            printSAirBtn.setEnabled(true);
            printSAirBtnpressed = getResources().getDrawable(R.drawable.print1);
            printSAirBtnpressed.setBounds(0, 0, printSAirBtnpressed.getMinimumWidth(), printSAirBtnpressed.getMinimumHeight());
            printSAirBtn.setCompoundDrawables(null, printSAirBtnpressed, null, null);

            saveSAirBtn.setEnabled(true);
            saveSAirBtnpressed = getResources().getDrawable(R.drawable.save1);
            saveSAirBtnpressed.setBounds(0, 0, saveSAirBtnpressed.getMinimumWidth(), saveSAirBtnpressed.getMinimumHeight());
            saveSAirBtn.setCompoundDrawables(null, saveSAirBtnpressed, null, null);

            exportSAirBtn.setEnabled(true);
            exportSAirBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportSAirBtnpressed.setBounds(0, 0, exportSAirBtnpressed.getMinimumWidth(), exportSAirBtnpressed.getMinimumHeight());
            exportSAirBtn.setCompoundDrawables(null, exportSAirBtnpressed, null, null);
        }
    };
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
            chartSAir.setVisibility(View.VISIBLE);
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
            chartSAir.setVisibility (View.VISIBLE);
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
            chartSAir.notifyDataSetChanged();
            chartSAir.moveViewToX(0.00f);
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
        dynamicLineChartManager_SAir.setData(realSAir_Data,0.1f);
        dynamicLineChartManager_SAir.setYAxis(5000, 0, 5);
        dynamicLineChartManager_SAir.setXAxis(120, 0, 10,0);
        dynamicLineChartManager_SAir.setHightLimitLine(0f, "");
        dynamicLineChartManager_SAir.desChart(names.get (0),165);
    }
}
