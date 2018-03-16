package com.nzarudna.shoppinglist.ui.templates.editdialog;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class EditTemplateDialogFragment extends BaseEditTemplateDialogFragment<CategoryTemplateItem, EditTemplateViewModel> {

    //private ViewModelArrayAdapter<Unit> mUnitAdapter;
    //private ViewModelArrayAdapter<Category> mCategoryAdapter;

    public static EditTemplateDialogFragment newInstance() {
        return new EditTemplateDialogFragment();
    }

    /*@Override
    protected EditTemplateViewModel getViewModel() {
        EditTemplateViewModel viewModel = ViewModelProviders.of(this).get(EditTemplateViewModel.class);
        ShoppingListApplication.getAppComponent().inject(viewModel);
        return viewModel;
    }*/

    @Override
    protected int getDialogFragmentResID() {
        return R.layout.fragment_edit_template_dialog;
    }

    /*@Override
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
    }*/
}
