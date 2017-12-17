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

    private WeaponType pType;
    private DamageType pDamageType;
    private int pAmmoCount;
    private String pAmmoDesc;

    public WeaponEntry()
    {
        super();

        pType = WeaponType.Other;
        pDamageType = DamageType.Other;
        pAmmoCount = 0;
        pAmmoDesc = "None";
    }

    public WeaponEntry(JsonObject json)
    {
        super(json);
        try { json = json.getAsJsonObject("weapon"); }
        catch (JsonParseException e) { throw new MalformedEntryException("Malformed ID"); }

        pType = _convertStringToWType(safeGet(json, "type", "??"));
        pDamageType = _convertStringToDtype(safeGet(json, "damageType", "??"));
        pAmmoCount = safeGet(json, "ammoCount", 0);
        pAmmoDesc = safeGet(json, "ammoType", "None");
    }

    public WeaponEntry(String raw)
    {
        super(raw);
        JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
        try { json = json.getAsJsonObject("weapon"); }
        catch (JsonParseException e) { throw new MalformedEntryException("Malformed ID"); }


        pType = _convertStringToWType(safeGet(json, "type", "??"));
        pDamageType = _convertStringToDtype(safeGet(json, "damageType", "??"));
        pAmmoCount = safeGet(json, "ammoCount", 0);
        pAmmoDesc = safeGet(json, "ammoType", "None");
    }

    @Override
    public JsonObject serialize()
    {
        JsonObject master = super.serialize();
        JsonObject json = new JsonObject();

        master.addProperty("typeid", 2);
        master.add("weapon", json);
        json.addProperty("type", _invertType(pType));
        json.addProperty("damageType", _invertDamageType(pDamageType));
        json.addProperty("ammoCount", pAmmoCount);
        json.addProperty("ammoType", pAmmoDesc);

        return master;
    }

    @Override
    public int performRoll()
    {
        int roll = _roll();
        if (isConsumable() && canRoll())
            pAmmoCount -= 1;
        return roll;
    }

    public WeaponType getType() { return pType; }
    public WeaponType getWeaponType() { return pType; }
    public DamageType getDamageType() { return pDamageType; }
    public boolean isRanged() { return pType == WeaponType.Ranged; }
    public boolean isMelee() { return pType == WeaponType.Melee; }
    public boolean isMagical() { return pType == WeaponType.Magical; }

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
        return (pDamageType == getDamageType().Slash) ? "Slashing" : (pDamageType == DamageType.Pierce) ? "Piercing" : (pDamageType == DamageType.Blunt) ? "Bludgeoning" : "???";
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
