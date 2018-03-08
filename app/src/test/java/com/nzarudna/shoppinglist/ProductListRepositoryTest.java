package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.product.list.ProductListDao;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.ShoppingListException;
import com.nzarudna.shoppinglist.model.user.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.nzarudna.shoppinglist.SharedPreferencesConstants.*;
import static com.nzarudna.shoppinglist.model.product.list.ProductListRepository.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test Shopping List
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductListRepositoryTest {

    private static final String MOCKED_DEFAULT_LIST_NAME = "Default List Name";
    private static final String MOCKED_DEFAULT_LIST_NAME_FROM_PREFERENCES = "My own default List Name";
    private static final UUID MOCKED_SELF_USER_ID = UUID.randomUUID();

    private ProductListRepository mSubject;

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

        mSubject = new ProductListRepository(mProductListDao, mProductDao, mProductTemplateRepository,
                mUserRepository, mResourceResolver, mSharedPreferences);
    }

    @Test
    public void createList() throws InterruptedException {

        final ProductList expectedProductList = new ProductList(MOCKED_DEFAULT_LIST_NAME, MOCKED_SELF_USER_ID);
        expectedProductList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.createNewList(new ProductListRepository.OnProductListCreateListener() {
            @Override
            public void onCreate(UUID productListID) {

                assertNotNull(productListID);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();

        verify(mProductListDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProductList)));
    }

    @Test
    public void createList_testDefaultParams_fromPreferences() throws InterruptedException {

        when(mSharedPreferences.getString(DEFAULT_PRODUCT_LIST_NAME, MOCKED_DEFAULT_LIST_NAME))
                .thenReturn(MOCKED_DEFAULT_LIST_NAME_FROM_PREFERENCES);
        when(mSharedPreferences.getBoolean(DEFAULT_PRODUCT_LIST_IS_GROUPED_VIEW, false))
                .thenReturn(true);

        ProductList expectedProductList = new ProductList(MOCKED_DEFAULT_LIST_NAME_FROM_PREFERENCES, MOCKED_SELF_USER_ID);
        expectedProductList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        expectedProductList.setIsGroupedView(true);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.createNewList(new ProductListRepository.OnProductListCreateListener() {
            @Override
            public void onCreate(UUID productListID) {

                assertNotNull(productListID);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();

        verify(mProductListDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProductList)));
    }

    @Test
    public void copyList_testEqualsData() throws InterruptedException, ShoppingListException {

        UUID etalonListID = UUID.randomUUID();
        ProductList etalonList = new ProductList("Some list name", UUID.randomUUID());
        etalonList.setListID(etalonListID);
        etalonList.setSorting(SORT_LISTS_BY_CREATED_AT);
        etalonList.setIsGroupedView(true);
        etalonList.setStatus(ProductList.STATUS_ARCHIVED);
        when(mProductListDao.findByIDSync(etalonListID)).thenReturn(etalonList);

        final long mockNewListID = 12;
        ProductList expectedList = new ProductList(etalonList.getName(), MOCKED_SELF_USER_ID);
        expectedList.setCreatedAt(new Date());
        expectedList.setSorting(SORT_LISTS_BY_CREATED_AT);
        expectedList.setIsGroupedView(true);
        expectedList.setStatus(ProductList.STATUS_ACTIVE);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.copyList(etalonListID, new ProductListRepository.OnProductListCreateListener() {
            @Override
            public void onCreate(UUID productListID) {

                assertNotNull(productListID);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();

        verify(mProductListDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedList)));
    }

    @Test
    public void copyList_testEqualsProducts() throws InterruptedException, ShoppingListException {

        UUID etalonListID = UUID.randomUUID();
        ProductList etalonList = new ProductList("Some etalon name", UUID.randomUUID());
        when(mProductListDao.findByIDSync(etalonListID)).thenReturn(etalonList);

        List<Product> etalonProducts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Product etalonProduct = new Product("Custom name");
            etalonProduct.setStatus(Product.BOUGHT);
            etalonProduct.setComment("Some comments");
            etalonProduct.setCategoryID(UUID.randomUUID());
            etalonProduct.setUnitID(UUID.randomUUID());
            etalonProduct.setCount(i + 5.5);
            etalonProduct.setOrder(i);

            etalonProducts.add(etalonProduct);
        }
        etalonProducts.get(1).setStatus(Product.ABSENT);
        etalonProducts.get(2).setStatus(Product.TO_BUY);
        when(mProductDao.findByListIDSync(etalonListID)).thenReturn(etalonProducts);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.copyList(etalonListID, new ProductListRepository.OnProductListCreateListener() {
            @Override
            public void onCreate(UUID productListID) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        List<Product> expectedProducts = etalonProducts;
        for (final Product expectedProduct : expectedProducts) {
            expectedProduct.setStatus(Product.TO_BUY);

            verify(mProductDao).insert(
                    argThat(AssertUtils.getProductArgumentMatcheWithoutPKAndListID(expectedProduct)));
        }
    }

    @Test(expected = ShoppingListException.class)
    public void copyList_testException_OnNonexistentList() throws InterruptedException, ShoppingListException {

        mSubject.copyList(UUID.randomUUID(), null);
    }

    @Test
    public void removeList() {
        ProductList listToRemove = new ProductList("Some name", UUID.randomUUID());

        mSubject.removeList(listToRemove.getListID());

        verify(mProductListDao).deleteByID(listToRemove.getListID());
    }

    @Test
    public void getList() {

        UUID listID = UUID.randomUUID();
        LiveData<ProductList> productsListLiveData = Mockito.mock(LiveData.class);
        when(mProductListDao.findByID(listID)).thenReturn(productsListLiveData);
        when(mProductListDao.findByIDSync(listID)).thenReturn(new ProductList("Some name", UUID.randomUUID()));

        ShoppingList shoppingList = mSubject.getShoppingList(listID);

        assertEquals(shoppingList.getListData(), productsListLiveData);
        assertEquals(shoppingList.getListID(), listID);
    }

    @Test
    public void getList_testNull_onNonexistentList() {

        ShoppingList shoppingList = mSubject.getShoppingList(UUID.randomUUID());

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

    @Test
    public void findAllLists() throws InterruptedException, ShoppingListException {

        mSubject.getAllLists();

        verify(mProductListDao).findAllSortByModifiedAtDesc();
    }
}
