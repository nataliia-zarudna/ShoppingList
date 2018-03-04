package com.nzarudna.shoppinglist.ui.productlist.edit;

import com.nzarudna.shoppinglist.model.ShoppingListException;
import com.nzarudna.shoppinglist.ui.productlist.CategoryProductItemViewModel;

/**
 * Created by Nataliia on 04.03.2018.
 */

public class EditCategoryProductItemViewModel extends CategoryProductItemViewModel {

    @Override
    public void onProductClick() {
        if (mObserver != null) {
            try {
                mObserver.openEditProductDialog(getProduct());
            } catch (ShoppingListException e) {
                //TODO: add event handler
                e.printStackTrace();
            }
        }
    }
}
