/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Stimuli;

import android.content.Context;
import android.util.Log;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.Toolbox.AlzTestDatabaseManager;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by Barak Yoresh on 10/12/2014.
 */
public class StimuliBrain {

    /**
     * Parses excel file and adds it to Database
     * @param sel
     */
    public static void appendStimuliToDbFromExternalFile(Context context, File sel) {
        //TODO: pormt "ignore first line" checkbox. currently ignoring by default!
        //TODO: open progress dialog
        /*
        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);*/


        try {
            Workbook workbook = Workbook.getWorkbook(sel);

            Sheet sheet = workbook.getSheet(0);

            String name = "", category = "";
            int value = 0;

            for (int i = 1; i < sheet.getRows(); i++){
                name = sheet.getCell(0, i).getContents();
                category = sheet.getCell(1, i).getContents();
                value = Integer.decode(sheet.getCell(2, i).getContents());

                Log.v(OptionListActivity.APPTAG, "Read new stimulus successfully: " + name + ", " + category + ", " + value);

                Stimulus s = new Stimulus(name, category, value);

                try {
                    AlzTestDatabaseManager.getInstance().getHelper().getStimuliDao().create(s);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Log.v(OptionListActivity.APPTAG, "Added new stimulus successfully");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
/*
        for(int i = 1; i < 10000; i++){
            if(i%100 == 0)
            Log.v(OptionListActivity.APPTAG, "loading...");
        }

        progressBar.setProgress(100);
        progressBar.setVisibility(View.INVISIBLE);*/
    }

    /**
     * clears the data base from all entries
     */
    public static void clearDB() {
        Dao<Stimulus, String> stimDao = AlzTestDatabaseManager.getInstance().getHelper().getStimuliDao();
        try {
            stimDao.delete(stimDao.queryForAll());
        } catch (Exception e) {
            Log.v(OptionListActivity.APPTAG, "clear DB failed - ");
            e.printStackTrace();
        }
    }
}
