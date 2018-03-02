package com.nzarudna.shoppinglist.ui.productlist.edit;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.databinding.ToolbarEditTitleBinding;
import com.nzarudna.shoppinglist.ui.productlist.ProductListFragment;
import com.nzarudna.shoppinglist.ui.productlist.ProductListViewModel;

import java.util.UUID;

/**
 * Created by Nataliia on 21.01.2018.
 */

public class EditProductListFragment extends ProductListFragment {

    private static final String TAG = "EditProductListFragment";

    private static final String ARG_PRODUCTS_LIST_ID = "products_list_id";

    private View mToolbarView;
    private View mFragmentView;

    public static EditProductListFragment getInstance(UUID productListID) {
        EditProductListFragment instance = new EditProductListFragment();
        instance.setProductListID(productListID);
        return instance;
    }

    @Override
    protected Class<? extends ProductListViewModel> getViewModelClass() {
        return EditProductListViewModel.class;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentView = inflater.inflate(R.layout.fragment_edit_product_list, container, false);

        if (mToolbarView != null) {
            ToolbarEditTitleBinding viewDataBinding = DataBindingUtil.bind(mToolbarView);
            viewDataBinding.setEditListViewModel((EditProductListViewModel) mViewModel);
        }

        initProductsRecyclerView(mFragmentView);

        return mFragmentView;
    }

    public void setToolbar(View toolbarView) {
        mToolbarView = toolbarView;
    }
}
