package com.example.heartogether.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import android.widget.Spinner;

import com.example.heartogether.R;

public class JoinSessionDialogFragment extends DialogFragment {

    String[] mockPhones = {"Pixel", "Overpriced Phone", "2007 Hunk of Junk", "Chinese Phones"};
    private final String TAG = "JoinSessionDialog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, mockPhones);

        final Spinner spinner = new Spinner(this.getActivity());
        spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        spinner.setAdapter(adapter);

        builder.setMessage("Join a new session?")
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.replace(R.id.fragmentLayout, new SessionListFragment());
                            // transaction.addToBackStack(null);
                            transaction.commit();
                        } catch (Exception e) {
                            e.fillInStackTrace();
                            Log.d(TAG, "error");
                        }
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
