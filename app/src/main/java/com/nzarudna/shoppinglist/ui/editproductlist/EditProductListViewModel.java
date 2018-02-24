package com.nzarudna.shoppinglist.ui.editproductlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.util.Log;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.ui.productlist.ProductListViewModel;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by nsirobaba on 1/22/18.
 */

public class EditProductListViewModel extends ProductListViewModel {

    private static final String TAG = "EditProductListVM";


    @Inject
    ProductListRepository mProductListRepository;

    private ShoppingList mShoppingList;

    public void setProductListID(UUID productListID) {

        mShoppingList = mProductListRepository.getShoppingList(productListID);
    }

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
