package com.nzarudna.shoppinglist.product;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ResourceResolver;
import com.nzarudna.shoppinglist.SharedPreferencesConstants;
import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductListDao;
import com.nzarudna.shoppinglist.user.UserRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Shopping List Repository
 */
@Singleton
public class ShoppingListRepository {

    private static final String TAG = "ShoppingListRepository";

    private ProductListDao mProductListDao;
    private ProductDao mProductDao;
    private UserRepository mUserRepository;
    private ResourceResolver mResourceResolver;
    private SharedPreferences mSharedPreferences;

    @Inject
    public ShoppingListRepository(ProductListDao productListDao, ProductDao productDao,
                                  UserRepository userRepository, ResourceResolver resourceResolver,
                                  SharedPreferences sharedPreferences) {
        mProductListDao = productListDao;
        mProductDao = productDao;
        mUserRepository = userRepository;
        mResourceResolver = resourceResolver;
        mSharedPreferences = sharedPreferences;
    }

    public void createList(@Nullable final OnProductListCreateListener onProductListCreateListener) {

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {

                int listID = (int) mProductListDao.insert(createProductsList());
                if (onProductListCreateListener != null) {
                    onProductListCreateListener.onCreate(listID);
                }

                return null;
            }
        }.execute();
    }

    public ProductList createProductsList() {
        ProductList productList = new ProductList();

        String defaultName = mResourceResolver.getString(R.string.default_list_name);
        String defaultNameFromPrefs = mSharedPreferences.getString(
                SharedPreferencesConstants.DEFAULT_PRODUCT_LIST_NAME, defaultName);
        productList.setName(defaultNameFromPrefs);

        productList.setSorting(ProductList.SORT_LISTS_BY_PRODUCT_ORDER);

        int selfUserID = mUserRepository.getSelfUserID();
        productList.setCreatedBy(selfUserID);

        return productList;
    }


    public void copyList(final int etalonListID, @Nullable final OnProductListCreateListener onProductListCreateListener)
            throws ShoppingListException {

        ProductList etalonList = mProductListDao.findByIDSync(etalonListID);
        if (etalonList == null) {
            throw new ShoppingListException("List with id " + etalonListID + " does not exist");
        }

        final ProductList newProductList = createProductsList();
        newProductList.setName(etalonList.getName());
        newProductList.setSorting(etalonList.getSorting());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                int newListID = (int) mProductListDao.insert(newProductList);
                copyProductsFromList(etalonListID, newListID);

                if (onProductListCreateListener != null) {
                    onProductListCreateListener.onCreate(newListID);
                }

                return null;
            }
        }.execute();
    }

    private void copyProductsFromList(int fromListID, int toListID) {

        try {
            List<Product> etalonProducts = mProductDao.findByListIDSync(fromListID);

            for (Product etalonProduct : etalonProducts) {

                Product newProduct = etalonProduct.clone();
                newProduct.setListID(toListID);
                newProduct.setStatus(Product.TO_BUY);

                mProductDao.insert(newProduct);
            }

        } catch (CloneNotSupportedException e) {
            Log.e(TAG, "Product cannot be copied", e);
        }
    }

    public void removeList(final int productListID) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mProductListDao.deleteByID(productListID);
                return null;
            }
        }.execute();
    }

    public ShoppingList getList(int productListID) {
        if (mProductListDao.findByIDSync(productListID) == null) {
            return null;
        }

        return new ShoppingList(mProductListDao, productListID);
    }

    public DataSource.Factory<Integer, ProductListWithStatistics> getLists(@ProductList.ProductListStatus int status,
                                                                           @ProductList.ProductListSorting int sorting)
            throws ShoppingListException {

        switch (sorting) {
            case ProductList.SORT_LISTS_BY_NAME:
                return mProductListDao.findWithStaticticsByStatusSortByName(status);
            case ProductList.SORT_LISTS_BY_CREATED_AT:
                return mProductListDao.findWithStaticticsByStatusSortByCreatedAtDesc(status);
            case ProductList.SORT_LISTS_BY_CREATED_BY:
                return mProductListDao.findWithStaticticsByStatusSortByCreatedByAndName(status);
            case ProductList.SORT_LISTS_BY_ASSIGNED:
                return mProductListDao.findWithStaticticsByStatusSortByAssignedAndName(status);
            case ProductList.SORT_LISTS_BY_MODIFIED_AT:
                return mProductListDao.findWithStaticticsByStatusSortByModifiedAtDesc(status);

            default:
                throw new ShoppingListException("Unknown sorting " + sorting);
        }
    }

    public interface OnProductListCreateListener {
        void onCreate(int productListID);
    }
}
