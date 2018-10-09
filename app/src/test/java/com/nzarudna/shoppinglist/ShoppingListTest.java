package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.exception.NameIsEmptyException;
import com.nzarudna.shoppinglist.model.exception.UniqueNameConstraintException;
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
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test methods on shopping list object
 */
@RunWith(MockitoJUnitRunner.class)
public class ShoppingListTest {

    private static final UUID MOCKED_PRODUCTS_LIST_ID = UUID.randomUUID();

    @Mock
    private ProductListDao mProductListDao;
    @Mock
    private ProductDao mProductDao;
    @Mock
    private ProductTemplateRepository mProductTemplateRepository;
    private UUID mMockedCategoryID;
    private UUID mMockedTemplateID;
    private ProductList mMockedProductList;

    private ShoppingList mSubject;

    @Before
    public void setUp() {

        mSubject = new ShoppingList(MOCKED_PRODUCTS_LIST_ID, mProductListDao, mProductDao,
                mProductTemplateRepository);

        mMockedCategoryID = UUID.randomUUID();
        mMockedCategoryID = UUID.randomUUID();
        mMockedProductList = Mockito.mock(ProductList.class);
    }

    @Test
    public void updateList() {

        ProductList updatedList = new ProductList("Some name", UUID.randomUUID());
        mSubject.updateProductList(updatedList);

        verify(mProductListDao).update(updatedList);
    }

    @Test
    public void addProductToList_withNotCustomSorting() throws InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        String productName = "Some product name";
        Product product = new Product();
        product.setName(productName);

        final Product expectedProduct = new Product();
        expectedProduct.setName(productName);
        expectedProduct.setListID(mSubject.getListID());

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.addProduct(product, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product resultProduct) {
                assertTrue(AssertUtils.matchesExceptID(resultProduct, expectedProduct));
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        verify(mProductDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
        verify(mProductTemplateRepository).createFromProductAsync(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)),
                any(AsyncResultListener.class));
    }

    @Test
    public void create_nullNameError() throws InterruptedException {

        final Product newProduct = new Product();

        final CountDownLatch countDown = new CountDownLatch(1);
        mSubject.addProduct(newProduct, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product product) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof NameIsEmptyException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void create_emptyNameError() throws InterruptedException {

        final Product newProduct = new Product();
        newProduct.setName("   ");

        final CountDownLatch countDown = new CountDownLatch(1);
        mSubject.addProduct(newProduct, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product product) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof NameIsEmptyException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void create_duplicateNameError() throws InterruptedException {

        String name = "some name";
        when(mProductDao.isProductsWithSameNameAndListExists(name, MOCKED_PRODUCTS_LIST_ID)).thenReturn(true);

        final Product newProduct = new Product();
        newProduct.setName(name);

        final CountDownLatch countDown = new CountDownLatch(1);
        mSubject.addProduct(newProduct, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product product) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof UniqueNameConstraintException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void addProductToList_withCustomSorting() throws InterruptedException {

        ProductList productList = new ProductList("Some list", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        when(mProductDao.getMaxProductOrderByListID(MOCKED_PRODUCTS_LIST_ID)).thenReturn(7.81);

        Product product = new Product();
        product.setName("Some product name");

        final Product expectedProduct = new Product();
        expectedProduct.setName(product.getName());
        expectedProduct.setListID(mSubject.getListID());
        expectedProduct.setOrder(17.81);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.addProduct(product, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product resultProduct) {
                assertTrue(AssertUtils.matchesExceptID(resultProduct, expectedProduct));
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        verify(mProductDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
        verify(mProductTemplateRepository).createFromProductAsync(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)),
                any(AsyncResultListener.class));
    }

    @Test
    public void addProductFromTemplate_withNotCustomSorting() throws InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        String mockedName = "Template name";

        ProductTemplate template = new ProductTemplate();
        template.setName(mockedName);
        template.setTemplateID(mMockedTemplateID);
        template.setCategoryID(mMockedCategoryID);

        final Product expectedProduct = new Product();
        expectedProduct.setName(mockedName);
        expectedProduct.setListID(MOCKED_PRODUCTS_LIST_ID);
        expectedProduct.setCategoryID(mMockedCategoryID);
        expectedProduct.setTemplateID(mMockedTemplateID);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.addProductFromTemplate(template, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product resultProduct) {
                assertTrue(AssertUtils.matchesExceptID(resultProduct, expectedProduct));
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        verify(mProductDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
        verify(mProductTemplateRepository, never())
                .createFromProductAsync(eq(expectedProduct), any(AsyncResultListener.class));
    }

    @Test
    public void addProductFromTemplate_withCustomSorting() throws InterruptedException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        when(mProductDao.getMaxProductOrderByListID(MOCKED_PRODUCTS_LIST_ID)).thenReturn(2.);

        String mockedName = "Template name";

        ProductTemplate template = new ProductTemplate();
        template.setName(mockedName);
        template.setTemplateID(mMockedTemplateID);
        template.setCategoryID(mMockedCategoryID);

        final Product expectedProduct = new Product();
        expectedProduct.setName(mockedName);
        expectedProduct.setListID(MOCKED_PRODUCTS_LIST_ID);
        expectedProduct.setCategoryID(mMockedCategoryID);
        expectedProduct.setTemplateID(mMockedTemplateID);
        expectedProduct.setOrder(12);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.addProductFromTemplate(template, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product resultProduct) {
                assertTrue(AssertUtils.matchesExceptID(resultProduct, expectedProduct));
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        verify(mProductDao).insert(
                argThat(AssertUtils.getArgumentMatcher(expectedProduct)));
        verify(mProductTemplateRepository, never())
                .createFromProductAsync(eq(expectedProduct), any(AsyncResultListener.class));
    }

    @Test
    public void updateProduct() throws InterruptedException {

        UUID productID = UUID.randomUUID();
        final Product product = new Product();
        product.setName("Some name");
        product.setProductID(productID);
        product.setTemplateID(UUID.randomUUID());
        when(mProductDao.findByIDSync(productID)).thenReturn(product);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.updateProduct(product, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product resultProduct) {
                assertEquals(resultProduct, product);
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        verify(mProductDao).update(product);
    }

    @Test
    public void update_nullNameError() throws InterruptedException {

        final Product newProduct = new Product();

        final CountDownLatch countDown = new CountDownLatch(1);
        mSubject.updateProduct(newProduct, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product product) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof NameIsEmptyException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void update_emptyNameError() throws InterruptedException {

        final Product newProduct = new Product();
        newProduct.setName("   ");

        final CountDownLatch countDown = new CountDownLatch(1);
        mSubject.updateProduct(newProduct, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product product) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof NameIsEmptyException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void update_duplicateNameError() throws InterruptedException {

        String name = "some name";
        when(mProductDao.isProductsWithSameNameAndListExists(name, MOCKED_PRODUCTS_LIST_ID)).thenReturn(true);

        final Product newProduct = new Product();
        newProduct.setName(name);
        when(mProductDao.findByIDSync(newProduct.getProductID())).thenReturn(newProduct);

        final CountDownLatch countDown = new CountDownLatch(1);
        mSubject.updateProduct(newProduct, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product product) {

            }

            @Override
            public void onAsyncError(Exception e) {
                countDown.countDown();

                assertTrue(e instanceof UniqueNameConstraintException);
            }
        });

        countDown.await(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void updateProductName_testClearTemplateID() throws InterruptedException {

        UUID productID = UUID.randomUUID();
        UUID mockedTemplateID = UUID.randomUUID();

        Product oldProduct = new Product();
        oldProduct.setName("Old name");
        oldProduct.setProductID(productID);
        oldProduct.setTemplateID(mockedTemplateID);
        when(mProductDao.findByIDSync(productID)).thenReturn(oldProduct);

        Product productWithNewName = new Product();
        productWithNewName.setName("New name");
        productWithNewName.setProductID(productID);
        productWithNewName.setTemplateID(mockedTemplateID);

        final Product expectedProduct = new Product();
        expectedProduct.setName("New name");
        expectedProduct.setProductID(productID);
        expectedProduct.setTemplateID(null);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.updateProduct(productWithNewName, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product resultProduct) {
                assertEquals(resultProduct, expectedProduct);
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        verify(mProductDao).update(expectedProduct);
    }

    @Test
    public void updateCategoryID_testClearTemplateID() throws InterruptedException {

        UUID productID = UUID.randomUUID();

        String mockedName = "Template name";

        Product oldProduct = new Product();
        oldProduct.setName("Some name");
        oldProduct.setProductID(productID);
        oldProduct.setCategoryID(mMockedCategoryID);
        oldProduct.setTemplateID(mMockedTemplateID);
        when(mProductDao.findByIDSync(productID)).thenReturn(oldProduct);

        Product productWithNewName = new Product();
        productWithNewName.setName("Some name");
        productWithNewName.setProductID(productID);
        productWithNewName.setCategoryID(mMockedCategoryID);
        productWithNewName.setTemplateID(mMockedTemplateID);

        final Product expectedProduct = new Product();
        expectedProduct.setName("Some name");
        expectedProduct.setProductID(productID);
        expectedProduct.setCategoryID(mMockedCategoryID);
        expectedProduct.setTemplateID(null);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.updateProduct(productWithNewName, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product resultProduct) {
                assertEquals(resultProduct, expectedProduct);
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        verify(mProductDao).update(expectedProduct);
    }

    @Test
    public void updateUniID_testClearTemplateID() throws InterruptedException {

        UUID productID = UUID.randomUUID();
        String name = "Some name";
        UUID mockedUnitID = UUID.randomUUID();

        Product oldProduct = new Product();
        oldProduct.setName(name);
        oldProduct.setProductID(productID);
        oldProduct.setUnitID(UUID.randomUUID());
        oldProduct.setTemplateID(mMockedTemplateID);
        when(mProductDao.findByIDSync(productID)).thenReturn(oldProduct);

        Product productWithNewName = new Product();
        productWithNewName.setName("Some name");
        productWithNewName.setProductID(productID);
        productWithNewName.setUnitID(mockedUnitID);
        productWithNewName.setTemplateID(mMockedTemplateID);

        final Product expectedProduct = new Product();
        expectedProduct.setName("Some name");
        expectedProduct.setProductID(productID);
        expectedProduct.setUnitID(mockedUnitID);
        expectedProduct.setTemplateID(null);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mSubject.updateProduct(productWithNewName, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product resultProduct) {
                assertEquals(resultProduct, expectedProduct);
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        verify(mProductDao).update(expectedProduct);
    }

    @Test
    public void updateProductsStatus() throws InterruptedException {
        mSubject.updateProductsStatus(Product.ABSENT);

        verify(mProductDao).updateStatus(Product.ABSENT, MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void moveProduct_insideList_WithCustomOrder() throws ShoppingListException {

        ProductList productList = new ProductList("Some name", UUID.randomUUID());
        productList.setListID(MOCKED_PRODUCTS_LIST_ID);
        productList.setSorting(ProductList.SORT_PRODUCTS_BY_ORDER);
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(productList);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        product1.setOrder(4.1);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        product2.setOrder(2);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 3");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        product3.setOrder(8.7);
        products.add(product3);

        mSubject.moveProduct(product3, product1, product2);

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
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        product1.setOrder(4.1);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        product2.setOrder(2);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 3");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        product3.setOrder(8.7);
        products.add(product3);

        mSubject.moveProduct(product1, null, product2);

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
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        product1.setOrder(4.1);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        product2.setOrder(2);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 3");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        product3.setOrder(8.7);
        products.add(product3);

        mSubject.moveProduct(product2, product3, null);

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
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 1");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product4.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product4);

        mSubject.moveProduct(product3, product1, product2);

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
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 1");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product4.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product4);

        mSubject.moveProduct(product3, product2, null);

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
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 1");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product4.setListID(MOCKED_PRODUCTS_LIST_ID);
        products.add(product4);

        mSubject.moveProduct(product1, null, product3);

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
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        product1.setStatus(Product.ABSENT);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        product2.setStatus(Product.TO_BUY);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        product3.setStatus(Product.ABSENT);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product4.setListID(MOCKED_PRODUCTS_LIST_ID);
        product4.setStatus(Product.BOUGHT);
        products.add(product4);

        mSubject.moveProduct(product3, product1, product2);

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
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        product1.setStatus(Product.ABSENT);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        product2.setStatus(Product.TO_BUY);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        product3.setStatus(Product.ABSENT);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product4.setListID(MOCKED_PRODUCTS_LIST_ID);
        product4.setStatus(Product.BOUGHT);
        products.add(product4);

        mSubject.moveProduct(product3, product2, null);

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
        Product product1 = new Product();
        product1.setName("Product 2");
        product1.setListID(MOCKED_PRODUCTS_LIST_ID);
        product1.setStatus(Product.ABSENT);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setListID(MOCKED_PRODUCTS_LIST_ID);
        product2.setStatus(Product.TO_BUY);
        products.add(product2);

        Product product3 = new Product();
        product3.setName("Product 4");
        product3.setListID(MOCKED_PRODUCTS_LIST_ID);
        product3.setStatus(Product.ABSENT);
        products.add(product3);

        Product product4 = new Product();
        product4.setName("Product 3");
        product4.setListID(MOCKED_PRODUCTS_LIST_ID);
        product4.setStatus(Product.BOUGHT);
        products.add(product4);

        mSubject.moveProduct(product1, null, product3);

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
    public void removeProductsWithTemplate() {

        UUID templateID = UUID.randomUUID();

        mSubject.removeProductsWithTemplate(templateID);

        verify(mProductDao).delete(templateID, MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void removeProductsByStatus() {

        mSubject.removeProductsByStatus(Product.BOUGHT);

        verify(mProductDao).delete(Product.BOUGHT, MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByName() throws ShoppingListException {
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(mMockedProductList);

        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_NAME, false);
        verify(mProductDao).findByListIDSortByName(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByStatus() throws ShoppingListException {
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(mMockedProductList);

        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_STATUS, false);
        verify(mProductDao).findByListIDSortByStatusAndName(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByOrder() throws ShoppingListException {
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(mMockedProductList);

        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_ORDER, false);
        verify(mProductDao).findByListIDSortByProductOrder(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByName_groupeView() throws ShoppingListException {
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(mMockedProductList);

        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_NAME, true);
        verify(mProductDao).findByListIDSortByNameWithCategory(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByStatus_groupeView() throws ShoppingListException {
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(mMockedProductList);

        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_STATUS, true);
        verify(mProductDao).findByListIDSortByStatusAndNameWithCategory(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_sortByOrder_groupeView() throws ShoppingListException {
        when(mProductListDao.findByIDSync(MOCKED_PRODUCTS_LIST_ID)).thenReturn(mMockedProductList);

        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_ORDER, true);
        verify(mProductDao).findByListIDSortByProductOrderWithCategory(MOCKED_PRODUCTS_LIST_ID);
    }

    @Test
    public void findProductsByListID_saveSortAndView() throws ShoppingListException {

        ProductList list = new ProductList("Some name", UUID.randomUUID());
        list.setListID(mSubject.getListID());
        list.setSorting(ProductList.SORT_PRODUCTS_BY_NAME);
        list.setIsGroupedView(false);
        when(mProductListDao.findByIDSync(list.getListID())).thenReturn(list);

        mSubject.getProducts(ProductList.SORT_PRODUCTS_BY_STATUS, true);

        ProductList expectedList = new ProductList(list.getName(), list.getCreatedBy());
        expectedList.setCreatedAt(list.getCreatedAt());
        expectedList.setListID(list.getListID());
        expectedList.setSorting(ProductList.SORT_PRODUCTS_BY_STATUS);
        expectedList.setIsGroupedView(true);

        verify(mProductListDao).update(expectedList);
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
