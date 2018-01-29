package com.nzarudna.shoppinglist.dao;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PagedList;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.nzarudna.shoppinglist.TestUtils;
import com.nzarudna.shoppinglist.model.user.User;
import com.nzarudna.shoppinglist.model.user.UserDao;
import com.nzarudna.shoppinglist.model.persistence.db.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsNot.not;

/**
 * Test for User Dao methods
 */
@RunWith(AndroidJUnit4.class)
public class UserDaoTest {

    private AppDatabase mAppDatabase;
    private UserDao mSubjectDao;

    @Before
    public void createDB() {

        Context context = InstrumentationRegistry.getContext();
        mAppDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        mSubjectDao = mAppDatabase.userDao();
    }

    @Test
    public void create() {

        User user = new User();
        user.setName("New user");

        long userID = mSubjectDao.insert(user);

        assertThat(userID, not(0l));
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exceptionOnCreateWithNullName() {

        User user = new User();
        user.setName(null);

        mSubjectDao.insert(user);
    }

    @Test
    public void createAndRead() throws InterruptedException {

        User user = new User();
        user.setName("New user");

        long userID = mSubjectDao.insert(user);
        user.setUserID((int) userID);

        LiveData<User> userLiveData = mSubjectDao.findByID((int) userID);
        User insertedUser = TestUtils.getLiveDataValueSync(userLiveData);

        assertThat(user, is(insertedUser));
    }

    @Test
    public void findByExcludeID() throws InterruptedException {

        List<User> users = createUsers(3);

        int excludeUserID = users.get(1).getUserID();
        DataSource.Factory<Integer, User> actualUsersDataSourse = mSubjectDao.findByExcludeID(excludeUserID);
        PagedList<User> actualUsers = TestUtils.getPagedListSync(actualUsersDataSourse);

        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(users.get(0));
        expectedUsers.add(users.get(2));

        TestUtils.assertEquals(expectedUsers, actualUsers);
    }

    @After
    public void closeDB() {
        mAppDatabase.close();
    }

    private User createUser() throws InterruptedException {

        User user = new User();
        user.setName("New user");

        long userID = mSubjectDao.insert(user);

        LiveData<User> userLiveData = mSubjectDao.findByID((int) userID);
        return TestUtils.getLiveDataValueSync(userLiveData);
    }

    private List<User> createUsers(int count) throws InterruptedException {

        List<User> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            users.add(createUser());
        }
        return users;
    }
}
