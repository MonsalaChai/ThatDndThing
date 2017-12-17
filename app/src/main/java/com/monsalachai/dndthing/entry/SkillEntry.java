package com.monsalachai.dndthing.entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.LinkedList;

/**
 * Created by mesalu on 12/9/17.
 */

public class SkillEntry extends Entry {
    private class MiscSources
    {
        public int mod;
        public String text;
        MiscSources(int modifier, String helptext) { mod = modifier; text = helptext;}
    }

    private int mMiscMod;
    private boolean mClassSkill;
    private int mRanks;
    private LinkedList<MiscSources> mSources;

    public SkillEntry()
    {
        super();
        mMiscMod = 0;
        mClassSkill = false;
        mRanks = 0;
        mSources = null;
    }

    public SkillEntry(JsonObject json)
    {
        super(json);
        json = safeGet(json, "skills");
        if (json == null) throw new MalformedEntryException("Malformed ID");

        mMiscMod = 0;
        mSources = new LinkedList<>();

        mRanks = safeGet(json,  "ranks", 0);
        mClassSkill = safeGet(json, "classSkill", false);

        for (JsonElement js : json.getAsJsonArray("miscSources"))
        {
            JsonObject jso = js.getAsJsonObject();
            MiscSources ms = new MiscSources(jso.get("mod").getAsInt(), jso.get("text").getAsString());
            mMiscMod += ms.mod;
            mSources.add(ms);
            // todo, look into using Gson object here instead.
        }
    }

    public SkillEntry(String raw)
    {
        super(raw);
        JsonObject json = null;
        try {
            json = new JsonParser().parse(raw).getAsJsonObject().getAsJsonObject("skills");
        }
        catch (com.google.gson.JsonParseException e) { throw new MalformedEntryException("Malformed ID"); }

        mRanks = safeGet(json, "ranks", 0);
        mClassSkill = safeGet(json, "classSkill", false);

        mMiscMod = 0;
        mSources = new LinkedList<>();

        for (JsonElement js : json.getAsJsonArray("sources"))
        {
            JsonObject jso = js.getAsJsonObject();
            MiscSources ms = new MiscSources(jso.get("mod").getAsInt(), jso.get("text").getAsString());
            mMiscMod += ms.mod;
            mSources.add(ms);
            // todo, look into using Gson object here instead.
        }
    }

    @Override
    public JsonObject serialize()
    {
        JsonObject json = super.serialize();
        json.addProperty("typeid", 3);

        JsonObject skillsjson = new JsonObject();
        json.add("skills", skillsjson);

        skillsjson.addProperty("skillRanks", mRanks);
        skillsjson.addProperty("skillClassSkill", mClassSkill);


        // create a json array with mSources.
        JsonArray ja = new JsonArray();
        for (MiscSources ms : mSources)
        {
            JsonObject jo = new JsonObject();
            jo.addProperty("text", ms.text);
            jo.addProperty("mod", ms.mod);
            ja.add(jo);
        }

        skillsjson.add("skillMiscSources", ja);

        return json;
    }

    public int getTotalModifier() { return mMiscMod + ((mClassSkill) ? 3 : 0) + mRanks; }

    // methods to get info on misc sources.

}
