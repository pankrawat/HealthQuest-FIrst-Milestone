package com.tupelo.wellness.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.tupelo.wellness.R;

/**
 * Created by admin1 on 22/9/17.
 */

public class MyWeight extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_weight);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(20, 60),
                new DataPoint(21, 65),
                new DataPoint(22, 62),
                new DataPoint(23, 60),
                new DataPoint(24, 70),
                new DataPoint(25, 72),
                new DataPoint(26, 68)

        });
        series.setDrawDataPoints(true);
        graph.addSeries(series);

    }
}
