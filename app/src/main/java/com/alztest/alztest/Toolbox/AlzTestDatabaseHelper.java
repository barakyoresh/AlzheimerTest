/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Toolbox;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alztest.alztest.Statistics.AlzTestSessionStatistics;
import com.alztest.alztest.Stimuli.Stimulus;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Barak Yoresh on 10/12/2014.
 */
public class AlzTestDatabaseHelper extends OrmLiteSqliteOpenHelper{

        // name of the database file for your application -- change to something appropriate for your app
        private static final String DATABASE_NAME = "AlzTest.sqlite";

        // any time you make changes to your database objects, you may have to increase the database version
        private static final int DATABASE_VERSION = 1;

        // the DAO object we use to access the SimpleData table
        private Dao<Stimulus, Integer> StimuliDao = null;
        private Dao<AlzTestSessionStatistics, Date> AlzTestSessionStatisticsDao = null;


        public AlzTestDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database,ConnectionSource connectionSource) {
            try {
                TableUtils.createTable(connectionSource, Stimulus.class);
                TableUtils.createTable(connectionSource, AlzTestSessionStatistics.class);
            } catch (SQLException e) {
                Log.e(AlzTestDatabaseHelper.class.getName(), "Can't create database", e);
                throw new RuntimeException(e);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db,ConnectionSource connectionSource, int oldVersion, int newVersion) {
            try {
                List<String> allSql = new ArrayList<String>();
                switch(oldVersion)
                {
                    case 1:
                        //allSql.add("alter table AdData add column `new_col` VARCHAR");
                        //allSql.add("alter table AdData add column `new_col2` VARCHAR");
                }
                for (String sql : allSql) {
                    db.execSQL(sql);
                }
            } catch (SQLException e) {
                Log.e(AlzTestDatabaseHelper.class.getName(), "exception during onUpgrade", e);
                throw new RuntimeException(e);
            }

        }

        public Dao<Stimulus, Integer> getStimuliDao() {
            if (null == StimuliDao) {
                try {
                    StimuliDao = getDao(Stimulus.class);
                }catch (java.sql.SQLException e) {
                    e.printStackTrace();
                }
            }
            return StimuliDao;
        }

        public Dao<AlzTestSessionStatistics, Date> getAlzTestSessionStatisticsDao() {
            if (null == AlzTestSessionStatisticsDao) {
                try {
                    AlzTestSessionStatisticsDao = getDao(AlzTestSessionStatistics.class);
                }catch (java.sql.SQLException e) {
                    e.printStackTrace();
                }
            }
            return AlzTestSessionStatisticsDao;
        }
}
