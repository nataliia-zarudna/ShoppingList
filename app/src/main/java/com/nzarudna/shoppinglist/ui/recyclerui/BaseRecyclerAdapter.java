package com.nzarudna.shoppinglist.ui.recyclerui;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.view.View;
import android.view.ViewGroup;

import com.nzarudna.shoppinglist.R;

/**
 * Created by Nataliia on 09.03.2018.
 */

public abstract class BaseRecyclerAdapter<T, IVM extends RecyclerItemViewModel<T>> extends PagedListAdapter<T, RecyclerItemViewHolder<T, IVM>> {

    private static final String TAG = "BaseRecyclerAdapter";

    protected Fragment mFragment;
    private int mItemLayoutResID;
    protected RecyclerItemViewModel.RecyclerItemViewModelObserver<T> mRecyclerItemViewModelObserver;
    private View.OnLongClickListener mOnItemLongClickListener;
    private View.OnClickListener mOnItemClickListener;

    protected BaseRecyclerAdapter(Fragment fragment, DiffCallback<T> diffCallback) {
        super(diffCallback);
        mFragment = fragment;

        mItemLayoutResID = R.layout.item_recycler_list;
    }

    @Override
    public RecyclerItemViewHolder<T, IVM> onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutResID = getItemLayoutResID(viewType);
        ViewDataBinding dataBinding =
                DataBindingUtil.inflate(mFragment.getLayoutInflater(), layoutResID, parent, false);

        RecyclerItemViewHolder<T, IVM> viewHolder = getViewHolderInstance(dataBinding);
        viewHolder.setOnLongClickListener(mOnItemLongClickListener);

        return viewHolder;
    }

    protected RecyclerItemViewHolder<T, IVM> getViewHolderInstance(ViewDataBinding dataBinding) {
        return new RecyclerItemViewHolder<T, IVM>(mFragment, dataBinding, mRecyclerItemViewModelObserver) {
            @Override
            protected IVM newItemViewModel() {
                return BaseRecyclerAdapter.this.getItemViewModel();
            }
        };
    }

    protected abstract IVM getItemViewModel();

    @LayoutRes
    protected int getItemLayoutResID(int viewType) {
        return mItemLayoutResID;
    }

    public void setItemLayoutResID(@LayoutRes int itemLayoutResID) {
        this.mItemLayoutResID = itemLayoutResID;
    }

    @Override
    public void onBindViewHolder(RecyclerItemViewHolder<T, IVM> holder, int position) {
        T item = getItem(position);
        holder.bind(item, position);
    }

    public void setRecyclerItemViewModelObserver(RecyclerItemViewModel.RecyclerItemViewModelObserver<T> observer) {
        this.mRecyclerItemViewModelObserver = observer;
    }

    public void setOnItemLongClickListener(View.OnLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
