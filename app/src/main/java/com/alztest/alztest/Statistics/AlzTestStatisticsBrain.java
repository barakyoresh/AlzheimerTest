/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Statistics;

import android.util.Log;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.Session.AlzTestSingleClickStats;
import com.alztest.alztest.Session.StimulusSelection;
import com.alztest.alztest.Stimuli.Stimulus;
import com.alztest.alztest.Toolbox.AlzTestDatabaseManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Created by Barak Yoresh on 12/06/2015.
 */
public class AlzTestStatisticsBrain {


    public static boolean saveStatisticsToFile(AlzTestSessionStatistics stats, File file) {
        if(!file.getAbsolutePath().endsWith(".xls")) {
            Log.e(OptionListActivity.APPTAG, "Only xls files are supported!");
            return false;
        }
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(file);

            WritableSheet data = workbook.createSheet("Data", 0);
            WritableSheet details = workbook.createSheet("Details", 1);

            //Update details
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm dd/MM/yyyy");

            details.addCell(new Label(0,0,"Subject ID"));
            details.addCell(new Label(0,1, Long.toString(stats.getSubjectId())));
            details.addCell(new Label(1,0,"Subject Name"));
            details.addCell(new Label(1,1, stats.getSubjectName()));
            details.addCell(new Label(2,0,"MMSE Orientation Space"));
            details.addCell(new Number(2,1, stats.getMMSEOrientationSpace()));
            details.addCell(new Label(3,0,"MMSE Orientation Time"));
            details.addCell(new Number(3,1, stats.getMMSEOrientationTime()));
            details.addCell(new Label(4,0,"MMSE Total"));
            details.addCell(new Number(4,1, stats.getMMSETotal()));
            details.addCell(new Label(5,0,"Session Date"));
            details.addCell(new Label(5,1, sdf.format(stats.getSessionStartTime())));

            //Update data
            data.addCell(new Label(0,0,"Stimuli Category"));
            data.addCell(new Label(1,0,"Left Stimulus"));
            data.addCell(new Label(2,0,"Right Stimulus"));
            data.addCell(new Label(3,0,"Left Stimulus Value"));
            data.addCell(new Label(4,0,"Right Stimulus Value"));
            data.addCell(new Label(5,0,"Selection 0-left 1-right"));
            data.addCell(new Label(6,0,"Correct Selection"));
            data.addCell(new Label(7,0,"Response Time in ms"));

            ArrayList<AlzTestSingleClickStats> clickStats = stats.getStatistics();
            for (int i = 1; i < clickStats.size();  i++) {
                AlzTestSingleClickStats s = clickStats.get(i);
                Stimulus l = s.getLeftStim();
                Stimulus r = s.getRightStim();
                data.addCell(new Label(0,i,l.getCategory()));
                data.addCell(new Label(1,i,l.getName()));
                data.addCell(new Label(2,i,r.getName()));
                data.addCell(new Number(3,i,l.getValue()));
                data.addCell(new Number(4,i,r.getValue()));
                data.addCell(new Number(5,i,(s.getSelected() == StimulusSelection.left ? 0 : 1)));
                data.addCell(new Number(6,i,(s.correctResponse ? 1 : 0)));
                data.addCell(new Number(7,i,s.responseTimeInMs));
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


    public static ArrayList<AlzTestSessionStatistics> getAllStatsById(long subjectId) {
        //get dao
        Dao<AlzTestSessionStatistics, Date> dao = AlzTestDatabaseManager.getInstance().getHelper().getAlzTestSessionStatisticsDao();
        ArrayList<AlzTestSessionStatistics> stats = new ArrayList<AlzTestSessionStatistics>();

        try {
            //build query
            QueryBuilder<AlzTestSessionStatistics, Date> qb = dao.queryBuilder();
            Where where = qb.where();
            where.eq("subjectId", subjectId);

            //search
            PreparedQuery<AlzTestSessionStatistics> preparedQuery = qb.prepare();
            stats = (ArrayList<AlzTestSessionStatistics>) dao.query(preparedQuery);
        } catch (SQLException e) {
            Log.e(OptionListActivity.APPTAG, "Database SQL error");
            e.printStackTrace();
        }

        return stats;
    }
}
