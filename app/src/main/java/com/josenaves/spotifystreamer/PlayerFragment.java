package com.josenaves.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
    private ImageButton btnRewind;
    private ImageButton btnForward;


    private int timeElapsed;
    private int finalTime;

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

        btnForward = (ImageButton)view.findViewById(R.id.btnForward);
        btnRewind = (ImageButton)view.findViewById(R.id.btnRewind);
        btnPlay = (ImageButton)view.findViewById(R.id.btnPlay);

        seekBar = (SeekBar)view.findViewById(R.id.seekTrackLength);
        seekBar.setMax(MAX_LENGTH_MILLI);
        seekBar.setClickable(false); // the user cannot drag the seekbar to any point

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

            //set time remaining
            double timeRemaining = finalTime - timeElapsed;

            txtElapsed.setText(
                    String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                            TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

            //repeat yourself that again in 100 milliseconds
            durationHandler.postDelayed(this, 100);
        }
    };


    private void prepareMedia()  {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(trackUrl);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

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
        super.onStop();
    }

}
