package com.nzarudna.shoppinglist.model.persistence.db;

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

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryDao;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitDao;
import com.nzarudna.shoppinglist.model.user.User;
import com.nzarudna.shoppinglist.model.user.UserDao;

import java.util.Date;
import java.util.UUID;

/**
 * Room application database
 */
@Database(entities = {Product.class, Category.class, ProductTemplate.class,
        ProductList.class, User.class, Unit.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG = "AppDatabase";

    private static final String DATABASE_NAME = "shopping_list";

    private static AppDatabase sInstance;

    public static synchronized AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {

                            Log.d(LOG, "onCreate initDB start");

                            db.beginTransaction();
                            try {

                                initDB(db, context);
                                db.setTransactionSuccessful();

                            } finally {
                                db.endTransaction();
                            }

                            Log.d(LOG, "onCreate initDB end");
                        }
                    })
                    .build();
        }
        return sInstance;
    }

    private static void initDB(SupportSQLiteDatabase db, Context context) {

        // For testing

        String selfUserID = insertSelfUser(db);
        Log.d(LOG, "selfUserID " + selfUserID);

        int defaultCategoryID = insertDefaultCatefory(db, context);

        String firstListID = UUID.randomUUID().toString();
        for (int i = 10; i < 40; i++) {
            ContentValues values = new ContentValues();
            values.put("list_id", (i == 10) ? firstListID : UUID.randomUUID().toString());
            values.put("name", "Shopping list #" + i);
            values.put("created_by", selfUserID);
            values.put("created_at", new Date().getTime());
            values.put("status", ProductList.STATUS_ACTIVE);
            values.put("sorting", ProductList.SORT_PRODUCTS_BY_NAME);
            values.put("is_grouped_view", false);

            long productListsID = db.insert("product_lists", OnConflictStrategy.IGNORE, values);
            Log.d(LOG, "Shopping list #" + i + " id " + productListsID);
        }

        String[] categoryIDs = {UUID.randomUUID().toString()
                , UUID.randomUUID().toString(), UUID.randomUUID().toString()};
        for (int i = 0; i < 3; i++) {
            ContentValues values = new ContentValues();
            values.put("category_id", categoryIDs[i]);
            values.put("name", "Category #" + i);

            db.insert("categories", OnConflictStrategy.IGNORE, values);
        }

        for (int i = 0; i < 10; i++) {
            int categoryID = (i < 8) ? (i % 2 + 1) : defaultCategoryID;
            ContentValues values = new ContentValues();
            values.put("product_id", UUID.randomUUID().toString());
            values.put("name", "Product #" + i + " cat " + categoryID);
            values.put("category_id", categoryIDs[i % 2]);
            values.put("list_id", firstListID);
            values.put("count", 0);
            values.put("status", Product.TO_BUY);
            values.put("`order`", 0);

            db.insert("products", OnConflictStrategy.IGNORE, values);
        }

        for (int i = 0; i < 10; i++) {
            ContentValues values = new ContentValues();
            values.put("template_id", UUID.randomUUID().toString());
            values.put("name", ((i % 2 > 0) ? ("Template #" + i) : ("Temp #" + i)));
            values.put("category_id", categoryIDs[i % 2]);

            db.insert("product_templates", OnConflictStrategy.IGNORE, values);
        }

        for (int i = 0; i < 7; i++) {
            ContentValues values = new ContentValues();
            values.put("unit_id", UUID.randomUUID().toString());
            values.put("name", ("Unit #" + i));

            db.insert("units", OnConflictStrategy.IGNORE, values);
        }
    }

    private static String insertSelfUser(SupportSQLiteDatabase db) {
        String selfUserID = UUID.randomUUID().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", selfUserID.toString());
        contentValues.put("name", "");
        db.insert("users", OnConflictStrategy.IGNORE, contentValues);

        ShoppingListApplication.getAppComponent().getSharedPreferences()
                .edit()
                .putString("selfUserID", selfUserID)
                .commit();

        return selfUserID;
    }

    private static int insertDefaultCatefory(SupportSQLiteDatabase db, Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("category_id", Category.DEFAULT_CATEGORY_ID.toString());
        contentValues.put("name", context.getString(R.string.default_category_name));

        return (int) db.insert("categories", OnConflictStrategy.IGNORE, contentValues);
    }

    @VisibleForTesting
    public static synchronized AppDatabase switchToInMemory(Context context) {
        sInstance = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();

        insertDefaultCatefory(sInstance.mDatabase, context);

        return sInstance;
    }

    public abstract ProductDao productDao();

    public abstract ProductListDao productListDao();

    public abstract UserDao userDao();

    public abstract CategoryDao categoryDao();

    public abstract ProductTemplateDao productTemplateDao();

    public abstract UnitDao unitDao();
}
