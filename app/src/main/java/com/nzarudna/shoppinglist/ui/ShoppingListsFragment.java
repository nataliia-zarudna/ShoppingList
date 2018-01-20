package com.nzarudna.shoppinglist.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.nzarudna.shoppinglist.AppComponent;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.databinding.ListItemShoppingListBinding;
import com.nzarudna.shoppinglist.product.ProductsList;

/**
 * Fragment with set of shopping lists
 */
public class ShoppingListsFragment extends Fragment {

    private static final String LOG = "ShoppingListsFragment";

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

        final ProductsListsViewModel viewModel = ViewModelProviders.of(this).get(ProductsListsViewModel.class);
        ShoppingListApplication.getAppComponent().inject(viewModel);

        RecyclerView recyclerView = fragmentView.findViewById(R.id.lists_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT );
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(LOG, "onSwiped direction " + direction);
                ((ProductsListViewHolder) viewHolder).mBinding.getViewModel().removeList();
            }
        }).attachToRecyclerView(recyclerView);

        final ProductsListAdapter listAdapter = new ProductsListAdapter();
        recyclerView.setAdapter(listAdapter);

        viewModel.getList(20).observe(this, new Observer<PagedList<ProductsList>>() {
            @Override
            public void onChanged(@Nullable PagedList<ProductsList> productsLists) {
                listAdapter.setList(productsLists);
            }
        });

        return fragmentView;
    }

    public class ProductsListViewHolder extends RecyclerView.ViewHolder {

        ListItemShoppingListBinding mBinding;

        public ProductsListViewHolder(ListItemShoppingListBinding binding) {
            super(binding.getRoot());

            mBinding = binding;

            ProductsListItemViewModel itemViewModel = new ProductsListItemViewModel();
            ShoppingListApplication.getAppComponent().inject(itemViewModel);
            mBinding.setViewModel(itemViewModel);
        }
        public void bind(ProductsList productsList) {
            mBinding.getViewModel().setProductsList(productsList);
            mBinding.executePendingBindings();
        }
    }

    private class ProductsListAdapter extends PagedListAdapter<ProductsList, ProductsListViewHolder> {

        public ProductsListAdapter() {
            super(DIFF_CALLBACK);
        }

        @Override
        public ProductsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ListItemShoppingListBinding binding = DataBindingUtil.inflate(
                    getLayoutInflater(),R.layout.list_item_shopping_list, parent, false);
            return new ProductsListViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ProductsListViewHolder holder, int position) {
            ProductsList productsList = getItem(position);
            holder.bind(productsList);
        }
    }

    private static final DiffCallback<ProductsList> DIFF_CALLBACK = new DiffCallback<ProductsList>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProductsList oldItem, @NonNull ProductsList newItem) {
            return oldItem.getListID() == newItem.getListID();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProductsList oldItem, @NonNull ProductsList newItem) {
            return oldItem.equals(newItem);
        }
    };
}
