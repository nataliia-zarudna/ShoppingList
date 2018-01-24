package com.nzarudna.shoppinglist.persistence;

import android.content.Context;

import com.nzarudna.shoppinglist.persistence.db.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provides Room Dao implementations
 */
@Module
public class RoomDaoModule {

    private AppDatabase mDatabase;

    public RoomDaoModule(AppDatabase database) {
        mDatabase = database;
    }

    @Singleton
    @Provides
    public CategoryDao provideCategoryDao(Context context) {
        return AppDatabase.getInstance(context).categoryDao();
    }

    @Singleton
    @Provides
    public ProductDao provideProductDao(Context context) {
        return AppDatabase.getInstance(context).productDao();
    }

    @Singleton
    @Provides
    public ProductListDao provideProductsListDao(Context context) {
        return mDatabase.productsListDao();
    }

    @Singleton
    @Provides
    public UserDao provideUserDao(Context context) {
        return AppDatabase.getInstance(context).userDao();
    }

    @Singleton
    @Provides
    public ProductTemplateDao provideProductTemplateDao(Context context) {
        return AppDatabase.getInstance(context).productTemplateDao();
    }

}
