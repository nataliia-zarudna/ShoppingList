package com.nzarudna.shoppinglist.ui.productlist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;

import java.util.List;
import java.util.UUID;

/**
 * Created by Nataliia on 26.02.2018.
 */

public class EditProductDialogFragment extends DialogFragment {

    private static final String ARG_PRODUCT_ID = "productID";

    private EditProductViewModel mViewModel;

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
        final NameAutocompleteAdapter adapter
                = new NameAutocompleteAdapter(getActivity(), R.layout.autocomplete_view, R.id.autocomplete_name);
        nameView.setAdapter(adapter);

        mViewModel.getNameAutocompleteList().observe(this, new Observer<List<ProductTemplate>>() {
            @Override
            public void onChanged(@Nullable List<ProductTemplate> productTemplates) {
                String[] names = new String[productTemplates.size()];
                for (int i = 0; i < productTemplates.size(); i++) {
                    names[i] = productTemplates.get(i).getName();
                }
                adapter.templates = names;
                adapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    private class NameViewHolder extends RecyclerView.ViewHolder {

        public NameViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class NameAutocompleteAdapter extends ArrayAdapter<String> {

        private String[] templates;

        public NameAutocompleteAdapter(@NonNull Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }
    }
}
