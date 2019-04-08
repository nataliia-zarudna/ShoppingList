package com.nzarudna.shoppinglist.ui;

import android.text.Spannable;
import android.text.SpannableString;

import com.nzarudna.shoppinglist.BR;

import androidx.databinding.Bindable;

/**
 * Created by Nataliia on 02.03.2018.
 */

public abstract class ArrayItemViewModel<T> extends ObservableViewModel {

    @Bindable
    protected T mItem;

    public void setItem(T item) {
        mItem = item;
        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    public T getItem() {
        return mItem;
    }

    public abstract String getItemName();

    public Spannable getFormatItemName() {
       return new SpannableString(getItemName());
    }
}
