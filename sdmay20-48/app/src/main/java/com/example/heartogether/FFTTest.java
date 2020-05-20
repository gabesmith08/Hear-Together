package com.example.heartogether;

import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FFTTest extends AppCompatActivity {

    private static final String TAG = "FFTTest";
    private Audio audio = null;

    private Button startBtn = null;
    private Button stopBtn = null;

    public FFTTest() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "FFTTest Activity Created");
        setContentView(R.layout.activity_ffttest);
        audio = new Audio(this.getBaseContext(), this);
        startBtn = findViewById(R.id.startBtn);
        stopBtn = findViewById(R.id.stopBtn);
        Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
        startActivityForResult(intent, 0);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audio.unpause();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audio.pause();
            }
        });
        Thread t = new Thread(audio);
        t.start();
        //audio.run();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audio.exit();
    }
}
