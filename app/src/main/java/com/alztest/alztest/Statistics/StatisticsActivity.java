/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Statistics;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;
import com.alztest.alztest.Toolbox.AlzTestSerializeManager;

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
                Log.v(OptionListActivity.APPTAG, stat.getSubjectName());
            }
        }
        //TODO handle reaching here
        return;
    }


}
