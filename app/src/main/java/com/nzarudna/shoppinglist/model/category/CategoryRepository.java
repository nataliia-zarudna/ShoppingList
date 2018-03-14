package com.nzarudna.shoppinglist.model.category;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.ShoppingListException;
import com.nzarudna.shoppinglist.model.UniqueNameConstraintException;
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

    public void create(final Category category, final @Nullable AsyncResultListener listener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    mCategoryDao.insert(category);

                    if (listener != null) {
                        listener.onSuccess();
                    }
                } catch (SQLiteConstraintException e) {
                    //throw new UniqueNameConstraintException("Category with name " + category.getName() + " already excists", e);
                    if (listener != null) {
                        listener.onError(new UniqueNameConstraintException("Category with name " + category.getName() + " already excists", e));
                    }
                }
                return null;
            }
        }.execute();
    }

    public void update(final Category category, final @Nullable AsyncResultListener listener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    mCategoryDao.update(category);

                    if (listener != null) {
                        listener.onSuccess();
                    }
                } catch (SQLiteConstraintException e) {
                    //throw new UniqueNameConstraintException("Category with name " + category.getName() + " already excists", e);
                    if (listener != null) {
                        listener.onError(new UniqueNameConstraintException("Category with name " + category.getName() + " already excists", e));
                    }
                }
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
