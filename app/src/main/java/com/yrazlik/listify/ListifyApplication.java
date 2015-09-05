package com.yrazlik.listify;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by SUUSER on 05.09.2015.
 */
public class ListifyApplication extends Application{

    private Tracker mTracker;

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(AppConstants.GOOGLE_ANALYTICS_TRACKING_ID);
        }
        return mTracker;
    }
}
