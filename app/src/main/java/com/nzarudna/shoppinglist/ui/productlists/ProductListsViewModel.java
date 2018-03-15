package com.nzarudna.shoppinglist.ui.productlists;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.SharedPreferencesConstants;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;
import com.nzarudna.shoppinglist.model.exception.ShoppingListException;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Nataliia on 19.01.2018.
 */

public class ProductListsViewModel extends RecyclerViewModel<ProductListWithStatistics>
        implements ProductListRepository.OnProductListCreateListener {

    @Inject
    ProductListRepository mProductListRepository;

    @Inject
    SharedPreferences mSharedPreferences;

    private ProductListViewModelObserver mProductListViewModelObserver;

    @ProductListRepository.ProductListSorting
    private int mSorting;

    public void setProductListViewModelObserver(ProductListViewModelObserver observer) {
        this.mProductListViewModelObserver = observer;
    }

    public void setSorting(int sorting) {
        this.mSorting = sorting;
    }

    @Override
    public LiveData<PagedList<ProductListWithStatistics>> getItems(int pageSize) {

        if (mSorting == 0) {
            mSorting = mSharedPreferences.getInt(SharedPreferencesConstants.DEFAULT_PRODUCT_LIST_SORTING,
                    ProductListRepository.SORT_LISTS_BY_NAME);
        } else {
            mSharedPreferences.edit()
                    .putInt(SharedPreferencesConstants.DEFAULT_PRODUCT_LIST_SORTING, mSorting)
                    .apply();
        }

        try {
            DataSource.Factory<Integer, ProductListWithStatistics> listFactory
                    = mProductListRepository.getListsWithStatistics(ProductList.STATUS_ACTIVE, mSorting);

            return new LivePagedListBuilder<>(listFactory, pageSize).build();

        } catch (ShoppingListException e) {
            //TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public void onSwipeProductListItem(UUID productListID) {
        mProductListRepository.archiveList(productListID);
    }

    public void onClickCreateListBtn() {
        mProductListRepository.createNewList(this);
    }

    public void onClickCopyListBtn(UUID etalonListID) {
        mProductListRepository.copyList(etalonListID, this);
    }

    @Override
    public void onCreateNewList(UUID productListID) {
        if (mProductListViewModelObserver != null) {
            mProductListViewModelObserver.startEditProductListActivity(productListID);
        }
    }

    public LiveData<List<ProductList>> getAllLists() {
        return mProductListRepository.getAllLists();
    }

    public interface ProductListViewModelObserver {

        void startEditProductListActivity(UUID productListID);
    }
}
