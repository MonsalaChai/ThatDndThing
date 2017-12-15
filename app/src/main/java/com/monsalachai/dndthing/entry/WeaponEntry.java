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

    private WeaponType _type;
    private DamageType _damagetype;
    private int    _ammo;
    private String _ammotype;

    public WeaponEntry()
    {
        super();

        _type = WeaponType.Other;
        _damagetype = DamageType.Other;
        _ammo = 0;
        _ammotype = "None";
    }

    public WeaponEntry(JsonObject json) throws  MalformedEntryException
    {
        super(json);
        try { json = json.getAsJsonObject("weapon"); }
        catch (JsonParseException e) { throw new MalformedEntryException("Malformed ID"); }

        _type       = _convertStringToWType(safeGet(json, "weaponType", "??"));
        _damagetype = _convertStringToDtype(safeGet(json, "weaponDamageType", "??"));
        _ammo       = safeGet(json, "weaponAmmoCount", 0);
        _ammotype   = safeGet(json, "weaponAmmoType", "None");
    }

    public WeaponEntry(String raw) throws MalformedEntryException
    {
        super(raw);
        JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
        try { json = json.getAsJsonObject("weapon"); }
        catch (JsonParseException e) { throw new MalformedEntryException("Malformed ID"); }


        _type       = _convertStringToWType(safeGet(json, "weaponType", "??"));
        _damagetype = _convertStringToDtype(safeGet(json, "weaponDamageType", "??"));
        _ammo       = safeGet(json, "weaponAmmoCount", 0);
        _ammotype   = safeGet(json, "weaponAmmoType", "None");
    }

    @Override
    public JsonObject serialize()
    {
        JsonObject master = super.serialize();
        JsonObject json = new JsonObject();

        master.addProperty("typeid", 2);
        master.add("weapon", json);
        json.addProperty("type", _invertType(_type));
        json.addProperty("damageType", _invertDamageType(_damagetype));
        json.addProperty("ammoCount", _ammo);
        json.addProperty("ammoType", _ammotype);

        return master;
    }

    @Override
    public int performRoll()
    {
        int roll = _roll();
        if (isConsumable() && canRoll())
            _ammo -= 1;
        return roll;
    }

    public WeaponType getType() { return _type; }
    public WeaponType getWeaponType() { return _type; }
    public DamageType getDamageType() { return _damagetype; }
    public boolean isRanged() { return _type == WeaponType.Ranged; }
    public boolean isMelee() { return _type == WeaponType.Melee; }
    public boolean isMagical() { return _type == WeaponType.Magical; }

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
        return (_damagetype == getDamageType().Slash) ? "Slashing" : (_damagetype == DamageType.Pierce) ? "Piercing" : (_damagetype == DamageType.Blunt) ? "Bludgeoning" : "???";
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