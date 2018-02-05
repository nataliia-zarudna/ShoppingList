package com.nzarudna.shoppinglist.ui.productlists;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ui.SingleFragmentActivity;

public class ProductListsActivity extends SingleFragmentActivity {

    private static final String TAG = "ProductListsActivity";

    @Override
    protected Fragment getFragment() {
        return ProductListsFragment.getInstance();
    }
}
