package com.nzarudna.shoppinglist.ui;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ResourceResolver;

import javax.inject.Inject;

/**
 * Created by Nataliia on 06.03.2018.
 */

public abstract class EditDialogViewModel<T> extends ObservableViewModel {

    @Inject
    ResourceResolver mResourceResolver;

    protected T mItem;
    protected boolean mIsNew;

    public void setItem(T item) {
        if (item != null) {
            this.mItem = item;
        } else {
            this.mItem = createNewItem();
            mIsNew = true;
        }
        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    protected abstract T createNewItem();

    public abstract String getName();

    public abstract void setName(String name);

    public String getDialogTitle() {
        return !mIsNew ? getName() : mResourceResolver.getString(R.string.new_entity_dialog_title);
    }

    public abstract void saveItem();
}
