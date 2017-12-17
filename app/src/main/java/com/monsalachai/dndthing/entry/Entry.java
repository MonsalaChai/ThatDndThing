package com.monsalachai.dndthing.entry;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.monsalachai.dndthing.App;
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
    private boolean pRollable;
    private boolean pCritable;
    private int pDie;
    private int pCoefficient;
    private int pConstant;
    private String pName;
    private String pDescription;

    Entry() {
        // Default constructor.
        pRollable    = false;
        pCritable    = false;
        pDie         = 0;
        pCoefficient = 1;
        pConstant    = 0;
        pName        = App.getGlobalContext().getResources().getString(R.string.unknown_entry);
        pDescription = App.getGlobalContext().getResources().getString(R.string.unused_longdesc);
    }

    Entry(JsonObject json) {
        pRollable    = safeGet(json, "rollable", false);
        pCritable    = safeGet(json, "critable", false);
        pDie         = safeGet(json, "die", 20);
        pConstant    = safeGet(json, "constant", 0);
        pCoefficient = safeGet(json, "coefficient", 1);
        pName        = safeGet(json, "label", App.getGlobalContext().getResources().getString(R.string.unknown_entry));
        pDescription = safeGet(json, "desc", App.getGlobalContext().getResources().getString(R.string.unused_longdesc));
    }

    Entry(String raw) {
        JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
        pRollable    = safeGet(json, "rollable", false);
        pCritable    = safeGet(json, "critable", false);
        pDie         = safeGet(json, "die", 20);
        pConstant    = safeGet(json, "constant", 0);
        pCoefficient = safeGet(json, "coefficient", 1);
        pName        = safeGet(json, "label", App.getGlobalContext().getResources().getString(R.string.unknown_entry));
        pDescription = safeGet(json, "desc", App.getGlobalContext().getResources().getString(R.string.unused_longdesc));
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();

        json.add("rollable", new JsonPrimitive(pRollable));
        json.add("die", new JsonPrimitive(pDie));
        json.add("coefficient", new JsonPrimitive(pCoefficient));
        json.add("constant", new JsonPrimitive(pConstant));
        json.add("label", new JsonPrimitive(pName));
        json.add("desc", new JsonPrimitive(pDescription));

        return json;
    }

    public boolean canRoll() { return pRollable; }
    public boolean canCrit() { return pCritable; }
    public String getRollDescriptor() { return  (pRollable) ? String.format(Locale.US, "%dd%d+%d", pCoefficient, pDie, pConstant) : "Not Rollable";}
    public String getLabel() { return pName;}
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
        setCustomOnClickListener(v, context);

        TextView tv = v.findViewById(R.id.entry_label_tv);
        tv.setText(getLabel(), TextView.BufferType.NORMAL);
        tv = v.findViewById(R.id.entry_longdesc);
        tv.setText(pDescription, TextView.BufferType.NORMAL);

        return v;
    }

    protected void setCustomOnClickListener(final View v, final Context context)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(v, getRollDescriptor(), Snackbar.LENGTH_INDEFINITE).setAction(getActionDescriptor(), (pRollable) ? new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("SnackRoll", "The user has issued a roll!");
                        int roll = performRoll();
                        Log.i("SnackRoll", "The user rolled: " + roll);

                        // create a dialog:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("You rolled: " + roll).setTitle("Roll Result").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.i("dlg+b", "They pressed the okay button.");
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                } : null).show();
            }
        });
    }

    protected int _roll()
    {
        if (!pRollable)
            return 0;

        Log.d("Roll", "Is rollable.");

        StringBuilder logmessage = new StringBuilder();
        logmessage.append("Rolled: ");
        logmessage.append(getRollDescriptor()).append(" -> ");

        Random rng = new Random();
        int roll = 0;
        for (int i = 0; i < pCoefficient; i++)
        {
            int thisdie = rng.nextInt(pDie) + 1; // 1 -> pDie

            // todo: check for critical success.

            roll += thisdie;
            logmessage.append(String.format(Locale.US, "%d, ", thisdie));
        }
        logmessage.append(String.format(Locale.US,"+ %d", pConstant));
        roll += pConstant;

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

