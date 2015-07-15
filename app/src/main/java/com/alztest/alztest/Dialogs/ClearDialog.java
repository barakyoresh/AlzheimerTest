/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.alztest.alztest.R;
import com.alztest.alztest.Stimuli.StimuliBrain;
import com.alztest.alztest.Stimuli.StimuliListFragment;

/**
 * Created by Barak Yoresh on 17/12/2014.
 */
public class ClearDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are You sure you want to delete all stimuli?");
        builder.setMessage("This operation cannot be undone.");
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setPositiveButton("I am sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StimuliBrain.clearDB();
                StimuliListFragment.clearList();
                return;
            }
        });

        return builder.create();
        //return super.onCreateDialog(savedInstanceState);
    }
}
