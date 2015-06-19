package com.josenaves.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class PlayerActivity extends AppCompatActivity {

    public static final String TAG = PlayerActivity.class.getSimpleName();

    public static final String TRACK_ID = "track_id";

    private String artistId;
    private String artistName;
    private String trackId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        artistName = intent.getStringExtra(MainActivityFragment.ARTIST_NAME);
        artistId = intent.getStringExtra(MainActivityFragment.ARTIST_ID);
        trackId = intent.getStringExtra(PlayerActivity.TRACK_ID);

        setContentView(R.layout.activity_player);

        getSupportActionBar().setSubtitle(artistName);
    }




}
