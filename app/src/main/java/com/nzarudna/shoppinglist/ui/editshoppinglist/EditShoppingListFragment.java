package com.nzarudna.shoppinglist.ui.editshoppinglist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.databinding.ToolbarEditTitleBinding;
import com.nzarudna.shoppinglist.product.ProductList;

/**
 * Created by Nataliia on 21.01.2018.
 */

public class EditShoppingListFragment extends Fragment {

    private static final String TAG = "EditShoppingListFragmen";

    private static final String ARG_PRODUCTS_LIST_ID = "products_list_id";

    private EditProductListViewModel mViewModel;
    private View mToolbarView;
    private View mFragmentView;

    public static EditShoppingListFragment getInstance(int productsListID) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PRODUCTS_LIST_ID, productsListID);

        EditShoppingListFragment instance = new EditShoppingListFragment();
        instance.setArguments(bundle);

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentView = inflater.inflate(R.layout.fragment_edit_shopping_list, container, false);

        int productsListID = getArguments().getInt(ARG_PRODUCTS_LIST_ID);
        mViewModel = ViewModelProviders.of(this).get(EditProductListViewModel.class);
        ShoppingListApplication.getAppComponent().inject(mViewModel);
        mViewModel.setProductListID(productsListID);

        if (mToolbarView != null) {
            ToolbarEditTitleBinding viewDataBinding = DataBindingUtil.bind(mToolbarView);
            viewDataBinding.setEditListViewModel(mViewModel);
        }

        mViewModel.getProductListData().observe(this, new Observer<ProductList>() {
            @Override
            public void onChanged(@Nullable ProductList productList) {
                mViewModel.setProductListData(productList);
            }
        });

        return mFragmentView;
    }

    public void setToolbar(View toolbarView) {
        mToolbarView = toolbarView;
    }
}
