package com.nzarudna.shoppinglist.model.unit;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PagedList;
import android.os.AsyncTask;

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
    public void createUnit(final Unit unit) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                mUnitDao.insert(unit);

                return null;
            }
        }.execute();
    }

    public void updateUnit(final Unit unit) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                mUnitDao.update(unit);

                return null;
            }
        }.execute();
    }

    public void removeUnit(final Unit unit) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                mUnitDao.delete(unit);

                return null;
            }
        }.execute();
    }

    //TODO: add tests. end
}
