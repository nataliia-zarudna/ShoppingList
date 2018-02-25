package com.nzarudna.shoppinglist.ui.productlist;

import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;

/**
 * Created by Nataliia on 28.01.2018.
 */

public class CategoryProductItemViewModel extends ViewModel implements Observable {

    @Bindable
    private CategoryProductItem mCategoryProductItem;

    private PropertyChangeRegistry mPropertyChangeRegistry = new PropertyChangeRegistry();

    public void setCategoryProductItem(CategoryProductItem categoryProductItem) {
        this.mCategoryProductItem = categoryProductItem;

        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    public String getName() {
        if (mCategoryProductItem != null) {
            return CategoryProductItem.TYPE_PRODUCT.equals(mCategoryProductItem.getType())
                    ? mCategoryProductItem.getProduct().getName()
                    : mCategoryProductItem.getCategory().getName();
        } else {
            return "";
        }
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mPropertyChangeRegistry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mPropertyChangeRegistry.remove(onPropertyChangedCallback);
    }
}
