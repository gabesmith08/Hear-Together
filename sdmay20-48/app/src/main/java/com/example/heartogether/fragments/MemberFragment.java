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

public class MemberFragment extends Fragment {

    private final String TAG = "MemberFragment";
    private MainActivity activity;
    private View v;
    private Button disBtn;

    public MemberFragment() {
        // Required empty public constructor
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
        activity.disconnect();
        View v = inflater.inflate(R.layout.fragment_member, container, false);
        disBtn = v.findViewById(R.id.Disconnect);
        disBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragmentLayout, new SessionListFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }

}
