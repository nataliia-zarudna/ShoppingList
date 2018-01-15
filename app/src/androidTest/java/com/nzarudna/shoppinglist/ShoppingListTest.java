package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.runner.AndroidJUnit4;

import com.nzarudna.shoppinglist.model.ProductsList;
import com.nzarudna.shoppinglist.model.ShoppingList;
import com.nzarudna.shoppinglist.model.dao.CategoryDao;
import com.nzarudna.shoppinglist.model.dao.DaoFactory;
import com.nzarudna.shoppinglist.model.dao.ProductDao;
import com.nzarudna.shoppinglist.model.dao.ProductTemplateDao;
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

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
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

    private UserDao mUserDao;
    private CategoryDao mCategoryDao;
    private ProductsListDao mProductsListDao;
    private ProductDao mProductDao;
    private ProductTemplateDao mProductTemplateDao;

    private int mSelfUser;
    private int mUser2;
    private int mCategory1;
    private int mCategory2;
    private int mProduct1;
    private int mProduct2;
    private int mProductsList1;
    private int mProductsList2;
    private int mProductTemplate1;
    private int mProductTemplate2;

    @Before
    public void setUp() {

        mMockContext = Mockito.mock(Context.class);

        Resources resources = Mockito.mock(Resources.class);
        when(mMockContext.getResources()).thenReturn(resources);

        mSharedPreferences = Mockito.mock(SharedPreferences.class);
        when(mMockContext.getSharedPreferences(mMockContext.getString(R.string.preference_key_file), Context.MODE_PRIVATE)).thenReturn(mSharedPreferences);

        AppDatabase.switchToInMemory(mMockContext);
        mUserDao = DaoFactory.getInstance().getUserDao(mMockContext);
        mCategoryDao = DaoFactory.getInstance().getCategoryDao(mMockContext);
        mProductsListDao = DaoFactory.getInstance().getProductsListDao(mMockContext);
        mProductDao = DaoFactory.getInstance().getProductDao(mMockContext);
        mProductTemplateDao = DaoFactory.getInstance().getProductTemplateDao(mMockContext);

        createSelfUser();
        createTestData();

        when(mMockContext.getString(R.string.default_list_name)).thenReturn(MOCKED_DEFAULT_LIST_NAME);
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
    public void copyList_testData() throws InterruptedException {

        int etalonListID = mProductsList2;
        ShoppingList newList = ShoppingList.copyList(etalonListID);

        LiveData<ProductsList> newListLiveData = newList.getListData();
        ProductsList newProductsList = TestUtils.findByIDSync(newListLiveData);

        LiveData<ProductsList> etalonListLiveData = mProductsListDao.findByID(etalonListID);
        ProductsList etalonProductsList = TestUtils.findByIDSync(etalonListLiveData);

        assertThat(newProductsList.getName(), is(etalonProductsList.getName()));
        TestUtils.assertDateEqualsToSeconds(new Date(), newProductsList.getCreatedAt());
        assertThat(newProductsList.getCreatedBy(), is(mSelfUser));
        assertNull(newProductsList.getModifiedAt());
        assertNull(newProductsList.getModifiedBy());
        assertNull(newProductsList.getAssignedID());
        assertThat(newProductsList.getStatus(), is(ProductsList.STATUS_ACTIVE));
    }

    @Test
    public void copyList_testProducts() throws InterruptedException {

        int etalonListID = mProductsList1;
        ShoppingList list = ShoppingList.copyList(etalonListID);
        LiveData<ProductsList> listLiveData = list.getListData();

        ProductsList productsList = TestUtils.findByIDSync(listLiveData);

        
    }

    private void createTestData() {
        mUser2 = TestUtils.insertUser(mUserDao);
        mCategory1 = TestUtils.insertCategory(mCategoryDao);
        mCategory2 = TestUtils.insertCategory(mCategoryDao);
        mProductsList1 = TestUtils.insertProductsList(mProductsListDao, mSelfUser);
        mProductsList2 = TestUtils.insertProductsList(mProductsListDao, mUser2);
        mProduct1 = TestUtils.insertProduct(mProductDao, mProductsList1);
        mProduct2 = TestUtils.insertProduct(mProductDao, mProductsList1);
        mProductTemplate1 = TestUtils.insertProductTemplate(mProductTemplateDao);
        mProductTemplate2 = TestUtils.insertProductTemplate(mProductTemplateDao);
    }

    @After
    public void closeDB() {

        AppDatabase.getInstance(mMockContext).close();
    }

}
