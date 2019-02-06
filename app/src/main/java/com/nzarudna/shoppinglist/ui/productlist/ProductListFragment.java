package com.nzarudna.shoppinglist.ui.productlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.exception.ShoppingListException;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductStatistics;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.ui.productlist.editproduct.EditProductDialogFragment;
import com.nzarudna.shoppinglist.ui.productlist.editproduct.EditProductViewModel;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerAdapter;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerViewFragment;
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

    protected Menu mOptionsMenu;

    private LiveData<PagedList<CategoryProductItem>> mProducts;
    protected ItemTouchHelper mRecycleViewItemTouchHelper;
    private UUID mProductListID;
    private boolean mProductJustMoved;

    protected void setProductListID(UUID productListID) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PRODUCT_LIST_ID, productListID);
        setArguments(bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mProductListID = (UUID) getArguments().getSerializable(ARG_PRODUCT_LIST_ID);

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void loadItems() {
        if (mViewModel.getProductList() != null) {
            super.loadItems();
        }
    }

    @Override
    public void onChanged(@Nullable PagedList<CategoryProductItem> items) {
        if (!mProductJustMoved) {
            super.onChanged(items);
        } else {
            mProductJustMoved = false;
        }
    }

    @Override
    protected ProductListViewModel getFragmentViewModel() {
        ProductListViewModel viewModel = ViewModelProviders.of(this).get(getViewModelClass());
        ShoppingListApplication.getAppComponent().inject(viewModel);
        viewModel.setProductListID(mProductListID);

        return viewModel;
    }

    @Override
    protected EditProductViewModel getEditDialogViewModel() {
        return new EditProductViewModel();
    }

    @Override
    protected EditProductDialogFragment getEditItemDialogFragment() {
        EditProductDialogFragment editFragment = EditProductDialogFragment.newInstance();
        editFragment.setViewModel(getEditDialogViewModel());
        return editFragment;
    }

    @Override
    protected EditProductDialogFragment getEditItemDialogFragment(CategoryProductItem item) {
        EditProductDialogFragment editFragment = getEditItemDialogFragment();
        editFragment.setItem(item);
        return editFragment;
    }

    @Override
    protected DiffUtil.ItemCallback<CategoryProductItem> getDiffCallback() {
        return new DiffUtil.ItemCallback<CategoryProductItem>() {

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

                    if (viewHolder.getLayoutPosition() != mStartPosition) {

                        CategoryProductItemViewModel currentViewModel = ((CategoryProductViewHolder) viewHolder).getItemViewModel();
                        CategoryProductItemViewModel prevViewModel = null;
                        CategoryProductItemViewModel nextViewModel = null;

                        if (mStartPosition < mTargetStartPosition) {
                            prevViewModel = ((CategoryProductViewHolder) mTarget).getItemViewModel();
                            int nextViewOffset = (mTargetStartPosition - mStartPosition) + 1;
                            View nextView = recyclerView.getChildAt(mTarget.getAdapterPosition() + nextViewOffset);
                            if (nextView != null) {
                                nextViewModel = (CategoryProductItemViewModel) nextView.getTag();
                            }
                        } else {
                            nextViewModel = ((CategoryProductViewHolder) mTarget).getItemViewModel();
                            int nextViewOffset = (mStartPosition - mTargetStartPosition) + 1;
                            View prevView = recyclerView.getChildAt(mTarget.getLayoutPosition() - nextViewOffset);
                            if (prevView != null) {
                                prevViewModel = (CategoryProductItemViewModel) prevView.getTag();
                            }
                        }
                        try {
                            mProductJustMoved = true;
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

        mViewModel.getProductListLiveData().observe(this, productList -> {
            mViewModel.setProductListData(productList);

            onLoadProductList(productList);
            loadItems();
        });

        mViewModel.getProductStatisticsLiveData().observe(this, productListStatistics -> {
            if (productListStatistics != null) {
                mViewModel.setProductStatistics(productListStatistics.getProductStatistics());
                hideUnnecessaryMenuItems();
            }
        });

        return recyclerView;
    }

    protected void onLoadProductList(ProductList productList) {
        hideUnnecessaryMenuItems();
    }

    private void hideUnnecessaryMenuItems() {
        ProductList productList = mViewModel.getProductList();
        ProductStatistics productStatistics = mViewModel.getProductStatistics();
        if (mOptionsMenu != null && productList != null && productStatistics != null) {
            hideUsedGroupViewMenu(productList);
            hideUsedSortingMenu(productList);
            hideMarkAsMenu(productStatistics);
            hideRemoveBought(productStatistics);
        }
    }

    private void hideUsedGroupViewMenu(ProductList productList) {
        if (productList.isGroupedView()) {
            mOptionsMenu.findItem(R.id.menu_item_view_separately).setVisible(true);
            mOptionsMenu.findItem(R.id.menu_item_view_by_categories).setVisible(false);
        } else {
            mOptionsMenu.findItem(R.id.menu_item_view_separately).setVisible(false);
            mOptionsMenu.findItem(R.id.menu_item_view_by_categories).setVisible(true);
        }
    }

    protected void hideUsedSortingMenu(ProductList productList) {
        switch (productList.getSorting()) {
            case ProductList.SORT_PRODUCTS_BY_NAME:
                mOptionsMenu.findItem(R.id.menu_item_sort_by_name).setVisible(false);
                mOptionsMenu.findItem(R.id.menu_item_apply_sort_by_name).setVisible(true);
                mOptionsMenu.findItem(R.id.menu_item_sort_by_status).setVisible(true);
                mOptionsMenu.findItem(R.id.menu_item_apply_sort_by_status).setVisible(false);
                break;
            case ProductList.SORT_PRODUCTS_BY_STATUS:
                mOptionsMenu.findItem(R.id.menu_item_sort_by_name).setVisible(true);
                mOptionsMenu.findItem(R.id.menu_item_apply_sort_by_name).setVisible(false);
                mOptionsMenu.findItem(R.id.menu_item_sort_by_status).setVisible(false);
                mOptionsMenu.findItem(R.id.menu_item_apply_sort_by_status).setVisible(true);
                break;
        }
    }

    private void hideMarkAsMenu(ProductStatistics listStatistics) {
        if (listStatistics.getBoughtProductsCount() == listStatistics.getAllProductsCount()) {
            mOptionsMenu.findItem(R.id.menu_item_mark_all_as_bought).setVisible(false);
        } else {
            mOptionsMenu.findItem(R.id.menu_item_mark_all_as_bought).setVisible(true);
        }

        if (listStatistics.getActiveProductsCount() == listStatistics.getAllProductsCount()) {
            mOptionsMenu.findItem(R.id.menu_item_mark_all_as_active).setVisible(false);
        } else {
            mOptionsMenu.findItem(R.id.menu_item_mark_all_as_active).setVisible(true);
        }
    }

    private void hideRemoveBought(ProductStatistics listStatistics) {
        if (listStatistics.getBoughtProductsCount() == 0) {
            mOptionsMenu.findItem(R.id.menu_item_remove_bought).setVisible(false);
        } else {
            mOptionsMenu.findItem(R.id.menu_item_remove_bought).setVisible(true);
        }
    }

    @Override
    protected BaseRecyclerAdapter<CategoryProductItem, CategoryProductItemViewModel> getRecyclerViewAdapter() {
        CategoryProductAdapter adapter = new CategoryProductAdapter(this, getDiffCallback());
        adapter.setRecyclerItemViewModelObserver(this);
        return adapter;
    }

    protected boolean isDragAndDropEnabled() {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.product_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        mOptionsMenu = menu;

        hideUnnecessaryMenuItems();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_apply_sort_by_name:
            case R.id.menu_item_apply_sort_by_status:
                mViewModel.setUseCustomSorting(false);
                reloadItems();
                return true;
            case R.id.menu_item_sort_by_name:
                mViewModel.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
                reloadItems();
                return true;
            case R.id.menu_item_sort_by_status:
                mViewModel.setSorting(ProductList.SORT_PRODUCTS_BY_STATUS);
                reloadItems();
                return true;

            case R.id.menu_item_view_by_categories:
                mViewModel.setIsGroupedView(true);
                reloadItems();
                return true;
            case R.id.menu_item_view_separately:
                mViewModel.setIsGroupedView(false);
                reloadItems();
                return true;

            case R.id.menu_item_remove_bought:
                mViewModel.removeBoughtProducts();
                return true;
            case R.id.menu_item_share:
                shareProducts();
                return true;
            case R.id.menu_item_mark_all_as_bought:
                mViewModel.markProductsAs(Product.BOUGHT);
                return true;
            case R.id.menu_item_mark_all_as_active:
                mViewModel.markProductsAs(Product.ACTIVE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reloadItems() {
        mProducts = mViewModel.getItems(DEFAULT_PAGE_SIZE);
        mProducts.observe(this, this);
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
                case Product.ACTIVE:
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
                    getCurrentItemViewModel().markProductAs(Product.ACTIVE);
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

        public CategoryProductViewHolder(Fragment fragment,
                                         ViewDataBinding dataBinding,
                                         @Nullable RecyclerItemViewModel.RecyclerItemViewModelObserver<CategoryProductItem> observer) {
            super(fragment, dataBinding, observer);

            getItemViewModel().setShoppingList(mViewModel.mShoppingList);

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
        protected CategoryProductItemViewModel newItemViewModel() {
            return ProductListFragment.this.getListItemViewModel();
        }
    }

    private class CategoryProductAdapter extends BaseRecyclerAdapter<CategoryProductItem, CategoryProductItemViewModel> {

        private static final int CATEGORY_VIEW_TYPE = 1;
        private static final int PRODUCT_VIEW_TYPE = 2;

        protected CategoryProductAdapter(Fragment fragment, DiffUtil.ItemCallback<CategoryProductItem> diffCallback) {
            super(fragment, diffCallback);
        }

        @Override
        protected RecyclerItemViewHolder<CategoryProductItem, CategoryProductItemViewModel> getViewHolderInstance(ViewDataBinding dataBinding) {
            return new CategoryProductViewHolder(mFragment, dataBinding, mRecyclerItemViewModelObserver);
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

    public void shareProducts() {
        mViewModel.getShareProductsText(shareText -> {

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });
    }
}
