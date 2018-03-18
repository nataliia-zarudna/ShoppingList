package com.nzarudna.shoppinglist.ui.archivedproductlists;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
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
    protected int getLayoutResID() {
        return R.layout.fragment_recycler_view;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.archived_lists_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_name:
                mViewModel.setSorting(ProductListRepository.SORT_LISTS_BY_NAME);
                loadItems();
                return true;
            case R.id.sort_by_modified_at:
                mViewModel.setSorting(ProductListRepository.SORT_LISTS_BY_MODIFIED_AT);
                loadItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getItemContextMenuResID() {
        return R.menu.archived_list_item_context_menu;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restore_menu_item:
                getCurrentItemViewModel().restoreItem();
                return true;
        }
        return super.onContextItemSelected(item);
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
