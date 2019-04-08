package com.nzarudna.shoppinglist.ui.recyclerui;

import com.nzarudna.shoppinglist.BR;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Nataliia on 09.03.2018.
 */

public abstract class RecyclerItemViewHolder<T, VM extends RecyclerItemViewModel<T>> extends RecyclerView.ViewHolder {

    private static final String TAG = "RecyclerItemViewHolder";

    private Fragment mFragment;
    protected ViewDataBinding mDataBinding;
    private VM mItemViewModel;

    public RecyclerItemViewHolder(Fragment fragment,
                                  ViewDataBinding dataBinding,
                                  @Nullable RecyclerItemViewModel.RecyclerItemViewModelObserver<T> observer) {
        super(dataBinding.getRoot());
        mDataBinding = dataBinding;
        mFragment = fragment;

        mItemViewModel = newItemViewModel();
        mItemViewModel.setObserver(observer);
        mDataBinding.setVariable(BR.viewModel, mItemViewModel);

        mDataBinding.getRoot().setTag(mItemViewModel);
    }

    protected abstract VM newItemViewModel();

    public VM getItemViewModel() {
        return mItemViewModel;
    }

    public void bind(T item, int position) {

        mItemViewModel.setItem(item);
        mItemViewModel.setPosition(position);

        if (mItemViewModel.hasContextMenu()) {
            mFragment.registerForContextMenu(mDataBinding.getRoot());
        } else {
            mFragment.unregisterForContextMenu(mDataBinding.getRoot());
            mDataBinding.getRoot().setOnLongClickListener(null);
        }

        mDataBinding.executePendingBindings();
    }
}
