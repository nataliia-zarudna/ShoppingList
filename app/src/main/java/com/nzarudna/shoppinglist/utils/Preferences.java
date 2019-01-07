package com.nzarudna.shoppinglist.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    public static final String PREF_LAST_USED_SHOPPING_LIST_NUM = "last_used_shopping_list_num";
    public static final String SHOPPING_LIST_PREFS = "shopping_list_prefs";
    public static final int SHOPPING_LIST_NUM_INIT_VALUE = 0;

    private SharedPreferences mPreferences;

    public Preferences(Context context) {
        mPreferences = context.getSharedPreferences(SHOPPING_LIST_PREFS, Context.MODE_PRIVATE);
    }

    public void setLastUsedShoppingListNum(int value) {
        mPreferences.edit().putInt(PREF_LAST_USED_SHOPPING_LIST_NUM, value).apply();
    }

    public int getLastUsedShoppingListNum() {
        return mPreferences.getInt(PREF_LAST_USED_SHOPPING_LIST_NUM, SHOPPING_LIST_NUM_INIT_VALUE);
    }

}
