package com.nzarudna.shoppinglist.ui.recyclerui;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ResourceResolver;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.ui.ObservableViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 06.03.2018.
 */

public abstract class EditDialogViewModel<T> extends ObservableViewModel implements AsyncResultListener {

    @Inject
    ResourceResolver mResourceResolver;

    protected T mItem;
    protected boolean mIsNew;
    private EditDialogViewModelObserver mEditDialogViewModelObserver;

    public void setItem(T item) {
        if (item != null) {
            this.mItem = item;
        } else {
            this.mItem = createItemObject();
            mIsNew = true;
        }
        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    public void setEditDialogViewModelObserver(EditDialogViewModelObserver observer) {
        this.mEditDialogViewModelObserver = observer;
    }

    protected abstract T createItemObject();

    public abstract String getName();

    public abstract void setName(String name);

    public String getDialogTitle() {
        return !mIsNew ? getName() : mResourceResolver.getString(R.string.new_entity_dialog_title);
    }

    public void saveItem() {
        if (mIsNew) {
            createItem();
        } else {
            updateItem();
        }
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(Exception e) {
        mValidationMessage = e.getMessage()
    }

    protected abstract void updateItem();

    protected abstract void createItem();

    interface EditDialogViewModelObserver {
        void showNameValidationError(String message);
    }
}
