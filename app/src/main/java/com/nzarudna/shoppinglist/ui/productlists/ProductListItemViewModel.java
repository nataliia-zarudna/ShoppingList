package com.nzarudna.shoppinglist.ui.productlists;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;

import java.util.UUID;

/**
 * View Model for item of product lists
 */

public class ProductListItemViewModel extends BaseObservable {

    private static final String TAG = "ProductListItemVM";

    @Bindable
    private ProductListWithStatistics mProductList;

    private ProductListItemViewModelObserver mObserver;

    public void setObserver(ProductListItemViewModelObserver observer) {
        this.mObserver = observer;
    }

    public String getListName() {
        Log.d(TAG, "getListName => " + (mProductList != null ? mProductList.getName() : ""));

        return mProductList != null ? mProductList.getName() : "";
    }

    public String getBoughtToAllText() {
        int allProductsCount = mProductList.getToBuyProductsCount() +
                mProductList.getAbsentProductsCount() +
                mProductList.getBoughtProductsCount();
        return mProductList.getBoughtProductsCount() + "/" + allProductsCount;
    }

    public void setProductList(ProductListWithStatistics productList) {
        Log.d(TAG, "setProductList => " + (productList != null ? productList.getName() : ""));

        this.mProductList = productList;
        notifyChange();
    }

    public ProductListWithStatistics getProductList() {
        return mProductList;
    }

    public void onListClick() {
        Log.d(TAG, "Click on list " + mProductList.getName());

        if (mObserver != null) {
            mObserver.startProductListActivity(mProductList.getListID());
        }
    }

    public interface ProductListItemViewModelObserver {

        void startProductListActivity(UUID productsListID);
    }
}
