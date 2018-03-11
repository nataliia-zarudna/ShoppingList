package com.nzarudna.shoppinglist.ui.productlists;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewModel;

import java.util.UUID;

import javax.inject.Inject;

/**
 * View Model for item of product lists
 */

public class ProductListItemViewModel extends RecyclerItemViewModel<ProductListWithStatistics> {

    private static final String TAG = "ProductListItemVM";

    @Inject
    ProductListRepository mProductListRepository;


    private ProductListItemViewModelObserver mObserver;

    public void setObserver(ProductListItemViewModelObserver observer) {
        this.mObserver = observer;
    }

    @Override
    public String getItemName() {
        return mItem.getName();
    }

    public String getBoughtToAllText() {
        int allProductsCount = mItem.getToBuyProductsCount() +
                mItem.getAbsentProductsCount() +
                mItem.getBoughtProductsCount();
        return mItem.getBoughtProductsCount() + "/" + allProductsCount;
    }

    public void onListClick() {
        if (mObserver != null) {
            mObserver.startProductListActivity(mItem.getListID());
        }
    }

    @Override
    public void removeItem() {

    }

    /*public void onSwipeProductListItem() {
        mProductListRepository.archiveList(mItem.getListID());
    }*/

    public void onDeleteMenuItemSelected() {
        mProductListRepository.archiveList(mItem.getListID());
    }

    public void onArchiveMenuItemSelected() {
        mProductListRepository.removeList(mItem.getListID());
    }

    public interface ProductListItemViewModelObserver {

        void startProductListActivity(UUID productsListID);

        //void startEditProductListActivity(UUID productsListID);
    }
}
