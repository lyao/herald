package org.church.volyn.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by user on 22.01.2015.
 */
public class GalleryTable {

    public static final String TABLE_NAME = "galleries";

    public static class GalleryColumns implements BaseColumns {
        public static final String NEWS_GUID = "news_guid";
        public static final String IMAGE_URL = "image_url";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + GalleryTable.TABLE_NAME + " (");
        sb.append(GalleryColumns._ID + " INTEGER PRIMARY KEY, ");
        sb.append(GalleryColumns.NEWS_GUID + " TEXT NOT NULL, ");
        sb.append(GalleryColumns.IMAGE_URL + " TEXT NOT NULL )");
        db.execSQL(sb.toString());
        StringBuilder sb1 = new StringBuilder();
        sb1.append("CREATE UNIQUE INDEX t1b ON " + GalleryTable.TABLE_NAME + "("+ GalleryColumns.NEWS_GUID + ", " + GalleryColumns.IMAGE_URL +")");
        db.execSQL(sb1.toString());
    }

    public static void onDelete(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + GalleryTable.TABLE_NAME);
    }
}
