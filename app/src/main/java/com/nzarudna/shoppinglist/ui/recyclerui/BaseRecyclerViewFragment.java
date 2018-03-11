package com.nzarudna.shoppinglist.ui.recyclerui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.ui.categories.EditCategoryViewModel;

/**
 * Created by nsirobaba on 3/9/18.
 */

public abstract class BaseRecyclerViewFragment
        <T extends Parcelable, VM extends RecyclerViewModel,
                IVM extends RecyclerItemViewModel, EVM extends EditDialogViewModel<T>>
        extends Fragment
        implements RecyclerItemViewModel.RecyclerItemViewModelObserver<T>,
        RecyclerViewModel.RecyclerViewModelObserver {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int REQUEST_CODE_EDIT_TEMPLATE = 1;
    protected VM mViewModel;
    protected RecyclerView mRecyclerView;
    protected BaseRecyclerAdapter<T, IVM> mAdapter;
    protected RecyclerItemViewModel<T> mItemModelView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = getFragmentViewModel();
        mViewModel.setObserver(this);

        setHasOptionsMenu(true);
    }

    protected abstract VM getFragmentViewModel();

    protected abstract IVM getListItemViewModel();

    protected abstract EVM getEditDialogViewModel();

    protected abstract DiffCallback<T> getDiffCallback();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewDataBinding dataBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_recycler_view_with_fab, container, false);
        dataBinding.setVariable(BR.viewModel, mViewModel);

        View fragmentView = dataBinding.getRoot();

        mRecyclerView = fragmentView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new BaseRecyclerAdapter<T, IVM>(this, getDiffCallback()) {
            @Override
            protected IVM getItemViewModel() {
                return getListItemViewModel();
            }
        };
        mAdapter.setRecyclerItemViewModelObserver(this);
        mRecyclerView.setAdapter(mAdapter);

        mViewModel.getItems(DEFAULT_PAGE_SIZE)
                .observe(this, new Observer<PagedList<T>>() {
                    @Override
                    public void onChanged(@Nullable PagedList<T> items) {
                        mAdapter.setList(items);
                    }
                });

        return fragmentView;
    }

    @MenuRes
    protected int getItemContextMenuResID() {
        return R.menu.item_context_menu;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        mItemModelView = (RecyclerItemViewModel<T>) v.getTag();

        MenuInflater menuInflater = new MenuInflater(getContext());
        menuInflater.inflate(getItemContextMenuResID(), menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_menu_item:
                openEditItemDialog(mItemModelView.getItem());
                return true;
            case R.id.remove_menu_item:
                mItemModelView.removeItem();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void openCreateNewItemDialog() {
        mItemModelView = null;

        BaseEditItemDialogFragment editDialog = getEditItemDialogFragment();
        showEditDialog(editDialog);
    }

    @Override
    public void openEditItemDialog(T item) {
        BaseEditItemDialogFragment editDialog = getEditItemDialogFragment(item);
        showEditDialog(editDialog);
    }

    protected BaseEditItemDialogFragment<T, EVM> getEditItemDialogFragment() {
        BaseEditItemDialogFragment<T, EVM> editDialog = BaseEditItemDialogFragment.newInstance();
        EVM editDialogViewModel = getEditDialogViewModel();
        editDialog.setViewModel(editDialogViewModel);
        return editDialog;
    }

    protected BaseEditItemDialogFragment<T, EVM> getEditItemDialogFragment(T item) {
        BaseEditItemDialogFragment<T, EVM> editDialog = getEditItemDialogFragment();
        editDialog.setArguments(item);
        return editDialog;
    }

    private void showEditDialog(BaseEditItemDialogFragment<T, EVM> dialog) {
        dialog.setTargetFragment(this, REQUEST_CODE_EDIT_TEMPLATE);
        dialog.show(getFragmentManager(), "EditTemplateDialogFragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_EDIT_TEMPLATE) {

            Toast.makeText(getActivity(), R.string.save_item_success_msg, Toast.LENGTH_SHORT).show();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showItemContextMenu(T item, int position) {
        View itemView = mRecyclerView.getChildAt(position);
        Log.d("DEBBUG", "itemView " + itemView);
        itemView.showContextMenu();
    }
}