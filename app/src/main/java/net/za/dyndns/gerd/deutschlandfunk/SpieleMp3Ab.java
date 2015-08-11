package net.za.dyndns.gerd.deutschlandfunk;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by hanno on 2015-07-27 19:20.
 */
public class SpieleMp3Ab implements AudioManager.OnAudioFocusChangeListener {
    private Activity activity;
    private Context context;
    private Context kontext;
    private TextView zeigeJetzt, zeigeDauer, tx3;
    private int debug;
    private Button tasteVor, tastePause,
            tasteSpiel, tasteRück, tasteStop, tasteLösche;

    private MediaPlayer mediaPlayer;
    private double zeitJetzt = 0;
    private double zeitDauer = 0;
    private Handler myHandler = new Handler();
    private int forwardTime = 16000;
    private int backwardTime = 16000;
    private SeekBar seekBar;
    public static int oneTimeOnly = 0;
    private Helfer h;

    SpieleMp3Ab(Activity activity, Context context, int debug) {
        this.activity = activity;
        this.context = context;
        this.debug = debug;
        kontext = activity.getApplicationContext();
        h = new Helfer(context, debug);
        h.logi(4, "Pl10", "Konstruktor SpieleMp3Ab");
        h.logi(4, "Pl20", context.toString());
        h.logi(4, "Pl20", kontext.toString());
    }

    public void hören(final File result) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int erg = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (erg != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // could not get audio focus.
            h.logi(4, "Pl30", "hören nicht AUDIOFOCUS_REQUEST_GRANTED");
        } else {
            // could get audio focus.
            h.logi(4, "Pl30", "hören AUDIOFOCUS_REQUEST_GRANTED");
        }
        // http://www.tutorialspoint.com/android/android_mediaplayer.htm
        mediaPlayer = MediaPlayer.create(context, Uri.fromFile(result));
        seekBar = (SeekBar) activity.findViewById(R.id.SeekBar01);
        //seekBar.setMax((int) zeitDauer);
        //seekBar.setProgress(0);
        seekBar.setClickable(false);
        tasteRück = (Button) activity.findViewById(R.id.buttonRück);
        tastePause = (Button) activity.findViewById(R.id.buttonPause);
        tasteStop = (Button) activity.findViewById(R.id.buttonStop);
        tasteSpiel = (Button) activity.findViewById(R.id.buttonSpiel);
        tasteVor = (Button) activity.findViewById(R.id.buttonVor);
        tasteLösche = (Button) activity.findViewById(R.id.buttonLösche);
        zeigeJetzt = (TextView) activity.findViewById(R.id.zeigeJetzt);
        zeigeDauer = (TextView) activity.findViewById(R.id.zeigeDauer);
        //tx3 = (TextView) activity.findViewById(R.id.fortschritt);

        tasteRück.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int) zeitJetzt;
                kontext = activity.getApplicationContext();
                if ((temp - backwardTime) > 0) {
                    zeitJetzt = zeitJetzt - backwardTime;
                    mediaPlayer.seekTo((int) zeitJetzt);
                    Toast.makeText(kontext, String.format("You have Jumped backward %d seconds", backwardTime / 1000), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(kontext, String.format("Cannot jump backward %d seconds", backwardTime / 1000), Toast.LENGTH_SHORT).show();
                }
            }
        });

        tasteVor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int) zeitJetzt;
                kontext = activity.getApplicationContext();
                if ((temp + forwardTime) <= zeitDauer) {
                    zeitJetzt = zeitJetzt + forwardTime;
                    mediaPlayer.seekTo((int) zeitJetzt);
                    Toast.makeText(kontext, String.format("You have Jumped forward %d seconds", forwardTime / 1000), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(kontext, String.format("Cannot jump forward %d seconds", forwardTime / 1000), Toast.LENGTH_SHORT).show();
                }
            }
        });

        tasteStop.setEnabled(false);
        tasteStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kontext = activity.getApplicationContext();
                Toast.makeText(kontext, "Stop sound", Toast.LENGTH_SHORT).show();
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                myHandler.removeCallbacks(UpdateSongTime);
                tasteStop.setEnabled(false);
                tasteSpiel.setEnabled(false);
                tastePause.setEnabled(false);
                h.logi(4, "Pl23", String.format("gestoppt %s", result));
            }
        });

        tasteLösche.setEnabled(false);
        tasteLösche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kontext = activity.getApplicationContext();
                Toast.makeText(kontext, "Stop and delete sound", Toast.LENGTH_SHORT).show();
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                myHandler.removeCallbacks(UpdateSongTime);
                tasteStop.setEnabled(false);
                tasteLösche.setEnabled(false);
                tasteSpiel.setEnabled(false);
                tastePause.setEnabled(false);
                String löschmeldung = "";
                if (result.delete()) {
                    löschmeldung = String.format("%s gelöscht.", result);
                } else {
                    löschmeldung = result + " nicht gelöscht.";
                }
                h.logi(4, "Pl33", löschmeldung);
                Toast.makeText(kontext, löschmeldung, Toast.LENGTH_SHORT).show();
            }
        });

        tastePause.setEnabled(false);
        tastePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kontext = activity.getApplicationContext();
                Toast.makeText(kontext, "Pausing sound", Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
                myHandler.removeCallbacks(UpdateSongTime);
                tastePause.setEnabled(false);
                tasteSpiel.setEnabled(true);
            }
        });

        tasteSpiel.setEnabled(true);
        tasteSpiel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kontext = activity.getApplicationContext();
                Toast.makeText(kontext, "Playing sound", Toast.LENGTH_SHORT).show();
                mediaPlayer.start();

                zeitDauer = mediaPlayer.getDuration();
                zeitJetzt = mediaPlayer.getCurrentPosition();

                if (oneTimeOnly == 0) {
                    seekBar.setMax((int) zeitDauer);
                    h.logi(4, "Pl55", String.format("oneTimeOnly Max=%s", zeitDauer));
                    oneTimeOnly = 1;
                } else {
                    seekBar.setMax((int) zeitDauer);
                    h.logi(4, "Pl56", String.format("oneTimeOnly Max=%s", zeitDauer));
                }
                seekBar.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // This is event handler thumb moving event
                        //seekChange(v);
                        if (mediaPlayer.isPlaying()) {
                            SeekBar seekBar = (SeekBar) v;
                            if (debug > 3) Log.i("Pl80", seekBar.toString());
                            mediaPlayer.seekTo(seekBar.getProgress());
                        }
                        if (debug > 3) Log.i("Pl70", event.toString());
                        return false;
                    }
                });

                zeigeDauer.setText(String.format("%d min, %d sec",
                                TimeUnit.MILLISECONDS.toMinutes((long) zeitDauer),
                                TimeUnit.MILLISECONDS.toSeconds((long) zeitDauer) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) zeitDauer))
                        )
                );

                zeigeJetzt.setText(String.format("%d min, %d sec",
                                TimeUnit.MILLISECONDS.toMinutes((long) zeitJetzt),
                                TimeUnit.MILLISECONDS.toSeconds((long) zeitJetzt) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) zeitJetzt))
                        )
                );

                seekBar.setProgress((int) zeitJetzt);
                myHandler.postDelayed(UpdateSongTime, 987);
                tasteStop.setEnabled(true);
                tasteLösche.setEnabled(true);
                tastePause.setEnabled(true);
                tasteSpiel.setEnabled(false);
            }
        });

    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if (mediaPlayer == null) {
                h.logi(8, "Pl50", "UpdateSongTime");
                return; // wegen release
            } else {
                zeitJetzt = mediaPlayer.getCurrentPosition();
                h.logi(8, "Pl51", String.format("zeitJetzt %s", zeitJetzt));
                zeigeJetzt.setText(String.format("%d min, %d sec",

                                TimeUnit.MILLISECONDS.toMinutes((long) zeitJetzt),
                                TimeUnit.MILLISECONDS.toSeconds((long) zeitJetzt) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                toMinutes((long) zeitJetzt))
                        )
                );
                seekBar.setProgress((int) zeitJetzt);
                myHandler.postDelayed(this, 987);
            }
        }
    };

    @Override
    public void onAudioFocusChange(int i) {
        h.logi(4, "Pl44", "AudioFocusChange");

    }
}
