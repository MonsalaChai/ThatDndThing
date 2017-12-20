package com.monsalachai.dndthing.entry;

import android.util.Log;

import java.lang.Math;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Created by mesalu on 12/9/17.
 */

public class EntryFactory {

    public class EntryBuilder {
        protected JsonObject mBuildSite;

        public EntryBuilder() {
            mBuildSite = new JsonObject();
            mBuildSite.addProperty("typeid", 0);
        }

        /**
         * Creates the Entry object from the current state of the Builder object.
         *
         * @return a new Entry object.
         */
        public Entry create() {
            return EntryFactory.deflate(mBuildSite);
        }

        /**
         * Add the die size to the entry.
         * if rollable is not already set, it will be set
         * as a byproduct of this call.
         * <p>
         * Negative numbers disallowed
         *
         * @param die the size off the die to add. (ex, 20 for a d20)
         * @return this
         */
        public EntryBuilder addRollDie(int die) {
            mBuildSite.addProperty("rollable", true);
            mBuildSite.addProperty("die", Math.abs(die));
            return this;
        }

        /**
         * Adds a constant to the rollable object.
         * <p>
         * Sets rollable to true if not already set.
         * Negative numbers disallowed
         *
         * @param value the value to assign as the constant.
         * @return this
         */
        public EntryBuilder addConstantValue(int value) {
            mBuildSite.addProperty("rollable", true);
            mBuildSite.addProperty("consant", Math.abs(value));
            return this;
        }

        /**
         * Adds the given coefficient to the Entry object.
         * The coefficient is the amount of times to roll the assigned die.
         * Ex: the Entry has a d12 set as the die size.
         * A coefficient of 3 would turn that into a 3d12 roll.
         * <p>
         * Negative numbers disallowed.
         * sets rollable to true if not otherwise set.
         *
         * @param coefficient
         * @return this
         */
        public EntryBuilder addRollCoefficient(int coefficient) {
            mBuildSite.addProperty("rollable", true);
            mBuildSite.addProperty("coefficient", Math.abs(coefficient));
            return this;
        }

        /**
         * Adds the given label to the Entry.
         * The label is equivalent to a name.
         *
         * @param label the string to set as the label.
         * @return this
         */
        public EntryBuilder addLabel(String label) {
            mBuildSite.addProperty("label", label);
            return this;
        }

        /**
         * Functions as addLabel(String) but accepts string builders for convenience.
         *
         * @param sbuilder the string builder to convert to the label.
         * @return this
         */
        public EntryBuilder addLabel(StringBuilder sbuilder) {
            mBuildSite.addProperty("label", sbuilder.toString());
            return this;
        }

        /**
         * Adds a description string to the Entry.
         *
         * @param desc the string that describes this entry.
         * @return this
         */
        public EntryBuilder addDescription(String desc) {
            mBuildSite.addProperty("desc", desc);
            return this;
        }

        /**
         * Functions as addDescription(String) but accepts a string builder for convenience.
         *
         * @param builder the string builder to convert to the description.
         * @return this
         */
        public EntryBuilder addDescription(StringBuilder builder) {
            mBuildSite.addProperty("desc", builder.toString());
            return this;
        }

        /**
         * Sets whether the constructing Entry has critical capabilities.
         *
         * @param canCrit The value to set critable state to.
         * @return this
         */
        public EntryBuilder setCritable(boolean canCrit) {
            mBuildSite.addProperty("critable", canCrit);
            return this;
        }

        /**
         * Sets whether the constructing entry can be rolled.
         *
         * @param canRoll The value to set rollable to.
         * @return this
         */
        public EntryBuilder setRollable(boolean canRoll) {
            mBuildSite.addProperty("rollable", canRoll);
            return this;
        }

        /**
         * Sets the Entry's type to reflect that it is representing an Item.
         * This affects the entry at construction time.
         *
         * @return this
         */
        public EntryBuilder setTypeItem() {
            // don't change real type if typeid indicates a subtype
            // (presently: weapon)
            if (mBuildSite.get("typeid").getAsInt() == 2)
                return this;    // don't change type.

            mBuildSite.addProperty("typeid", 1);
            return this;
        }

        /**
         * Sets the Entry's type to reflect that it is representing a Weapon
         * This affects the Entry at construction time.
         * Weapon is considered a subtype of Item
         *
         * @return this
         */
        public EntryBuilder setTypeWeapon() {
            mBuildSite.addProperty("typeid", 2);
            return this;
        }

        /**
         * Sets the Entry's type to reflect that it is a Skill
         *
         * @return this
         */
        public EntryBuilder setTypeSkill() {
            mBuildSite.addProperty("typeid", 3);
            return this;
        }

        /**
         * Sets the Entry's type to reflect that it is a Character
         *
         * @return this
         */
        public EntryBuilder setTypeCharacter() {
            mBuildSite.addProperty("typeid", 4);
            return this;
        }

        /**
         * Sets the Entry's type to reflect that it is representing an Attribute
         * @return this
         */
        public EntryBuilder setTypeAttribute()
        {
            mBuildSite.addProperty("typeid", 5);
            return this;
        }

        /**
         * Sets the Entry's type to reflect that it is representing a Spell
         * @return this
         */
        public EntryBuilder setTypeSpell()
        {
            mBuildSite.addProperty("typeid", 6);
            return this;
        }

        /**
         * Sets the Entry's type to reflect that it is representing a Feat
         * @return this
         */
        public EntryBuilder setTypeFeat()
        {
            mBuildSite.addProperty("typeid", 7);
            return this;
        }
    }

    public static Entry deflate(EntryBuilder builder)
    {
        // this is a bit of a go-around, but helps deal with Builder misuse.
        return builder.create();
    }

    public static Entry deflate(String raw)
    {
        return deflate(new JsonParser().parse(raw).getAsJsonObject());
    }

    public static Entry deflate(JsonObject json)
    {
        int id;
        try
        {
            id = json.get("typeid").getAsInt();
        }
        catch (JsonParseException e) {
            Log.wtf("ENTRYF", "Malformed json entry. Has no typeid.");
            throw e;
        }
        switch (id)
        {
            case 0:
                return new Entry(json);
            case 1:
                return new ItemEntry(json);
            case 2:
                return new WeaponEntry(json);
            case 3:
                return new SkillEntry(json);

            default:
                Log.w("ENTRYF", "Unable to interpret typeid. Falling back to Entry");
                return new Entry(json);
        }
    }
}
