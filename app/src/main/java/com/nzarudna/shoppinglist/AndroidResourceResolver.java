package com.nzarudna.shoppinglist;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.StringRes;

/**
 * Resolves android resources, e.g. strings
 */
@Singleton
public class AndroidResourceResolver implements ResourceResolver {

    private Context mContext;

    @Inject
    public AndroidResourceResolver(Context context) {
        mContext = context;
    }

    @Override
    public String getString(@StringRes int resID) {
        return mContext.getString(resID);
    }

    @Override
    public String getString(@StringRes int resID, Object... formatArgs) {
        return mContext.getString(resID, formatArgs);
    }
}
