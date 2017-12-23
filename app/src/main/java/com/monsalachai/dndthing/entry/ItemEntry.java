package com.monsalachai.dndthing.entry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.monsalachai.dndthing.roll.RollResult;

/**
 * Created by mesalu on 12/9/17.
 */

public class ItemEntry extends Entry {
    private int mCount;
    private int mWeight;
    private int mDurability;
    private boolean mConsumable;
    private boolean mWondrous;

    public ItemEntry()
    {
        super();
        mCount = 1;
        mWeight = 1;
        mDurability = 1;
        mConsumable = false;
        mWondrous = false;
    }

    public ItemEntry(JsonObject json)
    {
        super(json);
        json = safeGet(json, "item");
        if (json == null) throw new MalformedEntryException("Malformed ID");

        mCount = safeGet(json, "count", 1);
        mWeight = safeGet(json, "weight", 1);
        mDurability = safeGet(json, "durability", 1);
        mConsumable = safeGet(json, "consumable", false);
        mWondrous = safeGet(json, "wondrous", false);
    }

    public ItemEntry(String raw)
    {
        super(raw);
        JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
        json = safeGet(json, "item");
        if (json == null) throw new MalformedEntryException("Malformed ID");

        mCount = safeGet(json, "count", 1);
        mWeight = safeGet(json, "weight", 1);
        mDurability = safeGet(json, "durability", 1);
        mConsumable = safeGet(json, "consumable", false);
        mWondrous = safeGet(json, "wondrous", false);
    }

    @Override
    public JsonObject serialize()
    {
        JsonObject master = super.serialize();
        master.addProperty("typeid", 1);

        JsonObject json = new JsonObject();
        master.add("item", json);

        json.addProperty("count", mCount);
        json.addProperty("weight", mWeight);
        json.addProperty("durability", mDurability);
        json.addProperty("consumable", mConsumable);
        json.addProperty("wondrous", mWondrous);

        return master;
    }

    @Override
    public RollResult onRoll(RollResult res)
    {
        if (mConsumable && canRoll()) mCount -= 1;
        return res;
    }

    public int getWeight() { return mWeight; }
    public int getDurabililty() { return mDurability; }
    public boolean isConsumable() { return mConsumable; }
    public boolean isWondrous() { return mWondrous; }
}
