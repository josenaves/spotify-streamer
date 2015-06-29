package com.josenaves.spotifystreamer;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

/**
 * This class have many utility methods
 */
public final class Util {

    public static boolean isTabletMode(View view) {
        boolean tabletMode;
        // if search_container is present then the app is running on a tablet
        if (view.findViewById(R.id.tablet_container) != null) {
            tabletMode = true;
        }
        else {
            tabletMode = false;
        }
        return tabletMode;
    }

    public static boolean isTabletMode(Activity activity) {
        boolean tabletMode;
        // if search_container is present then the app is running on a tablet
        if (activity.findViewById(R.id.player_container) != null) {
            tabletMode = true;
        }
        else {
            tabletMode = false;
        }
        return tabletMode;
    }

    public static boolean isTabletMode(ViewGroup viewGroup) {
        boolean tabletMode;
        // if search_container is present then the app is running on a tablet
        if (viewGroup.findViewById(R.id.player_container) != null) {
            tabletMode = true;
        }
        else {
            tabletMode = false;
        }
        return tabletMode;
    }

}
