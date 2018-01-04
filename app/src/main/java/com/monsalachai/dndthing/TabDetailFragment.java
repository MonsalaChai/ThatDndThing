package com.monsalachai.dndthing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.monsalachai.dndthing.db.AppDatabase;
import com.monsalachai.dndthing.db.MainEntity;
import com.monsalachai.dndthing.entry.Entry;

import java.util.List;

/**
 * A fragment representing a single Tab detail screen.
 * This fragment is either contained in a {@link TabListActivity}
 * in two-pane mode (on tablets) or a {@link TabDetailActivity}
 * on handsets.
 */
public class TabDetailFragment extends Fragment {
    private int tabType;
    public static final String CONTENT_ARG = "content";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TabDetailFragment() {
        // Todo:
        // Write a second "initial" contructor that fetches data
        // and stores it in a static construct. Then load from that construct here.
        // That way expensive data retrieval is only done once per tab activation.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TDF", "onCreate");

        // Load entries from db based on content type.
        final String content = getArguments().getString(CONTENT_ARG);
        // convert from content-string to db int whatever
        if (content != null) {
            // Reference should be the same, so use .equals
            if (content.equals(TabListActivity.DndTabAdapter.COMBAT))
                tabType = MainEntity.Tag.COMBAT;
            else if (content.equals(TabListActivity.DndTabAdapter.CHARACTER))
                tabType = MainEntity.Tag.CHARACTER;
            else if (content.equals(TabListActivity.DndTabAdapter.FEATS))
                tabType = MainEntity.Tag.FEAT;
            else if (content.equals(TabListActivity.DndTabAdapter.INVENTORY))
                tabType = MainEntity.Tag.INVENTORY;
            else if (content.equals(TabListActivity.DndTabAdapter.SKILLS))
                tabType = MainEntity.Tag.SKILL;
            else if (content.equals(TabListActivity.DndTabAdapter.SPELLS))
                tabType = MainEntity.Tag.SPELL;
            else
                throw new RuntimeException("Apparently, the reference  is not the same.");
        }
        else
            Log.wtf("TDF", "THE ARGUMENTS BUNDLE WAS NOT SET. PAAAAAAAANIIIIIIIC");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // NOTE: This is called per rotate, so avoid any expensive operations here.
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_tab_detail,
                container, false);

        List<Entry> l = AppDatabase.getInstance("Testing").fetchAllByTag(tabType);
        for (Entry e : l)
            rootView.addView(e.generateView(getContext()));

        return rootView;
    }
}
