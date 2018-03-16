package com.nzarudna.shoppinglist.ui.units;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 11.03.2018.
 */

public class UnitsViewModel extends RecyclerViewModel<Unit> {

    @Inject
    UnitRepository mUnitRepository;

    @Override
    public LiveData<PagedList<Unit>> getItems(int pageSize) {
        DataSource.Factory<Integer, Unit> unitsFactory = mUnitRepository.getAllUnits();
        return new LivePagedListBuilder(unitsFactory, pageSize).build();
    }
}
