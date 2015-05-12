/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Prefrences.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alztest.alztest.Prefrences.AlzTestPrefrencesFragment;
import com.alztest.alztest.R;
import com.alztest.alztest.Stimuli.StimuliListFragment;
import com.alztest.alztest.Stimuli.Stimulus;
import com.alztest.alztest.Toolbox.AlzTestDatabaseManager;
import com.alztest.alztest.Toolbox.AlzTestPreferencesManager;
import com.j256.ormlite.dao.Dao;


/**
 * Created by Barak on 17/12/2014.
 */
public class SavePreferencesDialog extends DialogFragment {
    //TODO: show loadable names for override purposes.

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Save Current Preferences");

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();

                if(name.length() > 0) {
                    AlzTestPreferencesManager prefsManager = new AlzTestPreferencesManager(getActivity());
                    //TODO: Optional, promt override message

                    prefsManager.setPreferenceSet(name, prefsManager.getLastSavedPreferencesSet());
                    Toast toast = Toast.makeText(getActivity(), "Preference Saved As: " + name, Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    //TODO: promt usage dialog
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();

    }
}
