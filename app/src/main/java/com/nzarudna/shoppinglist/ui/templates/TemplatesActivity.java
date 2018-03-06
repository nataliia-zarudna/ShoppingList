package com.nzarudna.shoppinglist.ui.templates;

import android.support.v4.app.Fragment;

import com.nzarudna.shoppinglist.ui.NavigationSingleFragmentActivity;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class TemplatesActivity extends NavigationSingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        return new TemplatesFragment();
    }
}
