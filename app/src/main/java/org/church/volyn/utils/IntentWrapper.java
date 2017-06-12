package org.church.volyn.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import org.church.volyn.App;
import org.church.volyn.R;
import org.church.volyn.config.Settings;
import org.church.volyn.downloadHelper.DownloadIntentService;
import org.church.volyn.entities.NewsItem;
import org.church.volyn.ui.activity.FullscreenMediaViewActivity;
import org.church.volyn.ui.activity.GalleryActivity;
import org.church.volyn.ui.activity.MainActivity;
import org.church.volyn.ui.activity.MainPhoneActivity;
import org.church.volyn.media.MediaDownloaderIntentService;

public class IntentWrapper {

    public static final boolean IS_TABLET = App.getContext().getResources().getInteger(R.integer.device_type)
            != Settings.DEVICE_SCREEN_PHONE;

    public static void openMainActivity(Context context) {
        final Intent i;
        if (IS_TABLET) {
            i = new Intent(context, MainActivity.class);
        } else {
            i = new Intent(context, MainPhoneActivity.class);
        }
        context.startActivity(i);
    }

    public static void startDownloadService(Context context) {
        if (!DownloadIntentService.isRunning()) {
            Intent i = new Intent(context, DownloadIntentService.class);
            context.startService(i);
        }
    }

    public static void startMediaDownloadService(Context context) {
        if (!MediaDownloaderIntentService.isRunning()) {
            Intent i = new Intent(context, MediaDownloaderIntentService.class);
            context.startService(i);
        }
    }

    public static void startMediaDownloadService(Context context, Handler handler) {
        if (!MediaDownloaderIntentService.isRunning()) {
            Intent i = new Intent(context, MediaDownloaderIntentService.class);
            i.putExtra("msg", new Messenger(handler));
            context.startService(i);
        }
    }

    public static void openGalleryActivity(Context context, NewsItem newsItem) {
        Intent i = new Intent(context, GalleryActivity.class);
        i.putExtra(ExtraParams.NEWS_GUID, newsItem.getGuid());
        i.putExtra(ExtraParams.NEWS_TITLE, newsItem.getTitle());
        context.startActivity(i);
    }

    public static void openFullScreenPhotoActivity(Context context, NewsItem newsItem) {
        Intent i = new Intent(context, FullscreenMediaViewActivity.class);
        i.putExtra(ExtraParams.NEWS_GUID, newsItem.getGuid());
        i.putExtra(ExtraParams.NEWS_TITLE, newsItem.getTitle());
        context.startActivity(i);
    }

    public static void openFullScreenYoutubeActivity(Context context, NewsItem newsItem) {
        Intent i = new Intent(context, FullscreenMediaViewActivity.class);
        i.putExtra(ExtraParams.NEWS_GUID, newsItem.getGuid());
        i.putExtra(ExtraParams.NEWS_TITLE, newsItem.getTitle());
        i.putExtra(ExtraParams.YOUTUBE_VIDEO, newsItem.getVideoUrl());
        context.startActivity(i);
    }
}
