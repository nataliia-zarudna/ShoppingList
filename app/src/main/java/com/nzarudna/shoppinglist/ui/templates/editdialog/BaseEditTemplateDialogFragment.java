package com.nzarudna.shoppinglist.ui.templates.editdialog;

import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.ui.ViewModelArrayAdapter;
import com.nzarudna.shoppinglist.ui.productlist.editproduct.CategoryItemViewModel;
import com.nzarudna.shoppinglist.ui.productlist.editproduct.UnitItemViewModel;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseEditItemDialogFragment;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

/**
 * Created by Nataliia on 06.03.2018.
 */

public abstract class BaseEditTemplateDialogFragment<T extends Parcelable, VM extends BaseEditTemplateViewModel<T>>
        extends BaseEditItemDialogFragment<T, VM> {

    private ViewModelArrayAdapter<Unit> mUnitAdapter;
    private ViewModelArrayAdapter<Category> mCategoryAdapter;

    @Override
    protected View getCustomView() {

        View fragmentView = super.getCustomView();

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
                mUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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

        mViewModel.getCategories().observe(this, categories -> {

            mCategoryAdapter =
                    new ViewModelArrayAdapter<>(getContext(), R.layout.item_base_list, categories, CategoryItemViewModel.class);
            categorySpinner.setAdapter(mCategoryAdapter);

            int selectedPosition = mViewModel.getTemplateCategoryIndex(categories);
            categorySpinner.setSelection(selectedPosition);
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Category category = mCategoryAdapter.getItem(position);
                mViewModel.setCategoryID(category.getCategoryID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
