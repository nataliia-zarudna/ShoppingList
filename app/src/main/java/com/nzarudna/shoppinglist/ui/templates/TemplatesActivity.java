package com.nzarudna.shoppinglist.ui.templates;

import com.nzarudna.shoppinglist.ui.NavigationSingleFragmentActivity;

import androidx.fragment.app.Fragment;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class TemplatesActivity extends NavigationSingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        return new TemplatesFragment();
    }
}
