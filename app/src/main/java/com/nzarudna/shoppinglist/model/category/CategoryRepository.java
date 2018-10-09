package com.nzarudna.shoppinglist.model.category;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.support.annotation.WorkerThread;

import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.BaseRepository;
import com.nzarudna.shoppinglist.model.ModelUtils;
import com.nzarudna.shoppinglist.model.exception.NameIsEmptyException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;
import com.nzarudna.shoppinglist.utils.AppExecutors;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Category repository
 */
@Singleton
public class CategoryRepository extends BaseRepository<Category> {

    @Inject
    CategoryDao mCategoryDao;
    @Inject
    ProductDao mProductDao;
    @Inject
    ProductTemplateDao mProductTemplateDao;

//    @Inject
//    public CategoryRepository(CategoryDao categoryDao, ProductDao productDao,
//                              ProductTemplateDao productTemplateDao, AppExecutors appExecutors) {
//        super(appExecutors);
//
//        mCategoryDao = categoryDao;
//        mProductDao = productDao;
//        mProductTemplateDao = productTemplateDao;
//    }

    @Override
    @WorkerThread
    protected Category create(Category category) throws NameIsEmptyException, UniqueNameConstraintException {
        validateName(category.getName());
        mCategoryDao.insert(category);

        return category;
    }

    @Override
    @WorkerThread
    protected Category update(Category category) throws NameIsEmptyException, UniqueNameConstraintException {
        validateName(category.getName());
        mCategoryDao.update(category);

        return category;
    }

    @WorkerThread
    private void validateName(String name) throws NameIsEmptyException, UniqueNameConstraintException {
        ModelUtils.validateNameIsNotEmpty(name);

        UUID duplicateCategoryID = mCategoryDao.findBySimilarName(name);
        if (duplicateCategoryID != null) {
            UniqueNameConstraintException e = new UniqueNameConstraintException("Category with name '" + name + "' already exists");
            e.setDuplicateEntityID(duplicateCategoryID);
            throw e;
        }
    }

    @Override
    @WorkerThread
    protected void remove(Category category) {

        mProductDao.setDefaultCategoryID(category.getCategoryID());
        mProductTemplateDao.setDefaultCategoryID(category.getCategoryID());

        mCategoryDao.delete(category);
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
