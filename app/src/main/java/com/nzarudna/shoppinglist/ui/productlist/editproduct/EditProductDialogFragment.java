package com.nzarudna.shoppinglist.ui.productlist.editproduct;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.databinding.FragmentEditProductDialogBinding;
import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.ui.ViewModelArrayAdapter;

import java.util.List;
import java.util.UUID;

/**
 * Created by Nataliia on 26.02.2018.
 */

public class EditProductDialogFragment extends DialogFragment {

    private static final String TAG = "EditProductDF";

    private static final String ARG_PRODUCT = "productID";
    private static final String ARG_LIST_ID = "listID";

    private EditProductViewModel mViewModel;
    private ProductNameAutocompleteAdapter mNameAutocompleteAdapter;
    private ArrayAdapter<Unit> mUnitSpinnerAdapter;
    private ArrayAdapter<Category> mCategorySpinnerAdapter;

    public static EditProductDialogFragment newInstance(UUID listID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LIST_ID, listID);
        return newInstance(args);
    }

    public static EditProductDialogFragment newInstance(Product product) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_PRODUCT, product);
        return newInstance(args);
    }

    private static EditProductDialogFragment newInstance(Bundle args) {
        EditProductDialogFragment instance = new EditProductDialogFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(EditProductViewModel.class);
        ShoppingListApplication.getAppComponent().inject(mViewModel);

        Product product = null;
        UUID listID = null;
        if (getArguments() != null) {
            product = getArguments().getParcelable(ARG_PRODUCT);
            listID = (UUID) getArguments().getSerializable(ARG_LIST_ID);
        }
        if (product == null) {
            product = new Product(null);
            product.setListID(listID);
        }
        mViewModel.setProduct(product);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.save_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mViewModel.saveProduct();
                    }
                })
                .setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setView(getContainerView())
                .setTitle(mViewModel.getDialogTitle())
                .create();
    }

    private View getContainerView() {

        FragmentEditProductDialogBinding binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(),
                R.layout.fragment_edit_product_dialog, null, false);
        binding.setViewModel(mViewModel);

        View dialogView = binding.getRoot();

        configNameEditView(dialogView);
        configUnitSpinnerView(dialogView);
        configCategorySpinnerView(dialogView);

        return dialogView;
    }

    private void configNameEditView(View dialogView) {

        AppCompatAutoCompleteTextView nameView = dialogView.findViewById(R.id.name);
        mNameAutocompleteAdapter = new ProductNameAutocompleteAdapter(getActivity());
        nameView.setAdapter(mNameAutocompleteAdapter);

        loadNameAutocompleteValues("");

        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadNameAutocompleteValues(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        nameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick template " + ((ProductTemplate) mNameAutocompleteAdapter.getItem(i)).getName());
            }
        });
    }

    private void configUnitSpinnerView(View dialogView) {

        final AppCompatSpinner unitView = dialogView.findViewById(R.id.unit);

        mViewModel.getUnitList().observe(this, new Observer<List<Unit>>() {
            @Override
            public void onChanged(@Nullable List<Unit> units) {
                if (mUnitSpinnerAdapter == null && units != null) {
                    //TODO: add custom layout
                    mUnitSpinnerAdapter
                            = new ViewModelArrayAdapter<Unit>(EditProductDialogFragment.this, R.layout.item_base_list,
                            units, UnitItemViewModel.class);
                    unitView.setAdapter(mUnitSpinnerAdapter);
                }
            }
        });
    }

    private void configCategorySpinnerView(View dialogView) {

        final AppCompatSpinner categoryView = dialogView.findViewById(R.id.category);

        mViewModel.getCategoryList().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {
                if (mCategorySpinnerAdapter == null && categories != null) {
                    mCategorySpinnerAdapter
                            = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);
                    categoryView.setAdapter(mCategorySpinnerAdapter);
                }
            }
        });
    }

    private void loadNameAutocompleteValues(String filterValue) {
        mViewModel.getNameAutocompleteList(filterValue).observe(this, new Observer<List<ProductTemplate>>() {
            @Override
            public void onChanged(@Nullable List<ProductTemplate> productTemplates) {
                mNameAutocompleteAdapter.setTemplates(productTemplates);
            }
        });
    }
}