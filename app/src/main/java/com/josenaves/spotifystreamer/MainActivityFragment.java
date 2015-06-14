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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static final String TAG = MainActivityFragment.class.getSimpleName();

    public static final String ARTIST_ID = "artist_id";
    public static final String ARTIST_NAME = "artist_name";

    private SpotifyService spotifyService;

    private EditText txtSearch;

    private ArtistAdapter adapter;
    private FragmentActivity listener;

    public MainActivityFragment() {
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
        String country = sharedPreferences.getString(SettingsActivity.KEY_LOCATION, "BR");

        spotifyService = SpotifyRestService.getInstance(country);

        // create the adapter to convert the results into views
        adapter = new ArtistAdapter(listener, new ArrayList<Artist>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView...");
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        txtSearch = (EditText)view.findViewById(R.id.txtSearch);
        txtSearch.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                    keyCode == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    InputMethodManager imm = (InputMethodManager)listener.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);

                    String query = txtSearch.getText().toString();
                    Log.d(TAG, "Searching " + query);

                    new ArtistSearch().execute(query);

                    return true;
                }
                return false;
            }
        });

        // attach the adapter to the listview
        ListView lstResults = (ListView) view.findViewById(R.id.lstResults);
        lstResults.setAdapter(adapter);
        lstResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Artist artist = (Artist) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(listener, TopTenActivity.class);
                intent.putExtra(ARTIST_ID, artist.id);
                intent.putExtra(ARTIST_NAME, artist.name);
                startActivity(intent);
            }
        });


        return view;
    }

    /**
     * AsyncTask for doing network stuff off the main thread
     */
    private class ArtistSearch extends AsyncTask<String, Void, List<Artist>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((MainActivity)listener).showProgressBar();
        }

        @Override
        protected List<Artist> doInBackground(String... params) {
            ArtistsPager artistsPager = spotifyService.searchArtists(params[0]);
            return artistsPager.artists.items;
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {
            Log.d(TAG, "Got results... " + artists);

            adapter.clear();
            if (artists != null && !artists.isEmpty()) {
                adapter.addAll(artists);
            }
            else {
                String msg = "Nothing found for " + txtSearch.getText().toString();
                Toast.makeText(listener, msg, Toast.LENGTH_SHORT).show();
            }

            ((MainActivity)listener).hideProgressBar();
        }
    }

    /**
     * Adapter for results listview
     */
    private class ArtistAdapter extends ArrayAdapter<Artist> {

        private final Context context;
        private final List<Artist> artists;

        public ArtistAdapter(Context context, List<Artist> artists) {
            super(context, R.layout.list_item_artist, artists);
            this.context = context;
            this.artists = artists;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Artist artist = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.txtArtist);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imgArtist);

            textView.setText(artist.name);

            if (artist.images.size() > 0) {
                Picasso.with(context).
                        load(artist.images.get(0).url).
                        resize(96, 96).
                        centerCrop().
                        into(imageView);
            }

            return convertView;
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause...");
        super.onPause();
    }


}
