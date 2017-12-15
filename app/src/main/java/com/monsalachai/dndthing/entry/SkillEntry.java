package com.monsalachai.dndthing.entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.LinkedList;

/**
 * Created by mesalu on 12/9/17.
 */

/* Todo: discuss db 'user' entries, or a hard-set resource for each skill modification source
 * which would also help with things like determining _classmod. */

public class SkillEntry extends Entry {
    private class MiscSources
    {
        public int mod;
        public String text;
        MiscSources(int modifier, String helptext) { mod = modifier; text = helptext;}
    }

    private int _miscmod;
    private int _classmod;
    private int _ranks;
    private LinkedList<MiscSources> _sources;

    public SkillEntry()
    {
        super();
        _miscmod = 0;
        _classmod = 0;
        _ranks = 0;
        _sources = null;
    }

    public SkillEntry(JsonObject json)
    {
        super(json);
        json = safeGet(json, "skills");
        if (json == null) throw new MalformedEntryException("Malformed ID");

        _miscmod = 0;
        _sources = new LinkedList<>();

        _ranks    = safeGet(json,  "ranks", 0);
        _classmod = safeGet(json, "classSkill", 0);

        for (JsonElement js : json.getAsJsonArray("miscSources"))
        {
            JsonObject jso = js.getAsJsonObject();
            MiscSources ms = new MiscSources(jso.get("mod").getAsInt(), jso.get("text").getAsString());
            _miscmod += ms.mod;
            _sources.add(ms);
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

        _ranks = safeGet(json, "ranks", 0);
        _classmod = safeGet(json, "classSkill", 0);

        _miscmod = 0;
        _sources = new LinkedList<>();

        for (JsonElement js : json.getAsJsonArray("sources"))
        {
            JsonObject jso = js.getAsJsonObject();
            MiscSources ms = new MiscSources(jso.get("mod").getAsInt(), jso.get("text").getAsString());
            _miscmod += ms.mod;
            _sources.add(ms);
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

        skillsjson.addProperty("skillRanks", _ranks);
        skillsjson.addProperty("skillClassSkill", _classmod);


        // create a json array with _sources.
        JsonArray ja = new JsonArray();
        for (MiscSources ms : _sources)
        {
            JsonObject jo = new JsonObject();
            jo.addProperty("text", ms.text);
            jo.addProperty("mod", ms.mod);
            ja.add(jo);
        }

        skillsjson.add("skillMiscSources", ja);

        return json;
    }

    public int getTotalModifier() { return _miscmod + _classmod + _ranks; }

    // methods to get info on misc sources.

}
