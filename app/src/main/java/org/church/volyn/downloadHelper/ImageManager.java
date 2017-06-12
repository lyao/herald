package org.church.volyn.downloadHelper;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.church.volyn.App;
import org.church.volyn.entities.NewsItem;
import org.church.volyn.ui.adapter.CategoryAdapter;
import org.church.volyn.ui.adapter.NewsAdapter;

/**
 * Created by Admin on 14.02.2015.
 */
public class ImageManager {


    static final int DOWNLOAD_FAILED = -1;
    static final int DOWNLOAD_COMPLETE = 1;


    // Sets the size of the storage that's used to cache images
//    private static final int IMAGE_CACHE_SIZE = 1024 * 1024 * 4;
//    private static final int IMAGE_CACHE_SIZE = 10;

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    /**
     * NOTE: This is the number of total available cores. On current versions of
     * Android, with devices that use plug-and-play cores, this will return less
     * than the total number of cores. The total number of cores is not
     * available in current Android implementations.
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

//    private final LruCache<String, Bitmap> mImageCache;
//    private final HashMap<String, NewsImageView> mPendingImageView;
//    private final HashSet<String> mDownloadingImages;

    // A queue of Runnables for the image download pool
    private final BlockingQueue<Runnable> mDownloadWorkQueue;

    // A queue of PhotoManager tasks. Tasks are handed to a ThreadPool.
    private final Queue<ImageTask> mImageTaskWorkQueue;

    // A managed pool of background download threads
    private final ThreadPoolExecutor mDownloadThreadPool;

    // An object that manages Messages in a Thread
    private Handler mHandler;
    private static NewsAdapter mNewsAdapter;
    private static CategoryAdapter mCategoryAdapter;
    private static HashMap<String, Long> mCategoryUrls;
    // A single instance of PhotoManager, used to implement the singleton pattern

    private static ImageManager sInstance = null;
    private static Notifier mNotifier;

    static {

        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        // Creates a single static instance of PhotoManager
        sInstance = new ImageManager();
    }

    public interface Notifier {
        public void onImageDownloaded(Long id, String url);
    }

    private ImageManager() {

        /*
         * Creates a work queue for the pool of Thread objects used for downloading, using a linked
         * list queue that blocks when the queue is empty.
         */
        mDownloadWorkQueue = new LinkedBlockingQueue<Runnable>();


        /*
         * Creates a work queue for the set of of task objects that control downloading and
         * decoding, using a linked list queue that blocks when the queue is empty.
         */
        mImageTaskWorkQueue = new LinkedBlockingQueue<ImageTask>();

        /*
         * Creates a new pool of Thread objects for the download work queue
         */
        mDownloadThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDownloadWorkQueue);


        // Instantiates a new cache based on the cache size estimate
//        mImageCache = new LruCache<String, Bitmap>(IMAGE_CACHE_SIZE) {
//            @Override
//            protected int sizeOf(String key, Bitmap value) {
////
//                Log.d("Bitmap", "size: " + BitmapUtils.getBitmapSize(value));
//                return BitmapUtils.getBitmapSize(value) / 1024;
//            }
//        };

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mNewsAdapter != null)
                    mNewsAdapter.notifyDataSetChanged();
            }
        };

        mCategoryUrls = new HashMap<String, Long>();
    }

    public static void setNewsAdapter(NewsAdapter newsAdapter) {
        mNewsAdapter = newsAdapter;
    }

    public static void setNotifier(Notifier listener) {
        mNotifier = listener;
    }
    public static void setCategoryAdapter(CategoryAdapter categoryAdapter) {
        mCategoryAdapter = categoryAdapter;
    }
    public static ImageManager getInstance() {
        return sInstance;
    }

//    public LruCache<String, Bitmap> getImageCache() {
//        return mImageCache;
//    }

    public Bitmap getBitmap(String imgUrl) {
        Bitmap bitmap;
//        bitmap = mImageCache.get(imgUrl);
        bitmap = ImageCache.getInstance(App.getContext()).getFromMemCache(imgUrl);
        if (bitmap != null)
            return bitmap;
//        return DiskCache.getInstance(App.getContext()).getBitmap(imgUrl);
        return ImageCache.getInstance(App.getContext()).getFromDiskCache(imgUrl);
    }

    public static void startDownload(NewsItem newsItem) {

        ImageTask imageTask = sInstance.mImageTaskWorkQueue.poll();
        if (null == imageTask) {
            imageTask = new ImageTask();
        }

        imageTask.initializeDownloaderTask(newsItem);

//        if (sInstance.mImageCache.get(newsItem.getImageUrl()) == null) {
        if (!ImageCache.getInstance(App.getContext()).isBitmapSaved(newsItem.getImageUrl())) {
            sInstance.mDownloadThreadPool.execute(imageTask.getDownloadRunnable());
        }

    }

    public void registerCategoryObserver(Long catId, String url) {
        mCategoryUrls.put(url, catId);
    }

    public void handleState(ImageTask imageTask, int state) {

        switch (state) {
            case DOWNLOAD_COMPLETE:
                ImageCache.getInstance(App.getContext()).saveToMemCache(imageTask.getNewsItem().getImageUrl(), imageTask.getBitmap());
                ImageCache.getInstance(App.getContext()).saveToDiskCache(imageTask.getNewsItem().getImageUrl(), imageTask.getBitmap());
                recycleTask(imageTask);
                Message completeMessage = mHandler.obtainMessage(state, imageTask);
                completeMessage.sendToTarget();
                if (mCategoryUrls.containsKey(imageTask.getNewsItem().getImageUrl())) {
                    mNotifier.onImageDownloaded(mCategoryUrls.get(imageTask.getNewsItem().getImageUrl()), imageTask.getNewsItem().getImageUrl());
                    mCategoryUrls.remove(imageTask.getNewsItem().getImageUrl());
                }

                break;
            case DOWNLOAD_FAILED:
                break;
            default:
                break;
        }

    }

    void recycleTask(ImageTask downloadTask) {

        // Frees up memory in the task
        downloadTask.recycle();

        // Puts the task object back into the queue for re-use.
        mImageTaskWorkQueue.offer(downloadTask);
    }
}


