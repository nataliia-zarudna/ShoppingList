package com.nzarudna.shoppinglist.ui.productlists;

import android.util.Log;

import com.nzarudna.shoppinglist.ui.NavigationSingleFragmentActivity;

import java.util.UUID;

import androidx.fragment.app.Fragment;

public class ProductListsActivity extends NavigationSingleFragmentActivity {

    private static final String TAG = "ProductListsActivity";

    @Override
    protected Fragment getFragment() {

        for (int i = 0; i < 15; i++) {
            Log.d("GenerateUUID", UUID.randomUUID().toString());
        }

        return ProductListsFragment.getInstance();
    }
}
