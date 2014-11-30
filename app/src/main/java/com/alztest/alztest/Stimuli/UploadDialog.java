/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Stimuli;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.alztest.alztest.R;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by Barak Yoresh on 29/11/2014.
 */
public class UploadDialog extends DialogFragment {

    public static final String ALTERNATIVE_PATH = "alternative_path";
    private String[] mFileList;
    private static final File basePath = Environment.getExternalStorageDirectory();
    private File mPath = basePath;
    private String mChosenFile;
    private static final String FTYPE1 = ".xls";
    private static final String FTYPE2 = ".xlsx";
    private static final String TAG = "UploadDialog";


    private void loadFileList() {
        try {
            mPath.mkdirs();
        }
        catch(SecurityException e) {
            //Log.e(TAG, "unable to write on the sd card " + e.toString());
            Log.e(TAG, "unable to write on the sd card " + e.toString());
            System.out.println("unable to write on the sd card " + e.toString());
        }
        if(mPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    return filename.contains(FTYPE1) || filename.contains(FTYPE2) || sel.isDirectory();
                }

            };
            mFileList = mPath.list(filter);
        }
        else {
            mFileList= new String[0];
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(ALTERNATIVE_PATH)){
            System.out.println("got Alt Path! + " + bundle.getString(ALTERNATIVE_PATH));
            mPath = new File(bundle.getString(ALTERNATIVE_PATH));
        }
        System.out.println("creating dialog with path - " + mPath.getPath());
        loadFileList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Excel file to load");
        if(mFileList == null) {
            Log.e(TAG, "Showing file picker before loading the file list");
            System.out.println("Showing file picker before loading the file list");
            return builder.create();
        }
        builder.setNeutralButton("back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mPath.getPath().equals(basePath.getPath())){
                    System.out.println("base path");
                }else{
                    UploadDialog ud = new UploadDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString(ALTERNATIVE_PATH, mPath.getPath().substring(0, mPath.getPath().lastIndexOf("/")));
                    System.out.println("putting arg - " + mPath.getPath().substring(0, mPath.getPath().lastIndexOf("/")));
                    ud.setArguments(bundle);
                    ud.show(getFragmentManager(), getString(R.string.upload_stimuli));
                }
            }
        });
        builder.setItems(mFileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mChosenFile = mFileList[which];
                System.out.println(mChosenFile);
                File sel = new File(mPath, mChosenFile);
                //directory
                if(sel.isDirectory()) {
                    System.out.println("Directory!");
                    UploadDialog ud = new UploadDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString(ALTERNATIVE_PATH, sel.getPath());
                    System.out.println("putting arg - " + sel.getPath());
                    ud.setArguments(bundle);
                    ud.show(getFragmentManager(), getString(R.string.upload_stimuli));
                }
                //file
                else {
                    System.out.println("Got file - " + sel.getName());
                    appendToDbFromExternalFile(sel);
                    //TODO: Notify StimuliTable's table
                }
            }
        });

        return builder.create();
    }

    /**
     * Parses excel file and adds it to Database
     * @param sel
     */
    private void appendToDbFromExternalFile(File sel) {
        try {
            Workbook workbook = Workbook.getWorkbook(sel);

            Sheet sheet = workbook.getSheet(0);

            Cell a1 = sheet.getCell(0,0);
            Cell b2 = sheet.getCell(1,1);
            Cell c2 = sheet.getCell(2,1);

            String stringa1 = a1.getContents();
            String stringb2 = b2.getContents();
            String stringc2 = c2.getContents();

            System.out.println(stringa1 + " " + stringb2 + " " + stringc2);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }


}
