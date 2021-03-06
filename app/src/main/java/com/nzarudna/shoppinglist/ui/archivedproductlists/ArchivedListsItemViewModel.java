package com.nzarudna.shoppinglist.ui.archivedproductlists;

import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewModel;

import javax.inject.Inject;

/**
 * Created by nsirobaba on 3/13/18.
 */

public class ArchivedListsItemViewModel extends RecyclerItemViewModel<ProductList> {

    @Inject
    ProductListRepository mProductListRepository;

    private ArchivedListsItemViewModelObserver mArchivedListsItemViewModelObserver;

    public void setArchivedListsItemViewModelObserver(ArchivedListsItemViewModelObserver observer) {
        this.mArchivedListsItemViewModelObserver = observer;
    }

    @Override
    public String getItemName() {
        return mItem.getName();
    }

    @Override
    public void removeItem(AsyncListener listener) {
        mProductListRepository.removeList(mItem.getListID(), listener);
    }

    @Override
    public void onItemClick() {
        if (mArchivedListsItemViewModelObserver != null) {
            mArchivedListsItemViewModelObserver.openReadProductListFragment(mItem);
        }
    }

    public void restoreItem() {
        mProductListRepository.updateListStatus(mItem.getListID(), ProductList.STATUS_ACTIVE, null);
    }

    public interface ArchivedListsItemViewModelObserver {
        void openReadProductListFragment(ProductList productList);
    }
}
