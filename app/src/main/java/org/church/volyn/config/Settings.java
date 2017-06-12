package org.church.volyn.config;

import org.church.volyn.BuildConfig;

/**
 * Created by Yaroslav on 19.06.2016.
 */
public class Settings {

    public static final int DEVICE_SCREEN_PHONE = 1;
    public static final int DEVICE_SCREEN_TABLET = 2;
    public static final int DEVICE_SCREEN_TV = 3;

    public static final boolean ENABLE_ROTATION_FOR_TABLET = false;
    public static final boolean DEBUG = BuildConfig.BUILD_TYPE.contains("release") ? false : true;
    public static final boolean ENABLE_CRASHLYTICS = BuildConfig.BUILD_TYPE.contains("release") ? false : true;
    static final String YOUTUBE_DEBUG_KEY = "deleted";
    static final String YOUTUBE_RELEASE_KEY = "deleted";
    public static final String YOUTUBE_KEY = DEBUG ? YOUTUBE_DEBUG_KEY : YOUTUBE_RELEASE_KEY;
}
