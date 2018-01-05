package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.nzarudna.shoppinglist.model.User;
import com.nzarudna.shoppinglist.model.dao.UserDao;
import com.nzarudna.shoppinglist.model.db.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        User insertedUser = TestUtils.findByIDSync(userLiveData);

        assertThat(user, is(insertedUser));
    }

    @Test
    public void update() throws InterruptedException {

        User user = createUser();
        user.setName("Updated name");
        user.setPhoneNumber("12345678");
        user.setToken("987654qwe");

        mSubjectDao.update(user);
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
        return TestUtils.findByIDSync(userLiveData);
    }
}
