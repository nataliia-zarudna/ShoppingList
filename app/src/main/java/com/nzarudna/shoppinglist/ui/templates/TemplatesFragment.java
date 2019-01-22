package com.nzarudna.shoppinglist.ui.templates;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.ui.productlist.edit.EditProductListActivity;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerAdapter;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerViewFragment;
import com.nzarudna.shoppinglist.ui.templates.editdialog.EditTemplateDialogFragment;
import com.nzarudna.shoppinglist.ui.templates.editdialog.EditTemplateViewModel;

import java.util.UUID;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class TemplatesFragment extends BaseRecyclerViewFragment
        <CategoryTemplateItem, TemplatesViewModel, CategoryTemplateItemViewModel>
        implements TemplatesViewModel.TemplatesViewModelObserver,
        CategoryTemplateItemViewModel.CategoryTemplateItemViewModelObserver {

    private ActionMode mActionMode;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = new MenuInflater(getActivity());
            menuInflater.inflate(R.menu.template_multiple_context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.create_list_menu_item:
                    mViewModel.createProductList();
                    mActionMode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mViewModel.deselectAllItems();
        }
    };

    @Override
    protected TemplatesViewModel getFragmentViewModel() {
        TemplatesViewModel viewModel = ViewModelProviders.of(this).get(TemplatesViewModel.class);
        ShoppingListApplication.getAppComponent().inject(viewModel);
        viewModel.setTemplateViewObserver(this);

        return viewModel;
    }

    @Override
    protected CategoryTemplateItemViewModel getListItemViewModel() {
        CategoryTemplateItemViewModel itemViewModel = new CategoryTemplateItemViewModel();
        ShoppingListApplication.getAppComponent().inject(itemViewModel);
        itemViewModel.setCategoryTemplateItemViewModelObserver(this);
        return itemViewModel;
    }

    @Override
    protected EditTemplateViewModel getEditDialogViewModel() {
        EditTemplateViewModel viewModel = new EditTemplateViewModel();
        ShoppingListApplication.getAppComponent().inject(viewModel);
        return viewModel;
    }

    @Override
    protected DiffUtil.ItemCallback<CategoryTemplateItem> getDiffCallback() {
        return new DiffUtil.ItemCallback<CategoryTemplateItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull CategoryTemplateItem oldItem, @NonNull CategoryTemplateItem newItem) {
                if (oldItem.getType().equals(newItem.getType())) {
                    if (oldItem.getType().equals(CategoryTemplateItem.TYPE_TEMPLATE)) {
                        return oldItem.getTemplate().getTemplateID().equals(newItem.getTemplate().getTemplateID());
                    } else {
                        return oldItem.getCategory().getCategoryID().equals(newItem.getCategory().getCategoryID());
                    }
                } else {
                    return false;
                }
            }

            @Override
            public boolean areContentsTheSame(@NonNull CategoryTemplateItem oldItem, @NonNull CategoryTemplateItem newItem) {
                return oldItem.equals(newItem);
            }
        };
    }

    @Override
    public void onTemplateSelected(CategoryTemplateItem item) {
        if (mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
        }

        mViewModel.onItemSelected(item);

        if (mViewModel.getSelectedItemsCount() == 0) {
            mActionMode.finish();
        } else {
            String itemsCount = String.valueOf(mViewModel.getSelectedItemsCount());
            String title = getString(R.string.action_mode_selected_count, itemsCount);
            mActionMode.setTitle(title);
        }
    }

    @Override
    protected BaseRecyclerAdapter<CategoryTemplateItem, CategoryTemplateItemViewModel> getRecyclerViewAdapter() {
        CategoryTemplateAdapter adapter = new CategoryTemplateAdapter(this, getDiffCallback());
        adapter.setRecyclerItemViewModelObserver(this);
        return adapter;
    }

    @Override
    protected EditTemplateDialogFragment getEditItemDialogFragment() {
        EditTemplateDialogFragment editFragment = EditTemplateDialogFragment.newInstance();
        editFragment.setViewModel(getEditDialogViewModel());
        return editFragment;
    }

    @Override
    protected EditTemplateDialogFragment getEditItemDialogFragment(CategoryTemplateItem item) {
        EditTemplateDialogFragment editFragment = getEditItemDialogFragment();
        editFragment.setItem(item);
        return editFragment;
    }

    @Override
    public void onCreateProductList(UUID listID) {
        Intent intent = EditProductListActivity.newIntent(getActivity(), listID);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.templates_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_view_by_categories:
                mViewModel.setIsGroupedView(true);
                loadItems();
                return true;
            case R.id.menu_item_view_separately:
                mViewModel.setIsGroupedView(false);
                loadItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class CategoryTemplateAdapter
            extends BaseRecyclerAdapter<CategoryTemplateItem, CategoryTemplateItemViewModel> {

        private static final int TYPE_TEMPLATE = 1;
        private static final int TYPE_CATEGORY = 2;

        protected CategoryTemplateAdapter(Fragment fragment, DiffUtil.ItemCallback<CategoryTemplateItem> diffCallback) {
            super(fragment, diffCallback);
        }

        @Override
        protected CategoryTemplateItemViewModel getItemViewModel() {
            return TemplatesFragment.this.getListItemViewModel();
        }

        @Override
        protected int getItemLayoutResID(int viewType) {
            return (viewType == TYPE_TEMPLATE)
                    ? R.layout.item_recycler_list
                    : R.layout.item_category_template_list;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getType().equals(CategoryTemplateItem.TYPE_TEMPLATE)
                    ? TYPE_TEMPLATE : TYPE_CATEGORY;
        }
    }
}
