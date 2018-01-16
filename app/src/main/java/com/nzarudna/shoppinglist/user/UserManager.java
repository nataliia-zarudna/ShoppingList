package com.nzarudna.shoppinglist.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.SharedPreferencesConstants;

/**
 * Created by nsirobaba on 1/12/18.
 */

public class UserManager {

    public static int getSelfUserID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        return preferences.getInt(SharedPreferencesConstants.SELF_USER_ID, 0);
    }

}
