package com.example.heartogether;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class PitchActivityTest extends AppCompatActivity {

    private static final String TAG = "PitchActivityTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitchactivitytest);

        AudioDispatcher dispatcher =
                AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e){
                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processPitch(pitchInHz);
                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(dispatcher, "PitchAudioThread");
        audioThread.start();
    }

    public void processPitch(float pitchInHz) {

        TextView pitchText = findViewById(R.id.pitchText);
        TextView noteText = findViewById(R.id.noteText);


        pitchText.setText("" + pitchInHz);

        if(pitchInHz >= 110 && pitchInHz < 123.47) {
            //A
            noteText.setText("A");
        }
        else if(pitchInHz >= 123.47 && pitchInHz < 130.81) {
            //B
            noteText.setText("B");
        }
        else if(pitchInHz >= 130.81 && pitchInHz < 146.83) {
            //C
            noteText.setText("C");
        }
        else if(pitchInHz >= 146.83 && pitchInHz < 164.81) {
            //D
            noteText.setText("D");
        }
        else if(pitchInHz >= 164.81 && pitchInHz <= 174.61) {
            //E
            noteText.setText("E");
        }
        else if(pitchInHz >= 174.61 && pitchInHz < 185) {
            //F
            noteText.setText("F");
        }
        else if(pitchInHz >= 185 && pitchInHz < 196) {
            //G
            noteText.setText("G");
        }

        // SendOverWifi(pitchInHz);
    }

    // Do something like record voice input to text and pass it into the input stream here
    private void SendOverWifi(float pitch) {
        Context context = this.getApplicationContext();
        String host = this.getIP();
        int port = 8888;
        int len;
        Socket socket = new Socket();
        byte buf[]  = new byte[1024];
        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), 500);

            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data is retrieved by the server device.
             */
            OutputStream outputStream = socket.getOutputStream();
            ContentResolver cr = context.getContentResolver();
            InputStream inputStream = null;
            // inputStream = cr.openInputStream(pitch); PASS VOICE DATA FROM TEXT OR SOME FILE INTO HERE
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
            //catch com.example.heartogether.logic
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            //catch com.example.heartogether.logic
        }

        /**
         * Clean up any open sockets when done
         * transferring or if an exception occurred.
         */
                finally {
                    if (socket != null) {
                        if (socket.isConnected()) {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                //catch com.example.heartogether.logic
                            }
                        }
                    }
                }
    }

    private String getIP() {
        try {
            Socket socket = new Socket("google.com", 80);
            return socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }
}
