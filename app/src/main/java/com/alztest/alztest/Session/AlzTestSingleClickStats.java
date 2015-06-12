/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Session;

/**
 * Created by Barak Yoresh on 10/04/2015.
 */

import com.alztest.alztest.Stimuli.Stimulus;

import java.io.Serializable;

public class AlzTestSingleClickStats implements Serializable{
    public Stimulus leftStim, rightStim;
    public StimulusSelection selected;
    public boolean correctResponse;
    public long responseTimeInMs;


    public AlzTestSingleClickStats(Stimulus leftStim, Stimulus rightStim, StimulusSelection selected,
                                   long responseTimeInMs) {
        this.leftStim = leftStim;
        this.rightStim = rightStim;
        this.selected = selected;
        this.correctResponse = getCorrectResponse(leftStim, rightStim, selected);
        this.responseTimeInMs = responseTimeInMs;
    }

    private boolean getCorrectResponse(Stimulus left, Stimulus right, StimulusSelection selected) {
        StimulusSelection correctSelection = (left.getValue() >= right.getValue() ? StimulusSelection.left : StimulusSelection.right);
        return selected.equals(correctSelection);
    }

    public Stimulus getLeftStim() {
        return leftStim;
    }

    public void setLeftStim(Stimulus leftStim) {
        this.leftStim = leftStim;
    }

    public Stimulus getRightStim() {
        return rightStim;
    }

    public void setRightStim(Stimulus rightStim) {
        this.rightStim = rightStim;
    }

    public StimulusSelection getSelected() {
        return selected;
    }

    public void setSelected(StimulusSelection selected) {
        this.selected = selected;
    }

    public boolean isCorrectResponse(Stimulus leftStim, Stimulus rightStim, StimulusSelection selected) {
        return correctResponse;
    }

    public void setCorrectResponse(boolean correctResponse) {
        this.correctResponse = correctResponse;
    }

    public long getResponseTimeInMs() {
        return responseTimeInMs;
    }

    public void setResponseTimeInMs(long responseTimeInMs) {
        this.responseTimeInMs = responseTimeInMs;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(leftStim.getCategory())
                .append('\n')
                .append(selected.equals(StimulusSelection.left) ? "[X]" : "[ ]")
                .append(leftStim.getName())
                .append(':')
                .append(selected.equals(StimulusSelection.left) ? "[ ]" : "[X]")
                .append(rightStim.getName())
                .append('\n')
                .append("response correct: ")
                .append(correctResponse)
                .append('\n')
                .append("response in ms: ")
                .append(responseTimeInMs);
        return sb.toString();
    }
}