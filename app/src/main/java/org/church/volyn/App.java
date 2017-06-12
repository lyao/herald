package org.church.volyn;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.church.volyn.config.Settings;

import io.fabric.sdk.android.Fabric;

/**
 * Created by user on 24.01.2015.
 */
public class App extends MultiDexApplication {
    private static App sInstance;
    public static App getContext() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        TwitterAuthConfig authConfig =
                new TwitterAuthConfig("NDa2uOJnoeDLLE2yTRSE5aFt1", "91S25TnNRvKNcX3kgby2N4MGDKbC7ZMqk1ZMXEXKy9aYeAHkyI");

        FacebookSdk.sdkInitialize(getApplicationContext());
    }

}
