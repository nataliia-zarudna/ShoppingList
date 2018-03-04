package com.nzarudna.shoppinglist.ui.productlist.edit;

import android.support.annotation.Nullable;
import android.util.Log;

import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
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

    public void createProduct(Product newProduct, @Nullable ShoppingList.OnSaveProductCallback onSaveProductCallback) {
        mShoppingList.addProduct(newProduct, onSaveProductCallback);
    }
}
