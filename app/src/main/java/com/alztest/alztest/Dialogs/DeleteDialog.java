/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;
import com.alztest.alztest.Stimuli.StimuliListFragment;
import com.alztest.alztest.Stimuli.Stimulus;
import com.alztest.alztest.Toolbox.AlzTestDatabaseManager;
import com.j256.ormlite.dao.Dao;


/**
 * Created by Barak on 17/12/2014.
 */
public class DeleteDialog extends DialogFragment {

    public static final String STIMULI_TO_DELETE = "STIMULI_TO_DELETE";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int stimuliHash = 0;
        if (bundle != null && bundle.containsKey(STIMULI_TO_DELETE)) {
            Log.v(OptionListActivity.APPTAG, "got Stimuli name to Delete! + " + bundle.getInt(STIMULI_TO_DELETE));
            stimuliHash = bundle.getInt(STIMULI_TO_DELETE);
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
            final Stimulus stimToDelete = s;  //workarround for button-access
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(s.getCategory() + "\t|\t" + s.getName() + "\t|\t" + Integer.toString(s.getValue()))
                    .setMessage(Html.fromHtml("Are you sure you want to permanently delete <b>" + s.getName() + "</b>?\nThis operation cannot be undone."))
                    //.setMessage("Are you sure you want to permanently delete " + s.getName() + "?\nThis operation cannot be undone.")
                    .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .setPositiveButton(R.string.IAmSure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                stimDao.delete(stimToDelete);
                                StimuliListFragment.upDateListFromDB();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.v(OptionListActivity.APPTAG, "deletion failed!");
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
