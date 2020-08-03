package com.example.musicbox;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageView playButton;
    private ImageView nextButton;
    private ImageView prevButton;
    private SeekBar seekBar;
    private TextView song_name;
    private TextView song_title;
    private TextView timeLeft;
    private TextView timeRight;
    private Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        song_name = (TextView) findViewById(R.id.song_name);
        song_title = (TextView) findViewById(R.id.song_title);
        timeLeft = (TextView) findViewById(R.id.time_left);
        timeRight = (TextView) findViewById(R.id.time_right);

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.song);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                 if(b){
                     mediaPlayer.seekTo(i);
                     int current = mediaPlayer.getCurrentPosition();
                     int duration = mediaPlayer.getDuration();
                     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.FRENCH);
                     timeLeft.setText(simpleDateFormat.format(new Date(current)));
                     timeRight.setText(simpleDateFormat.format(new Date(duration - current)));
                 }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                int duration = mp.getDuration();
                String mDuration = String.valueOf(duration/1000);

                Toast.makeText(MainActivity.this, "duration "+mDuration, Toast.LENGTH_LONG).show();
            }
        });

        playButton = (ImageView) findViewById(R.id.play);
        prevButton = (ImageView) findViewById(R.id.previous);
        nextButton = (ImageView) findViewById(R.id.next);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    pauseMusic();
                }
                else{
                    startMusic();
                }
            }
        });
    }

    public void pauseMusic(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            playButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
        }
    }

    public void startMusic(){
        if(mediaPlayer != null){
            mediaPlayer.start();
            playButton.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
            UpdateThread();
        }
    }

    @Override
    protected void onDestroy() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        thread.interrupt();
        thread = null;
        super.onDestroy();
    }

    private void UpdateThread(){
        thread = new Thread(){
            @Override
            public void run() {
                try {

                    while (mediaPlayer != null && mediaPlayer.isPlaying()){
                                Thread.sleep(50);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int newPosition = mediaPlayer.getCurrentPosition();
                                        int newMax = mediaPlayer.getDuration();
                                        seekBar.setMax(newMax);
                                        seekBar.setProgress(newPosition);
                                        ///
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.FRENCH);
                                        timeLeft.setText(simpleDateFormat.format(new Date(newPosition)));
                                        timeRight.setText(simpleDateFormat.format(new Date(newMax - newPosition)));
                                    }
                                });
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

        };

        thread.start();
    }
}

