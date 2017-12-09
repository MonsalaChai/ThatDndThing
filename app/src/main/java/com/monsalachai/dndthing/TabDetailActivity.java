package com.monsalachai.dndthing;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import com.monsalachai.dndthing.entry.BaseEntry;
import com.monsalachai.dndthing.entry.EntryFactory;
import com.monsalachai.dndthing.entry.WeaponEntry;

/**
 * An activity representing a single Tab detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link TabListActivity}.
 */
public class TabDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Test some of the BaseEntry stuff:
        String raw =
                "{\n" +
                "    \"typeid\" : 2,\n" +
                "    \"rollable\" : true,\n" +
                "    \"label\" : \"Test Sword of Swordiness\",\n" +
                "    \"constant\" : 23,\n" +
                "    \"die\": 12,\n" +
                "    \"modifier\" : 2,\n" +
                "\n" +
                "    \"itemWeight\" : 150,\n" +
                "    \"itemDurability\" : 10,\n" +
                "    \"itemConsumable\" : false,\n" +
                "    \"itemWondrous\"   : false,\n" +
                "\n" +
                "    \"weaponType\" : \"g\",\n" +
                "    \"weaponDamage\": \"s\"\n" +
                "}";

        WeaponEntry we = (WeaponEntry)EntryFactory.deflate(raw);

        Log.i("ETest", we.serialize().toString());
        Log.i("ETest", we.getRoll());

        setContentView(R.layout.activity_tab_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(TabDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(TabDetailFragment.ARG_ITEM_ID));
            TabDetailFragment fragment = new TabDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tab_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, TabListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
