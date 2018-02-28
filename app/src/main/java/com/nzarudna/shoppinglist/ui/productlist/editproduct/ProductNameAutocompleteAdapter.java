package com.nzarudna.shoppinglist.ui.productlist.editproduct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;

import java.util.List;

/**
 * Created by nsirobaba on 2/27/18.
 */

public class ProductNameAutocompleteAdapter extends BaseAdapter implements Filterable {

    private List<ProductTemplate> templates;
    private Context mContext;

    public ProductNameAutocompleteAdapter(Context context) {
        this.mContext = context;
    }

    public void setTemplates(final List<ProductTemplate> templates) {
        ProductNameAutocompleteAdapter.this.templates = templates;

        notifyDataSetChanged();
        notifyDataSetInvalidated();
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
            view = LayoutInflater.from(mContext).inflate(R.layout.autocomplete_view, viewGroup, false);
        }

        //TODO: add data binding
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