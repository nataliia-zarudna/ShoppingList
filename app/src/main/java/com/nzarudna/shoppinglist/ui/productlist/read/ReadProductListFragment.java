package com.nzarudna.shoppinglist.ui.productlist.read;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.ui.productlist.CategoryProductItemViewModel;
import com.nzarudna.shoppinglist.ui.productlist.ProductListFragment;
import com.nzarudna.shoppinglist.ui.productlist.ProductListViewModel;
import com.nzarudna.shoppinglist.ui.productlist.edit.EditProductListActivity;
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
        return ReadProductListViewModel.class;
    }

    @Override
    protected int getProductItemLayoutID() {
        return R.layout.item_product_product_list;
    }

    @Override
    protected void onLoadProductList(ProductList productList) {
        getActivity().setTitle(mViewModel.getListName());
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
                Intent intent = EditProductListActivity.newIntent(getActivity(), mViewModel.getProductListID());
                startActivity(intent);
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
