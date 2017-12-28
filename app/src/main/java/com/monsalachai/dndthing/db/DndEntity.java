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

@Entity
public class DndEntity {
    @PrimaryKey
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


    // The following ColumnInfo members reflect which
    // UI tabs this  entity applies to.
    @ColumnInfo(name="combat")
    private int combat;

    @ColumnInfo(name="character")
    private int character;

    @ColumnInfo(name="skill")
    private int skill;

    @ColumnInfo(name="inventory")
    private int inventory;

    @ColumnInfo(name="feats")
    private int feat;

    @ColumnInfo(name="spells")
    private int spell;

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

    public void setCombatTag(boolean flag) { combat = (flag) ? 1  : 0;}
    public boolean getCombatTag() { return (combat == 1); }

    public void setCharacterTag(boolean flag) { character = (flag) ? 1  : 0; }
    public boolean getCharacterTag() { return (character == 1); }

    public void setSkillTag(boolean flag) { skill  = (flag) ? 1  : 0; }
    public boolean getSkillTag() { return (skill == 1); }

    public void setInventoryTag(boolean flag) { inventory  = (flag) ? 1  : 0; }
    public boolean getInventoryTag() { return (inventory == 1); }

    public void setFeatTag(boolean flag) { feat  = (flag) ? 1  : 0; }
    public boolean getFeatTag() { return (feat == 1); }

    public void setSpellTag(boolean flag) { spell  = (flag) ? 1  : 0; }
    public boolean getSpellTag() { return (spell == 1); }
}
