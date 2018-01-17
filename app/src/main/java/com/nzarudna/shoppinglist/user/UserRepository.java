package com.nzarudna.shoppinglist.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.SharedPreferencesConstants;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Provides user data
 */
@Singleton
public class UserRepository {

    private SharedPreferences mSharedPreferences;

    @Inject
    public UserRepository(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public int getSelfUserID() {
        return mSharedPreferences.getInt(SharedPreferencesConstants.SELF_USER_ID, 0);
    }

}
