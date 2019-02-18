package com.louistrapani.wheretowatch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;


// This class creates the dialog when the
// "About" menu item is selected
public class AboutDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Where to Watch was created by Louis Trapani \u00A9 2018");
            builder.setTitle("About");
            builder.setCancelable(false);
            return builder.create();
        }
}
