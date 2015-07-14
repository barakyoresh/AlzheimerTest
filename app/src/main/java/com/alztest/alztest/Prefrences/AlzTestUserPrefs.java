/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Prefrences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


/**
 * Created by Barak Yoresh on 14/02/2015.
 */
public class AlzTestUserPrefs {
    private int scroller = 0;
    private boolean swtich = false;


    private int textSize = 100;
    private boolean allowRepetition = false;
    private int operation = 0;
    private int numberOfPairsInTrial = 0;
    private int maximumValueDifference = 6;
    private int minimumValueDifference = 0;
    private static ArrayList<String> opertaions = new ArrayList<String>(Arrays.asList("Use All Except:", "Use Only:"));
    private static ArrayList<Integer> allValueDifferences = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6));
    private static ArrayList<Integer> AllSessCountdownTimes = new ArrayList<Integer>(Arrays.asList(5, 3, 1, 0));
    private static ArrayList<Integer> getAllTextSizes = new ArrayList<Integer>((Arrays.asList(100, 150, 200, 250)));
    private ArrayList<AlzTestCategoryAdapter.CategoryListItem> categoryPreferences = new ArrayList<AlzTestCategoryAdapter.CategoryListItem>();
    private int countdownTimerValue = 0;
    private boolean usingSpecificStimuliSubset = false;
    private HashSet<Integer> specificStimuliSubsetIndecies = new HashSet<Integer>();

    public HashSet<Integer> getSpecificStimuliSubsetIndecies() {
        return specificStimuliSubsetIndecies;
    }

    public void setSpecificStimuliSubsetIndecies(HashSet<Integer> specificStimuliSubsetIndecies) {
        this.specificStimuliSubsetIndecies = specificStimuliSubsetIndecies;
    }

    public boolean isUsingSpecificStimuliSubset() {
        return usingSpecificStimuliSubset;
    }

    public void setUsingSpecificStimuliSubset(boolean usingSpecificStimuliSubset) {
        this.usingSpecificStimuliSubset = usingSpecificStimuliSubset;
    }



    public AlzTestUserPrefs(){    }

    public boolean isAllowRepetition() {
        return allowRepetition;
    }

    public void setAllowRepetition(boolean allowRepetition) {
        this.allowRepetition = allowRepetition;
    }

    public ArrayList<Integer> getAllValueDifferences() {
        return allValueDifferences;
    }

    public int getMinimumValueDifference() {
        return minimumValueDifference;
    }

    public void setMinimumValueDifference(int minimumValueDifference) {
        this.minimumValueDifference = minimumValueDifference;
    }

    public int getMaximumValueDifference() {
        return maximumValueDifference;
    }

    public void setMaximumValueDifference(int maximumValueDifference) {
        this.maximumValueDifference = maximumValueDifference;
    }

    public ArrayList<String> getAllOperations(){
        return opertaions;
    }


    public int getOperationSelection() {
        return operation;
    }

    public void setOperationSelection(int operation) {
        this.operation = operation;
    }


    public ArrayList<AlzTestCategoryAdapter.CategoryListItem> getCategoryPreferences() {
        return categoryPreferences;
    }

    public void setCategoryPreferences(ArrayList<AlzTestCategoryAdapter.CategoryListItem> categoryPreferences) {
        this.categoryPreferences = categoryPreferences;
    }

    public int getScroller() {
        return scroller;
    }

    public void setScroller(int scroller) {
        this.scroller = scroller;
    }

    public boolean isSwtich() {
        return swtich;
    }

    public void setSwtich(boolean swtich) {
        this.swtich = swtich;
    }

    public int getNumberOfPairsInTrial() {
        return numberOfPairsInTrial;
    }

    public void setNumberOfPairsInTrial(int numberOfPairsInTrials) {
        this.numberOfPairsInTrial = numberOfPairsInTrials;
    }

    public int getCountdownTimerValue() {
        return countdownTimerValue;
    }

    public void setCountdownTimerValue(int countdownTimerValue) {
        this.countdownTimerValue = countdownTimerValue;
    }

    public ArrayList<Integer> getAllSessCountdownTimes() {
        return AllSessCountdownTimes;
    }

    public int getCountdownTimerValuePosition() {
        return AllSessCountdownTimes.indexOf(countdownTimerValue);
    }

    public int getTextSizePosition() {
        return getAllTextSizes.indexOf(textSize);
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public List<Integer> getAllTextSizes() {
        return getAllTextSizes;
    }
}
