package com.nzarudna.shoppinglist.model.category;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.ListenedAsyncTask;
import com.nzarudna.shoppinglist.model.ModelUtils;
import com.nzarudna.shoppinglist.model.exception.NameIsEmptyException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;

import java.util.List;
import java.util.UUID;

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

    private static class CreateUpdateAsyncTask extends ListenedAsyncTask<Category, Category> {

        CategoryDao mCategoryDao;
        boolean mIsCreate;

        CreateUpdateAsyncTask(CategoryDao categoryDao, @Nullable AsyncResultListener<Category> listener, boolean isCreate) {
            super(listener);
            mCategoryDao = categoryDao;
            mIsCreate = isCreate;
        }

        @Override
        protected Category doInBackground(Category... categories) {
            try {

                Category category = categories[0];

                validateName(mCategoryDao, category.getName());

                if (mIsCreate) {
                    mCategoryDao.insert(category);
                } else {
                    mCategoryDao.update(category);
                }
                return category;
            } catch (NameIsEmptyException | UniqueNameConstraintException e) {
                mResultException = e;
                return null;
            }
        }
    }

    public void create(final Category category, @Nullable AsyncResultListener<Category> listener) {
        new CreateUpdateAsyncTask(mCategoryDao, listener, true).execute(category);
    }

    public void update(final Category category, @Nullable AsyncResultListener<Category> listener) {
        new CreateUpdateAsyncTask(mCategoryDao, listener, false).execute(category);
    }

    private static void validateName(CategoryDao categoryDao, String name) throws NameIsEmptyException, UniqueNameConstraintException {
        ModelUtils.validateNameIsNotEmpty(name);

        UUID duplicateCategoryID = categoryDao.findBySimilarName(name);
        if (duplicateCategoryID != null) {
            UniqueNameConstraintException e = new UniqueNameConstraintException("Category with name '" + name + "' already exists");
            e.setDuplicateEntityID(duplicateCategoryID);
            throw e;
        }
    }

    private static class RemoveAsyncTask extends AsyncTask<Category, Void, Void> {

        CategoryDao mCategoryDao;
        ProductDao mProductDao;
        ProductTemplateDao mProductTemplateDao;

        RemoveAsyncTask(CategoryDao categoryDao, ProductDao productDao, ProductTemplateDao productTemplateDao) {
            this.mCategoryDao = categoryDao;
            this.mProductDao = productDao;
            this.mProductTemplateDao = productTemplateDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            Category category = categories[0];

            mProductDao.setDefaultCategoryID(category.getCategoryID());
            mProductTemplateDao.setDefaultCategoryID(category.getCategoryID());

            mCategoryDao.delete(category);

            return null;
        }
    }

    public void remove(Category category) {
        new RemoveAsyncTask(mCategoryDao, mProductDao, mProductTemplateDao).execute(category);
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
