package com.nzarudna.shoppinglist.product;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.os.AsyncTask;

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

    public ShoppingList createList() {

        final ShoppingList shoppingList = new ShoppingList();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                int listID = (int) mProductsListDao.insert(createProductsList());

                LiveData<ProductsList> productsList = mProductsListDao.findByID(listID);

                shoppingList.setListID(listID);
                shoppingList.setProductsList(productsList);

                return null;
            }
        }.execute();

        return shoppingList;
    }

    private ProductsList createProductsList() {
        ProductsList productsList = new ProductsList();

        String defaultName = mResourceResolver.getString(R.string.default_list_name);
        productsList.setName(defaultName);

        int selfUserID = mUserRepository.getSelfUserID();
        productsList.setCreatedBy(selfUserID);

        return productsList;
    }


    public ShoppingList copyList(int etalonListID) throws ShoppingListException {

        ProductsList etalonList = mProductsListDao.findByIDSync(etalonListID);
        if (etalonList == null) {
            throw new ShoppingListException("List with id " + etalonListID + " does not exist");
        }

        ProductsList newProductsList = createProductsList();
        newProductsList.setName(etalonList.getName());

        int newListID = (int) mProductsListDao.insert(newProductsList);
        LiveData<ProductsList> newListLiveData = mProductsListDao.findByID(newListID);

        copyProductsFromList(etalonListID, newListID);

        return new ShoppingList(newListLiveData, newListID);
    }

    private void copyProductsFromList(int fromListID, int toListID) throws ShoppingListException {

        try {
            List<Product> etalonProducts = mProductDao.findByListIDSync(fromListID);

            for (Product etalonProduct : etalonProducts) {

                Product newProduct = etalonProduct.clone();
                newProduct.setListID(toListID);
                newProduct.setStatus(Product.TO_BUY);

                mProductDao.insert(newProduct);
            }

        } catch (CloneNotSupportedException e) {
            throw new ShoppingListException("Product cannot be copied", e);
        }
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
}
