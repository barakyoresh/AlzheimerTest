/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.Prefrences.AlzTestUserPrefs;
import com.alztest.alztest.R;
import com.alztest.alztest.Stimuli.SpecificStimulusListAdapter;

import java.util.HashSet;

/**
 * Created by user on 14/07/2015.
 */
public class SpecificStimuliDialog extends DialogFragment {
    AlzTestUserPrefs userPrefs;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(userPrefs == null) {
            Log.e(OptionListActivity.APPTAG, "SpecificStimuliDialog: stimuli and userPrefs not initiated");
            return null;
        }

        final SpecificStimulusListAdapter stimulusAdapter = new SpecificStimulusListAdapter(getActivity(),  new HashSet<Integer>(userPrefs.getSpecificStimuliSubsetIndecies()));
        ListView ls = new ListView(getActivity());
        ls.setAdapter(stimulusAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("Choose Specific Stimuli")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setView(ls)
                        // Set the action buttons
                .setPositiveButton(R.string.Okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        userPrefs.setSpecificStimuliSubsetIndecies(stimulusAdapter.getSelected());
                    }
                })
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) { }
                });

        return builder.create();
    }


    public void setUserPrefs(AlzTestUserPrefs userPrefs) {
        this.userPrefs = userPrefs;
    }
}
