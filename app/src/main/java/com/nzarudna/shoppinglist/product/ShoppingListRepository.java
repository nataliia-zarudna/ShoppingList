package com.nzarudna.shoppinglist.product;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.content.Context;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductsListDao;

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
    private Context mContext;

    @Inject
    public ShoppingListRepository(Context context, ProductsListDao productsListDao, ProductDao productDao) {
        mProductsListDao = productsListDao;
        mProductDao = productDao;
        mContext = context;
    }

    public ShoppingList createList() {

        int productsListID = (int) mProductsListDao.insert(createProductsList());
        LiveData<ProductsList> productsList = mProductsListDao.findByID(productsListID);

        return new ShoppingList(productsList, productsListID);
    }

    private ProductsList createProductsList() {
        ProductsList productsList = new ProductsList();

        String defaultName = mContext.getString(R.string.default_list_name);
        productsList.setName(defaultName);

        //int selfUserID = UserManager.getSelfUserID(mContext);
        //productsList.setCreatedBy(selfUserID);

        return productsList;
    }


    public ShoppingList copyList(Context context, int etalonListID) throws ShoppingListException {

        ProductsList etalonList = mProductsListDao.findByIDSync(etalonListID);
        if (etalonList == null) {
            throw new ShoppingListException("List with id " + etalonListID + " does not exist");
        }

        final ProductsList newProductsList = createProductsList();
        newProductsList.setName(etalonList.getName());

        int newListID = (int) mProductsListDao.insert(newProductsList);
        LiveData<ProductsList> newListLiveData = mProductsListDao.findByID(newListID);

        copyProductsFromList(context, etalonListID, newListID);

        return new ShoppingList(newListLiveData, newListID);
    }

    private void copyProductsFromList(Context context, int fromListID, int toListID) throws ShoppingListException {

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

    public DataSource.Factory<Integer, ProductsList> getLists(Context context,
                                                                     @ProductsList.ProductListStatus int status,
                                                                     @ShoppingList.ProductsListSorting int sorting) throws ShoppingListException {

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
