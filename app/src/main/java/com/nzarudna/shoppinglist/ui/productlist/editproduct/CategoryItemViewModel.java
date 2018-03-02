package com.nzarudna.shoppinglist.ui.productlist.editproduct;

import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.ui.ArrayItemViewModel;

/**
 * Created by nsirobaba on 3/2/18.
 */

public class CategoryItemViewModel extends ArrayItemViewModel<Category> {

    @Override
    public String getItemName() {
        return getItem().getName();
    }
}
