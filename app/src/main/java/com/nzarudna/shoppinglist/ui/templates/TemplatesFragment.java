package com.nzarudna.shoppinglist.ui.templates;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.ui.RecyclerItemViewModel;
import com.nzarudna.shoppinglist.ui.RecyclerViewModel;
import com.nzarudna.shoppinglist.ui.templates.editdialog.EditTemplateDialogFragment;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class TemplatesFragment extends Fragment
        implements RecyclerItemViewModel.RecyclerItemViewModelObserver<CategoryTemplateItem>,
        RecyclerViewModel.RecyclerViewModelObserver {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int REQUEST_CODE_EDIT_TEMPLATE = 1;
    private TemplatesViewModel mViewModel;
    private RecyclerView mTemplatesView;
    private CategoryTemplateAdapter mAdapter;
    private CategoryTemplateItemViewModel mContextMenuItemModelView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(TemplatesViewModel.class);
        ShoppingListApplication.getAppComponent().inject(mViewModel);
        mViewModel.setObserver(this);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewDataBinding dataBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_recycler_view_with_fab, container, false);
        dataBinding.setVariable(BR.viewModel, mViewModel);

        View fragmentView = dataBinding.getRoot();

        mTemplatesView = fragmentView.findViewById(R.id.recycler_view);
        mTemplatesView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new CategoryTemplateAdapter();
        mTemplatesView.setAdapter(mAdapter);

        mViewModel.getItems()
                .observe(this, new Observer<PagedList<CategoryTemplateItem>>() {
                    @Override
                    public void onChanged(@Nullable PagedList<CategoryTemplateItem> categoryTemplateItems) {
                        mAdapter.setList(categoryTemplateItems);
                    }
                });

        return fragmentView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        mContextMenuItemModelView = (CategoryTemplateItemViewModel) v.getTag();

        MenuInflater menuInflater = new MenuInflater(getContext());
        menuInflater.inflate(R.menu.template_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_menu_item:
                openEditItemDialog(mContextMenuItemModelView.getItem());
                return true;
            case R.id.remove_menu_item:
                mContextMenuItemModelView.removeItem();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void openCreateNewItemDialog() {
        mContextMenuItemModelView = null;

        EditTemplateDialogFragment editDialog = EditTemplateDialogFragment.newInstance();
        showEditDialog(editDialog);
    }

    @Override
    public void openEditItemDialog(CategoryTemplateItem item) {
        EditTemplateDialogFragment editDialog = EditTemplateDialogFragment.newInstance(item.getTemplate());
        showEditDialog(editDialog);
    }

    private void showEditDialog(EditTemplateDialogFragment dialog) {
        dialog.setTargetFragment(this, REQUEST_CODE_EDIT_TEMPLATE);
        dialog.show(getFragmentManager(), "EditTemplateDialogFragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_EDIT_TEMPLATE) {

            Toast.makeText(getActivity(), R.string.save_product_success_msg, Toast.LENGTH_SHORT).show();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showItemContextMenu(CategoryTemplateItem item, int position) {
        View itemView = mTemplatesView.getChildAt(position);
        itemView.showContextMenu();
    }

    private class CategoryTemplateViewHolder extends RecyclerView.ViewHolder {

        ViewDataBinding mDataBinding;
        CategoryTemplateItemViewModel mItemViewModel;

        public CategoryTemplateViewHolder(ViewDataBinding dataBinding) {
            super(dataBinding.getRoot());
            mDataBinding = dataBinding;

            mItemViewModel = new CategoryTemplateItemViewModel();
            mItemViewModel.setObserver(TemplatesFragment.this);
            mDataBinding.setVariable(BR.viewModel, mItemViewModel);

            mDataBinding.getRoot().setTag(mItemViewModel);
        }

        public void bind(CategoryTemplateItem item) {
            mItemViewModel.setItem(item);
            if (mItemViewModel.hasContextMenu()) {
                registerForContextMenu(mDataBinding.getRoot());
            } else {
                unregisterForContextMenu(mDataBinding.getRoot());
            }

            mDataBinding.executePendingBindings();
        }
    }

    private class CategoryTemplateAdapter extends PagedListAdapter<CategoryTemplateItem, CategoryTemplateViewHolder> {

        private static final int TYPE_TEMPLATE = 1;
        private static final int TYPE_CATEGORY = 2;

        protected CategoryTemplateAdapter() {
            super(DIFF_CALLBACK);
        }

        @Override
        public CategoryTemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layoutResID = (viewType == TYPE_TEMPLATE)
                    ? R.layout.item_recycler_list
                    : R.layout.item_category_template_list;

            ViewDataBinding dataBinding =
                    DataBindingUtil.inflate(getLayoutInflater(), layoutResID, parent, false);
            return new CategoryTemplateViewHolder(dataBinding);
        }

        @Override
        public void onBindViewHolder(CategoryTemplateViewHolder holder, int position) {
            CategoryTemplateItem item = getItem(position);
            holder.bind(item);
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getType().equals(CategoryTemplateItem.TYPE_TEMPLATE)
                    ? TYPE_TEMPLATE : TYPE_CATEGORY;
        }
    }

    private static final DiffCallback<CategoryTemplateItem> DIFF_CALLBACK = new DiffCallback<CategoryTemplateItem>() {
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
