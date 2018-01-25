package com.nzarudna.shoppinglist.product;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nzarudna.shoppinglist.persistence.ProductListDao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Class that contains productsList object
 * and operates with it
 */

public class ShoppingList implements Observer<ProductList> {

    public static final String TAG = "ShoppingList";

    private LiveData<ProductList> mProductsList;
    private int mListID;

    private ProductListDao mProductListDao;

    public ShoppingList(ProductListDao productListDao, int listID) {
        mProductListDao = productListDao;
        mListID = listID;

        mProductsList = productListDao.findByID(listID);
    }

    public LiveData<ProductList> getListData() {
        return mProductsList;
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

    @Override
    public void onChanged(@Nullable ProductList productList) {

        //mNotificationManager.sendNotification();
    }

    /*public LiveData<Product> addProduct(@NonNull String name, String comment) {
        throw new UnsupportedOperationException("Not implemented yet");
    }*/

    public LiveData<Product> addProduct(@NonNull Product product) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public LiveData<Product> addProductFromTemplate(ProductTemplate template) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void removeProduct(LiveData<Product> product) {
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
