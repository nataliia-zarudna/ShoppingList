package com.nzarudna.shoppinglist.ui.units;

import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.EditDialogViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 11.03.2018.
 */

public class EditUnitViewModel extends EditDialogViewModel<Unit> {

    @Inject
    UnitRepository mUnitRepository;

    @Override
    protected Unit createItemObject() {
        return new Unit("");
    }

    @Override
    public String getName() {
        return mItem.getName();
    }

    @Override
    public void setName(String name) {
        mItem.setName(name);
    }

    @Override
    protected void updateItem() {
        mUnitRepository.updateUnit(mItem);
    }

    @Override
    protected void createItem() {
        mUnitRepository.createUnit(mItem);
    }
}
