package com.monsalachai.dndthing;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.monsalachai.dndthing.entry.EntryFactory;
import com.monsalachai.dndthing.entry.EntryFactory.EntryBuilder;
import com.monsalachai.dndthing.entry.ItemEntry;
import com.monsalachai.dndthing.entry.SkillEntry;
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
        // Find database content based on arguments["content"]
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
                        "    \"desc\" : \"Does some healing stuff with " +
                        "some healing things to make you feel healed...-y.\"," +
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

        rootView.addView(v);

        EntryBuilder builder = new EntryBuilder();
        builder.setTypeWeapon().setRollable(true).setCritable(true)
                .addRollDie(12).addConstantValue(32).addRollCoefficient(3)
                .addLabel("Deathy Axe of Deathitude")
                .addDescription("The deadly axe that causes death.")
                .setWeaponSlashing().setWeaponMelee().addItemCount(1)
                .addItemDurability(1337).addItemWeight(15);

        WeaponEntry we = (WeaponEntry) builder.create();
        v = we.generateView(getContext());
        rootView.addView(v);

        builder.clear();
        builder.setTypeSkill().addLabel("Skill of deft Skillfulness").addSkillSource(5,
                "SkillfulFeat",
                "Your feet are so skillful you got a feat.")
                .addDescription("You have very skillful feet.")
                .setRollable(true).addRollDie(20).setCritable(true)
                .addConstantValue(5);
        SkillEntry se = (SkillEntry)builder.create();

        v = se.generateView(getContext());


        rootView.addView(v);

        for (int i = 0; i < 6; i++)
        {
            v = builder.create().generateView(getContext());
            rootView.addView(v);
        }
        return rootView;
    }
}
