package com.josenaves.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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

    private EditText txtSearch;
    private ListView lstResults;

    private ArtistAdapter adapter;


    private SpotifyService spotifyService = SpotifyRestService.getInstance("BR");

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        txtSearch = (EditText)view.findViewById(R.id.txtSearch);
        txtSearch.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                    keyCode == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    performSearch(txtSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });

        // create the adapter to convert the results into views
        adapter = new ArtistAdapter(getActivity(), new ArrayList<Artist>());

        lstResults = (ListView) view.findViewById(R.id.lstResults);

        // attach the adapter to the listview
        lstResults.setAdapter(adapter);

        lstResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Artist artist = (Artist)adapterView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), TopTenActivity.class);
                intent.putExtra(ARTIST_ID, artist.id);
                intent.putExtra(ARTIST_NAME, artist.name);
                startActivity(intent);
            }
        });


        return view;
    }

    private void performSearch(String query) {
        Log.d(TAG, "Buscando " + query);

        Toast.makeText(getActivity(), "Buscando...", Toast.LENGTH_SHORT).show();

        new ArtistSearch().execute(query);
    }

    /**
     * AsyncTask for doing network stuff off the main thread
     */
    private class ArtistSearch extends AsyncTask<String, Void, List<Artist>> {

        @Override
        protected List<Artist> doInBackground(String... params) {
            ArtistsPager artistsPager = spotifyService.searchArtists(params[0]);
            return artistsPager.artists.items;
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {
            Log.d(TAG, "chegou... " + artists);
            if (artists != null) {
                adapter.clear();
                adapter.addAll(artists);
            }
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


}
