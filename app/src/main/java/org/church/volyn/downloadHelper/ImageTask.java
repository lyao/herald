package org.church.volyn.downloadHelper;

import android.graphics.Bitmap;

import org.church.volyn.entities.NewsItem;

/**
 * Created by Admin on 14.02.2015.
 */
public class ImageTask implements ImageDownloadRunnable.DownloadRunnableMethods {

    private NewsItem mNewsItem;
    private int mTargetHeight;
    private int mTargetWidth;

    private Runnable mDownloadRunnable;

    byte[] mImageBuffer;
    byte[] mImageBytes;
    private Bitmap mDecodedImage;

    private static ImageManager sImageManager;

    private final Object mHttpDiskCacheLock = new Object();

    ImageTask() {
        mDownloadRunnable = new ImageDownloadRunnable(this);
        sImageManager = ImageManager.getInstance();
    }

    void initializeDownloaderTask(NewsItem newsItem) {
        mNewsItem = newsItem;
    }

    @Override
    public Bitmap getBitmap() {
        return mDecodedImage;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        mDecodedImage = bitmap;

    }

    @Override
    public void setByteArray(byte[] imageBytes) {
       mImageBytes = imageBytes;
    }

    public byte[] getImageBytes() {
        return mImageBytes;
    }

    @Override
    public void handleDownloadState(int state) {
        int outState;
        switch (state){
            case ImageDownloadRunnable.HTTP_STATE_COMPLETED:
                outState = ImageManager.DOWNLOAD_COMPLETE;
//                FileImageCache.getInstance(App.getContext()).saveBitmap(mNewsItem, getBitmap());
//                ImageManager.getInstance().putBitmap(mNewsItem.getImageUrl(), getBitmap());
                break;
            case ImageDownloadRunnable.HTTP_STATE_FAILED:
                outState = ImageManager.DOWNLOAD_FAILED;
                break;
            default:
                outState = ImageManager.DOWNLOAD_FAILED;
        }
        handleState(this, outState);
    }

    @Override
    public NewsItem getNewsItem() {
        return mNewsItem;
    }

    private void handleState(ImageTask imageTask, int state) {
        sImageManager.handleState(this, state);
    }

    Runnable getDownloadRunnable() {
        synchronized (mHttpDiskCacheLock) {
            return mDownloadRunnable;
        }
    }

    void recycle() {
        // Releases references to the byte buffer and the BitMap
        mImageBuffer = null;
        mDecodedImage = null;
    }
}
