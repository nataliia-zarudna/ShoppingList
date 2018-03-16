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

    static class CreateUpdateAsyncTask extends ListenedAsyncTask<Category, Category> {

        @Inject
        CategoryDao mCategoryDao;
        boolean mIsCreate;

        CreateUpdateAsyncTask(@Nullable AsyncResultListener<Category> listener, boolean isCreate) {
            super(listener);
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
        new CreateUpdateAsyncTask(listener, true).execute(category);
    }

    public void update(final Category category, @Nullable AsyncResultListener<Category> listener) {
        new CreateUpdateAsyncTask(listener, false).execute(category);
    }

    private static void validateName(CategoryDao categoryDao, String name) throws NameIsEmptyException, UniqueNameConstraintException {
        ModelUtils.validateNameIsNotEmpty(name);

        if (categoryDao.isCategoriesWithSameNameExists(name)) {
            throw new UniqueNameConstraintException("Category with name '" + name + "' already exists");
        }
    }

    static class RemoveAsyncTask extends AsyncTask<Category, Void, Void> {

        @Inject
        CategoryDao mCategoryDao;
        @Inject
        ProductDao mProductDao;
        @Inject
        ProductTemplateDao mProductTemplateDao;

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
        new RemoveAsyncTask().execute(category);
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
