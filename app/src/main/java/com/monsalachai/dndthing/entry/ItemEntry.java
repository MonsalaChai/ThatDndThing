package com.monsalachai.dndthing.entry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by mesalu on 12/9/17.
 */

public class ItemEntry extends Entry {
    private int pCount;
    private int pWeight;
    private int pDurability;
    private boolean pConsumable;
    private boolean pWondrous;

    public ItemEntry()
    {
        super();
        pCount = 1;
        pWeight = 1;
        pDurability = 1;
        pConsumable = false;
        pWondrous = false;
    }

    public ItemEntry(JsonObject json)
    {
        super(json);
        json = safeGet(json, "item");
        if (json == null) throw new MalformedEntryException("Malformed ID");

        pCount = safeGet(json, "count", 1);
        pWeight = safeGet(json, "weight", 1);
        pDurability = safeGet(json, "durability", 1);
        pConsumable = safeGet(json, "consumable", false);
        pWondrous = safeGet(json, "wondrous", false);
    }

    public ItemEntry(String raw)
    {
        super(raw);
        JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
        json = safeGet(json, "item");
        if (json == null) throw new MalformedEntryException("Malformed ID");

        pCount = safeGet(json, "count", 1);
        pWeight = safeGet(json, "weight", 1);
        pDurability = safeGet(json, "durability", 1);
        pConsumable = safeGet(json, "consumable", false);
        pWondrous = safeGet(json, "wondrous", false);
    }

    @Override
    public JsonObject serialize()
    {
        JsonObject master = super.serialize();
        master.addProperty("typeid", 1);

        JsonObject json = new JsonObject();
        master.add("item", json);

        json.addProperty("count", pCount);
        json.addProperty("weight", pWeight);
        json.addProperty("durability", pDurability);
        json.addProperty("consumable", pConsumable);
        json.addProperty("wondrous", pWondrous);

        return master;
    }

    @Override
    public int performRoll()
    {
        int roll = _roll();
        if (pConsumable && canRoll()) pCount -= 1;
        return roll;
    }

    public int getWeight() { return pWeight; }
    public int getDurabililty() { return pDurability; }
    public boolean isConsumable() { return pConsumable; }
    public boolean isWondrous() { return pWondrous; }
}
