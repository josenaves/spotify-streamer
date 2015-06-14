package com.josenaves.spotifystreamer;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

class SpotifyRestService {

    public static SpotifyService getInstance(final String country) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addEncodedQueryParam(SpotifyService.COUNTRY, country);
                    }
                })
                .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
                .build();

        return restAdapter.create(kaaes.spotify.webapi.android.SpotifyService.class);
    }
}
