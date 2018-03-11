package com.nzarudna.shoppinglist.ui.recyclerui;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.nzarudna.shoppinglist.R;

/**
 * Created by Nataliia on 09.03.2018.
 */

public abstract class BaseRecyclerAdapter<T, IVM extends RecyclerItemViewModel> extends PagedListAdapter<T, RecyclerItemViewHolder> {

    private static final String TAG = "BaseRecyclerAdapter";

    private Fragment mFragment;
    private View.OnLongClickListener mOnItemLongClickListener;
    private RecyclerItemViewModel.RecyclerItemViewModelObserver<T> mRecyclerItemViewModelObserver;

    protected BaseRecyclerAdapter(Fragment fragment, DiffCallback<T> diffCallback) {
        super(diffCallback);
        mFragment = fragment;
    }

    @Override
    public RecyclerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutResID = getItemLayoutResID(viewType);
        ViewDataBinding dataBinding =
                DataBindingUtil.inflate(mFragment.getLayoutInflater(), layoutResID, parent, false);

        RecyclerItemViewHolder viewHolder = getViewHolderInstance(dataBinding);
        viewHolder.setOnLongClickListener(mOnItemLongClickListener);

        return viewHolder;
    }

    protected RecyclerItemViewHolder getViewHolderInstance(ViewDataBinding dataBinding) {

        RecyclerItemViewHolder viewHolder = new RecyclerItemViewHolder(mFragment, dataBinding, mRecyclerItemViewModelObserver) {
            @Override
            protected RecyclerItemViewModel<IVM> getItemViewModel() {
                return BaseRecyclerAdapter.this.getItemViewModel();
            }
        };
        Log.d(TAG, "Adapter view holder " + viewHolder.toString());

        return viewHolder;
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
