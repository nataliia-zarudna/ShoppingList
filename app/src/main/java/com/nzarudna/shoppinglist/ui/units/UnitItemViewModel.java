package com.nzarudna.shoppinglist.ui.units;

import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 11.03.2018.
 */

public class UnitItemViewModel extends RecyclerItemViewModel<Unit> {

    @Inject
    UnitRepository mUnitRepository;

    @Override
    public String getItemName() {
        return mItem.getName();
    }

    @Override
    public void removeItem() {
        mUnitRepository.removeUnit(mItem);
    }
}
