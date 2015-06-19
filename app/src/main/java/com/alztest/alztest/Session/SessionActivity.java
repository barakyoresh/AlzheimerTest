/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Session;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.Prefrences.AlzTestUserPrefs;
import com.alztest.alztest.R;
import com.alztest.alztest.Statistics.AlzTestSessionStatistics;
import com.alztest.alztest.Statistics.StatisticsListFragment;
import com.alztest.alztest.Stimuli.Stimulus;
import com.alztest.alztest.Toolbox.AlzTestDatabaseManager;
import com.alztest.alztest.Toolbox.AlzTestPreferencesManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Barak Yoresh on 14/03/2015.
 */
public class SessionActivity extends Activity {
    private TextView leftStimulus, rightStimulus;
    private Stimulus currentLeftStimulus, currentRightStimulus;
    private ArrayList<ArrayList<Pair<Stimulus, Stimulus>>> sessionStimuliPairsByCategory;
    private ArrayList<Pair<Stimulus, Stimulus>> sessionStimuliPairs;
    private AlzTestSessionStatistics sessionStatistics;
    private AlzTestUserPrefs userPrefs;
    private long stimuliStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        leftStimulus = (TextView) findViewById(R.id.leftStimulus);
        rightStimulus = (TextView) findViewById(R.id.rightStimulus);

        //hide keyboard
        //InputMethodManager imm = (InputMethodManager) this.getSystemService(
         //       Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);


        //load last userPrefs
        AlzTestPreferencesManager prefsManager = new AlzTestPreferencesManager(this);
        userPrefs = prefsManager.getCachedPreferencesSet();

        //set stimuli text size
        float textSizeMultiplier = (userPrefs.getTextSize() / 100);
        leftStimulus.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftStimulus.getTextSize() * textSizeMultiplier);
        rightStimulus.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightStimulus.getTextSize() * textSizeMultiplier);

        //load statistics DB
        AlzTestDatabaseManager.init(this);

        //Build pairs
        AlzTestSessionFactory sessionFactory = new AlzTestSessionFactory(this);
        sessionStimuliPairsByCategory = sessionFactory.buildSessionData(userPrefs);

        if(sessionStimuliPairsByCategory.size() > 0) {
            sessionStimuliPairs = sessionStimuliPairsByCategory.remove(0);
        }else {
            sessionStimuliPairs = null;
        }

       /* for (Pair<Stimulus, Stimulus> p : sessionStimuliPairs){
            Log.v(OptionListActivity.APPTAG, p.first.getName() + " : " + p.second.getName());
       }*/

        //init stats
        sessionStatistics = new AlzTestSessionStatistics(this.getIntent().getStringExtra(NewSessionFragment.SUBJECT_NAME),
                                                         this.getIntent().getLongExtra(NewSessionFragment.SUBJECT_ID, -1));
        sessionStatistics.setMMSEOrientationSpace(this.getIntent().getIntExtra(NewSessionFragment.MMSE_SPACE, -1));
        sessionStatistics.setMMSEOrientationTime(this.getIntent().getIntExtra(NewSessionFragment.MMSE_TIME, -1));
        sessionStatistics.setMMSETotal(this.getIntent().getIntExtra(NewSessionFragment.MMSE_TOTAL, -1));





        findViewById(R.id.leftLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(OptionListActivity.APPTAG, "left clicked");
                subjectClicked(StimulusSelection.left);
            }
        });
        findViewById(R.id.rightLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(OptionListActivity.APPTAG, "right clicked");
                subjectClicked(StimulusSelection.right);
            }
        });

        if(sessionStimuliPairs != null && sessionStimuliPairs.size() > 0) {
            invokeCountdown(this, "New Session, Category: " + sessionStimuliPairs.get(0).first.getCategory());
        }else{
            //TODO: when implementing pause on back, make sure empty sessions still work fine
            Toast.makeText(this, "Insufficient Stimuli for session", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
   }

    private void invokeCountdown(Activity activity, String title) {
        int timeInSeconds = userPrefs.getCountdownTimerValue();
        //TODO: put stuff in finals. magic numbers!
        if(timeInSeconds > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);

            LayoutInflater inflater = activity.getLayoutInflater();
            builder.setMessage("Starting in " + timeInSeconds + "...");
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onBackPressed();
                    return;
                }
            });
            builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stimulusSwap();
                    return;
                }
            });

            final AlertDialog dialog = builder.create();

            //TODO: why 500? somehow 1000 skips one...
            CountDownTimer timer = new CountDownTimer(TimeUnit.MILLISECONDS.convert(timeInSeconds, TimeUnit.SECONDS), 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    TextView TV = (TextView) dialog.findViewById(android.R.id.message);
                    TV.setText("Starting in " + (TimeUnit.SECONDS.convert(millisUntilFinished, TimeUnit.MILLISECONDS) + 1) + "...");
                    Log.v(OptionListActivity.APPTAG, "Starting in " + (TimeUnit.SECONDS.convert(millisUntilFinished, TimeUnit.MILLISECONDS) + 1) + "...");
                }

                @Override
                public void onFinish() {
                    if(dialog.isShowing()) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).callOnClick();
                    }
                }
            };
            timer.start();

            dialog.show();
        } else {
            stimulusSwap();
        }
    }

    private void stimulusSwap(){
        stimuliStartTime = System.nanoTime();
        if(sessionStimuliPairs.size() > 0) {
            Pair<Stimulus, Stimulus> p = sessionStimuliPairs.remove(0);
            currentLeftStimulus = p.first;
            currentRightStimulus = p.second;
            leftStimulus.setText(currentLeftStimulus.getName());
            rightStimulus.setText(currentRightStimulus.getName());
        }else{
            finishSession();
        }
    }

    private void finishSession() {
        if (sessionStimuliPairsByCategory.size() > 0) {
            //proceed to next category
            sessionStimuliPairs = sessionStimuliPairsByCategory.remove(0);
            invokeCountdown(this, "Continue session, Category: " + sessionStimuliPairs.get(0).first.getCategory());
        }else{
            //finish session
            sessionStatistics.setSessionEndTime(new Date());

            Toast toast;

            try {
                AlzTestDatabaseManager.getInstance().getHelper().getAlzTestSessionStatisticsDao().create(sessionStatistics);
                toast =  Toast.makeText(this, "Information saved to database", Toast.LENGTH_SHORT);
            } catch (SQLException e) {
                Log.v(OptionListActivity.APPTAG, "Failed adding data to DB");
                e.printStackTrace();
                toast =  Toast.makeText(this, "Database error! information lost :(", Toast.LENGTH_SHORT);
            }

            StatisticsListFragment.upDateListFromDB();

            toast.show();
            onBackPressed();
        }
    }

    private void subjectClicked(StimulusSelection selected) {
        long responseTimeNs = System.nanoTime() - stimuliStartTime;
        AlzTestSingleClickStats clickStat =  new AlzTestSingleClickStats(currentLeftStimulus,
                currentRightStimulus, selected,
                TimeUnit.MILLISECONDS.convert(responseTimeNs, TimeUnit.NANOSECONDS));
        sessionStatistics.addClickStat(clickStat);
        stimulusSwap();
    }

}
