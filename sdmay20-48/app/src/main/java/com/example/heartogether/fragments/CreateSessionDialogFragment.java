package com.example.heartogether.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.heartogether.R;

public class CreateSessionDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Create a new session?")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do things
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.beginTransaction()
                                .replace(R.id.fragmentLayout, new SessionFragment())
                                .addToBackStack(null)
                                .commit();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        getActivity().getFragmentManager().popBackStack();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
