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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerAdapter;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewHolder;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewModel;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;
import com.nzarudna.shoppinglist.ui.productlist.edit.EditProductListActivity;
import com.nzarudna.shoppinglist.ui.templates.editdialog.EditTemplateDialogFragment;

import java.util.UUID;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class TemplatesFragment extends Fragment
        implements RecyclerItemViewModel.RecyclerItemViewModelObserver<CategoryTemplateItem>,
        RecyclerViewModel.RecyclerViewModelObserver, TemplatesViewModel.TemplatesViewModelObserver {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int REQUEST_CODE_EDIT_TEMPLATE = 1;
    private TemplatesViewModel mViewModel;
    private RecyclerView mTemplatesView;
    private CategoryTemplateAdapter mAdapter;
    private CategoryTemplateItemViewModel mContextMenuItemModelView;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(TemplatesViewModel.class);
        ShoppingListApplication.getAppComponent().inject(mViewModel);
        mViewModel.setObserver(this);
        mViewModel.setTemplateViewObserver(this);

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

        mAdapter = new CategoryTemplateAdapter(getActivity());
        mAdapter.setOnItemLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mActionMode == null) {
                    mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
                }

                CategoryTemplateItemViewModel itemViewModel = (CategoryTemplateItemViewModel) view.getTag();
                itemViewModel.onSelect();
                mViewModel.onItemSelected(itemViewModel.getItem());

                if (mViewModel.getSelectedItemsCount() == 0) {
                    mActionMode.finish();
                } else {
                    String itemsCount = String.valueOf(mViewModel.getSelectedItemsCount());
                    String title = getString(R.string.action_mode_selected_count, itemsCount);
                    mActionMode.setTitle(title);
                }

                return true;
            }
        });
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
        menuInflater.inflate(R.menu.template_context_menu, menu);
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
    public void onCreateProductList(UUID listID) {
        Intent intent = EditProductListActivity.newIntent(getActivity(), listID);
        startActivity(intent);
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

    private class CategoryTemplateViewHolder extends RecyclerItemViewHolder<CategoryTemplateItem, CategoryTemplateItemViewModel> {

        public CategoryTemplateViewHolder(Activity activity,
                                          ViewDataBinding dataBinding,
                                          @Nullable RecyclerItemViewModel.RecyclerItemViewModelObserver<CategoryTemplateItem> observer) {
            super(activity, dataBinding, observer);
        }

        @Override
        protected CategoryTemplateItemViewModel getViewModelInstance() {
            return new CategoryTemplateItemViewModel();
        }
    }

    private class CategoryTemplateAdapter extends BaseRecyclerAdapter<CategoryTemplateItem, CategoryTemplateViewHolder> {

        private static final int TYPE_TEMPLATE = 1;
        private static final int TYPE_CATEGORY = 2;

        protected CategoryTemplateAdapter(Activity activity) {
            super(activity, DIFF_CALLBACK);
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

        @Override
        protected CategoryTemplateViewHolder getViewHolderInstance(ViewDataBinding dataBinding) {
            return new CategoryTemplateViewHolder(getActivity(), dataBinding, TemplatesFragment.this);
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
