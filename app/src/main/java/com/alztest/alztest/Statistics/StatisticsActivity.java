/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Statistics;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;
import com.alztest.alztest.Session.AlzTestSingleClickStats;
import com.alztest.alztest.Toolbox.AlzTestSerializeManager;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;

/**
 * Created by Barak Yoresh on 03/05/2015.
 */
public class StatisticsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        String statStr = getIntent().getStringExtra(StatisticsListFragment.STATISTIC);
        if (statStr != null) {
            AlzTestSessionStatistics stat = (AlzTestSessionStatistics) AlzTestSerializeManager.deSerialize(statStr, AlzTestSessionStatistics.class);

            if(stat != null) {
                Log.v(OptionListActivity.APPTAG, String.valueOf(stat.getMMSETotal()));

                Log.v(OptionListActivity.APPTAG, stat.getSubjectName());

                ArrayList<AlzTestSingleClickStats> clickStats = stat.getStatistics();
                ArrayList<AlzTestSingleClickStats> correctClickStats = stat.getCorrectStatistics();

                // append data
                updateGraph(correctClickStats, (GraphView) findViewById(R.id.statisticsaGraph));
            }
        }

        //TODO handle reaching here


        return;
    }


    private static void updateGraph(final ArrayList<AlzTestSingleClickStats> stats, GraphView gv) {

        DataPoint dp[] = new DataPoint[stats.size()+1];

        dp[0] = new DataPoint(0,stats.get(0).getResponseTimeInMs());
        for (int i = 0 ; i < stats.size() ; i++) {
            dp[i+1] = new DataPoint(i, stats.get(i).getResponseTimeInMs());
        }

        PointsGraphSeries<DataPoint> pseries = new PointsGraphSeries<DataPoint>(dp);
        LineGraphSeries<DataPoint> lseries = new LineGraphSeries<DataPoint>(dp);
        pseries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                System.out.println(stats.get((int) dataPointInterface.getX()).getLeftStim().getName());
                System.out.println("{" + dataPointInterface.getX() + ", " + dataPointInterface.getY() + "}");
            }
        });

        gv.addSeries(pseries);
        gv.addSeries(lseries);

        gv.getViewport().setXAxisBoundsManual(true);
        gv.getViewport().setMinX(0);
        gv.getViewport().setMaxX(stats.size() - 1);

        gv.setTitle("Response Times");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }


}
