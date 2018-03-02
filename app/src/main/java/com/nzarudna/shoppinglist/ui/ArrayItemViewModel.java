package com.nzarudna.shoppinglist.ui;

import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.nzarudna.shoppinglist.BR;

/**
 * Created by Nataliia on 02.03.2018.
 */

public abstract class ArrayItemViewModel<T> extends ViewModel implements Observable {

    private PropertyChangeRegistry mRegistry = new PropertyChangeRegistry();

    @Bindable
    private T mItem;

    public void setItem(T item) {
        mItem = item;
        mRegistry.notifyChange(this, BR._all);
    }

    public T getItem() {
        return mItem;
    }

    public abstract String getItemName();

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mRegistry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mRegistry.remove(onPropertyChangedCallback);
    }
}
