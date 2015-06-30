package com.josenaves.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import clojure.lang.Cons;

public class TopTenActivity extends AppCompatActivity {

    public static final String TAG = TopTenActivity.class.getSimpleName();

    private String artistId;
    private String artistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate...");
        super.onCreate(savedInstanceState);

        artistName = getIntent().getStringExtra(Constants.ARTIST_NAME);
        artistId = getIntent().getStringExtra(Constants.ARTIST_ID);

        setContentView(R.layout.activity_top_ten);
        getSupportActionBar().setSubtitle(artistName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState...");
        super.onRestoreInstanceState(savedInstanceState);
        artistId = savedInstanceState.getString(Constants.ARTIST_ID);
        artistName = savedInstanceState.getString(Constants.ARTIST_NAME);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState...");
        Log.d(TAG, "saving artistId:" + artistId);
        outState.putString(Constants.ARTIST_ID, artistId);
        outState.putString(Constants.ARTIST_NAME, artistName);
        super.onSaveInstanceState(outState);
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
}