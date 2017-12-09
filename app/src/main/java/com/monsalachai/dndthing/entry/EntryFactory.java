package com.monsalachai.dndthing.entry;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Created by mesalu on 12/9/17.
 */

public class EntryFactory {
    public static BaseEntry deflate(String raw)
    {
        return deflate(new JsonParser().parse(raw).getAsJsonObject());
    }

    public static BaseEntry deflate(JsonObject json)
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
            case 1:
                return new ItemEntry(json);
            case 2:
                return new WeaponEntry(json);
            case 3:
                return new SkillEntry(json);

            default:
                Log.w("ENTRYF", "Unable to interpret typeid. Falling back to BaseEntry");
                return new BaseEntry(json);
        }
    }
}
