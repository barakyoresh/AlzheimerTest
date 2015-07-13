/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Prefrences;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.ImageView;
import android.widget.ListAdapter;
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
    private AlzTestCategoryAdapter categoryAdapter = null;
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

        //countdown before session start
        setupSessionCountdownWidget();

        //size of stimuli text
        setupSessionTextSizeWidget();

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

    private void setupSessionTextSizeWidget() {
        //spinner
        final Spinner sessTextSize = (Spinner) rootView.findViewById(R.id.textSizeSpinner);

        //get text sizes as string array with '%' char
        ArrayList<String> textSizesStrings = new ArrayList<String>();
        for (int textSize : userPrefs.getAllTextSizes()) {
            textSizesStrings.add(Integer.toString(textSize) + "%");
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                textSizesStrings);
        sessTextSize.setAdapter(adapter);
        sessTextSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userPrefs.setTextSize(Integer.decode(adapter.getItem(position).substring(0, adapter.getItem(position).indexOf("%"))));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sessTextSize.setSelection(userPrefs.getTextSizePosition());


        //icon
        ImageView infoIcon = (ImageView) rootView.findViewById(R.id.textSizeInfoImage);
        infoIcon.setClickable(true);
        infoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExampleTextSizeDialog();
            }
        });

    }

    private void openExampleTextSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Stimuli Text Size");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_example_stimuli_size, null);
        TextView exampleText = (TextView) dialogView.findViewById(R.id.exampleText);
        float textSizeMultiplier = (userPrefs.getTextSize() / 100);
        exampleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, exampleText.getTextSize() * textSizeMultiplier);
        builder.setView(dialogView);
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void setupDummyWidgets() {
        //seekbar
        SeekBar sb = (SeekBar) rootView.findViewById(R.id.seekBar);
        sb.setMax(3);
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
        categoryAdapter = new AlzTestCategoryAdapter(getActivity(), categories, userPrefs);
        categorySelectionListView.setAdapter(categoryAdapter);

        //fix for android issue preventing a fully extended list view within a scrollable view
        setListViewHeightBasedOnChildren(categorySelectionListView);
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView
     **** This method was taken from the internet - http://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        listView.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
        int listHeight = listView.getMeasuredHeight();

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = listHeight * listAdapter.getCount() + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onPause() {
        super.onPause();
        //update category preferences each pause
        if(categoryAdapter != null && userPrefs != null) {
            userPrefs.setCategoryPreferences(categoryAdapter.getCategoryListItems());
        }
        prefsManager.setCachedPreferencesSet(userPrefs);
    }
}
