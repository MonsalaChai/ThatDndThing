package com.monsalachai.dndthing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.monsalachai.dndthing.db.DbHandle;
import com.monsalachai.dndthing.db.DndEntity;
import com.monsalachai.dndthing.entry.Entry;
import com.monsalachai.dndthing.entry.EntryFactory;
import com.monsalachai.dndthing.entry.EntryFactory.EntryBuilder;
import com.monsalachai.dndthing.entry.ItemEntry;
import com.monsalachai.dndthing.entry.SkillEntry;
import com.monsalachai.dndthing.entry.WeaponEntry;

import java.util.List;

/**
 * A fragment representing a single Tab detail screen.
 * This fragment is either contained in a {@link TabListActivity}
 * in two-pane mode (on tablets) or a {@link TabDetailActivity}
 * on handsets.
 */
public class TabDetailFragment extends Fragment {
    private int tabType;
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
        final String content = getArguments().getString("content");
        // convert from content-string to db int whatever
        if (content != null) {
            // Reference should be the same, so use .equals
            if (content.equals(TabListActivity.DndTabAdapter.COMBAT))
                tabType = DndEntity.Tag.COMBAT;
            else if (content.equals(TabListActivity.DndTabAdapter.CHARACTER))
                tabType = DndEntity.Tag.CHARACTER;
            else if (content.equals(TabListActivity.DndTabAdapter.FEATS))
                tabType = DndEntity.Tag.FEAT;
            else if (content.equals(TabListActivity.DndTabAdapter.INVENTORY))
                tabType = DndEntity.Tag.INVENTORY;
            else if (content.equals(TabListActivity.DndTabAdapter.SKILLS))
                tabType = DndEntity.Tag.SKILL;
            else if (content.equals(TabListActivity.DndTabAdapter.SPELLS))
                tabType = DndEntity.Tag.SPELL;
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
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_tab_detail, container, false);

        List<Entry> l = DbHandle.getInstance("Testing").fetchAllByTag(tabType);
        for (Entry e : l)
            rootView.addView(e.generateView(getContext()));


        /*
        Log.d("TDF", getClass().toString() + ".onCreateView invoked.");
        Log.d("TDF", "Container: " + ((container == null) ? "null" : container.toString()));

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
        }*/
        return rootView;
    }
}
