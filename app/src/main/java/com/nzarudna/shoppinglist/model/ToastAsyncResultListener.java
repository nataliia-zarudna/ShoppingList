package com.nzarudna.shoppinglist.model;

import android.content.Context;
import android.widget.Toast;

import com.nzarudna.shoppinglist.R;

import androidx.annotation.StringRes;

public class ToastAsyncResultListener implements AsyncListener {

    private static final int DEFAULT_SUCCESS_MESSAGE = R.string.async_success_result;
    private static final int DEFAULT_ERROR_MESSAGE = R.string.async_error_result;

    @StringRes
    private int mSuccessMessage;

    @StringRes
    private int mErrorMessage;

    private Context mContext;

    public ToastAsyncResultListener(Context context) {
        this(context, DEFAULT_SUCCESS_MESSAGE, DEFAULT_ERROR_MESSAGE);
    }

    public ToastAsyncResultListener(Context context, @StringRes int successMessage, @StringRes int errorMessage) {
        mContext = context;
        mSuccessMessage = successMessage;
        mErrorMessage = errorMessage;
    }

    @Override
    public void onAsyncSuccess() {
        showMessage(mSuccessMessage);
    }

    @Override
    public void onAsyncError(Exception e) {
        showMessage(mErrorMessage);
    }

    private void showMessage(int message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
