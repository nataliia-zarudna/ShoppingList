package com.nzarudna.shoppinglist.ui.productlists;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.databinding.ListItemProductListBinding;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;
import com.nzarudna.shoppinglist.ui.editproductlist.EditProductListActivity;
import com.nzarudna.shoppinglist.ui.productlist.ProductListActivity;

import java.util.UUID;

/**
 * Fragment with set of shopping lists
 */
public class ProductListsFragment extends Fragment implements
        ProductListItemViewModel.ProductListItemViewModelObserver,
        ProductListsViewModel.ProductListViewModelObserver,
        Observer<PagedList<ProductListWithStatistics>> {

    private static final String TAG = "ProductListsFragment";

    private static final int REQUEST_CODE_LIST_TO_COPY = 1;
    private static final int PAGE_SIZE = 20;

    public static Fragment getInstance() {
        return new ProductListsFragment();
    }

    private View mFragmentView;
    private ProductListsViewModel mViewModel;
    private RecyclerView mListRecyclerView;
    private ProductListAdapter mListAdapter;
    private LiveData<PagedList<ProductListWithStatistics>> mCurrentList;

    private FloatingActionButton mShowCreationMenuBtn;
    private FloatingActionButton mCreateNewSubItem;
    private FloatingActionButton mCopySubItem;
    private TextView mCreateNewSubItemTitle;
    private TextView mCopySubItemTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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

        loadList(0);

        mShowCreationMenuBtn = mFragmentView.findViewById(R.id.show_create_list_menu);
        mCreateNewSubItem = mFragmentView.findViewById(R.id.btn_new_list);
        mCopySubItem = mFragmentView.findViewById(R.id.btn_copy_list);
        mCreateNewSubItemTitle = mFragmentView.findViewById(R.id.new_list_title);
        mCopySubItemTitle = mFragmentView.findViewById(R.id.copy_list_title);
        configCreationMenu();

        return mFragmentView;
    }

    private void loadList(int sorting) {
        if (mCurrentList != null) {
            mCurrentList.removeObserver(this);
        }

        LiveData<PagedList<ProductListWithStatistics>> mCurrentList = mViewModel.getList(sorting, PAGE_SIZE);
        mCurrentList.observe(this, this);
    }

    @Override
    public void onChanged(@Nullable PagedList<ProductListWithStatistics> productLists) {
        mListAdapter.setList(productLists);
    }

    private void configCreationMenu() {

        mShowCreationMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int subItemsVisibility = (mCreateNewSubItem.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;

                mCreateNewSubItem.setVisibility(subItemsVisibility);
                mCopySubItem.setVisibility(subItemsVisibility);
                mCreateNewSubItemTitle.setVisibility(subItemsVisibility);
                mCopySubItemTitle.setVisibility(subItemsVisibility);
            }
        });

        mCreateNewSubItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.onClickCreateListBtn();
            }
        });

        mCopySubItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CopyListDialogFragment copyListFragment = new CopyListDialogFragment();
                copyListFragment.setTargetFragment(ProductListsFragment.this, REQUEST_CODE_LIST_TO_COPY);
                copyListFragment.show(getFragmentManager(), CopyListDialogFragment.class.getSimpleName());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_LIST_TO_COPY) {
            UUID listID = CopyListDialogFragment.getListID(data);
            mViewModel.onClickCopyListBtn(listID);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.product_lists_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort_by_name:
                loadList(ProductListRepository.SORT_LISTS_BY_NAME);
                return true;
            case R.id.sort_by_created_by:
                loadList(ProductListRepository.SORT_LISTS_BY_CREATED_BY);
                return true;
            case R.id.sort_by_created_at:
                loadList(ProductListRepository.SORT_LISTS_BY_CREATED_AT);
                return true;
            case R.id.sort_by_modified_at:
                loadList(ProductListRepository.SORT_LISTS_BY_MODIFIED_AT);
                return true;
            case R.id.sort_by_assigned:
                loadList(ProductListRepository.SORT_LISTS_BY_ASSIGNED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void startProductListActivity(UUID productListID) {
        Intent intent = ProductListActivity.newIntent(getActivity(), productListID);
        startActivity(intent);
    }

    @Override
    public void startEditProductListActivity(UUID productListID) {
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
