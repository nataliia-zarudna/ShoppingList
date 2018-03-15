package com.nzarudna.shoppinglist.model;

/**
 * Created by nsirobaba on 3/14/18.
 */

public interface AsyncResultListener {

    void onAsyncSuccess();

    void onAsyncError(Exception e);

}
