package com.nzarudna.shoppinglist.ui.shoppinglist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by Nataliia on 21.01.2018.
 */

public class ShoppingListFragment extends Fragment {

    private static final String TAG = "ShoppingListFragment";

    private static final String ARG_PRODUCTS_LIST_ID = "products_list_id";

    public static ShoppingListFragment getInstance(int productsListID) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PRODUCTS_LIST_ID, productsListID);

        ShoppingListFragment instance = new ShoppingListFragment();
        instance.setArguments(bundle);

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int productsListID = getArguments().getInt(ARG_PRODUCTS_LIST_ID);

        Log.d(TAG, "list id " + productsListID);
    }
}
