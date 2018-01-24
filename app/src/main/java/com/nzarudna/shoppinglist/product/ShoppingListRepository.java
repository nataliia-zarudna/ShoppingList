package com.nzarudna.shoppinglist.product;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ResourceResolver;
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

    @Inject
    public ShoppingListRepository(ProductListDao productListDao, ProductDao productDao,
                                  UserRepository userRepository, ResourceResolver resourceResolver) {
        mProductListDao = productListDao;
        mProductDao = productDao;
        mUserRepository = userRepository;
        mResourceResolver = resourceResolver;
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

    private ProductList createProductsList() {
        ProductList productList = new ProductList();

        String defaultName = mResourceResolver.getString(R.string.default_list_name);
        productList.setName(defaultName);

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

    public void removeList(final ProductList productList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mProductListDao.delete(productList);
                return null;
            }
        }.execute();
    }

    public ShoppingList getList(int productListID) {
        if (mProductListDao.findByIDSync(productListID) == null) {
            return null;
        }

        LiveData<ProductList> productsList = mProductListDao.findByID(productListID);
        return new ShoppingList(mProductListDao, productsList, productListID);
    }

    public DataSource.Factory<Integer, ProductList> getLists(@ProductList.ProductListStatus int status,
                                                             @ShoppingList.ProductsListSorting int sorting)
            throws ShoppingListException {

       /* switch (sorting) {
            case SORT_LISTS_BY_NAME:
                return mProductsListDao.findWithStaticticsByStatusSortByName(status);
            case SORT_LISTS_BY_CREATED_AT:
                return mProductsListDao.findWithStaticticsByStatusSortByCreatedAtDesc(status);
            case SORT_LISTS_BY_CREATED_BY:
                return mProductsListDao.findWithStaticticsByStatusSortByCreatedByAndName(status);
            case SORT_LISTS_BY_ASSIGNED:
                return mProductsListDao.findWithStaticticsByStatusSortByAssignedAndName(status);
            case SORT_LISTS_BY_MODIFIED_AT:
                return mProductsListDao.findWithStaticticsByStatusSortByModifiedAtDesc(status);

            default:
                throw new ShoppingListException("Unknown sorting " + sorting);
        }*/
       return null;
    }

    public interface OnProductListCreateListener {
        void onCreate(int productListID);
    }
}
