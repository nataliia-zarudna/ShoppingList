package com.nzarudna.shoppinglist.ui;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.ui.productlists.CopyListDialogFragment;

import java.util.List;

/**
 * Created by Nataliia on 01.03.2018.
 */

public class ViewModelArrayAdapter<T> extends ArrayAdapter<T> {

    @LayoutRes
    private int mLayoutResID;

    private Fragment mFragment;
    private int mViewModelVariableID;
    private Class<? extends ArrayItemViewModel> mViewModelClass;

    public ViewModelArrayAdapter(@NonNull Fragment fragment, int resource, @NonNull List<T> objects,
                                 Class<? extends ArrayItemViewModel> viewModelClass) {
        this(fragment, resource, BR.viewModel, objects, viewModelClass);
    }

    public ViewModelArrayAdapter(@NonNull Fragment fragment, int resource, int viewModelVariableID,
                                 @NonNull List<T> objects, Class<? extends ArrayItemViewModel> viewModelClass) {
        super(fragment.getContext(), resource, objects);
        mFragment = fragment;
        mLayoutResID = resource;
        mViewModelVariableID = viewModelVariableID;
        mViewModelClass = viewModelClass;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            ViewDataBinding dataBinding = DataBindingUtil.inflate(layoutInflater, mLayoutResID, parent, false);
            view = dataBinding.getRoot();

            viewHolder = new ViewHolder(dataBinding);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        T item = getItem(position);
        viewHolder.bind(item);

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private class ViewHolder<T> {

        ArrayItemViewModel<T> mViewModel;
        ViewDataBinding mViewDataBinding;

        ViewHolder(ViewDataBinding viewDataBinding) {
            mViewDataBinding = viewDataBinding;
            mViewModel = ViewModelProviders.of(mFragment).get(mViewModelClass);

            mViewDataBinding.setVariable(mViewModelVariableID, mViewModel);
        }

        void bind(T item) {
            mViewModel.setItem(item);
            mViewDataBinding.executePendingBindings();
        }
    }
}
