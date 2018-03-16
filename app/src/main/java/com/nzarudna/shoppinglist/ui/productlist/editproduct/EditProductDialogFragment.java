package com.nzarudna.shoppinglist.ui.productlist.editproduct;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.ui.templates.editdialog.BaseEditTemplateDialogFragment;

import java.util.List;
import java.util.UUID;

/**
 * Created by Nataliia on 26.02.2018.
 */

public class EditProductDialogFragment extends BaseEditTemplateDialogFragment<CategoryProductItem, EditProductViewModel> {

    private static final String TAG = "EditProductDF";

    private static final String ARG_LIST_ID = "listID";

    private ProductNameAutocompleteAdapter mNameAutocompleteAdapter;

    public static EditProductDialogFragment newInstance() {
        return new EditProductDialogFragment();
    }

    public void setListID(UUID listID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LIST_ID, listID);

        this.setArguments(args);
    }

    @Override
    protected EditProductViewModel getViewModel() {
        EditProductViewModel viewModel = new EditProductViewModel();
        ShoppingListApplication.getAppComponent().inject(viewModel);
        return viewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID listID = null;
        if (getArguments() != null) {
            listID = (UUID) getArguments().getSerializable(ARG_LIST_ID);
        }
        mViewModel.setListID(listID);
    }

    @Override
    protected int getDialogFragmentResID() {
        return R.layout.fragment_edit_product_dialog;
    }

    @Override
    protected View getCustomView() {
        View dialogView = super.getCustomView();

        configNameEditView(dialogView);

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
                mViewModel.setName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        nameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick template " + ((ProductTemplate) mNameAutocompleteAdapter.getItem(i)).getName());
                ProductTemplate template = ((ProductTemplate) mNameAutocompleteAdapter.getItem(i));
                mViewModel.onChooseProductTemplate(template);
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
