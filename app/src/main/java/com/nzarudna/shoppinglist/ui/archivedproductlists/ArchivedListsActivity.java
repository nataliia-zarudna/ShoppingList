package com.nzarudna.shoppinglist.ui.archivedproductlists;

import android.support.v4.app.Fragment;

import com.nzarudna.shoppinglist.ui.NavigationSingleFragmentActivity;

/**
 * Created by nsirobaba on 3/13/18.
 */

public class ArchivedListsActivity extends NavigationSingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        return ArchivedListsFragment.newInstance();
    }
}
