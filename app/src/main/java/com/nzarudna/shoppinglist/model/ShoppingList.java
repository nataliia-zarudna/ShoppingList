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
import com.nzarudna.shoppinglist.model.dao.ProductDao;
import com.nzarudna.shoppinglist.model.dao.ProductsListDao;
import com.nzarudna.shoppinglist.notification.NotificationManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Class that contains productsList object
 * and operates with it
 */

public class ShoppingList implements Observer<ProductsList> {

    public static final String TAG = "ShoppingList";

    private LiveData<ProductsList> mProductsList;
    private int mListID;

    private Context mContext;
    private NotificationManager mNotificationManager;
    private ProductsListDao mProductsListDao;

    private ShoppingList(Context context, LiveData<ProductsList> productsList, int listID) {
        mContext = context;
        mProductsList = productsList;
        mListID = listID;

        mNotificationManager = NotificationManager.getInstance();
        mProductsListDao = DaoFactory.getInstance().getProductsListDao(context);

        mProductsList.observeForever(this);
    }

    public static ShoppingList createList(Context context) {

        ProductsListDao productsListDao = DaoFactory.getInstance().getProductsListDao(context);
        int productsListID = (int) productsListDao.insert(createProductsList(context));
        LiveData<ProductsList> productsList = productsListDao.findByID(productsListID);

        return new ShoppingList(context, productsList, productsListID);
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

    public static ShoppingList copyList(Context context, int etalonListID) throws ShoppingListException {

        ProductsListDao productsListDao = DaoFactory.getInstance().getProductsListDao(context);
        ProductsList etalonList = productsListDao.findByIDSync(etalonListID);
        if (etalonList == null) {
            throw new ShoppingListException("List with id " + etalonListID + " does not exist");
        }

        final ProductsList newProductsList = createProductsList(context);
        newProductsList.setName(etalonList.getName());

        int newListID = (int) productsListDao.insert(newProductsList);
        LiveData<ProductsList> newListLiveData = productsListDao.findByID(newListID);

        copyProductsFromList(context, etalonListID, newListID);

        return new ShoppingList(context, newListLiveData, newListID);
    }

    private static void copyProductsFromList(Context context, int fromListID, int toListID) throws ShoppingListException {

        try {
            ProductDao productDao = DaoFactory.getInstance().getProductDao(context);
            List<Product> etalonProducts = productDao.findByListIDSync(fromListID);

            for (Product etalonProduct : etalonProducts) {

                Product newProduct = etalonProduct.clone();
                newProduct.setListID(toListID);
                newProduct.setStatus(Product.TO_BUY);

                productDao.insert(newProduct);
            }

        } catch (CloneNotSupportedException e) {
            throw new ShoppingListException("Product cannot be copied", e);
        }
    }

    public static LiveData<PagedList<ShoppingList>> getLists(
            @ProductsList.ProductListStatus int status, @ProductsListSorting int sorting) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public LiveData<ProductsList> getListData() {
        return mProductsList;
    }

    public int getListID() {
        return mListID;
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
