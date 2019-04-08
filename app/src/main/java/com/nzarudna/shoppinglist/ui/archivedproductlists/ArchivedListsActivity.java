package com.nzarudna.shoppinglist.ui.archivedproductlists;

import com.nzarudna.shoppinglist.ui.NavigationSingleFragmentActivity;

import androidx.fragment.app.Fragment;

/**
 * Created by nsirobaba on 3/13/18.
 */

public class ArchivedListsActivity extends NavigationSingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        return ArchivedListsFragment.newInstance();
    }
}
