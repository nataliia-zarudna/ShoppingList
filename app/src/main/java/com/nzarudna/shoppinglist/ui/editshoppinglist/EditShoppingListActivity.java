package com.nzarudna.shoppinglist.ui.editshoppinglist;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.nzarudna.shoppinglist.ui.lists.SingleFragmentActivity;

/**
 * Created by Nataliia on 21.01.2018.
 */

public class EditShoppingListActivity extends SingleFragmentActivity {

    private static final String EXTRA_PRODUCTS_LIST_ID = "extra_products_list_id";

    public static Intent newIntent(Context packageContext, int productsListID) {
        Intent intent = new Intent(packageContext, EditShoppingListActivity.class);
        intent.putExtra(EXTRA_PRODUCTS_LIST_ID, productsListID);
        return intent;
    }

    @Override
    protected Fragment getFragment() {

        int productsListID = getIntent().getIntExtra(EXTRA_PRODUCTS_LIST_ID, 0);
        return EditShoppingListFragment.getInstance(productsListID);
    }
}
