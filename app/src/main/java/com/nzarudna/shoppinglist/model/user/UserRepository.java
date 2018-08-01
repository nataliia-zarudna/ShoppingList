package com.nzarudna.shoppinglist.model.user;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.ListenedAsyncTask;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Provides user data
 */
@Singleton
public class UserRepository {

    private static final String SELF_USER_ID = "selfUserID";

    private SharedPreferences mSharedPreferences;
    private UserDao mUserDao;

    @Inject
    public UserRepository(SharedPreferences sharedPreferences,
                          UserDao userDao) {
        mSharedPreferences = sharedPreferences;
        mUserDao = userDao;
    }

    public UUID getSelfUserID() {
        String strUUID = mSharedPreferences.getString(SELF_USER_ID, "");
        return UUID.fromString(strUUID);
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

    public void updateUser(User user, AsyncResultListener asyncResultListener) {
        mUserDao.update(user);
    }

    public LiveData<User> getUser(UUID userID) {
        return mUserDao.findByIDLiveData(userID);
    }

    private static class CreateUserAsyncTask extends ListenedAsyncTask<User, User> {

        private UserDao mUserDao;
        AsyncResultListener mListener;

        public CreateUserAsyncTask(UserDao userDao, AsyncResultListener listener) {
            super(listener);
            mUserDao = userDao;
            mListener = listener;
        }

        @Override
        protected User doInBackground(User... params) {
            User user = params[0];
            mUserDao.insert(user);

            return user;
        }
    }
    public void createUser(User user, AsyncResultListener asyncResultListener) {
        new CreateUserAsyncTask(mUserDao, asyncResultListener).execute(user);
    }

    public void removeUser(User user) {
        mUserDao.delete(user);
    }

    public DataSource.Factory<Integer, User> getOtherUsers() {
        return mUserDao.findByExcludeID(getSelfUserID());
    }
}
