package com.nzarudna.shoppinglist.model.product.list;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ResourceResolver;
import com.nzarudna.shoppinglist.SharedPreferencesConstants;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.ListenedAsyncTask;
import com.nzarudna.shoppinglist.model.exception.ShoppingListException;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
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
        this.mProductListDao = productListDao;
        this.mProductDao = productDao;
        this.mProductTemplateRepository = productTemplateRepository;
        this.mUserRepository = userRepository;
        this.mResourceResolver = resourceResolver;
        this.mSharedPreferences = sharedPreferences;
    }

    private static class CreateListAsyncTask extends ListenedAsyncTask<Void, ProductList> {

        UserRepository mUserRepository;
        ResourceResolver mResourceResolver;
        SharedPreferences mSharedPreferences;
        ProductListDao mProductListDao;

        AsyncResultListener mListener;

        CreateListAsyncTask(UserRepository userRepository, ResourceResolver resourceResolver,
                            SharedPreferences sharedPreferences, ProductListDao productListDao,
                            @Nullable AsyncResultListener<ProductList> listener) {
            super(listener);
            this.mUserRepository = userRepository;
            this.mResourceResolver = resourceResolver;
            this.mSharedPreferences = sharedPreferences;
            this.mProductListDao = productListDao;
        }

        @Override
        protected ProductList doInBackground(Void... voids) {
            ProductList productList = createProductList(mUserRepository, mResourceResolver, mSharedPreferences);
            mProductListDao.insert(productList);

            return productList;
        }
    }

    public void createNewList(@Nullable AsyncResultListener<ProductList> listener) {
        new CreateListAsyncTask(mUserRepository, mResourceResolver, mSharedPreferences, mProductListDao, listener).execute();
    }

    private static class CreateFromTemplatesAsyncTask extends ListenedAsyncTask<Void, ProductList> {

        ProductListRepository mProductListRepository;
        ProductListDao mProductListDao;
        UserRepository mUserRepository;
        ResourceResolver mResourceResolver;
        SharedPreferences mSharedPreferences;

        List<ProductTemplate> mTemplates;

        public CreateFromTemplatesAsyncTask(ProductListRepository productListRepository,
                                            ProductListDao productListDao, UserRepository userRepository,
                                            ResourceResolver resourceResolver, SharedPreferences sharedPreferences,
                                            List<ProductTemplate> templates, @Nullable AsyncResultListener<ProductList> listener) {
            super(listener);
            this.mProductListRepository = productListRepository;
            this.mProductListDao = productListDao;
            this.mUserRepository = userRepository;
            this.mResourceResolver = resourceResolver;
            this.mSharedPreferences = sharedPreferences;
            this.mTemplates = templates;
        }

        @Override
        protected ProductList doInBackground(Void... voids) {

            ProductList productList = createProductList(mUserRepository, mResourceResolver, mSharedPreferences);
            mProductListDao.insert(productList);

            ShoppingList shoppingList = mProductListRepository.getShoppingList(productList.getListID());
            for (ProductTemplate template : mTemplates) {
                shoppingList.addProductFromTemplate(template, null);
            }

            return productList;
        }
    }

    //TODO: add test
    public void createNewList(List<ProductTemplate> templates, @Nullable AsyncResultListener<ProductList> listener) {
        new CreateFromTemplatesAsyncTask(this, mProductListDao, mUserRepository,
                mResourceResolver, mSharedPreferences, templates, listener).execute();
    }

    private static class CopyListAsyncTask extends ListenedAsyncTask<Void, ProductList> {

        ProductDao mProductDao;
        ProductListDao mProductListDao;
        UserRepository mUserRepository;
        ResourceResolver mResourceResolver;
        SharedPreferences mSharedPreferences;

        UUID mEtalonListID;

        public CopyListAsyncTask(ProductDao productDao, ProductListDao productListDao,
                                 UserRepository userRepository, ResourceResolver resourceResolver,
                                 SharedPreferences sharedPreferences, UUID etalonListID,
                                 @Nullable AsyncResultListener<ProductList> listener) {
            super(listener);
            this.mProductDao = productDao;
            this.mProductListDao = productListDao;
            this.mUserRepository = userRepository;
            this.mResourceResolver = resourceResolver;
            this.mSharedPreferences = sharedPreferences;
            this.mEtalonListID = etalonListID;
        }

        @Override
        protected ProductList doInBackground(Void... voids) {

            ProductList etalonList = mProductListDao.findByIDSync(mEtalonListID);

            ProductList newProductList = createProductList(mUserRepository, mResourceResolver, mSharedPreferences);
            newProductList.setName(etalonList.getName());
            newProductList.setSorting(etalonList.getSorting());
            newProductList.setIsGroupedView(etalonList.isGroupedView());

            mProductListDao.insert(newProductList);
            copyProductsFromList(mProductDao, mEtalonListID, newProductList.getListID());

            return newProductList;
        }
    }

    public void copyList(UUID etalonListID, @Nullable AsyncResultListener<ProductList> listener) {
        new CopyListAsyncTask(mProductDao, mProductListDao, mUserRepository, mResourceResolver,
                mSharedPreferences, etalonListID, listener).execute();
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


    private static class UpdateListStatusAsyncTask extends AsyncTask<UUID, Void, Void> {

        ProductListDao mProductListDao;

        @ProductList.ProductListStatus
        int mStatus;

        UpdateListStatusAsyncTask(ProductListDao productListDao, int status) {
            this.mProductListDao = productListDao;
            this.mStatus = status;
        }

        @Override
        protected Void doInBackground(UUID... params) {
            UUID productListID = params[0];

            ProductList productList = mProductListDao.findByIDSync(productListID);
            productList.setStatus(mStatus);
            mProductListDao.update(productList);

            return null;
        }
    }

    //TODO: add test start
    public void updateListStatus(UUID productListID, @ProductList.ProductListStatus int status) {
        new UpdateListStatusAsyncTask(mProductListDao, status).execute(productListID);
    }
    //TODO: add test end

    static class RemoveListAsyncTask extends AsyncTask<UUID, Void, Void> {

        ProductListDao mProductListDao;

        RemoveListAsyncTask(ProductListDao productListDao) {
            this.mProductListDao = productListDao;
        }

        @Override
        protected Void doInBackground(UUID... params) {
            UUID productListID = params[0];
            mProductListDao.deleteByID(productListID);
            return null;
        }
    }

    public void removeList(UUID productListID) {
        new RemoveListAsyncTask(mProductListDao).execute(productListID);
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
