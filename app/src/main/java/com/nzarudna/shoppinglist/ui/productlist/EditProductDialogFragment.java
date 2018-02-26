package com.nzarudna.shoppinglist.ui.productlist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.product.Product;

import java.util.UUID;

/**
 * Created by Nataliia on 26.02.2018.
 */

public class EditProductDialogFragment extends DialogFragment {

    private static final String ARG_PRODUCT_ID = "productID";

    public static EditProductDialogFragment newInstance(UUID productID) {
        EditProductDialogFragment instance = new EditProductDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT_ID, productID);
        instance.setArguments(args);

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_product_dialog, container, false);
        return rootView;
    }
}
