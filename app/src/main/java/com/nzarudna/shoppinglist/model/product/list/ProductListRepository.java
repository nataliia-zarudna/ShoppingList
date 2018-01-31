package com.nzarudna.shoppinglist.model.product.list;

import android.arch.paging.DataSource;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ResourceResolver;
import com.nzarudna.shoppinglist.SharedPreferencesConstants;
import com.nzarudna.shoppinglist.model.ShoppingListException;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.user.UserRepository;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Shopping List Repository
 */
@Singleton
public class ProductListRepository {

    private static final String TAG = "ProductListRepository";

    private ProductListDao mProductListDao;
    private ProductDao mProductDao;
    private ProductTemplateRepository mProductTemplateRepository;
    private UserRepository mUserRepository;
    private ResourceResolver mResourceResolver;
    private SharedPreferences mSharedPreferences;

    @Inject
    public ProductListRepository(ProductListDao productListDao, ProductDao productDao,
                                 ProductTemplateRepository productTemplateRepository,
                                 UserRepository userRepository, ResourceResolver resourceResolver,
                                 SharedPreferences sharedPreferences) {
        mProductListDao = productListDao;
        mProductDao = productDao;
        mProductTemplateRepository = productTemplateRepository;
        mUserRepository = userRepository;
        mResourceResolver = resourceResolver;
        mSharedPreferences = sharedPreferences;
    }

    public void createList(@Nullable final OnProductListCreateListener onProductListCreateListener) {

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {

                int listID = (int) mProductListDao.insert(createProductList());
                if (onProductListCreateListener != null) {
                    onProductListCreateListener.onCreate(listID);
                }

                return null;
            }
        }.execute();
    }

    public ProductList createProductList() {
        ProductList productList = new ProductList();

        String defaultName = mResourceResolver.getString(R.string.default_list_name);
        String defaultNameFromPrefs = mSharedPreferences.getString(
                SharedPreferencesConstants.DEFAULT_PRODUCT_LIST_NAME, defaultName);
        productList.setName(defaultNameFromPrefs);

        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);

        boolean defaultIsGroupedView = mSharedPreferences.getBoolean(
                SharedPreferencesConstants.DEFAULT_PRODUCT_LIST_IS_GROUPED_VIEW, false);
        productList.setIsGroupedView(defaultIsGroupedView);

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

        final ProductList newProductList = createProductList();
        newProductList.setName(etalonList.getName());
        newProductList.setSorting(etalonList.getSorting());
        newProductList.setIsGroupedView(etalonList.isGroupedView());

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

    public ShoppingList getShoppingList(int productListID) {

        ShoppingList shoppingList = new ShoppingList(productListID, mProductListDao,
                mProductDao, mProductTemplateRepository);
        return shoppingList;
    }

    public DataSource.Factory<Integer, ProductListWithStatistics> getLists(@ProductList.ProductListStatus int status,
                                                                           @ProductListSorting int sorting)
            throws ShoppingListException {

        switch (sorting) {
            case SORT_LISTS_BY_NAME:
                return mProductListDao.findWithStaticticsByStatusSortByName(status);
            case SORT_LISTS_BY_CREATED_AT:
                return mProductListDao.findWithStaticticsByStatusSortByCreatedAtDesc(status);
            case SORT_LISTS_BY_CREATED_BY:
                return mProductListDao.findWithStaticticsByStatusSortByCreatedByAndName(status);
            case SORT_LISTS_BY_ASSIGNED:
                return mProductListDao.findWithStaticticsByStatusSortByAssignedAndName(status);
            case SORT_LISTS_BY_MODIFIED_AT:
                return mProductListDao.findWithStaticticsByStatusSortByModifiedAtDesc(status);

            default:
                throw new ShoppingListException("Unknown sorting " + sorting);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SORT_LISTS_BY_NAME, SORT_LISTS_BY_CREATED_BY, SORT_LISTS_BY_CREATED_AT,
            SORT_LISTS_BY_MODIFIED_AT, SORT_LISTS_BY_ASSIGNED, SORT_LISTS_BY_PRODUCT_ORDER})
    public @interface ProductListSorting {
    }

    public static final int SORT_LISTS_BY_NAME = 1;
    public static final int SORT_LISTS_BY_CREATED_BY = 2;
    public static final int SORT_LISTS_BY_CREATED_AT = 3;
    public static final int SORT_LISTS_BY_MODIFIED_AT = 4;
    public static final int SORT_LISTS_BY_ASSIGNED = 5;
    public static final int SORT_LISTS_BY_PRODUCT_ORDER = 6;

    public interface OnProductListCreateListener {
        void onCreate(int productListID);
    }
}