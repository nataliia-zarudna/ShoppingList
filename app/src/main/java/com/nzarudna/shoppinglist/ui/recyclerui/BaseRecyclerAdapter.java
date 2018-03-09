package com.nzarudna.shoppinglist.ui.recyclerui;

import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.view.View;
import android.view.ViewGroup;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.utils.GenericFactory;

/**
 * Created by Nataliia on 09.03.2018.
 */

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerItemViewHolder> extends PagedListAdapter<T, VH> {

    private Activity mActivity;
    private View.OnLongClickListener mOnItemLongClickListener;

    protected BaseRecyclerAdapter(Activity activity, DiffCallback<T> diffCallback) {
        super(diffCallback);
        mActivity = activity;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutResID = getItemLayoutResID(viewType);
        ViewDataBinding dataBinding =
                DataBindingUtil.inflate(mActivity.getLayoutInflater(), layoutResID, parent, false);
        VH viewHolder = getViewHolderInstance(dataBinding);
        viewHolder.setOnLongClickListener(mOnItemLongClickListener);

        return viewHolder;
    }

    protected VH getViewHolderInstance(ViewDataBinding dataBinding) {
        try {
            VH viewHolder = new GenericFactory<VH>().newInstance();
            viewHolder.setViewDataBinding(dataBinding);

        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @LayoutRes
    protected int getItemLayoutResID(int viewType) {
        return R.layout.item_recycler_list;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        T item = getItem(position);
        holder.bind(item);
    }

    public void setOnItemLongClickListener(View.OnLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }
}
