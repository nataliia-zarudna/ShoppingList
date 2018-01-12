package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.runner.AndroidJUnit4;

import com.nzarudna.shoppinglist.model.ProductsList;
import com.nzarudna.shoppinglist.model.ShoppingList;
import com.nzarudna.shoppinglist.model.dao.DaoFactory;
import com.nzarudna.shoppinglist.model.dao.ProductsListDao;
import com.nzarudna.shoppinglist.model.dao.UserDao;
import com.nzarudna.shoppinglist.model.db.AppDatabase;
import com.nzarudna.shoppinglist.notification.NotificationManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test Shopping List
 */
@RunWith(AndroidJUnit4.class)
public class ShoppingListTest {

    private static final String MOCKED_DEFAULT_LIST_NAME = "Default List Name";

    private Context mMockContext;
    private SharedPreferences mSharedPreferences;
    private NotificationManager mNotificationManager;

    private int mSelfUser;

    @Before
    public void setUp() {

        mMockContext = Mockito.mock(Context.class);

        Resources resources = Mockito.mock(Resources.class);
        when(mMockContext.getResources()).thenReturn(resources);

        mSharedPreferences = Mockito.mock(SharedPreferences.class);
        when(mMockContext.getSharedPreferences(mMockContext.getString(R.string.preference_key_file), Context.MODE_PRIVATE)).thenReturn(mSharedPreferences);

        AppDatabase.switchToInMemory(mMockContext);
        createTestData();

        when(mMockContext.getString(R.string.default_list_name)).thenReturn(MOCKED_DEFAULT_LIST_NAME);

        createSelfUser();
        when(mSharedPreferences.getInt(SharedPreferencesConstants.SELF_USER_ID, 0)).thenReturn(mSelfUser);

        mNotificationManager = Mockito.mock(NotificationManager.class);
    }

    private void createSelfUser() {
        UserDao userDao = DaoFactory.getInstance().getUserDao(mMockContext);
        mSelfUser = TestUtils.insertUser(userDao);
    }

    @Test
    public void createList() throws InterruptedException {

        ShoppingList list = ShoppingList.createList(mMockContext);
        LiveData<ProductsList> listLiveData = list.getListData();

        ProductsList productsList = TestUtils.findByIDSync(listLiveData);

        assertThat(productsList.getName(), is(MOCKED_DEFAULT_LIST_NAME));
        assertThat(productsList.getCreatedBy(), is(mSelfUser));

        //verify(mNotificationManager).sendNotification();
    }

    @Test
    public void copyList() throws InterruptedException {

        ShoppingList list = ShoppingList.createList(mMockContext);
        LiveData<ProductsList> listLiveData = list.getListData();

        ProductsList productsList = TestUtils.findByIDSync(listLiveData);

        assertThat(productsList.getName(), is(MOCKED_DEFAULT_LIST_NAME));
        assertThat(productsList.getCreatedBy(), is(mSelfUser));

        //verify(mNotificationManager).sendNotification();
    }

    private void createTestData() {
        ProductsListDao productsListDao = DaoFactory.getInstance().getProductsListDao(mMockContext);
        
    }

    @After
    public void closeDB() {

        AppDatabase.getInstance(mMockContext).close();
    }

}
