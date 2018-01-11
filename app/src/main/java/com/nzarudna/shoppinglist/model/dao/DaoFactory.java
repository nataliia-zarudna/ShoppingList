package com.nzarudna.shoppinglist.model.dao;

import android.content.Context;

import com.nzarudna.shoppinglist.model.db.AppDatabase;

/**
 * Factory for DAO classes
 */

public class DaoFactory {

    private static DaoFactory instance;

    public static synchronized DaoFactory getInstance() {
        if (instance == null) {
            instance = new DaoFactory();
        }
        return instance;
    }

    private DaoFactory() {}

    public CategoryDao getCategoryDao(Context context) {
        return AppDatabase.getInstance(context).categoryDao();
    }

    public ProductDao getProductDao(Context context) {
        return AppDatabase.getInstance(context).productDao();
    }

    public ProductsListDao getProductsListDao(Context context) {
        return AppDatabase.getInstance(context).productsListDao();
    }

    public UserDao getUserDao(Context context) {
        return AppDatabase.getInstance(context).userDao();
    }

    public ProductTemplateDao getProductTemplateDao(Context context) {
        return AppDatabase.getInstance(context).productTemplateDao();
    }
}
