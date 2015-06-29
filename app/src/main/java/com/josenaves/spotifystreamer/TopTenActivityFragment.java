package com.josenaves.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TopTenActivityFragment extends Fragment {

    public static final String TAG = TopTenActivityFragment.class.getSimpleName();

    private SpotifyService spotifyService;

    private FragmentActivity listener;

    private String artistId;
    private String artistName;

    private boolean mTwoPane = false;

    private TrackAdapter adapter;

    public TopTenActivityFragment() {
    }

    public static TopTenActivityFragment newInstance(String artistId, String artistName) {
        TopTenActivityFragment topTenActivityFragment = new TopTenActivityFragment();
        topTenActivityFragment.artistId = artistId;
        topTenActivityFragment.artistName = artistName;

        topTenActivityFragment.mTwoPane = true; // only true if newInstance method is called
        return topTenActivityFragment;
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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String country = sharedPreferences.getString(SettingsActivity.KEY_LOCATION, SettingsActivity.DEFAULT_LOCATION);

        spotifyService = SpotifyRestService.getInstance(country);

        if (!mTwoPane) {
            // not in twopane - get those parameters from parent activity
            artistId = ((TopTenActivity)listener).getArtistId();
            artistName = ((TopTenActivity)listener).getArtistName();
        }

        // create the adapter to convert the results into views
        adapter = new TrackAdapter(listener, new ArrayList<Track>());

        // get tracks via SpotifyService
        new TracksTask().execute(artistId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView...");
        View view = inflater.inflate(R.layout.fragment_top_ten, container, false);
        ListView listTracks = (ListView) view.findViewById(R.id.lstTracks);
        listTracks.setAdapter(adapter);
        listTracks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = (Track) parent.getItemAtPosition(position);

                if (!mTwoPane) {
                    // pass data for next activity
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra(Constants.ALBUM_NAME, track.album.name);
                    intent.putExtra(Constants.ARTIST_ID, artistId);
                    intent.putExtra(Constants.ARTIST_NAME, artistName);
                    intent.putExtra(Constants.TRACK_ID, track.id);
                    intent.putExtra(Constants.TRACK_NAME, track.name);
                    intent.putExtra(Constants.TRACK_URL, track.preview_url);

                    if (track.album.images.size() > 0) {
                        intent.putExtra(Constants.TRACK_ART, track.album.images.get(0).url);
                    }

                    // call next activity
                    startActivity(intent);
                }
                else {
                    // fragment mode
                    String trackArt =
                            track.album.images.size() > 0 ? track.album.images.get(0).url : null;

                    PlayerFragment playerFragment = PlayerFragment.newInstance(track.album.name,
                            artistId, artistName, track.id, trackArt, track.name, track.preview_url);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager
                            .beginTransaction()
                            .add(R.id.player_container, playerFragment)
                            .addToBackStack(playerFragment.TAG)
                            .commit();
                }
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState...");

        // TODO make it work
        //outState.putSerializable(BUNDLE_ADAPTER, adapter);
        outState.putString(Constants.ARTIST_ID, artistId);

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
