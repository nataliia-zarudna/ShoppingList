package com.nzarudna.shoppinglist.ui.categories;

import com.nzarudna.shoppinglist.ui.NavigationSingleFragmentActivity;

import androidx.fragment.app.Fragment;

/**
 * Created by Nataliia on 09.03.2018.
 */

public class CategoriesActivity extends NavigationSingleFragmentActivity {


    @Override
    protected Fragment getFragment() {
        return CategoriesFragment.newInstance();
    }
}
