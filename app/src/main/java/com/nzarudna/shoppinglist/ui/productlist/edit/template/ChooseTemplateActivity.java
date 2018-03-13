package com.nzarudna.shoppinglist.ui.productlist.edit.template;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import com.nzarudna.shoppinglist.ui.SingleFragmentActivity;

import java.util.UUID;

/**
 * Created by Nataliia on 04.03.2018.
 */

public class ChooseTemplateActivity extends SingleFragmentActivity {
    private static final String EXTRA_LIST_ID = "com.nzarudna.shoppinglist.ui.productlist.edit.template.list_id";

    public static Intent newIntent(Context packageContext, UUID listID) {
        Intent intent = new Intent(packageContext, ChooseTemplateActivity.class);
        intent.putExtra(EXTRA_LIST_ID, listID);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected ChooseTemplateFragment getFragment() {
        UUID listID = (UUID) getIntent().getSerializableExtra(EXTRA_LIST_ID);
        return ChooseTemplateFragment.getInstance(listID);
    }
}
