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
import com.monsalachai.dndthing.roll.Roll;
import com.monsalachai.dndthing.roll.RollResult;

import java.util.Locale;

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
    private boolean mRollable;
    private boolean mCritable;
    private int mDie;
    private int mCoefficient;
    private int mConstant;
    private String mName;
    private String mDescription;

    Entry() {
        // Default constructor.
        mRollable = false;
        mCritable = false;
        mDie = 0;
        mCoefficient = 1;
        mConstant = 0;
        mName = App.getGlobalContext().getResources().getString(R.string.unknown_entry);
        mDescription = App.getGlobalContext().getResources().getString(R.string.unused_longdesc);
    }

    Entry(JsonObject json) {
        mRollable = safeGet(json, "rollable", false);
        mCritable = safeGet(json, "critable", false);
        mDie = safeGet(json, "die", 20);
        mConstant = safeGet(json, "constant", 0);
        mCoefficient = safeGet(json, "coefficient", 1);
        mName = safeGet(json, "label", App.getGlobalContext().getResources().getString(R.string.unknown_entry));
        mDescription = safeGet(json, "desc", App.getGlobalContext().getResources().getString(R.string.unused_longdesc));
    }

    Entry(String raw) {
        JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
        mRollable = safeGet(json, "rollable", false);
        mCritable = safeGet(json, "critable", false);
        mDie = safeGet(json, "die", 20);
        mConstant = safeGet(json, "constant", 0);
        mCoefficient = safeGet(json, "coefficient", 1);
        mName = safeGet(json, "label", App.getGlobalContext().getResources().getString(R.string.unknown_entry));
        mDescription = safeGet(json, "desc", App.getGlobalContext().getResources().getString(R.string.unused_longdesc));
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();

        json.add("rollable", new JsonPrimitive(mRollable));
        json.add("die", new JsonPrimitive(mDie));
        json.add("coefficient", new JsonPrimitive(mCoefficient));
        json.add("constant", new JsonPrimitive(mConstant));
        json.add("label", new JsonPrimitive(mName));
        json.add("desc", new JsonPrimitive(mDescription));

        return json;
    }

    public boolean canRoll() { return mRollable; }
    public boolean canCrit() { return mCritable; }
    public String getRollDescriptor() { return  (mRollable) ? String.format(Locale.US, "%dd%d+%d", mCoefficient, mDie, mConstant) : "Not Rollable";}
    public String getLabel() { return mName;}
    public String getActionDescriptor() { return "Roll!"; }


    public RollResult roll()
    {
        RollResult result = Roll.makeRoll(mCoefficient, mDie, mConstant);
        result = onRoll(result);
        return result;
    }

    // override this to hook into a roll event rather than overriding roll().
    protected RollResult onRoll(RollResult carryover)
    {
        return carryover;
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
        tv.setText(mDescription, TextView.BufferType.NORMAL);

        return v;
    }

    protected void setCustomOnClickListener(final View v, final Context context)
    {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(v, getRollDescriptor(), Snackbar.LENGTH_INDEFINITE).setAction(getActionDescriptor(), (mRollable) ? new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("SnackRoll", "The user has issued a roll!");
                        RollResult rr = roll();
                        Log.i("SnackRoll", "The user rolled: " + rr.getResult());



                        // create a dialog:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(rr.toString()).setTitle("Roll Result").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
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

