package org.church.volyn.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.church.volyn.App;
import org.church.volyn.R;
import org.church.volyn.downloadHelper.DiskCache;
import org.church.volyn.entities.CategoriesTitles;
import org.church.volyn.entities.Category;



/**
 * Created by user on 23.01.2015.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "herald";
    private static Context sContext;
    private static DatabaseOpenHelper sOpenHelper;

    public static synchronized DatabaseOpenHelper getOpenHelper() {

        if (sOpenHelper == null) {
            sContext = App.getContext();
            sOpenHelper = new DatabaseOpenHelper(sContext);
        }
        return sOpenHelper;
    }

//    public static void init(Context context) {
//        sContext = context;
//    }

    private DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        deleteTables(sqLiteDatabase);
        DiskCache.getInstance(App.getContext()).clear();
        createTables(sqLiteDatabase);
    }

    private void deleteTables(SQLiteDatabase sqLiteDatabase) {
        NewsTable.onDelete(sqLiteDatabase);
        CategoryTable.onDelete(sqLiteDatabase);
        GalleryTable.onDelete(sqLiteDatabase);
    }

    private void createTables(SQLiteDatabase sqLiteDatabase) {
        NewsTable.onCreate(sqLiteDatabase);
        CategoryTable.onCreate(sqLiteDatabase);
        GalleryTable.onCreate(sqLiteDatabase);
        CategoryDao categoriesDao = new CategoryDao(sqLiteDatabase);
        String[] categories = sContext.getResources().getStringArray(R.array.categories);
        for (String cat:  categories) {
            long i = categoriesDao.save(new Category(cat.split(":")[0], cat.split(":")[1], CategoriesTitles.getDirectoryOrder(cat.split(":")[0])));
        }
    }
}
