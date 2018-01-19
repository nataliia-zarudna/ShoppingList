package com.nzarudna.shoppinglist.persistence.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.nzarudna.shoppinglist.product.Category;
import com.nzarudna.shoppinglist.product.Product;
import com.nzarudna.shoppinglist.product.ProductTemplate;
import com.nzarudna.shoppinglist.product.ProductsList;
import com.nzarudna.shoppinglist.user.User;
import com.nzarudna.shoppinglist.persistence.CategoryDao;
import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductTemplateDao;
import com.nzarudna.shoppinglist.persistence.ProductsListDao;
import com.nzarudna.shoppinglist.persistence.UserDao;

import java.util.Date;

/**
 * Room application database
 */
@Database(entities = {Product.class, Category.class, ProductTemplate.class, ProductsList.class, User.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG = "AppDatabase";

    private static final String DATABASE_NAME = "shopping_list";

    private static AppDatabase sInstance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {

                            Log.d(LOG, "onCreate initDB start");

                            initDB(db);

                            Log.d(LOG, "onCreate initDB end");
                        }
                    })
                    .build();
        }
        return sInstance;
    }

    private static void initDB(SupportSQLiteDatabase db) {

        int selfUserID = insertSelfUser(db);
        Log.d(LOG, "selfUserID " + selfUserID);

        for (int i = 0; i < 3; i++) {
            ContentValues values = new ContentValues();
            values.put("name", "Shopping list #" + i);
            values.put("created_by", selfUserID);
            values.put("created_at", new Date().getTime());
            values.put("status", ProductsList.STATUS_ACTIVE);

            long productsListsID = db.insert("products_lists", OnConflictStrategy.IGNORE, values);
            Log.d(LOG, "Shopping list #" + i + " id " + productsListsID);
        }
    }

    private static int insertSelfUser(SupportSQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", "");
        return (int) db.insert("users", OnConflictStrategy.IGNORE, contentValues);
    }

    @VisibleForTesting
    public static synchronized AppDatabase switchToInMemory(Context context) {
        sInstance = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        return sInstance;
    }

    public abstract ProductDao productDao();

    public abstract ProductsListDao productsListDao();

    public abstract UserDao userDao();

    public abstract CategoryDao categoryDao();

    public abstract ProductTemplateDao productTemplateDao();
}
