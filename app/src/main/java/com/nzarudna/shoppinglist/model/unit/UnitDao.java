package com.nzarudna.shoppinglist.model.unit;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by nsirobaba on 1/31/18.
 */
@Dao
public interface UnitDao {

    //TODO: add tests. start

    @Insert
    void insert(Unit unit);

    @Update
    void update(Unit unit);

    @Query("SELECT 1 FROM units WHERE lower(name) = lower(:name) LIMIT 1")
    boolean isUnitsWithSameNameExists(String name);

    @Delete
    void delete(Unit unit);

    //TODO: add tests. end

    @Query(value = "SELECT * FROM units")
    LiveData<List<Unit>> findAll();

    @Query(value = "SELECT * FROM units")
    DataSource.Factory<Integer, Unit> findAllDataSource();

}
