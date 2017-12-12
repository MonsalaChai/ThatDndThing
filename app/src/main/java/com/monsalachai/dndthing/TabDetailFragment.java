package com.monsalachai.dndthing;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monsalachai.dndthing.dummy.DummyContent;
import com.monsalachai.dndthing.entry.EntryFactory;
import com.monsalachai.dndthing.entry.ItemEntry;

/**
 * A fragment representing a single Tab detail screen.
 * This fragment is either contained in a {@link TabListActivity}
 * in two-pane mode (on tablets) or a {@link TabDetailActivity}
 * on handsets.
 */
public class TabDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TabDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("title")) {
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
            if (appBarLayout != null)
                appBarLayout.setTitle(getArguments().getString("title"));
        }

        String raw =
                "{\n" +
                "    \"rollable\":true,\n" +
                "    \"die\":8,\n" +
                "    \"constant\":3,\n" +
                "    \"label\":\"Potion of Potioning\",\n" +
                "    \"typeid\":1,\n" +
                "    \"item\" : {\n" +
                "        \"weight\":150,\n" +
                "        \"durability\":10,\n" +
                "        \"consumable\":false,\n" +
                "        \"wondrous\":true\n" +
                "    }\n" +
                "\n" +
                "}";

        ItemEntry ie = (ItemEntry) EntryFactory.deflate(raw);

        Log.i("ETest", ie.getRoll());
        Log.i("ETest", String.format("Rolled: %d", ie.performRoll()));
        Log.i("ETest", ie.serialize().toString());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        View rootView = inflater.inflate(R.layout.tab_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.tab_detail)).setText(mItem.details);
        }

        return rootView;
        */
        String raw =
                "{\n" +
                        "    \"rollable\":true,\n" +
                        "    \"die\":8,\n" +
                        "    \"constant\":3,\n" +
                        "    \"label\":\"Potion of Potioning\",\n" +
                        "    \"typeid\":1,\n" +
                        "    \"item\" : {\n" +
                        "        \"weight\":150,\n" +
                        "        \"durability\":10,\n" +
                        "        \"consumable\":false,\n" +
                        "        \"wondrous\":true\n" +
                        "    }\n" +
                        "\n" +
                        "}";

        ItemEntry ie = (ItemEntry) EntryFactory.deflate(raw);
        return ie.generateView(getActivity().getApplicationContext());
    }
}
