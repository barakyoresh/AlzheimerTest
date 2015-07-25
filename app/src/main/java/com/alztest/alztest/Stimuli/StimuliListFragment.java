/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Stimuli;

import android.app.ActionBar;
import android.app.Activity;
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
import android.widget.Toast;

import com.alztest.alztest.Dialogs.AddDialog;
import com.alztest.alztest.Dialogs.ClearDialog;
import com.alztest.alztest.Dialogs.DeleteDialog;
import com.alztest.alztest.Dialogs.EditDialog;
import com.alztest.alztest.Dialogs.FileDialogCallback;
import com.alztest.alztest.Dialogs.SaveDialog;
import com.alztest.alztest.Dialogs.UploadDialog;
import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;

import java.io.File;

import static com.alztest.alztest.Stimuli.StimuliBrain.appendStimuliToDbFromExternalFile;

/**
 * Created by Barak Yoresh on 29/11/2014.
 */
public class StimuliListFragment extends Fragment{


    public static StimulusListAdapter sAdapter;
    private static ListView stimuliListView;
    public static final String SEARCH_QUERY = "STIM_SEARCH_QUERY";
    private String searchQuery = "";
    private String savedSearchQuery = "";
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_stimuli_list, container, false);
        if(savedInstanceState != null && savedInstanceState.containsKey(SEARCH_QUERY)) {
            savedSearchQuery = (String) savedInstanceState.get(SEARCH_QUERY);
        }
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
                openEditDialog(((Stimulus)sAdapter.getItem(position)));
            }
        });

        stimuliListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                openDeleteDialog(((Stimulus) sAdapter.getItem(position)));
                return true;
            }
        });
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu items for use in the action bar
        Log.v(OptionListActivity.APPTAG, "stimuli list trying to add buttons");
        inflater.inflate(R.menu.stimuli_list_actions, menu);

        //Search action
        searchView = (SearchView) menu.findItem(R.id.action_stim_search).getActionView();
        searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.RIGHT));
        searchView.setQueryHint(getResources().getString(R.string.search_title_stimuli));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.v(OptionListActivity.APPTAG, "Search Query submit " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.v(OptionListActivity.APPTAG, "Search Query change - " + newText);
                if(sAdapter != null) {
                    searchQuery = newText;
                    sAdapter.showStimuliSubset(newText);
                    invalidateList();
                }
                return false;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.v(OptionListActivity.APPTAG, "stim prepare menu, setting view to " + savedSearchQuery);
        searchView.setIconified((savedSearchQuery == null) || savedSearchQuery.equals(""));
        searchView.setQuery(savedSearchQuery, true);
        searchView.clearFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_upload:
                openUploadDialog();
                return true;
            case R.id.action_stim_search:
                return false;
            case R.id.action_clear:
                openClearDialog();
                return true;
            case R.id.action_add:
                openAddDialog();
                return true;
            case R.id.action_save:
                openSaveDialog();
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
        ud.setCallback(new FileDialogCallback() {
            @Override
            public void onChooseFile(Activity activity, File file) {
                boolean operationSuccesful;
                operationSuccesful = appendStimuliToDbFromExternalFile(file);
                if (operationSuccesful) {
                    StimuliListFragment.upDateListFromDB();
                }
                Log.v(OptionListActivity.APPTAG, operationSuccesful ? "success!" : "failed :(");
                Toast toast = Toast.makeText(getActivity(), operationSuccesful ? "Loaded file successfully" : "Loading file failed :(", Toast.LENGTH_SHORT);
                toast.show();

            }
        });
        ud.show(getFragmentManager(), getString(R.string.upload_stimuli));
    }

    private void openSaveDialog() {
        Log.v(OptionListActivity.APPTAG, "saving now");
        SaveDialog sd = new SaveDialog();
        sd.setCallback(new FileDialogCallback() {
            @Override
            public void onChooseFile(Activity activity, File file) {
                Log.v(OptionListActivity.APPTAG, "saving file - " + file.getAbsolutePath());
                boolean operationSuccesful = StimuliBrain.saveStimuliToFile(sAdapter.stimuli, file);
                Log.v(OptionListActivity.APPTAG, operationSuccesful ? "success!" : "failed :(");
                Toast toast = Toast.makeText(getActivity(), operationSuccesful ? "File saved successfully" : "File saving failed :(", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        sd.show(getFragmentManager(), getString(R.string.save_stimuli));
    }

    private void openClearDialog() {
        Log.v(OptionListActivity.APPTAG, "clearing now");
        ClearDialog ud = new ClearDialog();
        ud.show(getFragmentManager(), getString(R.string.clear_stimuli));
    }

    private void openEditDialog(Stimulus s) {
        Log.v(OptionListActivity.APPTAG, "editing now");
        EditDialog ud = new EditDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(EditDialog.STIMULI_TO_EDIT, s.hashCode());
        Log.v(OptionListActivity.APPTAG, "putting arg - " + s.hashCode());
        ud.setArguments(bundle);
        ud.show(getFragmentManager(), getString(R.string.edit_stimulus));
    }

    private void openAddDialog() {
        Log.v(OptionListActivity.APPTAG, "adding now");
        AddDialog ud = new AddDialog();
        ud.show(getFragmentManager(), getString(R.string.action_add));
    }

    private void openDeleteDialog(Stimulus s) {
        Log.v(OptionListActivity.APPTAG, "deleting now");
        DeleteDialog dd = new DeleteDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(DeleteDialog.STIMULI_TO_DELETE, s.hashCode());
        dd.setArguments(bundle);
        dd.show(getFragmentManager(), getString(R.string.action_delete));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SEARCH_QUERY, savedSearchQuery);
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onPause() {
        Log.v(OptionListActivity.APPTAG, "stim pause, saving " + searchQuery);
        savedSearchQuery = searchQuery;
        searchView.setQuery("", false);
        super.onPause();
    }

}
