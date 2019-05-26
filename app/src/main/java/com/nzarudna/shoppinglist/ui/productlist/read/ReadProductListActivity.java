package com.nzarudna.shoppinglist.ui.productlist.read;

import android.content.Context;
import android.content.Intent;

import com.nzarudna.shoppinglist.ui.SingleFragmentActivity;

import java.util.UUID;

import androidx.fragment.app.Fragment;

/**
 * Created by Nataliia on 21.01.2018.
 */

public class ReadProductListActivity extends SingleFragmentActivity {

    private static final String EXTRA_PRODUCTS_LIST_ID = "products_list_id";

    public static Intent newIntent(Context packageContext, UUID productListID) {
        Intent intent = new Intent(packageContext, ReadProductListActivity.class);
        intent.putExtra(EXTRA_PRODUCTS_LIST_ID, productListID);
        return intent;
    }

    @Override
    protected Fragment getFragment() {

        UUID productListID = (UUID) getIntent().getSerializableExtra(EXTRA_PRODUCTS_LIST_ID);
        return null;//ReadProductListFragment.getInstance(productListID);
    }
}
