package com.nzarudna.shoppinglist.ui.recyclerui;

import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

        Log.d(TAG, "Create " + toString());
        Log.d(TAG, "Create mItemViewModel " + mItemViewModel.toString());
    }

    protected abstract VM getItemViewModel();

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mOnItemLongClickListener = onLongClickListener;
    }

    public void bind(T item) {
        Log.d(TAG, "-----" + toString());
        Log.d(TAG, "Bind item " + item.toString());
        Log.d(TAG, "Bind mDataBinding " + mDataBinding.toString());
        Log.d(TAG, "Bind mItemViewModel " + mItemViewModel.toString());

        mItemViewModel.setItem(item);
        if (mItemViewModel.hasContextMenu()) {
            mFragment.registerForContextMenu(mDataBinding.getRoot());

            Log.d("DEBBUG", "registerForContextMenu " + mDataBinding.getRoot());

            if (mOnItemLongClickListener != null) {
                mDataBinding.getRoot().setOnLongClickListener(mOnItemLongClickListener);
            }
        } else {
            mFragment.unregisterForContextMenu(mDataBinding.getRoot());
            mDataBinding.getRoot().setOnLongClickListener(null);
        }

        mDataBinding.executePendingBindings();
    }
}
