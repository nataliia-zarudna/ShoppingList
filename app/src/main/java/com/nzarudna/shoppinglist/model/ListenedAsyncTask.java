package com.nzarudna.shoppinglist.model;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

/**
 * Created by Nataliia on 16.03.2018.
 */

public abstract class ListenedAsyncTask<Params, Result> extends AsyncTask<Params, Void, Result> {

    protected AsyncResultListener<Result> mListener;
    protected Exception mResultException;

    public ListenedAsyncTask(@Nullable AsyncResultListener<Result> listener) {
        mListener = listener;
    }

    @Override
    protected void onPostExecute(Result object) {
        if (mListener != null) {
            if (mResultException == null) {
                mListener.onAsyncSuccess(object);
            } else {
                mListener.onAsyncError(mResultException);
            }
        }
    }
}
