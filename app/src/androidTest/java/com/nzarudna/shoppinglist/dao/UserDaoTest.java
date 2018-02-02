package com.nzarudna.shoppinglist.dao;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PagedList;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import com.nzarudna.shoppinglist.TestUtils;
import com.nzarudna.shoppinglist.model.persistence.db.AppDatabase;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.user.User;
import com.nzarudna.shoppinglist.model.user.UserDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Test for User Dao methods
 */
@RunWith(AndroidJUnit4.class)
public class UserDaoTest {

    private AppDatabase mAppDatabase;
    private UserDao mSubjectDao;

    @Before
    public void createDB() {

        mAppDatabase = TestUtils.buildInMemoryDB();
        mSubjectDao = mAppDatabase.userDao();
    }

    @Test
    public void create() {

        User user = new User("New user");
        mSubjectDao.insert(user);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exceptionOnCreateWithNullName() {

        User user = new User(null);

        mSubjectDao.insert(user);
    }

    @Test
    public void createAndRead() throws InterruptedException {

        User user = new User("New user");

        mSubjectDao.insert(user);

        LiveData<User> userLiveData = mSubjectDao.findByID(user.getUserID());
        User insertedUser = TestUtils.getLiveDataValueSync(userLiveData);

        assertThat(user, is(insertedUser));
    }

    @Test
    public void remove_testOnDeleteCascade() throws InterruptedException {

        User user = createUser();
        ProductList list = new ProductList("Some list", user.getUserID());
        list.setAssignedID(user.getUserID());
        list.setModifiedBy(user.getUserID());
        mAppDatabase.productListDao().insert(list);

        mSubjectDao.delete(user);

        ProductList foundList = mAppDatabase.productListDao().findByIDSync(list.getListID());
        assertNotNull(foundList);
        assertNull(foundList.getCreatedBy());
        assertNull(foundList.getAssignedID());
        assertNull(foundList.getModifiedBy());
    }

    @Test
    public void findByExcludeID() throws InterruptedException {

        List<User> users = createUsers(3);

        UUID excludeUserID = users.get(1).getUserID();
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

        User user = new User("New user");

        mSubjectDao.insert(user);

        LiveData<User> userLiveData = mSubjectDao.findByID(user.getUserID());
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
