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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;
import com.alztest.alztest.Stimuli.StimuliListFragment;
import com.alztest.alztest.Stimuli.Stimulus;


/**
 * Created by Barak Yoresh on 10/02/2015.
 */
public class AddDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Stimuli");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_stimulus, null);
        final EditText category = (EditText) dialogView.findViewById(R.id.editTextCategory);
        final EditText name = (EditText) dialogView.findViewById(R.id.editTextName);
        final EditText value = (EditText) dialogView.findViewById(R.id.editTextValue);
        builder.setView(dialogView);
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String catStr = category.getText().toString();
                String nameStr = name.getText().toString();
                String valStr = value.getText().toString();
                if(catStr.length() > 0 && nameStr.length() > 0 && valStr.length() > 0)
                try {
                    int value = Integer.parseInt(valStr);

                    StimuliListFragment.sAdapter.updateEntry(null, new Stimulus(nameStr, catStr, value));
                }catch(Exception e)
                {
                    //TODO: open invalid value dialog
                    Log.v(OptionListActivity.APPTAG, "DAO update failed - ");
                    e.printStackTrace();
                    return;
                }


                return;
            }
        });

        return builder.create();
    }
}

