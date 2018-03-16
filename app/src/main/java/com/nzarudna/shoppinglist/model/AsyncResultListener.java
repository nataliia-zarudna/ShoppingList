package com.nzarudna.shoppinglist.model;

/**
 * Created by nsirobaba on 3/14/18.
 */

public interface AsyncResultListener<Result> {

    void onAsyncSuccess(Result result);

    void onAsyncError(Exception e);

}
