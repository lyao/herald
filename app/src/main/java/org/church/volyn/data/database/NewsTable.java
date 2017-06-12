package org.church.volyn.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by user on 22.01.2015.
 */
public class NewsTable {

    public static final String TABLE_NAME = "news";

    public static class NewsColumns implements BaseColumns {
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String PUB_DATE = "pub_date";
        public static final String IMG_URL = "img_url";
        public static final String NEWS_URL = "news_url";
        public static final String CATEGORY_ID = "category_id";
        public static final String GUID = "guid";
        public static final String STATUS = "status";
        public static final String VIDEO_URL = "video_url";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + NewsTable.TABLE_NAME + " (");
        sb.append(NewsColumns._ID + " INTEGER PRIMARY KEY, ");
        sb.append(NewsColumns.TITLE + " TEXT NOT NULL, ");
        sb.append(NewsColumns.CONTENT + " TEXT NOT NULL,");
        sb.append(NewsColumns.PUB_DATE + " TEXT NOT NULL,");
        sb.append(NewsColumns.IMG_URL + " TEXT NOT NULL,");
        sb.append(NewsColumns.NEWS_URL + " TEXT NOT NULL,");
        sb.append(NewsColumns.VIDEO_URL + " TEXT,");
        sb.append(NewsColumns.CATEGORY_ID + " TEXT NOT NULL,");
        sb.append(NewsColumns.GUID + " TEXT NOT NULL,");
        sb.append(NewsColumns.STATUS + " INTEGER )");
        db.execSQL(sb.toString());

        sb.setLength(0);
        sb.append("CREATE UNIQUE INDEX 'ind' ON " + TABLE_NAME + " ('" + NewsColumns.NEWS_URL + "' ASC)");
        db.execSQL(sb.toString());
    }

    public static void onDelete(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + NewsTable.TABLE_NAME);
    }
}
