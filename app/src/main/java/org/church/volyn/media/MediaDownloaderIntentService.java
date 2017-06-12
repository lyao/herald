package org.church.volyn.media;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import org.church.volyn.App;
import org.church.volyn.downloadHelper.DiskCache;
import org.church.volyn.downloadHelper.MediaFtpClient;
import org.church.volyn.entities.Category;
import org.church.volyn.entities.NewsItem;

/**
 * Created by user on 27.09.2015.
 */
public class MediaDownloaderIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */

    private static boolean isRunning = false;
    private Handler mHandler;
    private MediaDownloadListetner mDownloadListetner;
    private static Messenger mMessenger;
    private interface MediaDownloadListetner {
        void onParseFinished();
    }
    public MediaDownloaderIntentService() {
        super(MediaDownloaderIntentService.class.getName());
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        isRunning = true;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mMessenger = (Messenger) bundle.get("msg");

        }
        getMedias();
    }

    public static void getMedias() {
        MediaFtpClient mfc = new MediaFtpClient();
        mfc.connnectingwithFTP("deleted", "deleted", "deleted");
        String text = mfc.readFileFromFTP(MediaManager.MEDIA_FILE);
        if (text != null) {
            DiskCache.getInstance(App.getContext()).saveJsonToFile(MediaManager.MEDIA_FILE, text);
//            MediaParser mp = new MediaParser();
            MediaParser.parse(text);

            Message msg = Message.obtain();
            Bundle b = new Bundle();
            b.putInt("msg", 1);
            msg.setData(b); //put the data here
            try {
                mMessenger.send(msg);
            } catch (RemoteException e) {
                Log.i("error", "error");
            }
        }
    }
    public static boolean isRunning() {
        return isRunning;
    }

    public void setDownloadListetner(MediaDownloadListetner downloadListetner) {
        mDownloadListetner = downloadListetner;
    }
}
