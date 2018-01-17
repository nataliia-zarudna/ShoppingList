package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductsListDao;
import com.nzarudna.shoppinglist.product.Product;
import com.nzarudna.shoppinglist.product.ProductsList;
import com.nzarudna.shoppinglist.product.ShoppingList;
import com.nzarudna.shoppinglist.product.ShoppingListException;
import com.nzarudna.shoppinglist.product.ShoppingListRepository;
import com.nzarudna.shoppinglist.user.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test Shopping List
 */
@RunWith(MockitoJUnitRunner.class)
public class ShoppingListRepositoryTest {

    private static final String MOCKED_DEFAULT_LIST_NAME = "Default List Name";
    private static final int MOCKED_SELF_USER_ID = 2;

    private ShoppingListRepository mSubject;

    @Mock
    private UserRepository mUserRepository;
    @Mock
    private ResourceResolver mResourceResolver;

    @Mock
    private ProductsListDao mProductsListDao;
    @Mock
    private ProductDao mProductDao;

    @Before
    public void setUp() {

        when(mResourceResolver.getString(R.string.default_list_name)).thenReturn(MOCKED_DEFAULT_LIST_NAME);
        when(mUserRepository.getSelfUserID()).thenReturn(MOCKED_SELF_USER_ID);

        mSubject = new ShoppingListRepository(mProductsListDao, mProductDao, mUserRepository, mResourceResolver);
    }

    @Test
    public void createList() throws InterruptedException {

        mSubject.createList();

        ProductsList expectedProductsList = new ProductsList();
        expectedProductsList.setName(MOCKED_DEFAULT_LIST_NAME);
        expectedProductsList.setCreatedBy(MOCKED_SELF_USER_ID);

        verify(mProductsListDao).insert(expectedProductsList);
    }

    @Test
    public void copyList_testEqualsData() throws InterruptedException, ShoppingListException {

        int etalonListID = 2;
        ProductsList etalonList = new ProductsList();
        etalonList.setListID(etalonListID);
        etalonList.setName("Some list name");
        etalonList.setStatus(ProductsList.STATUS_ARCHIVED);

        when(mProductsListDao.findByIDSync(etalonListID)).thenReturn(etalonList);

        mSubject.copyList(etalonListID);

        ProductsList expectedList = new ProductsList();
        expectedList.setName(etalonList.getName());
        expectedList.setCreatedAt(new Date());
        expectedList.setCreatedBy(MOCKED_SELF_USER_ID);
        expectedList.setStatus(ProductsList.STATUS_ACTIVE);

        verify(mProductsListDao).insert(expectedList);
    }

    @Test
    public void copyList_testEqualsProducts() throws InterruptedException, ShoppingListException {

        int etalonListID = 2;
        ProductsList etalonList = new ProductsList();
        when(mProductsListDao.findByIDSync(etalonListID)).thenReturn(etalonList);

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

        mSubject.copyList(etalonListID);

        List<Product> expectedProducts = etalonProducts;
        for (Product expectedProduct : expectedProducts) {
            expectedProduct.setStatus(Product.TO_BUY);

            verify(mProductDao).insert(expectedProduct);
        }
    }

    @Test(expected = ShoppingListException.class)
    public void copyList_testException_OnNonexistentList() throws InterruptedException, ShoppingListException {

        mSubject.copyList( 99);
    }

    @Test
    public void findActiveSortByName() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductsList.STATUS_ACTIVE, ShoppingList.SORT_LISTS_BY_NAME);

        verify(mProductsListDao).findByStatusSortByName(ProductsList.STATUS_ACTIVE);
    }

    @Test
    public void findActiveSortByCreatedAtDesc() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductsList.STATUS_ACTIVE, ShoppingList.SORT_LISTS_BY_CREATED_AT);

        verify(mProductsListDao).findByStatusSortByCreatedAtDesc(ProductsList.STATUS_ACTIVE);
    }

    @Test
    public void findActiveSortByCreatedByAndName() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductsList.STATUS_ACTIVE, ShoppingList.SORT_LISTS_BY_CREATED_BY);

        verify(mProductsListDao).findByStatusSortByCreatedByAndName(ProductsList.STATUS_ACTIVE);
    }

    @Test
    public void findActiveSortByModifiedAtDesc() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductsList.STATUS_ACTIVE, ShoppingList.SORT_LISTS_BY_MODIFIED_AT);

        verify(mProductsListDao).findByStatusSortByModifiedAtDesc(ProductsList.STATUS_ACTIVE);
    }

    @Test
    public void findArchivedSortByModifiedAtDesc() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductsList.STATUS_ARCHIVED, ShoppingList.SORT_LISTS_BY_MODIFIED_AT);

        verify(mProductsListDao).findByStatusSortByModifiedAtDesc(ProductsList.STATUS_ARCHIVED);
    }

    @Test
    public void findActiveSortByAssignedAndName() throws InterruptedException, ShoppingListException {

        mSubject.getLists(ProductsList.STATUS_ACTIVE, ShoppingList.SORT_LISTS_BY_ASSIGNED);

        verify(mProductsListDao).findByStatusSortByAssignedAndName(ProductsList.STATUS_ACTIVE);
    }
}
