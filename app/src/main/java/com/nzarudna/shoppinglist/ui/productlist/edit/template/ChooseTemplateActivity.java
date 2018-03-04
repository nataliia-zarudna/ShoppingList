package com.nzarudna.shoppinglist.ui.productlist.edit.template;

import android.support.v4.app.Fragment;

import com.nzarudna.shoppinglist.ui.SingleFragmentActivity;

/**
 * Created by Nataliia on 04.03.2018.
 */

public class ChooseTemplateActivity extends SingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        return new ChooseTemplateFragment();
    }
}
