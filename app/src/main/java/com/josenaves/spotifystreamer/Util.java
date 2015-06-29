package com.josenaves.spotifystreamer;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

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
}
