package com.monsalachai.dndthing.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import com.monsalachai.dndthing.App;
import com.monsalachai.dndthing.R;
import com.monsalachai.dndthing.roll.Die;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by mesalu on 12/27/2017.
 * Represents a database structure.
 *
 * This is currently an inefficient working model.
 */

@Entity(tableName = "entities")
public class MainEntity {
    public static class Tag {
        public static final int COMBAT = 0x1;
        public static final int CHARACTER = 0x2;
        public static final int INVENTORY = 0x4;
        public static final int SKILL = 0x8;
        public static final int FEAT = 0x10;
        public static final int SPELL = 0x20;
    }
    public static class Type {
        public static final int GENERIC = 0x1;
        public static final int ITEM = 0x2;
        public static final int WEAPON = 0x4;
        public static final int SKILL = 0x8;
        public static final int FEAT = 0x10;
        public static final int SPELL = 0x20;
        public static final int ATTRIBUTE = 0x40;
    }

    // The ResrvedIds class defines Ids with special meaning.
    // It can contain further nested classes for the same purpose
    // but it is up to the coder to ensure no conflicts arise.
    // UUIDs  are generated from time of creation, and therefore
    // all values below 1514762607900 (give or take several thousand)
    // should be fine.
    public static class ReservedIds {
        // extra note: a convenience static method for
        // assigning out Ids is not supplied to ensure consistency
        // between revisions.
        public static class AttributeId{
            public static final long STRENGTH     = 1;
            public static final long DEXTERITY    = 2;
            public static final long CONSTITUTION = 3;
            public static final long INTELLIGENCE = 4;
            public static final long WISDOM       = 5;
            public static final long CHARISMA     = 6;
        }
        // more reserved Ids to come.
    }
    private static final AtomicLong ts = new AtomicLong();
    private static long generateUuidFromTime()
    {
        long micros = System.currentTimeMillis() * 1000;
        for (;;) {
            long value = ts.get();
            if (micros <= value)
                micros = value + 1; // we've already used this
                                    // value for a UUID (That was fast!)
            if (ts.compareAndSet(value, micros))
                return micros;
        }
    }
    @PrimaryKey
    private long uuid;   // locally unique identifier

    @ColumnInfo(name="name")
    private String name;

    // the value of this entity, can be a scalar or a Die
    @ColumnInfo(name="value")
    private String value;

    @ColumnInfo(name="description")
    private String description;

    // This has been removed (for now) as it is currently unused, and prone to falling behind the
    // schema. The initial intent was to use the doubly-linked state for error checking and correcting
    // The other entities that this entity affects
    //@ColumnInfo(name="affects")
    //private String affects;

    // Todo: pick a better name than "affector".
    @ColumnInfo(name="affectors")
    private String affectors;

    @ColumnInfo(name="type")
    private int type;

    // The following ColumnInfo members reflect which
    // UI tabs this  entity applies to.
    // They appear as separate "boolean" fields in order
    // to improve access rate.
    @ColumnInfo(name="combatTag")
    private int combatTag;

    @ColumnInfo(name="characterTag")
    private int characterTag;

    @ColumnInfo(name="skillTag")
    private int skillTag;

    @ColumnInfo(name="inventoryTag")
    private int inventoryTag;

    @ColumnInfo(name="featTag")
    private int featTag;

    @ColumnInfo(name="spellTag")
    private int spellTag;

    /**
     * Default constructor for MainEntity, sets default values. (Primarily to avoid NPEs)
     */
    public MainEntity()
    {
        uuid = generateUuidFromTime();
        name = App.getGlobalContext().getString(R.string.unknown_entry);
        description = App.getGlobalContext().getString(R.string.unused_long_desc);
        value = "0";
        affectors = "";
    }

    /**
     * Adds a locally unique identifier to the list of affectors.
     * If already in the list, it is not reappended.
     * @param luid the uuid to add.
     */
    public void addAffector(long luid)
    {
        for (String substring : affectors.split(","))
            if (substring.length() > 0)
                if  (Long.parseLong(substring) == luid)
                    return;

        // uuid not in it:
        affectors += String.format(",%d", luid);
    }

    // Convenience override for addAffector(long)
    public void addAffector(MainEntity other)
    {
        addAffector(other.getUuid());
    }

    /**
     * Returns a list of all UUIDs associated to other entities that
     * affect this entitiy.
     * DOES NOT confirm they actually exist.
     * @return a list of entity IDs
     */
    public List<Long> getAffectorsById()
    {
        LinkedList<Long> ll = new LinkedList<>();
        for (String substring : affectors.split(","))
            if (substring.length() > 0)
                ll.add(Long.parseLong(substring));

        return ll;
    }

    //*************************************************************************//
    // And now for compliance setters and getters:
    //*************************************************************************//

    // uuid setter  and getter
    public void setUuid(long id) { uuid = id; }
    public long getUuid()
    {
        return uuid;
    }

    // name setter and getter.
    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }

    // description setter and getter.
    public void setDescription(String desc) { description = desc; }
    public String getDescription() { return description; }

    // value setter and getter (the first setter is for compliance, the latter for convenience)
    public void setValue(String value)
    {
        // fly blind (for now, though checking value really should be done.)
        this.value = value;
    }
    public void setValue(int value)
    {
        this.value = Integer.toString(value);
    }
    public void setValue(Die d)
    {
        value = d.toString();
    }
    public String getValue()
    {
        return this.value;
    }
    public int getValueAsInt()
    {
        return Integer.parseInt(value);
    }
    public Die getValueAsDie()
    {
        if (value.contains("d"))
        {
            String[] split = value.split("d");
            int c = Integer.parseInt(split[0]);
            int d = Integer.parseInt(split[1]);

            return new Die(c, d);
        }
        return null;
    }

    // Affector setter and getters (these exist soley for Room compliance and are therefore package
    // private
    void setAffectors(String s) { affectors = s; }
    String getAffectors() { return affectors; }

    // todo: do some misuse-checking on type arg
    public void setType(int type) { this.type = type; }
    public int getType() { return type; }

    // Todo: Investigate using a single int, and bitwise queries instead.
    // Ideally the Tag members are to be treated as a boolean.
    // however for sqllite compatibility, they need to be an int internally
    // So fall back to more-or-less standard C 'boolean' practice.
    public void setCombatTag(int c) { combatTag = (c != 0) ? 1 : 0; }
    public int  getCombatTag() { return combatTag; }
    public boolean getCombatAsBoolean() { return (combatTag == 1); }
    public void setCombatTag(boolean flag) { combatTag = (flag) ? 1  : 0;}

    public void setCharacterTag(int c) { characterTag = (c != 0) ? 1 : 0;}
    public int getCharacterTag() { return characterTag; }
    public void setCharacterTag(boolean flag) { characterTag = (flag) ? 1  : 0; }
    public boolean getCharacterTagAsBoolean() { return (characterTag == 1); }

    public void setSkillTag(int c) { skillTag = (c != 0) ? 1 : 0; }
    public int getSkillTag() { return skillTag; }
    public void setSkillTag(boolean flag) { skillTag = (flag) ? 1  : 0; }
    public boolean getSkillTagAsBoolean() { return (skillTag == 1); }

    public void setInventoryTag(int c) { inventoryTag = (c != 0) ? 1 : 0; }
    public int getInventoryTag() { return inventoryTag;}
    public void setInventoryTag(boolean flag) { inventoryTag = (flag) ? 1  : 0; }
    public boolean getInventoryTagAsBoolean() { return (inventoryTag == 1); }

    public void setFeatTag(int c) { featTag = (c != 0) ? 1 : 0;}
    public int getFeatTag() { return featTag; }
    public void setFeatTag(boolean flag) { featTag = (flag) ? 1  : 0; }
    public boolean getFeatTagAsBoolean() { return (featTag == 1); }

    public void setSpellTag(int c) { spellTag = (c != 0) ? 1 : 0; }
    public int getSpellTag() { return spellTag; }
    public void setSpellTag(boolean flag) { spellTag  = (flag) ? 1  : 0; }
    public boolean getSpellTagAsBoolean() { return (spellTag == 1); }
}
