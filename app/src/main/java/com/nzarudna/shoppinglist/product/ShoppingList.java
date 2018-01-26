package com.nzarudna.shoppinglist.product;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductListDao;
import com.nzarudna.shoppinglist.persistence.ProductTemplateDao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

/**
 * Class that contains productsList object
 * and operates with it
 */

public class ShoppingList {

    public static final String TAG = "ShoppingList";

    private int mListID;

    @Inject
    public ProductTemplateRepository mProductTemplateRepository;
    @Inject
    public ProductListDao mProductListDao;
    @Inject
    public ProductTemplateDao mProductTemplateDao;
    @Inject
    public ProductDao mProductDao;

    public ShoppingList(/*ProductListDao productListDao,*/ int listID) {
        //mProductListDao = productListDao;
        mListID = listID;
    }

    public LiveData<ProductList> getListData() {
        return mProductListDao.findByID(mListID);
    }

    public int getListID() {
        return mListID;
    }

    public void updateProductList(final ProductList productList) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                mProductListDao.update(productList);
                return null;
            }
        }.execute();
    }

    public void addProduct(@NonNull final Product product) {
        insertProduct(product);
        mProductTemplateRepository.createTemplateFromProduct(product);
    }

    public void addProductFromTemplate(ProductTemplate template) {

        Product product = new Product();
        product.setName(template.getName());
        product.setCategoryID(template.getCategoryID());
        product.setTemplateID(template.getTemplateID());

        insertProduct(product);
    }

    private void insertProduct(final Product product) {
        product.setListID(mListID);

        ProductList productList = mProductListDao.findByIDSync(mListID);
        if (productList.getSorting() == ProductList.SORT_LISTS_BY_PRODUCT_ORDER) {
            double maxProductOrder = mProductDao.getMaxProductOrderByListID(mListID);
            product.setOrder(maxProductOrder + 10);
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mProductDao.insert(product);
                return null;
            }
        }.execute();
    }

    public void updateProduct(@NonNull final Product product) {

        Product oldProduct = mProductDao.findByIDSync(product.getProductID());
        if (!oldProduct.getName().equals(product.getName())
                || oldProduct.getCategoryID() != product.getCategoryID()) {
            product.setTemplateID(0);
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                mProductDao.update(product);
                return null;
            }
        }.execute();
    }

    public void moveProduct(Product product, Product productBefore, Product productAfter) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void removeProduct(Product product) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public LiveData<PagedList<Product>> getProducts(@ProducstsSorting int sorting) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static final int SORT_PRODUCTS_BY_NAME = 1;
    public static final int SORT_PRODUCTS_BY_CATEGORY = 2;
    public static final int SORT_PRODUCTS_BY_STATUS = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SORT_PRODUCTS_BY_NAME, SORT_PRODUCTS_BY_CATEGORY, SORT_PRODUCTS_BY_STATUS})
    public @interface ProducstsSorting {
    }
}
