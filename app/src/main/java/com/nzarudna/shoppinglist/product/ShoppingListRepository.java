package com.nzarudna.shoppinglist.product;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ResourceResolver;
import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductsListDao;
import com.nzarudna.shoppinglist.user.UserRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.nzarudna.shoppinglist.product.ShoppingList.*;

/**
 * Shopping List Repository
 */
@Singleton
public class ShoppingListRepository {

    private static final String TAG = "ShoppingListRepository";

    private ProductsListDao mProductsListDao;
    private ProductDao mProductDao;
    private UserRepository mUserRepository;
    private ResourceResolver mResourceResolver;

    @Inject
    public ShoppingListRepository(ProductsListDao productsListDao, ProductDao productDao,
                                  UserRepository userRepository, ResourceResolver resourceResolver) {
        mProductsListDao = productsListDao;
        mProductDao = productDao;
        mUserRepository = userRepository;
        mResourceResolver = resourceResolver;
    }

    public void createList(@Nullable final OnProductListCreateListener onProductListCreateListener) {

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {

                int listID = (int) mProductsListDao.insert(createProductsList());
                if (onProductListCreateListener != null) {
                    onProductListCreateListener.onCreate(listID);
                }

                return null;
            }
        }.execute();
    }

    private ProductsList createProductsList() {
        ProductsList productsList = new ProductsList();

        String defaultName = mResourceResolver.getString(R.string.default_list_name);
        productsList.setName(defaultName);

        int selfUserID = mUserRepository.getSelfUserID();
        productsList.setCreatedBy(selfUserID);

        return productsList;
    }


    public void copyList(final int etalonListID, @Nullable final OnProductListCreateListener onProductListCreateListener)
            throws ShoppingListException {

        ProductsList etalonList = mProductsListDao.findByIDSync(etalonListID);
        if (etalonList == null) {
            throw new ShoppingListException("List with id " + etalonListID + " does not exist");
        }

        final ProductsList newProductsList = createProductsList();
        newProductsList.setName(etalonList.getName());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                int newListID = (int) mProductsListDao.insert(newProductsList);
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

    public void removeList(final ProductsList productsList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mProductsListDao.delete(productsList);
                return null;
            }
        }.execute();
    }

    public ShoppingList getList(int productListID) {
        LiveData<ProductsList> productsList = mProductsListDao.findByID(productListID);
        return new ShoppingList(mProductsListDao, productsList, productListID);
    }

    public DataSource.Factory<Integer, ProductsList> getLists(@ProductsList.ProductListStatus int status,
                                                              @ShoppingList.ProductsListSorting int sorting)
            throws ShoppingListException {

        switch (sorting) {
            case SORT_LISTS_BY_NAME:
                return mProductsListDao.findByStatusSortByName(status);
            case SORT_LISTS_BY_CREATED_AT:
                return mProductsListDao.findByStatusSortByCreatedAtDesc(status);
            case SORT_LISTS_BY_CREATED_BY:
                return mProductsListDao.findByStatusSortByCreatedByAndName(status);
            case SORT_LISTS_BY_ASSIGNED:
                return mProductsListDao.findByStatusSortByAssignedAndName(status);
            case SORT_LISTS_BY_MODIFIED_AT:
                return mProductsListDao.findByStatusSortByModifiedAtDesc(status);

            default:
                throw new ShoppingListException("Unknown sorting " + sorting);
        }
    }

    public interface OnProductListCreateListener {
        void onCreate(int productListID);
    }
}
