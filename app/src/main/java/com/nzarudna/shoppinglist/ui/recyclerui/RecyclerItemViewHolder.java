package com.nzarudna.shoppinglist.ui.recyclerui;

import android.app.Activity;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nzarudna.shoppinglist.BR;

/**
 * Created by Nataliia on 09.03.2018.
 */

public abstract class RecyclerItemViewHolder<T, VM extends RecyclerItemViewModel<T>> extends RecyclerView.ViewHolder {

    private Activity mActivity;
    private ViewDataBinding mDataBinding;
    private VM mItemViewModel;
    private View.OnLongClickListener mOnItemLongClickListener;

    public RecyclerItemViewHolder(Activity activity,
                                  ViewDataBinding dataBinding,
                                  @Nullable RecyclerItemViewModel.RecyclerItemViewModelObserver<T> observer) {
        super(dataBinding.getRoot());
        mDataBinding = dataBinding;
        mActivity = activity;

        mItemViewModel = getViewModelInstance();
        mItemViewModel.setObserver(observer);
        mDataBinding.setVariable(BR.viewModel, mItemViewModel);

        mDataBinding.getRoot().setTag(mItemViewModel);
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mOnItemLongClickListener = onLongClickListener;
    }

    protected abstract VM getViewModelInstance();

    public void bind(T item) {
        mItemViewModel.setItem(item);
        if (mItemViewModel.hasContextMenu()) {
            mActivity.registerForContextMenu(mDataBinding.getRoot());
            if (mOnItemLongClickListener != null) {
                mDataBinding.getRoot().setOnLongClickListener(mOnItemLongClickListener);
            }
        } else {
            mActivity.unregisterForContextMenu(mDataBinding.getRoot());
            mDataBinding.getRoot().setOnLongClickListener(null);
        }

        mDataBinding.executePendingBindings();
    }
}
