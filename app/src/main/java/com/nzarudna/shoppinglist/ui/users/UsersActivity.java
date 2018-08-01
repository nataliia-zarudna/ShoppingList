package com.nzarudna.shoppinglist.ui.users;

import android.support.v4.app.Fragment;

import com.nzarudna.shoppinglist.ui.NavigationSingleFragmentActivity;

/**
 * Created by Nataliia on 11.03.2018.
 */

public class UsersActivity extends NavigationSingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        return UsersFragment.newInstance();
    }
}
