package com.nzarudna.shoppinglist.ui.archivedproductlists;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;

import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.ui.productlist.edit.EditProductListActivity;
import com.nzarudna.shoppinglist.ui.productlist.read.ReadProductListActivity;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerViewFragment;
import com.nzarudna.shoppinglist.ui.recyclerui.EditDialogViewModel;

/**
 * Created by nsirobaba on 3/13/18.
 */

public class ArchivedListsFragment
        extends BaseRecyclerViewFragment<ProductList, ArchivedListsViewModel, ArchivedListsItemViewModel>
        implements ArchivedListsItemViewModel.ArchivedListsItemViewModelObserver {

    public static ArchivedListsFragment newInstance() {
        return new ArchivedListsFragment();
    }

    @Override
    protected ArchivedListsViewModel getFragmentViewModel() {
        ArchivedListsViewModel viewModel = ViewModelProviders.of(this).get(ArchivedListsViewModel.class);
        ShoppingListApplication.getAppComponent().inject(viewModel);
        return viewModel;
    }

    @Override
    protected ArchivedListsItemViewModel getListItemViewModel() {
        ArchivedListsItemViewModel itemViewModel = new ArchivedListsItemViewModel();
        itemViewModel.setArchivedListsItemViewModelObserver(this);
        ShoppingListApplication.getAppComponent().inject(itemViewModel);
        return itemViewModel;
    }

    @Override
    public void openEditItemDialog(ProductList item) {
        Intent intent = EditProductListActivity.newIntent(getActivity(), item.getListID());
        startActivity(intent);
    }

    @Override
    public void openReadProductListFragment(ProductList productList) {
        Intent intent = ReadProductListActivity.newIntent(getActivity(), productList.getListID());
        startActivity(intent);
    }

    @Override
    protected EditDialogViewModel<ProductList> getEditDialogViewModel() {
        return null;
    }

    @Override
    protected DiffCallback<ProductList> getDiffCallback() {
        return new DiffCallback<ProductList>() {
            @Override
            public boolean areItemsTheSame(@NonNull ProductList oldItem, @NonNull ProductList newItem) {
                return oldItem.getListID().equals(newItem.getListID());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ProductList oldItem, @NonNull ProductList newItem) {
                return oldItem.equals(newItem);
            }
        };
    }
}
