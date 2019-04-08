package com.nzarudna.shoppinglist.ui.recyclerui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nzarudna.shoppinglist.R;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

/**
 * Created by Nataliia on 09.03.2018.
 */

public abstract class BaseRecyclerAdapter<T, IVM extends RecyclerItemViewModel<T>> extends PagedListAdapter<T, RecyclerItemViewHolder<T, IVM>> {

    private static final String TAG = "BaseRecyclerAdapter";

    protected Fragment mFragment;
    private int mItemLayoutResID;
    protected RecyclerItemViewModel.RecyclerItemViewModelObserver<T> mRecyclerItemViewModelObserver;

    protected BaseRecyclerAdapter(Fragment fragment, DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
        mFragment = fragment;

        mItemLayoutResID = R.layout.item_recycler_list;
    }

    @Override
    public RecyclerItemViewHolder<T, IVM> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layoutResID = getItemLayoutResID(viewType);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding dataBinding =
                DataBindingUtil.inflate(layoutInflater, layoutResID, parent, false);

        return getViewHolderInstance(dataBinding);
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
}
