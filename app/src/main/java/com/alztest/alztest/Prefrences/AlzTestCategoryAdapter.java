/*
 * Copyright (c) 2015. Barak Yoresh. all rights reserved.
 */

package com.alztest.alztest.Prefrences;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.alztest.alztest.OptionListActivity;
import com.alztest.alztest.R;

import java.util.ArrayList;

/**
 * Created by Baral Yoresh on 13/07/2015.
 */
public class AlzTestCategoryAdapter extends BaseAdapter {
    public static final int ABS_DELTA_Y_REQUIRED_FOR_SWAP = 40;
    public static final String HOLO_LIGHT_BLUE = "#33b5e5";
    public static final String TRANSPERANT_GRAY = "#00eeeeee";
    private ArrayList<String> categories;
    private ArrayList<CategoryListItem> categoryListItems;
    private AlzTestUserPrefs userPrefs;
    private LayoutInflater inflater;

    public AlzTestCategoryAdapter(Context context, ArrayList<String> categories, AlzTestUserPrefs userPrefs){
        this.categories = categories;
        this.userPrefs = userPrefs;

        //populate list
        inflater = LayoutInflater.from(context);



        populateList();

    }

    private void populateList() {
        ArrayList<CategoryListItem> userprefsCategoryList = userPrefs.getCategoryPreferences();
        categoryListItems = new ArrayList<CategoryListItem>();

        //iterate over prefs fist to maintain order
        for(CategoryListItem userCategory : userprefsCategoryList) {
            CategoryListItem listItem = null;

            //must iterate over list since contains() compares pointers and not content.
            for(String categoryName : categories) {
                if (userCategory.getCategory().equals(categoryName)) {
                    categoryListItems.add(userCategory);
                    break;
                }
            }
        }

        //iterate over unique categories as well to add unincluded categories
        for(String categoryName : categories) {
            CategoryListItem listItem = null;

            //must iterate over list since contains() compares pointers and not content.
            for(CategoryListItem userCategory : userprefsCategoryList) {
                if (userCategory.getCategory().equals(categoryName)) {
                    listItem = userCategory;
                    break;
                }
            }

            if(listItem == null) {
                categoryListItems.add(new CategoryListItem(categoryName));
            }
        }
    }

    @Override
    public int getCount() {
        return categoryListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if( convertView == null ){
            convertView = inflater.inflate(R.layout.fragment_category_list_entry, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.list_entry_category_name);
        CheckBox includeInSession = (CheckBox) convertView.findViewById(R.id.includeInSessionCheckBox);
        CheckBox includeInAnalysis = (CheckBox) convertView.findViewById(R.id.includeInAnalysisCheckBox);

        name.setText(categoryListItems.get(position).getCategory());
        includeInSession.setChecked(categoryListItems.get(position).isIncludeInSession());
        includeInAnalysis.setChecked(categoryListItems.get(position).isIncludeInAnalysis());

        final int pos = position;
        includeInSession.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                categoryListItems.get(pos).setIncludeInSession(isChecked);
            }
        });
        includeInAnalysis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                categoryListItems.get(pos).setIncludeInAnalysis(isChecked);
            }
        });


        //on touch events, if this view was long pressed, if the event is movment, swap it with the
        //entry in the direction of the movment. eventually this will be a propper sorting inteface.
        //but for now this will do
        convertView.setOnTouchListener(new View.OnTouchListener() {
            float y = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);


                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    y = event.getY();
                    Log.v(OptionListActivity.APPTAG, "y=" + y);
                }
                if(categoryListItems.get(pos).isLongPressed) {




                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        float deltaY = y - event.getY();
                        Log.v(OptionListActivity.APPTAG, "deltaY=" + deltaY);

                        if(Math.abs(deltaY) > ABS_DELTA_Y_REQUIRED_FOR_SWAP) {
                            categoryListItems.get(pos).isLongPressed = false;
                            swap(pos, pos + (deltaY < 0 ? 1 : -1));

                            //adjust colors
                            v.setBackgroundColor(v.getContext().getResources().getColor(android.R.color.transparent));
                            View swapee = ((ListView)v.getParent()).getChildAt(mod(pos + (deltaY < 0 ? 1 : -1), categoryListItems.size()));
                            swapee.setBackgroundColor(Color.parseColor(HOLO_LIGHT_BLUE));
                            animateColorChange(swapee, Color.parseColor(HOLO_LIGHT_BLUE), Color.parseColor(TRANSPERANT_GRAY));
                        }
                    }

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Log.v(OptionListActivity.APPTAG, "action up");
                        categoryListItems.get(pos).isLongPressed = false;
                        v.setBackgroundColor(v.getContext().getResources().getColor(android.R.color.transparent));
                    }
                }

                return false;
            }
        });


        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setBackgroundColor(Color.parseColor(HOLO_LIGHT_BLUE));
                categoryListItems.get(pos).isLongPressed = true;
                return true;

            }
        });

        return convertView;
    }

    private static void animateColorChange(final View v, int colorFrom, int colorTo){
        ObjectAnimator animator = ObjectAnimator.ofInt(v, "backgroundColor", colorFrom, colorTo).setDuration(300);
        animator.setEvaluator(new ArgbEvaluator());
        animator.start();
        /*
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setEvaluator(new ArgbEvaluator());
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                v.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();*/
    }

    /**
     * Swaps between two list entries
     * @param pos1
     * @param pos2
     */
    void swap(int pos1, int pos2) {
        Log.v(OptionListActivity.APPTAG, "swap request, swap " + pos1 + "<->" + pos2);
        pos1 = mod(pos1, categoryListItems.size());
        pos2 = mod(pos2, categoryListItems.size());
        Log.v(OptionListActivity.APPTAG, "after mod, swap " + pos1 + "<->" + pos2);
        CategoryListItem tmp = categoryListItems.get(pos1);
        categoryListItems.set(pos1, categoryListItems.get(pos2));
        categoryListItems.set(pos2, tmp);
        notifyDataSetChanged();
    }

    public ArrayList<CategoryListItem> getCategoryListItems() {
        return categoryListItems;
    }

    /**
     * % (modulo) isn't working in android amazingly.
     */
    private int mod(int x, int y)
    {
        int result = x % y;
        return result < 0 ? result + y : result;
    }


    /**
     * Nested class to hold the data required for category options
     */
    public class CategoryListItem {
        boolean isLongPressed;

        public CategoryListItem(String name) {
            category = name;
        }

        boolean includeInSession = true;

        public boolean isIncludeInSession() {
            return includeInSession;
        }

        public void setIncludeInSession(boolean includeInSession) {
            this.includeInSession = includeInSession;
        }

        public boolean isIncludeInAnalysis() {
            return includeInAnalysis;
        }

        public void setIncludeInAnalysis(boolean includeInAnalysis) {
            this.includeInAnalysis = includeInAnalysis;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        boolean includeInAnalysis = true;
        String category = "categoryName";
    }

}
