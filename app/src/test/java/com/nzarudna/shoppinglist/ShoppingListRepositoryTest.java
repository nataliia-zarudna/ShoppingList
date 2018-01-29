package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductListDao;
import com.nzarudna.shoppinglist.persistence.ProductTemplateDao;
import com.nzarudna.shoppinglist.product.Product;
import com.nzarudna.shoppinglist.product.ProductList;
import com.nzarudna.shoppinglist.product.ProductListWithStatistics;
import com.nzarudna.shoppinglist.product.ProductTemplateRepository;
import com.nzarudna.shoppinglist.product.ShoppingList;
import com.nzarudna.shoppinglist.product.ShoppingListException;
import com.nzarudna.shoppinglist.product.ShoppingListRepository;
import com.nzarudna.shoppinglist.user.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.nzarudna.shoppinglist.SharedPreferencesConstants.*;
import static com.nzarudna.shoppinglist.product.ShoppingListRepository.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test Shopping List
 */
@RunWith(MockitoJUnitRunner.class)
public class ShoppingListRepositoryTest {

    private static final String MOCKED_DEFAULT_LIST_NAME = "Default List Name";
    private static final String MOCKED_DEFAULT_LIST_NAME_FROM_PREFERENCES = "My own default List Name";
    private static final int MOCKED_SELF_USER_ID = 2;

    private ShoppingListRepository mSubject;

    @Mock
    private UserRepository mUserRepository;
    @Mock
    private ResourceResolver mResourceResolver;
    @Mock
    private SharedPreferences mSharedPreferences;

    @Mock
    private ProductListDao mProductListDao;
    @Mock
    private ProductDao mProductDao;
    @Mock
    private ProductTemplateRepository mProductTemplateRepository;
    @Mock
    private ProductTemplateDao mProductTemplateDao;

    @Before
    public void setUp() {

        when(mResourceResolver.getString(R.string.default_list_name))
                .thenReturn(MOCKED_DEFAULT_LIST_NAME);

        when(mSharedPreferences.getString(DEFAULT_PRODUCT_LIST_NAME, MOCKED_DEFAULT_LIST_NAME))
                .thenReturn(MOCKED_DEFAULT_LIST_NAME);

        when(mUserRepository.getSelfUserID()).thenReturn(MOCKED_SELF_USER_ID);

        mSubject = new ShoppingListRepository(mProductListDao, mProductDao, mProductTemplateRepository,
                mUserRepository, mResourceResolver, mSharedPreferences);
    }

    @Test
    public void createList() throws InterruptedException {

        final long mockListID = 33;

        ProductList expectedProductList = new ProductList();
        expectedProductList.setName(MOCKED_DEFAULT_LIST_NAME);
        expectedProductList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        expectedProductList.setCreatedBy(MOCKED_SELF_USER_ID);
        when(mProductListDao.insert(expectedProductList)).thenReturn(mockListID);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.createList(new ShoppingListRepository.OnProductListCreateListener() {
            @Override
            public void onCreate(int productListID) {

                assertEquals(productListID, mockListID);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();

        verify(mProductListDao).insert(expectedProductList);
    }

    @Test
    public void createList_testDefaultParams_fromPreferences() throws InterruptedException {

        when(mSharedPreferences.getString(DEFAULT_PRODUCT_LIST_NAME, MOCKED_DEFAULT_LIST_NAME))
                .thenReturn(MOCKED_DEFAULT_LIST_NAME_FROM_PREFERENCES);
        when(mSharedPreferences.getBoolean(DEFAULT_PRODUCT_LIST_IS_GROUPED_VIEW, false))
                .thenReturn(true);

        final long mockListID = 33;

        ProductList expectedProductList = new ProductList();
        expectedProductList.setName(MOCKED_DEFAULT_LIST_NAME_FROM_PREFERENCES);
        expectedProductList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        expectedProductList.setIsGroupedView(true);
        expectedProductList.setCreatedBy(MOCKED_SELF_USER_ID);
        when(mProductListDao.insert(expectedProductList)).thenReturn(mockListID);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.createList(new ShoppingListRepository.OnProductListCreateListener() {
            @Override
            public void onCreate(int productListID) {

                assertEquals(productListID, mockListID);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();

        verify(mProductListDao).insert(expectedProductList);
    }

    @Test
    public void copyList_testEqualsData() throws InterruptedException, ShoppingListException {

        int etalonListID = 2;
        ProductList etalonList = new ProductList();
        etalonList.setListID(etalonListID);
        etalonList.setName("Some list name");
        etalonList.setSorting(SORT_LISTS_BY_CREATED_AT);
        etalonList.setIsGroupedView(true);
        etalonList.setStatus(ProductList.STATUS_ARCHIVED);
        when(mProductListDao.findByIDSync(etalonListID)).thenReturn(etalonList);

        final long mockNewListID = 12;
        ProductList expectedList = new ProductList();
        expectedList.setName(etalonList.getName());
        expectedList.setCreatedAt(new Date());
        expectedList.setCreatedBy(MOCKED_SELF_USER_ID);
        expectedList.setSorting(SORT_LISTS_BY_CREATED_AT);
        expectedList.setIsGroupedView(true);
        expectedList.setStatus(ProductList.STATUS_ACTIVE);
        when(mProductListDao.insert(expectedList)).thenReturn(mockNewListID);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.copyList(etalonListID, new ShoppingListRepository.OnProductListCreateListener() {
            @Override
            public void onCreate(int productListID) {

                assertEquals(productListID, mockNewListID);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();

        verify(mProductListDao).insert(expectedList);
    }

    @Test
    public void copyList_testEqualsProducts() throws InterruptedException, ShoppingListException {

        int etalonListID = 2;
        ProductList etalonList = new ProductList();
        when(mProductListDao.findByIDSync(etalonListID)).thenReturn(etalonList);

        List<Product> etalonProducts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Product etalonProduct = new Product();
            etalonProduct.setStatus(Product.BOUGHT);
            etalonProduct.setName("Custom name");
            etalonProduct.setComment("Some comments");
            etalonProduct.setCategoryID(2);
            etalonProduct.setUnitID(3);
            etalonProduct.setCount(i + 5.5);
            etalonProduct.setOrder(i);

            etalonProducts.add(etalonProduct);
        }
        etalonProducts.get(1).setStatus(Product.ABSENT);
        etalonProducts.get(2).setStatus(Product.TO_BUY);
        when(mProductDao.findByListIDSync(etalonListID)).thenReturn(etalonProducts);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.copyList(etalonListID, new ShoppingListRepository.OnProductListCreateListener() {
            @Override
            public void onCreate(int productListID) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        List<Product> expectedProducts = etalonProducts;
        for (Product expectedProduct : expectedProducts) {
            expectedProduct.setStatus(Product.TO_BUY);

            verify(mProductDao).insert(expectedProduct);
        }
    }

    @Test(expected = ShoppingListException.class)
    public void copyList_testException_OnNonexistentList() throws InterruptedException, ShoppingListException {

        mSubject.copyList(99, null);
    }

    @Test
    public void removeList() {
        ProductList listToRemove = new ProductList();

        mSubject.removeList(listToRemove.getListID());

        verify(mProductListDao).deleteByID(listToRemove.getListID());
    }

    @Test
    public void getList() {

        int listID = 3;
        LiveData<ProductList> productsListLiveData = Mockito.mock(LiveData.class);
        when(mProductListDao.findByID(listID)).thenReturn(productsListLiveData);
        when(mProductListDao.findByIDSync(listID)).thenReturn(new ProductList());

        ShoppingList shoppingList = mSubject.getList(listID);

        assertEquals(shoppingList.getListData(), productsListLiveData);
        assertEquals(shoppingList.getListID(), listID);
    }

    @Test
    public void getList_testNull_onNonexistentList() {

        ShoppingList shoppingList = mSubject.getList(99);

        assertNull(shoppingList);
    }

    @Test
    public void getLists_testNull_onNonexistentStatus() throws ShoppingListException {

        DataSource.Factory<Integer, ProductListWithStatistics> lists =
                mSubject.getLists(99, SORT_LISTS_BY_NAME);

        assertNull(lists);
    }

    @Test(expected = ShoppingListException.class)
    public void getLists_testException_onNonexistentSorting() throws ShoppingListException {

        mSubject.getLists(ProductList.STATUS_ACTIVE, 99);
    }

    @Test
    public void findActiveSortByName() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductList.STATUS_ACTIVE, SORT_LISTS_BY_NAME);

        verify(mProductListDao).findWithStaticticsByStatusSortByName(ProductList.STATUS_ACTIVE);
    }

    @Test
    public void findActiveSortByCreatedAtDesc() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductList.STATUS_ACTIVE, SORT_LISTS_BY_CREATED_AT);

        verify(mProductListDao).findWithStaticticsByStatusSortByCreatedAtDesc(ProductList.STATUS_ACTIVE);
    }

    @Test
    public void findActiveSortByCreatedByAndName() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductList.STATUS_ACTIVE, SORT_LISTS_BY_CREATED_BY);

        verify(mProductListDao).findWithStaticticsByStatusSortByCreatedByAndName(ProductList.STATUS_ACTIVE);
    }

    @Test
    public void findActiveSortByModifiedAtDesc() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductList.STATUS_ACTIVE, SORT_LISTS_BY_MODIFIED_AT);

        verify(mProductListDao).findWithStaticticsByStatusSortByModifiedAtDesc(ProductList.STATUS_ACTIVE);
    }

    @Test
    public void findArchivedSortByModifiedAtDesc() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductList.STATUS_ARCHIVED, SORT_LISTS_BY_MODIFIED_AT);

        verify(mProductListDao).findWithStaticticsByStatusSortByModifiedAtDesc(ProductList.STATUS_ARCHIVED);
    }

    @Test
    public void findActiveSortByAssignedAndName() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductList.STATUS_ACTIVE, SORT_LISTS_BY_ASSIGNED);

        verify(mProductListDao).findWithStaticticsByStatusSortByAssignedAndName(ProductList.STATUS_ACTIVE);
    }
}
