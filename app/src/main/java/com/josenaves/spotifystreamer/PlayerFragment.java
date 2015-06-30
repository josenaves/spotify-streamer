package com.josenaves.spotifystreamer;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerFragment extends Fragment {

    public static final String TAG = PlayerFragment.class.getSimpleName();

    private static final String TRACK_LENGTH = "0:30";

    private static final int MAX_LENGTH_MILLI = 30000;

    private AppCompatActivity listener;
    private TrackChangedListener callback;

    private TextView txtArtist;
    private TextView txtAlbum;
    private TextView txtTrack;
    private TextView txtTrackLength;
    private TextView txtElapsed;
    private ImageView imgArt;
    private SeekBar seekBar;
    private ImageButton btnPlay;
    private ImageButton btnPrevious;
    private ImageButton btnNext;

    private Drawable iconPlay;
    private Drawable iconPause;

    private int timeElapsed;

    private int position;
    private SpotifyTrackParcelable spotifyTrackParcelable;
    private ArrayList<SpotifyTrackParcelable> tracks;

    private boolean isPlaying = false;

    // http://examples.javacodegeeks.com/android/android-mediaplayer-example/
    private MediaPlayer mediaPlayer;

    private Handler durationHandler = new Handler();

    private boolean mTwoPane = false;

    public static PlayerFragment newInstance(SpotifyTrackParcelable spotifyTrackParcelable) {
        PlayerFragment playerFragment = new PlayerFragment();
        playerFragment.spotifyTrackParcelable = spotifyTrackParcelable;
        playerFragment.mTwoPane = true;
        return playerFragment;
    }

    public PlayerFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach...");
        super.onAttach(activity);
        listener = (AppCompatActivity)activity;
        if (mTwoPane) {
            try {
                callback = (TrackChangedListener) activity;
            }
            catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement TrackChangedListener");
            }
        }
        else {
            position = ((PlayerActivity)listener).getTrackPosition();
            tracks = ((PlayerActivity)listener).getTracks();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate...");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (!mTwoPane) {
            spotifyTrackParcelable = ((PlayerActivity)listener).getSpotifyTrackParcelable();
        }

        Resources resources = getResources();
        iconPlay = resources.getDrawable(resources.getIdentifier("@android:drawable/ic_media_play", null, null));
        iconPause = resources.getDrawable(resources.getIdentifier("@android:drawable/ic_media_pause", null, null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView...");
        Log.d(TAG, spotifyTrackParcelable.toString());

        View view = inflater.inflate(R.layout.fragment_player, container, false);

        btnPrevious = (ImageButton)view.findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    callback.onPrevious();
                }
                else {
                    if (position > 0) {
                        position--;
                        spotifyTrackParcelable = tracks.get(position);
                        if (mediaPlayer.isPlaying()) pauseMusic(true);
                        refreshView();
                    }
                    else {
                        Toast.makeText(getActivity(), R.string.message_minimum_track, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnNext = (ImageButton)view.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    callback.onNext();
                }
                else {
                    if (position < tracks.size() - 1) {
                        position++;
                        spotifyTrackParcelable = tracks.get(position);
                        if (mediaPlayer.isPlaying()) pauseMusic(true);
                        refreshView();
                    }
                    else {
                        Toast.makeText(getActivity(), R.string.message_maximum_track, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnPlay = (ImageButton)view.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! isPlaying) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    prepareAndPlayMedia();

                    // change button play to pause
                    btnPlay.setImageDrawable(iconPause);
                }
                else if (isPlaying) {
                    pauseMusic();
                }
            }
        });

        seekBar = (SeekBar)view.findViewById(R.id.seekTrackLength);
        seekBar.setMax(MAX_LENGTH_MILLI);
        seekBar.setClickable(true);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        txtArtist = (TextView) view.findViewById(R.id.txtArtist);
        txtAlbum = (TextView) view.findViewById(R.id.txtAlbum);
        txtTrack = (TextView) view.findViewById(R.id.txtTrack);
        txtTrackLength = (TextView) view.findViewById(R.id.txtLength);
        txtElapsed = (TextView) view.findViewById(R.id.txtElapsed);
        imgArt = (ImageView) view.findViewById(R.id.imgArt);

        refreshView();

        return view;
    }

    private void pauseMusic() {
        pauseMusic(false);
    }

    private void pauseMusic(boolean release) {
        if (release) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        else {
            mediaPlayer.pause();
        }

        // change button play to play
        btnPlay.setImageDrawable(iconPlay);
        isPlaying = false;
    }

    private void refreshView() {
        durationHandler.removeCallbacks(updateSeekBar);
        timeElapsed = 0;
        seekBar.setProgress(0);

        txtArtist.setText(spotifyTrackParcelable.getArtistName());
        txtAlbum.setText(spotifyTrackParcelable.getAlbumName());
        txtTrack.setText(spotifyTrackParcelable.getTrackName());
        txtTrackLength.setText(TRACK_LENGTH);
        txtElapsed.setText("0:00");
        String trackArt = spotifyTrackParcelable.getTrackArtUrl();
        if (trackArt != null) {
            Picasso.with(listener.getBaseContext()).
                    load(trackArt).
                    resize(400, 400).
                    centerCrop().
                    into(imgArt);
        }
    }

    // handler to change seekBar
    private Runnable updateSeekBar = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();

            //set seekbar progress
            seekBar.setProgress((int) timeElapsed);

            txtElapsed.setText(String.format("%01d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed),
                    TimeUnit.MILLISECONDS.toSeconds((long) timeElapsed)));

            //repeat yourself that again in 100 milliseconds
            durationHandler.postDelayed(this, 100);
        }
    };

    private void prepareAndPlayMedia()  {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(spotifyTrackParcelable.getTrackAudioUrl());
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                durationHandler.removeCallbacks(updateSeekBar);
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                updateSeekBar.run();
                isPlaying = true;
            }
        });
        mediaPlayer.prepareAsync();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState...");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause...");
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        durationHandler.removeCallbacks(updateSeekBar);
        super.onStop();
    }
}