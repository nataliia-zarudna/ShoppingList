package com.nzarudna.shoppinglist.ui.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.IntDef;

import com.nzarudna.shoppinglist.persistence.CategoryProductItem;
import com.nzarudna.shoppinglist.product.Product;
import com.nzarudna.shoppinglist.product.ShoppingList;
import com.nzarudna.shoppinglist.product.ShoppingListException;
import com.nzarudna.shoppinglist.product.ShoppingListRepository;

import javax.inject.Inject;

/**
 * Created by Nataliia on 28.01.2018.
 */

public class ShoppingListViewModel extends ViewModel {

    @Inject
    ShoppingListRepository mShoppingListRepository;

    private int mProductListID;
    private ShoppingList mShoppingList;

    public void setProductListID(int productListID) {
        this.mProductListID = productListID;

        mShoppingList = mShoppingListRepository.getList(productListID);
    }

    public LiveData<PagedList<CategoryProductItem>> getProducts(int productSort, int pageSize) {

        DataSource.Factory<Integer, CategoryProductItem> productsFactory = null;
        try {
            productsFactory = mShoppingList.getProducts(productSort, true);
        } catch (ShoppingListException e) {
            //TODO: handle error
            e.printStackTrace();
        }
        return new LivePagedListBuilder<>(productsFactory, pageSize).build();
    }
}
