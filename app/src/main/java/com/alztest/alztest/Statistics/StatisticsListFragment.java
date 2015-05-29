/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Statistics;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;
import com.alztest.alztest.Dialogs.ClearDialog;
import com.alztest.alztest.Dialogs.UploadDialog;
import com.alztest.alztest.Toolbox.AlzTestSerializeManager;

/**
 * Created by Barak Yoresh on 29/11/2014.
 */
public class StatisticsListFragment extends Fragment{

    public static final String STATISTIC = "statistic";
    public static StatisticsListAdapter sAdapter;
    private static ListView stimuliListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_statistics_list, container, false);
        setHasOptionsMenu(true);

        //populate list
        if(sAdapter == null){
            sAdapter = new StatisticsListAdapter(getActivity());
    }

        stimuliListView = (ListView) rootView.findViewById(R.id.statistics_list);
        stimuliListView.setAdapter(sAdapter);


        //Add sorters click listener
        rootView.findViewById(R.id.legend_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(OptionListActivity.APPTAG, "clicked Date!");
                sAdapter.sort(SortCriteria.Date);
                invalidateList();
            }
        });
        rootView.findViewById(R.id.legend_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(OptionListActivity.APPTAG, "clicked Name!");
                sAdapter.sort(SortCriteria.Name);
                invalidateList();
            }
        });
        rootView.findViewById(R.id.legend_ID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(OptionListActivity.APPTAG, "clicked ID!");
                sAdapter.sort(SortCriteria.ID);
                invalidateList();
            }
        });


        stimuliListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openReadMoreDialog(((AlzTestSessionStatistics)sAdapter.getItem(position)));
            }
        });
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        Log.v(OptionListActivity.APPTAG, "statistics list trying to add buttons");
        inflater.inflate(R.menu.statistics_list_actions, menu);

        //Search action
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.RIGHT));
        searchView.setQueryHint(getResources().getString(R.string.search_title_statistics));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.v(OptionListActivity.APPTAG, "Search Query change - " + newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_export:
                openExportDialog();
                return true;
            case R.id.action_search:
                return false;
            case R.id.action_clear:
                openClearDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static void upDateListFromDB() {
        if (sAdapter != null && stimuliListView != null) {
            sAdapter.upDateListFromDB();
            invalidateList();
            return;
        } else {
            Log.v(OptionListActivity.APPTAG, "Null parameters!");
        return;
        }
    }

    public static void clearList() {
        if (sAdapter != null && stimuliListView != null) {
            sAdapter.clearList();
            invalidateList();
            return;
        } else {
            Log.v(OptionListActivity.APPTAG, "Null parameters!");
            return;
        }
    }

    public static void invalidateList(){
        if(stimuliListView != null)
        {
            stimuliListView.invalidateViews();
        }
        if(sAdapter != null)
        {
            sAdapter.notifyDataSetChanged();
        }
    }


    private void openExportDialog() {
        //TODO: implement export, also migrate to stimuli once done
        Log.v(OptionListActivity.APPTAG, "uploading now");
        UploadDialog ud = new UploadDialog();
        ud.show(getFragmentManager(), getString(R.string.upload_stimuli));
    }


    private void openClearDialog() {
        Log.v(OptionListActivity.APPTAG, "clearing now");
        ClearDialog ud = new ClearDialog();
        ud.show(getFragmentManager(), getString(R.string.clear_stimuli));
    }

    private void openReadMoreDialog(AlzTestSessionStatistics stat) {
        //TODO: open stats view
        Intent intent = new Intent(getActivity(), StatisticsActivity.class);
        intent.putExtra(STATISTIC, AlzTestSerializeManager.serialize(stat));
        startActivity(intent);
    }

}
