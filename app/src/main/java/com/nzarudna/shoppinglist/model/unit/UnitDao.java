package com.nzarudna.shoppinglist.model.unit;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by nsirobaba on 1/31/18.
 */
@Dao
public interface UnitDao {

    @Query(value = "SELECT * FROM units")
    List<Unit> findAll();

}
