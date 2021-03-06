/*
 * Copyright (c) 2014. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest;

import android.app.Fragment;

import com.alztest.alztest.Prefrences.AlzTestPrefrencesFragment;
import com.alztest.alztest.Session.NewSessionFragment;
import com.alztest.alztest.Statistics.StatisticsListFragment;
import com.alztest.alztest.Stimuli.StimuliListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Barak Yoresh on 28/11/2014.
 */
public class OptionsContent {

    /**
     * An array of sample option items items.
     */
    public static List<OptionItem> ITEMS = new ArrayList<OptionItem>();


    static {
        // Add menu items.
        addItem(new OptionItem("Start New Session", new NewSessionFragment()));
        addItem(new OptionItem("Preferences", new AlzTestPrefrencesFragment()));
        addItem(new OptionItem("Stimuli List", new StimuliListFragment()));
        addItem(new OptionItem("Statistics List", new StatisticsListFragment()));
    }

    private static void addItem(OptionItem item) {
        ITEMS.add(item);
    }

    /**
     * An Option representation
     */
    public static class OptionItem {
        public String content;
        public Fragment fragment;

        public OptionItem(String content, Fragment fragment) {
            this.content = content;
            this.fragment = fragment;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
