package com.nzarudna.shoppinglist.ui;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by Nataliia on 02.03.2018.
 */

public abstract class ArrayItemViewModel<T> extends BaseObservable {

    @Bindable
    private T mItem;

    public void setItem(T item) {
        mItem = item;
        notifyChange();
    }

    public T getItem() {
        return mItem;
    }

    public abstract String getItemName();
}
