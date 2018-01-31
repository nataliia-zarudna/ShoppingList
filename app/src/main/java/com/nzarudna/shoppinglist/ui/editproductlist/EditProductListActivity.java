package com.nzarudna.shoppinglist.ui.editproductlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ui.SingleFragmentActivity;

/**
 * Created by Nataliia on 21.01.2018.
 */

public class EditProductListActivity extends SingleFragmentActivity {

    private static final String EXTRA_PRODUCTS_LIST_ID = "extra_products_list_id";

    private EditProductListFragment mEditProductListFragment;

    public static Intent newIntent(Context packageContext, int productListID) {
        Intent intent = new Intent(packageContext, EditProductListActivity.class);
        intent.putExtra(EXTRA_PRODUCTS_LIST_ID, productListID);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.toolbar_edit_title);
        actionBar.setDisplayShowCustomEnabled(true);

        mEditProductListFragment.setToolbar(actionBar.getCustomView());
    }

    @Override
    protected Fragment getFragment() {

        int productListID = getIntent().getIntExtra(EXTRA_PRODUCTS_LIST_ID, 0);
        mEditProductListFragment = EditProductListFragment.getInstance(productListID);
        return mEditProductListFragment;
    }
}