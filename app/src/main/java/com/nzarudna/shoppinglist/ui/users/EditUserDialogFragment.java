package com.nzarudna.shoppinglist.ui.users;

import android.view.View;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseEditItemDialogFragment;

public class EditUserDialogFragment extends BaseEditItemDialogFragment {

    @Override
    protected int getDialogFragmentResID() {
        return R.layout.fragment_edit_user_dialog;
    }

    @Override
    protected boolean hasSaveAndNextButton() {
        return false;
    }

    @Override
    protected void onSaveButtonClick(View view) {
        super.onSaveButtonClick(view);
    }
}
