package com.nzarudna.shoppinglist.ui.archivedproductlists;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.model.exception.ShoppingListException;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;

import javax.inject.Inject;

/**
 * Created by nsirobaba on 3/13/18.
 */

public class ArchivedListsViewModel extends RecyclerViewModel<ProductList> {

    private static final String PREFERENCE_ARCHIVED_PRODUCT_LIST_SORTING = "archived_list_sorting";

    @Inject
    ProductListRepository mProductListRepository;
    @Inject
    SharedPreferences mSharedPreferences;

    private int mSorting;

    public void setSorting(int sorting) {
        this.mSorting = sorting;

        mSharedPreferences.edit()
                .putInt(PREFERENCE_ARCHIVED_PRODUCT_LIST_SORTING, sorting)
                .apply();
    }

    @Override
    public LiveData<PagedList<ProductList>> getItems(int pageSize) {
        if (mSorting == 0) {
            mSorting = mSharedPreferences.getInt(PREFERENCE_ARCHIVED_PRODUCT_LIST_SORTING, ProductListRepository.SORT_LISTS_BY_NAME);
        }

        try {
            DataSource.Factory<Integer, ProductList> productListFactory
                    = mProductListRepository.getLists(ProductList.STATUS_ARCHIVED, mSorting);
            return new LivePagedListBuilder(productListFactory, pageSize).build();

        } catch (ShoppingListException e) {
            //TODO: error handle
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean canCreateNewItem() {
        return false;
    }
}
