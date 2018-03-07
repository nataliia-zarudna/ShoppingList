package com.nzarudna.shoppinglist.ui;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

/**
 * Created by Nataliia on 07.03.2018.
 */

public abstract class RecyclerViewModel<T> extends ObservableViewModel {

    protected static final int DEFAULT_PAGE_SIZE = 20;

    protected RecyclerViewModelObserver mObserver;

    public void setObserver(RecyclerViewModelObserver observer) {
        this.mObserver = observer;
    }

    /*public LiveData<PagedList<T>> getItems() {
        return getItems(DEFAULT_PAGE_SIZE);
    }*/

    public abstract LiveData<PagedList<T>> getItems();

    public void onFABClick() {
        if (mObserver != null) {
            mObserver.openCreateNewItemDialog();
        }
    }

    public interface RecyclerViewModelObserver {
        void openCreateNewItemDialog();
    }

}
