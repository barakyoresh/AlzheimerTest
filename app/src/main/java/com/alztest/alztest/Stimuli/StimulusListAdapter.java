/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Stimuli;

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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Barak Yoresh on 13/12/2014.
 */
enum SortCriteria{Category, Name, Value};

public class StimulusListAdapter extends BaseAdapter {
    public ArrayList<Stimulus> stimuli = new ArrayList<Stimulus>();
    protected LayoutInflater inflater;
    private SortCriteria sortedBy = SortCriteria.Name;
    private boolean sortedAscending = true;

    public StimulusListAdapter(Context context){
        super();

        Log.v(OptionListActivity.APPTAG, "created adapter");

        //populate list
        inflater = LayoutInflater.from(context);

        try{
            Log.v(OptionListActivity.APPTAG, "populating list");
            stimuli = (ArrayList<Stimulus>) AlzTestDatabaseManager.getInstance().getHelper().getStimuliDao().queryForAll();
        }catch (SQLException e){
            e.printStackTrace();
        }

        sortList();

    }

    /**
     * Updates a list entry, if id is -1, it adds the entry as a new one
     * @param id id of entry to alter
     * @param stimulus new entry to replace old one
     * @throws SQLException
     */
    public void updateEntry(int id, Stimulus stimulus) throws SQLException{
        //DB handle
        Dao<Stimulus, Integer> stimDao = AlzTestDatabaseManager.getInstance().getHelper().getStimuliDao();

        if(id != -1) {
            //delete in db
            stimDao.deleteById(id);

            //update in local list in-place
            for(Stimulus s : stimuli){
                if(stimDao.extractId(s).equals(id)) {
                    stimuli.add(stimuli.indexOf(s) , stimulus);
                    stimuli.remove(s);
                    break;
                }
            }
        }else {
            //add to local list
            stimuli.add(stimulus);
        }

        //add to db
        stimDao.create(stimulus);

        //update view
        StimuliListFragment.invalidateList();

    }

    private void sortList() {
        Comparator<Stimulus> comparator = new nameComparator();
        switch(sortedBy){
            case Category:
                comparator = new categroyComparator();
                break;
            case Value:
                comparator = new valueComparator();
                break;
            case Name:
                comparator = new nameComparator();
            default:
                break;
        }

        Collections.sort(stimuli, comparator);
        if(!sortedAscending){
            Collections.reverse(stimuli);
        }
        notifyDataSetChanged();
    }

    public void clearList(){
        stimuli.clear();
        notifyDataSetChanged();
    }

    public void upDateListFromDB(){
        try{
            Log.v(OptionListActivity.APPTAG, "populating list");
            stimuli = (ArrayList<Stimulus>) AlzTestDatabaseManager.getInstance().getHelper().getStimuliDao().queryForAll();
        }catch (SQLException e){
            e.printStackTrace();
        }
        sortList();
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
        return stimuli.size();
    }

    @Override
    public Object getItem(int position) {
        return stimuli.get(position);
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
        TextView category = (TextView) convertView.findViewById(R.id.list_entry_stimulus_category);
        TextView name = (TextView) convertView.findViewById(R.id.list_entry_stimulus_name);
        TextView value = (TextView) convertView.findViewById(R.id.list_entry_stimulus_value);

        category.setText(stimuli.get(position).getCategory());
        name.setText(stimuli.get(position).getName());
        value.setText(Integer.toString(stimuli.get(position).getValue()));

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
        return stimuli.isEmpty();
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

    public class categroyComparator implements Comparator<Stimulus>{
        @Override
        public int compare(Stimulus lhs, Stimulus rhs) {
            return lhs.getCategory().compareTo(rhs.getCategory());
        }
    }

    public class nameComparator implements Comparator<Stimulus>{
        @Override
        public int compare(Stimulus lhs, Stimulus rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    public class valueComparator implements Comparator<Stimulus>{
        @Override
        public int compare(Stimulus lhs, Stimulus rhs) {
            return lhs.getValue() - rhs.getValue();
        }
    }
}

