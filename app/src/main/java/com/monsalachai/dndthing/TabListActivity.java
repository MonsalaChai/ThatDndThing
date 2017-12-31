package com.monsalachai.dndthing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monsalachai.dndthing.db.DbHandle;
import com.monsalachai.dndthing.db.DndEntity;
import com.monsalachai.dndthing.roll.Die;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Tabs. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link TabDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class TabListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.tab_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.tab_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        /*  Uncomment to blow out database.
        // 'automate' a storage wipe. (I got lazy)
        DbHandle.getInstance("Testing").getDao().deleteAll(
                DbHandle.getInstance("Testing").getDao().getAll()
        );
        */
        // This code exists entirely for testing purposes.
        // remove it before any semblance of production:
        if (DbHandle.getInstance("Testing").getDao().getAll().size() <= 0)
        {
            Log.i("TLA", "Autofilling some table entries.");
            DbHandle dbh = DbHandle.getInstance("Testing");
            DndEntity de = new DndEntity();
            de.setCombatTag(true);
            de.setInventoryTag(true);
            de.setType(DndEntity.Type.WEAPON);
            de.setName("Deathy Axe of Deathitude II");
            de.setDescription("The second axe of its kind. Freshly imported from the Persistent Lands");
            de.setValue(new Die(3, 12));

            // add the constant affect for the deathy axe.
            DndEntity de2 = new DndEntity();
            de2.setValue(33);
            de.addAffector(de2.getUuid());
            de2.setAffectee(de.getUuid());

            // todo subclas DndEntity for AttributeEntities that handle
            // auto-converting value and modifier.
            DndEntity destr  = new DndEntity();
            destr.setUuid(DndEntity.ReservedIds.AttributeId.STRENGTH);
            destr.setValue(5);  // for now though: just supply the modifier.
            destr.setType(DndEntity.Type.ATTRIBUTE);
            destr.setName("Strength");
            destr.setDescription("Your Character's strength (modifier, not score)");
            destr.setCharacterTag(true);
            de.addAffector(destr);

            dbh.getDao().insertAll(de, de2, destr);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
        recyclerView.setAdapter(new DndTabAdapter());
    }
    public class DndTabAdapter extends RecyclerView.Adapter<DndTabAdapter.ViewHolder>
    {
        public static final String CHARACTER = "Character";
        public static final String INVENTORY = "Inventory";
        public static final String COMBAT    = "Combat";
        public static final String SPELLS    = "Spells";
        public static final String SKILLS    = "Skills";
        public static final String FEATS     = "Feats";
        List<String> mTitles;
        DndTabAdapter()
        {
            mTitles = new ArrayList<>();
            mTitles.add(CHARACTER);
            mTitles.add(INVENTORY);
            mTitles.add(COMBAT);
            mTitles.add(SPELLS);
            mTitles.add(SKILLS);
            mTitles.add(FEATS);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tab_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.mTitleView.setText(mTitles.get(position));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString("title", mTitles.get(position));
                        TabDetailFragment fragment = new TabDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.tab_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, TabDetailActivity.class);
                        intent.putExtra("title", mTitles.get(position));

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mTitles.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTitleView;
            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTitleView = view.findViewById(R.id.content);
            }
        }
    }

    /*
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tab_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(TabDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        TabDetailFragment fragment = new TabDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.tab_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, TabDetailActivity.class);
                        intent.putExtra(TabDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
    */
}
