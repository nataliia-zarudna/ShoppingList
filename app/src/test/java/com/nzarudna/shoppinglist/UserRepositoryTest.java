package com.nzarudna.shoppinglist;

import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.model.BaseRepository;
import com.nzarudna.shoppinglist.model.exception.EmptyNameException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
import com.nzarudna.shoppinglist.model.user.User;
import com.nzarudna.shoppinglist.model.user.UserDao;
import com.nzarudna.shoppinglist.model.user.UserRepository;
import com.nzarudna.shoppinglist.utils.AppExecutors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static com.nzarudna.shoppinglist.Constants.PREF_SELF_USER_ID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by nsirobaba on 2/1/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest extends BaseRepositoryTest<User> {

    private UserRepository mSubject;
    private AppExecutors mAppExecutors;

    @Mock
    private UserDao mUserDao;

    @Mock
    private SharedPreferences mSharedPreferences;

    @Before
    public void setUp() {

        mAppExecutors = new TestAppExecutors();
        mSubject = new UserRepository(mSharedPreferences, mUserDao, mAppExecutors);
    }

    @Override
    protected BaseRepository<User> getRepositorySubject() {
        return mSubject;
    }

    @Test
    public void create() throws InterruptedException {
        User user = new User();
        user.setName("some name");

        verifyCreate(user);

        verify(mUserDao).insert(user);
    }

    @Test
    public void create_trimName() throws CloneNotSupportedException, InterruptedException {
        String name = " some name   ";

        User user = new User();
        user.setName(name);

        User resultUnit = user.clone();
        resultUnit.setName(name.trim());

        verifyCreate(user, resultUnit);

        verify(mUserDao).insert(resultUnit);
    }

    @Test
    public void create_callListenerCallback_nullNameError() throws InterruptedException {
        final User newUser = new User();

        verifyCreateWithException(newUser, EmptyNameException.class);
    }

    @Test
    public void create_callListenerCallback_emptyNameError() throws InterruptedException {
        final User newUser = new User();
        newUser.setName("  ");

        verifyCreateWithException(newUser, EmptyNameException.class);
    }

    @Test
    public void create_callListenerCallback_duplicateNameError() throws InterruptedException {

        String userName = "some name";
        when(mUserDao.isUsersWithSameNameExists(userName)).thenReturn(true);

        final User newUser = new User();
        newUser.setName(userName);

        verifyCreateWithException(newUser, UniqueNameConstraintException.class);
    }

    @Test
    public void update() throws InterruptedException {
        User user = new User();
        user.setName("Some name");

        verifyUpdate(user);

        verify(mUserDao).update(user);
    }

    @Test
    public void update_trimName() throws CloneNotSupportedException, InterruptedException {
        String name = " some name   ";

        User user = new User();
        user.setName(name);

        User resultUnit = user.clone();
        resultUnit.setName(name.trim());

        verifyUpdate(user, resultUnit);

        verify(mUserDao).update(resultUnit);
    }

    @Test
    public void update_callListenerCallback_nullNameError() throws InterruptedException {
        final User newUser = new User();
        verifyUpdateWithException(newUser, EmptyNameException.class);
    }

    @Test
    public void update_callListenerCallback_emptyNameError() throws InterruptedException {
        final User newUser = new User();
        newUser.setName("   ");
        verifyUpdateWithException(newUser, EmptyNameException.class);
    }

    @Test
    public void update_callListenerCallback_duplicateNameError() throws InterruptedException {

        String userName = "some name";
        when(mUserDao.isUsersWithSameNameExists(userName)).thenReturn(true);

        final User newUser = new User();
        newUser.setName(userName);

        verifyUpdateWithException(newUser, UniqueNameConstraintException.class);
    }

    @Test
    public void remove() throws InterruptedException {
        User user = new User();

        verifyRemove(user);

        verify(mUserDao).delete(user);
    }

    @Test
    public void findAll_exceptSelf() {

        UUID selfUserID = UUID.randomUUID();
        when(mSharedPreferences.getString(PREF_SELF_USER_ID, "")).thenReturn(selfUserID.toString());

        mSubject.getOtherUsers();

        verify(mUserDao).findByExcludeID(selfUserID);
    }

    @Test
    public void findUser() {

        UUID userID = UUID.randomUUID();
        when(mSharedPreferences.getString(PREF_SELF_USER_ID, "")).thenReturn(userID.toString());

        mSubject.getUser(userID);

        verify(mUserDao).findByID(userID);
    }

    @Test
    public void getSelfUserID() {

        UUID selfUserID = UUID.randomUUID();
        when(mSharedPreferences.getString(PREF_SELF_USER_ID, "")).thenReturn(selfUserID.toString());

        UUID foundSelfUserID = mSubject.getSelfUserID();

        assertEquals(selfUserID, foundSelfUserID);
    }
}