package org.church.volyn.downloadHelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.church.volyn.entities.NewsItem;

/**
 * Created by Admin on 13.02.2015.
 */
public class ImageDownloadRunnable implements Runnable {

    static final int HTTP_STATE_FAILED = -1;
    static final int HTTP_STATE_STARTED = 0;
    static final int HTTP_STATE_COMPLETED = 1;

    final DownloadRunnableMethods mImageTask;

    interface DownloadRunnableMethods {
        Bitmap getBitmap();
        void setBitmap(Bitmap bitmap);
        void setByteArray(byte[] imageBytes);
        void handleDownloadState(int state);
        NewsItem getNewsItem();
    }

    ImageDownloadRunnable(DownloadRunnableMethods imageTask){
        mImageTask = imageTask;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        HttpClient httpClient = RetryingHttpClient.getHttpClient();
        HttpGet request = new HttpGet(mImageTask.getNewsItem().getImageUrl());
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
            InputStream is = response.getEntity().getContent();
            byte[] imageBytes = readBytes(is);
            Bitmap bm = BitmapFactory.decodeStream(new ByteArrayInputStream(imageBytes));
            if (bm != null) {
                mImageTask.setBitmap(bm);
                mImageTask.handleDownloadState(HTTP_STATE_COMPLETED);
            }

        } catch (Exception e) {
        }
    }

    public byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }
}
