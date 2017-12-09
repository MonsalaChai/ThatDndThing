package com.monsalachai.dndthing.entry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by mesalu on 12/9/17.
 */

public class ItemEntry extends BaseEntry {
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

        _count      = safeGet(json, "itemCount", 1);
        _weight     = safeGet(json, "itemWeight", 1);
        _durability = safeGet(json, "itemDurability", 1);
        _consumable = safeGet(json, "itemConsumable", false);
        _wondrous   = safeGet(json, "itemWondrous", false);
    }

    public ItemEntry(String raw)
    {
        super(raw);
        JsonObject json = new JsonParser().parse(raw).getAsJsonObject();

        _count      = safeGet(json, "itemCount", 1);
        _weight     = safeGet(json, "itemWeight", 1);
        _durability = safeGet(json, "itemDurability", 1);
        _consumable = safeGet(json, "itemConsumable", false);
        _wondrous   = safeGet(json, "itemWondrous", false);
    }

    @Override
    public JsonObject serialize()
    {
        JsonObject json = super.serialize();

        json.addProperty("typeid", 1);
        json.addProperty("itemCount", _count);
        json.addProperty("itemWeight", _weight);
        json.addProperty("itemDurability", _durability);
        json.addProperty("itemConsumable", _consumable);
        json.addProperty("itemWondrous", _wondrous);

        return json;
    }

    @Override
    public int performRoll()
    {
        int roll = super.performRoll();
        if (_consumable) _count -= 1;
        return roll;
    }

    public int getWeight() { return _weight; }
    public int getDurabililty() { return _durability; }
    public boolean isConsumable() { return _consumable; }
    public boolean isWondrous() { return _wondrous; }
}
