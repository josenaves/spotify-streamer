package com.josenaves.spotifystreamer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    public static final String TAG = PlayerActivity.class.getSimpleName();

    private SpotifyTrackParcelable spotifyTrackParcelable;
    private ArrayList<SpotifyTrackParcelable> tracks;
    private int trackPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        spotifyTrackParcelable = intent.getParcelableExtra(Constants.TRACK);
        tracks = intent.getParcelableArrayListExtra(Constants.TRACKS);
        trackPosition = intent.getIntExtra(Constants.POSITION, 0);
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

    public SpotifyTrackParcelable getSpotifyTrackParcelable() {
        return spotifyTrackParcelable;
    }

    public ArrayList<SpotifyTrackParcelable> getTracks() {
        return tracks;
    }

    public int getTrackPosition() {
        return trackPosition;
    }
}
