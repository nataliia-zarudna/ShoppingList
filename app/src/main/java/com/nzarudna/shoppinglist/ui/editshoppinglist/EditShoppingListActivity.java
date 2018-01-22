package com.nzarudna.shoppinglist.ui.editshoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ui.lists.SingleFragmentActivity;

/**
 * Created by Nataliia on 21.01.2018.
 */

public class EditShoppingListActivity extends SingleFragmentActivity {

    private static final String EXTRA_PRODUCTS_LIST_ID = "extra_products_list_id";

    private EditShoppingListFragment mEditShoppingListFragment;

    public static Intent newIntent(Context packageContext, int productsListID) {
        Intent intent = new Intent(packageContext, EditShoppingListActivity.class);
        intent.putExtra(EXTRA_PRODUCTS_LIST_ID, productsListID);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.toolbar_edit_title);
        actionBar.setDisplayShowCustomEnabled(true);

        mEditShoppingListFragment.setToolbar(actionBar.getCustomView());
    }

    @Override
    protected Fragment getFragment() {

        int productsListID = getIntent().getIntExtra(EXTRA_PRODUCTS_LIST_ID, 0);
        mEditShoppingListFragment = EditShoppingListFragment.getInstance(productsListID);
        return mEditShoppingListFragment;
    }
}
