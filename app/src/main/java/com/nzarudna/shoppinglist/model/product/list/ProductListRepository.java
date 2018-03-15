package com.nzarudna.shoppinglist.model.product.list;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Index;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ResourceResolver;
import com.nzarudna.shoppinglist.SharedPreferencesConstants;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryDao;
import com.nzarudna.shoppinglist.model.exception.NameIsEmptyException;
import com.nzarudna.shoppinglist.model.exception.ShoppingListException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.user.UserRepository;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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

    static class CreateListAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Inject
        UserRepository mUserRepository;
        @Inject
        ResourceResolver mResourceResolver;
        @Inject
        SharedPreferences mSharedPreferences;
        @Inject
        ProductListDao mProductListDao;

        AsyncResultListener mListener;

        CreateListAsyncTask(@Nullable AsyncResultListener listener) {
            mListener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ProductList productList = createProductList(mUserRepository, mResourceResolver, mSharedPreferences);
            mProductListDao.insert(productList);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (mListener != null) {
                if (result) {
                    mListener.onAsyncSuccess();
                }
            }
        }
    }

    public void createNewList(@Nullable AsyncResultListener listener) {
        new CreateListAsyncTask(listener).execute();
    }

    static class CreateFromTemplatesAsyncTask extends CreateListAsyncTask {

        @Inject
        ProductListRepository mProductListRepository;

        List<ProductTemplate> mTemplates;

        CreateFromTemplatesAsyncTask(List<ProductTemplate> templates, @Nullable AsyncResultListener listener) {
            super(listener);
            mTemplates = templates;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            ProductList productList = createProductList(mUserRepository, mResourceResolver, mSharedPreferences);
            mProductListDao.insert(productList);

            ShoppingList shoppingList = mProductListRepository.getShoppingList(productList.getListID());
            for (ProductTemplate template : mTemplates) {
                shoppingList.addProductFromTemplate(template, null);
            }

            return true;
        }
    }

    //TODO: add test
    public void createNewList(final List<ProductTemplate> templates, @Nullable AsyncResultListener listener) {
        new CreateFromTemplatesAsyncTask(templates, listener).execute();
    }

    static class CopyListAsyncTask extends CreateListAsyncTask {

        @Inject
        ProductDao mProductDao;

        UUID mEtalonListID;

        CopyListAsyncTask(UUID etalonListID, @Nullable AsyncResultListener listener) {
            super(listener);
            mEtalonListID = etalonListID;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            ProductList etalonList = mProductListDao.findByIDSync(mEtalonListID);

            final ProductList newProductList = createProductList(mUserRepository, mResourceResolver, mSharedPreferences);
            newProductList.setName(etalonList.getName());
            newProductList.setSorting(etalonList.getSorting());
            newProductList.setIsGroupedView(etalonList.isGroupedView());

            mProductListDao.insert(newProductList);
            copyProductsFromList(mProductDao, mEtalonListID, newProductList.getListID());

            return true;
        }
    }

    public void copyList(final UUID etalonListID, @Nullable AsyncResultListener listener) {
        new CopyListAsyncTask(etalonListID, listener).execute();
    }

    private static ProductList createProductList(UserRepository userRepository, ResourceResolver mResourceResolver,
                                                 SharedPreferences sharedPreferences) {

        String defaultName = mResourceResolver.getString(R.string.default_list_name);
        String defaultNameFromPrefs = sharedPreferences.getString(
                SharedPreferencesConstants.DEFAULT_PRODUCT_LIST_NAME, defaultName);

        UUID selfUserID = userRepository.getSelfUserID();

        ProductList productList = new ProductList(defaultNameFromPrefs, selfUserID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);

        boolean defaultIsGroupedView = sharedPreferences.getBoolean(
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
                newProduct.setStatus(Product.TO_BUY);

                productDao.insert(newProduct);
            }

        } catch (CloneNotSupportedException e) {
            Log.e(TAG, "Product cannot be copied", e);
        }
    }

    public void removeList(final UUID productListID) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mProductListDao.deleteByID(productListID);
                return null;
            }
        }.execute();
    }

    public ShoppingList getShoppingList(UUID productListID) {

        ShoppingList shoppingList = new ShoppingList(productListID, mProductListDao,
                mProductDao, mProductTemplateRepository);
        return shoppingList;
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
        //TODO: add test
        return mProductListDao.findAllSortByModifiedAtDesc();
    }

    public void archiveList(final UUID productListID) {
        //TODO: add test
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                ProductList productList = mProductListDao.findByIDSync(productListID);
                productList.setStatus(ProductList.STATUS_ARCHIVED);
                mProductListDao.update(productList);

                return null;
            }
        }.execute();
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
        void onCreateNewList(UUID productListID);
    }
}
