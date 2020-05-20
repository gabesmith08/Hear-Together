package com.example.heartogether.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.heartogether.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "SettingsFragment";
    private DocumentReference sDocRef = FirebaseFirestore.getInstance().document("settingsData/settings");
    private Button saveSettingsButton;
    private SeekBar volumeBar;
    private SeekBar panBar;
    private CheckBox checkBox;
    View v;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedFragmentState) {
        super.onCreate(savedFragmentState);
        Log.d(TAG, "onCreate called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView called");
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        saveSettingsButton = v.findViewById(R.id.settingsSaveButton);
        saveSettingsButton.setOnClickListener(saveListener);
        volumeBar = v.findViewById(R.id.overallVolumeBar);
        panBar = v.findViewById(R.id.panBar);
        checkBox = v.findViewById(R.id.allowHighVolumeCheckbox);

        sDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {

                    int volume = documentSnapshot.getDouble("Volume").intValue();
                    int pan = documentSnapshot.getDouble("Pan").intValue();
                    boolean checkbox = documentSnapshot.getBoolean("Checkbox");

                    volumeBar.setProgress(volume);
                    panBar.setProgress(pan);
                    checkBox.setChecked(checkbox);
                }
            }
        });

        return v;
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        public void onClick(View v) {
            saveSettings(v);
        }
    };


    public void saveSettings(View v) {

                int userOverallVolume = volumeBar.getProgress();
                int userPan = panBar.getProgress();
                boolean userAllowHighVolumeCheckbox = checkBox.isChecked();

                Map<String, Object> dataToSave = new HashMap<String, Object>();
                dataToSave.put("Volume", userOverallVolume);
                dataToSave.put("Pan", userPan);
                dataToSave.put("Checkbox", userAllowHighVolumeCheckbox);

                sDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Settings have been saved!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Settings were not saved :(");
                    }
                });

                Toast.makeText(getActivity(), "Settings Saved!",
                        Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

    }
}