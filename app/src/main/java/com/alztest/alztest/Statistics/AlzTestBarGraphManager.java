package com.alztest.alztest.Statistics;

import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.Prefrences.AlzTestCategoryAdapter;
import com.alztest.alztest.Session.AlzTestSingleClickStats;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Barak Yoresh on 02/08/2015.
 */

public class AlzTestBarGraphManager {
    static final float EPSILON = 0.0001f;
    static int[] subGraphColors = new int[]{Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA};

    private int subGraphIndex = 0;
    private int numberOfSessions = 0;
    private int numberOfBars = 0;
    private int numberOfCategories;

    private LinkedHashMap<String, ArrayList<Pair<Float, Date>>> successDataTrends = new LinkedHashMap<String, ArrayList<Pair<Float, Date>>>();
    private LinkedHashMap<String, ArrayList<Pair<Float, Date>>> responseDataTrends = new LinkedHashMap<String, ArrayList<Pair<Float, Date>>>();
    private LinkedHashMap<String, ArrayList<Pair<Float, Date>>> sucDivRespDataTrends = new LinkedHashMap<String, ArrayList<Pair<Float, Date>>>();

    private HashMap<HashMap<String, ArrayList<Pair<Float, Date>>>, Boolean> trendSorted = new HashMap<HashMap<String, ArrayList<Pair<Float, Date>>>, Boolean>();

    private BarGraphSeries<DataPoint> series;
    private String[] labels;



    public AlzTestBarGraphManager() {
    }



    public HashMap<String, ArrayList<Pair<Float, Date>>> getSuccessDataTrends() {
        return successDataTrends;
    }

    public HashMap<String, ArrayList<Pair<Float, Date>>> getResponseDataTrends() {
        return responseDataTrends;
    }

    public HashMap<String, ArrayList<Pair<Float, Date>>> getSucDivRespDataTrends() {
        return sucDivRespDataTrends;
    }

    public void addSessionData(AlzTestSessionStatistics statistics) {
        if(numberOfSessions >= 4) {
            Log.e(OptionListActivity.APPTAG, "supports only 4 session at this time");
            return;
        }

        numberOfSessions++;
        ArrayList<AlzTestSingleClickStats> stats = statistics.getStatistics();
        ArrayList<AlzTestCategoryAdapter.CategoryListItem> categoryPreferences = statistics.getCategoryPreferences();

        Date date = statistics.getSessionStartTime();
        float sumCorrectAnswers = 0;
        long sumResponseTime = 0;
        int numOfPairs = 0;

        //iterate categories
        for(AlzTestCategoryAdapter.CategoryListItem category : categoryPreferences) {


            if(category.isIncludeInSession() && category.isIncludeInAnalysis()) {

                String categoryName = category.getCategory();
                int numOfPairsInCategroy = 0;
                int numOfCorrectAnswersInCategory = 0;
                long sumResponseTimesInMs = 0;

                //iterate stats
                for (AlzTestSingleClickStats stat : stats) {
                    if (stat.rightStim.getCategory().equals(categoryName)) { //if relevant category
                        numOfPairsInCategroy++;
                        sumResponseTimesInMs += stat.getResponseTimeInMs();
                        numOfCorrectAnswersInCategory += (stat.correctResponse ? 1 : 0);

                        // This is for possible future hard coded changes by Greg.
                        //sumResponseTimesInMs += (stat.correctResponse ? stat.getResponseTimeInMs() : 0);
                    }
                }

                numOfPairs += numOfPairsInCategroy;
                sumResponseTime += sumResponseTimesInMs;
                sumCorrectAnswers += numOfCorrectAnswersInCategory;


                //finalize data
                float meanResponseTime = ((float) sumResponseTimesInMs / (numOfPairsInCategroy + EPSILON));
                float meanCorrectAnswers = ((float) numOfCorrectAnswersInCategory / (numOfPairsInCategroy + EPSILON));
                addSessionToCategory(categoryName, date, meanCorrectAnswers, meanResponseTime);
            }
        }

        //add integrated fictive category
        addSessionToCategory("Integrated", date, (sumCorrectAnswers / (numOfPairs + EPSILON)),  ((float) sumResponseTime / (numOfPairs + EPSILON)));
    }

    private void addSessionToCategory(String categoryName, Date date, float meanCorrectAnswers, float meanResponseTime) {

        ArrayList<Pair<Float, Date>> l;
        numberOfBars++;


        //TODO: clean up this messy code, there's not option of one containing and the rest not.
        //success
        if (successDataTrends.containsKey(categoryName)) {
            l = successDataTrends.get(categoryName);
        } else {
            l = new ArrayList<Pair<Float, Date>>();
            numberOfCategories++; //this assumes that if one map has category, all of them do.
        }
        l.add(new Pair<Float, Date>(meanCorrectAnswers, date));

        successDataTrends.put(categoryName, l);
        trendSorted.put(successDataTrends, false);

        //reponse
        if (responseDataTrends.containsKey(categoryName)) {
            l = responseDataTrends.get(categoryName);
        } else {
            l = new ArrayList<Pair<Float, Date>>();
        }
        l.add(new Pair<Float, Date>(meanResponseTime, date));

        responseDataTrends.put(categoryName, l);
        trendSorted.put(responseDataTrends, false);


        //sucDivResp
        if (sucDivRespDataTrends.containsKey(categoryName)) {
            l = sucDivRespDataTrends.get(categoryName);
        } else {
            l = new ArrayList<Pair<Float, Date>>();
        }
        l.add(new Pair<Float, Date>(meanCorrectAnswers / (meanResponseTime + EPSILON), date));

        sucDivRespDataTrends.put(categoryName, l);
        trendSorted.put(sucDivRespDataTrends, false);

    }

    public void updateBarGraph(GraphView gv, LinearLayout categoryLabels, final HashMap<String, ArrayList<Pair<Float, Date>>> trend, String title) {
        //TODO: change this to be so that a user can choose which dates to show
        if(numberOfSessions <= 0 || numberOfBars <= 0){
            return;
        }

        //sort trend categories if needed
        if(!trendSorted.containsKey(trend) || !trendSorted.get(trend)) {
            //sort each category
            for(ArrayList<Pair<Float, Date>> subGraph : trend.values()) {
                Collections.sort(subGraph, new DateComparator());
                trendSorted.put(trend, true);
            }
        }


        //update series and labels
        updateBarSeriesAndLabels(trend);

        gv.addSeries(series);

        //graph styling
        //size
        gv.getViewport().setXAxisBoundsManual(true);
        gv.getViewport().setMinX(0);
        gv.getViewport().setMaxX(numberOfBars + 1 + trend.size());


        //labels
        GridLabelRenderer labelRenderer = gv.getGridLabelRenderer();
        labelRenderer.setGridStyle(GridLabelRenderer.GridStyle.NONE);
        labelRenderer.setNumHorizontalLabels(numberOfBars + 2 + trend.size());
        labelRenderer.setVerticalLabelsVisible(false);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(gv);
        staticLabelsFormatter.setHorizontalLabels(labels);
        labelRenderer.reloadStyles();
        gv.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        //title
        gv.setTitle(title);

        //update category labels
        for(String category : trend.keySet()) {
            TextView tv = new TextView(categoryLabels.getContext());
            tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            tv.setGravity(Gravity.CENTER);
            tv.setText(category);
            categoryLabels.addView(tv);
        }
    }

    private void updateBarSeriesAndLabels(final HashMap<String, ArrayList<Pair<Float, Date>>> trend){
        resetSubGraphColors();
        int offset = 1;

        //date labels
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        labels = new String[numberOfBars + 2 + trend.size()];
        labels[0] = "";
        labels[1] = "";

        DataPoint dp[] = new DataPoint[numberOfBars + trend.size()];

        //iterate categories and update series
        int j = 0;
        for(ArrayList<Pair<Float, Date>> subGraph : trend.values()) {
            //TODO: enable subset of sessions from each category
            //add data
            for (int i = 0 ; i < subGraph.size() ; i++) {
                dp[i + offset -1] = new DataPoint(i + offset, Float.isNaN(subGraph.get(i).first) ? 0 : subGraph.get(i).first);
                labels[i + offset] = sdf.format(subGraph.get(i).second);
            }

            //advance offset
            offset += subGraph.size();

            //add spacer
            if(j < trend.size()) {
                dp[offset -1] = new DataPoint(offset, 0f);
                labels[offset] = " ";
                offset++;
            }
            j++;
        }


        series = new BarGraphSeries<DataPoint>(dp);

        //series styling
        //color change callback
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return getSubGraphColor((int) (data.getX() - 1) / (numberOfSessions+1)); // +1 for spacers
            }
        });

        series.setSpacing(50);
    }


    private void resetSubGraphColors() {
        subGraphIndex = 0;
    }

    private int getSubGraphColor(int index){
        return subGraphColors[index % subGraphColors.length];
    }


    private class DateComparator implements Comparator<Pair<Float, Date>> {

        @Override
        public int compare(Pair<Float, Date> lhs, Pair<Float, Date> rhs) {
            return lhs.second.compareTo(rhs.second);
        }
    }
}
