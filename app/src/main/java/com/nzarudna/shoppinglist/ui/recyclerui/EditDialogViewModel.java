package com.nzarudna.shoppinglist.ui.recyclerui;

import android.databinding.Bindable;
import android.support.annotation.Nullable;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ResourceResolver;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.exception.NameIsEmptyException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.ui.ObservableViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 06.03.2018.
 */

public abstract class EditDialogViewModel<T> extends ObservableViewModel implements AsyncResultListener<Void> {

    @Inject
    protected ResourceResolver mResourceResolver;

    @Bindable
    protected T mItem;
    @Bindable
    protected boolean mIsNew;
    @Bindable
    private String mValidationMessage;

    private OnSaveItemListener mOnSaveItemListener;

    public void setItem(T item) {
        if (item != null) {
            this.mItem = item;
        } else {
            this.mItem = createItemObject();
            mIsNew = true;
        }
        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    public String getValidationMessage() {
        return mValidationMessage;
    }

    public void setOnSaveItemListener(OnSaveItemListener observer) {
        this.mOnSaveItemListener = observer;
    }

    protected abstract T createItemObject();

    public abstract String getName();

    public abstract void setName(String name);

    public String getDialogTitle() {
        return !mIsNew ? getName() : mResourceResolver.getString(R.string.new_entity_dialog_title);
    }

    public void saveItem(@Nullable OnSaveItemListener listener) {
        mOnSaveItemListener = listener;

        if (mIsNew) {
            createItem(this);
        } else {
            updateItem(this);
        }
    }

    @Override
    public void onAsyncSuccess(Void v) {
        if (mValidationMessage == null && mOnSaveItemListener != null) {
            mOnSaveItemListener.onSuccess();
        }
    }

    protected abstract String getUniqueNameValidationMessage();

    @Override
    public void onAsyncError(Exception e) {
        if (e instanceof NameIsEmptyException) {
            mValidationMessage = mResourceResolver.getString(R.string.name_is_empty_validation_message);
        } else if (e instanceof UniqueNameConstraintException) {
            mValidationMessage = getUniqueNameValidationMessage();
        } else {
            mValidationMessage = mResourceResolver.getString(R.string.error_occurd_message);
        }
        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    protected abstract void updateItem(AsyncResultListener asyncResultListener);

    protected abstract void createItem(AsyncResultListener asyncResultListener);

    public interface OnSaveItemListener {
        void onSuccess();
    }
}
