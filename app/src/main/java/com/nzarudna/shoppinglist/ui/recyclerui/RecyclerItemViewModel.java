package com.nzarudna.shoppinglist.ui.recyclerui;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.ui.ArrayItemViewModel;

/**
 * Created by Nataliia on 06.03.2018.
 */

public abstract class RecyclerItemViewModel<T> extends ArrayItemViewModel<T> {

    protected RecyclerItemViewModelObserver<T> mObserver;
    protected int mPosition;
    protected boolean mIsSelected;

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public void setObserver(RecyclerItemViewModelObserver<T> observer) {
        this.mObserver = observer;
    }

    public void onSelect() {
        this.mIsSelected = !mIsSelected;
        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void onItemClick() {
        if (mObserver != null) {
            mObserver.openEditItemDialog(mItem);
        }
    }

    public boolean onItemLongClick() {
        if (mObserver != null) {
            mObserver.showItemContextMenu(mItem, mPosition);
            return true;
        }
        return false;
    }

    public void onMenuItemClick() {
        if (mObserver != null) {
            mObserver.showItemContextMenu(mItem, mPosition);
        }
    }

    public boolean hasContextMenu() {
        return true;
    }

    public abstract void removeItem();

    public interface RecyclerItemViewModelObserver<T> {

        void openEditItemDialog(T item);

        void showItemContextMenu(T item, int position);
    }

}
