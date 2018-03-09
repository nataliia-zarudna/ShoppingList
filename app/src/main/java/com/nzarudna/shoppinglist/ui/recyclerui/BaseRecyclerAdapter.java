package com.nzarudna.shoppinglist.ui.recyclerui;

import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.view.View;
import android.view.ViewGroup;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.utils.GenericFactory;

/**
 * Created by Nataliia on 09.03.2018.
 */

public abstract class BaseRecyclerAdapter<T, IVM extends RecyclerItemViewModel> extends PagedListAdapter<T, RecyclerItemViewHolder> {

    private Activity mActivity;
    private View.OnLongClickListener mOnItemLongClickListener;
    private RecyclerItemViewModel.RecyclerItemViewModelObserver<T> mRecyclerItemViewModelObserver;

    protected BaseRecyclerAdapter(Activity activity, DiffCallback<T> diffCallback) {
        super(diffCallback);
        mActivity = activity;
    }

    @Override
    public RecyclerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutResID = getItemLayoutResID(viewType);
        ViewDataBinding dataBinding =
                DataBindingUtil.inflate(mActivity.getLayoutInflater(), layoutResID, parent, false);
        RecyclerItemViewHolder viewHolder = getViewHolderInstance(dataBinding);
        viewHolder.setOnLongClickListener(mOnItemLongClickListener);

        return viewHolder;
    }

    protected RecyclerItemViewHolder getViewHolderInstance(ViewDataBinding dataBinding) {

        return new RecyclerItemViewHolder(mActivity, dataBinding, mRecyclerItemViewModelObserver) {
            @Override
            protected RecyclerItemViewModel<IVM> getItemViewModel() {
                return BaseRecyclerAdapter.this.getItemViewModel();
            }
        };
    }

    protected abstract IVM getItemViewModel();

    @LayoutRes
    protected int getItemLayoutResID(int viewType) {
        return R.layout.item_recycler_list;
    }

    @Override
    public void onBindViewHolder(RecyclerItemViewHolder holder, int position) {
        T item = getItem(position);
        holder.bind(item);
    }

    public void setRecyclerItemViewModelObserver(RecyclerItemViewModel.RecyclerItemViewModelObserver<T> observer) {
        this.mRecyclerItemViewModelObserver = observer;
    }

    public void setOnItemLongClickListener(View.OnLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }
}
