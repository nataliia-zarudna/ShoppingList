package com.nzarudna.shoppinglist.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.nzarudna.shoppinglist.BR;

import java.util.List;

/**
 * Created by Nataliia on 01.03.2018.
 */

public class ViewModelArrayAdapter<T> extends ArrayAdapter<T> {

    @LayoutRes
    private int mLayoutResID;

    private int mViewModelVariableID;
    private Class<? extends ArrayItemViewModel> mViewModelClass;

    public ViewModelArrayAdapter(@NonNull Context context, int resource, @NonNull List<T> objects,
                                 Class<? extends ArrayItemViewModel> viewModelClass) {
        this(context, resource, BR.viewModel, objects, viewModelClass);
    }

    public ViewModelArrayAdapter(@NonNull Context context, int resource, int viewModelVariableID,
                                 @NonNull List<T> objects, Class<? extends ArrayItemViewModel> viewModelClass) {
        super(context, resource, objects);
        mLayoutResID = resource;
        mViewModelVariableID = viewModelVariableID;
        mViewModelClass = viewModelClass;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        try {
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

        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private class ViewHolder<T> {

        ArrayItemViewModel<T> mViewModel;
        ViewDataBinding mViewDataBinding;

        ViewHolder(ViewDataBinding viewDataBinding) throws IllegalAccessException, InstantiationException {
            mViewDataBinding = viewDataBinding;
            mViewModel = mViewModelClass.newInstance();

            mViewDataBinding.setVariable(mViewModelVariableID, mViewModel);
        }

        void bind(T item) {
            mViewModel.setItem(item);
            mViewDataBinding.executePendingBindings();
        }
    }
}
