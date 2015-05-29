/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Session;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;

/**
 * Created by Barak Yoresh on 28/11/2014.
 */
public class NewSessionFragment extends Fragment {
    public static final String SUBJECT_NAME = "subject_name";
    public static final String SUBJECT_ID = "subject_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_start_new_session, container, false);
        final EditText nameView = (EditText) rootView.findViewById(R.id.ET_subject_name);
        final EditText idView = (EditText) rootView.findViewById(R.id.ET_subject_id);
        final Button startBtn = (Button) rootView.findViewById(R.id.btnStartSession);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SessionActivity.class);


                //put extra data
                String subjectName = nameView.getText().toString();
                String subjectIdStr = idView.getText().toString();
                long subjectId = 0;
                try {
                    subjectId = Long.decode(subjectIdStr);
                } catch (Exception e) {
                    Log.e(OptionListActivity.APPTAG, "subject id decoding exception!");
                }


                intent.putExtra(SUBJECT_NAME, subjectName);
                intent.putExtra(SUBJECT_ID, subjectId);
                startActivity(intent);
            }
        });

        //make button unclickable until name and id are aparant
        checkAndSetButtonClickability(startBtn, nameView.getText().toString(), idView.getText().toString());
        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    checkAndSetButtonClickability(startBtn, nameView.getText().toString(), idView.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        idView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    checkAndSetButtonClickability(startBtn, nameView.getText().toString(), idView.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return rootView;
    }

    private void checkAndSetButtonClickability(Button startButton, String name, String id) {
        //startButton.setClickable((name.length() > 0 && id.length() > 0));
        startButton.setEnabled((name.length() > 0 && id.length() > 0));
    }
}

