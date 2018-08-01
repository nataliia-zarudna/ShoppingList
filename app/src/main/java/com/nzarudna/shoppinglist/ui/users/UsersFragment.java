package com.nzarudna.shoppinglist.ui.users;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.user.User;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseEditItemDialogFragment;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerViewFragment;
import com.nzarudna.shoppinglist.ui.recyclerui.EditDialogViewModel;

import java.util.UUID;

/**
 * Created by Nataliia on 11.03.2018.
 */

public class UsersFragment
        extends BaseRecyclerViewFragment<User, UsersViewModel, UserItemViewModel>
        implements EditUserViewModel.EditUserViewModelListener {

    public static UsersFragment newInstance() {
        return new UsersFragment();
    }

    @Override
    protected UsersViewModel getFragmentViewModel() {
        UsersViewModel viewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
        ShoppingListApplication.getAppComponent().inject(viewModel);
        return viewModel;
    }

    @Override
    protected UserItemViewModel getListItemViewModel() {
        UserItemViewModel itemViewModel = new UserItemViewModel();
        ShoppingListApplication.getAppComponent().inject(itemViewModel);
        return itemViewModel;
    }

    @Override
    protected EditDialogViewModel<User> getEditDialogViewModel() {
        EditUserViewModel viewModel = new EditUserViewModel();
        ShoppingListApplication.getAppComponent().inject(viewModel);
        return viewModel;
    }

    @Override
    protected BaseEditItemDialogFragment<User, ? extends EditDialogViewModel<User>> getEditItemDialogFragment() {
        return new EditUserDialogFragment();
    }

    @Override
    protected DiffUtil.ItemCallback<User> getDiffCallback() {
        return new DiffUtil.ItemCallback<User>() {
            @Override
            public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                return oldItem.getUserID().equals(newItem.getUserID());
            }

            @Override
            public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                return oldItem.equals(newItem);
            }
        };
    }

    public void inviteFriend(UUID userID) {

        mViewModel.getUser(userID).observe(this, user -> {
            if (user != null) {
                startSendLinkApp("", user.getInvitationLink());
            }
        });
    }

    private void startSendLinkApp(String subject, String invitationLink) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invite_user_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.id))
    }
}
