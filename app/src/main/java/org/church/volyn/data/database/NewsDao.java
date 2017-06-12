package org.church.volyn.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import org.church.volyn.Constants;
import org.church.volyn.entities.Category;
import org.church.volyn.entities.NewsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 22.01.2015.
 */
public class NewsDao implements Dao<NewsItem> {

    private static final String INSERT = "INSERT OR REPLACE INTO " + NewsTable.TABLE_NAME + "(" +
            NewsTable.NewsColumns.TITLE + ", " + NewsTable.NewsColumns.CONTENT + ", " + NewsTable.NewsColumns.PUB_DATE + ", " +
            NewsTable.NewsColumns.IMG_URL + ", " + NewsTable.NewsColumns.NEWS_URL + ", " + NewsTable.NewsColumns.CATEGORY_ID + ", " +
            NewsTable.NewsColumns.GUID + ", " + NewsTable.NewsColumns.STATUS + ", " +
            NewsTable.NewsColumns.VIDEO_URL + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE " + NewsTable.TABLE_NAME +
            "SET " + NewsTable.NewsColumns.STATUS;
    private static final String DELETE = "";

    private SQLiteDatabase db;
    private SQLiteStatement insertStatment;

    public NewsDao(SQLiteDatabase db) {
        this.db = db;
        insertStatment = db.compileStatement(INSERT);
    }

    @Override
    public long save(NewsItem newsItem) {
        String imgUrl = "";
        insertStatment.clearBindings();
        insertStatment.bindString(1, newsItem.getTitle());
        insertStatment.bindString(2, newsItem.getNewsContent());
        insertStatment.bindLong(3, newsItem.getPubDate());
        if (newsItem.getImageUrl() != null) {
            imgUrl = newsItem.getImageUrl();
        }
        insertStatment.bindString(4, imgUrl);
        insertStatment.bindString(5, newsItem.getNewsLink());
        insertStatment.bindLong(6, newsItem.getCategory().getId());
        insertStatment.bindString(7, newsItem.getGuid());
        insertStatment.bindLong(8, newsItem.getStatus());
        insertStatment.bindString(9, newsItem.getVideoUrl());
        return insertStatment.executeInsert();
    }


    public void updateStatus(NewsItem entity, int status) {
        final ContentValues values = new ContentValues();
        values.put(NewsTable.NewsColumns.STATUS, status);
        db.update(NewsTable.TABLE_NAME, values, NewsTable.NewsColumns.GUID +  " = ?",
                new String[] {String.valueOf(entity.getGuid())});
    }

    @Override
    public void update(NewsItem entity) {
    }

    public void deleteByStatus() {
        db.delete(NewsTable.TABLE_NAME, NewsTable.NewsColumns.STATUS + "= ?",
                new String[]{String.valueOf(Constants.PENDING_DELETING)});

    }

    public List<NewsItem> getNewsForDeleting() {
        List<NewsItem> newsList = new ArrayList<NewsItem>();
        String sql =
                String.format("select _id, %s, %s, %s, %s, %s, %s, %s, %s from %s where status = %d order by guid ",
                        NewsTable.NewsColumns.TITLE,
                        NewsTable.NewsColumns.CONTENT,
                        NewsTable.NewsColumns.NEWS_URL,
                        NewsTable.NewsColumns.IMG_URL,
                        NewsTable.NewsColumns.CATEGORY_ID,
                        NewsTable.NewsColumns.PUB_DATE,
                        NewsTable.NewsColumns.GUID,
                        NewsTable.NewsColumns.STATUS,
                        NewsTable.TABLE_NAME,
                        Constants.PENDING_DELETING);
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            NewsItem newsItem = buildNewsItemFromCursor(c);
            if (newsItem != null) {
                newsList.add(newsItem);
            }
        }

        if (!c.isClosed()) {
            c.close();
        }
        return newsList;
    }
    @Override
    public NewsItem get(String id) {
        String sql =
                String.format("select _id, %s, %s, %s, %s, %s, %s, %s, %s from %s where news_url = ?",
                        NewsTable.NewsColumns.TITLE,
                        NewsTable.NewsColumns.CONTENT,
                        NewsTable.NewsColumns.NEWS_URL,
                        NewsTable.NewsColumns.IMG_URL,
                        NewsTable.NewsColumns.CATEGORY_ID,
                        NewsTable.NewsColumns.PUB_DATE,
                        NewsTable.NewsColumns.GUID,
                        NewsTable.NewsColumns.VIDEO_URL,
                        NewsTable.TABLE_NAME);
        Cursor c = db.rawQuery(sql, new String[]{id});
        NewsItem newsItem = null;
        if (c.moveToNext()) {
            newsItem = buildNewsItemFromCursor(c);
        }

        if (!c.isClosed()) {
            c.close();
        }
        return newsItem;
    }

    @Override
    public NewsItem get(long id) {
        return null;
    }

    @Override
    public List<NewsItem> getAll() {
        List<NewsItem> newsList = new ArrayList<NewsItem>();
        String sql =
                String.format("select _id, %s, %s, %s, %s, %s, %s, %s, %s from %s order by guid desc",
                        NewsTable.NewsColumns.TITLE,
                        NewsTable.NewsColumns.CONTENT,
                        NewsTable.NewsColumns.NEWS_URL,
                        NewsTable.NewsColumns.IMG_URL,
                        NewsTable.NewsColumns.CATEGORY_ID,
                        NewsTable.NewsColumns.PUB_DATE,
                        NewsTable.NewsColumns.GUID,
                        NewsTable.NewsColumns.VIDEO_URL,
                        NewsTable.TABLE_NAME);
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            NewsItem newsItem = buildNewsItemFromCursor(c);
            if (newsItem != null) {
                newsList.add(newsItem);
            }
        }

        if (!c.isClosed()) {
            c.close();
        }
        return newsList;
    }

    @Override
    public List<NewsItem> getNonEmpty() {
        return null;
    }

    public NewsItem getByGuid(String guid) {
        String sql =
                String.format("select _id, %s, %s, %s, %s, %s, %s, %s, %s from %s where %s = ?",
                        NewsTable.NewsColumns.TITLE,
                        NewsTable.NewsColumns.CONTENT,
                        NewsTable.NewsColumns.NEWS_URL,
                        NewsTable.NewsColumns.IMG_URL,
                        NewsTable.NewsColumns.CATEGORY_ID,
                        NewsTable.NewsColumns.PUB_DATE,
                        NewsTable.NewsColumns.GUID,
                        NewsTable.NewsColumns.VIDEO_URL,
                        NewsTable.TABLE_NAME,
                        NewsTable.NewsColumns.GUID);
        Cursor c = db.rawQuery(sql, new String[]{guid});
        NewsItem newsItem = null;
        if (c.moveToNext()) {
            newsItem = buildNewsItemFromCursor(c);
        }

        if (!c.isClosed()) {
            c.close();
        }
        return newsItem;
    }

    public List<NewsItem> getNewsByCategory(int categoryId, int quantity) {
        Category category = null;
        List<NewsItem> newsList = new ArrayList<NewsItem>();
        String sql =
                String.format("select _id, %s, %s, %s, %s, %s, %s, %s, %s from %s where category_id = ? order by guid desc limit %d",
                        NewsTable.NewsColumns.TITLE,
                        NewsTable.NewsColumns.CONTENT,
                        NewsTable.NewsColumns.NEWS_URL,
                        NewsTable.NewsColumns.IMG_URL,
                        NewsTable.NewsColumns.CATEGORY_ID,
                        NewsTable.NewsColumns.PUB_DATE,
                        NewsTable.NewsColumns.GUID,
                        NewsTable.NewsColumns.VIDEO_URL,
                        NewsTable.TABLE_NAME,
                        quantity);
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(categoryId)});
        while (c.moveToNext()) {
            NewsItem newsItem = buildNewsItemFromCursor(c);
            if (newsItem != null) {
                newsList.add(newsItem);
            }
        }

        if (!c.isClosed()) {
            c.close();
        }
        return newsList;
    }

    private NewsItem buildNewsItemFromCursor(Cursor c) {
        NewsItem newsItem = null;
        String imgUrl;
        if (c != null) {
            newsItem = new NewsItem();
            newsItem.setTitle(c.getString(1));
            newsItem.setNewsContent(c.getString(2));
            newsItem.setNewsLink(c.getString(3));
            imgUrl = c.getString(4);
            if (imgUrl.isEmpty()) {
                imgUrl = null;
            }
            newsItem.setImageUrl(imgUrl);
            newsItem.setCategory(new Category().setId(c.getLong(5)));
            newsItem.setPubDate(c.getLong(6));
            newsItem.setGuid(c.getString(7));
            newsItem.setVideoUrl(c.getString(8));
        }
        return newsItem;
    }
}
