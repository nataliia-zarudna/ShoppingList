package com.nzarudna.shoppinglist.ui.shoppinglist;

import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable;

import com.nzarudna.shoppinglist.persistence.CategoryProductItem;

/**
 * Created by Nataliia on 28.01.2018.
 */

public class CategoryProductItemViewModel extends ViewModel {

    //@Bindable
    private CategoryProductItem mCategoryProductItem;

    public void setCategoryProductItem(CategoryProductItem categoryProductItem) {
        this.mCategoryProductItem = categoryProductItem;
    }

    public String getName() {
        if (mCategoryProductItem != null) {
            return CategoryProductItem.ITEM_PRODUCT_TYPE.equals(mCategoryProductItem.getType())
                    ? mCategoryProductItem.getProduct().getName()
                    : mCategoryProductItem.getCategory().getName();
        } else {
            return "";
        }
    }
}
