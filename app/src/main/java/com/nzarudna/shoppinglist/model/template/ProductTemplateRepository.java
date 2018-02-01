package com.nzarudna.shoppinglist.model.template;

import android.arch.paging.DataSource;
import android.os.AsyncTask;

import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;

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

    public void createTemplate(ProductTemplate template) {
        insertTemplate(template);
    }

    public void createTemplateFromProduct(Product product) {

        ProductTemplate template = new ProductTemplate(product.getName());
        template.setCategoryID(product.getCategoryID());
        template.setUnitID(product.getUnitID());

        insertTemplate(template);
    }

    private void insertTemplate(final ProductTemplate template) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mProductTemplateDao.insert(template);
                return null;
            }
        }.execute();
    }

    public void updateTemplate(final ProductTemplate template) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                mProductTemplateDao.update(template);
                mProductDao.clearTemplateIDs(template.getTemplateID());

                return null;
            }
        }.execute();
    }

    public void removeTemplate(final ProductTemplate template) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                mProductTemplateDao.delete(template);

                return null;
            }
        }.execute();
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

    public DataSource.Factory<Integer, ProductTemplate> getTemplatesByNameLike(String name, UUID listID) {
        return mProductTemplateDao.findAllByNameLike(name, listID);
    }
}
