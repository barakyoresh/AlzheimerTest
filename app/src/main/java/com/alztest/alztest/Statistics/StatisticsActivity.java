/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Statistics;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.alztest.alztest.Dialogs.FileDialogCallback;
import com.alztest.alztest.Dialogs.SaveDialog;
import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;
import com.alztest.alztest.Toolbox.AlzTestEmailAssistant;
import com.alztest.alztest.Toolbox.AlzTestSerializeManager;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * Created by Barak Yoresh on 03/05/2015.
 */
public class StatisticsActivity extends Activity {
    AlzTestSessionStatistics stat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        //get stats
        String statStr = getIntent().getStringExtra(StatisticsListFragment.STATISTIC);
        if (statStr == null) {
            onBackPressed();
        }

        stat = (AlzTestSessionStatistics) AlzTestSerializeManager.deSerialize(statStr, AlzTestSessionStatistics.class);
        if(stat == null) {
            onBackPressed();
        }

        Log.v(OptionListActivity.APPTAG, String.valueOf(stat.getMMSETotal()));
        Log.v(OptionListActivity.APPTAG, stat.getSubjectName());

        // present data
        //graph
        updateGraph(stat);

        //MMSE
        updateMMSE(stat);

        return;
    }

    private void updateMMSE(AlzTestSessionStatistics stat) {
        TextView spaceScore = (TextView) findViewById(R.id.MMSEorientationSpaceScore);
        TextView timeScore = (TextView) findViewById(R.id.MMSEorientationTimeScore);
        TextView totalScore = (TextView) findViewById(R.id.MMSEtotalScore);

        spaceScore.setText(Integer.toString(stat.getMMSEOrientationSpace()));
        timeScore.setText(Integer.toString(stat.getMMSEOrientationTime()));
        totalScore.setText(Integer.toString(stat.getMMSETotal()));
    }


    private void updateGraph(final AlzTestSessionStatistics stats) {
        GraphView gv = (GraphView) findViewById(R.id.statisticsaGraph);

        ArrayList<Pair<String, Float>> categoryCorDivAvgResponseTime =
                AlzTestStatisticsBrain.getBarGraphCatValues(stats.getStatistics(), stats.getCategoryPreferences());

        int size = categoryCorDivAvgResponseTime.size();

        //bar data and labels
        DataPoint dp[] = new DataPoint[size];
        String labels[] = new String[size + 2]; //the graph spans from zero to size. both zero and size should be empty labels
        labels[0] = "";
        labels[size] = "";

        for (int i = 0 ; i < size ; i++) {
            dp[i] = new DataPoint(i+1, categoryCorDivAvgResponseTime.get(i).second);
            labels[i+1] = categoryCorDivAvgResponseTime.get(i).first;
        }

        BarGraphSeries bseries = new BarGraphSeries<DataPoint>(dp);

        bseries.setSpacing(50);
        bseries.setDrawValuesOnTop(true);
        bseries.setValuesOnTopColor(Color.RED);

        gv.addSeries(bseries);

        gv.getViewport().setXAxisBoundsManual(true);
        gv.getViewport().setMinX(0);
        gv.getViewport().setMaxX(size + 1);

        GridLabelRenderer labelRenderer = gv.getGridLabelRenderer();
        labelRenderer.setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(gv);
        staticLabelsFormatter.setHorizontalLabels(labels);
        gv.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);


        gv.setTitle("(# Correct Responses / # Stimuli Pairs Show) / Average Response Times");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_export:
                Log.v(OptionListActivity.APPTAG, "exporting statistics");
                openSaveDialog();
                return true;
            case R.id.action_send_statistics_via_email:
                Log.v(OptionListActivity.APPTAG, "sending statistics via mail");
                sendStatisticsViaMail();
        }
        return true;
    }

    private void sendStatisticsViaMail() {
        boolean operationSuccessful = false;

        if(stat == null) {
            Toast.makeText(this, "Statistics not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        //create file
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sb.append(stat.getSubjectName())
                .append(" ")
                .append(stat.getSubjectId())
                .append(" ")
                .append(sdf.format(stat.getSessionStartTime()))
                .append(".xls");
        String cacheDir = this.getCacheDir().getAbsolutePath();
        File file = new File(cacheDir, sb.toString());

        Log.v(OptionListActivity.APPTAG, "attempting to save file, path: " + cacheDir + "\nfilename: " + sb.toString());
        operationSuccessful = AlzTestStatisticsBrain.saveStatisticsToFile(stat, file);

        Uri contentUri = getUriForFile(this, "com.alztest.fileprovider", file);

        if(!operationSuccessful) {
            Toast.makeText(this, "Error saving file. :(", Toast.LENGTH_SHORT).show();
            return;
        }

        //send email
        Log.v(OptionListActivity.APPTAG, "sending email.");
        AlzTestEmailAssistant.sendNewEmail("AlzTestApp - " + sb.toString(), "Attached is the session data", contentUri, this);
    }


    private void openSaveDialog() {
        SaveDialog sd = new SaveDialog();
        sd.extensionType = "xls";
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sb.append(stat.getSubjectName()).append(" ").append(stat.getSubjectId()).append(" ")
                .append(sdf.format(stat.getSessionStartTime()));
        sd.defaultFileName = sb.toString();
        sd.setCallback(new FileDialogCallback() {
            @Override
            public void onChooseFile(Activity activity, File file) {
                boolean operationSuccessful = false;

                if(stat != null) {
                    operationSuccessful = AlzTestStatisticsBrain.saveStatisticsToFile(stat, file);
                }

                Log.v(OptionListActivity.APPTAG, operationSuccessful ? "success!" : "failed :(");
                Toast toast = Toast.makeText(activity, operationSuccessful ? "Saved Statistics file successfully" : "Saving Statistics file failed :(", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        sd.show(getFragmentManager(), getString(R.string.action_save_preferences));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.statistics_actions, menu);
        return true;
    }
}
