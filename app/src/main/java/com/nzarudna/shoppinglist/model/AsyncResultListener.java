package com.nzarudna.shoppinglist.model;

/**
 * Created by nsirobaba on 3/14/18.
 */

public interface AsyncResultListener {

    void onSuccess();

    void onError(Exception e);

}
