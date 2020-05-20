package com.example.heartogether.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.heartogether.MainActivity;
import com.example.heartogether.PitchActivityTest;
import com.example.heartogether.R;

public class CreateSessionFragment extends Fragment {

    private final String TAG = "CreateSessionFragment";
    private MainActivity activity;
    private View v;
    private Button createBtn;
    private Button cancelBtn;

    //required empty constructor
    public CreateSessionFragment() {
    }

    @Override
    public void onCreate(Bundle savedFragmentState) {
        super.onCreate(savedFragmentState);
        Log.d(TAG, "onCreate called");
        activity = (MainActivity) this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View v = inflater.inflate(R.layout.fragment_createsession, container, false);
        createBtn = v.findViewById(R.id.create);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragmentLayout, new SessionFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        cancelBtn = v.findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragmentLayout, new HomeFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });


        return v;
    }


}
