package com.nzarudna.shoppinglist.ui.lists;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.databinding.ListItemShoppingListBinding;
import com.nzarudna.shoppinglist.product.ProductsList;
import com.nzarudna.shoppinglist.ui.editshoppinglist.EditShoppingListActivity;
import com.nzarudna.shoppinglist.ui.shoppinglist.ShoppingListActivity;

/**
 * Fragment with set of shopping lists
 */
public class ShoppingListsFragment extends Fragment implements ProductListItemViewModel.ProductListItemViewModelObserver, ProductListsViewModel.ProductListViewModelObserver {

    private static final String TAG = "ShoppingListsFragment";
    private PagedList<ProductsList> mProductsLists;

    public static Fragment getInstance() {
        return new ShoppingListsFragment();
    }

    private View mFragmentView;
    private ProductListsViewModel mViewModel;
    private RecyclerView mListRecyclerView;
    private ProductsListAdapter mListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentView = inflater.inflate(R.layout.fragment_shopping_lists, container, false);

        mViewModel = ViewModelProviders.of(this).get(ProductListsViewModel.class);
        mViewModel.setObserver(this);
        ShoppingListApplication.getAppComponent().inject(mViewModel);

        mListRecyclerView = mFragmentView.findViewById(R.id.lists_recycle_view);
        mListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListRecyclerView.setHasFixedSize(true);

        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG, "onSwiped direction " + direction);

                ProductListItemViewModel itemViewModel = ((ProductsListViewHolder) viewHolder).mBinding.getViewModel();
                removeListItem(itemViewModel, viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(mListRecyclerView);

        mListAdapter = new ProductsListAdapter();
        mListAdapter.mItemObserver = this;
        mListRecyclerView.setAdapter(mListAdapter);

        mViewModel.getList(20).observe(this, new Observer<PagedList<ProductsList>>() {
            @Override
            public void onChanged(@Nullable PagedList<ProductsList> productsLists) {
                mProductsLists = productsLists;
                mListAdapter.setList(productsLists);
            }
        });

        mFragmentView.findViewById(R.id.btn_add_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.createList();
            }
        });

        return mFragmentView;
    }

    private void removeListItem(final ProductListItemViewModel itemViewModel, final int position) {

        final ProductsList listToRemove = itemViewModel.getProductsList();
//        mProductsLists.remove(position);
        Log.d(TAG, "list to remove " + listToRemove.getName());

        mListAdapter.notifyItemRemoved(position);

        Snackbar.make(mFragmentView, R.string.message_remove_list, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_removal, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListAdapter.notifyItemInserted(position);
                    }
                })
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        if (event != DISMISS_EVENT_ACTION) {
                            Log.d(TAG, "removed list " + listToRemove.getName());
                            mViewModel.removeList(listToRemove);
                        }
                    }
                }).show();
    }

    @Override
    public void startProductsListActivity(int productsListID) {
        Intent intent = ShoppingListActivity.newIntent(getActivity(), productsListID);
        startActivity(intent);
    }

    @Override
    public void startEditProductsListActivity(int productsListID) {
        Intent intent = EditShoppingListActivity.newIntent(getActivity(), productsListID);
        startActivity(intent);
    }

    public class ProductsListViewHolder extends RecyclerView.ViewHolder {

        ListItemShoppingListBinding mBinding;

        public ProductsListViewHolder(ListItemShoppingListBinding binding,
                                      ProductListItemViewModel.ProductListItemViewModelObserver itemObserver) {
            super(binding.getRoot());

            mBinding = binding;

            ProductListItemViewModel itemViewModel = new ProductListItemViewModel();
            itemViewModel.setObserver(itemObserver);
            mBinding.setViewModel(itemViewModel);

            /*binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShoppingListActivity.newIntent(getActivity(), )
                }
            });*/
        }

        public void bind(ProductsList productsList) {
            Log.d(TAG, "bind productsList " + productsList.getName());

            mBinding.getViewModel().setProductsList(productsList);
            mBinding.executePendingBindings();
        }
    }

    private class ProductsListAdapter extends PagedListAdapter<ProductsList, ProductsListViewHolder> {

        private ProductListItemViewModel.ProductListItemViewModelObserver mItemObserver;

        public ProductsListAdapter() {
            super(DIFF_CALLBACK);
        }

        @Override
        public ProductsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ListItemShoppingListBinding binding = DataBindingUtil.inflate(
                    getLayoutInflater(), R.layout.list_item_shopping_list, parent, false);
            return new ProductsListViewHolder(binding, mItemObserver);
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
