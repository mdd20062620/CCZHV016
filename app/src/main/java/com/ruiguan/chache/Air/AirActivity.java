package com.ruiguan.chache.Air;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.ruiguan.activities.ActivityCollector;
import com.ruiguan.base.BaseActivity;
import com.ruiguan.chache.ChacheActivity;
import com.ruiguan.view.LineChartMarkView;

import java.util.ArrayList;
import java.util.List;

public class AirActivity extends BaseActivity {
    private Button startAirBtn;
    private Button stopAirBtn;
    private Button scanAirBtn;
    private Button printAirBtn;
    private Button saveAirBtn;
    private Button exportAirBtn;
    private Button backAirBtn;
    private Button exitAirBtn;

    private Drawable startAirBtnpressed;
    private Drawable stopAirBtnpressed;
    private Drawable scanAirBtnpressed;
    private Drawable printAirBtnpressed;
    private Drawable saveAirBtnpressed;
    private Drawable exportAirBtnpressed;
    private Handler handler = new Handler();
    private LineChart chartAir;
    private ArrayList<Float> realAir_Data = new ArrayList<>();
    private AirActivity.DynamicLineChartManager dynamicLineChartManager_Air;
    private List<String> names = new ArrayList<>(); //??????????????????
    private List<Integer> colour = new ArrayList<>();//??????????????????
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air);
        startAirBtn = findViewById(R.id.startAirBtn);
        stopAirBtn = findViewById(R.id.stopAirBtn);
        scanAirBtn = findViewById(R.id.scanAirBtn);
        printAirBtn = findViewById(R.id.printAirBtn);
        saveAirBtn = findViewById(R.id.saveAirBtn);
        exportAirBtn = findViewById(R.id.exportAirBtn);
        backAirBtn = findViewById(R.id.backAirBtn);
        exitAirBtn = findViewById(R.id.exitAirBtn);
        View.OnClickListener bl = new AirActivity.ButtonListener();
        setOnClickListener(startAirBtn, bl);
        setOnClickListener(stopAirBtn, bl);
        setOnClickListener(scanAirBtn, bl);
        setOnClickListener(printAirBtn, bl);
        setOnClickListener(saveAirBtn, bl);
        setOnClickListener(exportAirBtn, bl);
        setOnClickListener(backAirBtn, bl);
        setOnClickListener(exitAirBtn, bl);

        startAirBtn.setEnabled(true);
        startAirBtnpressed = getResources().getDrawable(R.drawable.start1);
        startAirBtnpressed.setBounds(0, 0, startAirBtnpressed.getMinimumWidth(), startAirBtnpressed.getMinimumHeight());
        startAirBtn.setCompoundDrawables(null, startAirBtnpressed, null, null);

        stopAirBtn.setEnabled(true);
        stopAirBtnpressed = getResources().getDrawable(R.drawable.stop1);
        stopAirBtnpressed.setBounds(0, 0, stopAirBtnpressed.getMinimumWidth(), stopAirBtnpressed.getMinimumHeight());
        stopAirBtn.setCompoundDrawables(null, stopAirBtnpressed, null, null);

        scanAirBtn.setEnabled(true);
        scanAirBtnpressed = getResources().getDrawable(R.drawable.scan1);
        scanAirBtnpressed.setBounds(0, 0, scanAirBtnpressed.getMinimumWidth(), scanAirBtnpressed.getMinimumHeight());
        scanAirBtn.setCompoundDrawables(null, scanAirBtnpressed, null, null);

        printAirBtn.setEnabled(true);
        printAirBtnpressed = getResources().getDrawable(R.drawable.print1);
        printAirBtnpressed.setBounds(0, 0, printAirBtnpressed.getMinimumWidth(), printAirBtnpressed.getMinimumHeight());
        printAirBtn.setCompoundDrawables(null, printAirBtnpressed, null, null);

        saveAirBtn.setEnabled(true);
        saveAirBtnpressed = getResources().getDrawable(R.drawable.save1);
        saveAirBtnpressed.setBounds(0, 0, saveAirBtnpressed.getMinimumWidth(), saveAirBtnpressed.getMinimumHeight());
        saveAirBtn.setCompoundDrawables(null, saveAirBtnpressed, null, null);

        exportAirBtn.setEnabled(true);
        exportAirBtnpressed = getResources().getDrawable(R.drawable.export1);
        exportAirBtnpressed.setBounds(0, 0, exportAirBtnpressed.getMinimumWidth(), exportAirBtnpressed.getMinimumHeight());
        exportAirBtn.setCompoundDrawables(null, exportAirBtnpressed, null, null);
        chartAir =findViewById(R.id.chartAir);

        names.add ("");
        colour.add (Color.argb (255, 255, 125, 0));            //??????Fre??????
        dynamicLineChartManager_Air = new AirActivity.DynamicLineChartManager(chartAir, names.get (0), colour.get (0), 0);
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
                case R.id.startAirBtn: {
                    startAirBtn.setEnabled(false);
                    startAirBtnpressed = getResources().getDrawable(R.drawable.start);
                    startAirBtnpressed.setBounds(0, 0, startAirBtnpressed.getMinimumWidth(), startAirBtnpressed.getMinimumHeight());
                    startAirBtn.setCompoundDrawables(null, startAirBtnpressed, null, null);
                }
                break;
                case R.id.stopAirBtn: {
                    stopAirBtn.setEnabled(false);
                    stopAirBtnpressed = getResources().getDrawable(R.drawable.stop);
                    stopAirBtnpressed.setBounds(0, 0, stopAirBtnpressed.getMinimumWidth(), stopAirBtnpressed.getMinimumHeight());
                    stopAirBtn.setCompoundDrawables(null, stopAirBtnpressed, null, null);
                }
                break;
                case R.id.scanAirBtn: {

                }
                break;
                case R.id.printAirBtn: {
                    printAirBtn.setEnabled(false);
                    printAirBtnpressed = getResources().getDrawable(R.drawable.print);
                    printAirBtnpressed.setBounds(0, 0, printAirBtnpressed.getMinimumWidth(), printAirBtnpressed.getMinimumHeight());
                    printAirBtn.setCompoundDrawables(null, printAirBtnpressed, null, null);
                }
                break;
                case R.id.saveAirBtn: {
                    saveAirBtn.setEnabled(false);
                    saveAirBtnpressed = getResources().getDrawable(R.drawable.save);
                    saveAirBtnpressed.setBounds(0, 0, saveAirBtnpressed.getMinimumWidth(), saveAirBtnpressed.getMinimumHeight());
                    saveAirBtn.setCompoundDrawables(null, saveAirBtnpressed, null, null);
                }
                break;
                case R.id.exportAirBtn: {
                    exportAirBtn.setEnabled(false);
                    exportAirBtnpressed = getResources().getDrawable(R.drawable.export);
                    exportAirBtnpressed.setBounds(0, 0, exportAirBtnpressed.getMinimumWidth(), exportAirBtnpressed.getMinimumHeight());
                    exportAirBtn.setCompoundDrawables(null, exportAirBtnpressed, null, null);
                    Toast.makeText(AirActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.backAirBtn: {
                    Intent intent1 = new Intent(AirActivity.this, ChacheActivity.class);
                    startActivity(intent1);
                    finish();
                }
                break;
                case R.id.exitAirBtn: {
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
            startAirBtn.setEnabled(true);
            startAirBtnpressed = getResources().getDrawable(R.drawable.start1);
            startAirBtnpressed.setBounds(0, 0, startAirBtnpressed.getMinimumWidth(), startAirBtnpressed.getMinimumHeight());
            startAirBtn.setCompoundDrawables(null, startAirBtnpressed, null, null);

            stopAirBtn.setEnabled(true);
            stopAirBtnpressed = getResources().getDrawable(R.drawable.stop1);
            stopAirBtnpressed.setBounds(0, 0, stopAirBtnpressed.getMinimumWidth(), stopAirBtnpressed.getMinimumHeight());
            stopAirBtn.setCompoundDrawables(null, stopAirBtnpressed, null, null);

            scanAirBtn.setEnabled(true);
            scanAirBtnpressed = getResources().getDrawable(R.drawable.scan1);
            scanAirBtnpressed.setBounds(0, 0, scanAirBtnpressed.getMinimumWidth(), scanAirBtnpressed.getMinimumHeight());
            scanAirBtn.setCompoundDrawables(null, scanAirBtnpressed, null, null);

            printAirBtn.setEnabled(true);
            printAirBtnpressed = getResources().getDrawable(R.drawable.print1);
            printAirBtnpressed.setBounds(0, 0, printAirBtnpressed.getMinimumWidth(), printAirBtnpressed.getMinimumHeight());
            printAirBtn.setCompoundDrawables(null, printAirBtnpressed, null, null);

            saveAirBtn.setEnabled(true);
            saveAirBtnpressed = getResources().getDrawable(R.drawable.save1);
            saveAirBtnpressed.setBounds(0, 0, saveAirBtnpressed.getMinimumWidth(), saveAirBtnpressed.getMinimumHeight());
            saveAirBtn.setCompoundDrawables(null, saveAirBtnpressed, null, null);

            exportAirBtn.setEnabled(true);
            exportAirBtnpressed = getResources().getDrawable(R.drawable.export1);
            exportAirBtnpressed.setBounds(0, 0, exportAirBtnpressed.getMinimumWidth(), exportAirBtnpressed.getMinimumHeight());
            exportAirBtn.setCompoundDrawables(null, exportAirBtnpressed, null, null);
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
            chartAir.setVisibility(View.VISIBLE);
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
            chartAir.setVisibility (View.VISIBLE);
        }

        @Override
        public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        }

        //????????????
        public DynamicLineChartManager(LineChart mLineChart, String name, int color, int position) {
            this.lineChart = mLineChart;
            this.position = position;
            //??????????????????
            lineChart.setOnChartGestureListener(this);
            //????????????
            lineDataSet = new LineDataSet(null, "???????????????(N)");
            lineDataSet.setLineWidth(1.0f);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setColor(Color.BLUE);
            lineDataSet.setHighLightColor(Color.BLACK);
            //??????????????????
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
            //????????????
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
            chartAir.notifyDataSetChanged();
            chartAir.moveViewToX(0.00f);
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

            //??????
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
         * ??????????????????
         *
         * @param high
         * @param name
         */
        public void setHightLimitLine(float high, String name) {
            if (name == null) {
                name = "????????????";
            }
            LimitLine hightLimit = new LimitLine(high, name);
            hightLimit.setLineWidth(0.1f);
            hightLimit.setTextSize(10f);
            hightLimit.enableDashedLine(8.0f, 4.0f, 4.0f);
            leftAxis.removeAllLimitLines(); //????????????????????????????????????????????????add??????????????????
            leftAxis.addLimitLine(hightLimit);
            hightLimit.setLineColor(Color.BLACK);
            lineChart.invalidate();
        }


        public void setLowLimitLine(float low, String name) {
            if (name == null) {
                name = "????????????";
            }
            LimitLine hightLimit = new LimitLine(low, name);
            hightLimit.setLineWidth(0.1f);
            hightLimit.setTextSize(10f);
            hightLimit.setLineColor(Color.BLACK);
            hightLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            leftAxis.removeAllLimitLines(); //????????????????????????????????????????????????add??????????????????
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
            xAxis.removeAllLimitLines(); //????????????????????????????????????????????????add??????????????????
            hightLimit.setLineColor(Color.BLACK);
            lineChart.invalidate();
        }
    }

    public void ShowWave() {
        dynamicLineChartManager_Air.setData(realAir_Data,0.1f);
        dynamicLineChartManager_Air.setYAxis(5000, 0, 5);
        dynamicLineChartManager_Air.setXAxis(120, 0, 10,0);
        dynamicLineChartManager_Air.setHightLimitLine(0f, "");
        dynamicLineChartManager_Air.desChart(names.get (0),165);
    }
}
