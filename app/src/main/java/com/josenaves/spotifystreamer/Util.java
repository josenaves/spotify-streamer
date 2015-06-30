package com.josenaves.spotifystreamer;



import android.app.Activity;
import android.content.res.Configuration;

import kaaes.spotify.webapi.android.models.Track;

/**
 * This class have many utility methods
 */
public final class Util {

    public static boolean isTwoPaneMode(Activity activity) {
        boolean twoPane = false;
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // if search_container is present then the app is running on a tablet
            if (activity.findViewById(R.id.player_container) != null) {
                twoPane = true;
            }
            else {
                twoPane = false;
            }
        }
        return twoPane;
    }

    public static SpotifyTrackParcelable fromTrack(Track track, String artistId, String artistName) {
        SpotifyTrackParcelable spotifyTrackParcelable;

        String trackArt = null;
        if (track.album.images.size() > 0) {
            trackArt = track.album.images.get(0).url;
        }

        spotifyTrackParcelable = new SpotifyTrackParcelable(track.id, track.name,
                track.preview_url, trackArt, track.album.name, artistId, artistName);

        return spotifyTrackParcelable;
    }
}
