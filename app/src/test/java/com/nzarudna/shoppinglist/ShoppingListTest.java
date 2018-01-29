package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.persistence.ProductDao;
import com.nzarudna.shoppinglist.persistence.ProductListDao;
import com.nzarudna.shoppinglist.product.Product;
import com.nzarudna.shoppinglist.product.ProductList;
import com.nzarudna.shoppinglist.product.ProductTemplate;
import com.nzarudna.shoppinglist.product.ProductTemplateRepository;
import com.nzarudna.shoppinglist.product.ShoppingList;
import com.nzarudna.shoppinglist.product.ShoppingListException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

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
    private ProductListDao mProductListDao;
    @Mock
    private ProductDao mProductDao;
    @Mock
    private ProductTemplateRepository mProductTemplateRepository;

    private ShoppingList mSubject;

    @Before
    public void setUp() {

        mSubject = new ShoppingList(MOCKED_PRODUCTS_LIST_ID, mProductListDao, mProductDao,
                mProductTemplateRepository);
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
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
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
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
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
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
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
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
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
    public void updateUniID_testClearTemplateID() {

        int productID = 88;

        Product oldProduct = new Product();
        oldProduct.setName("Some name");
        oldProduct.setProductID(productID);
        oldProduct.setUnitID(2);
        oldProduct.setTemplateID(5);
        when(mProductDao.findByIDSync(productID)).thenReturn(oldProduct);

        Product productWithNewName = new Product();
        productWithNewName.setName("Some name");
        productWithNewName.setProductID(productID);
        productWithNewName.setUnitID(8);
        productWithNewName.setTemplateID(5);

        mSubject.updateProduct(productWithNewName);

        Product expectedProduct = new Product();
        expectedProduct.setName("Some name");
        expectedProduct.setProductID(productID);
        expectedProduct.setUnitID(8);
        expectedProduct.setTemplateID(0);

        verify(mProductDao).update(expectedProduct);
    }

    @Test
    public void moveProduct_insideList_WithCustomOrder() throws ShoppingListException {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
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

        mSubject.moveProduct(product3, product2, product1);

        product3.setOrder((8.7 - 4.1) / 2);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListStart_WithCustomOrder() throws ShoppingListException {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
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

        mSubject.moveProduct(product1, product2, null);

        product1.setOrder(2 - 10);

        verify(mProductDao).update(product1);
    }

    @Test
    public void moveProduct_toListEnd_WithCustomOrder() throws ShoppingListException {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
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

        mSubject.moveProduct(product2, null, product3);

        product2.setOrder(8.7 + 10);

        verify(mProductDao).update(product2);
    }

    @Test
    public void moveProduct_insideList_WithOrderByName() throws ShoppingListException {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
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

        mSubject.moveProduct(product3, product2, product1);

        product3.setOrder(5);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListStart_WithOrderByName() throws ShoppingListException {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
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

        mSubject.moveProduct(product3, null, product2);

        product3.setOrder(-10);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListEnd_WithOrderByName() throws ShoppingListException {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
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

        mSubject.moveProduct(product1, product3, null);

        product1.setOrder(40);

        verify(mProductDao).update(product1);
    }

    @Test
    public void moveProduct_insideList_WithOrderByStatus() throws ShoppingListException {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_STATUS);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setStatus(Product.ABSENT);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product1.setStatus(Product.TO_BUY);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product1.setStatus(Product.ABSENT);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product1.setStatus(Product.BOUGHT);
        products.add(product4);

        mSubject.moveProduct(product3, product2, product1);

        // list order: 2 1 3 4
        product3.setOrder(5);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListStart_WithOrderByStatus() throws ShoppingListException {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_STATUS);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setStatus(Product.ABSENT);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product1.setStatus(Product.TO_BUY);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product1.setStatus(Product.ABSENT);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product1.setStatus(Product.BOUGHT);
        products.add(product4);

        mSubject.moveProduct(product3, null, product2);

        product3.setOrder(-10);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListEnd_WithOrderByStatus() throws ShoppingListException {

        ProductList productList = new ProductList();
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_STATUS);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setStatus(Product.ABSENT);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product1.setStatus(Product.TO_BUY);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product1.setStatus(Product.ABSENT);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product1.setStatus(Product.BOUGHT);
        products.add(product4);

        mSubject.moveProduct(product1, product3, null);

        product1.setOrder(40);

        verify(mProductDao).update(product1);
    }

    @Test(expected = ShoppingListException.class)
    public void moveProduct_exceptionOnNullBeforeAndAfterProducts() throws ShoppingListException {
        mSubject.moveProduct(new Product(), null, null);
    }

    @Test
    public void removeProduct() {

        Product product = new Product();

        mSubject.removeProduct(product);

        verify(mProductDao).delete(product);
    }

    @Test
    public void findProductsByListID_sortByName() throws ShoppingListException {

        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_NAME, false);
        verify(mProductDao).findByListIDSortByName(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByStatus() throws ShoppingListException {

        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_STATUS, false);
        verify(mProductDao).findByListIDSortByStatusAndName(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByOrder() throws ShoppingListException {

        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_ORDER, false);
        verify(mProductDao).findByListIDSortByProductOrder(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByName_groupeView() throws ShoppingListException {

        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_NAME, true);
        verify(mProductDao).findByListIDSortByNameWithCategory(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByStatus_groupeView() throws ShoppingListException {

        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_STATUS, true);
        verify(mProductDao).findByListIDSortByStatusAndNameWithCategory(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByOrder_groupeView() throws ShoppingListException {

        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_ORDER, true);
        verify(mProductDao).findByListIDSortByProductOrderWithCategory(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test(expected = ShoppingListException.class)
    public void findProductsByListID_exceptionOnUnknownSorting() throws ShoppingListException {
        mSubject.getProducts(99, false);
    }

    @Test(expected = ShoppingListException.class)
    public void findProductsByListID_exceptionOnUnknownSorting_groupeView() throws ShoppingListException {
        mSubject.getProducts(99, true);
    }
}
