package com.nzarudna.shoppinglist.ui.productlists;

import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.SharedPreferencesConstants;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.exception.ShoppingListException;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

/**
 * Created by Nataliia on 19.01.2018.
 */

public class ProductListsViewModel extends RecyclerViewModel<ProductListWithStatistics> implements AsyncResultListener<ProductList> {

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

//    public void onSwipeProductListItem(UUID productListID) {
//        mProductListRepository.updateListStatus(productListID, ProductList.STATUS_ARCHIVED, new ToastAsyncResultListener(null));
//    }

    public void onClickCreateListBtn() {
        mProductListRepository.createNewList(this);
    }

    public void onClickCopyListBtn(UUID etalonListID) {
        mProductListRepository.copyList(etalonListID, this);
    }

    public LiveData<List<ProductList>> getAllLists() {
        return mProductListRepository.getAllLists();
    }

    @Override
    public void onAsyncSuccess(ProductList productList) {
        if (mProductListViewModelObserver != null) {
            mProductListViewModelObserver.startEditProductListActivity(productList.getListID());
        }
    }

    @Override
    public void onAsyncError(Exception e) {

    }

    @Override
    public void onFABClick() {
        if (mProductListViewModelObserver != null) {
            mProductListViewModelObserver.showFABMenu();
        }
    }

    public interface ProductListViewModelObserver {

        void startEditProductListActivity(UUID productListID);

        void showFABMenu();
    }
}
