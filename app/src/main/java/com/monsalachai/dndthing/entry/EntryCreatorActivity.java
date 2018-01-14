package com.monsalachai.dndthing.entry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.monsalachai.dndthing.R;

public class EntryCreatorActivity extends AppCompatActivity {

    /**
     * A statically available click listener that can be used to standardize the launch of this activity.
     */
    public static View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, EntryCreatorActivity.class);
            context.startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_creator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
