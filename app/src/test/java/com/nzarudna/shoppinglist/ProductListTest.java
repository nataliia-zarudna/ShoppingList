package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.product.ProductDao;
import com.nzarudna.shoppinglist.model.product.list.ProductListDao;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.exception.ShoppingListException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test methods on shopping list object
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductListTest {

    private static final UUID MOCKED_PRODUCTS_LIST_ID = UUID.randomUUID();

    @Mock
    private ProductListDao mProductListDao;
    @Mock
    private ProductDao mProductDao;
    @Mock
    private ProductTemplateRepository mProductTemplateRepository;
    private UUID mMockedCategoryID;
    private UUID mMockedTemplateID;

    private ShoppingList mSubject;

    @Before
    public void setUp() {

        mSubject = new ShoppingList(MOCKED_PRODUCTS_LIST_ID, mProductListDao, mProductDao,
                mProductTemplateRepository);

        mMockedCategoryID = UUID.randomUUID();
        mMockedCategoryID = UUID.randomUUID();
    }

    @Test
    public void updateList() {

        ProductList updatedList = new ProductList("Some name", UUID.randomUUID());
        mSubject.updateProductList(updatedList);

        verify(mProductListDao).update(updatedList);
    }

    @Test
    public void addProductToList_withNotCustomSorting() {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        String productName = "Some product name";
        Product product = new Product(productName);

        mSubject.addProduct(product);

        Product expectedProduct = new Product(productName);
        expectedProduct.setListID(mSubject.getListID());

        verify(mProductDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
        verify(mProductTemplateRepository).createTemplateFromProduct(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
    }

    @Test
    public void addProductToList_withCustomSorting() {

        ProductList productList = new ProductList("Some list", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        when(mProductDao.getMaxProductOrderByListID(MOCKED_PRODUCTS_LIST_ID)).thenReturn(7.81);

        Product product = new Product("Some product name");

        mSubject.addProduct(product);

        Product expectedProduct = new Product("Some product name");
        expectedProduct.setListID(mSubject.getListID());
        expectedProduct.setOrder(17.81);

        verify(mProductDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
        verify(mProductTemplateRepository).createTemplateFromProduct(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
    }

    @Test
    public void addProductFromTemplate_withNotCustomSorting() {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        String mockedName = "Template name";

        ProductTemplate template = new ProductTemplate(mockedName);
        template.setTemplateID(mMockedTemplateID);
        template.setCategoryID(mMockedCategoryID);

        mSubject.addProductFromTemplate(template);

        Product expectedProduct = new Product(mockedName);
        expectedProduct.setListID(MOCKED_PRODUCTS_LIST_ID);
        expectedProduct.setCategoryID(mMockedCategoryID);
        expectedProduct.setTemplateID(mMockedTemplateID);

        verify(mProductDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
        verify(mProductTemplateRepository, never()).createTemplateFromProduct(expectedProduct);
    }

    @Test
    public void addProductFromTemplate_withCustomSorting() {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        when(mProductDao.getMaxProductOrderByListID(MOCKED_PRODUCTS_LIST_ID)).thenReturn(2.);

        String mockedName = "Template name";

        ProductTemplate template = new ProductTemplate(mockedName);
        template.setTemplateID(mMockedTemplateID);
        template.setCategoryID(mMockedCategoryID);

        mSubject.addProductFromTemplate(template);

        Product expectedProduct = new Product(mockedName);
        expectedProduct.setListID(MOCKED_PRODUCTS_LIST_ID);
        expectedProduct.setCategoryID(mMockedCategoryID);
        expectedProduct.setTemplateID(mMockedTemplateID);
        expectedProduct.setOrder(12);

        verify(mProductDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
        verify(mProductTemplateRepository, never()).createTemplateFromProduct(expectedProduct);
    }

    @Test
    public void updateProduct() {

        UUID productID = UUID.randomUUID();
        Product product = new Product("Some name");
        product.setProductID(productID);
        product.setTemplateID(UUID.randomUUID());
        when(mProductDao.findByIDSync(productID)).thenReturn(product);

        mSubject.updateProduct(product);

        verify(mProductDao).update(product);
    }

    @Test
    public void updateProductName_testClearTemplateID() {

        UUID productID = UUID.randomUUID();
        UUID mockedTemplateID = UUID.randomUUID();

        Product oldProduct = new Product("Old name");
        oldProduct.setProductID(productID);
        oldProduct.setTemplateID(mockedTemplateID);
        when(mProductDao.findByIDSync(productID)).thenReturn(oldProduct);

        Product productWithNewName = new Product("New name");
        productWithNewName.setProductID(productID);
        productWithNewName.setTemplateID(mockedTemplateID);

        mSubject.updateProduct(productWithNewName);

        Product expectedProduct = new Product("New name");
        expectedProduct.setProductID(productID);
        expectedProduct.setTemplateID(null);

        verify(mProductDao).update(expectedProduct);
    }

    @Test
    public void updateCategoryID_testClearTemplateID() {

        UUID productID = UUID.randomUUID();

        String mockedName = "Template name";

        Product oldProduct = new Product("Some name");
        oldProduct.setProductID(productID);
        oldProduct.setCategoryID(mMockedCategoryID);
        oldProduct.setTemplateID(mMockedTemplateID);
        when(mProductDao.findByIDSync(productID)).thenReturn(oldProduct);

        Product productWithNewName = new Product("Some name");
        productWithNewName.setProductID(productID);
        productWithNewName.setCategoryID(mMockedCategoryID);
        productWithNewName.setTemplateID(mMockedTemplateID);

        mSubject.updateProduct(productWithNewName);

        Product expectedProduct = new Product("Some name");
        expectedProduct.setProductID(productID);
        expectedProduct.setCategoryID(mMockedCategoryID);
        expectedProduct.setTemplateID(null);

        verify(mProductDao).update(expectedProduct);
    }

    @Test
    public void updateUniID_testClearTemplateID() {

        UUID productID = UUID.randomUUID();
        String name = "Some name";
        UUID mockedUnitID = UUID.randomUUID();

        Product oldProduct = new Product(name);
        oldProduct.setProductID(productID);
        oldProduct.setUnitID(UUID.randomUUID());
        oldProduct.setTemplateID(mMockedTemplateID);
        when(mProductDao.findByIDSync(productID)).thenReturn(oldProduct);

        Product productWithNewName = new Product("Some name");
        productWithNewName.setProductID(productID);
        productWithNewName.setUnitID(mockedUnitID);
        productWithNewName.setTemplateID(mMockedTemplateID);

        mSubject.updateProduct(productWithNewName);

        Product expectedProduct = new Product("Some name");
        expectedProduct.setProductID(productID);
        expectedProduct.setUnitID(mockedUnitID);
        expectedProduct.setTemplateID(null);

        verify(mProductDao).update(expectedProduct);
    }

    @Test
    public void moveProduct_insideList_WithCustomOrder() throws ShoppingListException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product("Product 1");
        product1.setOrder(4.1);
        products.add(product1);

        Product product2 = new Product("Product 2");
        product2.setOrder(2);
        products.add(product2);

        Product product3 = new Product("Product 3");
        product3.setOrder(8.7);
        products.add(product3);

        mSubject.moveProduct(product3, product2, product1);

        product3.setOrder((8.7 - 4.1) / 2);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListStart_WithCustomOrder() throws ShoppingListException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product("Product 1");
        product1.setOrder(4.1);
        products.add(product1);

        Product product2 = new Product("Product 2");
        product2.setOrder(2);
        products.add(product2);

        Product product3 = new Product("Product 3");
        product3.setOrder(8.7);
        products.add(product3);

        mSubject.moveProduct(product1, product2, null);

        product1.setOrder(2 - 10);

        verify(mProductDao).update(product1);
    }

    @Test
    public void moveProduct_toListEnd_WithCustomOrder() throws ShoppingListException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product("Product 1");
        product1.setOrder(4.1);
        products.add(product1);

        Product product2 = new Product("Product 2");
        product2.setOrder(2);
        products.add(product2);

        Product product3 = new Product("Product 3");
        product3.setOrder(8.7);
        products.add(product3);

        mSubject.moveProduct(product2, null, product3);

        product2.setOrder(8.7 + 10);

        verify(mProductDao).update(product2);
    }

    @Test
    public void moveProduct_insideList_WithOrderByName() throws ShoppingListException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product("Product 2");
        products.add(product1);

        Product product2 = new Product("Product 1");
        products.add(product2);

        Product product3 = new Product("Product 4");
        products.add(product3);

        Product product4 = new Product("Product 3");
        products.add(product4);

        mSubject.moveProduct(product3, product2, product1);

        product3.setOrder(5);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListStart_WithOrderByName() throws ShoppingListException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product("Product 2");
        products.add(product1);

        Product product2 = new Product("Product 1");
        products.add(product2);

        Product product3 = new Product("Product 4");
        products.add(product3);

        Product product4 = new Product("Product 3");
        products.add(product4);

        mSubject.moveProduct(product3, null, product2);

        product3.setOrder(-10);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListEnd_WithOrderByName() throws ShoppingListException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product("Product 2");
        products.add(product1);

        Product product2 = new Product("Product 1");
        products.add(product2);

        Product product3 = new Product("Product 4");
        products.add(product3);

        Product product4 = new Product("Product 3");
        products.add(product4);

        mSubject.moveProduct(product1, product3, null);

        product1.setOrder(40);

        verify(mProductDao).update(product1);
    }

    @Test
    public void moveProduct_insideList_WithOrderByStatus() throws ShoppingListException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_STATUS);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product("Product 2");
        product1.setStatus(Product.ABSENT);
        products.add(product1);

        Product product2 = new Product("Product 2");
        product1.setStatus(Product.TO_BUY);
        products.add(product2);

        Product product3 = new Product("Product 4");
        product1.setStatus(Product.ABSENT);
        products.add(product3);

        Product product4 = new Product("Product 3");
        product1.setStatus(Product.BOUGHT);
        products.add(product4);

        mSubject.moveProduct(product3, product2, product1);

        // list order: 2 1 3 4
        product3.setOrder(5);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListStart_WithOrderByStatus() throws ShoppingListException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_STATUS);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product("Product 2");
        product1.setStatus(Product.ABSENT);
        products.add(product1);

        Product product2 = new Product("Product 2");
        product1.setStatus(Product.TO_BUY);
        products.add(product2);

        Product product3 = new Product("Product 4");
        product1.setStatus(Product.ABSENT);
        products.add(product3);

        Product product4 = new Product("Product 3");
        product1.setStatus(Product.BOUGHT);
        products.add(product4);

        mSubject.moveProduct(product3, null, product2);

        product3.setOrder(-10);

        verify(mProductDao).update(product3);
    }

    @Test
    public void moveProduct_toListEnd_WithOrderByStatus() throws ShoppingListException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_STATUS);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product("Product 2");
        product1.setStatus(Product.ABSENT);
        products.add(product1);

        Product product2 = new Product("Product 2");
        product1.setStatus(Product.TO_BUY);
        products.add(product2);

        Product product3 = new Product("Product 4");
        product1.setStatus(Product.ABSENT);
        products.add(product3);

        Product product4 = new Product("Product 3");
        product1.setStatus(Product.BOUGHT);
        products.add(product4);

        mSubject.moveProduct(product1, product3, null);

        product1.setOrder(40);

        verify(mProductDao).update(product1);
    }

    @Test(expected = ShoppingListException.class)
    public void moveProduct_exceptionOnNullBeforeAndAfterProducts() throws ShoppingListException {
        mSubject.moveProduct(new Product("Some name"), null, null);
    }

    @Test
    public void removeProduct() {

        Product product = new Product("Some name");

        mSubject.removeProduct(product);

        verify(mProductDao).delete(product);
    }

    @Test
    public void removeProductsWithTemplate() {

        UUID templateID = UUID.randomUUID();

        mSubject.removeProductsWithTemplate(templateID);

        verify(mProductDao).delete(templateID, MOCKED_PRODUCTS_LIST_ID);
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
