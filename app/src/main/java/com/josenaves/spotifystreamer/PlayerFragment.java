package com.josenaves.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class PlayerFragment extends Fragment {

    public static final String TAG = PlayerFragment.class.getSimpleName();

    private static final String TRACK_LENGTH = "0:30";

    private static final int MAX_LENGTH_MILLI = 30000;

    private FragmentActivity listener;

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

    private boolean isPlaying = false;

    // http://examples.javacodegeeks.com/android/android-mediaplayer-example/
    private MediaPlayer mediaPlayer;

    private Handler durationHandler = new Handler();

    private String albumName;
    private String artistId;
    private String artistName;
    private String trackId;
    private String trackArt;
    private String trackUrl;
    private String trackName;

    private boolean mTwoPane = false;

    public static PlayerFragment newInstance(String albumName, String artistId, String artistName,
                                             String trackId, String trackArt, String trackName,
                                             String trackUrl) {

        PlayerFragment playerFragment = new PlayerFragment();

        playerFragment.albumName = albumName;
        playerFragment.artistId = artistId;
        playerFragment.artistName = artistName;
        playerFragment.trackId = trackId;
        playerFragment.trackArt = trackArt;
        playerFragment.trackName = trackName;
        playerFragment.trackUrl = trackUrl;

        playerFragment.mTwoPane = true;
        return playerFragment;
    }

    public PlayerFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach...");
        super.onAttach(activity);
        this.listener = (FragmentActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate...");
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if (!mTwoPane) {
            albumName = ((PlayerActivity)listener).getAlbumName();
            artistId = ((PlayerActivity)listener).getArtistId();
            artistName = ((PlayerActivity)listener).getArtistName();
            trackId = ((PlayerActivity)listener).getTrackId();
            trackArt = ((PlayerActivity)listener).getTrackArt();
            trackName = ((PlayerActivity)listener).getTrackName();
            trackUrl = ((PlayerActivity)listener).getTrackUrl();
        }

        Resources resources = getResources();
        iconPlay = resources.getDrawable(resources.getIdentifier("@android:drawable/ic_media_play", null, null));
        iconPause = resources.getDrawable(resources.getIdentifier("@android:drawable/ic_media_pause", null, null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView...");
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        String msg = "trackId = " + trackId + " - artist = " + artistId + " - artistName = "
                + artistName  + " - trackArt = " + trackArt + " - trackName = " + trackName
                + " - trackUrl = " + trackUrl + " - albumName = " + albumName;

        Toast.makeText(listener.getBaseContext(), msg, Toast.LENGTH_LONG).show();

        Log.d(TAG, msg);

        btnNext = (ImageButton)view.findViewById(R.id.btnNext);
        btnPrevious = (ImageButton)view.findViewById(R.id.btnPrevious);
        btnPlay = (ImageButton)view.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! isPlaying) {
                    prepareAndPlayMedia();

                    // change button play to pause
                    btnPlay.setImageDrawable(iconPause);
                }
                else if (isPlaying) {
                    mediaPlayer.pause();
                    // change button play to play
                    btnPlay.setImageDrawable(iconPlay);
                    isPlaying = false;
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
        txtArtist.setText(artistName);

        txtAlbum = (TextView) view.findViewById(R.id.txtAlbum);
        txtAlbum.setText(albumName);

        txtTrack = (TextView) view.findViewById(R.id.txtTrack);
        txtTrack.setText(trackName);

        txtTrackLength = (TextView) view.findViewById(R.id.txtLength);
        txtTrackLength.setText(TRACK_LENGTH);

        txtElapsed = (TextView) view.findViewById(R.id.txtElapsed);

        if (trackArt != null) {
            imgArt = (ImageView) view.findViewById(R.id.imgArt);
            Picasso.with(listener.getBaseContext()).
                    load(trackArt).
                    resize(400, 400).
                    centerCrop().
                    into(imgArt);
        }

        return view;
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
            mediaPlayer.setDataSource(trackUrl);
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
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        durationHandler.removeCallbacks(updateSeekBar);
    }
}