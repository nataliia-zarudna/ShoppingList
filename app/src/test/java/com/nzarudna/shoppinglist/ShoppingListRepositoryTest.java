package com.nzarudna.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.nzarudna.shoppinglist.product.ProductsList;
import com.nzarudna.shoppinglist.product.ShoppingList;
import com.nzarudna.shoppinglist.product.ShoppingListRepository;
import com.nzarudna.shoppinglist.persistence.CategoryDao;
import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductTemplateDao;
import com.nzarudna.shoppinglist.persistence.ProductsListDao;
import com.nzarudna.shoppinglist.persistence.UserDao;
import com.nzarudna.shoppinglist.persistence.db.AppDatabase;
import com.nzarudna.shoppinglist.notification.NotificationManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test Shopping List
 */
@RunWith(MockitoJUnitRunner.class)
public class ShoppingListRepositoryTest {

    private static final String MOCKED_DEFAULT_LIST_NAME = "Default List Name";
    private static final int MOCKED_SELF_USER_ID = 2;

    private Context mMockContext;
    private SharedPreferences mSharedPreferences;
    private NotificationManager mNotificationManager;

    private ShoppingListRepository mSubject;

    @Mock
    private UserDao mUserDao;
    @Mock
    private CategoryDao mCategoryDao;
    @Mock
    private ProductsListDao mProductsListDao;
    @Mock
    private ProductDao mProductDao;
    @Mock
    private ProductTemplateDao mProductTemplateDao;

    private int mSelfUser;
    private int mUser2;
    private int mCategory1;
    private int mCategory2;
    private int mProduct1;
    private int mProduct2;
    private int mProductTemplate1;
    private int mProductTemplate2;

    @Before
    public void setUp() {

        mMockContext = Mockito.mock(Context.class);

        Resources resources = Mockito.mock(Resources.class);
        when(mMockContext.getResources()).thenReturn(resources);

        mSharedPreferences = Mockito.mock(SharedPreferences.class);
        when(mMockContext.getSharedPreferences(mMockContext.getString(R.string.preference_key_file), Context.MODE_PRIVATE)).thenReturn(mSharedPreferences);

        /*AppDatabase.switchToInMemory(mMockContext);
        mUserDao = DaoFactory.getInstance().getUserDao(mMockContext);
        mCategoryDao = DaoFactory.getInstance().getCategoryDao(mMockContext);
        mProductsListDao = DaoFactory.getInstance().getProductsListDao(mMockContext);
        mProductDao = DaoFactory.getInstance().getProductDao(mMockContext);
        mProductTemplateDao = DaoFactory.getInstance().getProductTemplateDao(mMockContext);*/

        createSelfUser();
        createTestData();

        when(mMockContext.getString(R.string.default_list_name)).thenReturn(MOCKED_DEFAULT_LIST_NAME);
        when(mSharedPreferences.getInt(SharedPreferencesConstants.SELF_USER_ID, 0)).thenReturn(MOCKED_SELF_USER_ID);

        mSubject = new ShoppingListRepository(mMockContext, mProductsListDao, mProductDao);

        //mNotificationManager = Mockito.mock(NotificationManager.class);
    }

    private void createSelfUser() {
        //UserDao userDao = DaoFactory.getInstance().getUserDao(mMockContext);
        //mSelfUser = TestUtils.insertUser(userDao);
    }

    @Test
    public void createList() throws InterruptedException {

        ShoppingList list = mSubject.createList();

        ProductsList expectedProductsList = new ProductsList();
        expectedProductsList.setName(MOCKED_DEFAULT_LIST_NAME);
        expectedProductsList.setCreatedBy(MOCKED_SELF_USER_ID);

        verify(mProductsListDao).insert(expectedProductsList);

        /*ProductsList productsList = TestUtils.getLiveDataValueSync(listLiveData);

        assertThat(productsList.getName(), is(MOCKED_DEFAULT_LIST_NAME));
        assertThat(productsList.getCreatedBy(), is(mSelfUser));*/

        //verify(mNotificationManager).sendNotification();
    }
/*
    @Test
    public void copyList_testEqualsData() throws InterruptedException, ShoppingListException {

        int etalonListID = TestUtils.insertProductsList(mProductsListDao, mSelfUser);

        LiveData<ProductsList> etalonListLiveData = mProductsListDao.findByID(etalonListID);
        ProductsList etalonProductsList = TestUtils.getLiveDataValueSync(etalonListLiveData);
        etalonProductsList.setName("Not default name");
        mProductsListDao.update(etalonProductsList);

        ShoppingList newList = ShoppingList.copyList(mMockContext, etalonListID);

        LiveData<ProductsList> newListLiveData = newList.getListData();
        ProductsList newProductsList = TestUtils.getLiveDataValueSync(newListLiveData);

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

        int etalonListID = TestUtils.insertProductsList(mProductsListDao, mSelfUser);
        TestUtils.insertProduct(mProductDao, etalonListID);
        TestUtils.insertProduct(mProductDao, etalonListID);
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

    @Test
    public void findActiveSortByName() throws InterruptedException, ShoppingListException {

        List<ProductsList> lists = TestUtils.createProductsLists(4, mSelfUser);
        lists.get(0).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(0).setName("#1");

        lists.get(1).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(1).setName("#3");

        lists.get(2).setStatus(ProductsList.STATUS_ARCHIVED);

        lists.get(3).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(3).setName("#2");

        TestUtils.insertProductsLists(mProductsListDao, lists);

        List<ProductsList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(0));
        activeLists.add(lists.get(3));
        activeLists.add(lists.get(1));

        DataSource.Factory<Integer, ProductsList> foundProductsListDS =
                ShoppingList.getLists(mMockContext, ProductsList.STATUS_ACTIVE, ShoppingList.SORT_LISTS_BY_NAME);
        PagedList<ProductsList> foundProductsList = TestUtils.getPagedListSync(foundProductsListDS);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByCreatedAtDesc() throws InterruptedException, ShoppingListException {

        List<ProductsList> lists = TestUtils.createProductsLists(4, mSelfUser);
        lists.get(0).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(0).setCreatedAt(new Date(3000));

        lists.get(1).setStatus(ProductsList.STATUS_ARCHIVED);

        lists.get(2).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(2).setCreatedAt(new Date(1000));

        lists.get(3).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(3).setCreatedAt(new Date(2000));

        TestUtils.insertProductsLists(mProductsListDao, lists);

        List<ProductsList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(0));
        activeLists.add(lists.get(3));
        activeLists.add(lists.get(2));

        DataSource.Factory<Integer, ProductsList> foundProductsListDS =
                ShoppingList.getLists(mMockContext, ProductsList.STATUS_ACTIVE, ShoppingList.SORT_LISTS_BY_CREATED_AT);
        PagedList<ProductsList> foundProductsList = TestUtils.getPagedListSync(foundProductsListDS);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByCreatedByAndName() throws InterruptedException, ShoppingListException {

        List<ProductsList> lists = TestUtils.createProductsLists(4, mSelfUser);
        lists.get(0).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(0).setName("#1");
        lists.get(0).setCreatedBy(mUser2);

        lists.get(1).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(1).setName("#3");
        lists.get(1).setCreatedBy(mSelfUser);

        lists.get(2).setStatus(ProductsList.STATUS_ARCHIVED);
        lists.get(2).setCreatedBy(mSelfUser);

        lists.get(3).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(3).setName("#2");
        lists.get(3).setCreatedBy(mUser2);

        TestUtils.insertProductsLists(mProductsListDao, lists);

        List<ProductsList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(1));
        activeLists.add(lists.get(0));
        activeLists.add(lists.get(3));

        DataSource.Factory<Integer, ProductsList> foundLists =
                ShoppingList.getLists(mMockContext, ProductsList.STATUS_ACTIVE, ShoppingList.SORT_LISTS_BY_CREATED_BY);
        PagedList<ProductsList> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByModifiedAtDesc() throws InterruptedException, ShoppingListException {

        List<ProductsList> lists = TestUtils.createProductsLists(4, mSelfUser);
        lists.get(0).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(0).setModifiedAt(new Date(3000));

        lists.get(1).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(1).setModifiedAt(new Date(1000));

        lists.get(2).setStatus(ProductsList.STATUS_ARCHIVED);

        lists.get(3).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(3).setModifiedAt(new Date(2000));

        TestUtils.insertProductsLists(mProductsListDao, lists);

        List<ProductsList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(0));
        activeLists.add(lists.get(3));
        activeLists.add(lists.get(1));

        DataSource.Factory<Integer, ProductsList> foundLists =
                ShoppingList.getLists(mMockContext, ProductsList.STATUS_ACTIVE, ShoppingList.SORT_LISTS_BY_MODIFIED_AT);
        PagedList<ProductsList> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findArchivedSortByModifiedAtDesc() throws InterruptedException, ShoppingListException {

        List<ProductsList> lists = TestUtils.createProductsLists(4, mSelfUser);
        lists.get(0).setStatus(ProductsList.STATUS_ARCHIVED);
        lists.get(0).setModifiedAt(new Date(3000));

        lists.get(1).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(1).setModifiedAt(new Date(1000));

        lists.get(2).setStatus(ProductsList.STATUS_ARCHIVED);
        lists.get(2).setModifiedAt(new Date(4000));

        lists.get(3).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(3).setModifiedAt(new Date(2000));

        TestUtils.insertProductsLists(mProductsListDao, lists);

        List<ProductsList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(2));
        activeLists.add(lists.get(0));

        DataSource.Factory<Integer, ProductsList> foundLists =
                ShoppingList.getLists(mMockContext, ProductsList.STATUS_ARCHIVED, ShoppingList.SORT_LISTS_BY_MODIFIED_AT);
        PagedList<ProductsList> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }

    @Test
    public void findActiveSortByAssignedAndName() throws InterruptedException, ShoppingListException {

        List<ProductsList> lists = TestUtils.createProductsLists(4, mSelfUser);
        lists.get(0).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(0).setName("#1");
        lists.get(0).setAssignedID(mUser2);

        lists.get(1).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(1).setName("#3");
        lists.get(1).setAssignedID(mSelfUser);

        lists.get(2).setStatus(ProductsList.STATUS_ARCHIVED);
        lists.get(2).setAssignedID(mSelfUser);

        lists.get(3).setStatus(ProductsList.STATUS_ACTIVE);
        lists.get(3).setName("#2");
        lists.get(3).setAssignedID(mUser2);

        TestUtils.insertProductsLists(mProductsListDao, lists);

        List<ProductsList> activeLists = new ArrayList<>();
        activeLists.add(lists.get(1));
        activeLists.add(lists.get(0));
        activeLists.add(lists.get(3));

        DataSource.Factory<Integer, ProductsList> foundLists =
                ShoppingList.getLists(mMockContext, ProductsList.STATUS_ACTIVE, ShoppingList.SORT_LISTS_BY_ASSIGNED);
        PagedList<ProductsList> foundProductsList = TestUtils.getPagedListSync(foundLists);

        TestUtils.assertEquals(activeLists, foundProductsList);
    }*/

    private void createTestData() {
        /*mUser2 = TestUtils.insertUser(mUserDao);
        mCategory1 = TestUtils.insertCategory(mCategoryDao);
        mCategory2 = TestUtils.insertCategory(mCategoryDao);
        mProductTemplate1 = TestUtils.insertProductTemplate(mProductTemplateDao);
        mProductTemplate2 = TestUtils.insertProductTemplate(mProductTemplateDao);*/
    }

    @After
    public void closeDB() {

        AppDatabase.getInstance(mMockContext).close();
    }

}
