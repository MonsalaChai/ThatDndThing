package com.monsalachai.dndthing.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
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
 * Created by mesalu on 12/27/2017.
 * This class represents a database....
 */

@Database(entities = {MainEntity.class}, version=3)
public abstract class AppDatabase extends RoomDatabase{
    public abstract MainDao mainDao();

    private static final HashMap<String, AppDatabase> dbs = new HashMap<>();
    public static AppDatabase getInstance(String character)
    {
        if (!dbs.containsKey(character))
        {
            AppDatabase database = Room.databaseBuilder(App.getGlobalContext(),
                    AppDatabase.class,
                    character)
                    // Todo: disable main thread queries and
                    // implement the use of a "pending" view whilst
                    // waiting for a query.
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
            dbs.put(character, database);
        }
        return dbs.get(character);
    }

    /**
     * Removes the held reference to a specified table.
     * Does not delete the table, just allows its Room.Database handle to be garbage collected
     * @param character the table name (ex: the name of the character)
     */
    public static void destroyInstance(String character)
    {
        if (dbs.containsKey(character))
            dbs.remove(character);
    }

    /**
     * Loads a list of all MainEntities marked with the associated tag(s).
     * @param flags a composite representing target tags. (Use MainEntity.Tag values)
     * @return a list of loaded Entry instances.
     */
    public List<Entry> fetchAllByTag(int flags)
    {
        LinkedList<Entry> ll = new LinkedList<>();

        // load entities by associated tags.
        if ((flags & MainEntity.Tag.COMBAT) != 0)
            loadEntries(ll,  mainDao().getCombatEntities());
        if ((flags & MainEntity.Tag.CHARACTER) != 0)
            loadEntries(ll, mainDao().getCharacterEntities());
        if ((flags & MainEntity.Tag.INVENTORY) != 0)
            loadEntries(ll, mainDao().getInventoryEntities());
        if ((flags & MainEntity.Tag.SKILL) != 0)
            loadEntries(ll, mainDao().getSkillEntities());
        if ((flags & MainEntity.Tag.FEAT) != 0)
            loadEntries(ll, mainDao().getFeatEntities());
        if ((flags & MainEntity.Tag.SPELL) != 0)
            loadEntries(ll, mainDao().getSpellEntities());

        return ll;
    }

    /**
     * Converts from top-level MainEntities to Entries.
     * @param dest Where to store freshly made Entry instances.
     * @param src A list containing all the top level Entity 'targets'
     */
    private void loadEntries(List<Entry> dest, List<MainEntity> src) {

        // 1)  build a composite hash map of required entities:
        HashMap<Long, MainEntity> hash = new HashMap<>();

        for (MainEntity e : src)
            hash.put(e.getUuid(), e);

        // 2) now load all "dependency" entities:
        for (MainEntity e  : src)
            for (long id : e.getAffectorsById())
                if (!hash.containsKey(id))
                    hash.put(id, mainDao().getEntityById(id));

        // 3) All required entities are now loaded into hash.
        //    So compose (as complete as possible) entries from
        //    the base entities.

        for (MainEntity e : src)
            composeEntry(e, hash,  dest);
    }

    /**
     * Creates an Entry object from the target MainEntity, using the dependency
     * entities buffered in cache,  and stores it in dest.
     * @param target The "top level" Entity being converted to an  Entry
     * @param cache  A map that associates all (pre-fetched) Entities to their  luid
     * @param dest   A output list to store the newly created Entry in.
     */
    private void composeEntry(MainEntity target, Map<Long, MainEntity> cache, List<Entry> dest) {
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
            MainEntity de = cache.get(id);
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

        if (((type & MainEntity.Type.ITEM) != 0) || (type & MainEntity.Type.WEAPON) != 0) {
            eb.setTypeItem();
            // do other crap here (when applicable.
        }
        if ((type & MainEntity.Type.WEAPON) != 0)
        {
            eb.setTypeWeapon();
            // set other weapon attributes here when applicable.
        }
        if ((type & MainEntity.Type.SKILL) != 0)
        {
            eb.setTypeSkill();

            // create skill misc sources list.
            for (long id : target.getAffectorsById())
            {
                MainEntity de = cache.get(id);
                eb.addSkillSource(de.getValueAsInt(),
                        de.getName(),
                        de.getDescription());
            }
        }
        if ((type & MainEntity.Type.FEAT) != 0)
        {
            eb.setTypeFeat();
        }
        if ((type & MainEntity.Type.SPELL) != 0)
        {
            eb.setTypeSpell();
        }
        dest.add(eb.create());
    }
}
