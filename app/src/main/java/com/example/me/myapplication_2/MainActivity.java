package com.example.me.myapplication_2;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView ax,ay,az;
    private Sensor accelerometer;
    private SensorManager s;
    private LineChart mchart;
    private  Thread thread;
    private boolean plotData=true;
    private double magnitude_of_acceleration;

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        s.unregisterListener(this);
        thread.interrupt();
        super.onDestroy();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //sensormanager creating
        s=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //assign our sensor to type accelerometer
        accelerometer = s.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //registrying our sensorManager to listener
        s.registerListener(this, accelerometer ,SensorManager.SENSOR_DELAY_GAME);
        //linking views
        ax=findViewById(R.id.x_value);
        ay=findViewById(R.id.y_value);
        az=findViewById(R.id.z_value);
       mchart=(LineChart)findViewById(R.id.chart1);
       mchart.getDescription().setEnabled(true);
       mchart.getDescription().setText("realtimeaccelerometer");
       mchart.setTouchEnabled(false);
        mchart.setDragEnabled(false);
        mchart.setScaleEnabled(false);
        mchart.setDrawGridBackground(false);
        mchart.setPinchZoom(false);
        mchart.setBackgroundColor(Color.YELLOW);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);
        mchart.setData(data);
        //making series object
        startPlot();
       // readings_to_plot=new LinkedList<>();
    }

    private void startPlot() {
          if(thread!=null)
          {

              thread.interrupt();
          }
  thread=new Thread(new Runnable() {
      @Override
      public void run() {
          while(true)
          {
              plotData=true;
              try {

                  Thread.sleep(10);
              }catch (InterruptedException e)
              {
                  e.printStackTrace();
              }
          }
      }
  });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(thread!=null)
            thread.interrupt();
        s.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //here we will asign event values to our output view

            ax.setText("x: " + event.values[0]);
            ay.setText("y: " + event.values[1]);
            az.setText("z: " + event.values[2]);
      if(plotData)
      {
          addEntry(event);
          plotData=true;
      }
            magnitude_of_acceleration=Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]);

    }

    @Override
    protected void onResume() {
        super.onResume();
        s.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void addEntry(SensorEvent event) {
    LineData data=mchart.getData();
    if(data!=null)
    {
        ILineDataSet set= data.getDataSetByIndex(0);
        if(set==null) {
            set = createSet();
            data.addDataSet(set);
        }
        data.addEntry(new Entry(set.getEntryCount(),event.values[0]+5),0);
        data.notifyDataChanged();
        mchart.setMaxVisibleValueCount(100);
        mchart.moveViewToX(data.getEntryCount());
    }

    }

    private LineDataSet createSet() {
        LineDataSet set=new LineDataSet(null,"dynamic set");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }




}
