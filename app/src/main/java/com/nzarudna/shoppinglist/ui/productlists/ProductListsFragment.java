package com.nzarudna.shoppinglist.ui.productlists;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;
import com.nzarudna.shoppinglist.ui.fabdialog.FABsDialog;
import com.nzarudna.shoppinglist.ui.productlist.edit.EditProductListActivity;
import com.nzarudna.shoppinglist.ui.productlist.read.ReadProductListActivity;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerViewFragment;
import com.nzarudna.shoppinglist.ui.recyclerui.EditDialogViewModel;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;

/**
 * Created by Nataliia on 11.03.2018.
 */

public class ProductListsFragment
        extends BaseRecyclerViewFragment<ProductListWithStatistics, ProductListsViewModel, ProductListItemViewModel>
        implements
        ProductListItemViewModel.ProductListItemViewModelObserver,
        ProductListsViewModel.ProductListViewModelObserver {

    private static final int REQUEST_CODE_LIST_TO_COPY = 1;

    public static Fragment getInstance() {
        return new ProductListsFragment();
    }

    @Override
    protected int getItemLayoutResID() {
        return R.layout.item_product_list;
    }

    @Override
    protected int getItemContextMenuResID() {
        return R.menu.product_list_item_menu;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.archive_list_item:
                getCurrentItemViewModel().onArchiveMenuItemSelected();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_LIST_TO_COPY) {
            UUID listID = CopyListDialogFragment.getListID(data);
            mViewModel.onClickCopyListBtn(listID);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.product_lists_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort_by_name:
                mViewModel.setSorting(ProductListRepository.SORT_LISTS_BY_NAME);
                loadItems();
                return true;
            case R.id.sort_by_created_by:
                mViewModel.setSorting(ProductListRepository.SORT_LISTS_BY_CREATED_BY);
                loadItems();
                return true;
            case R.id.sort_by_created_at:
                mViewModel.setSorting(ProductListRepository.SORT_LISTS_BY_CREATED_AT);
                loadItems();
                return true;
            case R.id.sort_by_modified_at:
                mViewModel.setSorting(ProductListRepository.SORT_LISTS_BY_MODIFIED_AT);
                loadItems();
                return true;
            case R.id.sort_by_assigned:
                mViewModel.setSorting(ProductListRepository.SORT_LISTS_BY_ASSIGNED);
                loadItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected ProductListsViewModel getFragmentViewModel() {
        mViewModel = ViewModelProviders.of(this).get(ProductListsViewModel.class);
        mViewModel.setProductListViewModelObserver(this);
        ShoppingListApplication.getAppComponent().inject(mViewModel);
        return mViewModel;
    }

    @Override
    protected ProductListItemViewModel getListItemViewModel() {
        ProductListItemViewModel itemViewModel = new ProductListItemViewModel();
        ShoppingListApplication.getAppComponent().inject(itemViewModel);
        itemViewModel.setProductListItemViewModelObserver(this);
        return itemViewModel;
    }

    @Override
    protected EditDialogViewModel getEditDialogViewModel() {
        return null;
    }

    @Override
    public void openEditItemDialog(ProductListWithStatistics item) {
        startEditProductListActivity(item.getListID());
    }

    @Override
    public void startProductListActivity(UUID productListID) {
        Intent intent = ReadProductListActivity.newIntent(getActivity(), productListID);
        startActivity(intent);
    }

    @Override
    public void startEditProductListActivity(UUID productListID) {
        Intent intent = EditProductListActivity.newIntent(getActivity(), productListID);
        startActivity(intent);
    }

    @Override
    public void showFABMenu() {
        FABsDialog.newInstance()
                .addFAB(R.id.fab_copy_list, R.string.copy_list_title, R.drawable.ic_content_copy_black,
                        view -> {
                            CopyListDialogFragment copyListFragment = new CopyListDialogFragment();
                            copyListFragment.setTargetFragment(ProductListsFragment.this, REQUEST_CODE_LIST_TO_COPY);
                            copyListFragment.show(getFragmentManager(), CopyListDialogFragment.class.getSimpleName());
                        })
                .addFAB(R.id.fab_new_list, R.string.new_list_title, R.drawable.ic_add_black,
                        view -> mViewModel.onClickCreateListBtn())
                .show(getFragmentManager(), "FABsDialog");
    }

    @Override
    protected DiffUtil.ItemCallback<ProductListWithStatistics> getDiffCallback() {
        return new DiffUtil.ItemCallback<ProductListWithStatistics>() {
            @Override
            public boolean areItemsTheSame(@NonNull ProductListWithStatistics oldItem, @NonNull ProductListWithStatistics newItem) {
                return oldItem.getListID().equals(newItem.getListID());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ProductListWithStatistics oldItem, @NonNull ProductListWithStatistics newItem) {
                return oldItem.equals(newItem);
            }
        };
    }
}
