package com.nzarudna.shoppinglist.ui.users;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.net.Uri;

import com.nzarudna.shoppinglist.model.user.User;
import com.nzarudna.shoppinglist.model.user.UserRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;
import com.nzarudna.shoppinglist.utils.AppExecutors;
import com.nzarudna.shoppinglist.utils.UserUtils;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Nataliia on 11.03.2018.
 */

public class UsersViewModel extends RecyclerViewModel<User> {

    @Inject
    UserRepository mUserRepository;

    @Inject
    AppExecutors mAppExecutors;

    @Override
    public LiveData<PagedList<User>> getItems(int pageSize) {
        DataSource.Factory<Integer, User> usersFactory = mUserRepository.getOtherUsers();
        return new LivePagedListBuilder(usersFactory, pageSize).build();
    }

    public LiveData<String> getUserInvitationLink(UUID userID) {
        MutableLiveData<String> invitationLinkLiveData = new MutableLiveData<>();

        mAppExecutors.getDiscIO().execute(() -> {

            User user = mUserRepository.getUser(userID);
            if (user.getInvitationLink() != null) {
                invitationLinkLiveData.postValue(user.getInvitationLink());
            } else {

                User selfUser = mUserRepository.getSelfUser();
                UserUtils.buildInvitationLink(
                        mAppExecutors.getDiscIO(),
                        selfUser.getToken(),
                        user.getInvitorName(),
                        new UserUtils.BuildDynamicLinkListener() {

                            @Override
                            public void onBuildDynamicLinkSuccess(Uri shortLink) {
                                invitationLinkLiveData.postValue(shortLink.toString());
                            }

                            @Override
                            public void onBuildDynamicLinkError() {
                                invitationLinkLiveData.postValue(null);
                            }
                        });
            }
        });

        return invitationLinkLiveData;
    }
}
