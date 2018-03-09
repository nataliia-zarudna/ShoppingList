package com.nzarudna.shoppinglist.ui.recyclerui;

import android.app.Activity;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.utils.GenericFactory;

/**
 * Created by Nataliia on 09.03.2018.
 */

public class RecyclerItemViewHolder<T, VM extends RecyclerItemViewModel<T>> extends RecyclerView.ViewHolder {

    private Activity mActivity;
    private ViewDataBinding mDataBinding;
    private VM mItemViewModel;
    private View.OnLongClickListener mOnItemLongClickListener;

    public RecyclerItemViewHolder(Activity activity,
                                  ViewDataBinding dataBinding,
                                  @Nullable RecyclerItemViewModel.RecyclerItemViewModelObserver<T> observer) {
        super(dataBinding.getRoot());
        try {

            mDataBinding = dataBinding;
            mActivity = activity;

            mItemViewModel = new GenericFactory<VM>().newInstance();
            mItemViewModel.setObserver(observer);
            mDataBinding.setVariable(BR.viewModel, mItemViewModel);

            mDataBinding.getRoot().setTag(mItemViewModel);

        } catch (IllegalAccessException | InstantiationException e) {
            //TODO: add error handler
            e.printStackTrace();
        }
    }

    public void setViewDataBinding(ViewDataBinding dataBinding) {

    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mOnItemLongClickListener = onLongClickListener;
    }

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
