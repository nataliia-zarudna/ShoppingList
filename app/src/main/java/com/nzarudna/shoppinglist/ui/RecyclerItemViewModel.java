package com.nzarudna.shoppinglist.ui;

/**
 * Created by Nataliia on 06.03.2018.
 */

public abstract class RecyclerItemViewModel<T> extends ArrayItemViewModel<T> {

    protected RecyclerItemViewModelObserver<T> mObserver;
    protected int mPosition;

    public void setObserver(RecyclerItemViewModelObserver<T> observer) {
        this.mObserver = observer;
    }

    public void onFABClick() {
        if (mObserver != null) {
            mObserver.openCreateNewItemDialog();
        }
    }

    public void onItemClick() {
        if (mObserver != null) {
            mObserver.openEditItemDialog(mItem);
        }
    }

    public void onItemLongClick() {
        if (mObserver != null) {
            mObserver.showItemContextMenu(mItem, mPosition);
        }
    }

    public void onMenuItemClick() {
        if (mObserver != null) {
            mObserver.showItemContextMenu(mItem, mPosition);
        }
    }

    public boolean hasContextMenu() {
        return false;
    }

    ;

    public abstract void removeItem();

    public interface RecyclerItemViewModelObserver<T> {
        void openCreateNewItemDialog();

        void openEditItemDialog(T item);

        void showItemContextMenu(T item, int position);
    }

}
