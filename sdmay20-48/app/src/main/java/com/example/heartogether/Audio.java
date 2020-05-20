package com.example.heartogether;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.Equalizer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

/*
 * Thread to manage live recording/playback of voice input from the device's microphone.
 */
//https://stackoverflow.com/questions/6959930/android-need-to-record-mic-input?noredirect=1&lq=1
public class Audio implements Runnable {
    private boolean paused = true;
    private boolean exited = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private static final String TAG = "Audio";

    private Context context;
    private Activity activity;

    /**
     * Give the thread high priority so that it's not canceled unexpectedly, and start it
     */
    public Audio(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},
                    123);
        }
    }

    @Override
    public void run() {
        paused = false;
        if (ContextCompat.checkSelfPermission(this.activity,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1234);
        }
        /*
         * Initialize buffer to hold continuously recorded audio data, start recording, and start
         * playback.
         */
        Log.i(TAG, "Running Audio Thread");
        AudioRecord recorder = null;
        AudioTrack track = null;
        final int sampleRate = getSampleRate();
        try {
            int N = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AUDIO_FORMAT);
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AUDIO_FORMAT, N * 10);
            track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO, AUDIO_FORMAT, N * 10, AudioTrack.MODE_STREAM);
            int sessionId = track.getAudioSessionId();
            int priority = android.os.Process.THREAD_PRIORITY_URGENT_AUDIO;
            int bufferLen = 1024;
            short[][] buffers = new short[256][bufferLen];
            int ix = 0;
            recorder.startRecording();
            track.play();
            Equalizer ae = new Equalizer(priority, sessionId);

            /*
             * Loops until something outside of this thread stops it.
             * Reads the data from the recorder and writes it to the audio track for playback.
             */
            while (!exited) {
                if(!paused) {
                    track.play();
                    short[] buffer = buffers[ix++ % buffers.length];
                    recorder.read(buffer, 0, buffer.length, AudioRecord.READ_BLOCKING);
                    // Do fft transform
                        /*
                        FloatFFT_1D fft = new FloatFFT_1D(bufferLen);
                        short[] rawSoundData = new short[buffer.length];
                        float[] complexSoundData = new float[bufferLen*2];
                        System.arraycopy(floatMe(rawSoundData), 0, complexSoundData, 0, bufferLen);
                        float[] realSoundData = new float[buffer.length];
                        float[] imaginarySoundData = new float[buffer.length];
                        for(int i = 0; i < complexSoundData.length; i++) {
                            if(i%2 == 0) {
                                realSoundData[i/2] = complexSoundData[i];
                            } else {
                                imaginarySoundData[i/2] = complexSoundData[i];
                            }
                        }
                        fft.realForward(realSoundData);
                        fft.complexForward(imaginarySoundData);
                        final float[] magnitudes = toMagnitudes(realSoundData, imaginarySoundData);
                        // https://stackoverflow.com/questions/53997426/java-how-to-get-current-frequency-of-audio-input VERY IMPORTANT LINK
                        Log.i(TAG, Arrays.toString(magnitudes));
                        Log.i(TAG, Arrays.toString(imaginarySoundData));
                        Log.i(TAG, Arrays.toString(realSoundData));
                        //Convert magnitudes back to sound data?
                        fft.complexInverse(complexSoundData, true);
                        float[] invertedRealSoundData = new float[buffer.length];
                        int binWidth = (sampleRate / 2) / bufferLen;
                        for(int i = 0; i < complexSoundData.length; i+=2) {
                            //Just real bit
                            invertedRealSoundData[i/2] = complexSoundData[i];
                        }
                        rawSoundData = shortMe(invertedRealSoundData);
                        System.arraycopy(rawSoundData, 0, buffer, 0, buffer.length);
                        //send out data
                         */
                    track.write(buffer, 0, buffer.length, AudioTrack.WRITE_BLOCKING);
                } else {
                    ix = 0;
                    buffers = new short[256][bufferLen];
                    track.flush();
                    track.pause();
                }
            }
        } catch (Throwable x) {
            Log.e(TAG, "Error reading voice audio", x);
        } finally {
            /*
             * Frees the thread's resources after the loop completes so that it can be run again
             */
            recorder.stop();
            recorder.release();
            track.stop();
            track.release();
        }
    }

    private static int getSampleRate() {
        for (int rate : new int[]{44100, 22050, 11025, 16000, 8000}) {
            int buffersize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_CONFIGURATION_DEFAULT, AUDIO_FORMAT);
            if (buffersize > 0) {
                return rate;
            }
        }
        return 8000;
    }

    public void exit() {
        exited = true;
    }

    public void pause() {
        paused = true;
    }

    public void unpause() {
        paused = false;
    }

    private static float[] toMagnitudes(final float[] realPart, final float[] imaginaryPart) {
        final float[] powers = new float[realPart.length / 2];
        for (int i = 0; i < powers.length; i++) {
            powers[i] = (float) Math.sqrt(realPart[i] * realPart[i] + imaginaryPart[i] * imaginaryPart[i]);
        }
        return powers;
    }

    public static short[] shortMe(float[] fftdata) {
        short[] shorters = new short[fftdata.length];
        for (int i = 0; i < fftdata.length; i++) {
            //narrowing conversion!
            shorters[i] = (short) fftdata[i];
        }
        return shorters;
    }

    //https://stackoverflow.com/questions/10324355/how-to-convert-16-bit-pcm-audio-byte-array-to-double-or-float-array
    public static float[] floatMe(short[] pcms) {
        float[] floaters = new float[pcms.length];
        for (int i = 0; i < pcms.length; i++) {
            floaters[i] = pcms[i];
        }
        return floaters;
    }
}