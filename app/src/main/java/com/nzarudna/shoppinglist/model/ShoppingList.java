package com.nzarudna.shoppinglist.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.dao.DaoFactory;
import com.nzarudna.shoppinglist.model.dao.ProductsListDao;
import com.nzarudna.shoppinglist.notification.NotificationManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Class that contains productsList object
 * and operates with it
 */

public class ShoppingList implements Observer<ProductsList> {

    private LiveData<ProductsList> mProductsList;
    private NotificationManager mNotificationManager;

    private ShoppingList(LiveData<ProductsList> productsList) {
        mProductsList = productsList;

        mNotificationManager = NotificationManager.getInstance();

        mProductsList.observeForever(this);
    }

    public static ShoppingList createList(Context context) {

        ProductsListDao productsListDao = DaoFactory.getInstance().getProductsListDao(context);
        int productsListID = (int) productsListDao.insert(createProductsList(context));
        LiveData<ProductsList> productsList = productsListDao.findByID(productsListID);

        return new ShoppingList(productsList);
    }

    private static ProductsList createProductsList(Context context) {
        ProductsList productsList = new ProductsList();

        String defaultName = context.getString(R.string.default_list_name);
        productsList.setName(defaultName);

        int selfUserID = UserManager.getSelfUserID(context);
        productsList.setCreatedBy(selfUserID);

        return productsList;
    }

    @Override
    public void onChanged(@Nullable ProductsList productsList) {

        mNotificationManager.sendNotification();

    }

    public static ShoppingList copyList(ProductsList etalonList) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static LiveData<PagedList<ShoppingList>> getLists(
            @ProductsList.ProductListStatus int status, @ProductsListSorting int sorting) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public LiveData<ProductsList> getListData() {
        return mProductsList;
    }

    public void removeList() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public LiveData<Product> addProduct(@NonNull String name, String comment) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public LiveData<Product> addProduct(@NonNull String name, double count, int unitID) {
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
