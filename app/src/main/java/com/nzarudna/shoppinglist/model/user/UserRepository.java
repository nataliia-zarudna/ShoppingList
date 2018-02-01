package com.nzarudna.shoppinglist.model.user;

import android.content.SharedPreferences;

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

    @Inject
    public UserRepository(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public UUID getSelfUserID() {
        String strUUID = mSharedPreferences.getString(SELF_USER_ID, "");
        return UUID.fromString(strUUID);
    }

    public void setSelfUserID(UUID selfUserID) {
        mSharedPreferences
                .edit()
                .putString(SELF_USER_ID, selfUserID.toString())
                .commit();
    }

}
