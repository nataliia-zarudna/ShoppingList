package com.nzarudna.shoppinglist.model.persistence;

import android.content.Context;

import com.nzarudna.shoppinglist.model.category.CategoryDao;
import com.nzarudna.shoppinglist.model.persistence.db.AppDatabase;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.product.list.ProductListDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;
import com.nzarudna.shoppinglist.model.unit.UnitDao;
import com.nzarudna.shoppinglist.model.user.UserDao;

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
    public ProductListDao provideProductListDao(Context context) {
        return mDatabase.productListDao();
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

    @Singleton
    @Provides
    public UnitDao provideUnitDao(Context context) {
        return AppDatabase.getInstance(context).unitDao();
    }
}
