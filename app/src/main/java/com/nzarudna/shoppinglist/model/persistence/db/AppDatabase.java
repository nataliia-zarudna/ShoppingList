package com.nzarudna.shoppinglist.model.persistence.db;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.google.gson.stream.JsonReader;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

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

                                UUID selfUserID = insertSelfUser(db);

                                initDB(db, context, R.raw.init_db, selfUserID);
                                initDB(db, context, R.raw.test_db_data, selfUserID);
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

    private static void initDB(SupportSQLiteDatabase db, Context context, @RawRes int dataResID, UUID selfUserID) {

        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        JsonReader jsonReader = null;
        try {

            inputStream = context.getResources().openRawResource(dataResID);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            jsonReader = new JsonReader(bufferedReader);

            jsonReader.beginObject();
            while(jsonReader.hasNext()) {

                String tableName = jsonReader.nextName();
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {

                    ContentValues contentValues = new ContentValues();

                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {

                        String columnName = jsonReader.nextName();
                        String value = jsonReader.nextString();

                        if (columnName.equals("name")) {
                            value = getStringResByName(value, context);
                        } else if (columnName.equals("created_at")) {
                            Date date = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").parse(value);
                            value = String.valueOf(date.getTime());
                        } else if (columnName.equals("created_by")) {
                            value = selfUserID.toString();
                        }
                        contentValues.put(columnName, value);

                    }
                    jsonReader.endObject();

                    db.insert(tableName, OnConflictStrategy.IGNORE, contentValues);
                }
                jsonReader.endArray();
            }
            jsonReader.endObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                if (jsonReader != null) {
                    jsonReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getStringResByName(String stringResName, Context context) {

        String packageName = context.getPackageName();
        int stringIdentifier = context.getResources().getIdentifier(stringResName, "string", packageName);
        return stringIdentifier != 0 ? context.getString(stringIdentifier) : stringResName;
    }

    private static UUID insertSelfUser(SupportSQLiteDatabase db) {
        UUID selfUserID = UUID.randomUUID();

        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", selfUserID.toString());
        contentValues.put("name", "");
        db.insert("users", OnConflictStrategy.IGNORE, contentValues);

        ShoppingListApplication.getAppComponent().getSharedPreferences()
                .edit()
                .putString("selfUserID", selfUserID.toString())
                .commit();

        return selfUserID;
    }

    private static int insertDefaultCatefory(SupportSQLiteDatabase db, Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("category_id", Category.DEFAULT_CATEGORY_ID.toString());
        contentValues.put("name", context.getString(R.string.default_category_name));

        return (int) db.insert("categories", OnConflictStrategy.IGNORE, contentValues);
    }

    public abstract ProductDao productDao();

    public abstract ProductListDao productListDao();

    public abstract UserDao userDao();

    public abstract CategoryDao categoryDao();

    public abstract ProductTemplateDao productTemplateDao();

    public abstract UnitDao unitDao();
}
