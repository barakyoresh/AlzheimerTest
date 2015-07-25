/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Statistics;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;
import com.alztest.alztest.Toolbox.AlzTestDatabaseManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

//import com.alztest.alztest.Stimuli.SortCriteria;


enum SortCriteria{Date, Name, ID};

public class StatisticsListAdapter extends BaseAdapter {
    public ArrayList<AlzTestSessionStatistics> stats = new ArrayList<AlzTestSessionStatistics>();
    private LayoutInflater inflater;
    private SortCriteria sortedBy = SortCriteria.Name;
    private boolean sortedAscending = true;
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm dd/MM/yyyy");

    public StatisticsListAdapter(Context context){
        super();

        Log.v(OptionListActivity.APPTAG, "created adapter");

        //populate list
        inflater = LayoutInflater.from(context);

        try{
            Log.v(OptionListActivity.APPTAG, "populating list");
            stats = (ArrayList<AlzTestSessionStatistics>) AlzTestDatabaseManager.getInstance().getHelper().getAlzTestSessionStatisticsDao().queryForAll();
            Log.v(OptionListActivity.APPTAG, "Stat list: " + stats);
        }catch (SQLException e){
            e.printStackTrace();
        }

        sortList();

    }

    /**
     * Updates a list entry, if id is null, it adds the entry as a new one
     * @param id id of entry to alter
     * @param stat new entry to replace old one
     * @throws java.sql.SQLException
     */
    public void updateEntry(Date id, AlzTestSessionStatistics stat) throws SQLException{
        //DB handle
        Dao<AlzTestSessionStatistics, Date> statsDao = AlzTestDatabaseManager.getInstance().getHelper().getAlzTestSessionStatisticsDao();

        if(id != null) {
            //delete in db
            statsDao.deleteById(id);

            //update in local list in-place
            for(AlzTestSessionStatistics s : stats){
                if(statsDao.extractId(s).equals(id)) {
                    stats.add(stats.indexOf(s) , stat);
                    stats.remove(s);
                    break;
                }
            }
        }else {
            //add to local list
            stats.add(stat);
        }

        //add to db
        statsDao.create(stat);

        //update view
        StatisticsListFragment.invalidateList();

    }


    private void sortList() {
        Comparator<AlzTestSessionStatistics> comparator = new dateComparator();
        switch(sortedBy){
            case ID:
                comparator = new IDComparator();
                break;
            case Date:
                comparator = new dateComparator();
                break;
            case Name:
                comparator = new nameComparator();
            default:
                break;
        }

        Collections.sort(stats, comparator);
        if(!sortedAscending){
            Collections.reverse(stats);
        }
        notifyDataSetChanged();
    }

    public void clearList(){
        stats.clear();
        notifyDataSetChanged();
    }

    public void upDateListFromDB(){
        try{
            Log.v(OptionListActivity.APPTAG, "populating list");
            stats = (ArrayList<AlzTestSessionStatistics>) AlzTestDatabaseManager.getInstance().getHelper().getAlzTestSessionStatisticsDao().queryForAll();
        }catch (SQLException e){
            e.printStackTrace();
        }
        //sortList();
        notifyDataSetChanged();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return stats.size();
    }

    @Override
    public Object getItem(int position) {
        return stats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if( convertView == null ){
            convertView = inflater.inflate(R.layout.fragment_stimuli_list_entry, parent, false);
        }
        TextView date = (TextView) convertView.findViewById(R.id.list_entry_stimulus_category);
        TextView name = (TextView) convertView.findViewById(R.id.list_entry_stimulus_name);
        TextView id = (TextView) convertView.findViewById(R.id.list_entry_stimulus_value);


        date.setText(sdf.format(stats.get(position).getSessionStartTime()));
        name.setText(stats.get(position).getSubjectName());
        id.setText(Long.toString(stats.get(position).getSubjectId()));

        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stats.isEmpty();
    }


    public void sort(SortCriteria s){
        if (s == sortedBy) {
            sortedAscending = !sortedAscending;
        }
        else{
            sortedBy = s;
            sortedAscending = true;
        }
        Log.v(OptionListActivity.APPTAG, "sorting by " + sortedBy.name() + (sortedAscending ? " acending" : " decending"));
        sortList();
        notifyDataSetChanged();
        return;
    }

    public class dateComparator implements Comparator<AlzTestSessionStatistics>{
        @Override
        public int compare(AlzTestSessionStatistics lhs, AlzTestSessionStatistics rhs) {
            return lhs.getSessionStartTime().compareTo(rhs.getSessionStartTime());
        }
    }

    public class nameComparator implements Comparator<AlzTestSessionStatistics>{
        @Override
        public int compare(AlzTestSessionStatistics lhs, AlzTestSessionStatistics rhs) {
            return lhs.getSubjectName().compareTo(rhs.getSubjectName());
        }
    }

    public class IDComparator implements Comparator<AlzTestSessionStatistics>{
        @Override
        public int compare(AlzTestSessionStatistics lhs, AlzTestSessionStatistics rhs) {
            Long result = lhs.getSubjectId() - rhs.getSubjectId();
            if(result != 0) {
                return result > 0 ? 1 : 0;
            }
            return 0;
        }
    }

    public void showStatisticsSubset(String query) {
        Log.v(OptionListActivity.APPTAG, "showing stats subset according to query: " + query);
        try {
            //get dao
            Dao<AlzTestSessionStatistics, Date> dao = AlzTestDatabaseManager.getInstance().getHelper().getAlzTestSessionStatisticsDao();

            //build query
            QueryBuilder<AlzTestSessionStatistics, Date> qb = dao.queryBuilder();
            String mQuery = '%' + query + '%';
            Where where = qb.where();
            where.like("subjectName", mQuery);
            //if its numerify-able, search for value as well
            try {
                int val = Integer.parseInt(query);
                where.or().eq("subjectId", val);
            } catch (NumberFormatException e) {}

            //search
            PreparedQuery<AlzTestSessionStatistics> preparedQuery = qb.prepare();
            stats = (ArrayList<AlzTestSessionStatistics>) dao.query(preparedQuery);

            //sort
            sortList();
        } catch (SQLException e) {
            Log.e(OptionListActivity.APPTAG, "showStimuliSubset SQLExeption" );
            e.printStackTrace();
        }
    }
}

