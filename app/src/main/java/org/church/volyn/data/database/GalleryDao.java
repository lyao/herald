package org.church.volyn.data.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import org.church.volyn.entities.Category;
import org.church.volyn.entities.Gallery;
import org.church.volyn.entities.GalleryImage;
import org.church.volyn.entities.NewsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 14.11.2016.
 */

public class GalleryDao {

    private static final String INSERT = "INSERT OR REPLACE INTO "
            + GalleryTable.TABLE_NAME + "("
            + GalleryTable.GalleryColumns.NEWS_GUID + ", "
            + GalleryTable.GalleryColumns.IMAGE_URL
            + ") VALUES (?, ?)";

    private SQLiteDatabase db;
    private SQLiteStatement insertStatment;

    public GalleryDao(SQLiteDatabase db) {
        this.db = db;
        insertStatment = db.compileStatement(INSERT);
    }

    public long saveImage(GalleryImage type) {
        insertStatment.clearBindings();
        insertStatment.bindString(1, type.getNewsId());
        insertStatment.bindString(2, type.getImageUrl());
        return insertStatment.executeInsert();
    }

    public long saveImage(String newsGuid, String imgUrl) {
        insertStatment.clearBindings();
        insertStatment.bindString(1, newsGuid);
        insertStatment.bindString(2, imgUrl);
        return insertStatment.executeInsert();
    }

    public List<String> getGallery(String newsGuid) {

        Category category = null;
        List<String> imageList = new ArrayList<String>();
        String sql =
                String.format("select %s from %s where %s = ?",
                        GalleryTable.GalleryColumns.IMAGE_URL,
                        GalleryTable.TABLE_NAME,
                        GalleryTable.GalleryColumns.NEWS_GUID);
        Cursor c = db.rawQuery(sql, new String[]{newsGuid});
        while (c.moveToNext()) {


            imageList.add(c.getString(0));

        }

        if (!c.isClosed()) {
            c.close();
        }
        return imageList;
    }
}
