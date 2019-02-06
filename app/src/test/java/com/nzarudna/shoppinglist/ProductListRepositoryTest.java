package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.exception.ShoppingListException;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListDao;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateDao;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.user.UserRepository;
import com.nzarudna.shoppinglist.utils.AppExecutors;

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

import static com.nzarudna.shoppinglist.SharedPreferencesConstants.DEFAULT_PRODUCT_LIST_IS_GROUPED_VIEW;
import static com.nzarudna.shoppinglist.SharedPreferencesConstants.DEFAULT_PRODUCT_LIST_NAME;
import static com.nzarudna.shoppinglist.model.product.list.ProductListRepository.SORT_LISTS_BY_ASSIGNED;
import static com.nzarudna.shoppinglist.model.product.list.ProductListRepository.SORT_LISTS_BY_CREATED_AT;
import static com.nzarudna.shoppinglist.model.product.list.ProductListRepository.SORT_LISTS_BY_CREATED_BY;
import static com.nzarudna.shoppinglist.model.product.list.ProductListRepository.SORT_LISTS_BY_MODIFIED_AT;
import static com.nzarudna.shoppinglist.model.product.list.ProductListRepository.SORT_LISTS_BY_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test Shopping List
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductListRepositoryTest extends BaseAsyncTest {

    private static final String MOCKED_DEFAULT_LIST_NAME = "Default List Name";
    private static final String MOCKED_DEFAULT_LIST_NAME_FROM_PREFERENCES = "My own default List Name";
    private static final UUID MOCKED_SELF_USER_ID = UUID.randomUUID();

    private ProductListRepository mSubject;
    private AppExecutors mAppExecutors;

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

        mAppExecutors = new TestAppExecutors();
        mSubject = new ProductListRepository(mProductListDao, mProductDao, mProductTemplateRepository,
                mUserRepository, mResourceResolver, mSharedPreferences, mAppExecutors);
    }

    @Test
    public void createList() throws InterruptedException {

        final ProductList expectedProductList = new ProductList(MOCKED_DEFAULT_LIST_NAME, MOCKED_SELF_USER_ID);
        expectedProductList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        AsyncResultListener<ProductList> asyncListener = (AsyncResultListener<ProductList>) Mockito.mock(AsyncResultListener.class);
        doAnswer(invocation -> {

            assertNotNull(invocation.getArgument(0));

            countDownLatch.countDown();
            return null;
        }).when(asyncListener).onAsyncSuccess(any(ProductList.class));

        mSubject.createNewList(asyncListener);
        await(countDownLatch);

        verify(mProductListDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProductList)));
        verify(asyncListener).onAsyncSuccess(any(ProductList.class));
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

        AsyncResultListener<ProductList> asyncListener = (AsyncResultListener<ProductList>) Mockito.mock(AsyncResultListener.class);
        doAnswer(invocation -> {

            assertNotNull(invocation.getArgument(0));

            countDownLatch.countDown();
            return null;
        }).when(asyncListener).onAsyncSuccess(any(ProductList.class));

        mSubject.createNewList(asyncListener);
        await(countDownLatch);

        verify(mProductListDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProductList)));
        verify(asyncListener).onAsyncSuccess(any(ProductList.class));
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

        AsyncResultListener<ProductList> asyncListener = (AsyncResultListener<ProductList>) Mockito.mock(AsyncResultListener.class);
        doAnswer(invocation -> {

            assertNotNull(invocation.getArgument(0));

            countDownLatch.countDown();
            return null;
        }).when(asyncListener).onAsyncSuccess(any(ProductList.class));

        mSubject.copyList(etalonListID, asyncListener);
        await(countDownLatch);

        verify(mProductListDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedList)));
    }

    @Test
    public void copyList_testEqualsProducts() throws InterruptedException {

        UUID etalonListID = UUID.randomUUID();
        ProductList etalonList = new ProductList("Some etalon name", UUID.randomUUID());
        when(mProductListDao.findByIDSync(etalonListID)).thenReturn(etalonList);

        List<Product> etalonProducts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Product etalonProduct = new Product();
            etalonProduct.setName("Custom name");
            etalonProduct.setStatus(Product.BOUGHT);
            etalonProduct.setComment("Some comments");
            etalonProduct.setCategoryID(UUID.randomUUID());
            etalonProduct.setUnitID(UUID.randomUUID());
            etalonProduct.setCount(i + 5.5);
            etalonProduct.setOrder(i);

            etalonProducts.add(etalonProduct);
        }
        etalonProducts.get(1).setStatus(Product.ABSENT);
        etalonProducts.get(2).setStatus(Product.ACTIVE);
        when(mProductDao.findByListIDSync(etalonListID)).thenReturn(etalonProducts);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        AsyncResultListener<ProductList> asyncListener = (AsyncResultListener<ProductList>) Mockito.mock(AsyncResultListener.class);
        doAnswer(invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(asyncListener).onAsyncSuccess(any(ProductList.class));
        mSubject.copyList(etalonListID, asyncListener);
        await(countDownLatch);

        List<Product> expectedProducts = etalonProducts;
        for (final Product expectedProduct : expectedProducts) {
            expectedProduct.setStatus(Product.ACTIVE);

            verify(mProductDao).insert(
                    argThat(AssertUtils.getProductArgumentMatcheWithoutPKAndListID(expectedProduct)));
        }

        verify(asyncListener).onAsyncSuccess(any(ProductList.class));
    }

    @Test
    public void createList_fromTemplates() throws InterruptedException {

        List<ProductTemplate> templates = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ProductTemplate template = new ProductTemplate();
            template.setName("temp #" + i);
            template.setCategoryID(UUID.randomUUID());
            template.setUnitID(UUID.randomUUID());
            templates.add(template);
        }

        final ProductList expectedProductList = new ProductList(MOCKED_DEFAULT_LIST_NAME, MOCKED_SELF_USER_ID);
        expectedProductList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        when(mProductListDao.findByIDSync(any(UUID.class))).thenReturn(expectedProductList);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final List<ProductList> resultList = new ArrayList<>();

        AsyncResultListener<ProductList> asyncListener = (AsyncResultListener<ProductList>) Mockito.mock(AsyncResultListener.class);
        doAnswer(invocation -> {

            ProductList productList = invocation.getArgument(0);
            resultList.add(productList);

            assertNotNull(productList);

            countDownLatch.countDown();
            return null;
        }).when(asyncListener).onAsyncSuccess(any(ProductList.class));

        mSubject.createNewList(templates, asyncListener);
        await(countDownLatch);

        expectedProductList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        verify(mProductListDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProductList)));

        for (ProductTemplate template : templates) {

            Product product = new Product();
            product.setName(template.getName());
            product.setListID(resultList.get(0).getListID());
            product.setCategoryID(template.getCategoryID());
            product.setUnitID(template.getUnitID());
            product.setTemplateID(template.getTemplateID());

            verify(mProductDao).insert(argThat(AssertUtils.getArgumentMatcher(product)));
        }
        verify(asyncListener).onAsyncSuccess(any(ProductList.class));
    }

    @Test
    public void updateListStatus() throws InterruptedException {

        ProductList productList = new ProductList(MOCKED_DEFAULT_LIST_NAME, MOCKED_SELF_USER_ID);
        when(mProductListDao.findByIDSync(productList.getListID())).thenReturn(productList);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.updateListStatus(productList.getListID(), ProductList.STATUS_ARCHIVED, null);
        await(countDownLatch);

        productList.setStatus(ProductList.STATUS_ARCHIVED);

        verify(mProductListDao).update(productList);
    }

    @Test
    public void removeList() throws InterruptedException {
        ProductList listToRemove = new ProductList("Some name", UUID.randomUUID());
        CountDownLatch countDownLatch = new CountDownLatch(1);

        AsyncListener asyncListener = Mockito.mock(AsyncListener.class);
        doAnswer(invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(asyncListener).onAsyncSuccess();

        mSubject.removeList(listToRemove.getListID(), asyncListener);
        await(countDownLatch);

        verify(mProductListDao).deleteByID(listToRemove.getListID());
        verify(asyncListener).onAsyncSuccess();
    }

    @Test
    public void getList() {

        UUID listID = UUID.randomUUID();
        LiveData<ProductList> productsListLiveData = Mockito.mock(LiveData.class);
        when(mProductListDao.findByID(listID)).thenReturn(productsListLiveData);

        ShoppingList shoppingList = mSubject.getShoppingList(listID);

        assertEquals(shoppingList.getListData(), productsListLiveData);
        assertEquals(shoppingList.getListID(), listID);
    }

    @Test
    public void getLists_testNull_onNonexistentStatus() throws ShoppingListException {

        DataSource.Factory<Integer, ProductListWithStatistics> lists =
                mSubject.getListsWithStatistics(99, SORT_LISTS_BY_NAME);

        assertNull(lists);
    }

    @Test(expected = ShoppingListException.class)
    public void getLists_testException_onNonexistentSorting() throws ShoppingListException {

        mSubject.getListsWithStatistics(ProductList.STATUS_ACTIVE, 99);
    }

    @Test
    public void findActiveSortByName() throws InterruptedException, ShoppingListException {

        mSubject.getListsWithStatistics(ProductList.STATUS_ACTIVE, SORT_LISTS_BY_NAME);

        verify(mProductListDao).findWithStatisticsByStatusSortByName(ProductList.STATUS_ACTIVE);
    }

    @Test
    public void findActiveSortByCreatedAtDesc() throws InterruptedException, ShoppingListException {

        mSubject.getListsWithStatistics(ProductList.STATUS_ACTIVE, SORT_LISTS_BY_CREATED_AT);

        verify(mProductListDao).findWithStatisticsByStatusSortByCreatedAtDesc(ProductList.STATUS_ACTIVE);
    }

    @Test
    public void findActiveSortByCreatedByAndName() throws InterruptedException, ShoppingListException {

        mSubject.getListsWithStatistics(ProductList.STATUS_ACTIVE, SORT_LISTS_BY_CREATED_BY);

        verify(mProductListDao).findWithStatisticsByStatusSortByCreatedByAndName(ProductList.STATUS_ACTIVE);
    }

    @Test
    public void findActiveSortByModifiedAtDesc() throws InterruptedException, ShoppingListException {

        mSubject.getListsWithStatistics(ProductList.STATUS_ACTIVE, SORT_LISTS_BY_MODIFIED_AT);

        verify(mProductListDao).findWithStatisticsByStatusSortByModifiedAtDesc(ProductList.STATUS_ACTIVE);
    }

    @Test
    public void findActiveSortByAssignedAndName() throws InterruptedException, ShoppingListException {

        mSubject.getListsWithStatistics(ProductList.STATUS_ACTIVE, SORT_LISTS_BY_ASSIGNED);

        verify(mProductListDao).findWithStatisticsByStatusSortByAssignedAndName(ProductList.STATUS_ACTIVE);
    }

    @Test
    public void findArchivedSortByName() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductList.STATUS_ARCHIVED, SORT_LISTS_BY_NAME);

        verify(mProductListDao).findByStatusSortByName(ProductList.STATUS_ARCHIVED);
    }

    @Test
    public void findArchivedSortByModifiedAtDesc() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductList.STATUS_ARCHIVED, SORT_LISTS_BY_MODIFIED_AT);

        verify(mProductListDao).findStatusSortByModifiedAtDesc(ProductList.STATUS_ARCHIVED);
    }

    @Test
    public void findAllLists() throws InterruptedException, ShoppingListException {

        mSubject.getAllLists();

        verify(mProductListDao).findAllSortByModifiedAtDesc();
    }
}
