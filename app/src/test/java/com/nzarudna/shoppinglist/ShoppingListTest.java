package com.nzarudna.shoppinglist;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductListDao;
import com.nzarudna.shoppinglist.product.Product;
import com.nzarudna.shoppinglist.product.ProductList;
import com.nzarudna.shoppinglist.product.ProductTemplate;
import com.nzarudna.shoppinglist.product.ProductTemplateRepository;
import com.nzarudna.shoppinglist.product.ShoppingList;
import com.nzarudna.shoppinglist.product.ShoppingListRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
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
    private ProductTemplateRepository mProductTemplateRepository;
    @Mock
    private ShoppingListRepository mShoppingListRepository;

    private ShoppingList mSubject;

    @Before
    public void setUp() {

        mSubject = new ShoppingList(MOCKED_PRODUCTS_LIST_ID);
        mSubject.mProductListDao = mProductListDao;
        mSubject.mProductDao = mProductDao;
        mSubject.mProductTemplateRepository = mProductTemplateRepository;
    }

    @Test
    public void updateList() {

        ProductList updatedList = new ProductList();
        mSubject.updateProductList(updatedList);

        verify(mProductListDao).update(updatedList);
    }

    @Test
    public void addProductToList_withNotCustomSorting() {

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

        verify(mProductDao).insert(expectedProduct);
        verify(mProductTemplateRepository).createTemplateFromProduct(expectedProduct);
    }

    @Test
    public void addProductToList_withCustomSorting() {

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

        verify(mProductDao).insert(expectedProduct);
        verify(mProductTemplateRepository).createTemplateFromProduct(expectedProduct);
    }

    @Test
    public void addProductFromTemplate_withNotCustomSorting() {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_LISTS_BY_CREATED_AT);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        ProductTemplate template = new ProductTemplate();
        template.setTemplateID(10);
        template.setCategoryID(20);
        template.setName("Template name");

        mSubject.addProductFromTemplate(template);

        Product expectedProduct = new Product();
        expectedProduct.setName("Template name");
        expectedProduct.setListID(MOCKED_PRODUCTS_LIST_ID);
        expectedProduct.setCategoryID(20);
        expectedProduct.setTemplateID(10);

        verify(mProductDao).insert(expectedProduct);
        verify(mProductTemplateRepository, never()).createTemplateFromProduct(expectedProduct);
    }

    @Test
    public void addProductFromTemplate_withCustomSorting() {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_LISTS_BY_PRODUCT_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        when(mProductDao.getMaxProductOrderByListID(MOCKED_PRODUCTS_LIST_ID)).thenReturn(2.);

        ProductTemplate template = new ProductTemplate();
        template.setTemplateID(10);
        template.setCategoryID(20);
        template.setName("Template name");

        mSubject.addProductFromTemplate(template);

        Product expectedProduct = new Product();
        expectedProduct.setName("Template name");
        expectedProduct.setListID(MOCKED_PRODUCTS_LIST_ID);
        expectedProduct.setCategoryID(20);
        expectedProduct.setTemplateID(10);
        expectedProduct.setOrder(12);

        verify(mProductDao).insert(expectedProduct);
        verify(mProductTemplateRepository, never()).createTemplateFromProduct(expectedProduct);
    }

    @Test
    public void updateProduct() {

        int productID = 88;
        Product product = new Product();
        product.setName("Some name");
        product.setProductID(productID);
        product.setTemplateID(4);
        when(mProductDao.findByIDSync(productID)).thenReturn(product);

        mSubject.updateProduct(product);

        verify(mProductDao).update(product);
    }

    @Test
    public void updateProductName_testClearTemplateID() {

        int productID = 88;

        Product oldProduct = new Product();
        oldProduct.setProductID(productID);
        oldProduct.setName("Old name");
        oldProduct.setTemplateID(5);
        when(mProductDao.findByIDSync(productID)).thenReturn(oldProduct);

        Product productWithNewName = new Product();
        productWithNewName.setProductID(productID);
        productWithNewName.setName("New name");
        productWithNewName.setTemplateID(5);

        mSubject.updateProduct(productWithNewName);

        Product expectedProduct = new Product();
        expectedProduct.setProductID(productID);
        expectedProduct.setName("New name");
        expectedProduct.setTemplateID(0);

        verify(mProductDao).update(expectedProduct);
    }

    @Test
    public void updateCategoryID_testClearTemplateID() {

        int productID = 88;

        Product oldProduct = new Product();
        oldProduct.setName("Some name");
        oldProduct.setProductID(productID);
        oldProduct.setCategoryID(2);
        oldProduct.setTemplateID(5);
        when(mProductDao.findByIDSync(productID)).thenReturn(oldProduct);

        Product productWithNewName = new Product();
        productWithNewName.setName("Some name");
        productWithNewName.setProductID(productID);
        productWithNewName.setCategoryID(8);
        productWithNewName.setTemplateID(5);

        mSubject.updateProduct(productWithNewName);

        Product expectedProduct = new Product();
        expectedProduct.setName("Some name");
        expectedProduct.setProductID(productID);
        expectedProduct.setCategoryID(8);
        expectedProduct.setTemplateID(0);

        verify(mProductDao).update(expectedProduct);
    }

    @Test
    public void moveProduct_insideList_WithCustomOrder() {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_LISTS_BY_PRODUCT_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setOrder(4.1);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setOrder(2);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 3");
        product3.setOrder(8.7);
        products.add(product3);

        //when(mProductDao.findByListIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(products);

        mSubject.moveProduct(product3, product2, product1);

        product3.setOrder((8.7 - 4.1) / 2);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListStart_WithCustomOrder() {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_LISTS_BY_PRODUCT_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setOrder(4.1);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setOrder(2);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 3");
        product3.setOrder(8.7);
        products.add(product3);

        //when(mProductDao.findByListIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(products);

        mSubject.moveProduct(product1, product2, null);

        product1.setOrder(2 - 10);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListEnd_WithCustomOrder() {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_LISTS_BY_PRODUCT_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setOrder(4.1);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setOrder(2);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 3");
        product3.setOrder(8.7);
        products.add(product3);

        //when(mProductDao.findByListIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(products);

        mSubject.moveProduct(product2, null, product3);

        product2.setOrder(8.7 + 10);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_insideList_WithOrderByName() {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_LISTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 2");
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 1");
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        products.add(product4);

        when(mProductDao.findByListIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(products);

        mSubject.moveProduct(product3, product2, product1);

        


        verify(mProductDao).update(product3);
    }
}
