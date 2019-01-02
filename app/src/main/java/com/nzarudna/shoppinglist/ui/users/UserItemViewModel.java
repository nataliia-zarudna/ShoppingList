package com.nzarudna.shoppinglist.ui.users;

import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.user.User;
import com.nzarudna.shoppinglist.model.user.UserRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 11.03.2018.
 */

public class UserItemViewModel extends RecyclerItemViewModel<User> {

    @Inject
    UserRepository mUserRepository;

    @Override
    public String getItemName() {
        return mItem.getName();
    }

    @Override
    public void removeItem(AsyncListener listener) {
        mUserRepository.removeAsync(mItem, listener);
    }
}
