/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Stimuli;

import android.app.ActionBar;
import android.app.Fragment;
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
import com.alztest.alztest.Stimuli.Dialogs.AddDialog;
import com.alztest.alztest.Stimuli.Dialogs.ClearDialog;
import com.alztest.alztest.Stimuli.Dialogs.EditDialog;
import com.alztest.alztest.Stimuli.Dialogs.UploadDialog;

/**
 * Created by Barak Yoresh on 29/11/2014.
 */
public class StimuliListFragment extends Fragment{

    public static StimulusListAdapter sAdapter;
    private static ListView stimuliListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_stimuli_list, container, false);
        setHasOptionsMenu(true);

        //populate list
        if(sAdapter == null){
            sAdapter = new StimulusListAdapter(getActivity());
        }

        stimuliListView = (ListView) rootView.findViewById(R.id.stimuli_list);
        stimuliListView.setAdapter(sAdapter);


        //Add sorters click listener
        rootView.findViewById(R.id.legend_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(OptionListActivity.APPTAG, "clicked Category!");
                sAdapter.sort(SortCriteria.Category);
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
        rootView.findViewById(R.id.legend_value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(OptionListActivity.APPTAG, "clicked Value!");
                sAdapter.sort(SortCriteria.Value);
                invalidateList();
            }
        });

        stimuliListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openEditDialog(((Stimulus)sAdapter.getItem(position)).getName());
            }
        });
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        Log.v(OptionListActivity.APPTAG, "stimuli list trying to add buttons");
        inflater.inflate(R.menu.stimuli_list_actions, menu);

        //Search action
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.RIGHT));
        searchView.setQueryHint(getResources().getString(R.string.search_title_stimuli));
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
            case R.id.action_upload:
                openUploadDialog();
                return true;
            case R.id.action_search:
                return false;
            case R.id.action_clear:
                openClearDialog();
                return true;
            case R.id.action_add:
                openAddDialog();
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

    private void openUploadDialog() {
        Log.v(OptionListActivity.APPTAG, "uploading now");
        UploadDialog ud = new UploadDialog();
        ud.show(getFragmentManager(), getString(R.string.upload_stimuli));
    }

    private void openClearDialog() {
        Log.v(OptionListActivity.APPTAG, "clearing now");
        ClearDialog ud = new ClearDialog();
        ud.show(getFragmentManager(), getString(R.string.clear_stimuli));
    }

    private void openEditDialog(String stimulusName) {
        Log.v(OptionListActivity.APPTAG, "editing now");
        EditDialog ud = new EditDialog();
        Bundle bundle = new Bundle();
        bundle.putString(EditDialog.STIMULI_TO_EDIT, stimulusName);
        Log.v(OptionListActivity.APPTAG, "putting arg - " + stimulusName);
        ud.setArguments(bundle);
        ud.show(getFragmentManager(), getString(R.string.edit_stimulus));
    }

    private void openAddDialog() {
        Log.v(OptionListActivity.APPTAG, "adding now");
        AddDialog ud = new AddDialog();
        Bundle bundle = new Bundle();
        ud.show(getFragmentManager(), getString(R.string.action_add));
    }
}
