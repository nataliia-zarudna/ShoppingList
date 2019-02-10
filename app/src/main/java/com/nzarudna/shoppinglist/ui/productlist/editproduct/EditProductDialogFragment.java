package com.nzarudna.shoppinglist.ui.productlist.editproduct;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.ui.templates.editdialog.BaseEditTemplateDialogFragment;

import java.util.UUID;

/**
 * Created by Nataliia on 26.02.2018.
 */

public class EditProductDialogFragment extends BaseEditTemplateDialogFragment<CategoryProductItem, EditProductViewModel> {

    private static final String TAG = "EditProductDF";

    private static final String ARG_LIST_ID = "listID";

    private ProductNameAutocompleteAdapter mNameAutocompleteAdapter;
    private ConstraintLayout mDetailsView;
    private ImageView mDetailsSwitch;

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
        EditProductViewModel viewModel = ViewModelProviders.of(this).get(EditProductViewModel.class);
        ShoppingListApplication.getAppComponent().inject(viewModel);
        viewModel.init();
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

        mDetailsView = dialogView.findViewById(R.id.product_details);
        configNameEditView(dialogView);

        return dialogView;
    }

    private void configNameEditView(View dialogView) {

        AppCompatAutoCompleteTextView nameView = dialogView.findViewById(R.id.name);
        mNameAutocompleteAdapter = new ProductNameAutocompleteAdapter(getActivity());
        nameView.setAdapter(mNameAutocompleteAdapter);

//        nameView.setError("Some test error");

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

        nameView.setOnItemClickListener((adapterView, view, i, l) -> {
            Log.d(TAG, "onItemClick template " + ((ProductTemplate) mNameAutocompleteAdapter.getItem(i)).getName());
            ProductTemplate template = ((ProductTemplate) mNameAutocompleteAdapter.getItem(i));
            mViewModel.onChooseProductTemplate(template);
        });
    }

    private void loadNameAutocompleteValues(String filterValue) {
        mViewModel.getNameAutocompleteList(filterValue).observe(this, productTemplates -> {
            mNameAutocompleteAdapter.setTemplates(productTemplates);
        });
    }

    @Override
    protected View getCustomTitleView() {
        if (getActivity() == null) {
            return null;
        }

        View titleView = getActivity().getLayoutInflater().inflate(R.layout.title_edit_product, null, false);

        TextView title = titleView.findViewById(R.id.title);
        title.setText(mViewModel.getDialogTitle());

        mDetailsSwitch = titleView.findViewById(R.id.details_switch);
        mDetailsSwitch.setOnClickListener(this::onToggleDetailsView);

        setDetailsSwitchDrawable();

        return titleView;
    }

    private void setDetailsSwitchDrawable() {
        if (mViewModel.isDetailsShow()) {
            mDetailsSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_less_black_24dp));
        } else {
            mDetailsSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_more_black_24dp));
        }
    }

    private void onToggleDetailsView(View view) {
        // TODO: add animation

        mViewModel.toggleDetails();
        if (mViewModel.isDetailsShow()) {
            mDetailsView.setVisibility(View.VISIBLE);
        } else {
            mDetailsView.setVisibility(View.GONE);
        }
        setDetailsSwitchDrawable();
    }
}
