package com.nzarudna.shoppinglist.model.unit;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

/**
 * Created by nsirobaba on 1/31/18.
 */
@Dao
public interface UnitDao {

    @Insert
    void insert(Unit unit);

    @Update
    void update(Unit unit);

    @Query("SELECT 1 FROM units WHERE lower(name) = lower(:name) LIMIT 1")
    boolean isUnitsWithSameNameExists(String name);

    @Delete
    void delete(Unit unit);

    @Query(value = "SELECT * FROM units")
    LiveData<List<Unit>> findAllLiveData();

    @Query(value = "SELECT * FROM units")
    DataSource.Factory<Integer, Unit> findAll();
}
