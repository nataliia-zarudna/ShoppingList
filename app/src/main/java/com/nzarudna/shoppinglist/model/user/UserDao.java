package com.nzarudna.shoppinglist.model.user;

import java.util.UUID;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
