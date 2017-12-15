package com.monsalachai.dndthing.entry;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

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

    // custom view class
    public class EntryView extends View {
        protected boolean _showRoll;
        protected Paint _paint;
        public EntryView(Context context) {
            super(context);
            initDrawingResources();
            initOther();
        }
        public EntryView(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                                                                     R.styleable.EntryView,
                                                                     0, 0);
            _showRoll = a.getBoolean(R.styleable.EntryView_showRoll, false);

            a.recycle();

            initDrawingResources();
            initOther();


        }

        public boolean rollIsEnabled() { return _showRoll; }
        public void setRollEnabled(boolean state)
        {
            _showRoll = state;
            invalidate();
            requestLayout();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // freaking java man. No implicit conversion between double and float? really?
            setTextSizeForWidth(_paint, 400, "Hello World");
            canvas.drawText("Hello World", (float)(getWidth()/ 2.0), (float)(getHeight() / 2.0), _paint);

        }


        protected void setTextSizeForWidth(Paint paint, float desiredWidth, String sampletext)
        {
            // again, WTF java. casting "double" to float...
            final float initialSize = (float)48.0;

            paint.setTextSize(initialSize);
            Rect rect = new Rect();

            paint.getTextBounds(sampletext, 0, sampletext.length(), rect);

            // finalize size.
            float finalSize = initialSize * desiredWidth / rect.width();
            paint.setTextSize(finalSize);
        }

        protected void initDrawingResources()
        {
            // do any init for required drawing resources to save time during draw events.
            _paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            _paint.setARGB(255, 192, 192, 192);
        }

        protected void initOther()
        {
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // create a snackbar message with the optional action to roll
                    OnClickListener ocl = new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.i("OCL", "Do the thing with the rolly things.");
                        }
                    };
                    Snackbar.make(view, getRoll(), Snackbar.LENGTH_LONG)
                            .setAction("Roll!", (_showRoll) ? ocl : null).show();
                }
            });
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
    public String  getRoll() { return  (_rollable) ? String.format(Locale.US, "%dd%d+%d", _modifier, _die, _constant) : "Not Rollable";}
    public String getLabel() { return _label;}
    public int performRoll() {
        return _roll();
    }

    public View generateView(Context context)
    {
        EntryView ev = new EntryView(context);
        ev.setRollEnabled(_rollable);
        return ev;
    }

    protected int _roll()
    {
        if (!_rollable)
            return 0;

        Log.d("Roll", "Is rollable.");

        StringBuilder logmessage = new StringBuilder();
        logmessage.append("Rolled: ");
        logmessage.append(getRoll()).append(" -> ");

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

