package com.nzarudna.shoppinglist.model.template;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.ListenedAsyncTask;
import com.nzarudna.shoppinglist.model.ModelUtils;
import com.nzarudna.shoppinglist.model.exception.NameIsEmptyException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository class for work with product templates
 */
@Singleton
public class ProductTemplateRepository {


    private ProductTemplateDao mProductTemplateDao;
    private ProductDao mProductDao;

    @Inject
    public ProductTemplateRepository(ProductTemplateDao productTemplateDao, ProductDao productDao) {
        mProductTemplateDao = productTemplateDao;
        mProductDao = productDao;
    }

    public void createTemplate(ProductTemplate template, @Nullable AsyncResultListener listener) {
        insertTemplate(template, listener);
    }

    public void createTemplateFromProduct(Product product, @Nullable AsyncResultListener listener) {

        ProductTemplate template = new ProductTemplate();
        template.setName(product.getName());
        template.setCategoryID(product.getCategoryID());
        template.setUnitID(product.getUnitID());

        insertTemplate(template, listener);
    }

    private static class CreateUpdateAsyncTask extends ListenedAsyncTask<ProductTemplate, ProductTemplate> {

        ProductTemplateDao mProductTemplateDao;
        ProductDao mProductDao;
        boolean mIsCreate;

        CreateUpdateAsyncTask(ProductTemplateDao productTemplateDao, ProductDao productDao, @Nullable AsyncResultListener<ProductTemplate> listener, boolean isCreate) {
            super(listener);
            mProductTemplateDao = productTemplateDao;
            mProductDao = productDao;
            mIsCreate = isCreate;
        }

        @Override
        protected ProductTemplate doInBackground(ProductTemplate... templates) {
            try {
                ProductTemplate template = templates[0];

                validateName(mProductTemplateDao, template.getName());

                if (mIsCreate) {
                    mProductTemplateDao.insert(template);
                } else {
                    mProductTemplateDao.update(template);
                    mProductDao.clearTemplateIDs(template.getTemplateID());
                }
                return template;

            } catch (NameIsEmptyException | UniqueNameConstraintException e) {
                mResultException = e;
                return null;
            }
        }
    }

    private void insertTemplate(ProductTemplate template, @Nullable AsyncResultListener<ProductTemplate> listener) {
        new CreateUpdateAsyncTask(mProductTemplateDao, mProductDao, listener, true).execute(template);
    }

    public void updateTemplate(ProductTemplate template, @Nullable AsyncResultListener<ProductTemplate> listener) {
        new CreateUpdateAsyncTask(mProductTemplateDao, mProductDao, listener, false).execute(template);
    }

    private static void validateName(ProductTemplateDao productTemplateDao, String name) throws NameIsEmptyException, UniqueNameConstraintException {
        ModelUtils.validateNameIsNotEmpty(name);

        if (productTemplateDao.isTemplatesWithSameNameExists(name)) {
            throw new UniqueNameConstraintException("Template with name '" + name + "' already exists");
        }
    }

    static class RemoveAsyncTask extends AsyncTask<ProductTemplate, Void, Void> {

        ProductTemplateDao mProductTemplateDao;

        public RemoveAsyncTask(ProductTemplateDao productTemplateDao) {
            this.mProductTemplateDao = productTemplateDao;
        }

        @Override
        protected Void doInBackground(ProductTemplate... templates) {
            ProductTemplate template = templates[0];

            mProductTemplateDao.delete(template);

            return null;
        }
    }

    public void remove(ProductTemplate template) {
        new RemoveAsyncTask(mProductTemplateDao).execute(template);
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
