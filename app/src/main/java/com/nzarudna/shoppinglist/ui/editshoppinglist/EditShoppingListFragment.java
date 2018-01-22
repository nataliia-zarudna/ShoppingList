package com.nzarudna.shoppinglist.ui.editshoppinglist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by Nataliia on 21.01.2018.
 */

public class EditShoppingListFragment extends Fragment {

    private static final String TAG = "EditShoppingListFragment";

    private static final String ARG_PRODUCTS_LIST_ID = "products_list_id";

    public static EditShoppingListFragment getInstance(int productsListID) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PRODUCTS_LIST_ID, productsListID);

        EditShoppingListFragment instance = new EditShoppingListFragment();
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
