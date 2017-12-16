package com.monsalachai.dndthing;

import android.app.Activity;
import android.graphics.drawable.Drawable;
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
import com.monsalachai.dndthing.entry.WeaponEntry;

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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TDF", getClass().toString() + ".onCreateView invoked.");
        Log.d("TDF", "Container: " + ((container == null) ? "null" : container.toString()));
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_tab_detail, container, false);
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
        View v = ie.generateView(getContext());

        Drawable drawable = getActivity().getDrawable(R.drawable.tmpback);
        v.setBackground(drawable);
        rootView.addView(v);

        raw = "{\n" +
                "    \"typeid\" : 2,\n" +
                "    \"critable\" : true,\n" +
                "    \"die\" : 12,\n" +
                "    \"constant\" : 32,\n" +
                "    \"modifier\" : 3,\n" +
                "    \"rollable\" : true,\n" +
                "    \"label\" : \"Deathy Axe of Deathitude\", \n" +
                "    \"weapon\" : {\n" +
                "        \"type\" : \"magical\",\n" +
                "        \"damageType\" : \"Slashing\"\n" +
                "    },\n" +
                "    \"item\" : {\n" +
                "        \"count\" : 1,\n" +
                "        \"durability\" : 1337\n" +
                "    }\n" +
                "}";

        WeaponEntry we = (WeaponEntry) EntryFactory.deflate(raw);
        v = we.generateView(getContext());
        v.setBackground(drawable);
        rootView.addView(v);
        return rootView;
    }
}
