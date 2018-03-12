package com.nzarudna.shoppinglist.ui.productlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.ShoppingListException;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerAdapter;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerViewFragment;
import com.nzarudna.shoppinglist.ui.recyclerui.EditDialogViewModel;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewHolder;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewModel;

import java.util.UUID;

/**
 * Created by Nataliia on 21.01.2018.
 */

public abstract class ProductListFragment
        extends BaseRecyclerViewFragment<CategoryProductItem, ProductListViewModel, CategoryProductItemViewModel> implements Observer<PagedList<CategoryProductItem>> {

    private static final String TAG = "ProductListFragment";

    private static final String ARG_PRODUCT_LIST_ID = "products_list_id";

    private LiveData<PagedList<CategoryProductItem>> mProducts;
    protected ItemTouchHelper mRecycleViewItemTouchHelper;
    private UUID mProductListID;

    protected void setProductListID(UUID productListID) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PRODUCT_LIST_ID, productListID);
        setArguments(bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProductListID = (UUID) getArguments().getSerializable(ARG_PRODUCT_LIST_ID);

        setHasOptionsMenu(true);
    }

    @Override
    protected ProductListViewModel getFragmentViewModel() {
        ProductListViewModel viewModel = ViewModelProviders.of(this).get(getViewModelClass());
        ShoppingListApplication.getAppComponent().inject(viewModel);
        viewModel.setProductListID(mProductListID);

        return viewModel;
    }

    @Override
    protected EditDialogViewModel<CategoryProductItem> getEditDialogViewModel() {
        return null;
    }

    @Override
    protected DiffCallback<CategoryProductItem> getDiffCallback() {
        return new DiffCallback<CategoryProductItem>() {

            @Override
            public boolean areItemsTheSame(@NonNull CategoryProductItem oldItem, @NonNull CategoryProductItem newItem) {
                if (oldItem.getType().equals(CategoryProductItem.TYPE_PRODUCT)
                        && newItem.getType().equals(CategoryProductItem.TYPE_PRODUCT)) {
                    return oldItem.getProduct().getProductID().equals(newItem.getProduct().getProductID());
                }
                if (oldItem.getType().equals(CategoryProductItem.TYPE_CATEGORY)
                        && newItem.getType().equals(CategoryProductItem.TYPE_CATEGORY)) {
                    return oldItem.getCategory().getCategoryID().equals(newItem.getCategory().getCategoryID());
                }
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull CategoryProductItem oldItem, @NonNull CategoryProductItem newItem) {
                return oldItem.equals(newItem);
            }
        };
    }

    protected abstract Class<? extends ProductListViewModel> getViewModelClass();

    @LayoutRes
    protected abstract int getProductItemLayoutID();

    @Override
    protected RecyclerView getRecyclerView(View fragmentView) {

        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (isDragAndDropEnabled()) {
            mRecycleViewItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                int mStartPosition = -1;
                int mTargetStartPosition = -1;
                RecyclerView.ViewHolder mTarget;

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                        if (mStartPosition == -1) {
                            mStartPosition = viewHolder.getLayoutPosition();
                            mTargetStartPosition = target.getLayoutPosition();
                        }
                        mTarget = target;

                        mAdapter.notifyItemMoved(viewHolder.getLayoutPosition(), target.getLayoutPosition());
                        return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                }

                @Override
                public boolean isLongPressDragEnabled() {
                    return false;
                }

                @Override
                public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    super.clearView(recyclerView, viewHolder);
                    Log.d(TAG, "clear view " + viewHolder.getLayoutPosition());
                    Log.d(TAG, "clear view: current old pos " + mStartPosition);
                    Log.d(TAG, "clear view: target " + ((CategoryProductViewHolder) mTarget).mItemViewModel.getItemName());

                    if (viewHolder.getLayoutPosition() != mStartPosition) {

                        CategoryProductItemViewModel currentViewModel = ((CategoryProductViewHolder) viewHolder).mItemViewModel;
                        CategoryProductItemViewModel prevViewModel = null;
                        CategoryProductItemViewModel nextViewModel = null;

                        if (mStartPosition < mTargetStartPosition) {
                            prevViewModel = ((CategoryProductViewHolder) mTarget).mItemViewModel;
                            int nextViewOffset = (mTargetStartPosition - mStartPosition) + 1;
                            View nextView = recyclerView.getChildAt(mTarget.getAdapterPosition() + nextViewOffset);
                            if (nextView != null) {
                                nextViewModel = (CategoryProductItemViewModel) nextView.getTag();
                            }
                        } else {
                            nextViewModel = ((CategoryProductViewHolder) mTarget).mItemViewModel;
                            int nextViewOffset = (mStartPosition - mTargetStartPosition) + 1;
                            View prevView = recyclerView.getChildAt(mTarget.getLayoutPosition() - nextViewOffset);
                            if (prevView != null) {
                                prevViewModel = (CategoryProductItemViewModel) prevView.getTag();
                            }
                        }
                        try {
                            currentViewModel.onMoveItem(prevViewModel, nextViewModel);
                        } catch (ShoppingListException e) {
                            //TODO: handle error
                            e.printStackTrace();
                        }
                    }
                    mStartPosition = -1;
                    mTargetStartPosition = -1;
                }
            });
            mRecycleViewItemTouchHelper.attachToRecyclerView(recyclerView);
        }


        mViewModel.getProductListData().observe(this, new Observer<ProductList>() {
            @Override
            public void onChanged(@Nullable ProductList productList) {
                mViewModel.setProductListData(productList);

                getActivity().setTitle(mViewModel.getListName());
            }
        });

        return recyclerView;
    }

    @Override
    protected BaseRecyclerAdapter<CategoryProductItem, CategoryProductItemViewModel> getRecyclerViewAdapter() {
        return new CategoryProductAdapter(this, getDiffCallback());
    }

    protected boolean isDragAndDropEnabled() {
        return false;
    }

    @Override
    public void onChanged(@Nullable PagedList<CategoryProductItem> categoryProductItems) {
        mAdapter.setList(categoryProductItems);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.product_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_name:
                mViewModel.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
                mProducts = mViewModel.getItems(DEFAULT_PAGE_SIZE);
                mProducts.observe(this, this);
                return true;
            case R.id.sort_by_status:
                mViewModel.setSorting(ProductList.SORT_PRODUCTS_BY_STATUS);
                mProducts = mViewModel.getItems(DEFAULT_PAGE_SIZE);
                mProducts.observe(this, this);
                return true;

            case R.id.view_by_categories:
                mViewModel.setIsGroupedView(true);
                mProducts = mViewModel.getItems(DEFAULT_PAGE_SIZE);
                mProducts.observe(this, this);
                return true;
            case R.id.view_separately:
                mViewModel.setIsGroupedView(false);
                mProducts = mViewModel.getItems(DEFAULT_PAGE_SIZE);
                mProducts.observe(this, this);
                return true;

            case R.id.menu_item_remove_bought:
                mViewModel.removeBoughtProducts();
                return true;
            case R.id.menu_item_mark_all_as_bought:
                mViewModel.markProductsAs(Product.BOUGHT);
                return true;
            case R.id.menu_item_mark_all_as_active:
                mViewModel.markProductsAs(Product.TO_BUY);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getItemContextMenuResID() {
        return R.menu.product_item_menu;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        try {
            Product product = getCurrentItemViewModel().getProduct();
            switch (product.getStatus()) {
                case Product.TO_BUY:
                    menu.removeItem(R.id.mark_product_as_active_menu_item);
                    break;
                case Product.BOUGHT:
                    menu.removeItem(R.id.mark_product_as_bought_menu_item);
                    break;
                case Product.ABSENT:
                    menu.removeItem(R.id.mark_product_as_absent_menu_item);
                    break;
            }

        } catch (ShoppingListException e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        try {
            switch (item.getItemId()) {
                case R.id.mark_product_as_active_menu_item:
                    getCurrentItemViewModel().markProductAs(Product.TO_BUY);
                    return true;
                case R.id.mark_product_as_bought_menu_item:
                    getCurrentItemViewModel().markProductAs(Product.BOUGHT);
                    return true;
                case R.id.mark_product_as_absent_menu_item:
                    getCurrentItemViewModel().markProductAs(Product.ABSENT);
                    return true;
            }
            return super.onContextItemSelected(item);

        } catch (ShoppingListException e) {
            //TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    protected ImageView getDraggableItemViewHandler(View itemView) {
        return null;
    }

    protected class CategoryProductViewHolder extends RecyclerItemViewHolder<CategoryProductItem, CategoryProductItemViewModel> {

        protected CategoryProductItemViewModel mItemViewModel;

        public CategoryProductViewHolder(Fragment fragment,
                                      ViewDataBinding dataBinding,
                                      @Nullable RecyclerItemViewModel.RecyclerItemViewModelObserver<CategoryProductItem> observer) {
            super(fragment, dataBinding, observer);

            mItemViewModel.setShoppingList(mViewModel.mShoppingList);

            ImageView dragHandler = getDraggableItemViewHandler(mDataBinding.getRoot());
            if (isDragAndDropEnabled() && dragHandler != null) {
                dragHandler.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN
                                || motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            mRecycleViewItemTouchHelper.startDrag(CategoryProductViewHolder.this);
                            return true;
                        }
                        return false;
                    }
                });
            }
        }

        @Override
        protected CategoryProductItemViewModel getItemViewModel() {
            return ProductListFragment.this.getListItemViewModel();
        }
    }

    private class CategoryProductAdapter extends BaseRecyclerAdapter<CategoryProductItem, CategoryProductItemViewModel> {

        private static final int CATEGORY_VIEW_TYPE = 1;
        private static final int PRODUCT_VIEW_TYPE = 2;

        protected CategoryProductAdapter(Fragment fragment, DiffCallback<CategoryProductItem> diffCallback) {
            super(fragment, diffCallback);
        }

        @Override
        public int getItemViewType(int position) {
            return CategoryProductItem.TYPE_CATEGORY.equals(getItem(position).getType())
                    ? CATEGORY_VIEW_TYPE : PRODUCT_VIEW_TYPE;
        }

        @Override
        protected int getItemLayoutResID(int viewType) {
            return (viewType == PRODUCT_VIEW_TYPE)
                    ? getProductItemLayoutID() : R.layout.item_category_product_list;
        }

        @Override
        protected CategoryProductItemViewModel getItemViewModel() {
            return ProductListFragment.this.getListItemViewModel();
        }
    }
}
