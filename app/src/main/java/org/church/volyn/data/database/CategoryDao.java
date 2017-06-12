package org.church.volyn.data.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.volyn.App;
import org.church.volyn.data.database.CategoryTable.CategoryColumns;
import org.church.volyn.downloadHelper.DiskCache;
import org.church.volyn.downloadHelper.ImageCache;
import org.church.volyn.entities.Category;

/**
 * Created by user on 22.01.2015.
 */
public class CategoryDao implements Dao<Category> {

    private static final String INSERT = "INSERT INTO "
            + CategoryTable.TABLE_NAME + "("
            + CategoryColumns.VALIDATION_NAME + ", "
            + CategoryColumns.RESOURCES_ID + ", "
            + CategoryColumns.ORDER
            + ") VALUES (?, ?, ?)";

    private SQLiteDatabase db;
    private SQLiteStatement insertStatment;

    public CategoryDao(SQLiteDatabase db) {
        this.db = db;
        insertStatment = db.compileStatement(INSERT);
    }

    @Override
    public long save(Category newsCategory) {
        insertStatment.clearBindings();
        insertStatment.bindString(1, newsCategory.getValidationName());
        insertStatment.bindString(2, newsCategory.getResourceID());
        insertStatment.bindLong(3, newsCategory.getOrder());
        return insertStatment.executeInsert();
    }

    @Override
    public void update(Category type) {

    }

    @Override
    public Category get(String id) {
        return null;
    }

    @Override
    public Category get(long id) {
        Category category = null;
        String sql =
                String.format("select _id, %s, %s from %s where _id = ?",
                        CategoryColumns.VALIDATION_NAME,
                        CategoryColumns.RESOURCES_ID,
                        CategoryTable.TABLE_NAME);
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            category = new Category();
            category.setId(c.getLong(0));
            category.setValidationName(c.getString(1));
            category.setResourceID(c.getString(2));
        }
        if (!c.isClosed()) {
            c.close();
        }
        return category;
    }

    @Override
    public List<Category> getAll() {
        List<Category> list = new ArrayList<Category>();
        Category category = null;
        String sql =
                String.format("select _id, %s, %s from %s order by %s",
                        CategoryColumns.VALIDATION_NAME,
                        CategoryColumns.RESOURCES_ID,
                        CategoryTable.TABLE_NAME,
                        CategoryColumns.ORDER);

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                category = new Category();
                category.setId(cursor.getLong(0));
                category.setValidationName(cursor.getString(1));
                category.setResourceID(cursor.getString(2));
                list.add(category);
                cursor.moveToNext();
            }
        }
        return list;
    }

    @Override
    public List<Category> getNonEmpty() {
        List<Category> list = new ArrayList<Category>();
        Category category = null;
        String sql = "select distinct cat._id, cat.validation_name, cat.resources_id \n" +
                "from categories as cat inner join news on cat._id = news.category_id order by cat.category_order";


        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                category = new Category();
                category.setId(cursor.getLong(0));
                category.setValidationName(cursor.getString(1));
                category.setResourceID(cursor.getString(2));
                list.add(category);
                cursor.moveToNext();
            }
        }
        return list;
    }

    public Category findCategoryByStringId(String stringId) {
        return findCategory(new String[]{stringId.toUpperCase()});
    }

    public Category find(String name) {
        return findCategory(new String[]{CategoriesAliases.getAlias(name).toUpperCase()});
    }
    public Category findCategory(String[] name) {
        Category category = null;
        String sql =
                String.format("select _id, %s, %s from %s where upper(%s) = upper(?) limit 1",
                        CategoryColumns.VALIDATION_NAME,
                        CategoryColumns.RESOURCES_ID,
                        CategoryTable.TABLE_NAME,
                        CategoryColumns.RESOURCES_ID);
        Cursor c = db.rawQuery(sql, name);
        if (c.moveToFirst()) {
            category = new Category();
            category.setId(c.getLong(0));
            category.setValidationName(c.getString(1));
            category.setResourceID(c.getString(2));
        }
        if (!c.isClosed()) {
            c.close();
        }
        return category;
    }

    public Map<Long, String> getCategoriesBitmapsFileNames() {
        Map<Long, String> hashMap = new HashMap<Long, String>();
        Category category;

        String sql =
                String.format("select news_image.res_id, news.img_url from news join \n" +
                        "(select categories.resources_id as res_id, min(news._id) as news_id from\n" +
                        "categories join news on categories._id = news.category_id\n" +
                        "group by categories.resources_id) as news_image on news._id = news_image.news_id");
        Cursor cursor = db.rawQuery(sql, null);
        String imgFileName;
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                category = findCategoryByStringId(cursor.getString(0));
                imgFileName = DiskCache.getFileName(cursor.getString(1));
                if (imgFileName == null) {
                    imgFileName = getCategoryBitmapFileName(new String[]{String.valueOf(category.getId())});
                }

                hashMap.put((new Long(category.getId())), imgFileName);
                cursor.moveToNext();
            }
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        return hashMap;
    }

    public String getCategoryBitmapFileName(String[] id) {
        String sql =
                String.format("select %s\n" +
                "from news\n" +
                "where category_id = ?",
                        NewsTable.NewsColumns.IMG_URL);
        Cursor cursor = db.rawQuery(sql, id);
        String imgUrl = null;
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                imgUrl = DiskCache.getFileName(cursor.getString(0));

                if (imgUrl != null && ImageCache.getInstance(App.getContext()).sizeOf(imgUrl) != 0) {
                    if (!cursor.isClosed()) {
                        cursor.close();
                    }
                    return imgUrl;
                }
                cursor.moveToNext();
            }
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return imgUrl;
    }
}
