package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.utils.AppExecutors;

public class TestAppExecutors extends AppExecutors {

    public TestAppExecutors() {
        super();
        mMainThread = getDiscIO();
    }
}
