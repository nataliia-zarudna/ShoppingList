package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductListDao;
import com.nzarudna.shoppinglist.product.Product;
import com.nzarudna.shoppinglist.product.ProductList;
import com.nzarudna.shoppinglist.product.ProductTemplate;
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
import static org.mockito.Mockito.when;

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
    private ProductDao mProductDao;
    @Mock
    private ShoppingListRepository mShoppingListRepository;

    private ShoppingList mSubject;

    @Before
    public void setUp() {

        mSubject = new ShoppingList(MOCKED_PRODUCTS_LIST_ID);
        mSubject.mProductListDao = mProductListDao;
        mSubject.mProductDao = mProductDao;
    }

    @Test
    public void updateList() {

        ProductList updatedList = new ProductList();
        mSubject.updateProductList(updatedList);

        verify(mProductListDao).update(updatedList);
    }

    @Test
    public void addProductToListWith_NotCustomSorting() {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_LISTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        Product product = new Product();
        product.setName("Some product name");

        mSubject.addProduct(product);

        Product expectedProduct = new Product();
        expectedProduct.setListID(mSubject.getListID());
        expectedProduct.setName("Some product name");

        assertEquals(product, expectedProduct);
    }

    @Test
    public void addProductToListWith_CustomSorting() {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_LISTS_BY_PRODUCT_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        when(mProductDao.getMaxProductOrderByListID(MOCKED_PRODUCTS_LIST_ID)).thenReturn(7.81);

        Product product = new Product();
        product.setName("Some product name");

        mSubject.addProduct(product);

        Product expectedProduct = new Product();
        expectedProduct.setListID(mSubject.getListID());
        expectedProduct.setName("Some product name");
        expectedProduct.setOrder(17.81);

        assertEquals(product, expectedProduct);
    }

    /*@Test
    public void addProductFromTemplate() {

        ProductTemplate template = new ProductTemplate();
        template.setTemplateID(10);
        template.setCategoryID(20);
        template.setName("Template name");
        when()


    }*/
}
