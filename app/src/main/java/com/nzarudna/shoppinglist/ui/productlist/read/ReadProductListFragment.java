package com.nzarudna.shoppinglist.ui.productlist.read;

import android.os.Bundle;

import com.nzarudna.shoppinglist.ui.productlist.ProductListFragment;
import com.nzarudna.shoppinglist.ui.productlist.ProductListViewModel;
import com.nzarudna.shoppinglist.ui.productlist.edit.EditProductListViewModel;

import java.util.UUID;

/**
 * Created by Nataliia on 25.02.2018.
 */

public class ReadProductListFragment extends ProductListFragment {

    public static ProductListFragment getInstance(UUID productListID) {
        ReadProductListFragment instance = new ReadProductListFragment();
        instance.setProductListID(productListID);
        return instance;
    }

    @Override
    protected Class<? extends ProductListViewModel> getViewModelClass() {
        return EditProductListViewModel.class;
    }
}
