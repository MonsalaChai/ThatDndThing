package com.monsalachai.dndthing.entry.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.monsalachai.dndthing.R;

/**
 * Created by mesalu on 1/20/2018.
 */

public class WeaponCustomizerView extends LinearLayout {
    private Spinner mTypeSpinner;
    private Spinner mSubSpinner;

    public interface CallbackHandler {
        void onTypeChange(String newSelection);
        void onSubTypeChange(String subType);
    }

    public WeaponCustomizerView(Context context) {
        super(context);
        init();
    }

    public WeaponCustomizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_weapon_customizer, this);

        // find members by id.
        mTypeSpinner = findViewById(R.id.weapon_type_selector);
        mSubSpinner  = findViewById(R.id.weapon_subselectoin);

        // set adaptors!

        // set change listeners!
    }
}
