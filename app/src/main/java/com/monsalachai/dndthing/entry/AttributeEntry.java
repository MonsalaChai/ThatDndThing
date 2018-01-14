package com.monsalachai.dndthing.entry;

import android.view.View;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.monsalachai.dndthing.App;
import com.monsalachai.dndthing.R;

import java.util.Locale;

/**
 * Created by mesalu on 12/17/17.
 */

public class AttributeEntry extends Entry {
    public class InvalidScoreRelationshipError extends MalformedEntryException {
        InvalidScoreRelationshipError(String message) {
            super(message);
        }
    }

    private int mScore;

    public AttributeEntry() {
        super();
        mScore = 10;

        validateValueAndScore();
    }

    public AttributeEntry(String raw) {
        super(raw);
        JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
        json = safeGet(json, "attribute");
        if (json == null) throw new MalformedEntryException("Malformed ID");

        mScore = safeGet(json, "score", 10);

        validateValueAndScore();
    }

    public AttributeEntry(JsonObject json) {
        super(json);
        json = safeGet(json, "attribute");
        if (json == null) throw new MalformedEntryException("Malformed ID");

        mScore = safeGet(json, "score", 10);

        validateValueAndScore();
    }

    @Override
    protected void setViewBackground(View v) {
        v.setBackground(App.getGlobalContext().getDrawable(R.drawable.background_attribute_entry));
    }

    private void validateValueAndScore()
    {
        float div = (mScore - 10) / 2;
        int expected = (int) Math.floor(div / 2.0);

        if (expected != getConstant()) throw new InvalidScoreRelationshipError(
                String.format(Locale.US, "Real Constant: %d, Expected Constant: %d",
                        getConstant(), expected));
    }
}
