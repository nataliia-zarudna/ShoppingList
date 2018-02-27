package com.nzarudna.shoppinglist.ui.productlist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;

import java.util.List;
import java.util.UUID;

/**
 * Created by Nataliia on 26.02.2018.
 */

public class EditProductDialogFragment extends DialogFragment {

    private static final String TAG = "EditProductDF";

    private static final String ARG_PRODUCT_ID = "productID";

    private EditProductViewModel mViewModel;
    private ProductNameAutocompleteAdapter mAdapter;

    public static EditProductDialogFragment newInstance(UUID productID) {
        EditProductDialogFragment instance = new EditProductDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT_ID, productID);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(EditProductViewModel.class);
        ShoppingListApplication.getAppComponent().inject(mViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_product_dialog, container, false);

        AutoCompleteTextView nameView = rootView.findViewById(R.id.name);
        mAdapter = new ProductNameAutocompleteAdapter();
        nameView.setAdapter(mAdapter);

        loadList("");

        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        nameView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "Selected template " + mAdapter.templates.get(i).getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        nameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick template " + mAdapter.templates.get(i).getName());
            }
        });

        return rootView;
    }

    private void loadList(String filterValue) {
        mViewModel.getNameAutocompleteList(filterValue).observe(this, new Observer<List<ProductTemplate>>() {
            @Override
            public void onChanged(@Nullable List<ProductTemplate> productTemplates) {
                mAdapter.setTemplates(productTemplates);
            }
        });
    }

    private class ProductNameAutocompleteAdapter extends BaseAdapter implements Filterable {

        List<ProductTemplate> templates;

        public void setTemplates(final List<ProductTemplate> templates) {
            ProductNameAutocompleteAdapter.this.templates = templates;

            mAdapter.notifyDataSetChanged();
            mAdapter.notifyDataSetInvalidated();
        }

        @Override
        public int getCount() {
            return templates.size();
        }

        @Override
        public Object getItem(int i) {
            return templates.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.autocomplete_view, viewGroup, false);
            }

            TextView nameView = (TextView) view;
            ProductTemplate template = templates.get(i);
            nameView.setText(template.getName());

            return view;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    FilterResults filterResults = new FilterResults();
                    filterResults.count = templates.size();
                    filterResults.values = templates;

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                }

                @Override
                public CharSequence convertResultToString(Object resultValue) {
                    ProductTemplate template = (ProductTemplate) resultValue;
                    return template.getName();
                }
            };
        }
    }
}
