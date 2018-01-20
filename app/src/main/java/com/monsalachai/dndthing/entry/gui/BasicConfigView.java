package com.monsalachai.dndthing.entry.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.monsalachai.dndthing.R;

/**
 * Created by mesalu on 1/17/18.
 */

public class BasicConfigView extends LinearLayout {
    private ConfigCallbackHandler mCallBack;
    private Spinner mSpinner;
    private Switch mSwitch;

    public BasicConfigView(Context context) {
        super(context);
        init();
    }

    public BasicConfigView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BasicConfigView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setmCallBackHandler(ConfigCallbackHandler handler) { mCallBack = handler; }

    private void init() {
        inflate(getContext(), R.layout.basicconfigview, this);
        mSpinner = findViewById(R.id.type_spinner);
        mSwitch = findViewById(R.id.rollable_switch);

        // Set mSpinner's adapter.
        ArrayAdapter<CharSequence> array = ArrayAdapter.createFromResource(getContext(), R.array.ecdf_enterable_types, android.R.layout.simple_spinner_item);
        array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(array);

        // Set listeners.
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mCallBack != null) mCallBack.onRollableChange(isChecked);
                else Log.i("ConfigView", "Unable to notify of Rollable state change.");
            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mCallBack != null) mCallBack.onSelectionChange(parent.getItemAtPosition(position).toString());
                else Log.i("ConfigView", "Unable to notify of selection change");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("ConfigView", "Nothing selected by user.");
            }
        });
    }
}
