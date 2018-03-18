package com.nzarudna.shoppinglist.ui.recyclerui;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;

/**
 * Created by nsirobaba on 3/9/18.
 */

public abstract class BaseRecyclerViewFragment
        <T extends Parcelable, VM extends RecyclerViewModel, IVM extends RecyclerItemViewModel<T>>
        extends Fragment
        implements RecyclerItemViewModel.RecyclerItemViewModelObserver<T>,
        RecyclerViewModel.RecyclerViewModelObserver, Observer<PagedList<T>> {

    protected static final int DEFAULT_PAGE_SIZE = 20;
    private static final int REQUEST_CODE_EDIT_ITEM = 1;

    protected LiveData<PagedList<T>> mItemsLiveData;
    protected VM mViewModel;
    protected RecyclerView mRecyclerView;
    protected BaseRecyclerAdapter<T, IVM> mAdapter;
    private IVM mItemModelView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = getFragmentViewModel();
        mViewModel.setObserver(this);

        setHasOptionsMenu(true);
    }

    protected abstract VM getFragmentViewModel();

    protected abstract IVM getListItemViewModel();

    protected abstract EditDialogViewModel<T> getEditDialogViewModel();

    protected abstract DiffCallback<T> getDiffCallback();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewDataBinding dataBinding =
                DataBindingUtil.inflate(inflater, getLayoutResID(), container, false);
        dataBinding.setVariable(BR.viewModel, mViewModel);

        View fragmentView = dataBinding.getRoot();

        mRecyclerView = getRecyclerView(fragmentView);

        mAdapter = getRecyclerViewAdapter();
        mAdapter.setItemLayoutResID(getItemLayoutResID());
        mRecyclerView.setAdapter(mAdapter);

        loadItems();

        return fragmentView;
    }

    protected void loadItems() {
        if (mItemsLiveData != null) {
            mItemsLiveData.removeObserver(this);
        }

        mItemsLiveData = mViewModel.getItems(DEFAULT_PAGE_SIZE);
        mItemsLiveData.observe(this, this);
    }

    @Override
    public void onChanged(@Nullable PagedList<T> items) {
        mAdapter.setList(items);
        mViewModel.onItemsLoad(items.isEmpty());
    }

    @LayoutRes
    protected int getLayoutResID() {
        return R.layout.fragment_recycler_view;
    }

    @LayoutRes
    protected int getItemLayoutResID() {
        return R.layout.item_recycler_list;
    }

    protected RecyclerView getRecyclerView(View fragmentView) {

        RecyclerView mRecyclerView = fragmentView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                //Log.d(TAG, "onSwiped direction " + direction);

                //ProductListItemViewModel itemViewModel = ((ProductListsFragmentOld.ProductListViewHolder) viewHolder).mBinding.getViewModel();
                //removeListItem(itemViewModel, viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(mRecyclerView);

        return mRecyclerView;
    }

    private void removeItem(final IVM itemViewModel, final int position) {

        //final ProductListWithStatistics listToRemove = itemViewModel.getProductList();
//        mProductLists.remove(position);
        //Log.d(TAG, "list to remove " + itemViewModel.getProductList().getName());

        /*mAdapter.notifyItemRemoved(position);

        Snackbar.make(getView(), R.string.message_remove_list, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_removal, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAdapter.notifyItemInserted(position);
                    }
                })
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        if (event != DISMISS_EVENT_ACTION) {
                            Log.d(TAG, "removed list " + itemViewModel.getProductList().getName());
                            itemViewModel.onSwipeProductListItem();
                        }
                    }
                }).show();*/
    }

    protected BaseRecyclerAdapter<T, IVM> getRecyclerViewAdapter() {
        BaseRecyclerAdapter<T, IVM> adapter = new BaseRecyclerAdapter<T, IVM>(this, getDiffCallback()) {
            @Override
            protected IVM getItemViewModel() {
                return getListItemViewModel();
            }
        };
        adapter.setRecyclerItemViewModelObserver(this);
        return adapter;
    }

    @MenuRes
    protected int getItemContextMenuResID() {
        return R.menu.item_context_menu;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        mItemModelView = (IVM) v.getTag();

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

    protected IVM getCurrentItemViewModel() {
        return mItemModelView;
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

    protected BaseEditItemDialogFragment<T, ? extends EditDialogViewModel<T>> getEditItemDialogFragment() {
        BaseEditItemDialogFragment<T, EditDialogViewModel<T>> editDialog = BaseEditItemDialogFragment.newInstance();
        EditDialogViewModel<T> editDialogViewModel = getEditDialogViewModel();
        editDialog.setViewModel(editDialogViewModel);
        return editDialog;
    }

    protected BaseEditItemDialogFragment<T, ? extends EditDialogViewModel<T>> getEditItemDialogFragment(T item) {
        BaseEditItemDialogFragment<T, ? extends EditDialogViewModel<T>> editDialog = getEditItemDialogFragment();
        editDialog.setItem(item);
        return editDialog;
    }

    private void showEditDialog(BaseEditItemDialogFragment<T, ? extends EditDialogViewModel<T>> dialog) {
        dialog.setTargetFragment(this, REQUEST_CODE_EDIT_ITEM);
        dialog.show(getFragmentManager(), "BaseEditItemDialogFragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_EDIT_ITEM) {

            Toast.makeText(getActivity(), R.string.save_item_success_msg, Toast.LENGTH_SHORT).show();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showItemContextMenu(T item, int position) {
        View itemView = mRecyclerView.getChildAt(position);
        itemView.showContextMenu();
    }
}