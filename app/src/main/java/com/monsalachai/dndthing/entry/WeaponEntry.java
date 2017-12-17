package com.monsalachai.dndthing.entry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Created by mesalu on 12/9/17.
 */

public class WeaponEntry extends ItemEntry {
    public enum DamageType{
        Slash, Pierce, Blunt, Other
    }
    public enum WeaponType{
        Melee, Ranged, Magical, Other
    }

    private WeaponType mType;
    private DamageType mDamageType;
    private int mAmmoCount;
    private String mAmmoDesc;

    public WeaponEntry()
    {
        super();

        mType = WeaponType.Other;
        mDamageType = DamageType.Other;
        mAmmoCount = 0;
        mAmmoDesc = "None";
    }

    public WeaponEntry(JsonObject json)
    {
        super(json);
        try { json = json.getAsJsonObject("weapon"); }
        catch (JsonParseException e) { throw new MalformedEntryException("Malformed ID"); }

        mType = _convertStringToWType(safeGet(json, "type", "??"));
        mDamageType = _convertStringToDtype(safeGet(json, "damageType", "??"));
        mAmmoCount = safeGet(json, "ammoCount", 0);
        mAmmoDesc = safeGet(json, "ammoType", "None");
    }

    public WeaponEntry(String raw)
    {
        super(raw);
        JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
        try { json = json.getAsJsonObject("weapon"); }
        catch (JsonParseException e) { throw new MalformedEntryException("Malformed ID"); }


        mType = _convertStringToWType(safeGet(json, "type", "??"));
        mDamageType = _convertStringToDtype(safeGet(json, "damageType", "??"));
        mAmmoCount = safeGet(json, "ammoCount", 0);
        mAmmoDesc = safeGet(json, "ammoType", "None");
    }

    @Override
    public JsonObject serialize()
    {
        JsonObject master = super.serialize();
        JsonObject json = new JsonObject();

        master.addProperty("typeid", 2);
        master.add("weapon", json);
        json.addProperty("type", _invertType(mType));
        json.addProperty("damageType", _invertDamageType(mDamageType));
        json.addProperty("ammoCount", mAmmoCount);
        json.addProperty("ammoType", mAmmoDesc);

        return master;
    }

    @Override
    public int performRoll()
    {
        int roll = _roll();
        if (isConsumable() && canRoll())
            mAmmoCount -= 1;
        return roll;
    }

    public WeaponType getType() { return mType; }
    public WeaponType getWeaponType() { return mType; }
    public DamageType getDamageType() { return mDamageType; }
    public boolean isRanged() { return mType == WeaponType.Ranged; }
    public boolean isMelee() { return mType == WeaponType.Melee; }
    public boolean isMagical() { return mType == WeaponType.Magical; }

    public String getWeaponDescriptor()
    {
       return String.format("%s%s %s",
                (isWondrous()) ? "Wondrous " : "",
                getTypeDescriptor(),
                getLabel());

    }


    protected String getTypeDescriptor()
    {
        // ever feel like doing things the hard way because you can?
        return (mDamageType == getDamageType().Slash) ? "Slashing" : (mDamageType == DamageType.Pierce) ? "Piercing" : (mDamageType == DamageType.Blunt) ? "Bludgeoning" : "???";
    }

    protected WeaponType _convertStringToWType(String param)
    {
        switch (param)
        {
            case "m":
            case "melee":
            case "M":
                return WeaponType.Melee;
            case "r":
            case "R":
            case "ranged":
                return WeaponType.Ranged;
            case "magical":
            case "Magical":
            case "g":
            case "G":
                return WeaponType.Magical;
            default:
                return WeaponType.Other;
        }
    }

    protected DamageType _convertStringToDtype(String param)
    {
        switch (param)
        {
            case "p":
            case "pierce":
            case "Pierce":
                return DamageType.Pierce;
            case "s":
            case "slash":
            case "Slash":
                return DamageType.Slash;
            case "b":
            case "blunt":
            case "Blunt":
            case "bludgeon":
            case "Bludgeon":
                return DamageType.Blunt;
            default:
                return DamageType.Other;
        }
    }

    protected String _invertDamageType(DamageType dtype)
    {
        if (dtype == DamageType.Blunt) return "b";
        if (dtype == DamageType.Other) return "?";
        if (dtype == DamageType.Slash) return "s";
        if (dtype == DamageType.Pierce) return "p";
        return null;
    }

    protected String _invertType(WeaponType  type)
    {
        if (type == WeaponType.Melee)   return "melee";
        if (type == WeaponType.Magical) return "magical";
        if (type == WeaponType.Ranged)  return "ranged";
        if (type == WeaponType.Other)   return "?";
        return null;
    }
}
