package com.nzarudna.shoppinglist;

import android.content.Context;
import android.support.annotation.StringRes;

import javax.inject.Inject;
import javax.inject.Singleton;

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
}
