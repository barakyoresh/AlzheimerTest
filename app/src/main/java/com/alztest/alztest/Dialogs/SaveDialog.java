/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Barak Yoresh on 28.5.15.
 */
public class SaveDialog extends DialogFragment {

    /**
     * While working like a charm, this dialog and the other file-related dialog have quite
     * a few code duplications and are in general not well designed, in the sence that they could
     * have been more generic, and not so specific to the problem at hand
     */

    public static final String ALTERNATIVE_PATH = "alternative_path";
    public static final String FILE_NAME = "file_name";
    public static String defaultFileName = "untitled";
    private String[] mFileList;
    private static final File basePath = Environment.getExternalStorageDirectory();
    private File mPath = basePath;
    private String mChosenFile;
    private static final String FTYPE1 = ".xls";
    private static final String FTYPE2 = ".xlsx";
    private static final String TAG = "SaveDialog";
    private static final String ELIPSIS = "\\..";
    private FileDialogCallback callback = null;
    private EditText input;
    private String fileName = "";
    public String extensionType = "xls";


    public void setCallback(FileDialogCallback callback) {
        this.callback = callback;
    }


    private void loadFileList() {
        try {
            mPath.mkdirs();
        }
        catch(SecurityException e) {
            //Log.e(TAG, "unable to write on the sd card " + e.toString());
            Log.e(TAG, "unable to write on the sd card " + e.toString());
            Log.v(OptionListActivity.APPTAG, "unable to write on the sd card " + e.toString());
        }
        if(mPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    return filename.contains("." + extensionType) || sel.isDirectory();
                }

            };
            mFileList = mPath.list(filter);
            //add "back" option
            if(!mPath.getPath().equals(basePath.getPath())) {
                mFileList = addElipsis(mFileList);
            }
        }
        else {
            mFileList= new String[1];
            //add "back" option
            mFileList[0] = ELIPSIS;
        }
    }


    private static String[] addElipsis(String[] mFileList) {
        String[] ret = new String[mFileList.length + 1];
        ret[0] = ELIPSIS;
        for (int i = 0; i < mFileList.length; i++){
            ret[i+1] = mFileList[i];
        }
        return ret;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle != null) {
            if (bundle.containsKey(ALTERNATIVE_PATH)) {
                Log.v(OptionListActivity.APPTAG, "got Alt Path! + " + bundle.getString(ALTERNATIVE_PATH));
                mPath = new File(bundle.getString(ALTERNATIVE_PATH));
            }
            if(bundle.containsKey(FILE_NAME)) {
                Log.v(OptionListActivity.APPTAG, "got file name! + " + bundle.getString(FILE_NAME));
                fileName = bundle.getString(FILE_NAME);
            }
        }
        Log.v(OptionListActivity.APPTAG, "creating dialog with path - " + mPath.getPath());
        loadFileList();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mPath.getAbsolutePath());
        if(mFileList == null) {
            Log.e(TAG, "Showing file picker before loading the file list");
            Log.v(OptionListActivity.APPTAG, "Showing file picker before loading the file list");
            return builder.create();
        }
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        //MKDIR button
        builder.setNeutralButton("Create Directory", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //this method is overriden and never called
            }
        });
        //Save button
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fullFileName = mPath.getPath() + "/" + (validFileName(fileName) ? fileName : defaultFileName) + (extensionType.equals("") ? "" : "." + extensionType);
                File f = new File(fullFileName);
                if (!f.exists()) {
                    callback.onChooseFile(getActivity(), f);
                } else {
                    //TODO: promt override dialog
                    Log.v(OptionListActivity.APPTAG, "Overriding");
                    callback.onChooseFile(getActivity(), f);
                }
            }
        });
        builder.setItems(mFileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mChosenFile = mFileList[which];
                Log.v(OptionListActivity.APPTAG, mChosenFile);
                File sel = new File(mPath, mChosenFile);
                //directory
                if (sel.isDirectory()) {
                    openDirectory(sel);
                }
                //file or Elipsis
                else {
                    if (sel.getName().equals(ELIPSIS)) {
                        //simulate back
                        if (mPath.getPath().equals(basePath.getPath())) {
                            Log.v(OptionListActivity.APPTAG, "base path");
                        } else {
                            SaveDialog sd = new SaveDialog();
                            sd.setCallback(callback);
                            Bundle bundle = new Bundle();
                            bundle.putString(ALTERNATIVE_PATH, mPath.getPath().substring(0, mPath.getPath().lastIndexOf("/")));
                            Log.v(OptionListActivity.APPTAG, "putting arg - " + mPath.getPath().substring(0, mPath.getPath().lastIndexOf("/")));
                            bundle.putString(FILE_NAME, fileName);
                            sd.setArguments(bundle);
                            sd.extensionType = extensionType;
                            sd.show(getFragmentManager(), getString(R.string.save_stimuli));
                        }
                    } else {
                        //regular file
                        String name = sel.getName();
                        name = name.substring(0, name.lastIndexOf('.'));
                        SaveDialog sd = new SaveDialog();
                        sd.setCallback(callback);
                        Bundle bundle = new Bundle();
                        bundle.putString(ALTERNATIVE_PATH, mPath.getPath());
                        Log.v(OptionListActivity.APPTAG, "putting arg - " + sel.getPath());
                        bundle.putString(FILE_NAME, name);
                        sd.setArguments(bundle);
                        sd.extensionType = extensionType;
                        sd.show(getFragmentManager(), getString(R.string.save_stimuli));
                    }
                }
            }
        });
        builder.setView(getInputView());
        final Dialog d = builder.create();

        //override neutral button so pressing it won't dismiss dialog
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v(OptionListActivity.APPTAG, "Neutral button pressed override");
                        AlertDialog.Builder dirBuilder = new AlertDialog.Builder(getActivity());
                        final EditText dirNameEditText = new EditText(getActivity());
                        dirNameEditText.setHint("New Directory");
                        dirBuilder.setView(dirNameEditText)
                                .setTitle("Create a new directory")
                                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton(R.string.Okay, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //get file name
                                        String dirName = dirNameEditText.getText().toString();
                                        if (dirName.equals("")) { dirName = (String) dirNameEditText.getHint(); }

                                        //make sure its valid
                                        if (!validFileName(dirName)) {
                                            Log.v(OptionListActivity.APPTAG, "illegal file name, canceling");
                                            Toast.makeText(getActivity(), "Invalid directory name", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        File f = new File(mPath, dirName);

                                        //make sure file doesn't exist
                                        if(f.exists()) {
                                            Log.v(OptionListActivity.APPTAG, "Path exists, canceling");
                                            Toast.makeText(getActivity(), "Directory already exists", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        //attempt creating
                                        boolean success = f.mkdirs();
                                        if(!success) {
                                            Log.v(OptionListActivity.APPTAG, "file creation failed, canceling");
                                            Toast.makeText(getActivity(), "Failed creating directory", Toast.LENGTH_SHORT).show();

                                            return;
                                        }

                                        //dismiss and open dir
                                        d.dismiss();
                                        openDirectory(f);
                                    }
                                });
                        dirBuilder.create().show();
                    }
                });
            }
        });

        return d;
    }

    private boolean validFileName(String fileName) {
        //TODO: more advanced protection
        if (fileName.length() <= 0) return false;
        return true;
    }

    private void openDirectory(File sel) {
        Log.v(OptionListActivity.APPTAG, "Directory!");
        SaveDialog sd = new SaveDialog();
        sd.setCallback(callback);
        Bundle bundle = new Bundle();
        bundle.putString(ALTERNATIVE_PATH, sel.getPath());
        Log.v(OptionListActivity.APPTAG, "putting arg - " + sel.getPath());
        bundle.putString(FILE_NAME, fileName);
        sd.extensionType = extensionType;
        sd.setArguments(bundle);
        sd.show(getFragmentManager(), getString(R.string.save_stimuli));
    }

    private View getInputView() {
        input = new EditText(getActivity());
        TextView extension = new TextView(getActivity());
        extension.setText(extensionType.equals("") ? "" : "." + extensionType);
        LinearLayout l = new LinearLayout(getActivity());
        l.addView(input);
        l.addView(extension);

        if(fileName.equals("")) {
            input.setHint(defaultFileName);
        } else {
            input.setText(fileName);
        }

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    fileName = s.toString();
            }
        });
        return l;
    }

}
