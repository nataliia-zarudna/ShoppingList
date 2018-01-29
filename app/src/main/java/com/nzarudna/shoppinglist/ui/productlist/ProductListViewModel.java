package com.nzarudna.shoppinglist.ui.productlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.ShoppingListException;

import javax.inject.Inject;

/**
 * Created by Nataliia on 28.01.2018.
 */

public class ProductListViewModel extends ViewModel {

    @Inject
    ProductListRepository mProductListRepository;

    private int mProductListID;
    private ShoppingList mShoppingList;

    public void setProductListID(int productListID) {
        this.mProductListID = productListID;

        mShoppingList = mProductListRepository.getShoppingList(productListID);
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
