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
import java.util.Map;

/**
 * Created by Barak Yoresh on 02/08/2015.
 */

public class AlzTestBarGraphManager {
    static final float EPSILON = 0.0001f;
    public static final String SUC_DIV_RESP = "Mean Success Rate/Mean Response Time",
                        RESP = "Mean Response Time",
                        SUC = "Mean Success Rate";
    public static final String INTEGRATED = "Integrated";
    static int[] subGraphColors = new int[]{Color.parseColor("#329967"), Color.parseColor("#366092"),Color.parseColor("#953737"), Color.parseColor("#31859D")};
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");


    private int numberOfSessions = 0;
    private int numberOfBars = 0;
    private int numberOfCategories;

    private LinkedHashMap<String, ArrayList<Pair<Float, Date>>> successDataTrends = new LinkedHashMap<String, ArrayList<Pair<Float, Date>>>();
    private LinkedHashMap<String, ArrayList<Pair<Float, Date>>> responseDataTrends = new LinkedHashMap<String, ArrayList<Pair<Float, Date>>>();
    private LinkedHashMap<String, ArrayList<Pair<Float, Date>>> sucDivRespDataTrends = new LinkedHashMap<String, ArrayList<Pair<Float, Date>>>();
    private HashMap<Integer, Integer> colorMap;
    private LinkedHashMap<String, Float> categoryLabelsAndWeights;

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
        addSessionToCategory(INTEGRATED, date, (sumCorrectAnswers / (numOfPairs + EPSILON)),  ((float) sumResponseTime / (numOfPairs + EPSILON)));
    }

    private void addSessionToCategory(String categoryName, Date date, float meanCorrectAnswers, float meanResponseTime) {
        ArrayList<Pair<Float, Date>> l;


        //TODO: clean up this messy code, there's not option of one containing  the key and the rest not.
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


    public void updateBarGraph(GraphView gv, LinearLayout categoryLabels, HashMap<String, ArrayList<Pair<Float, Date>>> trend, String title, int pos, int len, Date specificDate) {
        if(numberOfSessions <= 0){
            return;
        }

        gv.removeAllSeries();
        gv.getGridLabelRenderer().resetStyles();
        categoryLabels.removeAllViews();

        //sort trend categories if needed
        if(!trendSorted.containsKey(trend) || !trendSorted.get(trend)) {
            //sort each category
            for(ArrayList<Pair<Float, Date>> subGraph : trend.values()) {
                Collections.sort(subGraph, new DateComparator());
                trendSorted.put(trend, true);
            }
        }


        //update series and labels
        updateBarSeriesAndLabels(trend, pos, len, specificDate);

        gv.addSeries(series);

        //graph styling
        //size
        gv.getViewport().setXAxisBoundsManual(true);

        gv.getViewport().setMinX(0);
        gv.getViewport().setMaxX(numberOfBars + trend.size());


        //labels
        GridLabelRenderer labelRenderer = gv.getGridLabelRenderer();
        labelRenderer.setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        labelRenderer.setNumVerticalLabels(2);
        labelRenderer.setVerticalLabelsVisible(false);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(gv);
        staticLabelsFormatter.setHorizontalLabels(labels);
        labelRenderer.reloadStyles();
        gv.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        //title
        gv.setTitle(title);

        //update category labels
        for(String category : categoryLabelsAndWeights.keySet()) {
            TextView tv = new TextView(categoryLabels.getContext());
            tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, categoryLabelsAndWeights.get(category)));
            tv.setGravity(Gravity.CENTER);
            tv.setText(category.contains("_") ? "" : category); //"_" represent a spacer
            categoryLabels.addView(tv);
        }
    }

    private void updateBarSeriesAndLabels(HashMap<String, ArrayList<Pair<Float, Date>>> trend, int pos, int len,  Date specificDate){
        int offset = 1;
        ArrayList<String> tmpLabels = new ArrayList<String>();
        ArrayList<DataPoint> tmpDp = new ArrayList<DataPoint>();
        colorMap = new HashMap<Integer, Integer>();
        categoryLabelsAndWeights = new LinkedHashMap<String, Float>();

        //sort categories inside trend
        ArrayList<Map.Entry<String, ArrayList<Pair<Float, Date>>>> entries = new ArrayList<Map.Entry<String, ArrayList<Pair<Float, Date>>>>(trend.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, ArrayList<Pair<Float, Date>>>>() {
            @Override
            public int compare(Map.Entry<String, ArrayList<Pair<Float, Date>>> lhs, Map.Entry<String, ArrayList<Pair<Float, Date>>> rhs) {
                if(lhs.getKey().equals(INTEGRATED)) return 1; //integrated always last
                if(rhs.getKey().equals(INTEGRATED)) return -1;
                return - lhs.getKey().compareTo(rhs.getKey()); //the rest are reverese alphabetical
            }
        });
        trend = new LinkedHashMap<String, ArrayList<Pair<Float, Date>>>();
        for(Map.Entry<String, ArrayList<Pair<Float, Date>>> entry : entries) {
            trend.put(entry.getKey(), entry.getValue());
        }


        //fill bars
        tmpLabels.add(" ");
        categoryLabelsAndWeights.put("_" + Integer.toString(offset), 0.5f);

        //iterate categories and update series
        int category = 0;
        numberOfBars = 0;
        for(String categoryName : trend.keySet()) {
            ArrayList<Pair<Float, Date>> subGraph = trend.get(categoryName);
            //add data
            for (int i = 0 ; i < subGraph.size() ; i++) {

                //add point if its in range OR if date isn't null and matches current date
                boolean appendDataPoint = false;
                if(specificDate == null) {
                    if ((i >= pos && i < pos + len)) {
                        appendDataPoint = true;
                    }
                }
                else {
                    if(specificDate.equals(subGraph.get(i).second)){
                            appendDataPoint = true;
                    }
                }


                if(appendDataPoint) {
                    tmpLabels.add(sdf.format(subGraph.get(i).second));
                    tmpDp.add(new DataPoint(offset, Float.isNaN(subGraph.get(i).first) ? 0 : subGraph.get(i).first));
                    colorMap.put(offset, category);
                    offset++;
                    numberOfBars++;
                    categoryLabelsAndWeights.put(categoryName,
                            categoryLabelsAndWeights.containsKey(categoryName) ?
                                    categoryLabelsAndWeights.get(categoryName) + 1 : 1f);
                }
            }

            //add empty bar as spacer
            if(category < trend.size() -1) {
                tmpDp.add(new DataPoint(offset, 0f));
                tmpLabels.add(" ");
                categoryLabelsAndWeights.put("_" + Float.toString(offset), 1f);
                offset++;
            }
            category++;
        }

        categoryLabelsAndWeights.put("_" + Integer.toString(offset), 0.5f);

        tmpLabels.add(" ");
        labels = tmpLabels.toArray(new String[tmpLabels.size()]);
        series = new BarGraphSeries<DataPoint>(tmpDp.toArray(new DataPoint[tmpDp.size()]));

        //series styling
        //color change callback
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                int intPos = (int) (data.getX());
                if(colorMap.containsKey(intPos)) {
                    return getSubGraphColor(colorMap.get(intPos));
                }
                return getSubGraphColor(0);
            }
        });

        series.setSpacing(50);
    }


    private int getSubGraphColor(int index){
        return subGraphColors[index % subGraphColors.length];
    }

    public void addAllSessionData(ArrayList<AlzTestSessionStatistics> stats) {
        for (AlzTestSessionStatistics stat : stats) {
            addSessionData(stat);
        }
    }


    private class DateComparator implements Comparator<Pair<Float, Date>> {

        @Override
        public int compare(Pair<Float, Date> lhs, Pair<Float, Date> rhs) {
            return lhs.second.compareTo(rhs.second);
        }
    }


    public int getNumberOfSessions() {
        return numberOfSessions;
    }

    public void resetData() {
        numberOfSessions = 0;
        numberOfBars = 0;
        numberOfCategories = 0;

        successDataTrends = new LinkedHashMap<String, ArrayList<Pair<Float, Date>>>();
        responseDataTrends = new LinkedHashMap<String, ArrayList<Pair<Float, Date>>>();
        sucDivRespDataTrends = new LinkedHashMap<String, ArrayList<Pair<Float, Date>>>();

        trendSorted = new HashMap<HashMap<String, ArrayList<Pair<Float, Date>>>, Boolean>();
        series = new BarGraphSeries<DataPoint>();
    }
}
