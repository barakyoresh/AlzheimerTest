/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Prefrences;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alztest.alztest.Dialogs.FileDialogCallback;
import com.alztest.alztest.Dialogs.SaveDialog;
import com.alztest.alztest.Dialogs.UploadDialog;
import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;
import com.alztest.alztest.Toolbox.AlzTestPreferencesManager;
import com.alztest.alztest.Toolbox.SerializeManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Barak Yoresh on 11/02/2015.
 */
public class AlzTestPrefrencesFragment extends Fragment {
    private AlzTestPreferencesManager prefsManager = null;
    private AlzTestUserPrefs userPrefs = null;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_prefrences, container, false);
        setHasOptionsMenu(true);

        //load last userPrefs
        prefsManager = new AlzTestPreferencesManager(getActivity());
        userPrefs = prefsManager.getCachedPreferencesSet();
        upDateWidgetsWithPrefs(userPrefs);


        return rootView;
    }

    public void upDateWidgetsWithPrefs(final AlzTestUserPrefs userPrefs) {
        this.userPrefs = userPrefs;

        //prefs widgets
        // category selection widget
        setupCategorySelectionWidget();

        // minmax value difference widget
        setupMinMaxValueDifferenceWidget();

        // number of stimuli widget
        setupNumOfStimWidget();

        setupSessionCountdownWidget();

        // dummy widgets
        setupDummyWidgets();
    }

    private void setupSessionCountdownWidget() {
        final Spinner sessCountdown = (Spinner) rootView.findViewById(R.id.sessionCountdownSpinner);
        final ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                userPrefs.getAllSessCountdownTimes());
        sessCountdown.setAdapter(adapter);
        sessCountdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        userPrefs.setCountdownTimerValue(adapter.getItem(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
        sessCountdown.setSelection(userPrefs.getCountdownTimerValuePosition());
    }

    private void setupDummyWidgets() {
        //seekbar
        SeekBar sb = (SeekBar) rootView.findViewById(R.id.seekBar);
        sb.setMax(256);
        sb.setProgress(userPrefs.getScroller());

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int myProgress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                myProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                userPrefs.setScroller(myProgress);
            }
        });

        Switch sw = (Switch) rootView.findViewById(R.id.switch1);
        sw.setChecked(userPrefs.isSwtich());

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userPrefs.setSwtich(isChecked);
            }
        });
    }

    private void setupNumOfStimWidget() {
        final EditText numOfStimPairsET = (EditText) rootView.findViewById(R.id.numberOfStimuliPairsEditText);
        numOfStimPairsET.setText(Integer.toString(userPrefs.getNumberOfPairsInTrial()), TextView.BufferType.EDITABLE);
        numOfStimPairsET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int numOfTrials = userPrefs.getNumberOfPairsInTrial();
                try{
                    numOfTrials = Integer.decode(s.toString());
                }catch (Exception e){
                    return;
                }
                userPrefs.setNumberOfPairsInTrial(numOfTrials);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        Log.v(OptionListActivity.APPTAG, "preference editor trying to add buttons");
        inflater.inflate(R.menu.preferences_actions, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_save_preferences:
                openSaveDialog();
                return true;
            case R.id.action_open_preferences:
                openOpenDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSaveDialog() {
        prefsManager.setPreferenceSet(null, userPrefs);
        SaveDialog sd = new SaveDialog();
        sd.extensionType = "json";
        sd.setCallback(new FileDialogCallback() {
            @Override
            public void onChooseFile(Activity activity, File file) {
                boolean operationSuccessful = true;
                try{
                    PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
                    writer.write(SerializeManager.serialize(userPrefs));
                    writer.flush();
                    writer.close();
                }catch(Exception e){
                    e.printStackTrace();
                    operationSuccessful = false;
                }

                Log.v(OptionListActivity.APPTAG, operationSuccessful ? "success!" : "failed :(");
                Toast toast = Toast.makeText(getActivity(), operationSuccessful ? "Saved Preferences file successfully" : "Saving Preferences file failed :(", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        sd.show(getFragmentManager(), getString(R.string.action_save_preferences));
    }

    private void openOpenDialog() {
        UploadDialog ud = new UploadDialog();
        ud.extensionType = "json";
        ud.setCallback(new FileDialogCallback() {
            @Override
            public void onChooseFile(Activity activity, File file) {
                boolean operationSuccesful = true;

                try{
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    StringBuilder sb = new StringBuilder();
                    String line = reader.readLine();

                    while (line != null) {
                        sb.append(line);
                        line = reader.readLine();
                    }

                    userPrefs = (AlzTestUserPrefs) SerializeManager.deSerialize(sb.toString(), AlzTestUserPrefs.class);

                }catch (Exception e) {
                    e.printStackTrace();
                    operationSuccesful = false;
                }


                if(operationSuccesful) {
                    upDateWidgetsWithPrefs(userPrefs);
                }

                Log.v(OptionListActivity.APPTAG, operationSuccesful ? "success!" : "failed :(");
                Toast toast = Toast.makeText(getActivity(), operationSuccesful ? "Loaded Preferences file successfully" : "Loading Preferences file failed :(", Toast.LENGTH_SHORT);
                toast.show();

            }
        });
        ud.show(getFragmentManager(), getString(R.string.action_load_preferences));
    }

    private void setupMinMaxValueDifferenceWidget() {
        //min
        Spinner minSpinner = (Spinner) rootView.findViewById(R.id.minValueDifferenceSpinner);
        minSpinner.setAdapter(new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                userPrefs.getAllValueDifferences()){

            @Override
            public boolean isEnabled(int position) {
                int maxDiff = userPrefs.getMaximumValueDifference();
                if (position > maxDiff){
                    return false;
                }
                return true;
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                v.setEnabled(isEnabled(position));
                return v;
            }


        });
        minSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userPrefs.setMinimumValueDifference(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        minSpinner.setSelection(userPrefs.getMinimumValueDifference());

        //max
        Spinner maxSpinner = (Spinner) rootView.findViewById(R.id.maxValueDifferenceSpinner);
       // maxSpinner.setAdapter(new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
       //         userPrefs.getAllValueDifferences()));
        maxSpinner.setAdapter(new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                userPrefs.getAllValueDifferences()){

            @Override
            public boolean isEnabled(int position) {
                int minDiff = userPrefs.getMinimumValueDifference();
                if (position < minDiff){
                    return false;
                }
                return true;
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                v.setEnabled(isEnabled(position));
                return v;
            }
        });
        maxSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userPrefs.setMaximumValueDifference(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        maxSpinner.setSelection(userPrefs.getMaximumValueDifference());

    }

    private void setupCategorySelectionWidget() {
        //categories
        final ArrayList<String> categories = ((OptionListActivity) getActivity()).getAllUniqueCategories();
        final ListView categorySelectionListView = (ListView) rootView.findViewById(R.id.categoryListView);
        categorySelectionListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        categorySelectionListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice,
                categories));
        categorySelectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if((categorySelectionListView.isItemChecked(position))){
                    userPrefs.addSelectedCategory(categories.get(position));
                }else{
                    userPrefs.removeSelectedCategory(categories.get(position));
                }
            }
        });
        for (String s : categories) {
            if (userPrefs.getSelectedCategories().contains(s)) {
                categorySelectionListView.setItemChecked(categories.indexOf(s), true);
            }else{
                categorySelectionListView.setItemChecked(categories.indexOf(s), false);
            }
        }

        //operations
        final ArrayList<String> operations = userPrefs.getAllOperations();
        final Spinner operationSelectionSpinner = (Spinner) rootView.findViewById(R.id.operationSpinner);
        operationSelectionSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                operations));
        operationSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userPrefs.setOperationSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        operationSelectionSpinner.setSelection(userPrefs.getOperationSelection());
    }

    @Override
    public void onPause() {
        super.onPause();
        prefsManager.setCachedPreferencesSet(userPrefs);
    }
}
