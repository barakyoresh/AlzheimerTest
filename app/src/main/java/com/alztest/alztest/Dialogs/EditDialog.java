/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;
import com.alztest.alztest.Stimuli.StimuliListFragment;
import com.alztest.alztest.Stimuli.Stimulus;
import com.alztest.alztest.Toolbox.AlzTestDatabaseManager;
import com.j256.ormlite.dao.Dao;


/**
 * Created by Barak on 17/12/2014.
 */
public class EditDialog extends DialogFragment {

    public static final String STIMULI_TO_EDIT = "STIMULI_TO_EDIT";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int stimuliHash = 0;
        if (bundle != null && bundle.containsKey(STIMULI_TO_EDIT)) {
            Log.v(OptionListActivity.APPTAG, "got Stimuli name to Edit! + " + bundle.getInt(STIMULI_TO_EDIT));
            stimuliHash = bundle.getInt(STIMULI_TO_EDIT);
        }
        final Dao<Stimulus, Integer> stimDao = AlzTestDatabaseManager.getInstance().getHelper().getStimuliDao();
        Stimulus s = null;
        try {
            if (stimDao.idExists(stimuliHash)) {
                s = stimDao.queryForId(stimuliHash);
            }
        } catch (Exception e) {
            Log.v(OptionListActivity.APPTAG, "queryForId raised exception -");
            e.printStackTrace();
        }

        if (s != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Edit Stimuli");

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_edit_stimulus, null);
            final EditText category = (EditText) dialogView.findViewById(R.id.editTextCategory);
            final EditText name = (EditText) dialogView.findViewById(R.id.editTextName);
            final EditText value = (EditText) dialogView.findViewById(R.id.editTextValue);
            category.setHint((CharSequence) s.getCategory());
            name.setHint((CharSequence) s.getName());
            value.setHint((CharSequence) Integer.toString(s.getValue()));
            final Stimulus stimToAdd = s;  //workarround for button-access
            builder.setView(dialogView);

            builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            builder.setPositiveButton(R.string.Okay, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String text = category.getText().toString();
                    boolean altered = false;
                    int sID = -1;
                    try {
                        sID = stimDao.extractId(stimToAdd);
                    }catch(Exception e){
                        e.printStackTrace();
                        Log.v(OptionListActivity.APPTAG, "extractId failed!");
                    }
                    if(text.length() > 0) {
                        Log.v(OptionListActivity.APPTAG, "updating Category to - " + text);
                        stimToAdd.setCategory(text);
                        altered = true;
                    }
                    text = name.getText().toString();
                    if(text.length() > 0) {
                        Log.v(OptionListActivity.APPTAG, "updating Name to - " + text);
                        stimToAdd.setName(text);
                        altered = true;
                    }
                    text = value.getText().toString();
                    if(text.length() > 0) {
                        try {
                            int value = Integer.parseInt(text);
                            Log.v(OptionListActivity.APPTAG, "updating Value to - " + value);
                            stimToAdd.setValue(value);
                            altered = true;
                        }catch(Exception e)
                        {
                            //TODO: open invalid value dialog
                            return;
                        }
                    }
                    try {
                        //if the data was changed
                        if(altered){
                            if(sID != -1){
                                StimuliListFragment.sAdapter.updateEntry(sID, stimToAdd);
                            }
                        }
                    }catch(Exception e){
                        Log.v(OptionListActivity.APPTAG, "DAO update failed - ");
                        e.printStackTrace();
                    }
                    return;
                }
            });

            return builder.create();

        } else {
            return super.onCreateDialog(savedInstanceState);
        }
    }
}
