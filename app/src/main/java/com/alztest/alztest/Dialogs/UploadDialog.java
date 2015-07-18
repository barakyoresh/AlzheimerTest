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
import android.util.Log;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Barak Yoresh on 29/11/2014.
 */
public class UploadDialog extends DialogFragment {

    /**
     * While working like a charm, this dialog and the other file-related dialog have quite
     * a few code duplications and are in general not well designed, in the sence that they could
     * have been more generic, and not so specific to the problem at hand
      */


    public static final String ALTERNATIVE_PATH = "alternative_path";
    private String[] mFileList;
    private static final File basePath = Environment.getExternalStorageDirectory();
    private File mPath = basePath;
    private String mChosenFile;
    private static final String FTYPE1 = ".xls";
    private static final String FTYPE2 = ".xlsx";
    private static final String TAG = "UploadDialog";
    private static final String ELIPSIS = "\\..";
    private FileDialogCallback callback = null;
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
        if(bundle != null && bundle.containsKey(ALTERNATIVE_PATH)){
            Log.v(OptionListActivity.APPTAG, "got Alt Path! + " + bundle.getString(ALTERNATIVE_PATH));
            mPath = new File(bundle.getString(ALTERNATIVE_PATH));
        }
        Log.v(OptionListActivity.APPTAG, "creating dialog with path - " + mPath.getPath());
        loadFileList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Excel file to load");
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
        builder.setItems(mFileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mChosenFile = mFileList[which];
                Log.v(OptionListActivity.APPTAG, mChosenFile);
                File sel = new File(mPath, mChosenFile);
                //directory
                if(sel.isDirectory()) {
                    Log.v(OptionListActivity.APPTAG, "Directory!");
                    UploadDialog ud = new UploadDialog();
                    ud.setCallback(callback);
                    Bundle bundle = new Bundle();
                    bundle.putString(ALTERNATIVE_PATH, sel.getPath());
                    Log.v(OptionListActivity.APPTAG, "putting arg - " + sel.getPath());
                    ud.setArguments(bundle);
                    ud.extensionType = extensionType;
                    ud.show(getFragmentManager(), getString(R.string.upload_stimuli));
                }
                //file or Elipsis
                else {
                    if(sel.getName().equals(ELIPSIS)){
                        //simulate back
                        if(mPath.getPath().equals(basePath.getPath())){
                            Log.v(OptionListActivity.APPTAG, "base path");
                        }else{
                            UploadDialog ud = new UploadDialog();
                            ud.setCallback(callback);
                            Bundle bundle = new Bundle();
                            bundle.putString(ALTERNATIVE_PATH, mPath.getPath().substring(0, mPath.getPath().lastIndexOf("/")));
                            Log.v(OptionListActivity.APPTAG, "putting arg - " + mPath.getPath().substring(0, mPath.getPath().lastIndexOf("/")));
                            ud.setArguments(bundle);
                            ud.extensionType = extensionType;
                            ud.show(getFragmentManager(), getString(R.string.upload_stimuli));
                        }
                    }else{
                        //regular file
                        Log.v(OptionListActivity.APPTAG, "Got file - " + sel.getName());
                        if(callback != null) {
                            callback.onChooseFile(getActivity(), sel);
                        }
                    }
                }
            }
        });

        return builder.create();
    }




}
