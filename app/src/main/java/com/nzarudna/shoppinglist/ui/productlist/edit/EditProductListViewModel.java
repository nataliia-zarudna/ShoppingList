package com.nzarudna.shoppinglist.ui.productlist.edit;

import android.util.Log;

import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.ui.productlist.ProductListViewModel;

/**
 * Created by nsirobaba on 1/22/18.
 */

public class EditProductListViewModel extends ProductListViewModel {

    private static final String TAG = "EditProductListVM";

    public void onListNameChanged(String newName) {
        Log.d(TAG, "onListNameChanged " + newName);
        if (mProductList != null && !mProductList.getName().equals(newName)) {
            mProductList.setName(newName);
            mShoppingList.updateProductList(mProductList);
        }
    }

    public String getListName() {
        if (mShoppingList != null) {
            ProductList productList = mShoppingList.getListData().getValue();
            return productList != null ? productList.getName() : "";
        }
        return "";
    }
}
