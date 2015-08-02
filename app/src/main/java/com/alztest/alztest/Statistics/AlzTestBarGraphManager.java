package com.alztest.alztest.Statistics;

import android.graphics.Color;
import android.util.Log;
import android.util.Pair;

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
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Barak Yoresh on 02/08/2015.
 */

public class AlzTestBarGraphManager {
    static final float EPSILON = 0.0001f;
    static int[] subGraphColors = new int[]{Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA};

    private int subGraphIndex = 0;
    private int numberOfSessions = 0;
    private int numberOfBars = 0;

    private HashMap<String, ArrayList<Pair<Float, Date>>> successDataTrends = new HashMap<String, ArrayList<Pair<Float, Date>>>();
    private HashMap<String, ArrayList<Pair<Float, Date>>> responseDataTrends = new HashMap<String, ArrayList<Pair<Float, Date>>>();
    private HashMap<String, ArrayList<Pair<Float, Date>>> sucDivRespDataTrends = new HashMap<String, ArrayList<Pair<Float, Date>>>();



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

        //success
        if (successDataTrends.containsKey(categoryName)) {
            l = successDataTrends.get(categoryName);
        } else {
            l = new ArrayList<Pair<Float, Date>>();
        }
        l.add(new Pair<Float, Date>(meanCorrectAnswers, date));

        //reponse
        if (responseDataTrends.containsKey(categoryName)) {
            l = responseDataTrends.get(categoryName);
        } else {
            l = new ArrayList<Pair<Float, Date>>();
        }
        l.add(new Pair<Float, Date>(meanResponseTime, date));

        responseDataTrends.put(categoryName, l);

        //sucDivResp
        if (sucDivRespDataTrends.containsKey(categoryName)) {
            l = sucDivRespDataTrends.get(categoryName);
        } else {
            l = new ArrayList<Pair<Float, Date>>();
        }
        l.add(new Pair<Float, Date>(meanCorrectAnswers / (meanResponseTime + EPSILON), date));

        sucDivRespDataTrends.put(categoryName, l);
    }

    public void updateBarGraph(GraphView gv, final HashMap<String, ArrayList<Pair<Float, Date>>> trend, String title) {
        if(numberOfSessions <= 0 || numberOfBars <= 0){
            return;
        }

        resetSubGraphColors();
        int offset = 1;

        //date labels
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        final String[] labels = new String[numberOfBars + 2 + trend.size()];
        labels[0] = "";
        labels[1] = "";

        DataPoint dp[] = new DataPoint[numberOfBars + trend.size()];

        //iterate categories
        int j = 0;
        for(ArrayList<Pair<Float, Date>> subGraph : trend.values()) {
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


        BarGraphSeries bseries = new BarGraphSeries<DataPoint>(dp);

        //series styling
        //color change callback
        bseries.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return getSubGraphColor((int) (data.getX() - 1) / (numberOfSessions+1)); // +1 for spacers
            }
        });
        bseries.setSpacing(50);

        //graph styling
        gv.addSeries(bseries);
        gv.getViewport().setXAxisBoundsManual(true);
        gv.getViewport().setMinX(0);
        gv.getViewport().setMaxX(numberOfBars + 1 + trend.size());

        //TODO: enable this, the library is simply not that good and buggy, by the time someone updates this, there might be a google graph view that works properly.
        //scroll effect -
        /*
        gv.getViewport().setXAxisBoundsManual(true);
        gv.getViewport().setMaxX(16);

        gv.getViewport().setYAxisBoundsManual(true);
        gv.getViewport().setMinY(0);

        gv.getViewport().setScrollable(true);

        labelRenderer.setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double v, boolean b) {
                if(b) {
                    int index = (int) v;
                    if(index >= 0 && index < labels.length) {
                        return labels[index];
                    }
                    return "";
                }
                return super.formatLabel(v, b);
            }

        });
        */

        GridLabelRenderer labelRenderer = gv.getGridLabelRenderer();
        labelRenderer.setGridStyle(GridLabelRenderer.GridStyle.NONE);
        labelRenderer.setNumHorizontalLabels(numberOfBars + 2 + trend.size());
        labelRenderer.setVerticalLabelsVisible(false);




        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(gv);
        staticLabelsFormatter.setHorizontalLabels(labels);


        labelRenderer.reloadStyles();
        gv.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        gv.setTitle(title);
    }

    private void resetSubGraphColors() {
        subGraphIndex = 0;
    }

    private int getSubGraphColor(int index){
        return subGraphColors[index % subGraphColors.length];
    }
}
