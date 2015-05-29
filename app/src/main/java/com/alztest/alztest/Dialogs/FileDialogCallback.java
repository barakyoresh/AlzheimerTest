/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Dialogs;

import android.app.Activity;

import java.io.File;

/**
 * Created by user on 28/05/2015.
 */
public interface FileDialogCallback {
    public void onChooseFile(Activity activity, File file);
}
