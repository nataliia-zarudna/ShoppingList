package com.nzarudna.shoppinglist.ui.productlists;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.nzarudna.shoppinglist.ui.SingleFragmentActivity;

public class ProductListsActivity extends SingleFragmentActivity {

    private static final String TAG = "ProductListsActivity";

    @Override
    protected Fragment getFragment() {
        return ProductListsFragment.getInstance();
    }
}
