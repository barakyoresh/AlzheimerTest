/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Stimuli;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.alztest.alztest.R;

/**
 * Created by Barak Yoresh on 29/11/2014.
 */
public class StimuliTableFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_stimuli_table, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        System.out.println("stimuli list trying to add buttons");
        inflater.inflate(R.menu.stimuli_list_actions, menu);

        //Search action
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.RIGHT));
        searchView.setQueryHint(getResources().getString(R.string.search_title));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("Search Query change - " + newText);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openUploadDialog() {
        System.out.println("uploading now");
        UploadDialog ud = new UploadDialog();
        ud.show(getFragmentManager(), getString(R.string.upload_stimuli));
    }
}
