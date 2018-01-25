package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.nzarudna.shoppinglist.persistence.ProductListDao;
import com.nzarudna.shoppinglist.product.Product;
import com.nzarudna.shoppinglist.product.ProductList;
import com.nzarudna.shoppinglist.product.ShoppingList;
import com.nzarudna.shoppinglist.product.ShoppingListRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Test methods on shopping list object
 */
@RunWith(MockitoJUnitRunner.class)
public class ShoppingListTest {

    private static final int MOCKED_PRODUCTS_LIST_ID = 3;

    @Mock
    private Context mMockContext;
    @Mock
    private ProductListDao mProductListDao;
    @Mock
    private ShoppingListRepository mShoppingListRepository;

    @Mock
    private LiveData<ProductList> productsListLiveData;
    private ShoppingList mSubject;

    @Before
    public void setUp() {

        mSubject = new ShoppingList(mProductListDao, MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void updateList() {

        ProductList updatedList = new ProductList();
        mSubject.updateProductList(updatedList);

        verify(mProductListDao).update(updatedList);
    }

    @Test
    public void addProductToListWith_NotCustomSorting() throws InterruptedException {

        

        Product product = new Product();
        product.setName("Some product name");
        mSubject.addProduct(product);

        Product expectedProduct = new Product();
        expectedProduct.setListID(mSubject.getListID());
        expectedProduct.setName("Some product name");
        expectedProduct.setOrder(0);

        assertEquals(product.getName(), productName);
        assertEquals(product.getComment(), productComment);
        assertEquals(product.getListID(), mShoppingList.getListID());
        assertEquals(product.getStatus(), Product.TO_BUY);
    }
/*
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
*/




}
