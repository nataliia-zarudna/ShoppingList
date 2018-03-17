package com.nzarudna.shoppinglist.ui.productlists;

import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.ui.ArrayItemViewModel;

/**
 * Created by Nataliia on 17.03.2018.
 */

public class CopyListItemViewModel extends ArrayItemViewModel<ProductList> {

    @Override
    public String getItemName() {
        return mItem.getName();
    }
}
