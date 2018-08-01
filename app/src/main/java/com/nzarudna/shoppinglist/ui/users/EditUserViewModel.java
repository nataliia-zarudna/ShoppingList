package com.nzarudna.shoppinglist.ui.users;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.user.User;
import com.nzarudna.shoppinglist.model.user.UserRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.EditDialogViewModel;
import com.nzarudna.shoppinglist.utils.AppExecutors;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Nataliia on 11.03.2018.
 */
@Singleton
public class EditUserViewModel extends EditDialogViewModel<User> {

    @Inject
    UserRepository mUserRepository;

    @Inject
    AppExecutors mAppExecutors;

    private User mCurrentUser;

    public EditUserViewModel() {
        mAppExecutors.getDiscIO().execute(() -> {
            mCurrentUser = mUserRepository.getSelfUser();
        });
    }

    @Override
    protected User createItemObject() {
        return new User();
    }

    @Override
    public String getName() {
        return mItem.getName();
    }

    @Override
    public void setName(String name) {
        mItem.setName(name);
    }

    public boolean isUserCompleted() {
        return mCurrentUser == null || mCurrentUser.getToken() != null;
    }

    public String getCurrentUserName() {
        return mCurrentUser != null ? mCurrentUser.getName() : "";
    }

    public void setCurrentUserName(String name) {
        if (mCurrentUser != null) {
            this.mCurrentUser.setName(name);
        }
    }

    @Override
    protected int getSaveButtonTitle() {
        return !isUserCompleted() ? R.string.invite_btn : super.getSaveButtonTitle();
    }

    @Override
    protected String getUniqueNameValidationMessage() {
        return mResourceResolver.getString(R.string.user_unique_name_validation_message);
    }

    @Override
    protected void updateItem(AsyncResultListener asyncResultListener) {
        mUserRepository.updateUser(mItem, asyncResultListener);
    }

    @Override
    protected void createItem(AsyncResultListener asyncResultListener) {
        mUserRepository.createUser(mItem, asyncResultListener);
    }
}
