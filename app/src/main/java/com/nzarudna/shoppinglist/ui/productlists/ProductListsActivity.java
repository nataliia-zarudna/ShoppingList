package com.nzarudna.shoppinglist.ui.productlists;

import android.support.v4.app.Fragment;

import com.nzarudna.shoppinglist.ui.NavigationSingleFragmentActivity;

public class ProductListsActivity extends NavigationSingleFragmentActivity {

    private static final String TAG = "ProductListsActivity";

    @Override
    protected Fragment getFragment() {
        return ProductListsFragment.getInstance();
    }
}
