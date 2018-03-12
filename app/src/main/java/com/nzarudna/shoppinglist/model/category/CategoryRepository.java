package com.nzarudna.shoppinglist.model.category;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.os.AsyncTask;

import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Category repository
 */
@Singleton
public class CategoryRepository {

    private CategoryDao mCategoryDao;
    private ProductDao mProductDao;
    private ProductTemplateDao mProductTemplateDao;

    @Inject
    public CategoryRepository(CategoryDao categoryDao, ProductDao productDao,
                              ProductTemplateDao productTemplateDao) {
        mCategoryDao = categoryDao;
        mProductDao = productDao;
        mProductTemplateDao = productTemplateDao;
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

                mProductDao.setDefaultCategoryID(category.getCategoryID());
                mProductTemplateDao.setDefaultCategoryID(category.getCategoryID());

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

    public LiveData<List<Category>> getAvailableCategories() {
        return mCategoryDao.findAllLiveData();
    }
}
