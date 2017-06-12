package org.church.volyn.downloadHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.church.volyn.entities.NewsItem;
import org.church.volyn.parser.NewsParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 09.12.2014.
 */
public class NewsDownloadRunnable implements Runnable {

    private String TAG = "NewsDownloadRunnable";
    private static final int READ_SIZE = 1024 * 2;

    // Sets a tag for this class
    @SuppressWarnings("unused")
    private static final String LOG_TAG = "PhotoDownloadRunnable";

    // Constants for indicating the state of the download
    static final int DOWNLOAD_STATE_FAILED = -1;
    static final int DOWNLOAD_STATE_STARTED = 0;
    static final int DOWNLOAD_STATE_COMPLETED = 1;

    // Defines a field that contains the calling object of type PhotoTask.
    final TaskRunnableNewsDownloadMethods mNewsTask;

    interface TaskRunnableNewsDownloadMethods {

        void setDownloadThread(Thread currentThread);
        void setNewsItem(NewsItem newsItem);
        void handleNewsDownloadState(int state);
        String getNewsUrl();
        NewsItem getNewsItem();
    }
    NewsDownloadRunnable(TaskRunnableNewsDownloadMethods newsTask) {

        mNewsTask = newsTask;
    }
    @Override
    public void run() {
        mNewsTask.setDownloadThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        HttpClient httpClient = RetryingHttpClient.getHttpClient();
//            String path = "http://volyn.church.ua/feed/";
        HttpGet request = new HttpGet(mNewsTask.getNewsUrl());

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(request);
            InputStream data = httpResponse.getEntity().getContent();
            String response = DownloadUtilities.readResponse(data);
            mNewsTask.getNewsItem().setNewsContent(getNewsContentFromResponse(response));
            mNewsTask.getNewsItem().setImageUrl(getImageUrlFromResponse(response));

            mNewsTask.handleNewsDownloadState(NewsDownloadRunnable.DOWNLOAD_STATE_COMPLETED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getNewsContentFromResponse(String response) {
        response = NewsParser.getNewsTextWithTags(response);
        String[] s = response.split("<div data-carousel-extra=");
        return s[0];
    }

    private String getImageUrlFromResponse(String response){
        String news = NewsParser.getNewsTextWithTags(response);

        String imageUrl = "";

        String[] s = response.split("<div data-carousel-extra=");
        Pattern pattern = Pattern.compile("<a.*<img.*</a>");
        Matcher matcher = pattern.matcher(s[0]);
        if (matcher.find()) {
            String imgTag = matcher.group();
            imageUrl = imgTag.split("\"", 3)[1];
            return imageUrl;
        }
        if (s.length > 1) {
            Pattern galleryImagePattern = Pattern.compile("data-orig-file=\".+?\"");
            matcher = galleryImagePattern.matcher(s[1]);

            if (matcher.find()) {
                String imgTag = matcher.group();
                return imageUrl;
            }

        }
        return imageUrl;

    }
}
