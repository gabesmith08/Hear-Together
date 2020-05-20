package com.example.heartogether.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.heartogether.R;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class JoinSessionFromListDialogFragment extends DialogFragment {

    private final String TAG = "JoinSessionDialog";
    String deviceAddress;

    public JoinSessionFromListDialogFragment(Context context, Activity activity) {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},
                    123);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Join a new session?")
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = fm.beginTransaction();
                            WifiP2pConfig config = new WifiP2pConfig();

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
