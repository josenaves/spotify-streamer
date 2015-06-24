package com.josenaves.spotifystreamer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity {

    public static final String TAG = PlayerActivity.class.getSimpleName();

    private String artistId;
    private String artistName;
    private String trackId;
    private String trackName;
    private String trackArt;
    private String trackUrl;
    private String albumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        albumName = intent.getStringExtra(Constants.ALBUM_NAME);
        artistName = intent.getStringExtra(Constants.ARTIST_NAME);
        artistId = intent.getStringExtra(Constants.ARTIST_ID);
        trackId = intent.getStringExtra(Constants.TRACK_ID);
        trackArt = intent.getStringExtra(Constants.TRACK_ART);
        trackName = intent.getStringExtra(Constants.TRACK_NAME);
        trackUrl = intent.getStringExtra(Constants.TRACK_URL);

        setContentView(R.layout.activity_player);

        getSupportActionBar().setSubtitle(R.string.app_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_ten, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getTrackArt() {
        return trackArt;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public String getAlbumName() {
        return albumName;
    }
}
