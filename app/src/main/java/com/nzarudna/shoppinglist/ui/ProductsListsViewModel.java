package com.nzarudna.shoppinglist.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nzarudna.shoppinglist.product.ProductsList;
import com.nzarudna.shoppinglist.product.ShoppingList;
import com.nzarudna.shoppinglist.product.ShoppingListException;
import com.nzarudna.shoppinglist.product.ShoppingListRepository;

import javax.inject.Inject;

/**
 * Created by Nataliia on 19.01.2018.
 */

public class ProductsListsViewModel extends ViewModel {

    @Inject
    ShoppingListRepository mShoppingListRepository;

    public LiveData<PagedList<ProductsList>> getList(int pageSize) {
        try {
            DataSource.Factory<Integer, ProductsList> listFactory
                    = mShoppingListRepository.getLists(ProductsList.STATUS_ACTIVE, ShoppingList.SORT_LISTS_BY_NAME);

            return new LivePagedListBuilder<Integer, ProductsList>(listFactory, pageSize).build();

        } catch (ShoppingListException e) {
            //TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }
}
