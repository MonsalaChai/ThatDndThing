package com.monsalachai.dndthing.entry;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.monsalachai.dndthing.R;

import java.util.Locale;
import java.util.Random;

/**
 * Created by mesalu on 12/8/17.
 */

public class Entry {
    // go-to exception for data malformation errors.
    public class MalformedEntryException extends RuntimeException {
        MalformedEntryException(String message)
        {
            super(message);
        }
    }

    //  Note: <modifier>d<die> + <constant>
    private boolean _rollable;
    private boolean _critable;
    private int _die;
    private int _modifier;
    private int _constant;
    private String _label;

    Entry() {
        // Default constructor.
        _rollable = false;
        _critable = false;
        _die = 0;
        _modifier = 1;
        _constant = 0;
        _label = "Unknown"; // todo, figure out how to use R.string.unknown_entry instead
        //Resources.getSystem().getString(android.R.string.unknown_entry); //<-- cannot resolve symbol
    }

    Entry(JsonObject json) {
        _rollable = safeGet(json, "rollable", false);
        _critable = safeGet(json, "critable", false);
        _die      = safeGet(json, "die", 20);
        _constant = safeGet(json, "constant", 0);
        _modifier = safeGet(json, "modifier", 1);
        _label    = safeGet(json, "label", "Unknown");
    }

    Entry(String raw) {
        JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
        _rollable = safeGet(json, "rollable", false);
        _critable = safeGet(json, "critable", false);
        _die      = safeGet(json, "die", 20);
        _constant = safeGet(json, "constant", 0);
        _modifier = safeGet(json, "modifier", 1);
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
    public boolean canCrit() { return _critable; }
    public String getRollDescriptor() { return  (_rollable) ? String.format(Locale.US, "%dd%d+%d", _modifier, _die, _constant) : "Not Rollable";}
    public String getLabel() { return _label;}
    public String getActionDescriptor() { return "Roll!"; }
    public int performRoll() {
        return _roll();
    }

    public View generateView(Context context)
    {
        // get an inflater:
        LayoutInflater inflater = (LayoutInflater)context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.view_entry, null);

        // set some stuff up in v...
        setCustomOnClickListener(v);

        TextView tv = v.findViewById(R.id.entry_label_tv);
        tv.setText(getLabel(), TextView.BufferType.NORMAL);

        return v;
    }

    protected void setCustomOnClickListener(final View v)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(v, getRollDescriptor(), Snackbar.LENGTH_INDEFINITE).setAction(getActionDescriptor(), (_rollable) ? new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("Entry", "They clicked the rolly-button! D:");
                    }
                } : null).show();
            }
        });
    }

    protected int _roll()
    {
        if (!_rollable)
            return 0;

        Log.d("Roll", "Is rollable.");

        StringBuilder logmessage = new StringBuilder();
        logmessage.append("Rolled: ");
        logmessage.append(getRollDescriptor()).append(" -> ");

        Random rng = new Random();
        int roll = 0;
        for (int i = 0; i < _modifier; i++)
        {
            int thisdie = rng.nextInt(_die) + 1; // 1 -> _die

            // todo: check for critical success.

            roll += thisdie;
            logmessage.append(String.format(Locale.US, "%d, ", thisdie));
        }
        logmessage.append(String.format(Locale.US,"+ %d", _constant));
        roll += _constant;

        logmessage.append(" = ");
        logmessage.append(roll);
        Log.d("Roll", logmessage.toString());
        return roll;
    }

    protected JsonObject safeGet(JsonObject json, String tag)
    {
        // well, safe-ish. Can still be mis-used to create a NPE.
        try { return json.getAsJsonObject(tag); }
        catch (com.google.gson.JsonParseException e) { return null; }
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

