package com.nzarudna.shoppinglist.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nzarudna.shoppinglist.user.User;

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

    @Query("SELECT * FROM users WHERE user_id = :userID")
    LiveData<User> findByID(int userID);

    @Query("SELECT * FROM users WHERE user_id <> :userID")
    DataSource.Factory<Integer, User> findByExcludeID(int userID);
}
