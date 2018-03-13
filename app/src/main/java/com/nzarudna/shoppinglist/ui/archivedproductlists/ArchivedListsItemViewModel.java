package com.nzarudna.shoppinglist.ui.archivedproductlists;

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

    @Override
    public String getItemName() {
        return mItem.getName();
    }

    @Override
    public void removeItem() {
        mProductListRepository.removeList(mItem.getListID());
    }
}
