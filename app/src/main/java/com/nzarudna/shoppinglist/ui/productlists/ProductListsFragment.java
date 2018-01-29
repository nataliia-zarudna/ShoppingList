package com.nzarudna.shoppinglist.ui.productlists;

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
import com.nzarudna.shoppinglist.databinding.ListItemProductListBinding;
import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;
import com.nzarudna.shoppinglist.ui.editproductlist.EditProductListActivity;
import com.nzarudna.shoppinglist.ui.productlist.ProductListActivity;

/**
 * Fragment with set of shopping lists
 */
public class ProductListsFragment extends Fragment implements ProductListItemViewModel.ProductListItemViewModelObserver, ProductListsViewModel.ProductListViewModelObserver {

    private static final String TAG = "ProductListsFragment";

    public static Fragment getInstance() {
        return new ProductListsFragment();
    }

    private View mFragmentView;
    private ProductListsViewModel mViewModel;
    private RecyclerView mListRecyclerView;
    private ProductListAdapter mListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(ProductListsViewModel.class);
        mViewModel.setObserver(this);
        ShoppingListApplication.getAppComponent().inject(mViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentView = inflater.inflate(R.layout.fragment_product_lists, container, false);

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

                ProductListItemViewModel itemViewModel = ((ProductListViewHolder) viewHolder).mBinding.getViewModel();
                removeListItem(itemViewModel, viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(mListRecyclerView);

        mListAdapter = new ProductListAdapter();
        mListAdapter.mItemObserver = this;
        mListRecyclerView.setAdapter(mListAdapter);

        mViewModel.getList(20).observe(this, new Observer<PagedList<ProductListWithStatistics>>() {
            @Override
            public void onChanged(@Nullable PagedList<ProductListWithStatistics> productLists) {
                mListAdapter.setList(productLists);
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

        final ProductListWithStatistics listToRemove = itemViewModel.getProductList();
//        mProductLists.remove(position);
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
    public void startProductListActivity(int productListID) {
        Intent intent = ProductListActivity.newIntent(getActivity(), productListID);
        startActivity(intent);
    }

    @Override
    public void startEditProductListActivity(int productListID) {
        Intent intent = EditProductListActivity.newIntent(getActivity(), productListID);
        startActivity(intent);
    }

    public class ProductListViewHolder extends RecyclerView.ViewHolder {

        ListItemProductListBinding mBinding;

        public ProductListViewHolder(ListItemProductListBinding binding,
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

        public void bind(ProductListWithStatistics productList) {
            Log.d(TAG, "bind productList " + productList.getName());

            mBinding.getViewModel().setProductList(productList);
            mBinding.executePendingBindings();
        }
    }

    private class ProductListAdapter extends PagedListAdapter<ProductListWithStatistics, ProductListViewHolder> {

        private ProductListItemViewModel.ProductListItemViewModelObserver mItemObserver;

        public ProductListAdapter() {
            super(DIFF_CALLBACK);
        }

        @Override
        public ProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ListItemProductListBinding binding = DataBindingUtil.inflate(
                    getLayoutInflater(), R.layout.list_item_product_list, parent, false);
            return new ProductListViewHolder(binding, mItemObserver);
        }

        @Override
        public void onBindViewHolder(ProductListViewHolder holder, int position) {
            ProductListWithStatistics productList = getItem(position);
            holder.bind(productList);
        }
    }

    private static final DiffCallback<ProductListWithStatistics> DIFF_CALLBACK = new DiffCallback<ProductListWithStatistics>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProductListWithStatistics oldItem, @NonNull ProductListWithStatistics newItem) {
            return oldItem.getListID() == newItem.getListID();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProductListWithStatistics oldItem, @NonNull ProductListWithStatistics newItem) {
            return oldItem.equals(newItem);
        }
    };
}
