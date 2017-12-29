package com.nzarudna.shoppinglist.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import com.nzarudna.shoppinglist.model.User;

/**
 * Created by Nataliia on 29.12.2017.
 */

@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

}
