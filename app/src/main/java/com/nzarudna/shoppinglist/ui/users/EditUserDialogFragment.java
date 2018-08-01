package com.nzarudna.shoppinglist.ui.users;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.user.User;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseEditItemDialogFragment;

import java.util.UUID;

public class EditUserDialogFragment extends BaseEditItemDialogFragment<User, EditUserViewModel> {

    private static final String EXTRA_NEW_USER_ID = "com.nzarudna.shoppinglist.extra.NEW_USER_ID";

    public static UUID getNewUserID(Intent data) {
        return (UUID) data.getSerializableExtra(EXTRA_NEW_USER_ID);
    }

    @Override
    protected int getDialogFragmentResID() {
        return R.layout.fragment_edit_user_dialog;
    }

    @Override
    protected boolean hasSaveAndNextButton() {
        return false;
    }

    @NonNull
    @Override
    protected Intent getResponseIntent() {
        if (mViewModel.isNewItem()) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_NEW_USER_ID, mViewModel.getItem().getUserID());
            return intent;
        } else {
            return super.getResponseIntent();
        }
    }
}
