package org.church.volyn.data.database;

/**
 * Created by user on 23.01.2015.
 */
public class DatabaseManager
//        implements DataManagerInterface
{

////    private static Context sContext;
//    private SQLiteDatabase db;
//    private static NewsDao mNewsDao;
//    private static CategoryDao mCategoryDao;
//    private static DatabaseManager sInstance = null;
//
//    public static DatabaseManager getInstance() {
//        if (sInstance == null) {
//            sInstance = new DatabaseManager();
//        }
//        return sInstance;
//    }
//
//
//    private DatabaseManager() {
////        sContext = App.getContext();
//        DatabaseOpenHelper dbHelper = DatabaseOpenHelper.getOpenHelper();
//        db = dbHelper.getWritableDatabase();
//        mNewsDao = new NewsDao(db);
//        mCategoryDao = new CategoryDao(db);
//    }
//
//    @Override
//    public NewsItem getNews(String url) {
//        return null;
//    }
//
//    @Override
//    public List<NewsItem> getNews() {
//        List<NewsItem> newsList = mNewsDao.getAll();
//        if (newsList != null){
//            for (NewsItem newsItem: newsList) {
//                newsItem.setCategory(mCategoryDao.get(newsItem.getCategory().getId()));
//            }
//        }
//        return newsList;
//    }
//
//    @Override
//    public long saveNews(NewsItem newsItem)
//    {
//        long newsId = 0L;
//        if (newsItem.getCategory() != null) {
//            newsId = mNewsDao.save(newsItem);
//        }
//        return newsId;
//    }
//
//    public Category findCategoryByName(String catName) {
//        return mCategoryDao.find(catName);
//    }
//
//    public List<Category> getAll() {
//        return mCategoryDao.getAll();
//    }

}
