package com.nzarudna.shoppinglist.user;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Provides user data
 */
@Singleton
public class UserRepository {

    private static final String SELF_USER_ID = "selfUserID";

    private SharedPreferences mSharedPreferences;

    @Inject
    public UserRepository(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public int getSelfUserID() {
        return mSharedPreferences.getInt(SELF_USER_ID, 0);
    }

    public void setSelfUserID(int selfUserID) {
        mSharedPreferences
                .edit()
                .putInt(SELF_USER_ID, selfUserID)
                .commit();
    }

}
