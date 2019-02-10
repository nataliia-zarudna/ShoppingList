package com.nzarudna.shoppinglist.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    public static final String SHOPPING_LIST_PREFS = "shopping_list_prefs";

    public static final String PREF_LAST_USED_SHOPPING_LIST_NUM = "last_used_shopping_list_num";
    public static final String PREF_IS_EDIT_PRODUCT_DETAILS_SHOWN = "is_edit_product_details_shown";

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

    public void setIsEditProductDetailsShown(boolean value) {
        mPreferences.edit().putBoolean(PREF_IS_EDIT_PRODUCT_DETAILS_SHOWN, value).apply();
    }

    public boolean isEditProductDetailsShown() {
        return mPreferences.getBoolean(PREF_IS_EDIT_PRODUCT_DETAILS_SHOWN, true);
    }

}
