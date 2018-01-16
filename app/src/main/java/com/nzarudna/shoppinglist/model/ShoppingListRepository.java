package com.nzarudna.shoppinglist.model;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.content.Context;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.dao.DaoFactory;
import com.nzarudna.shoppinglist.model.dao.ProductDao;
import com.nzarudna.shoppinglist.model.dao.ProductsListDao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.nzarudna.shoppinglist.model.ShoppingList.*;

/**
 * Shopping List Repository
 */
@Singleton
public class ShoppingListRepository {

    private ProductsListDao mProductsListDao;

    @Inject
    public ShoppingListRepository(ProductsListDao productsListDao) {
        mProductsListDao = productsListDao;
    }

    public ShoppingList createList(Context context) {

        ProductsListDao productsListDao = DaoFactory.getInstance().getProductsListDao(context);
        int productsListID = (int) productsListDao.insert(createProductsList(context));
        LiveData<ProductsList> productsList = productsListDao.findByID(productsListID);

        return new ShoppingList(context, productsList, productsListID);
    }

    private ProductsList createProductsList(Context context) {
        ProductsList productsList = new ProductsList();

        String defaultName = context.getString(R.string.default_list_name);
        productsList.setName(defaultName);

        int selfUserID = UserManager.getSelfUserID(context);
        productsList.setCreatedBy(selfUserID);

        return productsList;
    }


    public ShoppingList copyList(Context context, int etalonListID) throws ShoppingListException {

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

    private void copyProductsFromList(Context context, int fromListID, int toListID) throws ShoppingListException {

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

    public static DataSource.Factory<Integer, ProductsList> getLists(Context context,
                                                                     @ProductsList.ProductListStatus int status,
                                                                     @ShoppingList.ProductsListSorting int sorting) throws ShoppingListException {

        ProductsListDao productsListDao = DaoFactory.getInstance().getProductsListDao(context);
        switch (sorting) {
            case SORT_LISTS_BY_NAME:
                return productsListDao.findByStatusSortByName(status);
            case SORT_LISTS_BY_CREATED_AT:
                return productsListDao.findByStatusSortByCreatedAtDesc(status);
            case SORT_LISTS_BY_CREATED_BY:
                return productsListDao.findByStatusSortByCreatedByAndName(status);
            case SORT_LISTS_BY_ASSIGNED:
                return productsListDao.findByStatusSortByAssignedAndName(status);
            case SORT_LISTS_BY_MODIFIED_AT:
                return productsListDao.findByStatusSortByModifiedAtDesc(status);

            default:
                throw new ShoppingListException("Unknown sorting " + sorting);
        }
    }
}
