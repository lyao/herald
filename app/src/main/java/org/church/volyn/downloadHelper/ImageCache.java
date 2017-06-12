package org.church.volyn.downloadHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import org.church.volyn.utils.BitmapUtils;

/**
 * Created by user on 20.03.2015.
 */
public class ImageCache {
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 5; // 5MB
    private Context mContext;
    private LruCache<String, Bitmap> mMemoryImageCache;
    private DiskCache mDiskImageCache;
    private static ImageCache sInstance;


    public static ImageCache getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ImageCache(context);
        }
        return sInstance;
    }

    private ImageCache(Context context){
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mMemoryImageCache = new LruCache<String, Bitmap>(DEFAULT_MEM_CACHE_SIZE){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return BitmapUtils.getBitmapSize(value)/1024;

            }
        };

        mDiskImageCache = DiskCache.getInstance(context);
    }

    public void saveToMemCache(String url, Bitmap bitmap) {
        if (url == null || bitmap == null) return;
        mMemoryImageCache.put(url, bitmap);
    }

    public Bitmap getFromMemCache(String url){
        return mMemoryImageCache.get(url);
    }

    public void saveToDiskCache(String url, Bitmap bitmap) {
        mDiskImageCache.saveBitmap(url, bitmap);
    }

    public Bitmap getFromDiskCache(String url){
        return mDiskImageCache.getBitmap(url);
    }

    public boolean isBitmapSaved(String url) {
        boolean result = true;
        if ((mMemoryImageCache.get(url) == null) && (!mDiskImageCache.bitmapIsSaved(url))){
            result = false;
        }
        return result;
    }

    public boolean isBitmapSavedToDisk(String url) {
        return mDiskImageCache.bitmapIsSaved(url);
    }

    public long sizeOf(String url) {
        return mDiskImageCache.sizeOfFile(url);
    }
//    public void saveToCache(String url, Bitmap bitmap) {
//
//    }
//    private static RetainFragment findOrCreateRetainFragment(FragmentManager fm {})

//    public static class RetainFragment extends Fragment {
//        private Object mObject;
//
//        public RetainFragment() {}
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//
//            setRetainInstance(true);
//        }
//
//        public void setObject(Object object) {
//            mObject = object;
//        }
//
//        public Object getObject() {
//            return mObject;
//        }
//    }
}
