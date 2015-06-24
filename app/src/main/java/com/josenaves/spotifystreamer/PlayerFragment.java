package com.josenaves.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class PlayerFragment extends Fragment {

    public static final String TAG = PlayerFragment.class.getSimpleName();

    private static final String TRACK_LENGTH = "0:30";

    private FragmentActivity listener;

    private String albumName;
    private String artistId;
    private String artistName;
    private String trackId;
    private String trackArt;
    private String trackUrl;
    private String trackName;

    private TextView txtArtist;
    private TextView txtAlbum;
    private TextView txtTrack;
    private TextView txtTrackLength;
    private ImageView imgArt;


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

        albumName = ((PlayerActivity)listener).getAlbumName();
        artistId = ((PlayerActivity)listener).getArtistId();
        artistName = ((PlayerActivity)listener).getArtistName();
        trackId = ((PlayerActivity)listener).getTrackId();
        trackArt = ((PlayerActivity)listener).getTrackArt();
        trackName = ((PlayerActivity)listener).getTrackName();
        trackUrl = ((PlayerActivity)listener).getTrackUrl();

        setRetainInstance(true);
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


        txtArtist = (TextView) view.findViewById(R.id.txtArtist);
        txtArtist.setText(artistName);

        txtAlbum = (TextView) view.findViewById(R.id.txtAlbum);
        txtAlbum.setText(albumName);

        txtTrack = (TextView) view.findViewById(R.id.txtTrack);
        txtTrack.setText(trackName);

        txtTrackLength = (TextView) view.findViewById(R.id.txtLength);
        txtTrackLength.setText(TRACK_LENGTH);


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

}
