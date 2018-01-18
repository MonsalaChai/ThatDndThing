package com.monsalachai.dndthing.entry.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.monsalachai.dndthing.App;
import com.monsalachai.dndthing.R;

/**
 * Created by mesalu on 1/15/18.
 */

public class EntryCreatorDialogFragment extends DialogFragment {
    public static final String ARG_ORIGINNAME = "arg_origin";

    private String mOriginName;
    private View mView;

    /**
     * This class method is a convenience function for launching a creator dialog.
     * @param mgr Fragment manager from the calling context.
     */
    public static void LaunchDialog(FragmentManager mgr, String origin) {
        EntryCreatorDialogFragment ecdf = new EntryCreatorDialogFragment();
        Bundle args = new Bundle();
        args.putString(EntryCreatorDialogFragment.ARG_ORIGINNAME, origin);
        ecdf.setArguments(args);
        ecdf.show(mgr, "EntryCreateDialog");
    }

    public EntryCreatorDialogFragment() {}  // Required empty constructor.

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mOriginName = getArguments().getString(ARG_ORIGINNAME);
        else if (savedInstanceState != null)
            mOriginName = savedInstanceState.getString(ARG_ORIGINNAME);
        else mOriginName = "???";

        // inflate calls onCreateDialog (for some reason)... so call it here rather than there.
        mView = getLayoutInflater().inflate(R.layout.layout_create_dialog, null);
        ((BasicConfigView)mView.findViewById(R.id.ecdf_type_selector)).setFragment(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // load mView up with sub views.

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(mView).setTitle(String.format("%s: %s", mOriginName, App.getGlobalContext().getResources().getString(R.string.dialog_entry_create_title)))
                .setNegativeButton(R.string.dialog_entry_create_neg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("ECDlg", "User canceled operation");
                    }
                })
                .setPositiveButton(R.string.dialog_entry_create_pos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("ECDlg", "User submitted new entry");
                    }
                });

        return builder.create();
    }

    /**
     * This is a callback available to BasicConfigView that helps in dynamically adding and
     * removing sub views from this Fragment.
     * @param field The title of the slider that changed
     * @param state the state that the slider changed to.
     */
    public void stateChanged(String field, boolean state) {
        Log.i("ecdf", "State change in " + field + " to " + state);
        if (field.compareTo("Rollable") == 0) {
            if (state)
                mView.findViewById(R.id.weapon_customizer).setVisibility(View.VISIBLE);
            else
                mView.findViewById(R.id.weapon_customizer).setVisibility(View.GONE);
        }
    }

    /**
     * This is a callback available to BasicConfigView that is used to indicate when the spinner
     * selects a type.
     * @param field the freshly selected item in the spinner.
     */
    public void stateChanged(String field) {
        // hide currently shown view if necessary.
        switch (field) {
            case "Weapon":
            case "Item":
            case "Condition":
            case "Spell":
            case "Feat":
                break;
        }
    }

}
