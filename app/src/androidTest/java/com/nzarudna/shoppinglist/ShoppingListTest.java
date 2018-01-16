package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.nzarudna.shoppinglist.model.Product;
import com.nzarudna.shoppinglist.model.ProductsList;
import com.nzarudna.shoppinglist.model.ShoppingList;
import com.nzarudna.shoppinglist.model.dao.DaoFactory;
import com.nzarudna.shoppinglist.model.dao.ProductsListDao;
import com.nzarudna.shoppinglist.model.db.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        mProductsListDao = DaoFactory.getInstance().getProductsListDao(mMockContext);
        AppDatabase.switchToInMemory(mMockContext);

        mShoppingList = ShoppingList.createList(mMockContext);
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

    @After
    public void cleanUp() {
        AppDatabase.getInstance(mMockContext).close();
    }




}
