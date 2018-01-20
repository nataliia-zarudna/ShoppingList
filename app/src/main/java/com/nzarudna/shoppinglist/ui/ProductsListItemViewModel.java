package com.nzarudna.shoppinglist.ui;

import android.util.Log;

import com.nzarudna.shoppinglist.product.ProductsList;
import com.nzarudna.shoppinglist.product.ShoppingList;
import com.nzarudna.shoppinglist.product.ShoppingListRepository;

import javax.inject.Inject;

/**
 * View Model for item of product lists
 */

public class ProductsListItemViewModel {

    private static final String LOG = "ProductsListItemVM";

    @Inject
    ShoppingListRepository shoppingListRepository;

    private ProductsList mProductsList;

    public String getListName() {
        Log.d(LOG, "getListName => " + (mProductsList != null ? mProductsList.getName() : ""));

        return mProductsList != null ? mProductsList.getName() : "";
    }

    public void setProductsList(ProductsList productsList) {
        this.mProductsList = productsList;
    }

    public void removeList() {
        shoppingListRepository.removeList(mProductsList);
    }
}
