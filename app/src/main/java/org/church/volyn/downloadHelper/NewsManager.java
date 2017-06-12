package org.church.volyn.downloadHelper;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.church.volyn.data.NewsCache;
import org.church.volyn.data.database.DataManager;
import org.church.volyn.data.database.DatabaseManager;
import org.church.volyn.entities.Category;
import org.church.volyn.entities.NewsItem;

/**
 * Created by user on 14.11.2014.
 */
public class NewsManager {

    static final int NEWS_DOWNLOAD_FAILED = -1;
    static final int NEWS_DOWNLOAD_STARTED = 1;
    static final int NEWS_DOWNLOAD_COMPLETE = 2;
    static final int CATEGORY_DOWNLOAD_COMPLETE = 3;
    static final int CATEGORY_IMG_DOWNLOAD_COMPLETE = 4;
    static final int IMAGE_DOWNLOAD_STARTED = 3;
    static final int IMAGE_DOWNLOAD_COMPLETE = 4;
    static final int TASK_COMPLETE = 5;

    private static DatabaseManager mDatabaseManager;
    private Handler mHandler;

    // A single instance of PhotoManager, used to implement the singleton pattern
    private static NewsManager sInstance = null;
    private static NewsCache mNewsCache;
    private static HashMap<Long, String> mCategoriesImages;
    private List<Category> mCategories;
    private boolean needUseCatCache;
    private Context mContext;
    static {
        // Creates a single static instance of PhotoManager
        sInstance = new NewsManager();
    }

    public interface NewsManagerStateNotifier {
        public void onItemAdded(NewsItem newsItem);
        public void onDatasetIsNotEmpty();
    }

    public interface NewsManagerCategoriesNotifier {

        public void onCategoriesAdded(Category category);
        public void onBitmapLinkAdded(Long catId, String url);
    }

    private NewsManagerStateNotifier mNewsManagerStateNotifier;
    private NewsManagerCategoriesNotifier mNewsManagerCategoriesNotifier;

    public void setNeedUseCategoriesCache(boolean b) {
        needUseCatCache = b;
    }

    public static NewsManager getInstance() {
        return sInstance;
    }

    private NewsManager(){
        mNewsCache = new NewsCache();
        mCategoriesImages = new HashMap<Long, String>();
        mCategories = new ArrayList<Category>();
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case NEWS_DOWNLOAD_COMPLETE:
                            if (mNewsManagerStateNotifier != null){
                                NewsItem ni = (NewsItem)msg.obj;
                                mNewsManagerStateNotifier.onItemAdded(ni);
                            }
                            break;
                        case  CATEGORY_DOWNLOAD_COMPLETE:
                            if (mNewsManagerCategoriesNotifier != null && needUseCatCache) {
                                Category category = (Category) msg.obj;
                                mNewsManagerCategoriesNotifier.onCategoriesAdded(category);
                            }
                            break;
                        case CATEGORY_IMG_DOWNLOAD_COMPLETE:
                            if (mNewsManagerCategoriesNotifier != null) {
                                Bundle b = (Bundle)msg.obj;
                                if (b != null) {
                                    mNewsManagerCategoriesNotifier.onBitmapLinkAdded(b.getLong("catId"), b.getString("url"));
                                }
                            }
                            break;
                        default:
                            super.handleMessage(msg);
                    }
                }
  //          }
        };
    }

    public void setOnAddNewsItemListener(NewsManagerStateNotifier listener) {
        mNewsManagerStateNotifier = listener;
    }

    public void setOnAddCategoryListener(NewsManagerCategoriesNotifier listener) {
        mNewsManagerCategoriesNotifier = listener;
    }

    public void addNewsItemToCache(NewsItem newsItem){
        mNewsCache.add(newsItem);
        putCategoryToCache(newsItem);
        Message newsMessage = mHandler.obtainMessage(NEWS_DOWNLOAD_COMPLETE, newsItem);
        newsMessage.sendToTarget();
    }

    public NewsItem getNewsItemFromCache(String url) {
        NewsItem ni = null;
        if (mNewsCache != null) {
            ni = mNewsCache.get(url);

            if (ni == null) {
                ni = DataManager.getInstance().getNews(url);
                if (ni != null) {
                    mNewsCache.add(ni);
                }
            }
        }
        return ni;
    }

    public List<NewsItem> getNewsItemsFromCache() {
        List<NewsItem> list = DataManager.getInstance().getNews(0);
        mNewsCache.clear();
        mNewsCache.addAll(list);
        return list;
    }

    public HashMap<Long, String> getCategoriesBitmapFileNames() {
        return mCategoriesImages;
    }

    public List<Category> getCategories() {
        return mCategories;
    }

    private void putCategoryToCache(NewsItem newsItem) {
        if (newsItem == null || newsItem.getCategory() == null ||
                newsItem.getImageUrl() == null)
                //||              ImageCache.getInstance(App.getContext()).sizeOf(newsItem.getImageUrl()) == 0)
            return;
        Category cat = newsItem.getCategory();
        long catId = cat.getId();

        if (!mCategoriesImages.containsKey(catId))
        //        &&            ImageCache.getInstance(App.getContext()).getFromMemCache(newsItem.getImageUrl()) != null)
        {
            mCategoriesImages.put(catId, newsItem.getImageUrl());
            Bundle bundle = new Bundle();
            bundle.putLong("catId", catId);
            bundle.putString("url", newsItem.getImageUrl());
            Message catMessage = mHandler.obtainMessage(CATEGORY_IMG_DOWNLOAD_COMPLETE, bundle);
            catMessage.sendToTarget();
        }

        if (!mCategories.contains(cat)) {

            mCategories.add(cat);
            Message catMessage = mHandler.obtainMessage(CATEGORY_DOWNLOAD_COMPLETE, cat);
            catMessage.sendToTarget();

        } else {

        }
    }

    public void onDatasetIsNotEmpty() {
        if (mNewsManagerStateNotifier != null){
            mNewsManagerStateNotifier.onDatasetIsNotEmpty();
        }
    }
    private int indexOf(NewsItem newsItem){
        return mNewsCache.indexOf(newsItem);
    }

}
