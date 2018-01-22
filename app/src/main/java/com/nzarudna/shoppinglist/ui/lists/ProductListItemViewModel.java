package com.nzarudna.shoppinglist.ui.lists;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.nzarudna.shoppinglist.product.ProductsList;

/**
 * View Model for item of product lists
 */

public class ProductListItemViewModel extends BaseObservable {

    private static final String TAG = "ProductsListItemVM";

    @Bindable
    private ProductsList mProductsList;

    private ProductListItemViewModelObserver mObserver;

    public void setObserver(ProductListItemViewModelObserver observer) {
        this.mObserver = observer;
    }

    public String getListName() {
        Log.d(TAG, "getListName => " + (mProductsList != null ? mProductsList.getName() : ""));

        return mProductsList != null ? mProductsList.getName() : "";
    }

    public void setProductsList(ProductsList productsList) {
        Log.d(TAG, "setProductsList => " + (productsList != null ? productsList.getName() : ""));

        this.mProductsList = productsList;
        notifyChange();
    }

    public ProductsList getProductsList() {
        return mProductsList;
    }

    public void onListClick() {
        Log.d(TAG, "Click on list " + mProductsList.getName());

        if (mObserver != null) {
            mObserver.startProductsListActivity(mProductsList.getListID());
        }
    }

    public interface ProductListItemViewModelObserver {

        void startProductsListActivity(int productsListID);
    }
}
