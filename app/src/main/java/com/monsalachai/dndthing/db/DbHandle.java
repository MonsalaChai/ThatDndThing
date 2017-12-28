package com.monsalachai.dndthing.db;

import android.arch.persistence.room.Room;

import com.monsalachai.dndthing.App;
import com.monsalachai.dndthing.entry.Entry;
import com.monsalachai.dndthing.entry.EntryFactory;
import com.monsalachai.dndthing.roll.Die;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mesalu on 12/28/2017.
 * This class is the front facing handle on the
 * internal db workings.
 *
 * Does stuff like convert a DndEntry to an Entry.
 */

public class DbHandle {
    public static DndDatabase db = null;

    public static final int COMBAT = 0x1;
    public static final int CHARACTER = 0x2;
    public static final int INVENTORY = 0x4;
    public static final int SKILL = 0x8;
    public static final int FEAT = 0x10;
    public static final int SPELL = 0x20;

    public DbHandle()
    {
        if (db == null)
            db = Room.databaseBuilder(App.getGlobalContext(),
                    DndDatabase.class,
                    "dnd-db")
                    .build();
    }

    List<Entry> fetchAllByTag(int flags)
    {
        LinkedList<Entry> ll = new LinkedList<>();

        if ((flags & COMBAT) != 0)
            __loadEntries(ll,  db.dndDao().getCombatEntities());
        if ((flags & CHARACTER) != 0)
            __loadEntries(ll, db.dndDao().getCharacterEntities());
        if ((flags & INVENTORY) != 0)
            __loadEntries(ll, db.dndDao().getInventoryEntities());
        if ((flags & SKILL) != 0)
            __loadEntries(ll, db.dndDao().getSkillEntities());
        if ((flags & FEAT) != 0)
            __loadEntries(ll, db.dndDao().getFeatEntities());
        if ((flags & SPELL) != 0)
            __loadEntries(ll,db.dndDao().getSpellEntities());

        return ll;
    }

    private void __loadEntries(List<Entry> dest, List<DndEntity> src) {
        // roll through all  src Entities (database rows)
        // and compose the Entry (this WILL be costly)
        for (DndEntity entity : src)
        {
            EntryFactory.EntryBuilder eb = new EntryFactory.EntryBuilder();
            // run through a friggin battery of composition checks.

            // load name and description.
            if (entity.getName() != null)
                eb.addLabel(entity.getName());

            if (entity.getDescription() != null)
                eb.addDescription(entity.getDescription());

            // check for rollability:
            Die d = entity.getValueAsDie();
            if (d != null)
                eb.setRollable(true).addRollDie(d.getDie()).addRollCoefficient(d.getCoefficient());
            else
                eb.setRollable(false);

            // roll through affectors and compositestuff.
            List<DndEntity> affectors = db.dndDao().getEntitiesById(entity.getAffectorsByIdAsArray());
        }
    }
}
