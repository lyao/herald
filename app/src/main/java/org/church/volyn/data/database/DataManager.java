package org.church.volyn.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.church.volyn.Constants;
import org.church.volyn.downloadHelper.DiskCache;
import org.church.volyn.entities.Category;
import org.church.volyn.entities.NewsItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by user on 23.01.2015.
 */
public class DataManager implements DataManagerInterface {

//    private static Context sContext;
    private SQLiteDatabase db;
    private static NewsDao mNewsDao;
    private static CategoryDao mCategoryDao;
    private static GalleryDao mGalleryDao;
    private static DataManager sInstance = null;

    {
        DatabaseOpenHelper dbHelper = DatabaseOpenHelper.getOpenHelper();
        db = dbHelper.getWritableDatabase();
    }

    public static DataManager getInstance() {
        if (sInstance == null) {
            sInstance = new DataManager();
        }
        return sInstance;
    }


    private DataManager() {
//        DatabaseOpenHelper dbHelper = DatabaseOpenHelper.getOpenHelper();
//        db = dbHelper.getWritableDatabase();
        mNewsDao = new NewsDao(db);
        mCategoryDao = new CategoryDao(db);
        mGalleryDao = new GalleryDao(db);
    }

    @Override
    public NewsItem getNews(String url) {
        return mNewsDao.get(url);
    }

    @Override
    public List<NewsItem> getNews(int categoryId) {
        List<NewsItem> newsList;
        if (categoryId == 0) {
            newsList = mNewsDao.getAll();
        } else {
            newsList = mNewsDao.getNewsByCategory(categoryId, 50);
        }
        if (newsList != null){
            for (NewsItem newsItem: newsList) {
                newsItem.setCategory(mCategoryDao.get(newsItem.getCategory().getId()));
            }
        }
        return newsList;
    }

    @Override
    public long saveNews(NewsItem newsItem)
    {
        long newsId = 0L;

        if (newsItem.getCategory() != null) {
            newsId = mNewsDao.save(newsItem);
        }

        return newsId;
    }

    public NewsItem getNewsByGuid(String quid) {
        return mNewsDao.getByGuid(quid);

    }
    public Category findCategoryByName(String catName) {
        return mCategoryDao.find(catName);
    }

    public Category findCategoryByStringId(String stringId) {
        return mCategoryDao.findCategoryByStringId(stringId);
    }
    public List<Category> getAllCategories() {
        return mCategoryDao.getAll();
    }

    public List<Category> getNonEmptyCategories() {
        return mCategoryDao.getNonEmpty();
    }

    public Category getCategoryById(long id) {
        return mCategoryDao.get(id);
    }

    public Map<Long, String> getCategoriesBitmapsFileNames() {
        return mCategoryDao.getCategoriesBitmapsFileNames();
    }

    public void prepareNewsForCleaning() {
        ArrayList<NewsItem> newsList = (ArrayList) mNewsDao.getAll();
        int newsQuantity = newsList.size();
        int quantityForDeleting = newsQuantity - Constants.TOTAL_ITEMS_COUNT;

        for (int i = 0; i < quantityForDeleting; i ++) {
            mNewsDao.updateStatus(newsList.get(newsQuantity - i - 1), Constants.PENDING_DELETING);
        }
    }

    public void cleanNews() {
        ArrayList<NewsItem> newsList = (ArrayList) mNewsDao.getNewsForDeleting();
        for (int i = 0; i < newsList.size(); i ++) {
            if (!TextUtils.isEmpty(newsList.get(i).getImageUrl())) {
                DiskCache.getFile(newsList.get(i).getImageUrl()).delete();
            }
        }
        mNewsDao.deleteByStatus();
    }

    public void saveGallery(NewsItem newsItem) {

        Document doc;
        doc = Jsoup.parse(newsItem.getRawNewsContent());
        Elements images = doc.select("img[data-orig-file]");
        for (int i = 0; i < images.size(); i++) {
            mGalleryDao.saveImage(newsItem.getGuid(), images.get(i).attr("data-orig-file"));
        }
    }

    public List<String> getGallery(String newsGuid) {
        return mGalleryDao.getGallery(newsGuid);
    }
}
