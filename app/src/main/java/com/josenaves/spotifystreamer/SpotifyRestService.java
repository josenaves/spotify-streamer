package com.josenaves.spotifystreamer;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import retrofit.RestAdapter;

public class SpotifyRestService {

    public static SpotifyService getInstance() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
                .build();

        return restAdapter.create(kaaes.spotify.webapi.android.SpotifyService.class);
    }
}
