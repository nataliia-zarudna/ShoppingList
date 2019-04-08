package com.nzarudna.shoppinglist.ui.users;

import com.nzarudna.shoppinglist.ui.NavigationSingleFragmentActivity;

import androidx.fragment.app.Fragment;

/**
 * Created by Nataliia on 11.03.2018.
 */

public class UsersActivity extends NavigationSingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        return UsersFragment.newInstance();
    }
}
