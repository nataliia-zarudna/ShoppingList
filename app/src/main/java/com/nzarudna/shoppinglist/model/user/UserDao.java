package com.nzarudna.shoppinglist.model.user;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.UUID;

/**
 * Created by Nataliia on 29.12.2017.
 */

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE user_id = :userID")
    LiveData<User> findByIDLiveData(UUID userID);

    @Query("SELECT * FROM users WHERE user_id = :userID")
    User findByID(UUID userID);

    @Query("SELECT * FROM users WHERE user_id <> :userID")
    DataSource.Factory<Integer, User> findByExcludeID(UUID userID);

    @Query("SELECT 1 FROM users WHERE lower(name) = lower(:name) LIMIT 1")
    boolean isUsersWithSameNameExists(String name);
}
