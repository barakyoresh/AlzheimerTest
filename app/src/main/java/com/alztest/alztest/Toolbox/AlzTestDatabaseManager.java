/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Toolbox;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by Barak Yoresh on 10/12/2014.
 */
public class AlzTestDatabaseManager {

    static private AlzTestDatabaseManager instance;

    static public void init(Context ctx) {
        if (null==instance) {
            instance = new AlzTestDatabaseManager(ctx);
        }
    }

    static public AlzTestDatabaseManager getInstance() {
        return instance;
    }

    private AlzTestDatabaseHelper helper;
    private AlzTestDatabaseManager(Context ctx) {
        helper = new AlzTestDatabaseHelper(ctx);
    }

    public AlzTestDatabaseHelper getHelper() {
        return helper;
    }


}
