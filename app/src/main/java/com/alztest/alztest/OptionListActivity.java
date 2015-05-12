package com.alztest.alztest;

// This is the main activity.

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alztest.alztest.Stimuli.Stimulus;
import com.alztest.alztest.Toolbox.AlzTestDatabaseManager;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * An activity representing a list of Options. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link OptionDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link OptionListFragment} and the item details
 * (if present) is a {@link OptionDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link OptionListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class OptionListActivity extends Activity
        implements OptionListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public static final String APPTAG = "AlzTest";
    private boolean mTwoPane;
    public ArrayList<Stimulus> stimuli;
    //private final static String FLURRY_APIKEY = "6D427V9KD6FG2ZZ8YC82";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_list);
        Log.v(APPTAG, "Option List Activity - this is the main activity");

        //init Flurry analytics
        //FlurryAgent.init(this, FLURRY_APIKEY); Test fairy suffices for now

        //init DB
        Log.v(OptionListActivity.APPTAG, "initialising Database");
        AlzTestDatabaseManager.init(this);

        if (findViewById(R.id.option_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((OptionListFragment) getFragmentManager()
                    .findFragmentById(R.id.option_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link OptionListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(Fragment item) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            //Bundle arguments = new Bundle();
            //arguments.putString(OptionDetailFragment.ARG_ITEM_ID, id);
            //OptionDetailFragment fragment = new OptionDetailFragment();
            //fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.option_detail_container, item)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, OptionDetailActivity.class);
            //detailIntent.putExtra(OptionDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    private void updateInternalListFromDB(){
        try{
            stimuli = (ArrayList<Stimulus>) AlzTestDatabaseManager.getInstance().getHelper().getStimuliDao().queryForAll();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> getAllUniqueCategories(){
        updateInternalListFromDB();
        ArrayList<String> cats = new ArrayList<String>();
        for (Stimulus s : stimuli) {
            if(!cats.contains(s.getCategory())) {
                cats.add(s.getCategory());
            }
        }
        return cats;
    }
}
