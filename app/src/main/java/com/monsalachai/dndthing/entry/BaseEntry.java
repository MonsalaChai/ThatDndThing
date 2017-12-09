package com.monsalachai.dndthing.entry;

import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.Locale;
import java.util.Random;

/**
 * Created by mesalu on 12/8/17.
 */

public class BaseEntry {
    //  Note: <modifier>d<die> + <constant>
    protected boolean _rollable;
    protected int _die;
    protected int _modifier;
    protected int _constant;
    protected String _label;

    public BaseEntry() {
        // Default constructor.
        _rollable = false;
        _die = 0;
        _modifier = 0;
        _constant = 0;
        _label = "Unknown"; // todo, figure out how to use R.string.unknown_entry instead
        //Resources.getSystem().getString(android.R.string.unknown_entry); //<-- cannot resolve symbol
    }

    public BaseEntry(JsonObject json) {
        _rollable = safeGet(json, "rollable", false);
        _die      = safeGet(json, "die", 20);
        _constant = safeGet(json, "constant", 0);
        _modifier = safeGet(json, "modifier", 0);
        _label    = safeGet(json, "label", "Unknown");
    }

    public BaseEntry(String raw) {
        JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
        _rollable = safeGet(json, "rollable", false);
        _die      = safeGet(json, "die", 20);
        _constant = safeGet(json, "constant", 0);
        _modifier = safeGet(json, "modifier", 0);
        _label    = safeGet(json, "label", "Unknown");
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();

        json.add("rollable", new JsonPrimitive(_rollable));
        json.add("die", new JsonPrimitive(_die));
        json.add("constant", new JsonPrimitive(_constant));
        json.add("label", new JsonPrimitive(_label));

        return json;
    }

    public boolean canRoll() { return _rollable; }
    public String  getRoll() { return  (_rollable) ? String.format(Locale.US, "%dd%d+%d", _modifier, _die, _constant) : "Not Rollable";}
    public String getLabel() { return _label;}
    public int performRoll() {
        if (!_rollable)
            return 0;

        Random rng = new Random();
        int roll = 0;
        for (int i = 0; i < _modifier; i++)
            roll += rng.nextInt(_die) + 1;  // 1 -> _die

        roll += _constant;
        return roll;
    }

    protected String safeGet(JsonObject json, String tag,  String defval)
    {
        String ret = defval;
        try
        {
            ret = json.get(tag).getAsString();
        }
        catch (com.google.gson.JsonParseException e)
        {
            Log.d("ENTRY", String.format("Failed to parse desired field. Returning default value. %s", defval));
        }
        finally {
            return ret;
        }
    }

    protected int safeGet(JsonObject json, String tag, int defval)
    {
        int ret = defval;
        try
        {
            ret = json.get(tag).getAsInt();
        }
        catch (com.google.gson.JsonParseException e)
        {
            Log.d("ENTRY", String.format("Failed to parse desired field. Returning default value. %d", defval));
        }
        finally {
            return ret;
        }
    }

    protected float safeGet(JsonObject json, String tag, float defval)
    {
        float ret = defval;
        try
        {
            ret = json.get(tag).getAsFloat();
        }
        catch (com.google.gson.JsonParseException e)
        {
            Log.d("ENTRY", String.format("Failed to parse desired field. Returning default value. %.3f", defval));
        }
        finally
        {
            return ret;
        }
    }

    protected boolean safeGet(JsonObject json, String tag, boolean defval)
    {
        boolean ret = defval;
        try {
            ret = json.get(tag).getAsBoolean();
        }
        catch (com.google.gson.JsonParseException e)
        {
            Log.d("ENTRY", String.format("Failed to parse desired field. Returning default value. %s", defval));
        }
        finally
        {
            return ret;
        }
    }
}

