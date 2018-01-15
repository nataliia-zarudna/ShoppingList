package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.test.runner.AndroidJUnit4;

import com.nzarudna.shoppinglist.model.Product;
import com.nzarudna.shoppinglist.model.ProductsList;
import com.nzarudna.shoppinglist.model.ShoppingList;
import com.nzarudna.shoppinglist.model.ShoppingListException;
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
import org.mockito.Mockito;

import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
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
    public void copyList_testEqualsData() throws InterruptedException, ShoppingListException {

        int etalonListID = mProductsList2;
        LiveData<ProductsList> etalonListLiveData = mProductsListDao.findByID(etalonListID);
        ProductsList etalonProductsList = TestUtils.findByIDSync(etalonListLiveData);
        etalonProductsList.setName("Not default name");
        mProductsListDao.update(etalonProductsList);

        ShoppingList newList = ShoppingList.copyList(mMockContext, etalonListID);

        LiveData<ProductsList> newListLiveData = newList.getListData();
        ProductsList newProductsList = TestUtils.findByIDSync(newListLiveData);

        assertThat(newProductsList.getName(), is(etalonProductsList.getName()));
        TestUtils.assertDateEqualsToSeconds(new Date(), newProductsList.getCreatedAt());
        assertThat(newProductsList.getCreatedBy(), is(mSelfUser));
        assertNull(newProductsList.getModifiedAt());
        assertNull(newProductsList.getModifiedBy());
        assertNull(newProductsList.getAssignedID());
        assertThat(newProductsList.getStatus(), is(ProductsList.STATUS_ACTIVE));

        //verify(mNotificationManager).sendNotification();
    }

    @Test
    public void copyList_testEqualsProducts() throws InterruptedException, ShoppingListException {

        int etalonListID = mProductsList1;
        List<Product> etalonProductsList = mProductDao.findByListIDSync(etalonListID);

        Product etalonListProduct = etalonProductsList.get(1);
        etalonListProduct.setStatus(Product.BOUGHT);
        etalonListProduct.setName("Custom name");
        etalonListProduct.setComment("Some comments");
        etalonListProduct.setCount(5.5);
        etalonListProduct.setOrder(3);
        mProductDao.update(etalonListProduct);

        ShoppingList newList = ShoppingList.copyList(mMockContext, etalonListID);
        List<Product> newProductsList = mProductDao.findByListIDSync(newList.getListID());

        assertEquals(etalonProductsList.size(), newProductsList.size());

        for (int i = 0; i < etalonProductsList.size(); i++) {

            Product etalonProduct = etalonProductsList.get(i);
            Product newProduct = newProductsList.get(i);

            assertThat(newProduct.getName(), is(etalonProduct.getName()));
            assertThat(newProduct.getCategoryID(), is(etalonProduct.getCategoryID()));
            assertThat(newProduct.getComment(), is(etalonProduct.getComment()));
            assertThat(newProduct.getCount(), is(etalonProduct.getCount()));
            assertThat(newProduct.getListID(), is(newList.getListID()));
            assertThat(newProduct.getOrder(), is(etalonProduct.getOrder()));
            assertThat(newProduct.getStatus(), is(Product.TO_BUY));
            assertThat(newProduct.getUnitID(), is(etalonProduct.getUnitID()));
        }

        //verify(mNotificationManager).sendNotification();
    }

    @Test(expected = ShoppingListException.class)
    public void copyList_testException_OnNonexistentList() throws InterruptedException, ShoppingListException {

        ShoppingList.copyList(mMockContext, 99);
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
