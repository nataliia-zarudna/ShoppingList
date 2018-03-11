package com.nzarudna.shoppinglist.ui.categories;

import android.support.v4.app.Fragment;

import com.nzarudna.shoppinglist.ui.NavigationSingleFragmentActivity;
import com.nzarudna.shoppinglist.ui.SingleFragmentActivity;

/**
 * Created by Nataliia on 09.03.2018.
 */

public class CategoriesActivity extends NavigationSingleFragmentActivity {


    @Override
    protected Fragment getFragment() {
        return CategoriesFragment.newInstance();
    }
}
