package com.nzarudna.shoppinglist.model.unit;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;

import com.nzarudna.shoppinglist.model.BaseRepository;
import com.nzarudna.shoppinglist.model.ModelUtils;
import com.nzarudna.shoppinglist.model.exception.NameIsEmptyException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.utils.AppExecutors;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by nsirobaba on 2/27/18.
 */

public class UnitRepository extends BaseRepository<Unit> {

    @Inject
    UnitDao mUnitDao;

//    @Inject
//    public UnitRepository(UnitDao unitDao, AppExecutors appExecutors) {
//        super(appExecutors);
//        this.mUnitDao = unitDao;
//    }

    //TODO: add tests. start

    @Override
    protected Unit create(Unit unit) throws Exception {
        validateUnitName(mUnitDao, unit.getName());
        mUnitDao.insert(unit);

        return unit;
    }

    @Override
    protected Unit update(Unit unit) throws Exception {
        validateUnitName(mUnitDao, unit.getName());
        mUnitDao.update(unit);

        return unit;
    }

    @Override
    protected void remove(Unit unit) {
        mUnitDao.delete(unit);
    }

    private static void validateUnitName(UnitDao unitDao, String name) throws NameIsEmptyException, UniqueNameConstraintException {
        ModelUtils.validateNameIsNotEmpty(name);

        if (unitDao.isUnitsWithSameNameExists(name)) {
            throw new UniqueNameConstraintException("Unit with name '" + name + "' already exists");
        }
    }

    public LiveData<List<Unit>> getAvailableUnits() {
        return mUnitDao.findAllLiveData();
    }

    public DataSource.Factory<Integer, Unit> getAllUnits() {
        return mUnitDao.findAll();
    }

    //TODO: add tests. end
}
