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

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by nsirobaba on 1/22/18.
 */

public class EditProductListViewModel extends ViewModel implements Observable {

    private static final String TAG = "EditProductListVM";

    private PropertyChangeRegistry mRegistry = new PropertyChangeRegistry();

    @Inject
    ProductListRepository mProductListRepository;

    private ShoppingList mShoppingList;

    @Bindable
    private ProductList mProductList;

    public void setProductListID(UUID productListID) {

        mShoppingList = mProductListRepository.getShoppingList(productListID);
    }

    public LiveData<ProductList> getProductListData() {
        return mShoppingList.getListData();
    }

    public void setProductListData(ProductList productList) {
        mProductList = productList;
        mRegistry.notifyChange(this, BR._all);
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

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mRegistry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mRegistry.remove(onPropertyChangedCallback);
    }
}
