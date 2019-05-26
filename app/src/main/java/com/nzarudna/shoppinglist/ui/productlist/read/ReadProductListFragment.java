package com.nzarudna.shoppinglist.ui.productlist.read;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nzarudna.shoppinglist.NavGraphDirections;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.ui.productlist.CategoryProductItemViewModel;
import com.nzarudna.shoppinglist.ui.productlist.ProductListFragment;
import com.nzarudna.shoppinglist.ui.productlist.ProductListViewModel;
import com.nzarudna.shoppinglist.ui.productlist.edit.EditProductListActivity;
import com.nzarudna.shoppinglist.ui.productlist.edit.EditProductListFragment;

import java.util.UUID;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

/**
 * Created by Nataliia on 25.02.2018.
 */

public class ReadProductListFragment extends ProductListFragment {

    public static void navigateToReadProductListFragment(Fragment fragment, UUID productListID) {
        NavGraphDirections.OpenListReadMode openListReadModeDirection
                = NavGraphDirections.openListReadMode(productListID);
        NavHostFragment.findNavController(fragment).navigate(openListReadModeDirection);
    }

    @Override
    protected Class<? extends ProductListViewModel> getViewModelClass() {
        return ReadProductListViewModel.class;
    }

    @Override
    protected int getProductItemLayoutID() {
        return R.layout.item_product_product_list;
    }

    @Override
    protected void onLoadProductList(ProductList productList) {
        super.onLoadProductList(productList);
        getActivity().setTitle(mViewModel.getListName());
    }

    @Override
    protected void hideUsedSortingMenu(ProductList productList) {
        super.hideUsedSortingMenu(productList);
        mOptionsMenu.findItem(R.id.menu_item_apply_sort_by_name).setVisible(false);
        mOptionsMenu.findItem(R.id.menu_item_apply_sort_by_status).setVisible(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_item_edit_list).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_edit_list:
                EditProductListFragment.navigateToEditProductListFragment(this, mViewModel.getProductListID());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected CategoryProductItemViewModel getListItemViewModel() {
        return new ReadCategoryProductItemViewModel();
    }
}
