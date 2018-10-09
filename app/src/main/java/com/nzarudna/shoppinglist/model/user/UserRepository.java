package com.nzarudna.shoppinglist.model.user;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.BaseRepository;
import com.nzarudna.shoppinglist.model.ListenedAsyncTask;
import com.nzarudna.shoppinglist.utils.AppExecutors;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Provides user data
 */
@Singleton
public class UserRepository extends BaseRepository<User> {

    private static final String SELF_USER_ID = "selfUserID";

    @Inject
    SharedPreferences mSharedPreferences;
    @Inject
    UserDao mUserDao;

//    @Inject
//    public UserRepository(SharedPreferences sharedPreferences,
//                          UserDao userDao, AppExecutors appExecutors) {
//        super(appExecutors);
//        mSharedPreferences = sharedPreferences;
//        mUserDao = userDao;
//    }

    public UUID getSelfUserID() {
        String strUUID = mSharedPreferences.getString(SELF_USER_ID, "");
        return UUID.fromString(strUUID);
    }

    @Override
    protected User create(User user) throws Exception {
        mUserDao.insert(user);
        return user;
    }

    @Override
    protected User update(User user) throws Exception {
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
                .putString(SELF_USER_ID, selfUserID.toString())
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
}
