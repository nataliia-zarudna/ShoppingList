package com.nzarudna.shoppinglist.ui.productlist;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.nzarudna.shoppinglist.ui.SingleFragmentActivity;

/**
 * Created by Nataliia on 21.01.2018.
 */

public class ProductListActivity extends SingleFragmentActivity {

    private static final String EXTRA_PRODUCTS_LIST_ID = "extra_products_list_id";

    public static Intent newIntent(Context packageContext, int productListID) {
        Intent intent = new Intent(packageContext, ProductListActivity.class);
        intent.putExtra(EXTRA_PRODUCTS_LIST_ID, productListID);
        return intent;
    }

    @Override
    protected Fragment getFragment() {

        int productListID = getIntent().getIntExtra(EXTRA_PRODUCTS_LIST_ID, 0);
        return ProductListFragment.getInstance(productListID);
    }
}
