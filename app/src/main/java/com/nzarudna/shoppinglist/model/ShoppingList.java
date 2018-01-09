package com.nzarudna.shoppinglist.model;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Class that contains productsList object
 * and operates with it
 */

public class ShoppingList {

    private LiveData<ProductsList> productsList;

    public static ShoppingList createList() {
        throw new UnsupportedOperationException();
    }

    public static ShoppingList copyList(ProductsList etalonList) {
        throw new UnsupportedOperationException();
    }

    public static LiveData<PagedList<ShoppingList>> getLists(
            @ProductsList.ProductListStatus int status, @ProductsListSorting int sorting) {
        throw new UnsupportedOperationException();
    }

    public void removeList() {
        throw new UnsupportedOperationException();
    }

    public LiveData<Product> addProduct(@NonNull String name, String comment) {
        throw new UnsupportedOperationException();
    }

    public LiveData<Product> addProduct(@NonNull String name, double count, int unitID) {
        throw new UnsupportedOperationException();
    }

    public LiveData<Product> addProductFromTemplate(ProductTemplate template) {
        throw new UnsupportedOperationException();
    }

    public void removeProduct(LiveData<Product> product) {
        throw new UnsupportedOperationException();
    }

    public LiveData<PagedList<Product>> getProducts(@ProducstsSorting int sorting) {
        throw new UnsupportedOperationException();
    }

    public static final int SORT_LISTS_BY_NAME = 1;
    public static final int SORT_LISTS_BY_CREATED_BY = 2;
    public static final int SORT_LISTS_BY_CREATED_AT = 3;
    public static final int SORT_LISTS_BY_MODIFIED_AT = 4;
    public static final int SORT_LISTS_BY_ASSIGNED = 5;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SORT_LISTS_BY_NAME, SORT_LISTS_BY_CREATED_BY, SORT_LISTS_BY_CREATED_AT,
            SORT_LISTS_BY_MODIFIED_AT, SORT_LISTS_BY_ASSIGNED})
    public @interface ProductsListSorting {
    }

    public static final int SORT_PRODUCTS_BY_NAME = 1;
    public static final int SORT_PRODUCTS_BY_CATEGORY = 2;
    public static final int SORT_PRODUCTS_BY_STATUS = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SORT_PRODUCTS_BY_NAME, SORT_PRODUCTS_BY_CATEGORY, SORT_PRODUCTS_BY_STATUS})
    public @interface ProducstsSorting {
    }
}
