package com.monsalachai.dndthing.entry;

import android.util.Log;

import java.lang.Math;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.monsalachai.dndthing.App;
import com.monsalachai.dndthing.R;

/**
 * Created by mesalu on 12/9/17.
 */

public class EntryFactory {

    public static class EntryBuilder {
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
            Log.i("EBuilder", mBuildSite.toString());
            return EntryFactory.deflate(mBuildSite);
        }

        /**
         * Clears the production state of the builder
         * @return this
         */
        public EntryBuilder clear()
        {
            mBuildSite = new JsonObject();
            return this;
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
            mBuildSite.addProperty("constant", Math.abs(value));
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
            // (presently: weapon(2))
            if (mBuildSite.get("typeid").getAsInt() != 2)
                mBuildSite.addProperty("typeid", 1);


            noOverwrite("item");
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
            noOverwrite("weapon");

            // make sure that Item information is present too:
            setTypeItem();

            return this;
        }

        /**
         * Sets the Entry's type to reflect that it is a Skill
         * @return this
         */
        public EntryBuilder setTypeSkill() {
            mBuildSite.addProperty("typeid", 3);
            noOverwrite("skill");
            return this;
        }

        /**
         * Sets the Entry's type to reflect that it is a Character
         *
         * @return this
         */
        public EntryBuilder setTypeCharacter() {
            mBuildSite.addProperty("typeid", 4);
            noOverwrite("character");
            return this;
        }

        /**
         * Sets the Entry's type to reflect that it is representing an Attribute
         * @return this
         */
        public EntryBuilder setTypeAttribute()
        {
            mBuildSite.addProperty("typeid", 5);
            noOverwrite("attribute");
            return this;
        }

        /**
         * Sets the Entry's type to reflect that it is representing a Spell
         * @return this
         */
        public EntryBuilder setTypeSpell()
        {
            mBuildSite.addProperty("typeid", 6);
            noOverwrite("spell");
            return this;
        }

        /**
         * Sets the Entry's type to reflect that it is representing a Feat
         * @return this
         */
        public EntryBuilder setTypeFeat()
        {
            mBuildSite.addProperty("typeid", 7);
            noOverwrite("feat");
            return this;
        }

        /**
         * Sets the ItemEntry's durability to the given value.
         * @param value the value of the items durability
         * @return this
         */
        public EntryBuilder addItemDurability(int value)
        {
            setTypeItem();
            mBuildSite.getAsJsonObject("item").addProperty("durability", value);
            return this;
        }

        /**
         * Sets the ItemEntry's weight to the given value.
         * @param value The item's weight
         * @return this
         */
        public EntryBuilder addItemWeight(int value)
        {
            setTypeItem();
            mBuildSite.getAsJsonObject("item").addProperty("weight", value);
            return this;
        }

        /**
         * Sets the ItemEntry's count to the given value (IOW: "how many of these items are
         * in the inventory")
         * @param value the amount of these items.
         * @return this
         */
        public EntryBuilder addItemCount(int value)
        {
            setTypeItem();
            mBuildSite.getAsJsonObject("item").addProperty("count", value);
            return this;
        }

        /**
         * Sets if this ItemEntry represents a consumable item.
         * @return this
         */
        public EntryBuilder setItemConsumable()
        {
            setTypeItem();
            mBuildSite.getAsJsonObject("item").addProperty("consumable", true);
            return this;
        }

        /**
         * Sets if this ItemEntry represents a wondrous item.
         * @return this
         */
        public EntryBuilder setItemWondrous()
        {
            setTypeWeapon();
            mBuildSite.getAsJsonObject("item").addProperty("wondrous", true);
            return this;
        }


        /**
         * Sets the WeaponEntry's weapon type to Magical
         */
        public EntryBuilder setWeaponMagical()
        {
            setTypeWeapon();
            mBuildSite.getAsJsonObject("weapon").addProperty("type", "Magical");
            return this;
        }

        public EntryBuilder setWeaponMelee()
        {
            setTypeWeapon();
            mBuildSite.getAsJsonObject("weapon").addProperty("type", "Melee");
            return this;
        }

        public EntryBuilder setWeaponRanged()
        {
            setTypeWeapon();
            mBuildSite.getAsJsonObject("weapon").addProperty("type", "Ranged");
            return this;
        }

        /**
         * Sets the type The WeaponEntry's damage type to Slashing
         * @return this
         */
        public EntryBuilder setWeaponSlashing()
        {
            setTypeWeapon();
            mBuildSite.getAsJsonObject("weapon").addProperty("damageType", "Slashing");
            return this;
        }

        public EntryBuilder setWeaponPiercing()
        {
            setTypeWeapon();
            mBuildSite.getAsJsonObject("weapon").addProperty("damageType", "Piercing");
            return this;
        }

        public EntryBuilder setWeaponBludgeoning()
        {
            setTypeWeapon();
            mBuildSite.getAsJsonObject("weapon").addProperty("damageType", "Bludgeoning");
            return this;
        }

        public EntryBuilder addAmmoCount(int count)
        {
            setTypeWeapon();
            mBuildSite.getAsJsonObject("weapon").addProperty("ammo", count);
            return this;
        }

        public EntryBuilder addAmmoTypeDescription(String desc)
        {
            setTypeWeapon();
            mBuildSite.getAsJsonObject("weapon").addProperty("ammoType", desc);
            return this;
        }

        /**
         * Sets this SkillEntry to reflect a class skill
         * @return this
         */
        public EntryBuilder setSkillClassSkill()
        {
            setTypeSkill();
            mBuildSite.getAsJsonObject("skill").addProperty("classSkill", true);
            return this;
        }

        /**
         * Sets this Skill as a class Skill, meaning that if a single rank is put into the skill
         * the class supply a bonus 3 points.
         * @param flag the state to set classSkill to.
         * @return this
         */
        public EntryBuilder setSkillClassSkill(boolean flag)
        {
            setTypeSkill();
            mBuildSite.getAsJsonObject("skill").addProperty("classSkill", flag);
            return this;
        }

        /**
         * Assigns a total rank value to the skill.
         * This value can vary from the sum of source points.
         *
         * @param value the total number of ranks.
         * @return this
         */
        public EntryBuilder addRankValue(int value)
        {
            setTypeSkill();
            mBuildSite.getAsJsonObject("skill").addProperty("ranks", value);
            return this;
        }

        public EntryBuilder addSkillSource(int value)
        {
            return addSkillSource(value,
                    App.getGlobalContext().getResources().getString(R.string.unknown_entry),
                    App.getGlobalContext().getResources().getString(R.string.unused_skill_source_desc));
        }

        public EntryBuilder addSkillSource(int value, String label)
        {
            return addSkillSource(value, label,
                    App.getGlobalContext().getResources().getString(R.string.unused_skill_source_desc));
        }

        /**
         * Add a source to the skill entry.
         * The source details the point value (how much is being added/removed) from the skill result
         * what the source is called (ex, a feat name)
         * and a short description if available of what the source is.
         * @param value the value (can be negative) associated to the source.
         * @param label what the source's name is
         * @param description description of the skill source
         * @return this
         */
        public EntryBuilder addSkillSource(int value, String label, String description)
        {
            setTypeSkill();
            noOverwrite(mBuildSite.getAsJsonObject("skill"), "miscSources", new JsonArray());
            JsonArray array = mBuildSite.getAsJsonObject("skill").getAsJsonArray("miscSources");
            JsonObject object = new JsonObject();

            object.addProperty("name", label);
            object.addProperty("mod", value);
            object.addProperty("text", description);

            array.add(object);

            return this;
        }

        /**
         * Adds the given object to buildsite if the action would not otherwise overwrite data.
         * @param label the label in mBuildSite to add to.
         * @param object the JsonElement to add.
         */
        private void noOverwrite(String label, JsonElement object)
        {
            noOverwrite(mBuildSite, label, object);
        }

        /**
         * Functions as above, but on a specified json object.
         * @param label the label in json to add in as.
         * @param object the object to add to json.
         */
        private void noOverwrite(JsonObject json, String label, JsonElement object)
        {
            if (json.get(label) == null)
                json.add(label, object);
        }

        /**
         * As the other two noOverwrite methods, but will assume
         * that object will be a brand new JSON object, and will not
         * allocate it before determining that it will be used.
         * @param label the label to add the new json object to.
         */
        private void noOverwrite(JsonObject json, String label)
        {
            if (json.get(label) == null)
                json.add(label, new JsonObject());
        }

        /**
         * As above, but assumes target is mBuildSite
         * @param label the label to add the new json object to.
         */
        private void noOverwrite(String label)
        {
            noOverwrite(mBuildSite, label);
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
