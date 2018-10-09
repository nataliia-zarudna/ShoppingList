package com.nzarudna.shoppinglist.model;

/**
 * Created by nsirobaba on 3/14/18.
 */

public interface AsyncListener {

    void onAsyncSuccess();

    void onAsyncError(Exception e);

}
