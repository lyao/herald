package org.church.volyn.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by user on 22.01.2015.
 */
public class CategoryTable {

    public static final String TABLE_NAME = "categories";

    public static class CategoryColumns implements BaseColumns {
        public static final String VALIDATION_NAME = "validation_name";
        public static final String RESOURCES_ID = "resources_id";
        public static final String ORDER = "category_order";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + CategoryTable.TABLE_NAME + " (");
        sb.append(CategoryColumns._ID + " INTEGER PRIMARY KEY, ");
        sb.append(CategoryColumns.VALIDATION_NAME + " TEXT NOT NULL, ");
        sb.append(CategoryColumns.RESOURCES_ID + " TEXT NOT NULL, ");
        sb.append(CategoryColumns.ORDER + " INTEGER )");
        db.execSQL(sb.toString());
    }

    public static void onDelete(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoryTable.TABLE_NAME);
    }
}
