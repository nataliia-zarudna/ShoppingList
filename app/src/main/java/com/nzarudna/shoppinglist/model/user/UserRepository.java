package com.nzarudna.shoppinglist.model.user;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.model.BaseRepository;
import com.nzarudna.shoppinglist.model.ModelUtils;
import com.nzarudna.shoppinglist.model.exception.EmptyNameException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.utils.AppExecutors;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.nzarudna.shoppinglist.Constants.PREF_SELF_USER_ID;

/**
 * Provides user data
 */
@Singleton
public class UserRepository extends BaseRepository<User> {

    private SharedPreferences mSharedPreferences;
    private UserDao mUserDao;

    @Inject
    public UserRepository(SharedPreferences sharedPreferences,
                          UserDao userDao, AppExecutors appExecutors) {
        super(appExecutors);
        mSharedPreferences = sharedPreferences;
        mUserDao = userDao;
    }

    public UUID getSelfUserID() {
        String strUUID = mSharedPreferences.getString(PREF_SELF_USER_ID, "");
        return UUID.fromString(strUUID);
    }

    @Override
    protected User create(User user) throws Exception {
        validateUserName(user.getName());

        mUserDao.insert(user);
        return user;
    }

    @Override
    protected User update(User user) throws Exception {
        validateUserName(user.getName());

        mUserDao.update(user);
        return user;
    }

    @Override
    protected void remove(User user) {
        mUserDao.delete(user);
    }

    public void setSelfUserID(UUID selfUserID) {
        mSharedPreferences
                .edit()
                .putString(PREF_SELF_USER_ID, selfUserID.toString())
                .apply();
    }

    public User getSelfUser() {
        return mUserDao.findByID(getSelfUserID());
    }

    public LiveData<User> getSelfUserLiveData() {
        return mUserDao.findByIDLiveData(getSelfUserID());
    }

    public LiveData<User> getUserLiveData(UUID userID) {
        return mUserDao.findByIDLiveData(userID);
    }

    public User getUser(UUID userID) {
        return mUserDao.findByID(userID);
    }

    public DataSource.Factory<Integer, User> getOtherUsers() {
        return mUserDao.findByExcludeID(getSelfUserID());
    }

    private void validateUserName(String name) throws EmptyNameException, UniqueNameConstraintException {
        ModelUtils.validateNameIsNotEmpty(name);

        if (mUserDao.isUsersWithSameNameExists(name)) {
            throw new UniqueNameConstraintException("User with name '" + name + "' already exists");
        }
    }
}
