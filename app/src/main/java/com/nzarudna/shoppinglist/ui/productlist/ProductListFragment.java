package com.nzarudna.shoppinglist.ui.productlist;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Intent;
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
import android.widget.Toast;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.ShoppingListException;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.ui.productlist.editproduct.EditProductDialogFragment;

import java.util.UUID;

/**
 * Created by Nataliia on 21.01.2018.
 */

public abstract class ProductListFragment extends Fragment implements Observer<PagedList<CategoryProductItem>>, CategoryProductItemViewModel.CategoryProductItemViewModelObserver {

    private static final String TAG = "ProductListFragment";

    private static final String ARG_PRODUCT_LIST_ID = "products_list_id";
    private static final int DEFAULT_LOAD_LIST_SIZE = 20;
    private static final int REQUEST_CODE_EDIT_RPODUCT = 1;

    protected ProductListViewModel mViewModel;
    private CategoryProductAdapter mAdapter;
    private LiveData<PagedList<CategoryProductItem>> mProducts;
    private CategoryProductItemViewModel mCurrentContextMenuProductVM;
    protected RecyclerView mProductsRecyclerView;
    protected ItemTouchHelper mRecycleViewItemTouchHelper;

    protected void setProductListID(UUID productListID) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PRODUCT_LIST_ID, productListID);
        setArguments(bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID productListID = (UUID) getArguments().getSerializable(ARG_PRODUCT_LIST_ID);

        mViewModel = ViewModelProviders.of(this).get(getViewModelClass());
        ShoppingListApplication.getAppComponent().inject(mViewModel);
        mViewModel.setProductListID(productListID);

        setHasOptionsMenu(true);

        Log.d(TAG, "list id " + productListID);
    }

    protected abstract Class<? extends ProductListViewModel> getViewModelClass();

    @LayoutRes
    protected abstract int getProductItemLayoutID();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        initProductsRecyclerView(fragmentView);

        return fragmentView;
    }

    protected void initProductsRecyclerView(View fragmentView) {
        mProductsRecyclerView = fragmentView.findViewById(R.id.recycler_view);
        mProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new CategoryProductAdapter();
        mProductsRecyclerView.setAdapter(mAdapter);

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
                    Log.d(TAG, "clear view: target " + ((CategoryProductViewHolder) mTarget).mItemViewModel.getProductName());

                    if (viewHolder.getLayoutPosition() != mStartPosition) {

                        CategoryProductItemViewModel currentViewModel = ((CategoryProductViewHolder) viewHolder).mItemViewModel;
                        CategoryProductItemViewModel prevViewModel = null;
                        CategoryProductItemViewModel nextViewModel = null;

                        if (mStartPosition < mTargetStartPosition) {
                            prevViewModel = ((CategoryProductViewHolder) mTarget).mItemViewModel;
                            int nextViewOffset = (mTargetStartPosition - mStartPosition) + 1;
                            View nextView = mProductsRecyclerView.getChildAt(mTarget.getAdapterPosition() + nextViewOffset);
                            if (nextView != null) {
                                nextViewModel = (CategoryProductItemViewModel) nextView.getTag();
                            }
                        } else {
                            nextViewModel = ((CategoryProductViewHolder) mTarget).mItemViewModel;
                            int nextViewOffset = (mStartPosition - mTargetStartPosition) + 1;
                            View prevView = mProductsRecyclerView.getChildAt(mTarget.getLayoutPosition() - nextViewOffset);
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
            mRecycleViewItemTouchHelper.attachToRecyclerView(mProductsRecyclerView);
        }


        mViewModel.getProductListData().observe(this, new Observer<ProductList>() {
            @Override
            public void onChanged(@Nullable ProductList productList) {
                mViewModel.setProductListData(productList);

                if (mProducts == null) {
                    mProducts = mViewModel.getProducts(DEFAULT_LOAD_LIST_SIZE);
                    mProducts.observe(ProductListFragment.this, ProductListFragment.this);
                }

                getActivity().setTitle(mViewModel.getListName());
            }
        });
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
                mProducts = mViewModel.getProducts(ProductList.SORT_PRODUCTS_BY_NAME, DEFAULT_LOAD_LIST_SIZE);
                mProducts.observe(this, this);
                return true;
            case R.id.sort_by_status:
                mProducts = mViewModel.getProducts(ProductList.SORT_PRODUCTS_BY_STATUS, DEFAULT_LOAD_LIST_SIZE);
                mProducts.observe(this, this);
                return true;

            case R.id.view_by_categories:
                mProducts = mViewModel.getProducts(true, DEFAULT_LOAD_LIST_SIZE);
                mProducts.observe(this, this);
                return true;
            case R.id.view_separately:
                mProducts = mViewModel.getProducts(false, DEFAULT_LOAD_LIST_SIZE);
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getActivity().getMenuInflater().inflate(R.menu.product_item_menu, menu);
        mCurrentContextMenuProductVM = (CategoryProductItemViewModel) v.getTag();

        try {
            Product product = mCurrentContextMenuProductVM.getProduct();
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
                case R.id.edit_product_menu_item:
                    openEditProductDialog(mCurrentContextMenuProductVM.getProduct());
                    return true;

                case R.id.mark_product_as_active_menu_item:
                    mCurrentContextMenuProductVM.markProductAs(Product.TO_BUY);
                    return true;
                case R.id.mark_product_as_bought_menu_item:
                    mCurrentContextMenuProductVM.markProductAs(Product.BOUGHT);
                    return true;
                case R.id.mark_product_as_absent_menu_item:
                    mCurrentContextMenuProductVM.markProductAs(Product.ABSENT);
                    return true;

                case R.id.remove_product_menu_item:
                    mCurrentContextMenuProductVM.removeProduct();
            }
            return super.onContextItemSelected(item);
        } catch (ShoppingListException e) {
            //TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void openEditProductDialog(Product product) {
        EditProductDialogFragment dialogFragment = EditProductDialogFragment.newInstance(product);
        dialogFragment.setTargetFragment(this, REQUEST_CODE_EDIT_RPODUCT);
        dialogFragment.show(getFragmentManager(), EditProductDialogFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_EDIT_RPODUCT) {
            showSuccessSaveMessage();
        }
    }

    @Override
    public void showContextMenu(int productPosition) {
        View productView = mProductsRecyclerView.getChildAt(productPosition);
        //registerForContextMenu(productView);
        productView.showContextMenu();
        //unregisterForContextMenu(productView);

        mCurrentContextMenuProductVM = (CategoryProductItemViewModel) productView.getTag();
    }

    @Override
    public void showSuccessSaveMessage() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), R.string.save_item_success_msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected abstract CategoryProductItemViewModel getCategoryProductItemViewModel();

    @Nullable
    protected ImageView getDraggableItemViewHandler(View itemView) {
        return null;
    }

    protected class CategoryProductViewHolder extends RecyclerView.ViewHolder {

        ViewDataBinding mBinding;
        CategoryProductItemViewModel mItemViewModel;

        public CategoryProductViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

            mItemViewModel = getCategoryProductItemViewModel();
            ShoppingListApplication.getAppComponent().inject(mItemViewModel);
            mItemViewModel.setShoppingList(mViewModel.mShoppingList);
            mItemViewModel.setObserver(ProductListFragment.this);

            mBinding.setVariable(BR.viewModel, mItemViewModel);

            ImageView dragHandler = getDraggableItemViewHandler(mBinding.getRoot());
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

        public void bind(CategoryProductItem categoryProductItem, final int position) {
            mItemViewModel.setCategoryProductItem(categoryProductItem);

            if (CategoryProductItem.TYPE_PRODUCT.equals(categoryProductItem.getType())) {
                mBinding.getRoot().setTag(mItemViewModel);
                mItemViewModel.setCurrentPosition(position);
                registerForContextMenu(mBinding.getRoot());
            } else {
                unregisterForContextMenu(mBinding.getRoot());
            }

            mBinding.executePendingBindings();
        }
    }

    private class CategoryProductAdapter extends PagedListAdapter<CategoryProductItem, CategoryProductViewHolder> {

        private static final int CATEGORY_VIEW_TYPE = 1;
        private static final int PRODUCT_VIEW_TYPE = 2;

        public CategoryProductAdapter() {
            super(DIFF_CALLBACK);
        }

        @Override
        public int getItemViewType(int position) {
            return CategoryProductItem.TYPE_CATEGORY.equals(getItem(position).getType())
                    ? CATEGORY_VIEW_TYPE : PRODUCT_VIEW_TYPE;
        }

        @Override
        public CategoryProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            int layoutResID = (viewType == PRODUCT_VIEW_TYPE)
                    ? getProductItemLayoutID() : R.layout.item_category_product_list;
            ViewDataBinding binding = DataBindingUtil.inflate(getLayoutInflater(), layoutResID, parent, false);

            return new CategoryProductViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(CategoryProductViewHolder holder, int position) {
            holder.bind(getItem(position), position);
        }
    }

    private static final DiffCallback<CategoryProductItem> DIFF_CALLBACK = new DiffCallback<CategoryProductItem>() {

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
