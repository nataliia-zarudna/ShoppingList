package com.nzarudna.shoppinglist.model.unit;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.ModelUtils;
import com.nzarudna.shoppinglist.model.exception.NameIsEmptyException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;

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

    public DataSource.Factory<Integer, Unit> getAllUnits() {
        return mUnitDao.findAllDataSource();
    }

    //TODO: add tests. start

    private static class CreateUpdateAsyncTask extends AsyncTask<Unit, Void, Void> {

        UnitDao mUnitDao;
        AsyncResultListener mListener;
        boolean mIsCreate;

        CreateUpdateAsyncTask(UnitDao unitDao, @Nullable AsyncResultListener listener, boolean isCreate) {
            mUnitDao = unitDao;
            mListener = listener;
            mIsCreate = isCreate;
        }

        @Override
        protected Void doInBackground(Unit... units) {

            Unit unit = units[0];

            try {
                String trimmedName = unit.getName();
                unit.setName(trimmedName);
                validateUnitName(mUnitDao, unit.getName());

                if (mIsCreate) {
                    mUnitDao.insert(unit);
                } else {
                    mUnitDao.update(unit);
                }

                if (mListener != null) {
                    mListener.onAsyncSuccess();
                }

            } catch (NameIsEmptyException | UniqueNameConstraintException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void createUnit(Unit unit, @Nullable AsyncResultListener listener) {
        new CreateUpdateAsyncTask(mUnitDao, listener, true).execute(unit);
    }

    public void updateUnit(Unit unit, @Nullable AsyncResultListener listener) {
        new CreateUpdateAsyncTask(mUnitDao, listener, false).execute(unit);
    }


    private static void validateUnitName(UnitDao unitDao, String name) throws NameIsEmptyException, UniqueNameConstraintException {
        ModelUtils.validateNameIsNotEmpty(name);

        if (unitDao.isUnitsWithSameNameExists(name)) {
            throw new UniqueNameConstraintException("Unit with name '" + name + "' already exists");
        }
    }



    private static class RemoveAsyncTask extends AsyncTask<Unit, Void, Void> {

        UnitDao mUnitDao;

        RemoveAsyncTask(UnitDao unitDao) {
            mUnitDao = unitDao;
        }

        @Override
        protected Void doInBackground(Unit... units) {
            Unit unit = units[0];
            mUnitDao.delete(unit);

            return null;
        }
    }

    public void removeUnit(final Unit unit) {
        new RemoveAsyncTask(mUnitDao).execute(unit);
    }

    //TODO: add tests. end
}
