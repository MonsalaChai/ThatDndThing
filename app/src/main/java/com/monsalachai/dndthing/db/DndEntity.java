package com.monsalachai.dndthing.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.monsalachai.dndthing.roll.Die;

import java.util.Collection;
import java.util.Vector;

/**
 * Created by mesalu on 12/27/2017.
 * Represents a database structure.
 *
 * This is currently an inefficient working model.
 */

@Entity(tableName = "entities")
public class DndEntity {
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
    @PrimaryKey(autoGenerate = true)
    private int luid;   // locally unique identifier

    @ColumnInfo(name="name")
    private String name;

    // the value of this entity, can be a scalar or a Die
    @ColumnInfo(name="value")
    private String value;

    @ColumnInfo(name="desciption")
    private String description;

    // The other entities that this entity affects
    @ColumnInfo(name="affects")
    private String affects;

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

    public DndEntity(int luid)
    {
        this.luid = luid;
    }

    /**
     * Adds a locally unique identifier to the list of affectors.
     * If already in the list, it is not reappended.
     * @param luid the luid to add.
     */
    public void addAffector(int luid)
    {
        for (String substring : affectors.split(","))
            if  (Integer.parseInt(substring) == luid)
                return;
        // luid not in it:
        affectors += String.format(",%d", luid);
    }

    // luid setter  and getter
    public void setLuid(int id)
    {
        luid = id;
    }

    public int getLuid()
    {
        return luid;
    }

    // name setter and getter.
    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }

    // description setter and getter.
    public void setDescription(String desc) { description = desc; }
    public String getDescription() { return description; }

    // Affectors getter (use addAffector for setting.)
    public Vector<Integer> getAffectorsById()
    {
        Vector<Integer> v = new Vector<>();

        for (String substring : affectors.split(","))
            v.add(Integer.parseInt(substring));

        return v;
    }

    public int[] getAffectorsByIdAsArray()
    {
        String[] arr = affectors.split(",");
        int[] ret = new int[arr.length];

        for (int i = 0 ; i < arr.length; i++)
            ret[i] = Integer.parseInt(arr[i]);

        return ret;
    }

    // Affects setter and "getter"
    public void setAffectees(String s)
    {
        affects = s;
    }
    public void setAffectees(Collection<Integer> l)
    {
        StringBuilder sb = new StringBuilder();
        for (Integer i : l)
            sb.append(",").append(i);

        affects = sb.toString();
    }
    public Vector<Integer> getAffecteesById()
    {
        Vector<Integer> v = new Vector<>();

        for (String substring  :  affects.split(","))
            v.add(Integer.parseInt(substring));

        return v;
    }

    // value setter and getter
    public void setValue(int value)
    {
        this.value = Integer.toString(value);
    }
    public void setValue(String value)
    {
        // fly blind (for now)
        this.value = value;
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

    // For room compatability:
    public void setAffects(String s) { affects = s; }
    public String getAffects() { return affects; }

    public void setAffectors(String s) { affectors = s; }
    public String getAffectors() { return affectors; }

    // todo: do some misuse-checking on type arg
    public void setType(int type) { this.type = type; }
    public int getType() { return type; }

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
