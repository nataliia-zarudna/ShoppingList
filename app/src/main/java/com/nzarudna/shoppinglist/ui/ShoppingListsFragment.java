package com.nzarudna.shoppinglist.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.persistence.db.AppDatabase;
import com.nzarudna.shoppinglist.product.ProductsList;
import com.nzarudna.shoppinglist.product.ShoppingListRepository;

/**
 * Fragment with set of shopping lists
 */
public class ShoppingListsFragment extends Fragment {

    public static Fragment getInstance() {
        return new ShoppingListsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_shopping_lists, container, false);

        ProductsListsViewModel viewModel = ViewModelProviders.of(this).get(ProductsListsViewModel.class);
        ShoppingListApplication.getAppComponent().inject(viewModel);

        RecyclerView recyclerView = fragmentView.findViewById(R.id.lists_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final ProductsListAdapter listAdapter = new ProductsListAdapter();
        recyclerView.setAdapter(listAdapter);

        /*new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ProductsList productsList = new ProductsList();
                productsList.setName("abc");
                AppDatabase.getInstance(getActivity()).productsListDao().insert(productsList);
                return null;
            }
        }.execute();*/

        viewModel.getList(20).observe(this, new Observer<PagedList<ProductsList>>() {
            @Override
            public void onChanged(@Nullable PagedList<ProductsList> productsLists) {
                listAdapter.setList(productsLists);
            }
        });

        return fragmentView;
    }

    private class ProductsListViewHolder extends RecyclerView.ViewHolder {

        TextView mListNameText;

        public ProductsListViewHolder(View itemView) {
            super(itemView);
            mListNameText = itemView.findViewById(R.id.list_name);
        }

        public void bind(ProductsList productsList) {
            mListNameText.setText(productsList.getName());
        }
    }

    private class ProductsListAdapter extends PagedListAdapter<ProductsList, ProductsListViewHolder> {

        public ProductsListAdapter() {
            //super(DIFF_CALLBACK);
            super(new DiffCallback<ProductsList>() {
                @Override
                public boolean areItemsTheSame(@NonNull ProductsList oldItem, @NonNull ProductsList newItem) {
                    return oldItem.getListID() == newItem.getListID();
                }

                @Override
                public boolean areContentsTheSame(@NonNull ProductsList oldItem, @NonNull ProductsList newItem) {
                    return oldItem.equals(newItem);
                }
            });
        }

        @Override
        public ProductsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.item_shopping_list, parent, false);
            return new ProductsListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ProductsListViewHolder holder, int position) {
            ProductsList productsList = getItem(position);
            holder.bind(productsList);
        }

        /*private static final DiffCallback<ProductsList> DIFF_CALLBACK = new DiffCallback<ProductsList>() {
            @Override
            public boolean areItemsTheSame(@NonNull ProductsList oldItem, @NonNull ProductsList newItem) {
                return oldItem.getListID() == newItem.getListID();
            }

            @Override
            public boolean areContentsTheSame(@NonNull ProductsList oldItem, @NonNull ProductsList newItem) {
                return oldItem.equals(newItem);
            }
        };*/
    }
}
