package com.nzarudna.shoppinglist.ui.productlist.edit.template;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItemWithListStatistics;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerAdapter;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerViewFragment;
import com.nzarudna.shoppinglist.ui.recyclerui.EditDialogViewModel;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewHolder;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewModel;

import java.util.UUID;

/**
 * Created by Nataliia on 04.03.2018.
 */

public class ChooseTemplateFragment
        extends BaseRecyclerViewFragment<CategoryTemplateItemWithListStatistics, ChooseTemplateViewModel, CategoryTemplateItemViewModel> {

    private static final String ARG_LIST_ID = "com.nzarudna.shoppinglist.ui.productlist.edit.template.list_id";

    private Menu mMenu;

    public static ChooseTemplateFragment getInstance(UUID listID) {
        ChooseTemplateFragment instance = new ChooseTemplateFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_LIST_ID, listID);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            UUID listID = (UUID) getArguments().getSerializable(ARG_LIST_ID);
            mViewModel.setProductListID(listID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    protected ChooseTemplateViewModel getFragmentViewModel() {
        ChooseTemplateViewModel viewModel = ViewModelProviders.of(this).get(ChooseTemplateViewModel.class);
        ShoppingListApplication.getAppComponent().inject(viewModel);
        return viewModel;
    }

    @Override
    protected CategoryTemplateItemViewModel getListItemViewModel() {
        return new CategoryTemplateItemViewModel();
    }

    @Override
    protected EditDialogViewModel<CategoryTemplateItemWithListStatistics> getEditDialogViewModel() {
        return null;
    }

    @Override
    protected DiffUtil.ItemCallback<CategoryTemplateItemWithListStatistics> getDiffCallback() {
        return new DiffUtil.ItemCallback<CategoryTemplateItemWithListStatistics>() {
            @Override
            public boolean areItemsTheSame(@NonNull CategoryTemplateItemWithListStatistics oldItem, @NonNull CategoryTemplateItemWithListStatistics newItem) {
                if (oldItem.getType().equals(newItem.getType())) {
                    if (oldItem.getType().equals(CategoryTemplateItemWithListStatistics.TYPE_TEMPLATE)) {
                        return oldItem.getTemplate().getTemplateID().equals(newItem.getTemplate().getTemplateID());
                    } else {
                        return oldItem.getCategory().getCategoryID().equals(newItem.getCategory().getCategoryID());
                    }
                } else {
                    return false;
                }
            }

            @Override
            public boolean areContentsTheSame(@NonNull CategoryTemplateItemWithListStatistics oldItem, @NonNull CategoryTemplateItemWithListStatistics newItem) {
                return oldItem.equals(newItem);
            }
        };
    }

    @Override
    protected BaseRecyclerAdapter<CategoryTemplateItemWithListStatistics, CategoryTemplateItemViewModel> getRecyclerViewAdapter() {
        return new CategoryTemplateAdapter(this, getDiffCallback());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.choose_template_menu, menu);

        mMenu = menu;
        hideUnusedMenuItems();
    }

    private void hideUnusedMenuItems() {
        if (mViewModel.isIsGroupedView()) {
            mMenu.findItem(R.id.menu_item_view_by_categories).setVisible(false);
            mMenu.findItem(R.id.menu_item_view_separately).setVisible(true);
        } else {
            mMenu.findItem(R.id.menu_item_view_by_categories).setVisible(true);
            mMenu.findItem(R.id.menu_item_view_separately).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_view_by_categories:
                mViewModel.setIsGroupedView(true);
                loadItems();
                hideUnusedMenuItems();
                return true;
            case R.id.menu_item_view_separately:
                mViewModel.setIsGroupedView(false);
                loadItems();
                hideUnusedMenuItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class CategoryTemplateViewHolder
            extends RecyclerItemViewHolder<CategoryTemplateItemWithListStatistics, CategoryTemplateItemViewModel> {

        public CategoryTemplateViewHolder(Fragment fragment, ViewDataBinding dataBinding,
                                          @Nullable RecyclerItemViewModel.RecyclerItemViewModelObserver<CategoryTemplateItemWithListStatistics> observer) {
            super(fragment, dataBinding, observer);

            getItemViewModel().setShoppingList(mViewModel.getShoppingList());
        }

        @Override
        protected CategoryTemplateItemViewModel newItemViewModel() {
            return ChooseTemplateFragment.this.getListItemViewModel();
        }
    }

    private class CategoryTemplateAdapter
            extends BaseRecyclerAdapter<CategoryTemplateItemWithListStatistics, CategoryTemplateItemViewModel> {

        private static final int VIEW_TYPE_TEMPLATE = 1;
        private static final int VIEW_TYPE_CATEGORY = 2;

        protected CategoryTemplateAdapter(Fragment fragment, DiffUtil.ItemCallback<CategoryTemplateItemWithListStatistics> diffCallback) {
            super(fragment, diffCallback);
        }

        @Override
        public CategoryTemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            int layoutResID;
            if (viewType == VIEW_TYPE_TEMPLATE) {
                layoutResID = R.layout.item_template_checkable_template_list;
            } else {
                layoutResID = R.layout.item_category_checkable_template_list;
            }
            ViewDataBinding dataBinding = DataBindingUtil.inflate(
                    getLayoutInflater(), layoutResID, parent, false);

            return new CategoryTemplateViewHolder(mFragment, dataBinding, mRecyclerItemViewModelObserver);
        }

        @Override
        protected CategoryTemplateItemViewModel getItemViewModel() {
            return ChooseTemplateFragment.this.getListItemViewModel();
        }

        @Override
        public int getItemViewType(int position) {
            CategoryTemplateItemWithListStatistics item = getItem(position);
            return CategoryTemplateItemWithListStatistics.TYPE_TEMPLATE.equals(item.getType())
                    ? VIEW_TYPE_TEMPLATE : VIEW_TYPE_CATEGORY;
        }
    }
}
