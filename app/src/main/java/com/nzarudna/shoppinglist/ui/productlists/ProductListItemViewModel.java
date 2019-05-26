package com.nzarudna.shoppinglist.ui.productlists;

import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
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


    private ProductListItemViewModelObserver mProductListItemViewModelObserver;

    public void setProductListItemViewModelObserver(ProductListItemViewModelObserver observer) {
        this.mProductListItemViewModelObserver = observer;
    }

    @Override
    public String getItemName() {
        return mItem.getProductList().getName();
    }

    public String getBoughtToAllText() {
        return mItem.getProductStatistics().getBoughtProductsCount() + "/" + mItem.getProductStatistics().getAllProductsCount();
    }

    @Override
    public void onItemClick() {
        if (mProductListItemViewModelObserver != null) {
            mProductListItemViewModelObserver.openProductListReadMode(mItem.getListID());
        }
    }

    @Override
    public void removeItem(AsyncListener listener) {
        mProductListRepository.removeList(mItem.getListID(), null);
    }

    /*public void onSwipeProductListItem() {
        mProductListRepository.updateListStatus(mItem.getListID());
    }*/

    public void onArchiveMenuItemSelected() {
        mProductListRepository.updateListStatus(mItem.getListID(), ProductList.STATUS_ARCHIVED, null);
    }

    public interface ProductListItemViewModelObserver {

        void openProductListReadMode(UUID productsListID);

        //void openProductListEditMode(UUID productsListID);
    }
}
