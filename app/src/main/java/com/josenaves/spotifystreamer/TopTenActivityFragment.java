package com.josenaves.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TopTenActivityFragment extends Fragment {

    public static final String TAG = TopTenActivityFragment.class.getSimpleName();

    private static final String BUNDLE_ARTIST = "artist";
    private static final String BUNDLE_ADAPTER = "adapter";

    private SpotifyService spotifyService = SpotifyRestService.getInstance("BR");

    private ListView listTracks;

    private FragmentActivity listener;

    protected String artistId;

    protected TrackAdapter adapter;


    public TopTenActivityFragment() {
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

//        if (savedInstanceState != null) {
//            adapter = (TrackAdapter)savedInstanceState.getSerializable(BUNDLE_ADAPTER);
//            artistId = savedInstanceState.getString(BUNDLE_ARTIST);
//        }
//        else {
//            artistId = ((TopTenActivity)listener).getArtistId();
//
//            // create the adapter to convert the results into views
//            adapter = new TrackAdapter(listener, new ArrayList<Track>());
//
//            // get tracks via SpotifyService
//            new TracksTask().execute(artistId);
//        }

        artistId = ((TopTenActivity)listener).getArtistId();

        // create the adapter to convert the results into views
        adapter = new TrackAdapter(listener, new ArrayList<Track>());

        // get tracks via SpotifyService
        new TracksTask().execute(artistId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView...");
        View view = inflater.inflate(R.layout.fragment_top_ten, container, false);
        listTracks = (ListView)view.findViewById(R.id.lstTracks);
        listTracks.setAdapter(adapter);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState...");

        // TODO make it work
        //outState.putSerializable(BUNDLE_ADAPTER, adapter);
        outState.putString(BUNDLE_ARTIST, artistId);

        super.onSaveInstanceState(outState);
    }

    /**
     * AsyncTask for doing network stuff off the main thread
     */
    private class TracksTask extends AsyncTask<String, Void, List<Track>> {

        @Override
        protected List<Track> doInBackground(String... params) {
            Log.d(TAG, "Get tracks on the interwebs...");
            Tracks tracks = spotifyService.getArtistTopTrack(params[0]);
            Log.d(TAG, "Tracks = " + tracks.tracks);
            return tracks.tracks;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            Log.d(TAG, "Got results... " + tracks);

            adapter.clear();
            if (tracks != null && !tracks.isEmpty()) {
                adapter.addAll(tracks);
            }
            else {
                String msg = "Sorry, no tracks for your artist";
                Toast.makeText(listener, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Adapter for results listview
     */
    private class TrackAdapter extends ArrayAdapter<Track> {

        private final Context context;
        private final List<Track> tracks;

        public TrackAdapter(Context context, List<Track> tracks) {
            super(context, R.layout.list_item_track, tracks);
            this.context = context;
            this.tracks = new ArrayList<>(tracks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Track track = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_track, parent, false);
            }

            TextView txtAlbum = (TextView) convertView.findViewById(R.id.txtAlbumName);
            TextView txtTrack = (TextView) convertView.findViewById(R.id.txtTrackName);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imgAlbum);

            txtAlbum.setText(track.album.name);
            txtTrack.setText(track.name);

            if (track.album.images.size() > 0) {
                Picasso.with(context).
                        load(track.album.images.get(0).url).
                        resize(96, 96).
                        centerCrop().
                        into(imageView);
            }

            return convertView;
        }

        public List<Track> getValues() { return tracks; }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause...");
        super.onPause();
    }

}
