package com.nzarudna.shoppinglist.model.category;

import android.arch.paging.DataSource;
import android.os.AsyncTask;

import javax.inject.Inject;

/**
 * Category repository
 */

public class CategoryRepository {

    private CategoryDao mCategoryDao;

    @Inject
    public CategoryRepository(CategoryDao categoryDao) {
        mCategoryDao = categoryDao;
    }

    public void create(final Category category) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mCategoryDao.insert(category);
                return null;
            }
        }.execute();
    }

    public void update(final Category category) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mCategoryDao.update(category);
                return null;
            }
        }.execute();
    }

    public void remove(final Category category) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mCategoryDao.delete(category);
                return null;
            }
        }.execute();
    }

    public DataSource.Factory<Integer, Category> getAllCategories() {
        return mCategoryDao.findAll();
    }

    public DataSource.Factory<Integer, CategoryStatisticsItem> getCategoriesWithStatistics() {
        return mCategoryDao.findAllWithStatistics();
    }
}
