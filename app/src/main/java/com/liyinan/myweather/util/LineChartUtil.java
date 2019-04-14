package com.liyinan.myweather.util;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class LineChartUtil {

    public static void initChart(LineChart lineChart) {
        /***图表设置***/
        //是否展示网格线
        lineChart.setDrawGridBackground(false);
        //是否显示边界
        lineChart.setDrawBorders(false);
        //是否可以拖动
        lineChart.setDragEnabled(false);
        //是否有触摸事件
        lineChart.setTouchEnabled(false);
        //设置XY轴动画效果
        //lineChart.animateY(2500);
        //lineChart.animateX(1500);
        //设置一页最大显示个数为6，超出部分就滑动
        //float ratio = (float) 7/(float) 5;
        //显示的时候是按照多大的比率缩放显示,1f表示不放大缩小
        //lineChart.zoom(ratio,1f,0,0);

        /***XY轴的设置***/
        //获取坐标轴
        XAxis xAxis = lineChart.getXAxis();
        YAxis leftYAxis = lineChart.getAxisLeft();
        YAxis rightYaxis = lineChart.getAxisRight();
        //隐藏坐标轴
        leftYAxis.setEnabled(false);
        rightYaxis.setEnabled(false);
        xAxis.setEnabled(false);
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(-0.2f);
        xAxis.setAxisMaximum(6.2f);
        xAxis.setGranularity(1f);
        //保证Y轴从0开始，不然会上移一点
        //leftYAxis.setAxisMinimum(0f);
        //rightYaxis.setAxisMinimum(0f);


        /***折线图例 标签 设置***/
        Legend legend = lineChart.getLegend();
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.NONE);
        legend.setTextColor(Color.WHITE);
        //legend.setTextSize(12f);
        //显示位置 左下方
        //legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        //legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        //legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        //legend.setDrawInside(false);
        //隐藏x轴描述
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);
    }


    public static void initLineDataSet(LineDataSet lineDataSet, String color, LineDataSet.Mode mode) {
        lineDataSet.setColor(Color.parseColor(color));
        lineDataSet.setCircleColor(Color.parseColor(color));
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(3f);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(12f);
        //设置折线图填充
        lineDataSet.setDrawFilled(false);
        //lineDataSet.setFormLineWidth(1f);
        //lineDataSet.setFormSize(15.f);
        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
        //
        lineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int n=(int)value;
                return n+"°";
            }
        });
    }

    public static void showLineChart(List<Integer> tmpList, String name, String color,LineChart lineChart) {
        List<Entry> entries = new ArrayList<>();
        for (int i=0;i<tmpList.size();i++) {
            /**
             * 在此可查看 Entry构造方法，可发现 可传入数值 Entry(float x, float y)
             * 也可传入Drawable， Entry(float x, float y, Drawable icon) 可在XY轴交点 设置Drawable图像展示
             */
            entries.add(new Entry(i, tmpList.get(i)));
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }
    /**
     * 添加曲线
     */
    public static void addLine(List<Integer> tmpList, String name, String color,LineChart lineChart) {
        List<Entry> entries = new ArrayList<>();
        for (int i=0;i<tmpList.size();i++) {
            entries.add(new Entry(i, tmpList.get(i)));
        }

        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.getLineData().addDataSet(lineDataSet);
        lineChart.invalidate();
    }
}
