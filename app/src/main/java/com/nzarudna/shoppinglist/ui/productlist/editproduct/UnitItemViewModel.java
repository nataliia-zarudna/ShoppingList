package com.nzarudna.shoppinglist.ui.productlist.editproduct;

import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.ui.ArrayItemViewModel;

/**
 * Created by Nataliia on 02.03.2018.
 */

public class UnitItemViewModel extends ArrayItemViewModel<Unit> {

    @Override
    public String getItemName() {
        return getItem().getName();
    }
}
