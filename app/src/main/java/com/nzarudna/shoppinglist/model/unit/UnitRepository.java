package com.nzarudna.shoppinglist.model.unit;

import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by nsirobaba on 2/27/18.
 */

public class UnitRepository {

    private UnitDao mUnitDao;

    @Inject
    public UnitRepository(UnitDao unitDao) {
        this.mUnitDao = unitDao;
    }

    public LiveData<List<Unit>> getAvailableUnits() {
        return mUnitDao.findAll();
    }

}
