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

            DbHandle dbh = new DbHandle(database);
            if (dbh.getDao().getAll().size() <= 0)
                dbh.autoFillBaseInfo();

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
            // only directly support scalars here. (for now!) (IE: no composite die rolls: 1d12 + 2d4)
            DndEntity de = cache.get(id);
            if (de == null) {
                Log.v("db", "You're about to crash. Congratulations.");
                Log.v("db", "The database appears to be malformed. An entity lists a dependency that doesn't exist.");
                Log.v("db", "Perpetrating dependency ID: " + id);
                Log.v("db", "ID of target: " + target.getUuid());
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

    /**
     * Called when a database is determined to be empty.
     * Should fill in some default information so that all characters are ready to edit by the user
     * from the get-go.
     */
    private void autoFillBaseInfo()
    {
        // Todo: change these string literals to string resources.

        // Strength:
        DndEntity de = new DndEntity();
        de.setName("Strength");
        de.setDescription("Your character's physical capabilities");
        de.setUuid(DndEntity.ReservedIds.AttributeId.STRENGTH);
        de.setValue(10);
        de.setType(DndEntity.Type.ATTRIBUTE);
        de.setCharacterTag(true);
        db.dndDao().insert(de);

        de = new DndEntity();
        de.setName("str_mod");
        de.setDescription("A hack around (int)((value-10)/2)");
        de.setUuid(DndEntity.ReservedIds.AttributeId.STRENGTH_MOD);
        de.setValue(0);
        db.dndDao().insert(de);

        // Dexterity:
        de = new DndEntity();       // Todo: findout if just changing the uuid would allow reuse as temporary
        de.setName("Dexterity");
        de.setDescription("Your character's physical coordination");
        de.setUuid(DndEntity.ReservedIds.AttributeId.DEXTERITY);
        de.setValue(10);
        de.setType(DndEntity.Type.ATTRIBUTE);
        de.setCharacterTag(true);
        db.dndDao().insert(de);

        de = new DndEntity();
        de.setName("dex_mod");
        de.setDescription("A hack around (int)((value-10)/2)");
        de.setUuid(DndEntity.ReservedIds.AttributeId.DEXTERITY_MOD);
        de.setValue(0);
        db.dndDao().insert(de);

        // Constitution
        de = new DndEntity();
        de.setName("Constitution");
        de.setDescription("Your character's physical sturdiness");
        de.setUuid(DndEntity.ReservedIds.AttributeId.CONSTITUTION);
        de.setValue(10);
        de.setType(DndEntity.Type.ATTRIBUTE);
        de.setCharacterTag(true);
        db.dndDao().insert(de);

        de = new DndEntity();
        de.setName("con_mod");
        de.setDescription("A hack around (int)((value-10)/2)");
        de.setUuid(DndEntity.ReservedIds.AttributeId.CONSTITUTION_MOD);
        de.setValue(0);
        db.dndDao().insert(de);

        // Intelligence
        de = new DndEntity();
        de.setName("Intelligence");
        de.setDescription("Your character's book knowledge.");
        de.setUuid(DndEntity.ReservedIds.AttributeId.INTELLIGENCE);
        de.setValue(10);
        de.setType(DndEntity.Type.ATTRIBUTE);
        de.setCharacterTag(true);
        db.dndDao().insert(de);

        de = new DndEntity();
        de.setName("int_mod");
        de.setDescription("A hack around (int)((value-10)/2)");
        de.setUuid(DndEntity.ReservedIds.AttributeId.INTELLIGENCE_MOD);
        de.setValue(0);
        db.dndDao().insert(de);

        // Wisdom
        de = new DndEntity();
        de.setName("Wisdom");
        de.setDescription("Your character's mental power and wit");
        de.setUuid(DndEntity.ReservedIds.AttributeId.WISDOM);
        de.setValue(10);
        de.setType(DndEntity.Type.ATTRIBUTE);
        de.setCharacterTag(true);
        db.dndDao().insert(de);

        de = new DndEntity();
        de.setName("wis_mod");
        de.setDescription("A hack around (int)((value-10)/2)");
        de.setUuid(DndEntity.ReservedIds.AttributeId.WISDOM_MOD);
        de.setValue(0);
        db.dndDao().insert(de);

        // Charisma
        de = new DndEntity();
        de.setName("Charisma");
        de.setDescription("Your character's social capability");
        de.setUuid(DndEntity.ReservedIds.AttributeId.CHARISMA);
        de.setValue(10);
        de.setType(DndEntity.Type.ATTRIBUTE);
        de.setCharacterTag(true);
        db.dndDao().insert(de);

        de = new DndEntity();
        de.setName("cha_mod");
        de.setDescription("A hack around (int)((value-10)/2)");
        de.setUuid(DndEntity.ReservedIds.AttributeId.CHARISMA_MOD);
        de.setValue(0);
        db.dndDao().insert(de);

        de = new DndEntity();
        de.setName("Initiative");
        de.setDescription("Your character's readiness");
        de.setUuid(DndEntity.ReservedIds.MiscId.INITIATIVE);
        de.setValue(0);
        de.addAffector(DndEntity.ReservedIds.AttributeId.DEXTERITY_MOD);
        de.setCombatTag(true);
        de.setSkillTag(true);
        db.dndDao().insert(de);

        // insert skills here:

        // and now some example weapons.
        de = new DndEntity();
        de.setName("Example Weapon");
        de.setDescription("A stock long sword.");
        de.setValue(new Die(1, 6));
        de.setType(DndEntity.Type.WEAPON);
        de.addAffector(DndEntity.ReservedIds.AttributeId.STRENGTH_MOD);
        de.setInventoryTag(true);
        de.setCombatTag(true);
        db.dndDao().insert(de);
    }
}
