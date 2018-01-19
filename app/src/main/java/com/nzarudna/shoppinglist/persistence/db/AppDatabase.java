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

/**
 * Room application database
 */
@Database(entities = {Product.class, Category.class, ProductTemplate.class, ProductsList.class, User.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "shopping_list";

    private static AppDatabase sInstance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);

                            initDB(db);
                        }
                    })
                    .build();
        }
        return sInstance;
    }

    private static void initDB(SupportSQLiteDatabase db) {

        db.beginTransaction();
        insertSelfUser(db);

        for (int i = 0; i < 3; i++) {
            ContentValues values = new ContentValues();
            values.put("name", "Shopping list #" + i);
            db.insert("products_lists", OnConflictStrategy.REPLACE, values);
        }
        db.endTransaction();
    }

    private static void insertSelfUser(SupportSQLiteDatabase db) {
        db.insert("users", OnConflictStrategy.REPLACE, new ContentValues());
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
