package com.nzarudna.shoppinglist.ui.productlist.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ui.SingleFragmentActivity;

import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

/**
 * Created by Nataliia on 21.01.2018.
 */

public class EditProductListActivity extends SingleFragmentActivity {

    private static final String EXTRA_PRODUCTS_LIST_ID = "extra_products_list_id";

    private EditProductListFragment mEditProductListFragment;

    public static Intent newIntent(Context packageContext, UUID productListID) {
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

        if (mEditProductListFragment == null) {
            mEditProductListFragment =
                    (EditProductListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }
        mEditProductListFragment.setToolbar(actionBar.getCustomView());
    }

    @Override
    protected Fragment getFragment() {

        UUID productListID = (UUID) getIntent().getSerializableExtra(EXTRA_PRODUCTS_LIST_ID);
        mEditProductListFragment = EditProductListFragment.getInstance(productListID);
        return mEditProductListFragment;
    }
}
