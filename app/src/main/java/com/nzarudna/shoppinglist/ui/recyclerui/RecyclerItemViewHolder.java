package com.nzarudna.shoppinglist.ui.recyclerui;

import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nzarudna.shoppinglist.BR;

/**
 * Created by Nataliia on 09.03.2018.
 */

public abstract class RecyclerItemViewHolder<T, VM extends RecyclerItemViewModel<T>> extends RecyclerView.ViewHolder {

    private static final String TAG = "RecyclerItemViewHolder";

    private Fragment mFragment;
    private ViewDataBinding mDataBinding;
    private VM mItemViewModel;
    private View.OnLongClickListener mOnItemLongClickListener;
    private View.OnClickListener mOnItemClickListener;

    public RecyclerItemViewHolder(Fragment fragment,
                                  ViewDataBinding dataBinding,
                                  @Nullable RecyclerItemViewModel.RecyclerItemViewModelObserver<T> observer) {
        super(dataBinding.getRoot());
        mDataBinding = dataBinding;
        mFragment = fragment;

        mItemViewModel = getItemViewModel();
        mItemViewModel.setObserver(observer);
        mDataBinding.setVariable(BR.viewModel, mItemViewModel);

        mDataBinding.getRoot().setTag(mItemViewModel);
    }

    protected abstract VM getItemViewModel();

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mOnItemLongClickListener = onLongClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnItemClickListener = onClickListener;
    }

    public void bind(T item, int position) {

        mItemViewModel.setItem(item);
        mItemViewModel.setPosition(position);

        if (mItemViewModel.hasContextMenu()) {
            mFragment.registerForContextMenu(mDataBinding.getRoot());

            if (mOnItemLongClickListener != null) {
                mDataBinding.getRoot().setOnLongClickListener(mOnItemLongClickListener);
            }
            if (mOnItemClickListener != null) {
                mDataBinding.getRoot().setOnLongClickListener(mOnItemLongClickListener);
            }
        } else {
            mFragment.unregisterForContextMenu(mDataBinding.getRoot());
            mDataBinding.getRoot().setOnLongClickListener(null);
        }

        mDataBinding.executePendingBindings();
    }
}
