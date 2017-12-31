package com.monsalachai.dndthing.db;

import android.arch.persistence.room.Room;
import android.util.Log;

import com.monsalachai.dndthing.App;
import com.monsalachai.dndthing.entry.Entry;
import com.monsalachai.dndthing.entry.EntryFactory;
import com.monsalachai.dndthing.roll.Die;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by mesalu on 12/28/2017.
 * This class is the front facing handle on the
 * internal db workings.
 *
 * Does stuff like convert a DndEntry to an Entry.
 */

// Todo: figure out "implicit"  modifiers (ex: melee weapons <- str)
public class DbHandle {
    // this instance member is the database instance associated to this.
    private DndDatabase db;
    // This class member associates instances to table-names.
    private static final HashMap<String, DbHandle> dbs = new HashMap<>();

    // mapped singleton constructor.
    public static DbHandle getInstance(String character)
    {
        if (!dbs.containsKey(character)) {
            DndDatabase database = Room.databaseBuilder(App.getGlobalContext(),
                    DndDatabase.class,
                    character)
                    // Todo: disable main thread queries and
                    // implement the use of a "pending" view whilst
                    // waiting for a query.
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
            dbs.put(character, new DbHandle(database));
        }
        return dbs.get(character);
    }

    // singleton instance 'destroyer'.
    public static void destroyInstance(String character)
    {
        if (dbs.containsKey(character))
            dbs.remove(character);
    }

    private DbHandle(DndDatabase db)
    {
        this.db = db;
    }

    public DndDao getDao()
    {
        return this.db.dndDao();
    }

    public List<Entry> fetchAllByTag(int flags)
    {
        LinkedList<Entry> ll = new LinkedList<>();

        // load entities by associated tags.
        if ((flags & DndEntity.Tag.COMBAT) != 0)
            __loadEntries(ll,  db.dndDao().getCombatEntities());
        if ((flags & DndEntity.Tag.CHARACTER) != 0)
            __loadEntries(ll, db.dndDao().getCharacterEntities());
        if ((flags & DndEntity.Tag.INVENTORY) != 0)
            __loadEntries(ll, db.dndDao().getInventoryEntities());
        if ((flags & DndEntity.Tag.SKILL) != 0)
            __loadEntries(ll, db.dndDao().getSkillEntities());
        if ((flags & DndEntity.Tag.FEAT) != 0)
            __loadEntries(ll, db.dndDao().getFeatEntities());
        if ((flags & DndEntity.Tag.SPELL) != 0)
            __loadEntries(ll,db.dndDao().getSpellEntities());

        return ll;
    }

    private void __loadEntries(List<Entry> dest, List<DndEntity> src) {

        // 1)  build a composite hash map of required entities:
        HashMap<Long, DndEntity> hash = new HashMap<>();

        for (DndEntity e : src)
            hash.put(e.getUuid(), e);

        // 2) now load all "dependency" entities:
        for (DndEntity e  : src)
            for (long id : e.getAffectorsById())
                if (!hash.containsKey(id))
                    hash.put(id, db.dndDao().getEntityById(id));

        // 3) All required entities are now loaded into hash.
        //    So compose (as complete as possible) entries from
        //    the base entities.

        for (DndEntity e : src)
            __composeEntry(e, hash,  dest);
    }

    /**
     * Creates an Entry object from the target DndEntity, using the dependency
     * entities buffered in cache,  and stores it in dest.
     * @param target The "top level" Entity being converted to an  Entry
     * @param cache  A map that associates all (pre-fetched) Entities to their  luid
     * @param dest   A output list to store the newly created Entry in.
     */
    private void __composeEntry(DndEntity target, Map<Long, DndEntity> cache, List<Entry> dest) {
        EntryFactory.EntryBuilder eb = new EntryFactory.EntryBuilder();
        // attempt to copy name and description:
        String tmp = target.getName();
        if (tmp != null)
            eb.addLabel(tmp);
        tmp = target.getDescription();
        if (tmp != null)
            eb.addDescription(tmp);

        // check for rollability:
        Die d = target.getValueAsDie();
        if (d != null)
            eb.setRollable(true).addRollDie(d.getDie()).addRollCoefficient(d.getCoefficient());
        else
            eb.setRollable(false);

        // load composite value
        int value = (d == null) ? target.getValueAsInt() : 0;
        for (long id : target.getAffectorsById()) {
            // only directly support scalars here. (for now!)
            DndEntity de = cache.get(id);
            if (de == null) {
                Log.v("db", "You're about to crash. Congratulations.");
                Log.v("db", "The database appears to be malformed. An entity lists a dependency that doesn't exist.");
            }

            if (de.getValueAsDie() != null)
                throw new RuntimeException("Composite die entities not yet supported");
            value += de.getValueAsInt();
        }
        eb.addConstantValue(value);

        // now look at Type and move from there.
        final int type = target.getType();

        if (((type & DndEntity.Type.ITEM) != 0) || (type & DndEntity.Type.WEAPON) != 0) {
            eb.setTypeItem();
            // do other crap here (when applicable.
        }
        if ((type & DndEntity.Type.WEAPON) != 0)
        {
            eb.setTypeWeapon();
            // set other weapon attributes here when applicable.
        }
        if ((type & DndEntity.Type.SKILL) != 0)
        {
            eb.setTypeSkill();

            // create skill misc sources list.
            for (long id : target.getAffectorsById())
            {
                DndEntity de = cache.get(id);
                eb.addSkillSource(de.getValueAsInt(),
                                  de.getName(),
                                  de.getDescription());
            }
        }
        if ((type & DndEntity.Type.FEAT) != 0)
        {
            eb.setTypeFeat();
        }
        if ((type & DndEntity.Type.SPELL) != 0)
        {
            eb.setTypeSpell();
        }
        dest.add(eb.create());
    }
}
