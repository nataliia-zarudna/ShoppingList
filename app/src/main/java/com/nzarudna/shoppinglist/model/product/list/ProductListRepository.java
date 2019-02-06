package com.nzarudna.shoppinglist.model.product.list;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ResourceResolver;
import com.nzarudna.shoppinglist.SharedPreferencesConstants;
import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.exception.ShoppingListException;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.user.UserRepository;
import com.nzarudna.shoppinglist.utils.AppExecutors;
import com.nzarudna.shoppinglist.utils.ErrorHandler;
import com.nzarudna.shoppinglist.utils.Preferences;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    private AppExecutors mAppExecutors;
    private Preferences mPreferences;

    @Inject
    public ProductListRepository(ProductListDao productListDao, ProductDao productDao,
                                 ProductTemplateRepository productTemplateRepository,
                                 UserRepository userRepository, ResourceResolver resourceResolver,
                                 SharedPreferences sharedPreferences, AppExecutors appExecutors,
                                 Preferences preferences) {
        this.mProductListDao = productListDao;
        this.mProductDao = productDao;
        this.mProductTemplateRepository = productTemplateRepository;
        this.mUserRepository = userRepository;
        this.mResourceResolver = resourceResolver;
        this.mSharedPreferences = sharedPreferences;
        this.mAppExecutors = appExecutors;
        this.mPreferences = preferences;
    }

    public void createNewList(@Nullable AsyncResultListener<ProductList> listener) {
        mAppExecutors.loadAsync(() -> {

            ProductList productList = createProductList();
            mProductListDao.insert(productList);

            return productList;
        }, listener);
    }

    public void createNewList(List<ProductTemplate> templates, @Nullable AsyncResultListener<ProductList> listener) {
        mAppExecutors.loadAsync(() -> {

            ProductList productList = createProductList();
            mProductListDao.insert(productList);

            ShoppingList shoppingList = getShoppingList(productList.getListID());
            for (ProductTemplate template : templates) {
                shoppingList.addProductFromTemplate(template, null);
            }

            return productList;

        }, listener);
    }

    public void copyList(UUID etalonListID, @Nullable AsyncResultListener<ProductList> listener) {
        mAppExecutors.loadAsync(() -> {

            ProductList etalonList = mProductListDao.findByIDSync(etalonListID);

            ProductList newProductList = createProductList();
            newProductList.setName(etalonList.getName());
            newProductList.setSorting(etalonList.getSorting());
            newProductList.setUseCustomSorting(etalonList.isUseCustomSorting());
            newProductList.setIsGroupedView(etalonList.isGroupedView());

            mProductListDao.insert(newProductList);
            copyProductsFromList(mProductDao, etalonListID, newProductList.getListID());

            return newProductList;

        }, listener);
    }

    private ProductList createProductList() {

        String defaultName = mResourceResolver.getString(R.string.default_list_name, getNextShoppingListNum());
        String defaultNameFromPrefs = mSharedPreferences.getString(
                SharedPreferencesConstants.DEFAULT_PRODUCT_LIST_NAME, defaultName);

        UUID selfUserID = mUserRepository.getSelfUserID();

        ProductList productList = new ProductList(defaultNameFromPrefs, selfUserID);

        boolean defaultIsGroupedView = mSharedPreferences.getBoolean(
                SharedPreferencesConstants.DEFAULT_PRODUCT_LIST_IS_GROUPED_VIEW, false);
        productList.setIsGroupedView(defaultIsGroupedView);

        return productList;
    }

    private static void copyProductsFromList(ProductDao productDao, UUID fromListID, UUID toListID) {

        try {
            List<Product> etalonProducts = productDao.findByListIDSync(fromListID);

            for (Product etalonProduct : etalonProducts) {

                Product newProduct = etalonProduct.clone();
                newProduct.setListID(toListID);
                newProduct.setStatus(Product.ACTIVE);

                productDao.insert(newProduct);
            }

        } catch (CloneNotSupportedException e) {
            ErrorHandler.logError(TAG, "Product cannot be copied", e);
        }
    }

    public void updateListStatus(UUID productListID, @ProductList.ProductListStatus int status, @Nullable AsyncResultListener<ProductList> listener) {
        mAppExecutors.loadAsync(() -> {

            ProductList productList = mProductListDao.findByIDSync(productListID);
            productList.setStatus(status);
            productList.setModifiedBy(mUserRepository.getSelfUserID());
            productList.setModifiedAt(new Date());
            mProductListDao.update(productList);

            return productList;

        }, listener);
    }

    public void removeList(UUID productListID, AsyncListener listener) {
        mAppExecutors.loadAsync(() -> mProductListDao.deleteByID(productListID), listener);
    }

    public ShoppingList getShoppingList(UUID productListID) {
        return new ShoppingList(productListID, mProductListDao,
                mProductDao, mProductTemplateRepository, mUserRepository, mAppExecutors);
    }

    public DataSource.Factory<Integer, ProductListWithStatistics> getListsWithStatistics(@ProductList.ProductListStatus int status,
                                                                                         @ProductListSorting int sorting)
            throws ShoppingListException {

        switch (sorting) {
            case SORT_LISTS_BY_NAME:
                return mProductListDao.findWithStatisticsByStatusSortByName(status);
            case SORT_LISTS_BY_CREATED_AT:
                return mProductListDao.findWithStatisticsByStatusSortByCreatedAtDesc(status);
            case SORT_LISTS_BY_CREATED_BY:
                return mProductListDao.findWithStatisticsByStatusSortByCreatedByAndName(status);
            case SORT_LISTS_BY_ASSIGNED:
                return mProductListDao.findWithStatisticsByStatusSortByAssignedAndName(status);
            case SORT_LISTS_BY_MODIFIED_AT:
                return mProductListDao.findWithStatisticsByStatusSortByModifiedAtDesc(status);

            default:
                throw new ShoppingListException("Unknown sorting " + sorting);
        }
    }

    public DataSource.Factory<Integer, ProductList> getLists(@ProductList.ProductListStatus int status,
                                                             @ProductListSorting int sorting)
            throws ShoppingListException {

        switch (sorting) {
            case SORT_LISTS_BY_NAME:
                return mProductListDao.findByStatusSortByName(status);
            case SORT_LISTS_BY_MODIFIED_AT:
                return mProductListDao.findStatusSortByModifiedAtDesc(status);

            default:
                throw new ShoppingListException("Unsupported sorting " + sorting);
        }
    }

    public LiveData<List<ProductList>> getAllLists() {
        return mProductListDao.findAllSortByModifiedAtDesc();
    }

    private int getNextShoppingListNum() {
        int lastUsedShoppingListNum = mPreferences.getLastUsedShoppingListNum();
        lastUsedShoppingListNum++;
        mPreferences.setLastUsedShoppingListNum(lastUsedShoppingListNum);
        return lastUsedShoppingListNum;
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
}
