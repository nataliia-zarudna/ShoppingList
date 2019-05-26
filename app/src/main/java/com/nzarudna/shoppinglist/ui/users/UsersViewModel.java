package com.nzarudna.shoppinglist.ui.users;

import android.net.Uri;

import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.nzarudna.shoppinglist.model.user.User;
import com.nzarudna.shoppinglist.model.user.UserRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;
import com.nzarudna.shoppinglist.utils.AppExecutors;
import com.nzarudna.shoppinglist.utils.UserUtils;

import java.util.UUID;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

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
                        new UserUtils.DynamicLinkListener() {

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

    public void createUserFromInvitationLink(@NonNull PendingDynamicLinkData pendingDynamicLinkData) {
        Uri deepLink = pendingDynamicLinkData.getLink();
        String invitorFirebaseToken = UserUtils.getInvitorTokenFromInvitationLink(deepLink);
        String invitorName = UserUtils.getInvitorNameFromInvitationLink(deepLink);

        User user = new User();
        user.setName(invitorName);
        user.setToken(invitorFirebaseToken);
        mUserRepository.createAsync(user);
    }
}
