package com.nzarudna.shoppinglist.ui.users;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitRepository;
import com.nzarudna.shoppinglist.model.user.User;
import com.nzarudna.shoppinglist.model.user.UserRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Nataliia on 11.03.2018.
 */

public class UsersViewModel extends RecyclerViewModel<User> {

    @Inject
    UserRepository mUserRepository;

    @Override
    public LiveData<PagedList<User>> getItems(int pageSize) {
        DataSource.Factory<Integer, User> usersFactory = mUserRepository.getOtherUsers();
        return new LivePagedListBuilder(usersFactory, pageSize).build();
    }

    public LiveData<User> getUser(UUID userID) {
        return mUserRepository.getUser(userID);
    }
}
