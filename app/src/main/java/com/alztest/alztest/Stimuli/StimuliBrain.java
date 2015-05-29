/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Stimuli;

import android.util.Log;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.Toolbox.AlzTestDatabaseManager;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Created by Barak Yoresh on 10/12/2014.
 */
public class StimuliBrain {

    /**
     * Parses excel file and adds it to Database
     * @param file
     */
    public static boolean appendStimuliToDbFromExternalFile(File file) {
        //TODO: pormt "ignore first line" checkbox. currently ignoring by default!
        //TODO: open progress dialog

        try {
            Workbook workbook = Workbook.getWorkbook(file);

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
                    Log.w(OptionListActivity.APPTAG, "Failed to add Stimuli - "  + name + ", " + category + ", " + value);
                }

                Log.v(OptionListActivity.APPTAG, "Added new stimulus successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
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

    public static boolean saveStimuliToFile(ArrayList<Stimulus> stimuli, File file) {
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(file);

            WritableSheet sheet = workbook.createSheet("Stimuli", 0);


            sheet.addCell(new Label(0,0,"stimulus"));
            sheet.addCell(new Label(1,0,"category"));
            sheet.addCell(new Label(2,0,"distance"));

            //add stimuli
            for (int i = 0; i < stimuli.size();  i++) {
                Stimulus s = stimuli.get(i);
                sheet.addCell(new Label(0,i+1, s.getName()));
                sheet.addCell(new Label(1,i+1, s.getCategory()));
                sheet.addCell(new Number(2,i+1, s.getValue()));
            }

            //commit
            workbook.write();
            workbook.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


}
