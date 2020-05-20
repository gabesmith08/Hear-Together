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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heartogether.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfilesFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "ProfilesFragment";
    public static final String USERNAME_KEY = "Username";
    private DocumentReference sDocRef = FirebaseFirestore.getInstance().document("profilesData/profiles");
    private Button saveProfileButton;
    private Button updateUsernameButton;
    private EditText usernameText;
    private TextView displayUsername;
    View v;

    public ProfilesFragment() {
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
        View v = inflater.inflate(R.layout.fragment_profiles, container, false);

        saveProfileButton = v.findViewById(R.id.usernameSaveButton);
        saveProfileButton.setOnClickListener(saveListener);
        updateUsernameButton = v.findViewById(R.id.fetchUsernameButton);
        updateUsernameButton.setOnClickListener(usernameListener);
        usernameText = v.findViewById(R.id.newUsernameText);
        displayUsername = v.findViewById(R.id.currentUsernameTextView);

        return v;
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        public void onClick(View v) {
            saveProfile(v);
        }
    };

    private View.OnClickListener usernameListener = new View.OnClickListener() {
        public void onClick(View v) {
            fetchUsername(v);
        }
    };


    public void saveProfile(View v) {

        String newUsername = usernameText.getText().toString();
        Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put(USERNAME_KEY, newUsername);


        sDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "username have been saved!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "username were not saved :(");
            }
        });

        Toast.makeText(getActivity(), "Username Updated!",
                Toast.LENGTH_LONG).show();
    }

    public void fetchUsername(View v) {
        sDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    String username = documentSnapshot.getString(USERNAME_KEY);
                    displayUsername.setText(username);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

    }
}