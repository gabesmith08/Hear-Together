package com.example.heartogether.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.heartogether.Audio;
import com.example.heartogether.R;
import com.example.heartogether.models.Session;

public class SessionFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "SessionFragment";
    public Audio audio;
    private Session session;
    private Button recordBtn;
    private Button stopBtn;
    private Button playBtn;
    private Button pauseBtn;

    private View v;

    public SessionFragment() {
        // Instantiate Audio (maybe move this into the session itself ?)

        //this.session = new Session(com.example.heartogether.logic);
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
        View v = inflater.inflate(R.layout.fragment_session, container, false);
        this.audio = new Audio(v.getContext(), this.getActivity());
        this.session = new Session(audio);

        /**
         * HEY DEVELOPER, ALL THESE BUTTONS CRASH, MAKE THEM WORK WHEN WE CAN START RECORDING STUFF
         */

        recordBtn = v.findViewById(R.id.startButton);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audio.run();
            }
        });

        stopBtn = v.findViewById(R.id.stopButton);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audio.pause();
            }
        });

        playBtn = v.findViewById(R.id.playButton);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // com.example.heartogether.logic.startPlaying();
            }
        });

        pauseBtn = v.findViewById(R.id.pauseButton);
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // com.example.heartogether.logic.stopPlaying();
            }
        });

        // Button buttonBluetooth = (Button) v.findViewById(R.id.buttonSayHi);
        // buttonBluetooth.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View view) {
        return;
    }
}
