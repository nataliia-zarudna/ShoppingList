package com.nzarudna.shoppinglist.ui.recyclerui;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.databinding.Bindable;
import android.support.annotation.StringRes;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ui.ObservableViewModel;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nataliia on 07.03.2018.
 */

public abstract class RecyclerViewModel<T> extends ObservableViewModel {

    protected List<T> mSelectedItems;
    protected RecyclerViewModelObserver mObserver;

    @Bindable
    private boolean listEmpty;

    public RecyclerViewModel() {
        mSelectedItems = new LinkedList<>();
        listEmpty = false;
    }

    public void setObserver(RecyclerViewModelObserver observer) {
        this.mObserver = observer;
    }

    public abstract LiveData<PagedList<T>> getItems(int pageSize);

    public boolean getListEmpty() {
        return listEmpty;
    }

    @StringRes
    public int getNoItemsMessage() {
        return R.string.list_is_empty_title;
    }

    public void onItemsLoad(boolean isListEmpty) {
        listEmpty = isListEmpty;
        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    public void onItemSelected(T item) {
        if (!mSelectedItems.contains(item)) {
            mSelectedItems.add(item);
        } else {
            mSelectedItems.remove(item);
        }
    }

    public int getSelectedItemsCount() {
        return mSelectedItems.size();
    }

    public boolean canCreateNewItem() {
        return true;
    }

    public void onFABClick() {
        if (mObserver != null) {
            mObserver.openCreateNewItemDialog();
        }
    }

    public void deselectAllItems() {
        mSelectedItems = new LinkedList<>();
    }

    public interface RecyclerViewModelObserver {
        void openCreateNewItemDialog();
    }
}
