package com.nzarudna.shoppinglist.ui.recyclerui;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import com.nzarudna.shoppinglist.ui.ObservableViewModel;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nataliia on 07.03.2018.
 */

public abstract class RecyclerViewModel<T> extends ObservableViewModel {

    protected RecyclerViewModelObserver mObserver;
    protected List<T> mSelectedItems = new LinkedList<>();

    public void setObserver(RecyclerViewModelObserver observer) {
        this.mObserver = observer;
    }

    public abstract LiveData<PagedList<T>> getItems(int pageSize);

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
