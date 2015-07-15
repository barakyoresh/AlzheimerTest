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
import android.view.MenuItem;
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


        //init stats
        sessionStatistics = new AlzTestSessionStatistics(this.getIntent().getStringExtra(NewSessionFragment.SUBJECT_NAME),
                                                         this.getIntent().getLongExtra(NewSessionFragment.SUBJECT_ID, -1));
        sessionStatistics.setMMSEOrientationSpace(this.getIntent().getIntExtra(NewSessionFragment.MMSE_SPACE, -1));
        sessionStatistics.setMMSEOrientationTime(this.getIntent().getIntExtra(NewSessionFragment.MMSE_TIME, -1));
        sessionStatistics.setMMSETotal(this.getIntent().getIntExtra(NewSessionFragment.MMSE_TOTAL, -1));


        //click handlers
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

        //start session
        if(sessionStimuliPairs != null && sessionStimuliPairs.size() > 0) {
            invokeCountdown(this, "New Session, Category: " + sessionStimuliPairs.get(0).first.getCategory(), new CountdownCallback() {
                @Override
                public void onTimerExceeded() {
                    stimulusSwap();
                }
            }, userPrefs.getCountdownTimerValue());
        }else{
            Toast.makeText(this, "Insufficient Stimuli for session", Toast.LENGTH_SHORT).show();
            superOnBackPressed();
        }
   }

    @Override
    public void onBackPressed() {
        Log.v(OptionListActivity.APPTAG, "back pressed, showing pause screen");

        //build "pause" dialog
        final SessionActivity activity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit session?\nAll session data will be lost.")
                .setTitle("Session Paused")
                .setPositiveButton("I am sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(OptionListActivity.APPTAG, "back confirmed. exiting");
                        activity.superOnBackPressed();
                    }
                })
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.v(OptionListActivity.APPTAG, "back canceled. resuming");
                invokeCountdown(activity, "Resuming Session... " +
                                (sessionStimuliPairs.size() > 0 ? sessionStimuliPairs.get(0).first.getCategory() : ""), //state category if possible
                        new CountdownCallback() {

                            @Override
                            public void onTimerExceeded() {
                                Log.v(OptionListActivity.APPTAG, "reseting response time");

                                if(stimuliStartTime != 0) { //reset response time
                                    stimuliStartTime = System.nanoTime();
                                } else {                    //if response time wasn't initiated, this is the first pair
                                    stimulusSwap();
                                }

                            }
                        }, 3);
            }
        });
        dialog.show();
    }

    /**
     * This is necessary in order to avoid violating encapsulation.
     * This is the rare case where we don't want polymorphism.
     */
    public void superOnBackPressed() {
        super.onBackPressed();
    }

    /**
     * callback interface for countdown timer
     */
    private interface CountdownCallback{
        //method called at end of timer or on confirmation click
        public void onTimerExceeded();
    }


    private void invokeCountdown(Activity activity, String title, final CountdownCallback callback, int timeInSeconds) {
        if (timeInSeconds < 0) { timeInSeconds = 0; }
        if (timeInSeconds > 10) { timeInSeconds = 10; }
        //TODO: put stuff in finals. magic numbers!
        if(timeInSeconds > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);

            LayoutInflater inflater = activity.getLayoutInflater();
            builder.setMessage("Starting in " + timeInSeconds + "...")
                    .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.onTimerExceeded();
                            return;
                        }
                    });

            final AlertDialog dialog = builder.create();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    onBackPressed();
                    return;
                }
            });

            //500 is because exactly 1000 skips a second.
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
            invokeCountdown(this, "Continue session, Category: " + sessionStimuliPairs.get(0).first.getCategory(), new CountdownCallback() {
                @Override
                public void onTimerExceeded() {
                    stimulusSwap();
                }
            }, userPrefs.getCountdownTimerValue());
        }else{
            //finish session
            sessionStatistics.setSessionEndTime(new Date());
            sessionStatistics.setCategoryPreferences(userPrefs.getCategoryPreferences());
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
            superOnBackPressed();
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

    /**
     * Overriding home button to do back action
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
