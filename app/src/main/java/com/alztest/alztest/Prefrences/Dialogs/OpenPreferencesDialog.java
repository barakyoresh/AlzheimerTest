/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Prefrences.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.alztest.alztest.Prefrences.AlzTestPrefrencesFragment;
import com.alztest.alztest.Prefrences.AlzTestUserPrefs;
import com.alztest.alztest.Toolbox.AlzTestPreferencesManager;

import java.util.ArrayList;


/**
 * Created by Barak on 17/12/2014.
 */
public class OpenPreferencesDialog extends DialogFragment {
    //TODO: Add delete option

    String currentChoice = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final AlzTestPreferencesManager prefsManager = new AlzTestPreferencesManager(getActivity());

        builder.setTitle("Load Saved Preferences");

        final ArrayList<String> savedPrefs = prefsManager.getSavedPreferencesSetNames();

        if(savedPrefs != null && savedPrefs.size() > 0){
            // Set up the list options
            builder.setSingleChoiceItems(savedPrefs.toArray(new CharSequence[savedPrefs.size()]), 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    currentChoice = savedPrefs.get(which);
                }
            });

            // positive button
            builder.setPositiveButton("Load", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlzTestUserPrefs prefs = prefsManager.getPreferenceSet(currentChoice);
                    if(prefs != null){
                        ((AlzTestPrefrencesFragment) getTargetFragment()).upDateWidgetsWithPrefs(prefs);
                        Toast toast = Toast.makeText(getActivity(), "Loaded: " + currentChoice, Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(getActivity(), "Corrupt Data", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }else{
            builder.setMessage("There are no saved preferences.");
        }




        // cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();

    }
}
