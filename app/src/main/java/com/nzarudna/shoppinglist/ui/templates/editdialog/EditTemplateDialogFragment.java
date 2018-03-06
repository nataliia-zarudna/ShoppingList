package com.nzarudna.shoppinglist.ui.templates.editdialog;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.ui.ViewModelArrayAdapter;
import com.nzarudna.shoppinglist.ui.productlist.editproduct.CategoryItemViewModel;
import com.nzarudna.shoppinglist.ui.productlist.editproduct.UnitItemViewModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class EditTemplateDialogFragment extends DialogFragment {

    private static final String EXTRA_PRODUCT_TEMPLATE = "com.nzarudna.shoppinglist.ui.templates.editdialog.product_template";
    private static final String ARG_PRODUCT_TEMPLATE = "com.nzarudna.shoppinglist.ui.templates.editdialog.product_template";
    private EditTemplateViewModel mViewModel;
    private ViewModelArrayAdapter<Unit> mUnitAdapter;
    private ViewModelArrayAdapter<Category> mCategoryAdapter;

    public static EditTemplateDialogFragment newInstance() {
        return new EditTemplateDialogFragment();
    }

    public static EditTemplateDialogFragment newInstance(ProductTemplate template) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_PRODUCT_TEMPLATE, template);

        EditTemplateDialogFragment instance = new EditTemplateDialogFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(EditTemplateViewModel.class);
        ShoppingListApplication.getAppComponent().inject(mViewModel);

        ProductTemplate template = null;
        if (getArguments() != null) {
            template = getArguments().getParcelable(ARG_PRODUCT_TEMPLATE);
        }
        mViewModel.setItem(template);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(mViewModel.getDialogTitle())
                .setPositiveButton(R.string.save_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ProductTemplate template = mViewModel.getItem();
                        sendResponse(template);
                    }
                })
                .setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setView(getCustomView())
                .create();
    }

    private View getCustomView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewDataBinding dataBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_edit_template_dialog, null, false);
        dataBinding.setVariable(BR.viewModel, mViewModel);

        View fragmentView = dataBinding.getRoot();

        configUnitSpinner(fragmentView);
        configCategorySpinner(fragmentView);

        return fragmentView;
    }

    private void configUnitSpinner(View fragmentView) {
        final Spinner unitSpinner = fragmentView.findViewById(R.id.unit_spinner);

        mViewModel.getUnits().observe(this, new Observer<List<Unit>>() {
            @Override
            public void onChanged(@Nullable List<Unit> units) {

                mUnitAdapter =
                        new ViewModelArrayAdapter<>(getContext(), R.layout.item_base_list, units, UnitItemViewModel.class);
                unitSpinner.setAdapter(mUnitAdapter);

                int selectedUnitPosition = mViewModel.getTemplateUnitIndex(units);
                unitSpinner.setSelection(selectedUnitPosition);
            }
        });

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Unit selectedUnit = mUnitAdapter.getItem(position);
                mViewModel.setUnit(selectedUnit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void configCategorySpinner(View fragmentView) {
        final Spinner categorySpinner = fragmentView.findViewById(R.id.category_spinner);

        mViewModel.getCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {

                mCategoryAdapter =
                        new ViewModelArrayAdapter<>(getContext(), R.layout.item_base_list, categories, CategoryItemViewModel.class);
                categorySpinner.setAdapter(mCategoryAdapter);

                int selectedPosition = mViewModel.getTemplateCategoryIndex(categories);
                categorySpinner.setSelection(selectedPosition);
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Category category = mCategoryAdapter.getItem(position);
                mViewModel.setCategory(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void sendResponse(ProductTemplate template) {
        Intent responseIntent = new Intent();
        responseIntent.putExtra(EXTRA_PRODUCT_TEMPLATE, template);

        Fragment targetFragment = getTargetFragment();
        targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, responseIntent);
    }

    public static ProductTemplate getResultTemplate(Intent intent) {
        return (ProductTemplate) intent.getParcelableExtra(EXTRA_PRODUCT_TEMPLATE);
    }
}
