/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Stimuli;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alztest.alztest.R;

import java.util.HashSet;

/**
 * Created by user on 14/07/2015.
 */
public class SpecificStimulusListAdapter extends StimulusListAdapter {
    LayoutInflater inflater;

    private HashSet<Integer> selected = new HashSet<Integer>();

    public SpecificStimulusListAdapter(Context context) {
        super(context);
    }

    public SpecificStimulusListAdapter(Context context, HashSet<Integer> selected) {
        super(context);
        this.selected = selected;
        sort(SortCriteria.Category);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if( convertView == null ){
            convertView = super.inflater.inflate(R.layout.fragment_specific_stimuli_list_entry, parent, false);
        }
        TextView category = (TextView) convertView.findViewById(R.id.list_entry_stimulus_category);
        TextView name = (TextView) convertView.findViewById(R.id.list_entry_stimulus_name);
        TextView value = (TextView) convertView.findViewById(R.id.list_entry_stimulus_value);
        final CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.specificListEntryCheckbox);

        category.setText(stimuli.get(position).getCategory());
        name.setText(stimuli.get(position).getName());
        value.setText(Integer.toString(stimuli.get(position).getValue()));
        checkbox.setChecked(selected.contains(stimuli.get(position).hashCode()));
        checkbox.setClickable(false);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !checkbox.isChecked();
                setSelectedPos(position, isChecked);
                checkbox.setChecked(isChecked);
            }
        });


        return convertView;
    }

    public void setSelectedPos(int pos, boolean checked) {
        if (checked) {
            selected.add(stimuli.get(pos).hashCode());
        } else {
            selected.remove(stimuli.get(pos).hashCode());
        }
    }

    public boolean getSelectedPos(int pos) {
        return selected.contains(stimuli.get(pos).hashCode());
    }

    public HashSet<Integer> getSelected() {
        return selected;
    }

    public void setSelected(HashSet<Integer> selected) {
        this.selected = selected;
    }


}
