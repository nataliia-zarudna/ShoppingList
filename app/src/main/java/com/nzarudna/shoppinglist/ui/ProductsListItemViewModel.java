package com.nzarudna.shoppinglist.ui;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.nzarudna.shoppinglist.product.ProductsList;
import com.nzarudna.shoppinglist.product.ShoppingList;
import com.nzarudna.shoppinglist.product.ShoppingListRepository;

import javax.inject.Inject;

/**
 * View Model for item of product lists
 */

public class ProductsListItemViewModel extends BaseObservable {

    private static final String LOG = "ProductsListItemVM";

    @Bindable
    private ProductsList mProductsList;

    public String getListName() {
        Log.d(LOG, "getListName => " + (mProductsList != null ? mProductsList.getName() : ""));

        return mProductsList != null ? mProductsList.getName() : "";
    }

    public void setProductsList(ProductsList productsList) {
        Log.d(LOG, "setProductsList => " + (productsList != null ? productsList.getName() : ""));

        this.mProductsList = productsList;
        notifyChange();
    }

    public ProductsList getProductsList() {
        return mProductsList;
    }
}
