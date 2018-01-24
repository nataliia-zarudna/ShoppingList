package com.nzarudna.shoppinglist.ui.lists;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.nzarudna.shoppinglist.product.ProductList;

/**
 * View Model for item of product lists
 */

public class ProductListItemViewModel extends BaseObservable {

    private static final String TAG = "ProductsListItemVM";

    @Bindable
    private ProductList mProductList;

    private ProductListItemViewModelObserver mObserver;

    public void setObserver(ProductListItemViewModelObserver observer) {
        this.mObserver = observer;
    }

    public String getListName() {
        Log.d(TAG, "getListName => " + (mProductList != null ? mProductList.getName() : ""));

        return mProductList != null ? mProductList.getName() : "";
    }

    public void setProductsList(ProductList productList) {
        Log.d(TAG, "setProductsList => " + (productList != null ? productList.getName() : ""));

        this.mProductList = productList;
        notifyChange();
    }

    public ProductList getProductsList() {
        return mProductList;
    }

    public void onListClick() {
        Log.d(TAG, "Click on list " + mProductList.getName());

        if (mObserver != null) {
            mObserver.startProductsListActivity(mProductList.getListID());
        }
    }

    public interface ProductListItemViewModelObserver {

        void startProductsListActivity(int productsListID);
    }
}
