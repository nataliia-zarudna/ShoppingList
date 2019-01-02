package com.nzarudna.shoppinglist.model.template;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.BaseRepository;
import com.nzarudna.shoppinglist.model.ModelUtils;
import com.nzarudna.shoppinglist.model.exception.EmptyNameException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.utils.AppExecutors;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository class for work with product templates
 */
@Singleton
public class ProductTemplateRepository extends BaseRepository<ProductTemplate> {

    private ProductTemplateDao mProductTemplateDao;
    private ProductDao mProductDao;

    @Inject
    public ProductTemplateRepository(ProductTemplateDao productTemplateDao, ProductDao productDao,
                                     AppExecutors appExecutors) {
        super(appExecutors);
        mProductTemplateDao = productTemplateDao;
        mProductDao = productDao;
    }

    @Override
    protected ProductTemplate create(ProductTemplate template) throws Exception {
        insertTemplate(template);
        return template;
    }

    public void createFromProductAsync(Product product, @Nullable AsyncResultListener<ProductTemplate> listener) {
        mAppExecutors.loadAsync(() -> {

            ProductTemplate template = new ProductTemplate();
            template.setName(product.getName());
            template.setCategoryID(product.getCategoryID());
            template.setUnitID(product.getUnitID());

            insertTemplate(template);

            return template;

        }, listener);
    }

    @Override
    protected ProductTemplate update(ProductTemplate template) throws Exception {
        validateName(mProductTemplateDao, template.getName());

        mProductTemplateDao.update(template);
        mProductDao.clearTemplateIDs(template.getTemplateID());

        return template;
    }

    @Override
    protected void remove(ProductTemplate template) {
        mProductTemplateDao.delete(template);
    }

    @WorkerThread
    private ProductTemplate insertTemplate(ProductTemplate template) throws UniqueNameConstraintException, EmptyNameException {

        validateName(mProductTemplateDao, template.getName());
        mProductTemplateDao.insert(template);
        return template;
    }

    private static void validateName(ProductTemplateDao productTemplateDao, String name) throws EmptyNameException, UniqueNameConstraintException {
        ModelUtils.validateNameIsNotEmpty(name);

        if (productTemplateDao.isTemplatesWithSameNameExists(name)) {
            throw new UniqueNameConstraintException("Template with name '" + name + "' already exists");
        }
    }

    public DataSource.Factory<Integer, CategoryTemplateItem> getTemplates(boolean isGroupedView) {

        if (isGroupedView) {
            return mProductTemplateDao.findAllSortByNameWithCategory();
        } else {
            return mProductTemplateDao.findAllSortByName();
        }
    }

    public DataSource.Factory<Integer, CategoryTemplateItemWithListStatistics> getTemplates(boolean isGroupedView, UUID listID) {

        if (isGroupedView) {
            return mProductTemplateDao.findAllSortByNameWithCategoryAndListStatistics(listID);
        } else {
            return mProductTemplateDao.findAllSortByNameWithListStatistics(listID);
        }
    }

    public LiveData<List<ProductTemplate>> getTemplatesByNameLike(String name, UUID listID) {
        return mProductTemplateDao.findAllByNameLike(name, listID);
    }
}
