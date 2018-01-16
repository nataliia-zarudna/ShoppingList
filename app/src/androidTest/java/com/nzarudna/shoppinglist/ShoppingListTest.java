package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.nzarudna.shoppinglist.model.Product;
import com.nzarudna.shoppinglist.model.ProductsList;
import com.nzarudna.shoppinglist.model.ShoppingList;
import com.nzarudna.shoppinglist.model.ShoppingListException;
import com.nzarudna.shoppinglist.model.dao.ProductsListDao;
import com.nzarudna.shoppinglist.model.db.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test methods on shopping list object
 */
@RunWith(MockitoJUnitRunner.class)
public class ShoppingListTest {

    private Context mMockContext;
    private ProductsListDao mProductsListDao;
    private ShoppingList mShoppingList;

    @Before
    public void setUp() {

        mMockContext = Mockito.mock(Context.class);
       // mProductsListDao = DaoFactory.getInstance().getProductsListDao(mMockContext);
        AppDatabase.switchToInMemory(mMockContext);

        //mShoppingList = ShoppingList.createList(mMockContext);
    }

    @Test
    public void removeList() {

        int listID = mShoppingList.getListID();
        mShoppingList.removeList();

        ProductsList removedList = mProductsListDao.findByIDSync(listID);

        assertNull(removedList);
    }

    @Test
    public void addProductWithComment() throws InterruptedException {

        String productName = "Some product";
        String productComment = "Something about product";
        LiveData<Product> productLiveData = mShoppingList.addProduct(productName, productComment);

        Product product = TestUtils.getLiveDataValueSync(productLiveData);

        assertEquals(product.getName(), productName);
        assertEquals(product.getComment(), productComment);
        assertEquals(product.getListID(), mShoppingList.getListID());
        assertEquals(product.getStatus(), Product.TO_BUY);
    }

    @Test(expected = ShoppingListException.class)
    public void exception_onAddProductWithComment_AndEmptyName() throws InterruptedException {

        String emptyName = "";
        String productComment = "Something about product";
        LiveData<Product> productLiveData = mShoppingList.addProduct(emptyName, productComment);

        TestUtils.getLiveDataValueSync(productLiveData);
    }

    @Test
    public void addProductWithUnits() throws InterruptedException {

        String productName = "Some product";
        double productCount = 5.5;
        int productUnitID = 1;
        LiveData<Product> productLiveData = mShoppingList.addProduct(productName, productCount, productUnitID);

        Product product = TestUtils.getLiveDataValueSync(productLiveData);

        assertEquals(product.getName(), productName);
        assertTrue(Math.abs(product.getCount() - productCount) < 0.001);
        assertEquals(product.getUnitID(), productUnitID);
        assertEquals(product.getListID(), mShoppingList.getListID());
        assertEquals(product.getStatus(), Product.TO_BUY);
    }

    @After
    public void cleanUp() {
        AppDatabase.getInstance(mMockContext).close();
    }




}
