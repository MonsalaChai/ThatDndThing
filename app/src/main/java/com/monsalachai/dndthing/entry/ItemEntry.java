package com.monsalachai.dndthing.entry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by mesalu on 12/9/17.
 */

public class ItemEntry extends Entry {
    private int _count;
    private int _weight;
    private int _durability;
    private boolean _consumable;
    private boolean _wondrous;

    public ItemEntry()
    {
        super();
        _count      = 1;
        _weight     = 1;
        _durability = 1;
        _consumable = false;
        _wondrous   = false;
    }

    public ItemEntry(JsonObject json)
    {
        super(json);
        json = safeGet(json, "item");
        if (json == null) throw new MalformedEntryException("Malformed ID");

        _count      = safeGet(json, "count", 1);
        _weight     = safeGet(json, "weight", 1);
        _durability = safeGet(json, "durability", 1);
        _consumable = safeGet(json, "consumable", false);
        _wondrous   = safeGet(json, "wondrous", false);
    }

    public ItemEntry(String raw)
    {
        super(raw);
        JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
        json = safeGet(json, "item");
        if (json == null) throw new MalformedEntryException("Malformed ID");

        _count      = safeGet(json, "count", 1);
        _weight     = safeGet(json, "weight", 1);
        _durability = safeGet(json, "durability", 1);
        _consumable = safeGet(json, "consumable", false);
        _wondrous   = safeGet(json, "wondrous", false);
    }

    @Override
    public JsonObject serialize()
    {
        JsonObject master = super.serialize();
        master.addProperty("typeid", 1);

        JsonObject json = new JsonObject();
        master.add("item", json);

        json.addProperty("count", _count);
        json.addProperty("weight", _weight);
        json.addProperty("durability", _durability);
        json.addProperty("consumable", _consumable);
        json.addProperty("wondrous", _wondrous);

        return master;
    }

    @Override
    public int performRoll()
    {
        int roll = _roll();
        if (_consumable && canRoll()) _count -= 1;
        return roll;
    }

    public int getWeight() { return _weight; }
    public int getDurabililty() { return _durability; }
    public boolean isConsumable() { return _consumable; }
    public boolean isWondrous() { return _wondrous; }
}
